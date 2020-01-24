/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util.configuration;

import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.database.ConfigurationDAO;

import llnl.gnem.apps.detection.database.TableNames;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.database.DbCommandLineParser;

import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Geometry.EModel;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author dodge
 */
public class ConfigCreator {
    
    private DbCommandLineParser parser;
    
    private String refSta;
    private String configName;
    private File configDirectory;
    private Integer minDate = -1;
    private Integer maxDate = 2286324;
    private Collection<ChanInfo> chans;
    private Collection<StreamKey> staChansToUse;
    private double minTemplateLength = 50;
    private double maxTemplateLength = 150;
    private double minFrequency = 1.0;
    private double maxFrequency = 8.0;
    private double ssThresh = 0.6;
    private double staLtaThresh = 20;
    private double snrThreshold = 5.0;
    private double minEventDuration = 15.0;
    private int numThreads = 4;
    private double blockSizeSeconds = 400;
    private int decimationRate = 4;
    private boolean spawnCorrelationDetectors = true;
    DetectorType bootDetectorType = DetectorType.STALTA;
    Double beamAzimuth;
    Double beamVelocity;
    private String wfdiscName = "wfdisc_stage";
    Collection<String> stations = new ArrayList<>();
    private int evid = -1;
    
    private void run() throws Exception {
        
        File pwd = new File(".");
        configDirectory = new File(pwd, configName);
        
        if (ConfigurationDAO.getInstance().configExists(configName)) {
            throw new IllegalStateException(String.format("Configuration: %s already exists in database!", configName));
        }
        
        if (stations.isEmpty() && !refStaInSite()) {
            throw new IllegalStateException(String.format("REFSTA: %s not found in SITE!", refSta));
        }
        
        if (!stations.isEmpty()) {
            findStationDataRange();
        } else {
            findRefstaDataRange();
        }
        chooseChannels();
        verifyDataAvailability();
        boolean isArray = isArray(refSta);
        int stationCount = getDistinctStationCount();
        if (bootDetectorType == DetectorType.ARRAYPOWER && (!isArray || stationCount < 3)) {
            throw new IllegalStateException("Boot detectortype was chosen as ARRAYPOWER but chosen stations are not an array!");
        }
        
        if (beamAzimuth == null || beamVelocity == null) {
            if (evid > 0 && isArray) {
                computeAzAndSlowness();
                if (beamAzimuth == null || beamVelocity == null) {
                    throw new IllegalStateException("Failed to compute beam azimuth and slowness!");
                }
            }
        }
        
        reportChannelStats();
        
        File bulletinFile = null;
        new ConfigFileWriter(refSta,
                configName,
                configDirectory,
                minDate,
                maxDate,
                staChansToUse,
                minTemplateLength,
                maxTemplateLength,
                minFrequency,
                maxFrequency,
                ssThresh,
                staLtaThresh,
                numThreads,
                bootDetectorType,
                beamAzimuth,
                beamVelocity,
                bulletinFile,
                snrThreshold,
                minEventDuration,
                blockSizeSeconds,
                decimationRate,
                spawnCorrelationDetectors).create();
    }
    
    private void printUsage(Options options) {
        String usage = "ConfigCreator login/password@instance  [options]";
        
        String footer = "\n";
        
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usage, "", options, footer, false);
    }
    
    private void initializeConnection() throws Exception {
        
        ConnectionManager.getInstance(parser.getCredentials().username, parser.getCredentials().password, parser.getCredentials().instance);
    }
    
    public ConfigCreator(String[] args) throws Exception {
        getCommandLineInfo(args);
        initializeConnection();
        
    }
    
    public static void main(String[] args) {
        
        ConfigCreator runner;
        try {
            ApplicationLogger.getInstance().setFileHandler("ConfigCreator", false);
            ApplicationLogger.getInstance().useConsoleHandler();
            runner = new ConfigCreator(args);
            
            runner.run();
        } catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.SEVERE, e.getMessage(), e);
        }
        
    }
    
    private void getCommandLineInfo(String[] args) throws IOException {
        
        Options options = new Options();
        
        Option help = new Option("h", "help", false, "Show this message");
        
        Option configOption = new Option("c", "ConfigName", true, "Specifies the name of this configuration.");
        configOption.setType(String.class);
        configOption.setRequired(true);
        options.addOption(configOption);
        
        Option stationCodesOption = new Option("s", "StationList", true, "One or more station codes to use in creating this configuration. If more than 1 then separate with commas and no spaces e.g. -s sta1,sta2,sta3.");
        stationCodesOption.setType(String.class);
        stationCodesOption.setRequired(false);
        options.addOption(stationCodesOption);
        
        Option refstaOption = new Option("r", "RefSta", true, "Specifies the REFSTA to use in creating this configuration.");
        refstaOption.setType(String.class);
        refstaOption.setRequired(false);
        options.addOption(refstaOption);
        
        Option bootDetTypeOption = new Option("b", "BootDetectorType", true, "Type of boot detector to create. only ARRAYPOWER and STALTA are supported. (defaults to STALTA).");
        bootDetTypeOption.setType(String.class);
        bootDetTypeOption.setRequired(false);
        options.addOption(bootDetTypeOption);
        
        Option wfdiscNameOption = new Option("w", "WfdiscName", true, "The name of the continuous wfdisc to use. (Defaults to WFDISC_STAGE)");
        wfdiscNameOption.setType(String.class);
        wfdiscNameOption.setRequired(false);
        options.addOption(wfdiscNameOption);
        
        Option minJdateOption = new Option("m", "MinDate", true, "The minDate to use for this configuration (defaults to min(jdate) in WFDISC for REFSTA).");
        minJdateOption.setType(Number.class);
        minJdateOption.setRequired(false);
        options.addOption(minJdateOption);
        
        Option maxJdateOption = new Option("M", "MaxDate", true, "The maxDate to use for this configuration (defaults to max(jdate) in WFDISC for REFSTA).");
        maxJdateOption.setType(Number.class);
        maxJdateOption.setRequired(false);
        options.addOption(maxJdateOption);
        
        Option numThreadsOption = new Option("n", "NumThreads", true, "The number of threads used for detection processing (defaults to 4).");
        numThreadsOption.setType(Number.class);
        numThreadsOption.setRequired(false);
        options.addOption(numThreadsOption);
        
        Option evidOption = new Option("E", "evid", true, "The evid of the event to beam to.");
        evidOption.setType(Number.class);
        evidOption.setRequired(false);
        options.addOption(evidOption);
        
        Option maxTemplateLengthOption = new Option("e", "ExpectedLength", true, "Max Template length in seconds (defaults to 150).");
        maxTemplateLengthOption.setType(Number.class);
        maxTemplateLengthOption.setRequired(false);
        options.addOption(maxTemplateLengthOption);
        
        Option minTemplateLengthOption = new Option("i", "MinTemplateLength", true, "Min Template length in seconds (defaults to 50).");
        minTemplateLengthOption.setType(Number.class);
        minTemplateLengthOption.setRequired(false);
        options.addOption(minTemplateLengthOption);
        
        Option minFreqOption = new Option("f", "MinFreq", true, "The min frequency in passband (defaults to 1.0).");
        minFreqOption.setType(Number.class);
        minFreqOption.setRequired(false);
        options.addOption(minFreqOption);
        
        Option maxFreqOption = new Option("F", "MaxFreq", true, "The max frequency in passband (defaults to 8.0).");
        maxFreqOption.setType(Number.class);
        maxFreqOption.setRequired(false);
        options.addOption(maxFreqOption);
        
        Option subspaceThreshOption = new Option("t", "SSThresh", true, "The threshold to use for subspace detectors (defaults to 0.6).");
        subspaceThreshOption.setType(Number.class);
        subspaceThreshOption.setRequired(false);
        options.addOption(subspaceThreshOption);
        
        Option staltaThreshOption = new Option("T", "STALTAThresh", true, "The threshold to use for STA/LTA detectors (defaults to 20.0).");
        staltaThreshOption.setType(Number.class);
        staltaThreshOption.setRequired(false);
        options.addOption(staltaThreshOption);
        
        Option beamAzimuthOption = new Option("a", "BeamAzimuth", true, "The azimuth of the ARRAYPOWER boot detector. This must be set if BootDetectorType is ARRAYPOWER");
        beamAzimuthOption.setType(Number.class);
        beamAzimuthOption.setRequired(false);
        options.addOption(beamAzimuthOption);
        
        Option beamVelOption = new Option("v", "BeamVelocity", true, "The velocity of the ARRAYPOWER boot detector. This must be set if BootDetectorType is ARRAYPOWER");
        beamVelOption.setType(Number.class);
        beamVelOption.setRequired(false);
        options.addOption(beamVelOption);
        
        Option siteTableNameOption = new Option("site", "SiteTableName", true, "The name of the SITE table used for station queries. Default is SITE in the current schema.");
        siteTableNameOption.setType(String.class);
        siteTableNameOption.setRequired(false);
        options.addOption(siteTableNameOption);
        
        Option originTableNameOption = new Option("origin", "OriginTableName", true, "The name of the ORIGIN table used when EVID is supplied. Default is ORIGIN in the current schema.");
        originTableNameOption.setType(String.class);
        originTableNameOption.setRequired(false);
        options.addOption(originTableNameOption);
        
        Option chanDescTableNameOption = new Option("cd", "ChanDescTableName", true, "The name of the CHAN_DESC table used for channel code processing. Default is CHAN_DESC in the current schema.");
        chanDescTableNameOption.setType(String.class);
        chanDescTableNameOption.setRequired(false);
        options.addOption(chanDescTableNameOption);
        
        Option snrThreshOption = new Option("S", "SNRThresh", true, "Power detector triggers are screened to have SNR exceed this value.");
        snrThreshOption.setType(Number.class);
        snrThreshOption.setRequired(false);
        options.addOption(snrThreshOption);
        
        Option minDurationOption = new Option("D", "DurationThresh", true, "Power detector triggers are screened to have duration exceed this value.");
        minDurationOption.setType(Number.class);
        minDurationOption.setRequired(false);
        options.addOption(minDurationOption);
        
        Option blockSizeSecondsOption = new Option("B", "BlockSize", true, "Size in seconds of the data blocks scanned for triggers (default = 400).");
        blockSizeSecondsOption.setType(Number.class);
        blockSizeSecondsOption.setRequired(false);
        options.addOption(blockSizeSecondsOption);
        
        Option decimationRateOption = new Option("d", "Decimation", true, "Decimation factor to apply to filtered data. (default = 4).");
        decimationRateOption.setType(Number.class);
        decimationRateOption.setRequired(false);
        options.addOption(decimationRateOption);
        
        if (args.length == 0 || args[0].trim().isEmpty()) {
            printUsage(options);
            System.exit(2);
        }
        
        parser = new DbCommandLineParser();
        
        try {
            CommandLine cmd = parser.parse(options, args);
 //           credentials = parser.getCredentials();
            
            if (cmd.hasOption(help.getOpt())) {
                printUsage(options);
                System.exit(0);
            }
            String site = cmd.hasOption(siteTableNameOption.getOpt()) ? cmd.getOptionValue(siteTableNameOption.getOpt()) : "site";
            TableNames.getInstance().setSiteTableName(site);
            
            String origin = cmd.hasOption(originTableNameOption.getOpt()) ? cmd.getOptionValue(originTableNameOption.getOpt()) : "origin";
            TableNames.getInstance().setOriginTableName(origin);
            
            String chanDesc = cmd.hasOption(chanDescTableNameOption.getOpt()) ? cmd.getOptionValue(chanDescTableNameOption.getOpt()) : "chan_desc";
            TableNames.getInstance().setChanDescTableName(chanDesc);
            
            configName = cmd.getOptionValue(configOption.getOpt());
            
            String tmp = cmd.hasOption(stationCodesOption.getOpt()) ? cmd.getOptionValue(stationCodesOption.getOpt()) : null;
            
            if (tmp != null) {
                StringTokenizer st = new StringTokenizer(tmp, ",");
                while (st.hasMoreTokens()) {
                    stations.add(st.nextToken());
                }
            }
            
            tmp = cmd.hasOption(refstaOption.getOpt()) ? cmd.getOptionValue(refstaOption.getOpt()) : null;
            
            if (tmp != null) {
                if (!stations.isEmpty()) {
                    System.err.println("Cannot specify both -r and -s options!");
                    printUsage(options);
                    System.exit(1);
                }
                refSta = tmp;
            }
            
            tmp = cmd.hasOption(bootDetTypeOption.getOpt()) ? cmd.getOptionValue(bootDetTypeOption.getOpt()) : null;
            
            if (tmp != null) {
                bootDetectorType = DetectorType.valueOf(tmp);
                if (bootDetectorType != DetectorType.ARRAYPOWER && bootDetectorType != DetectorType.STALTA) {
                    System.err.println("Only ARRAYPOWER and STALTA are supported as boot detectors!");
                    printUsage(options);
                    System.exit(1);
                }
            } else {
                bootDetectorType = DetectorType.STALTA;
            }
            
            tmp = cmd.hasOption(wfdiscNameOption.getOpt()) ? cmd.getOptionValue(wfdiscNameOption.getOpt()) : null;
            if (tmp != null) {
                wfdiscName = tmp;
            }
            
            Number tmpInt = cmd.hasOption(minJdateOption.getOpt()) ? (Number) cmd.getParsedOptionValue(minJdateOption.getOpt()) : null;
            if (tmpInt != null) {
                minDate = tmpInt.intValue();
            }
            
            tmpInt = cmd.hasOption(maxJdateOption.getOpt()) ? (Number) cmd.getParsedOptionValue(maxJdateOption.getOpt()) : null;
            if (tmpInt != null) {
                maxDate = tmpInt.intValue();
            }
            
            tmpInt = cmd.hasOption(numThreadsOption.getOpt()) ? (Number) cmd.getParsedOptionValue(numThreadsOption.getOpt()) : null;
            if (tmpInt != null) {
                numThreads = tmpInt.intValue();
            }
            
            tmpInt = cmd.hasOption(evidOption.getOpt()) ? (Number) cmd.getParsedOptionValue(evidOption.getOpt()) : null;
            if (tmpInt != null) {
                evid = tmpInt.intValue();
            }
            
            Number tmpDouble = cmd.hasOption(maxTemplateLengthOption.getOpt()) ? (Number) cmd.getParsedOptionValue(maxTemplateLengthOption.getOpt()) : null;
            if (tmpDouble != null && tmpDouble.doubleValue() > 0) {
                maxTemplateLength = tmpDouble.doubleValue();
            }
            tmpDouble = cmd.hasOption(minTemplateLengthOption.getOpt()) ? (Number) cmd.getParsedOptionValue(minTemplateLengthOption.getOpt()) : null;
            if (tmpDouble != null && tmpDouble.doubleValue() > 0) {
                minTemplateLength = tmpDouble.doubleValue();
            }
            
            tmpDouble = cmd.hasOption(minFreqOption.getOpt()) ? (Number) cmd.getParsedOptionValue(minFreqOption.getOpt()) : null;
            if (tmpDouble != null && tmpDouble.doubleValue() > 0) {
                minFrequency = tmpDouble.doubleValue();
            }
            tmpDouble = cmd.hasOption(maxFreqOption.getOpt()) ? (Number) cmd.getParsedOptionValue(maxFreqOption.getOpt()) : null;
            if (tmpDouble != null && tmpDouble.doubleValue() > 0) {
                maxFrequency = tmpDouble.doubleValue();
            }
            if (maxFrequency <= minFrequency) {
                System.err.println("MinFrequency is not greater than maxFrequency!");
                printUsage(options);
                System.exit(1);
            }
            tmpDouble = cmd.hasOption(subspaceThreshOption.getOpt()) ? (Number) cmd.getParsedOptionValue(subspaceThreshOption.getOpt()) : null;
            if (tmpDouble != null && tmpDouble.doubleValue() > 0) {
                ssThresh = tmpDouble.doubleValue();
            }
            tmpDouble = cmd.hasOption(staltaThreshOption.getOpt()) ? (Number) cmd.getParsedOptionValue(staltaThreshOption.getOpt()) : null;
            if (tmpDouble != null && tmpDouble.doubleValue() > 0) {
                staLtaThresh = tmpDouble.doubleValue();
            }
            
            tmpDouble = cmd.hasOption(beamAzimuthOption.getOpt()) ? (Number) cmd.getParsedOptionValue(beamAzimuthOption.getOpt()) : null;
            if (bootDetectorType == DetectorType.ARRAYPOWER && tmpDouble == null) {
                System.err.println("Beam azimuth was not specified but boot detector type is set as ARRAYPOWER!");
                printUsage(options);
                System.exit(1);
            } else if (tmpDouble != null) {
                beamAzimuth = tmpDouble.doubleValue();
            }
            
            tmpDouble = cmd.hasOption(beamVelOption.getOpt()) ? (Number) cmd.getParsedOptionValue(beamVelOption.getOpt()) : null;
            if (bootDetectorType == DetectorType.ARRAYPOWER && tmpDouble == null) {
                System.err.println("Beam velocity was not specified but boot detector type is set as ARRAYPOWER!");
                printUsage(options);
                System.exit(1);
            } else if (tmpDouble != null) {
                beamVelocity = tmpDouble.doubleValue();
            }
            
            if (refSta == null && stations.isEmpty()) {
                System.err.println("You must specify either the -r or the -s option!");
                printUsage(options);
                System.exit(1);
            }
            
            tmpDouble = cmd.hasOption(snrThreshOption.getOpt()) ? (Number) cmd.getParsedOptionValue(snrThreshOption.getOpt()) : null;
            if (tmpDouble != null && tmpDouble.doubleValue() > 0) {
                snrThreshold = tmpDouble.doubleValue();
            }
            
            tmpDouble = cmd.hasOption(minDurationOption.getOpt()) ? (Number) cmd.getParsedOptionValue(minDurationOption.getOpt()) : null;
            if (tmpDouble != null && tmpDouble.doubleValue() > 0) {
                minEventDuration = tmpDouble.doubleValue();
            }
            
            tmpDouble = cmd.hasOption(blockSizeSecondsOption.getOpt()) ? (Number) cmd.getParsedOptionValue(blockSizeSecondsOption.getOpt()) : null;
            if (tmpDouble != null && tmpDouble.doubleValue() > 0) {
                blockSizeSeconds = tmpDouble.doubleValue();
            }
            
            tmpInt = cmd.hasOption(decimationRateOption.getOpt()) ? (Number) cmd.getParsedOptionValue(decimationRateOption.getOpt()) : null;
            if (tmpInt != null) {
                decimationRate = tmpInt.intValue();
            }
            
        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            printUsage(options);
            System.exit(2);
        }
        
    }
    
    private boolean directoryExists() {
        File pwd = new File(".");
        configDirectory = new File(pwd, configName);
        return configDirectory.exists();
    }
    
    private boolean refStaInSite() throws SQLException {
        if (refSta == null || refSta.isEmpty()) {
            ApplicationLogger.getInstance().log(Level.INFO, String.format("No refsta was specified, so will choose one from database"));
            refSta = findDefaultRefsta();
            ApplicationLogger.getInstance().log(Level.INFO, String.format("%s was chosen from database", refSta));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select sta from %s where refsta = ?", TableNames.getInstance().getSiteTableName()));
            stmt.setString(1, refSta);
            rs = stmt.executeQuery();
            return rs.next();
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
    
    private boolean isArray(String refSta) throws SQLException {
        if (!stations.isEmpty()) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select statype from %s where sta = ?", TableNames.getInstance().getSiteTableName()));
            stmt.setString(1, refSta);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String statype = rs.getString(1);
                return statype.equals("ar");
            } else {
                throw new IllegalStateException("Could not determine statype for refsta: " + refSta);
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
    
    private String findDefaultRefsta() throws SQLException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select refsta, count(*) from %s a, %s b where "
                    + "a.sta = b.sta and jdate between ondate and offdate group by refsta order by count(*) desc",
                    wfdiscName, TableNames.getInstance().getSiteTableName()));
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new IllegalStateException("Failed to find station or array with data in WFDISC and supporting SITE row(s)");
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
    
    private void findStationDataRange() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            ApplicationLogger.getInstance().log(Level.INFO, String.format("Determining WFDISC data range for stations (This may take a while.)..."));
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select min(jdate), max(jdate) from %s where sta = ? ", wfdiscName));
            for (String sta : stations) {
                checkThisSta(stmt, sta);
            }
            
        } finally {
            
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
        
    }
    
    private void findRefstaDataRange() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            ApplicationLogger.getInstance().log(Level.INFO, String.format("Determining WFDISC data range for refsta %s (This may take a while.)...", refSta));
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select min(jdate), max(jdate) from %s a, "
                    + "%s b where refsta = ? and a.sta = b.sta and jdate between %d and %d and jdate between ondate and offdate",
                    wfdiscName, TableNames.getInstance().getSiteTableName(), minDate, maxDate));
            stmt.setString(1, refSta);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                int defaultMinDate = rs.getInt(1);
                int defaultMaxDate = rs.getInt(2);
                minDate = defaultMinDate;
                ApplicationLogger.getInstance().log(Level.INFO, String.format("Setting MinJdate to %d", minDate));
                minDate = defaultMinDate;
                
                maxDate = defaultMaxDate;
                ApplicationLogger.getInstance().log(Level.INFO, String.format("Setting MaxJdate to %d", maxDate));
                
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
    
    private void chooseChannels() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        chans = new ArrayList<>();
        try {
//            ApplicationLogger.getInstance().log(Level.INFO, String.format("Determining available channels in WFDISC for refsta %s...", refSta));

            conn = ConnectionManager.getInstance().checkOut();
            if (stations.isEmpty()) {
                ApplicationLogger.getInstance().log(Level.INFO, String.format("Determining available channels in WFDISC for refsta %s...", refSta));
                String sql = String.format("select b.chan, band, instrument, "
                        + "orientation, count(*) from %s a, %s b, %s c where "
                        + "a.refsta = ? and a.sta = b.sta and b.sta != ? and b.chan = c.chan "
                        + "and jdate between ondate and offdate and jdate between %d and %d "
                        + "group by b.chan, band, instrument, orientation order by count(*) desc",
                        TableNames.getInstance().getSiteTableName(), wfdiscName,
                        TableNames.getInstance().getChanDescTableName(), minDate, maxDate);
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, refSta);
                stmt.setString(2, refSta);
            } else {
                ApplicationLogger.getInstance().log(Level.INFO, String.format("Determining available channels in WFDISC for user-supplied stations..."));
                String items = makeQuotedStationString();
                String sql = String.format("select b.chan, band, instrument, "
                        + "orientation, count(*) from %s b, %s c where "
                        + "b.sta in (%s) and b.chan = c.chan and jdate between %d and %d "
                        + "group by b.chan, band, instrument, orientation order by count(*) desc", wfdiscName,
                        TableNames.getInstance().getChanDescTableName(), items, minDate, maxDate);
                stmt = conn.prepareStatement(sql);
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                String chan = rs.getString(1);
                String band = rs.getString(2);
                String instrument = rs.getString(3);
                String orientation = rs.getString(4);
                int count = rs.getInt(5);
                chans.add(new ChanInfo(chan, band, instrument, orientation, count));
            }
            if (chans.isEmpty()) {
                throw new IllegalStateException("Could not retrieve a set of channels for refsta!");
            }
            ChannelDisplayPanel panel = new ChannelDisplayPanel(chans, refSta);
            String[] options1 = {"Accept", "Cancel"};
            int answer = JOptionPane.showOptionDialog(null, panel, "Choose channels to use with configuration", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, // do not use a
                    // custom Icon
                    options1, // the titles of buttons
                    options1[0]);
            if (answer == JOptionPane.YES_OPTION) {
                chans.clear();
                chans.addAll(panel.getSelectedChannels());
                if (chans.isEmpty()) {
                    throw new IllegalStateException("No Channels were chosen!");
                }
            } else {
                System.exit(0);
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
    
    private void verifyDataAvailability() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        staChansToUse = new ArrayList<>();
        try {
            conn = ConnectionManager.getInstance().checkOut();
            ApplicationLogger.getInstance().log(Level.INFO, String.format("Determining available station-channel-bands in WFDISC for date range..."));
            
            StringBuilder sb = new StringBuilder();
            for (ChanInfo ci : chans) {
                if (stations.isEmpty()) {
                    String tmp = String.format("select distinct b.sta, b.chan from %s a, %s b, %s c where "
                            + "a.refsta = '%s' and statype = 'ss' and b.sta not like '%%31' and b.sta not like '%%32' "
                            + "and a.sta = b.sta and b.chan = c.chan and "
                            + "c.chan = '%s' and jdate between %d and %d ",
                            TableNames.getInstance().getSiteTableName(), wfdiscName,
                            TableNames.getInstance().getChanDescTableName(),
                            refSta, ci.getChan(), minDate, maxDate);
                    sb.append(tmp);
                } else {
                    String tmp = String.format("select distinct b.sta, b.chan from %s b, %s c where "
                            + "b.sta in (%s) and b.chan = c.chan and "
                            + "band = '%s' and instrument = '%s' and orientation = '%s' and jdate between %d and %d", wfdiscName,
                            TableNames.getInstance().getChanDescTableName(),
                            this.makeQuotedStationString(), ci.getBand(), ci.getInstrument(), ci.getOrientation(), minDate, maxDate);
                    sb.append(tmp);
                }
                sb.append("\nunion ");
            }
            
            String sql = sb.toString();
            sql = sql.substring(0, sql.lastIndexOf("\nunion "));
            
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                String sta = rs.getString(1);
                String chan = rs.getString(2);
                staChansToUse.add(new StreamKey(sta, chan));
            }
            if (staChansToUse.isEmpty()) {
                throw new IllegalStateException("Could not retrieve a set of channels for refsta and chosen bands in chosen time period!");
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
    
    private int getDistinctStationCount() {
        Set<String> stations = new HashSet<>();
        for (StreamKey sc : staChansToUse) {
            stations.add(sc.getSta());
        }
        return stations.size();
    }
    
    private void checkThisSta(PreparedStatement stmt, String sta) throws SQLException {
        
        ResultSet rs = null;
        try {
            stmt.setString(1, sta);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                int defaultMinDate = rs.getInt(1);
                int defaultMaxDate = rs.getInt(2);
                if (rs.wasNull()) {
                    String msg = String.format("Station %s not found in %s table!", sta, this.wfdiscName);
                    throw new IllegalStateException(msg);
                }
                
                if (minDate == null) {
                    minDate = defaultMinDate;
                    ApplicationLogger.getInstance().log(Level.INFO, String.format("Setting MinJdate to %d", minDate));
                } else if (minDate < defaultMinDate) {
                    ApplicationLogger.getInstance().log(Level.INFO, String.format("Requested MinJdate of %d is out of bounds of data. Setting MinJdate to %d", minDate, defaultMinDate));
                    minDate = defaultMinDate;
                }
                if (maxDate == null) {
                    maxDate = defaultMaxDate;
                    ApplicationLogger.getInstance().log(Level.INFO, String.format("Setting MaxJdate to %d", maxDate));
                } else if (maxDate > defaultMaxDate) {
                    ApplicationLogger.getInstance().log(Level.INFO, String.format("Requested MaxJdate of %d is out of bounds of data. Setting MaxJdate to %d", maxDate, defaultMaxDate));
                    maxDate = defaultMaxDate;
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            
        }
        
    }
    
    private String makeQuotedStationString() {
        StringBuilder sb = new StringBuilder("'");
        for (String sta : stations) {
            sb.append(sta);
            sb.append("','");
        }
        String tmp = sb.toString();
        int idx = tmp.lastIndexOf(",'");
        return tmp.substring(0, idx);
    }
    
    private void computeAzAndSlowness() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        chans = new ArrayList<>();
        double hSlowness = 0.125;
        try {
            ApplicationLogger.getInstance().log(Level.INFO, String.format("Determining BAZ and slowness for refsta %s...", refSta));
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select a.lat olat, a.lon olon, a.depth, b.lat slat, b.lon slon from %s a, "
                    + " %s b where evid = ? and sta = ? and jdate between ondate and offdate",
                    TableNames.getInstance().getOriginTableName(),
                    TableNames.getInstance().getSiteTableName()));
            stmt.setInt(1, evid);
            stmt.setString(2, refSta);
            rs = stmt.executeQuery();
            while (rs.next()) {
                double olat = rs.getDouble(1);
                double olon = rs.getDouble(2);
                double slat = rs.getDouble(4);
                double slon = rs.getDouble(5);
                beamAzimuth = EModel.getAzimuth(slat, slon, olat, olon);
                
                beamVelocity = 1 / hSlowness;
                return;
            }
            
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    private void reportChannelStats() throws SQLException {
        for (StreamKey sc : staChansToUse) {
            reportOnChannel(sc);
        }
    }
    
    private void reportOnChannel(StreamKey sc) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        chans = new ArrayList<>();
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select endtime - time from %s where sta = ? and chan = ? and jdate between ? and ?", wfdiscName));
            stmt.setString(1, sc.getSta());
            stmt.setString(2, sc.getChan());
            stmt.setInt(3, minDate);
            stmt.setInt(4, maxDate);
            rs = stmt.executeQuery();
            double sum = 0;
            while (rs.next()) {
                sum += rs.getDouble(1);
            }
            sum /= 3600;
            double possible = getRequestedDurationInHours();
            ApplicationLogger.getInstance().log(Level.INFO, String.format("For %s (%5.2f hours out of %5.2f hours)", sc, sum, possible));
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
    
    private double getRequestedDurationInHours() {
        int year = minDate / 1000;
        int jday = minDate % 1000;
        TimeT tBegin = new TimeT(year, jday, 0, 0, 0, 0);
        year = (maxDate + 1) / 1000;
        jday = (maxDate + 1) % 1000;
        TimeT tEnd = new TimeT(year, jday, 0, 0, 0, 0);
        Epoch e = new Epoch(tBegin.getEpochTime(), tEnd.getEpochTime() - 0.001);
        return e.duration() / 3600;
    }
}
