package llnl.gnem.apps.detection.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.logging.Level;
import llnl.gnem.apps.detection.database.ChannelSubstitutionDAO;

import llnl.gnem.apps.detection.database.ConfigurationDAO;
import llnl.gnem.apps.detection.database.DbOps;
import llnl.gnem.apps.detection.database.FrameworkRunDAO;
import llnl.gnem.apps.detection.database.TableNames;
import llnl.gnem.apps.detection.source.SourceData;
import llnl.gnem.apps.detection.source.WfdiscTableSourceData;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.database.DbCommandLineParser;
import llnl.gnem.core.io.SAC.SACFile;
import llnl.gnem.core.io.SAC.SACHeader;
import llnl.gnem.core.signalprocessing.Sequence;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class DetSegExtractor {

    private final SourceData source;
    private double preTrigSeconds;
    private double duration;
    private double detStatThreshold;
    private int runid;
    private int detectorid;
    private File targetDir;
    private DbCommandLineParser parser;

    private void printUsage(Options options) {
        String usage = "DetSegExtractor login/password@instance  [options]";

        String footer = "\n";

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usage, "", options, footer, false);
    }

    private void initializeConnection() throws Exception {

        ConnectionManager.getInstance(parser.getCredentials().username, parser.getCredentials().password, parser.getCredentials().instance);
    }

    public DetSegExtractor(String[] args) throws Exception {

        getCommandLineInfo(args);
        initializeConnection();

        FrameworkRun fr = FrameworkRunDAO.getInstance().getFrameworkRun(runid);
        String sourceWfdiscTable = fr.getWfdisc();
        double fixedRawRate = fr.getFixedRawSampleRate();
        String configName = ConfigurationDAO.getInstance().getConfigNameForRun(runid);
        int configid = ConfigurationDAO.getInstance().getConfigid(configName);
        source = new WfdiscTableSourceData(sourceWfdiscTable, configName, false);
        source.setFixedRawRate(fixedRawRate);
        source.setChannelSubstitutions(ChannelSubstitutionDAO.getInstance().getChannelSubstitutions(configid));

    }

    private void getCommandLineInfo(String[] args) throws IOException {

        Options options = new Options();

        Option help = new Option("h", "help", false, "Show this message");
        Option preTrigSecOption = new Option("p", "PreTrigSeconds", true, "Seconds before trigger time to extract. (Default = 10)");
        Option durationOption = new Option("d", "Duration", true, "Duration (in seconds) of segment to extract. (Default = max duration of selected detections.)");
        Option detStatThresholdOption = new Option("T", "DetStatThresh", true, "Only extract segments with a statistic >= this value.");
        Option runidOption = new Option("r", "Runid", true, "The RUNID for which results will be extracted.");
        Option detectoridOption = new Option("D", "Detectorid", true, "The DETECTORID for which results will be extracted.");
        Option siteTableNameOption = new Option("site", "SiteTableName", true, "The name of the SITE table used for station queries. Default is SITE in the current schema.");
        siteTableNameOption.setType(String.class);
        siteTableNameOption.setRequired(false);
        options.addOption(siteTableNameOption);

        runidOption.setRequired(true);
        detectoridOption.setRequired(true);

        options.addOption(help);
        options.addOption(preTrigSecOption);

        options.addOption(durationOption);
        options.addOption(detStatThresholdOption);
        options.addOption(runidOption);
        options.addOption(detectoridOption);

        if (args.length == 0 || args[0].trim().isEmpty()) {
            printUsage(options);
            System.exit(2);
        }

        parser = new DbCommandLineParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption(help.getOpt())) {
                printUsage(options);
                System.exit(0);
            }

            preTrigSeconds = cmd.hasOption(preTrigSecOption.getOpt()) ? new Double(cmd.getOptionValue(preTrigSecOption.getOpt())) : 10.0;

            duration = cmd.hasOption(durationOption.getOpt()) ? new Double(cmd.getOptionValue(durationOption.getOpt())) : -1.0;
            detStatThreshold = cmd.hasOption(detStatThresholdOption.getOpt()) ? new Double(cmd.getOptionValue(detStatThresholdOption.getOpt())) : 0.0;

            runid = new Integer(cmd.getOptionValue(runidOption.getOpt()));
            detectorid = new Integer(cmd.getOptionValue(detectoridOption.getOpt()));

            String site = cmd.hasOption(siteTableNameOption.getOpt()) ? cmd.getOptionValue(siteTableNameOption.getOpt()) : "site";
            TableNames.getInstance().setSiteTableName(site);

        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            printUsage(options);
            System.exit(2);
        }
    }

    public void run() throws Exception {
        source.setStaChanArrays();
    //    createBaseDirectory();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        if (duration <= 0) {
            duration = preTrigSeconds + DbOps.getInstance().getMeanDuration(runid, detectorid);
        }
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select detectionid, time, a.detectorid from detection a, trigger_record b where a.runid = ? "
                    + "and  a.triggerid = b.triggerid and detection_statistic >= ?");
            stmt.setInt(1, runid);
//            stmt.setInt(2, detectorid);
            stmt.setDouble(2, detStatThreshold);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int detectionid = rs.getInt(1);
                System.out.println("Processing detectionid: " + detectionid);
                double time = rs.getDouble(2);
                detectorid = rs.getInt(3);
                createBaseDirectory();
                Collection<WaveformSegment> results = source.retrieveDataBlock(new TimeT(time - preTrigSeconds), duration, false);
                boolean segmentsAreSameLength = segmentLengthsAgree(results);
                if (segmentsAreSameLength) {
                    for (WaveformSegment data : results) {
                        writeSacFile(detectionid, data);
                    }
                } else {
                    System.out.println(String.format("Skipping detectionid: %d because not all segements are same length.", detectionid));
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    private void createBaseDirectory() throws FileSystemException {
        File cwd = new File(".");
        File subdir1 = new File(cwd, String.format("%d", detectorid));
        targetDir = new File(subdir1, String.format("%d", runid));
        if (!targetDir.exists()) {
            boolean created = targetDir.mkdirs();
            if (!created) {
                throw new FileSystemException(String.format("Failed to create: (%s)!", targetDir.getAbsolutePath()));
            }
        }

    }

    private File makeDetectionDirectory(int detectionid) throws FileSystemException {
        File finalDir = new File(targetDir, String.format("%d", detectionid));
        if (!finalDir.exists()) {
            boolean created = finalDir.mkdirs();
            if (!created) {
                throw new FileSystemException(String.format("Failed to create: (%s)!", finalDir.getAbsolutePath()));
            }
        }
        return finalDir;
    }

    private void writeSacFile(int detectionid, WaveformSegment data) throws FileSystemException {
        WaveformSegment waveformSegment = data;
        SACHeader header = new SACHeader();
        header.setTime(new llnl.gnem.core.util.TimeT(waveformSegment.getTimeAsDouble()));
        header.b = 0;
        header.delta = (float) (1.0 / data.getSamprate());
        header.kstnm = waveformSegment.getSta();
        header.kcmpnm = waveformSegment.getChan();
        File finalDir = makeDetectionDirectory(detectionid);
        File file = new File(finalDir, String.format("%s_%s.sac", waveformSegment.getSta(), waveformSegment.getChan()));
        Sequence sequence = new Sequence(waveformSegment.getData());
        SACFile sacfile = new SACFile(file, header, sequence);
        sacfile.write();
    }

    public static void main(String[] args) {

        DetSegExtractor runner;
        try {

            runner = new DetSegExtractor(args);

            runner.run();
        } catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "General Failure!", e);
        }

    }

    private boolean segmentLengthsAgree(Collection<WaveformSegment> results) {
        int length = -1;
        for (WaveformSegment seg : results) {
            int aLength = seg.getNsamp();
            if (length < 0) {
                length = aLength;
            } else if (length != aLength) {
                return false;
            }
        }
        return true;
    }
}
