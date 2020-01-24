package llnl.gnem.apps.detection;

import Jampack.JampackParameters;
import llnl.gnem.apps.detection.database.ConfigurationDAO;
import llnl.gnem.apps.detection.database.TableNames;
import llnl.gnem.apps.detection.source.*;
import llnl.gnem.apps.detection.tasks.BlockRetrievalFutureTask;
import llnl.gnem.apps.detection.tasks.ComputationService;
import llnl.gnem.apps.detection.tasks.RetrieveAllBlocksTask;
import llnl.gnem.apps.detection.util.PowerDetThreshold;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.apps.detection.util.SubspaceThreshold;
import llnl.gnem.apps.detection.util.SubspaceUpdateParams;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import llnl.gnem.apps.detection.database.ChannelSubstitutionDAO;
import llnl.gnem.apps.detection.util.DetectoridRestriction;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.database.DbCommandLineParser;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.BuildInfo;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.apps.detection.database.FrameworkRunDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Created by dodge1 Date: Jan 8, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class FrameworkRunner {
    
    private SourceData source;
    private DbCommandLineParser parser;
    private String sourceWfdiscTable;
    private static String logLevel = "INFO";
    private final DetectionFramework framework;
    private final double primaryBufferSize = 7200.0;
    private boolean exitOnFileEnd = false;
    private boolean scaleByCalib;
    private Integer runidToResume = null;
    
    private void printUsage(Options options) {
        String usage = "framework_runner login/password@instance -E <arg> | -e <arg> [options]";
        
        StringBuilder footer = new StringBuilder("\nAvailable logger levels  are:  ");
        
        footer.append("\n\n").append(ApplicationLogger.getAllLevelsString());
        
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usage, "", options, footer.toString(), false);
    }
    
    private void initializeConnection() throws Exception {
        
        ConnectionManager.getInstance(parser.getCredentials().username, parser.getCredentials().password, parser.getCredentials().instance);
    }
    
    public FrameworkRunner(String[] args) throws Exception {
        
        String buf = "usage: DetectionFramework login/password  \n{Required Arguments:}\n\t";
        
        JampackParameters.setBaseIndex(0);
        getCommandLineInfo(args, buf);
        String commandLineArgs = getCommandLineString(args);
        
        String configName = ProcessingPrescription.getInstance().getConfigName();
        if ((configName == null || configName.isEmpty())) {
            System.err.println("No configuration was specified in the parameter file!");
            System.exit(1);
        }
        source = new WfdiscTableSourceData(sourceWfdiscTable, configName, scaleByCalib);

        double fixSampleRateValue = ProcessingPrescription.getInstance().isFixRawSampleRate() ? ProcessingPrescription.getInstance().getFixedRawSampleRate() : -999;
        if (ProcessingPrescription.getInstance().isCreateConfiguration() && runidToResume == null) {
            ConfigurationDAO.getInstance().createOrReplaceConfigurationUsingInputFiles(configName, source.getWfdiscTable(), commandLineArgs, fixSampleRateValue);
        }
        RunInfo.getInstance().initialize(runidToResume, sourceWfdiscTable, commandLineArgs, fixSampleRateValue);
        int configid = ConfigurationDAO.getInstance().getConfigid(configName);

        
        Integer startingJdate = null;
        if( runidToResume != null){
            startingJdate = FrameworkRunDAO.getInstance().getJdateOfLastTrigger(runidToResume);
        }
        source.setFixedRawRate(fixSampleRateValue);
        source.setChannelSubstitutions(ChannelSubstitutionDAO.getInstance().getChannelSubstitutions(configid));
        source.setStaChanArrays();
        source.summarize(startingJdate);
        ProcessingPrescription.getInstance().validateStreamParams(source.getCommonSampleRate());
        
        RetrieveAllBlocksTask task = new RetrieveAllBlocksTask(source, exitOnFileEnd);
        BlockRetrievalFutureTask mft = new BlockRetrievalFutureTask(task);
        ComputationService.getInstance().getBlockRetrievalExecutorService().execute(mft);
        
        framework = new DetectionFramework(source, primaryBufferSize);
    }
    
    public final void getCommandLineInfo(String[] args, String helpMsg) throws IOException, FileNotFoundException {
        
        Options options = new Options();
        Option help = new Option("h", "help", false, "Show this message");
        Option parameterFileOption = new Option("p", "parameterFile", true, "Name of text file containing processing parameters.");
        Option exitOnEndOption = new Option("E", "exitOnFileEnd", false, "Stop processing when end of data is reached. (Default is false.) When false, code will periodically check for new data in source wfdisc.");
        Option logLevelOption = new Option("L", "LogLevel", true, "The logging level to use.");
        Option srcWfdiscTableOption = new Option("t", "SourceWfdiscTable", true, "Source WFDISC table");
        
        Option ssThreshOption = new Option("S", "SubspaceThreshold", true, "When specified, the value here will override all others.");
        Option pdThreshOption = new Option("P", "PowerDetThreshold", true, "When specified, the value here will override all others.");
        Option updateSSDetOption = new Option("u", "UpdateSubspaceDetectors", false, "When specified, new detections may be used to update the detectors. (Default is false.)");
        Option sdUpdateThreshOption = new Option("T", "UpdateThreshold", true, "A new detection will only be used to update a subspace detector if its detection statistic >= this value. (Default = 0.8)");
        
        Option sdUpdateLambdaOption = new Option("l", "LambdaOption", true, "Fade-out factor. (Default = 0.9)");
        Option sdUpdateECaptureOption = new Option("e", "EnergyCaptureOption", true, "The energy capture to use for subspace updating. (Default = 0.8)");
        
        Option siteTableNameOption = new Option("site", "SiteTableName", true, "The name of the SITE table used for station queries. Default is SITE in the current schema.");
        Option scaleByCalibOption = new Option("c", "ScaleByCalib", false, "Scale all waveforms by the calib value in wfdisc table. (Default is false.)");
        Option nonStrictCompatibilityOption = new Option("n", "NonStrict", false, "When specified, waveform segments must match in code but not necessarily location to be concatenated. (Default requires both.)");
 
        Option detectoridFileOption = new Option("d", "DetectoridFile", true, "Name of text file containing DETECTORID values. Only these detectors will be loaded.");
        Option resumeRunOption = new Option("r", "ResumeRun", true, "the runid of the run to resume. If exists and is compatible with configuration,execution will resume on day of last trigger.");
        
        
        siteTableNameOption.setRequired(false);
        srcWfdiscTableOption.setRequired(true);
        parameterFileOption.setRequired(true);
        scaleByCalibOption.setRequired(false);
        nonStrictCompatibilityOption.setRequired(false);
        detectoridFileOption.setRequired(false);
        resumeRunOption.setRequired(false);
        
        siteTableNameOption.setType(String.class);
        ssThreshOption.setType(Number.class);
        pdThreshOption.setType(Number.class);
        sdUpdateThreshOption.setType(Number.class);
        sdUpdateLambdaOption.setType(Number.class);
        sdUpdateECaptureOption.setType(Number.class);
        nonStrictCompatibilityOption.setType(Boolean.class);
        detectoridFileOption.setType(String.class);
        resumeRunOption.setType(Number.class);
        
        options.addOption(help);
        
        options.addOption(parameterFileOption);
        options.addOption(siteTableNameOption);
        options.addOption(exitOnEndOption);
        options.addOption(logLevelOption);
        options.addOption(srcWfdiscTableOption);
        options.addOption(ssThreshOption);
        options.addOption(pdThreshOption);
        options.addOption(updateSSDetOption);
        options.addOption(sdUpdateThreshOption);
        options.addOption(sdUpdateLambdaOption);
        options.addOption(sdUpdateECaptureOption);
        options.addOption(scaleByCalibOption);
        options.addOption(nonStrictCompatibilityOption);
        options.addOption(detectoridFileOption);
        options.addOption(resumeRunOption);
        
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
            initializeConnection();
            
            String configFileName = cmd.getOptionValue(parameterFileOption.getOpt());
            ProcessingPrescription.getInstance().initialize(configFileName);
            exitOnFileEnd = cmd.hasOption(exitOnEndOption.getOpt());
            scaleByCalib = cmd.hasOption(scaleByCalibOption.getOpt());
            
            getLogLevel(cmd, logLevelOption);
            ApplicationLogger.getInstance().setLevel(logLevel);
            
            sourceWfdiscTable = cmd.hasOption(srcWfdiscTableOption.getOpt()) ? cmd.getOptionValue(srcWfdiscTableOption.getOpt()) : null;
            String site = cmd.hasOption(siteTableNameOption.getOpt()) ? cmd.getOptionValue(siteTableNameOption.getOpt()) : "site";
            TableNames.getInstance().setSiteTableName(site);
            
            String detectoridFileName = cmd.hasOption(detectoridFileOption.getOpt()) ? cmd.getOptionValue(detectoridFileOption.getOpt()) : "-";
            DetectoridRestriction.getInstance().maybeLoadDetectoridFile(detectoridFileName);
            
            
            SubspaceUpdateParams.getInstance().setUpdateOnDetection(cmd.hasOption(updateSSDetOption.getOpt()));
            
            if (cmd.hasOption(ssThreshOption.getOpt())) {
                double v = ((Number) cmd.getParsedOptionValue(ssThreshOption.getOpt())).doubleValue();
                SubspaceThreshold.getInstance().setCommandlineOverride(v);
            }
            if (cmd.hasOption(pdThreshOption.getOpt())) {
                double v = ((Number) cmd.getParsedOptionValue(pdThreshOption.getOpt())).doubleValue();
                PowerDetThreshold.getInstance().setCommandlineOverride(v);
            }
            
            if (cmd.hasOption(sdUpdateThreshOption.getOpt())) {
                double v = ((Number) cmd.getParsedOptionValue(sdUpdateThreshOption.getOpt())).doubleValue();
                SubspaceUpdateParams.getInstance().setUpdateThreshold(v);
            }
            
            if (cmd.hasOption(sdUpdateLambdaOption.getOpt())) {
                double v = ((Number) cmd.getParsedOptionValue(sdUpdateLambdaOption.getOpt())).doubleValue();
                SubspaceUpdateParams.getInstance().setLambda(v);
            }
            if (cmd.hasOption(sdUpdateECaptureOption.getOpt())) {
                double v = ((Number) cmd.getParsedOptionValue(sdUpdateECaptureOption.getOpt())).doubleValue();
                SubspaceUpdateParams.getInstance().setEnergyCapture(v);
            }
            if (cmd.hasOption(resumeRunOption.getOpt())) {
                runidToResume = ((Number) cmd.getParsedOptionValue(resumeRunOption.getOpt())).intValue();
            }
            
            if (cmd.hasOption(nonStrictCompatibilityOption.getOpt())) {
                WaveformSegment.setStrictness(WaveformSegment.CompatibilityStrictness.ONLY_MATCH_CODES);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            printUsage(options);
            System.exit(2);
        }
        
    }
    
    public void run() throws Exception {
        framework.initialize();
        framework.run();
    }
    
    public void close() throws Exception {
        
        if (framework != null) {
            framework.close();
        }
    }
    
    public static void main(String[] args) {
        
        FrameworkRunner runner = null;
        try {
            BuildInfo buildInfo = new BuildInfo(FrameworkRunner.class);
            
            ApplicationLogger.getInstance().setFileHandler("DetectionFramework", false);
            ApplicationLogger.getInstance().useConsoleHandler();
            ApplicationLogger.getInstance().setLevel(logLevel);
            if (buildInfo.isFromJar()) {
                ApplicationLogger.getInstance().log(Level.INFO, buildInfo.getBuildInfoString());
            }
            runner = new FrameworkRunner(args);
            runner.summarizeSource();
            runner.run();
        } catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "General Failure", e);
        } catch (OutOfMemoryError ome) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Out of memory!");
        } finally {
            if (runner != null) {
                try {
                    runner.close();
                } catch (Exception e) {
                    ApplicationLogger.getInstance().log(Level.WARNING, "General Failure", e);
                }
            }
        }
        
    }
    
    private void summarizeSource() throws SQLException {
        source.printSummary();
    }
    
    private String getCommandLineString(String[] args) {
        StringBuilder buf = new StringBuilder();
        for (String arg : args) {
            buf.append(arg);
            buf.append(" ");
        }
        return buf.toString().trim();
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
}
