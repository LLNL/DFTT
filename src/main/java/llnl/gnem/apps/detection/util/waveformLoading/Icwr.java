/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
package llnl.gnem.apps.detection.util.waveformLoading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.WaveformCriteria;
import edu.iris.dmc.service.NoDataFoundException;
import edu.iris.dmc.service.ServiceNotSupportedException;
import edu.iris.dmc.service.WaveformService;
import edu.iris.dmc.timeseries.model.Segment;
import edu.iris.dmc.timeseries.model.Timeseries;
import llnl.gnem.dftt.core.database.ConnectionManager;
import llnl.gnem.dftt.core.database.DbCommandLineParser;
import llnl.gnem.dftt.core.database.login.DbCredentials;
import llnl.gnem.dftt.core.fdsn.DataCenter;
import llnl.gnem.dftt.core.fdsn.FedCatalog;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.dftt.core.util.FileUtil.DriveMapper;
import llnl.gnem.dftt.core.waveform.merge.NamedIntWaveform;

/**
 *
 * @author dodge1
 */
public class Icwr {

    private static final String sepChar = File.separator;
    private DbCommandLineParser parser;
    private DbCredentials credentials;
    private String netCode;
    private String chanCode;
    private String stationCode;
    private String baseDir;
    private final String wfdisc = "continuous_wfdisc";
    private int minDate = -1;
    private int maxDate = 3000000;
    private boolean getLatest;
    private String sourceToRetrieve;

    private void printUsage(Options options) {
        String usage = "icwr login/password@instance -E <arg> | -e <arg> [options]";

        StringBuilder footer = new StringBuilder("");

        footer.append("\n\nAvailable logger levels are: ").append(ApplicationLogger.getAllLevelsString());

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usage, "", options, footer.toString(), false);
    }

    private void initializeConnection() throws Exception {

        ConnectionManager.getInstance(credentials.username, credentials.password, credentials.instance);
    }

    public Icwr(String[] args) throws IOException, Exception {
        getCommandLineInfo(args);
    }

    private void getCommandLineInfo(String[] args) throws FileNotFoundException, IOException {
        Options options = new Options();
        Option help = new Option("h", "help", false, "Show this message");
        Option netCodeOption = new Option("n", "NetCode", true, "The network code for which data are to be retrieved.");
        Option stationCodeOption = new Option("s", "StationCode", true, "The station code for which data are to be retrieved.");
        Option channelCodeOption = new Option("c", "ChannelCode", true, "The channel code for which data are to be retrieved.");
        Option baseDirOption = new Option("b", "BaseDirectory", true, "The directory below which data files will be written.");
        Option logLevelOption = new Option("L", "LogLevel", true, "The logging level to use.");
        Option minJdateOption = new Option("m", "MinJdate", true, "The start date (yyyyddd) for which data should be retrieved.");
        Option maxJdateOption = new Option("M", "MaxJdate", true, "The end date (yyyyddd) for which data should be retrieved.");
        Option latestOption = new Option("l", "Latest", false, "If specified, retrieve data from latest in DB to latest at provider.");
        Option sourceNameOption = new Option("a", "Agency", true, "The name of the source from which data will be retrieved. If not specified, IRISDMC is used.");
        Option listDCOption = new Option("d", "DataCenterList", false, "List the available waveform data centers.");

        netCodeOption.setRequired(true);
        stationCodeOption.setRequired(true);
        channelCodeOption.setRequired(true);
        baseDirOption.setRequired(true);
        minJdateOption.setRequired(false);
        maxJdateOption.setRequired(false);
        latestOption.setRequired(false);
        listDCOption.setRequired(false);

        netCodeOption.setType(String.class);
        stationCodeOption.setType(String.class);
        channelCodeOption.setType(String.class);
        baseDirOption.setType(String.class);
        minJdateOption.setType(Number.class);
        maxJdateOption.setType(Number.class);
        latestOption.setType(Number.class);
        listDCOption.setType(Boolean.class);

        options.addOption(help);
        options.addOption(netCodeOption);

        options.addOption(stationCodeOption);
        options.addOption(channelCodeOption);
        options.addOption(baseDirOption);
        options.addOption(logLevelOption);
        options.addOption(minJdateOption);
        options.addOption(maxJdateOption);
        options.addOption(latestOption);
        options.addOption(listDCOption);

        sourceNameOption.setType(String.class);
        sourceNameOption.setRequired(false);
        options.addOption(sourceNameOption);

        if (args.length == 0 || args[0].trim().isEmpty()) {
            printUsage(options);
            System.exit(2);
        }

        parser = new DbCommandLineParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            credentials = parser.getCredentials();

            if (cmd.hasOption(help.getOpt())) {
                printUsage(options);
                listDataCenters();
                System.exit(0);
            }

            if (cmd.hasOption(listDCOption.getOpt())) {
                listDataCenters();
                System.exit(0);
            }

            initializeConnection();

            netCode = cmd.getOptionValue(netCodeOption.getOpt());
            chanCode = cmd.getOptionValue(channelCodeOption.getOpt());
            stationCode = cmd.getOptionValue(stationCodeOption.getOpt());
            baseDir = cmd.getOptionValue(baseDirOption.getOpt());

            sourceToRetrieve = cmd.hasOption(sourceNameOption.getOpt()) ? cmd.getOptionValue(sourceNameOption.getOpt()) : "IRISDMC";

            if (cmd.hasOption(logLevelOption.getOpt())) {
                String level = cmd.getOptionValue(logLevelOption.getOpt());
                ApplicationLogger.getInstance().setLevel(Level.parse(level));
            } else {
                ApplicationLogger.getInstance().setLevel(Level.parse("INFO"));
            }
            if (cmd.hasOption(minJdateOption.getOpt())) {
                minDate = ((Number) cmd.getParsedOptionValue(minJdateOption.getOpt())).intValue();
            }
            if (cmd.hasOption(maxJdateOption.getOpt())) {
                maxDate = ((Number) cmd.getParsedOptionValue(maxJdateOption.getOpt())).intValue();
            } else {
                maxDate = new TimeT().getJdate();
            }

            getLatest = cmd.hasOption(latestOption.getOpt());
            if ((minDate < 0 || maxDate < 0) && !getLatest) {
                throw new IllegalStateException("Both minDate and maxDate must be specified unless using the 'Latest' option!");
            }

        } catch (Exception ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, ex.getMessage());
            printUsage(options);
            listDataCenters();
            System.exit(2);
        }

    }

    public static void main(String[] args) {
        DriveMapper.setupWindowsNFSDriveMap();

        try {

            ApplicationLogger.getInstance().setFileHandler("ICWR", false);
            ApplicationLogger.getInstance().useConsoleHandler();
            ApplicationLogger.getInstance().setGuiWarnings(false);

            Icwr icwrmain = new Icwr(args);

            icwrmain.run();
        } catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "General Failure", e);
        }

    }

    void listDataCenters() throws IOException {
        ApplicationLogger.getInstance().log(Level.INFO, "\n\nAvailable Data Centers:");
        FedCatalog cat = new FedCatalog();
        for (DataCenter dc : cat.getDataCenters()) {
            if (dc.hasWaveformService()) {
                ApplicationLogger.getInstance().log(Level.INFO, dc.getName());
            }
        }
    }

    void run() throws Exception {

        Connection conn = ConnectionManager.getInstance().getConnection();
        double blockSizeSeconds = 14400;

        FedCatalog cat = new FedCatalog();
        DataCenter dc = cat.getDataCenter(sourceToRetrieve);
        String url = dc.getWaveformServiceUrl();
        WaveformService waveFormService = new WaveformService(url, "2.0.17", "1.1", "ICWR");
        if (getLatest) {
            TimeT startTime = getLastSampleTime(netCode, stationCode, chanCode, conn);
            if (startTime == null) {
                throw new IllegalStateException("No data found for " + netCode + "  " + stationCode + "  " + chanCode);
            }
            TimeT endTime = new TimeT();
            String location = "**";
            StreamKey key = new StreamKey(netCode, stationCode, chanCode, location);
            getDataForTimeInterval(startTime, blockSizeSeconds, endTime, key, waveFormService, conn);
        } else {
            TimeT startTime = jdateToEpoch(minDate);
            TimeT endTime = jdateToEpoch(maxDate);
            String location = "**";
            StreamKey key = new StreamKey(netCode, stationCode, chanCode, location);
            getDataForTimeInterval(startTime, blockSizeSeconds, endTime, key, waveFormService, conn);
        }
    }

    private void getDataForTimeInterval(TimeT startTime, double blockSizeSeconds, TimeT endTime, StreamKey key, WaveformService waveFormService, Connection conn)
            throws IOException, ParseException, SQLException {
        double blockStart = startTime.getEpochTime();
        double blockEnd = blockStart + blockSizeSeconds;
        double termination = endTime.getEpochTime();
        String[] channels = { key.getChan() };
        String sql = String.format(
                "insert into %s " + "(wfid,net,sta,chan,locid,time,endtime,jdate,nsamp,samprate,calib,calper," + "datatype,dir,dfile,foff,lddate)" + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)",
                    wfdisc);
        try (PreparedStatement wfdiscInsStmt = conn.prepareStatement(sql)) {
            while (blockEnd <= termination) {
                Date startDate = convertToDate(blockStart);
                Date endDate = convertToDate(blockEnd);
                ApplicationLogger.getInstance().log(Level.INFO, String.format("Processing start date: %s...", startDate));
                WaveformCriteria criteria = new WaveformCriteria();
                criteria.add(key.getNet(), key.getSta(), key.getLocationCode(), channels[0], startDate, endDate);

                try {
                    List<Timeseries> timeseriesList = waveFormService.fetch(criteria);

                    // process the fetched data.
                    for (Timeseries timeseries : timeseriesList) {
                        String sta = timeseries.getStationCode();
                        String loc = timeseries.getLocation();
                        String cha = timeseries.getChannelCode();

                        if (loc.trim().equals("")) {
                            loc = "--";
                        }

                        for (Segment segment : timeseries.getSegments()) {
                            NamedIntWaveform waveform = new NamedIntWaveform(-1, sta, cha, segment);
                            try {
                                int wfid = Utility.getNextAvailableWfid(conn);
                                double calib = 1;
                                double calper = -1;
                                String datatype;
                                String dfile = sta + '_' + waveform.getChan() + '_' + wfid + ".w";
                                String dir = Utility.makeOutputDirectory(baseDir, sta, waveform.getStart());
                                String filename = dir + File.separator + dfile;
                                datatype = Utility.writeDfile(filename, waveform.getData(), waveform.getNpts());
                                int idx = 1;
                                wfdiscInsStmt.setInt(idx++, wfid);
                                wfdiscInsStmt.setString(idx++, key.getNet());
                                wfdiscInsStmt.setString(idx++, sta);
                                wfdiscInsStmt.setString(idx++, cha);
                                wfdiscInsStmt.setString(idx++, loc);
                                wfdiscInsStmt.setDouble(idx++, waveform.getStart());
                                wfdiscInsStmt.setDouble(idx++, waveform.getEnd());
                                wfdiscInsStmt.setInt(idx++, waveform.getJdate());
                                wfdiscInsStmt.setInt(idx++, waveform.getNpts());
                                wfdiscInsStmt.setDouble(idx++, waveform.getRate());
                                wfdiscInsStmt.setDouble(idx++, calib);
                                wfdiscInsStmt.setDouble(idx++, calper);
                                wfdiscInsStmt.setString(idx++, datatype);
                                wfdiscInsStmt.setString(idx++, dir);
                                wfdiscInsStmt.setString(idx++, dfile);
                                wfdiscInsStmt.setInt(idx++, 0);
                                wfdiscInsStmt.execute();
                                conn.commit();
                                ApplicationLogger.getInstance().log(Level.INFO, String.format("Wrote %s", dfile));
                            } catch (IOException ex) {
                                ApplicationLogger.getInstance().log(Level.WARNING, "", ex);
                            } catch (SQLException ex) {
                                ApplicationLogger.getInstance().log(Level.WARNING, String.format("SQL exception writing WFDISC row! %s - %s", startDate, endDate), ex);
                            } catch (Exception ex) {
                                ApplicationLogger.getInstance().log(Level.WARNING, String.format("Unknown error! %s - %s", startDate, endDate), ex);
                            }
                        }
                    }

                } catch (NoDataFoundException ex) {
                    ApplicationLogger.getInstance().log(Level.INFO, String.format("No Data found for %s to %s: ", startDate, endDate));
                } catch (CriteriaException ex) {
                    ApplicationLogger.getInstance().log(Level.SEVERE, "Criteria not accepted by server!", ex);
                } catch (ServiceNotSupportedException ex) {
                    ApplicationLogger.getInstance().log(Level.SEVERE, "Service not supported!", ex);
                } catch (IOException ex) {
                    ApplicationLogger.getInstance().log(Level.SEVERE, "Unknown IO Error!", ex);
                }

                blockStart += blockSizeSeconds;
                blockEnd += blockSizeSeconds;
            }
        }
    }

    private Date convertToDate(double anEpochTime) throws ParseException {

        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dfm.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        TimeT tmp = new TimeT(anEpochTime);

        return dfm.parse(tmp.toString("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }


    private TimeT jdateToEpoch(int minDate) {
        int year = minDate / 1000;
        int jday = minDate % 1000;
        return new TimeT(year, jday, 0, 0, 0, 0);
    }



    private TimeT getLastSampleTime(String netCode, String stationCode, String chanCode, Connection conn) throws SQLException {
        String sql = "select max(endtime) from continuous_wfdisc where net = ? and sta = ? and chan = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, netCode);
            stmt.setString(2, stationCode);
            stmt.setString(3, chanCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (rs.wasNull()) {
                        return null;
                    } else {
                        return new TimeT(rs.getDouble(1));
                    }
                }
            }
        }
        return null;
    }

}
