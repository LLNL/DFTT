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
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import llnl.gnem.apps.detection.gaps.GapManager;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.database.DbCommandLineParser;
import llnl.gnem.core.database.dao.OracleWaveformDAO;
import llnl.gnem.core.io.SAC.SACFile;
import llnl.gnem.core.io.SAC.SACHeader;
import llnl.gnem.core.signalprocessing.Sequence;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.BuildInfo;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.waveform.merge.MergeException;
import llnl.gnem.core.waveform.merge.NamedIntWaveform;
import llnl.gnem.core.waveform.merge.IntWaveform;
import llnl.gnem.core.waveform.merge.WaveformMerger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author dodge1
 */
public class SacFileDumper {

    private String wfdiscTable;

    private DbCommandLineParser parser;
    private Double startTime;
    private Double endTime;
    private String station;
    private String channel;
    private static String logLevel = "INFO";

    private void printUsage(Options options) {
        String usage = "SacFileDumper login/password@instance  [options]";

        String footer = "\n";

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usage, "", options, footer, false);
    }

    private void initializeConnection() throws Exception {

        ConnectionManager.getInstance(parser.getCredentials().username, parser.getCredentials().password, parser.getCredentials().instance);
    }

    public SacFileDumper(String[] args) throws Exception {

        getCommandLineInfo(args);
        initializeConnection();
    }

    public static void main(String[] args) {

        SacFileDumper runner;
        try {
            BuildInfo buildInfo = new BuildInfo(SacFileDumper.class);

            ApplicationLogger.getInstance().setFileHandler("SacFileDumper", false);
            ApplicationLogger.getInstance().useConsoleHandler();
            ApplicationLogger.getInstance().setLevel(logLevel);
            if (buildInfo.isFromJar()) {
                ApplicationLogger.getInstance().log(Level.INFO, buildInfo.getBuildInfoString());
            }
            runner = new SacFileDumper(args);
            runner.run();
        } catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "General Failure!", e);
        }

    }

    private void getCommandLineInfo(String[] args) throws IOException {

        Options options = new Options();

        Option help = new Option("h", "help", false, "Show this message");
        Option startSecOption = new Option("s", "StartSeconds", true, "Epoch time of segment start");
        Option endSecOption = new Option("e", "EndSeconds", true, "Epoch time of segment end");
        Option staOption = new Option("S", "StaCode", true, "Station code to extract");
        Option chanOption = new Option("c", "Chan", true, "Channel code to extract");
        Option wfdiscTableNameOption = new Option("w", "WfdiscTableName", true, "The name of the continuous wfdisc table");
        Option logLevelOption = new Option("L", "LogLevel", true, "The logging level to use.");

        wfdiscTableNameOption.setType(String.class);
        chanOption.setType(String.class);
        staOption.setType(String.class);
        endSecOption.setType(Double.class);
        startSecOption.setType(Double.class);

        wfdiscTableNameOption.setRequired(true);
        chanOption.setRequired(true);
        staOption.setRequired(true);
        endSecOption.setRequired(true);
        startSecOption.setRequired(true);

        options.addOption(wfdiscTableNameOption);
        options.addOption(help);
        options.addOption(startSecOption);
        options.addOption(endSecOption);
        options.addOption(staOption);
        options.addOption(chanOption);
        options.addOption(logLevelOption);

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

            startTime = new Double(cmd.getOptionValue(startSecOption.getOpt()));
            endTime = new Double(cmd.getOptionValue(endSecOption.getOpt()));
            station = cmd.getOptionValue(staOption.getOpt());
            channel = cmd.getOptionValue(chanOption.getOpt());
            wfdiscTable = cmd.getOptionValue(wfdiscTableNameOption.getOpt());
            getLogLevel(cmd, logLevelOption);
            ApplicationLogger.getInstance().setLevel(logLevel);

        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            printUsage(options);
            System.exit(2);
        }
    }

    private void getLogLevel(CommandLine cmd, Option logLevelOption) {
        if (cmd.hasOption(logLevelOption.getOpt())) {
            String level = cmd.getOptionValue(logLevelOption.getOpt());
            logLevel = level;
            ApplicationLogger.getInstance().setLevel(logLevel);
        } else {
            logLevel = "INFO";
        }
    }

    public void run() throws Exception {
        ApplicationLogger.getInstance().log(Level.FINE, "Retrieving named waveform collection...");
        Collection<NamedIntWaveform> waveforms = getNamedWaveformCollection(station, channel, endTime, startTime);
        for (NamedIntWaveform waveform : waveforms) {
            System.out.println("Processing: " + waveform.getSta() + "-" + waveform.getChan());
            writeSacFile(waveform);
        }
    }

    private void writeSacFile(NamedIntWaveform data) throws FileSystemException {
        ApplicationLogger.getInstance().log(Level.FINE, "Writing SAC file...");
        SACHeader header = new SACHeader();
        header.setTime(new llnl.gnem.core.util.TimeT(data.getStart()));
        header.b = 0;
        header.delta = (float) (1.0 / data.getRate());
        header.kstnm = data.getSta();
        header.kcmpnm = data.getChan();

        File file = new File(String.format("%s_%s.sac", data.getSta(), data.getChan()));
        Sequence sequence = new Sequence(data.getDataAsFloatArray());
        SACFile sacfile = new SACFile(file, header, sequence);
        sacfile.write();
    }

    private Collection<NamedIntWaveform> getNamedWaveformCollection(String sta, String chan, double end, double start) throws SQLException {

        PreparedStatement stmt = null;
        Connection aConn = null;

        try {
            aConn = ConnectionManager.getInstance().checkOut();

            String sql = String.format("select distinct time, endtime,nsamp,samprate,dir,dfile, foff, datatype, sta, chan, wfid from %s  "
                    + "where sta = ? and chan = ? and time <= ? and ? <= endtime order by time", wfdiscTable);
            ApplicationLogger.getInstance().log(Level.FINE, sql);
            stmt = aConn.prepareStatement(sql);

            Collection<NamedIntWaveform> namedWaveforms = new ArrayList<>();

            stmt.setString(1, sta);
            stmt.setString(2, chan);
            stmt.setDouble(3, end);
            stmt.setDouble(4, start);
            try {
                NamedIntWaveform waveform = getRows(start, end, stmt);
                if (waveform != null) {
                    ApplicationLogger.getInstance().log(Level.FINE, "Got non-null NamedWaveform.");
                    waveform = GapManager.getInstance().maybeFillGaps(waveform);
                    namedWaveforms.add(waveform);
                }
            } catch (MergeException | SQLException e) {
                ApplicationLogger.getInstance().log(Level.SEVERE, e.getMessage(), e);
            }

            return namedWaveforms;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (aConn != null) {
                ConnectionManager.getInstance().checkIn(aConn);
            }
        }
    }

    private NamedIntWaveform getRows(double requestedStart, double requestedEnd, PreparedStatement stmt) throws MergeException, SQLException {

        NamedIntWaveform result = null;
        ApplicationLogger.getInstance().log(Level.FINE, "Executing query...");
        try (ResultSet rs = stmt.executeQuery()) {
            int wfid = 1;
            while (rs.next()) {
                ApplicationLogger.getInstance().log(Level.FINE, "Processing ResultSet row...");
                NamedIntWaveform waveform = null;
                try {
                    waveform = createNamedWaveform(requestedStart, requestedEnd, rs, wfid);
                    ApplicationLogger.getInstance().log(Level.FINE, "Created NamedWaveform...");
                } catch (SQLException | IOException e) {
                    ApplicationLogger.getInstance().log(Level.SEVERE, e.getMessage(), e);
                }
                if (waveform != null && !waveform.isEmpty()) {
                    ++wfid;
                    if (result == null) {
                        result = waveform;
                    } else { // More than 1 segment on the source side satisfies our request. Join them
                        boolean ignoreMergeError = true;
                        boolean ignoreMismatchedSamples = true;
                        ApplicationLogger.getInstance().log(Level.FINE, "Merging waveforms...");
                        IntWaveform wf = WaveformMerger.mergeWaveforms(waveform, result, ignoreMergeError, ignoreMismatchedSamples);
                        result = new NamedIntWaveform(wf.getWfid(), waveform.getSta(), waveform.getChan(), wf.getStart(), wf.getRate(), wf.getData());

                    }
                }
            }
            if (result != null) {
                ApplicationLogger.getInstance().log(Level.FINE, "Trimming to user-specified window...");
                if (result.getStart() - requestedStart > 1 / result.getRate()) {
                    IntWaveform wf = result.getNewStartCopy(requestedStart);
                    result = new NamedIntWaveform(wf, result.getSta(), result.getChan());
                }
                if (Math.abs(result.getEnd() - requestedEnd) > 1 / result.getRate()) {
                    IntWaveform wf = result.getNewEndCopy(requestedEnd);
                    result = new NamedIntWaveform(wf, result.getSta(), result.getChan());
                }
            }
            return result;
        }
    }

    private NamedIntWaveform createNamedWaveform(double requestedStart, double requestedEnd, ResultSet rs, int wfid) throws SQLException, IOException {
        double time = rs.getDouble(1);
        double endtime = rs.getDouble(2);
        int nsamp = rs.getInt(3);
        double samprate = rs.getDouble(4);
        String dir = rs.getString(5);
        String dfile = rs.getString(6);
        int foff = rs.getInt(7);
        String datatype = rs.getString(8);
        String sta = rs.getString(9);
        String chan = rs.getString(10);
        int sourceWfid = rs.getInt(11);
        ApplicationLogger.getInstance().log(Level.FINE, String.format("Retrieving %d samples of waveform data for wfid %d...", nsamp, sourceWfid));
        int[] data = new int[nsamp];
        try {
            data = OracleWaveformDAO.getInstance().getSeismogramDataAsIntArray(dir, dfile, foff, nsamp, datatype);
        } catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.FINE, String.format("Failed reading file %s/%s. Missing data will be replaced with zeros.", dir, dfile));

        }

        int idx = 0;
        double start = time;
        if (requestedStart > time) { // The segment starts earlier than we need...
            start = requestedStart;
            idx = (int) Math.round((start - time) * samprate);
        }

        int endIdx = data.length - 1;
        if (requestedEnd < endtime) {  // The segment extends past what we need.
            endIdx = (int) Math.round((requestedEnd - time) * samprate);
        }

        int length = endIdx - idx + 1;
        if (length < 2) {
            return null;
        }
        int[] resultArray = new int[length];
        if (data.length >= resultArray.length && idx >= 0 && idx + length <= data.length) {
            System.arraycopy(data, idx, resultArray, 0, length);
        }
        ApplicationLogger.getInstance().log(Level.FINEST, String.format("\t\t\t\tRetrieved %d-point segment starting "
                + "%8.3f seconds from request window start for %s - %s",
                length, start - requestedStart, sta, chan));
        IntWaveform waveform = new IntWaveform(wfid, start, samprate, resultArray);
        return new NamedIntWaveform(waveform, sta, chan);
    }

}
