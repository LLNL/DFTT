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
package llnl.gnem.apps.detection.util.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import llnl.gnem.apps.detection.dataAccess.ApplicationRoleManager;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.SeismogramSourceInfo;
import llnl.gnem.core.database.DbCommandLineParser;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class ConfigCreatorParameters {

    private DbCommandLineParser parser;

    private String refSta;
    private String configName;

    private Integer minDate = -1;
    private Integer maxDate = 2286324;

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
    private int decimationRate = 1;
    private boolean spawnCorrelationDetectors = true;
    private DetectorType bootDetectorType = DetectorType.STALTA;
    private Double beamAzimuth = null;
    private Double beamVelocity = null;
    private Collection<String> stations = new ArrayList<>();
    private SeismogramSourceInfo seismogramSourceInfo = new SeismogramSourceInfo(); // defaults to use the DB

    public String getRefSta() {
        return refSta;
    }

    public String getConfigName() {
        return configName;
    }

    public Integer getMinDate() {
        return minDate;
    }

    public Integer getMaxDate() {
        return maxDate;
    }

    public Collection<StreamKey> getStaChansToUse() {
        return staChansToUse;
    }

    public double getMinTemplateLength() {
        return minTemplateLength;
    }

    public double getMaxTemplateLength() {
        return maxTemplateLength;
    }

    public double getMinFrequency() {
        return minFrequency;
    }

    public double getMaxFrequency() {
        return maxFrequency;
    }

    public double getSsThresh() {
        return ssThresh;
    }

    public double getStaLtaThresh() {
        return staLtaThresh;
    }

    public double getSnrThreshold() {
        return snrThreshold;
    }

    public double getMinEventDuration() {
        return minEventDuration;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public double getBlockSizeSeconds() {
        return blockSizeSeconds;
    }

    public int getDecimationRate() {
        return decimationRate;
    }

    public boolean isSpawnCorrelationDetectors() {
        return spawnCorrelationDetectors;
    }

    public DetectorType getBootDetectorType() {
        return bootDetectorType;
    }

    public Double getBeamAzimuth() {
        return beamAzimuth;
    }

    public Double getBeamVelocity() {
        return beamVelocity;
    }

    public Collection<String> getStations() {
        return stations;
    }

    public SeismogramSourceInfo getSeismogramSourceInfo() {
        return seismogramSourceInfo;
    }

    public boolean isStationBased() {
        return !stations.isEmpty() && (refSta == null || refSta.isEmpty());
    }

    public boolean isArrayBased() {
        return getStations().isEmpty() && refSta != null && !refSta.isEmpty();
    }

    private ConfigCreatorParameters() {
    }

    private void printUsage(Options options) {
        String usage = "ConfigCreator login/password@instance  [options]";

        String footer = "\n";

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usage, "", options, footer, false);
    }

    public void initializeConnection() throws Exception {
        DAOFactory.getInstance(parser.getCredentials().username, parser.getCredentials().password, parser.getCredentials().instance, new ApplicationRoleManager());
    }

    public void getCommandLineInfo(String[] args) throws IOException {

        Options options = new Options();

        Option help = new Option("h", "help", false, "Show this message");

        Option configOption = new Option("c", "ConfigName", true, "Specifies the name of this configuration.");
        configOption.setType(String.class);
        configOption.setRequired(true);
        options.addOption(configOption);

        Option stationCodesOption = new Option("s",
                                               "StationList",
                                               true,
                                               "One or more station codes to use in creating this configuration. If more than 1 then separate with commas and no spaces e.g. -s sta1,sta2,sta3.");
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

        Option minJdateOption = new Option("m", "MinDate", true, "The minDate to use for this configuration.");
        minJdateOption.setType(Number.class);
        minJdateOption.setRequired(true);
        options.addOption(minJdateOption);

        Option maxJdateOption = new Option("M", "MaxDate", true, "The maxDate to use for this configuration.");
        maxJdateOption.setType(Number.class);
        maxJdateOption.setRequired(true);
        options.addOption(maxJdateOption);

        Option numThreadsOption = new Option("n", "NumThreads", true, "The number of threads used for detection processing (defaults to 4).");
        numThreadsOption.setType(Number.class);
        numThreadsOption.setRequired(false);
        options.addOption(numThreadsOption);

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

        Option sourceIdentifierOption = new Option("I",
                                                   "SourceIdentifier",
                                                   true,
                                                   "When source_type is CssDatabase this is the name of the continuous_wfdisc table. When source_type is FDSN this is the agency string. When source_type is Type2Database this field is ignored.");
        Option srcTypeOption = new Option("type", "SourceType", true, "Source type (one of CssDatabase, Type2Database, FDSN)");
        srcTypeOption.setRequired(false);
        sourceIdentifierOption.setRequired(false);
        srcTypeOption.setType(String.class);
        sourceIdentifierOption.setType(String.class);
        options.addOption(sourceIdentifierOption);
        options.addOption(srcTypeOption);

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

            configName = cmd.getOptionValue(configOption.getOpt());

            String tmp = cmd.hasOption(stationCodesOption.getOpt()) ? cmd.getOptionValue(stationCodesOption.getOpt()) : null;

            if (tmp != null) {
                StringTokenizer st = new StringTokenizer(tmp, ",");
                while (st.hasMoreTokens()) {
                    stations.add(st.nextToken().trim());
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
            if (cmd.hasOption(srcTypeOption.getOpt()) && cmd.hasOption(sourceIdentifierOption.getOpt())) {
                tmp = cmd.getOptionValue(srcTypeOption.getOpt());
                SeismogramSourceInfo.SourceType sourceType = SeismogramSourceInfo.SourceType.valueOf(tmp);
                tmp = cmd.getOptionValue(sourceIdentifierOption.getOpt());
                seismogramSourceInfo = new SeismogramSourceInfo(sourceType, tmp);
            }

            if (isArrayBased() && (beamAzimuth == null || beamVelocity == null)) {
                System.out.println("Array configurations must have both beam azimuth and velocity specified!");
                printUsage(options);
                System.exit(1);
            }

        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            printUsage(options);
            System.exit(2);
        }

    }

    public static ConfigCreatorParameters getInstance() {
        return ConfigCreatorParametersHolder.INSTANCE;
    }

    public Epoch getRequestEpoch() {
        TimeT time = TimeT.getTimeFromJulianDate(minDate);
        TimeT endTime = TimeT.getTimeFromJulianDate(maxDate);
        return new Epoch(time, endTime);
    }

    private static class ConfigCreatorParametersHolder {

        private static final ConfigCreatorParameters INSTANCE = new ConfigCreatorParameters();
    }
}
