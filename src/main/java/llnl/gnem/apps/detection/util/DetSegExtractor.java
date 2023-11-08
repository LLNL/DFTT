/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.apps.detection.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import llnl.gnem.apps.detection.ConfigurationInfo;

import llnl.gnem.apps.detection.source.SourceData;

import llnl.gnem.dftt.core.database.DbCommandLineParser;
import llnl.gnem.dftt.core.io.SAC.SACFile;
import llnl.gnem.dftt.core.io.SAC.SACHeader;
import llnl.gnem.dftt.core.signalprocessing.Sequence;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.FileSystemException;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.apps.detection.dataAccess.ApplicationRoleManager;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectionSummary;
import llnl.gnem.dftt.core.dataAccess.DAOFactory;
import llnl.gnem.dftt.core.dataAccess.SeismogramSourceInfo;
import llnl.gnem.dftt.core.util.FileUtil.DriveMapper;
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
        DAOFactory.getInstance(parser.getCredentials().username, parser.getCredentials().password, parser.getCredentials().instance, new ApplicationRoleManager());
    }

    public DetSegExtractor(String[] args) throws Exception {

        getCommandLineInfo(args);
        initializeConnection();
        FrameworkRun fr = DetectionDAOFactory.getInstance().getFrameworkRunDAO().getFrameworkRun(runid);
        if (fr == null) {
            throw new IllegalStateException("Could not retrieve FrameworkRun info for runid: " + runid);
        }
        int configid = fr.getConfigid();
        SeismogramSourceInfo sourceInfo = DetectionDAOFactory.getInstance().getConfigurationDAO().getConfigurationSeismogramSourceInfo(configid);
        if (sourceInfo == null) {
            throw new IllegalStateException("Could not retrieve SeismogramSourceInfo for configuration " + configid + ", runid " + runid);
        }
        DAOFactory.getInstance().setSeismogramSourceInfo(sourceInfo);

        String configName = DetectionDAOFactory.getInstance().getConfigurationDAO().getConfigNameForRun(runid);

        boolean scaleByCalib = false;
        source = new SourceData(configName, scaleByCalib);
        ConfigurationInfo.getInstance().setCurrentConfigurationData(configid);
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

        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            printUsage(options);
            System.exit(2);
        }
    }

    public void run() throws Exception {

        if (duration <= 0) {
            duration = preTrigSeconds + DetectionDAOFactory.getInstance().getTriggerDAO().getMeanDuration(runid, detectorid);
        }
        Collection<DetectionSummary> detections = DetectionDAOFactory.getInstance().getDetectionDAO().getDetectionSummaries(runid, detectorid, detStatThreshold);
        for (DetectionSummary summary : detections) {
            createBaseDirectory();
            Collection<WaveformSegment> results = source.retrieveDataBlock(new TimeT(summary.getTriggerTime() - preTrigSeconds), duration, false);
            boolean segmentsAreSameLength = segmentLengthsAgree(results);
            if (segmentsAreSameLength) {
                for (WaveformSegment data : results) {
                    writeSacFile(summary.getDetectionid(), data);
                }
            } else {
                System.out.println(String.format("Skipping detectionid: %d because not all segements are same length.", summary.getDetectionid()));
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
        header.setTime(new llnl.gnem.dftt.core.util.TimeT(waveformSegment.getTimeAsDouble()));
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

    public static void main(String[] args) throws FileNotFoundException {

        String driveMapFile = System.getenv("DRIVE_MAP_FILE");
        if (driveMapFile != null) {
            DriveMapper.getInstance().loadDriveMapData(driveMapFile);
        }

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
