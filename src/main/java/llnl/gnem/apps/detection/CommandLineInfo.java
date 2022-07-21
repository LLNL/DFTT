/*-
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import llnl.gnem.apps.detection.dataAccess.ApplicationRoleManager;
import llnl.gnem.apps.detection.util.DetectoridRestriction;
import llnl.gnem.apps.detection.util.PowerDetThreshold;
import llnl.gnem.apps.detection.util.SubspaceThreshold;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.SeismogramSourceInfo;
import llnl.gnem.core.database.DbCommandLineParser;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class CommandLineInfo {

    private DbCommandLineParser parser;

    private static String logLevel = "INFO";

    private final double primaryBufferSize = 7200.0;

    private boolean scaleByCalib;
    private Integer runidToResume = null;
    private boolean ignoreDetectorClassification = false;

    private CommandLineInfo() {
    }
    
    public static CommandLineInfo getInstance() {
        return CommandLineInfoHolder.INSTANCE;
    }
    
    private static class CommandLineInfoHolder {

        private static final CommandLineInfo INSTANCE = new CommandLineInfo();
    }

    public static String getLogLevel() {
        return logLevel;
    }

    public double getPrimaryBufferSize() {
        return primaryBufferSize;
    }

    public boolean isScaleByCalib() {
        return scaleByCalib;
    }

    public Integer getRunidToResume() {
        return runidToResume;
    }

    public boolean isIgnoreDetectorClassification() {
        return ignoreDetectorClassification;
    }
    
    private void printUsage(Options options) {
        String usage = "framework_runner login/password@instance -E <arg> | -e <arg> [options]";

        StringBuilder footer = new StringBuilder("\nAvailable logger levels  are:  ");

        footer.append("\n\n").append(ApplicationLogger.getAllLevelsString());

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usage, "", options, footer.toString(), false);
    }

    private void initializeConnection() throws Exception {

        DAOFactory.getInstance(parser.getCredentials().username, parser.getCredentials().password, parser.getCredentials().instance, new ApplicationRoleManager());
    }    
    
    public final void getCommandLineInfo(String[] args, String helpMsg) throws IOException, FileNotFoundException {

        Options options = new Options();
        Option help = new Option("h", "help", false, "Show this message");
        Option parameterFileOption = new Option("p", "parameterFile", true, "Name of text file containing processing parameters.");
        Option sourceIdentifierOption = new Option("t",
                                                   "SourceIdentifier",
                                                   true,
                                                   "When source_type is CssDatabase this is the name of the continuous_wfdisc table. When source_type is FDSN this is the agency string. When source_type is Database this field is ignored.");
        Option logLevelOption = new Option("L", "LogLevel", true, "The logging level to use.");
        Option srcTypeOption = new Option("T", "SourceType", true, "Source type (one of CssDatabase, Database, FDSN)");

        Option ssThreshOption = new Option("S", "SubspaceThreshold", true, "When specified, the value here will override all others.");
        Option pdThreshOption = new Option("P", "PowerDetThreshold", true, "When specified, the value here will override all others.");
        Option scaleByCalibOption = new Option("c", "ScaleByCalib", false, "Scale all waveforms by the calib value in wfdisc table. (Default is false.)");

        Option detectoridFileOption = new Option("d", "DetectoridFile", true, "Name of text file containing DETECTORID values. Only these detectors will be loaded.");
        Option resumeRunOption = new Option("r", "ResumeRun", true, "the runid of the run to resume. If exists and is compatible with configuration,execution will resume on day of last trigger.");
        Option ignoreClassOption = new Option("i",
                                              "Ignore",
                                              false,
                                              "When specified, no detectors will be excluded at startup regardless of classification (Default behavior is that b and u class detectors are excluded.)");
        Option replaceBulletinDetectorOption = new Option("R",
                                                          "ReplaceBulletinDetector",
                                                          false,
                                                          "When specified, any existing bulletin detectors for specified streamid (See TargetStreamid option) are removed and the current specification file is used to create new bulletin detector(s)");
        Option targetStreamidOption = new Option("m", "TargetStreamid", true, "This is the STREAMID which is the target for bulletin detector replacement.");

        srcTypeOption.setRequired(false);
        sourceIdentifierOption.setRequired(false);
        parameterFileOption.setRequired(true);
        scaleByCalibOption.setRequired(false);
        detectoridFileOption.setRequired(false);
        resumeRunOption.setRequired(false);
        ignoreClassOption.setRequired(false);
        replaceBulletinDetectorOption.setRequired(false);
        targetStreamidOption.setRequired(false);

        ssThreshOption.setType(Number.class);
        pdThreshOption.setType(Number.class);
        detectoridFileOption.setType(String.class);
        resumeRunOption.setType(Number.class);
        ignoreClassOption.setType(Boolean.class);
        replaceBulletinDetectorOption.setType(Boolean.class);
        targetStreamidOption.setType(Number.class);
        srcTypeOption.setType(String.class);
        sourceIdentifierOption.setType(String.class);

        options.addOption(help);

        options.addOption(parameterFileOption);
        options.addOption(sourceIdentifierOption);
        options.addOption(logLevelOption);
        options.addOption(srcTypeOption);
        options.addOption(ssThreshOption);
        options.addOption(pdThreshOption);
        options.addOption(scaleByCalibOption);
        options.addOption(sourceIdentifierOption);
        options.addOption(detectoridFileOption);
        options.addOption(resumeRunOption);
        options.addOption(ignoreClassOption);
        options.addOption(replaceBulletinDetectorOption);
        options.addOption(targetStreamidOption);

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
            scaleByCalib = cmd.hasOption(scaleByCalibOption.getOpt());

            ignoreDetectorClassification = cmd.hasOption(ignoreClassOption.getOpt());
            DetectoridRestriction.getInstance().setIgnoreClassificationStatus(ignoreDetectorClassification);

            getLogLevel(cmd, logLevelOption);
            ApplicationLogger.getInstance().setLevel(logLevel);

            String detectoridFileName = cmd.hasOption(detectoridFileOption.getOpt()) ? cmd.getOptionValue(detectoridFileOption.getOpt()) : "-";
            DetectoridRestriction.getInstance().maybeLoadDetectoridFile(detectoridFileName);

            if (cmd.hasOption(ssThreshOption.getOpt())) {
                double v = ((Number) cmd.getParsedOptionValue(ssThreshOption.getOpt())).doubleValue();
                SubspaceThreshold.getInstance().setCommandlineOverride(v);
            }
            if (cmd.hasOption(pdThreshOption.getOpt())) {
                double v = ((Number) cmd.getParsedOptionValue(pdThreshOption.getOpt())).doubleValue();
                PowerDetThreshold.getInstance().setCommandlineOverride(v);
            }

            if (cmd.hasOption(resumeRunOption.getOpt())) {
                runidToResume = ((Number) cmd.getParsedOptionValue(resumeRunOption.getOpt())).intValue();
            }

            if (cmd.hasOption(replaceBulletinDetectorOption.getOpt())) {
                ProcessingPrescription.getInstance().setReplaceBulletinDetector(true);
            }

            if (cmd.hasOption(targetStreamidOption.getOpt())) {
                int streamid = ((Number) cmd.getParsedOptionValue(targetStreamidOption.getOpt())).intValue();
                ProcessingPrescription.getInstance().setTargetBulletinDetectorStreamid(streamid);
            }

            if (cmd.hasOption(srcTypeOption.getOpt()) && cmd.hasOption(sourceIdentifierOption.getOpt())) {
                String tmp = cmd.getOptionValue(srcTypeOption.getOpt());
                SeismogramSourceInfo.SourceType sourceType = SeismogramSourceInfo.SourceType.valueOf(tmp);
                tmp = cmd.getOptionValue(sourceIdentifierOption.getOpt());
                SeismogramSourceInfo ssi = new SeismogramSourceInfo(sourceType, tmp);
                ProcessingPrescription.getInstance().setSeismogramSourceInfo(ssi);
            }
            DAOFactory.getInstance().setSeismogramSourceInfo(ProcessingPrescription.getInstance().getSeismogramSourceInfo());

            if (ProcessingPrescription.getInstance().isReplaceBulletinDetector() && ProcessingPrescription.getInstance().getTargetBulletinDetectorStreamid() < 0) {
                System.err.println("BulletinDetector replacement was specified, but no target STREAMID was supplied!");
                printUsage(options);
                System.exit(1);
            }
        } catch (Exception ex) {
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

}
