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
package llnl.gnem.apps.detection.util.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.core.dataAccess.SeismogramSourceInfo;
import llnl.gnem.core.util.FileUtils;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class ConfigFileWriter {

    private static final String sep = System.getProperty("line.separator");
    private final File stream1Dir;
    private final String refSta;
    private final String configName;
    private final File configDirectory;
    private final Integer minDate;
    private final Integer maxDate;
    private final Collection<StreamKey> staChansToUse;
    private final double minTemplateLength;
    private final double maxTemplateLength;
    private final double minFrequency;
    private final double maxFrequency;
    private final double ssThresh;
    private final double staLtaThresh;
    private final double snrThreshold;
    private final double minEventDuration;
    private final int numThreads;
    private final DetectorType bootDetectorType;
    private final Double beamAzimuth;
    private final Double beamVelocity;
    private final File bulletinFile;
    private final double blockSizeSeconds;
    private final int decimationRate;
    private final boolean spawnCorrelationDetectors;

    public ConfigFileWriter(String refSta, String configName, File configDirectory, Integer minDate, Integer maxDate, Collection<StreamKey> staChansToUse, double minTemplateLength,
            double maxTemplateLength, double minFrequency, double maxFrequency, double ssThresh, double staLtaThresh, int numThreads, DetectorType bootDetectorType, Double beamAzimuth,
            Double beamVelocity, File bulletinFile, double snrThreshold, double minEventDuration, double blockSizeSeconds, int decimationRate, boolean spawnCorrelationDetectors) throws IOException {
        this.refSta = refSta;
        this.configName = configName;
        this.configDirectory = configDirectory;
        this.minDate = minDate;
        this.maxDate = maxDate;

        this.staChansToUse = new ArrayList<>(staChansToUse);
        this.minTemplateLength = minTemplateLength;
        this.maxTemplateLength = maxTemplateLength;
        this.minFrequency = minFrequency;
        this.maxFrequency = maxFrequency;
        this.ssThresh = ssThresh;
        this.staLtaThresh = staLtaThresh;
        this.snrThreshold = snrThreshold;
        this.minEventDuration = minEventDuration;
        this.numThreads = numThreads;
        this.bootDetectorType = bootDetectorType;
        this.beamAzimuth = beamAzimuth;
        this.beamVelocity = beamVelocity;
        this.bulletinFile = bulletinFile;
        this.blockSizeSeconds = blockSizeSeconds;
        this.decimationRate = decimationRate;
        this.spawnCorrelationDetectors = spawnCorrelationDetectors;
        stream1Dir = new File(configDirectory, "STREAM1");
    }

    public void create() throws IOException {
        buildCleanBaseDir();
        File streamFile = createStream1().toPath().normalize().toFile();
        File streamsFile = createStreamsTxtFile(streamFile).toPath().normalize().toFile();
        File detStatDirFile = createDetStatDir().toPath().normalize().toFile();
        File rawTracesDirFile = createRawTraceDir().toPath().normalize().toFile();
        File rawTraceSpecFile = createRawTraceSpecFile().toPath().normalize().toFile();
        File modifiedTracesDirFile = createModifiedTraceDir().toPath().normalize().toFile();
        File modifiedTraceSpecFile = createModifiedTraceSpecFile().toPath().normalize().toFile();
        writeConfigFile(streamsFile, detStatDirFile, rawTracesDirFile, rawTraceSpecFile, modifiedTracesDirFile, modifiedTraceSpecFile);
    }

    private void buildCleanBaseDir() throws IOException, IllegalStateException {
        if (configDirectory.exists()) {
            boolean deleted = FileUtils.deleteDirectory(configDirectory);
            if (!deleted) {
                throw new IllegalStateException("Failed to delete directory: " + configDirectory);
            }
        }
        boolean created = configDirectory.mkdirs();
        if (!created) {
            throw new IllegalStateException("Failed to create directory: " + configDirectory);
        }
    }

    private File createStream1() throws FileNotFoundException {

        boolean created = stream1Dir.mkdirs();
        if (!created) {
            throw new IllegalStateException("Failed to create directory: " + stream1Dir);
        }
        File bootDetectorFile = new BootDetectorCreator(stream1Dir, staLtaThresh, staChansToUse, bootDetectorType, beamAzimuth, beamVelocity).createBootDetectors(bulletinFile)
                .toPath()
                .normalize()
                .toFile();

        ExampleTemplateCreator etc = new ExampleTemplateCreator(stream1Dir, staChansToUse);
        Collection<File> templates = etc.makeTemplateDirectories();
        File templatesFile = makeTemplatesFile(templates).toPath().normalize().toFile();

        File streamChannelFile = createStreamChannelFile().toPath().normalize().toFile();

        return createStreamFile(bootDetectorFile, streamChannelFile, templatesFile);
    }

    private File makeTemplatesFile(Collection<File> templates) throws FileNotFoundException {
        File templatesFile = new File(stream1Dir, "DetectorsToCreate.txt");
        try (PrintWriter writer = new PrintWriter(templatesFile)) {
            for (File file : templates) {
                String tmp = file.getAbsolutePath();
                writer.print(String.format("%s%s", tmp.replace("\\", "\\\\"), sep));
            }
            return templatesFile;
        }

    }

    private File createStreamChannelFile() throws FileNotFoundException {
        File streamChannelsFile = new File(stream1Dir, "Stream_Channels.txt");
        try (PrintWriter writer = new PrintWriter(streamChannelsFile)) {
            for (StreamKey sc : staChansToUse) {
                writer.print(sc.getAgency() != null ? sc.getAgency() + " " : "");
                writer.print(sc.getNet() != null ? sc.getNet() + " " : "");
                writer.print(sc.getSta() != null ? sc.getSta() + " " : "");
                writer.print(sc.getChan() != null ? sc.getChan() + " " : "");
                writer.print(sc.getLocationCode() != null ? sc.getLocationCode() + " " : "");
                writer.print(sep);
            }
            return streamChannelsFile;
        }

    }

    private File createStreamFile(File bootDetectorFile, File streamChannelFile, File templatesFile) throws FileNotFoundException {
        File streamFile = new File(stream1Dir, "STREAM1.txt");
        try (PrintWriter writer = new PrintWriter(streamFile)) {
            writer.print(String.format("StreamName = STREAM1%s", sep));
            writer.print(String.format("%s", sep));

            writer.print(String.format("%s", sep));
            writer.print(String.format("Passband             = %f %f%s", minFrequency, maxFrequency, sep));
            writer.print(String.format("PreprocessorFilterOrder = 4%s", sep));
            writer.print(String.format("# The BlockSizeSeconds is the length in seconds of the detection statistic it must be longer than the maximum template length. %s", sep));
            writer.print(String.format("# If BlockSizeSeconds is set to -1, then the code will compute a blocksize = 1.5 * max template length. %s", sep));
            writer.print(String.format("BlockSizeSeconds = %f%s", blockSizeSeconds, sep));
            writer.print(
                    String.format(
                            "# The decimation rate controls the down-sampling of the data. DecimationRate * Fmax must be less than 1/2 the data sample rate and must be an even divisor of (int)(BlockSize * sample rate). %s",
                            sep));
            writer.print(String.format("# If DecimationRate is set to -1, then the code will compute the max possible decimation rate. %s", sep));
            writer.print(String.format("DecimationRate = %d%s", decimationRate, sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("# New subspace detectors created from detections  will be created using these parameters%s", sep));
            writer.print(String.format("SubspaceThresholdValue   = %f%s", ssThresh, sep));
            writer.print(String.format("SubspaceBlackoutPeriod   = 3.0%s", sep));
            writer.print(String.format("EnergyCaptureThreshold   = 0.9%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("# If UseConfigFileThreshold is true then retrieved subspace detectors will use threshold in this file.%s", sep));
            writer.print(String.format("UseConfigFileThreshold   = true%s%s", sep, sep));
            writer.print(String.format("# If UseDynamicThresholds is true then subspace detection thresholds will be periodically revised based on detection statistics.%s", sep));
            writer.print(String.format("UseDynamicThresholds   = true%s%s", sep, sep));
            writer.print(String.format("# StatsRefreshIntervalInBlocks is the number of blocks processed between statistics refreshes.%s", sep));
            writer.print(String.format("StatsRefreshIntervalInBlocks   = 1000%s%s", sep, sep));

            writer.print(
                    String.format(
                            "# To specify detectors using user-supplied templates use a line like the following where the path is the name of the file with the specification file list..%s",
                            sep));
            writer.print(String.format("#DetectorSpecificationFileList = %s%s%s", templatesFile.getAbsoluteFile(), sep, sep));

            writer.print(String.format("\nSpawnCorrelationDetectors = %s%s", spawnCorrelationDetectors ? "true" : "false", sep));
            writer.print(String.format("# If next line is true, then although spawners will run, they will not produce triggers.%s", sep));
            writer.print(String.format("TriggerOnlyOnCorrelators = false%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("LoadCorrelatorsFromDb = true%s", sep));
            writer.print(String.format("%s", sep));

            writer.print(String.format("%s", sep));
            writer.print(
                    String.format(
                            "# If next line is true, then any bulletin detectors in the specified STREAMID are replaced with new bulletin detectors defined in the current config files.%s",
                            sep));
            writer.print(String.format("replaceBulletinDetector = false%s", sep));
            writer.print(String.format("targetBulletinDetectorStreamid   = -1%s%s", sep, sep));

            writer.print(String.format("# SNR Trigger Screening----------------------------%s", sep));
            writer.print(String.format("# Lengths are in seconds%s", sep));
            writer.print(String.format("SnrThreshold = %f%s", snrThreshold, sep));
            writer.print(String.format("# Duration Trigger screening ----------------------%s", sep));
            writer.print(String.format("# events must be at least this long in seconds to be a valid trigger%s", sep));
            writer.print(String.format("MinEventDuration = %f%s", minEventDuration, sep));
            writer.print(String.format("%s%s", sep, sep));

            writer.print(String.format("# Other Trigger screening ----------------------%s", sep));

            FKScreenParams.writeDefaultConfigInfo(writer, bootDetectorType, sep);

            writer.print(String.format("%s", sep));
            writer.print(String.format("# The framework can be run with detectors installed and producing detection statistics but with no triggers produced.%s", sep));
            writer.print(String.format("# Default is to produce triggers.%s", sep));
            writer.print(String.format("ProduceTriggers = true%s", sep));
            writer.print(String.format("%s", sep));
            String tmp = streamChannelFile.getAbsolutePath();
            writer.print(String.format("StreamChannelFile = %s%s", tmp, sep));
            tmp = bootDetectorFile.getAbsolutePath();
            writer.print(String.format("BootDetectorsFile = %s%s%s", tmp, sep, sep));

            return streamFile;
        }
    }

    private File createStreamsTxtFile(File streamFile) throws FileNotFoundException {
        File streamsFile = new File(configDirectory, "streams.txt");
        try (PrintWriter writer = new PrintWriter(streamsFile)) {
            String tmp = streamFile.getAbsolutePath();
            writer.print(String.format("%s%s", tmp.replace("\\", "\\\\"), sep));
            return streamsFile;
        }
    }

    private File createDetStatDir() {
        File file = new File(configDirectory, "detectionStatistics");
        if (!file.mkdirs()) {
            throw new IllegalStateException("Failed creating directory: " + file.getAbsolutePath());
        }
        return file;
    }

    private File createRawTraceDir() {
        File file = new File(configDirectory, "rawTraces");
        if (!file.mkdirs()) {
            throw new IllegalStateException("Failed creating directory: " + file.getAbsolutePath());
        }
        return file;
    }

    private File createRawTraceSpecFile() throws FileNotFoundException {
        File file = new File(configDirectory, "rawTraces.txt");
        try (PrintWriter writer = new PrintWriter(file)) {
            for (StreamKey sc : staChansToUse) {
                writer.print(sc.getAgency() != null ? sc.getAgency() + " " : "");
                writer.print(sc.getNet() != null ? sc.getNet() + " " : "");
                writer.print(sc.getSta() != null ? sc.getSta() + " " : "");
                writer.print(sc.getChan() != null ? sc.getChan() + " " : "");
                writer.print(sc.getLocationCode() != null ? sc.getLocationCode() + " " : "");
                writer.print(sep);
            }
            return file;
        }

    }

    private File createModifiedTraceDir() {
        File file = new File(configDirectory, "modifiedTraces");
        if (!file.mkdirs()) {
            throw new IllegalStateException("Failed creating directory: " + file.getAbsolutePath());
        }
        return file;
    }

    private File createModifiedTraceSpecFile() throws FileNotFoundException {
        File file = new File(configDirectory, "modifiedTraces.txt");
        try (PrintWriter writer = new PrintWriter(file)) {
            for (StreamKey sc : staChansToUse) {
                writer.print(sc.getAgency() != null ? sc.getAgency() + " " : "");
                writer.print(sc.getNet() != null ? sc.getNet() + " " : "");
                writer.print(sc.getSta() != null ? sc.getSta() + " " : "");
                writer.print(sc.getChan() != null ? sc.getChan() + " " : "");
                writer.print(sc.getLocationCode() != null ? sc.getLocationCode() + " " : "");
                writer.print(sep);
            }
            return file;
        }

    }

    private void writeConfigFile(File streamsFile, File detStatDirFile, File rawTracesDirFile, File rawTraceSpecFile, File modifiedTracesDirFile, File modifiedTraceSpecFile)
            throws FileNotFoundException {
        File file = new File(configDirectory, "config.txt");
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(String.format("#This is a comment%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("config_name = %s %s", configName, sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("#When this is specified any existing configuration of the above name will be deleted. Then a new configuration will be created.%s", sep));
            writer.print(String.format("#When not specified, the named configuration must exist or an error will occur.%s", sep));
            writer.print(String.format("CreateConfiguration=true%s", sep));
            writer.print(String.format("#===============================================================================================================================%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("# These optional parameters force use of a subset of the available data.%s", sep));
            writer.print(String.format("MinJdateToProcess = %d%s", minDate, sep));
            writer.print(String.format("MaxJdateToProcess = %d%s", maxDate, sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("# Templates are constrained to be between MinTemplateLength and MaxTemplateLength.%s", sep));
            writer.print(String.format("MinTemplateLength =  %f%s", minTemplateLength, sep));
            writer.print(String.format("MaxTemplateLength =  %f%s", maxTemplateLength, sep));
            writer.print(String.format("%s", sep));

            writer.print(String.format("# To prevent adaptation of template lengths to detected signals set the following to true.%s", sep));
            writer.print(String.format("ForceFixedTemplateLengths = false%s", sep));

            SeismogramSourceInfo seismogramSourceInfo = ConfigCreatorParameters.getInstance().getSeismogramSourceInfo();
            writer.print(String.format("# The framework supports seismogram source type of CssDatabase, Type2Database, FDSN). One of these should be specified on the next line.%s", sep));
            writer.println(String.format("SourceType = %s", seismogramSourceInfo.getSourceType()));
            writer.println(
                    String.format(
                            "# You must also specify the SourceIdentifier. When source_type is CssDatabase this is the name of the continuous_wfdisc table. \n# When source_type is FDSN this is the agency string. When source_type is Type2Database this field is ignored."));
            writer.println(String.format("SourceIdentifier = %s", seismogramSourceInfo.getSourceIdentifier()));
            writer.print(String.format("%s", sep));

            writer.print(String.format("# The streams file contains pointers to one or more streams that make up this configuration.%s", sep));
            String tmp = streamsFile.getAbsolutePath();
            writer.print(String.format("StreamsFile = %s%s", tmp, sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("# By default, all processing is done in a single thread. However, if multiple cores are available, it may be advantageous%s", sep));
            writer.print(String.format("# to use more than one thread. In some cases, having more threads than processors may still gain a performance increase.%s", sep));
            writer.print(String.format("# For example, if processes are waiting on I/O for a significant amount of time it may help to run another unblocked process%s", sep));
            writer.print(String.format("# on that thread. The code is parallelized on the calculate detection statistic tasks.%s", sep));
            writer.print(String.format("NumberOfThreads = %d%s", numThreads, sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("# The framework can be set to only load detectors that were created during a specific run. This does not apply to 'boot'%s", sep));
            writer.print(String.format("# detectors. If the specified RUNID was not executed under the current configuration then no detectors are loaded.%s", sep));
            writer.print(String.format("# This can be used as a mechanism to suppress loading of previously-created subspace and matched field detectors.%s", sep));
            writer.print(String.format("# To restrict detectors in this fashion, set LoadOnlyDetectorsFromSpecifiedRunid = true and then%s", sep));
            writer.print(String.format("# set RunidForDetectorRetrieval = desired runid (something like -1 will result in no detectors being loaded)%s", sep));
            writer.print(String.format("LoadOnlyDetectorsFromSpecifiedRunid = false%s", sep));
            writer.print(String.format("RunidForDetectorRetrieval = -1%s", sep));
            writer.print(String.format("%s", sep));

            writer.print(String.format("%s", sep));
            writer.print(String.format("# The next set of parameters control detector re-windowing. This process attempts to adjust the detector template windows%s", sep));
            writer.print(String.format("# to most closely align with the signal most coherent across all detections for each detector.%s", sep));
            writer.print(String.format("# When operative, newly-created detectors are subject to re-windowing when their detection count reaches RewindowDetectionCountThreshold.%s", sep));
            writer.print(String.format("# During re-windowing a moving window SVD is used to produce an energy capture function. That function is then evalued to%s", sep));
            writer.print(String.format("# estimate the onset and duration of coherent signal. These values are used to produce a new template.%s", sep));
            writer.print(String.format("RewindowDetectors = true%s", sep));
            writer.print(String.format("# The RewindowDetectionCountThreshold is the number of detections required before re-windowing occurs. The greater the value the more stable%s", sep));
            writer.print(String.format("# the computation. However compute time varies as the square of this number.%s", sep));
            writer.print(String.format("RewindowDetectionCountThreshold = 8%s", sep));
            writer.print(
                    String.format(
                            "# The RewindowSlidingWindowLengthSeconds value sets the length of the sliding window. Small values increase the resolution at the expense of computation time.%s",
                            sep));
            writer.print(String.format("RewindowSlidingWindowLengthSeconds = 2%s", sep));
            writer.print(
                    String.format("# The algorithm uses an analysis window which should be long enough to include the entirety of the desired signals as well as the offsets likely to occur.%s", sep));
            writer.print(
                    String.format("# Computation time is linearly related to changes in the analysis window length. The analysis window starts RewindowPreTriggerSeconds before the trigger%s", sep));
            writer.print(String.format("# times and extends RewindowAnalysisWindowLengthSeconds  past that point.%s", sep));
            writer.print(String.format("RewindowPreTriggerSeconds = 50%s", sep));
            writer.print(String.format("RewindowAnalysisWindowLengthSeconds = 400%s", sep));

            writer.print(String.format("# As a sanity check on the algorithm's results, the user can specify minimum and maximum values for the generated template window lengths.%s", sep));
            writer.print(String.format("ReWindowMinWindowLengthSeconds = 20%s", sep));
            writer.print(String.format("ReWindowMaxWindowLengthSeconds = 150%s", sep));

            writer.print(String.format("%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("writeDetectionStatistics = false%s", sep));
            tmp = detStatDirFile.getAbsolutePath();
            writer.print(String.format("DetectionStatisticDirectory = %s%s", tmp, sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("WriteRawTraces = false%s", sep));
            tmp = rawTracesDirFile.getAbsolutePath();
            writer.print(String.format("RawTraceDirectory = %s%s", tmp, sep));
            tmp = rawTraceSpecFile.getAbsolutePath();
            writer.print(String.format("RawTraceChannelFile = %s%s", tmp, sep));

            writer.print(String.format("%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("WriteModifiedTraces = false%s", sep));
            tmp = modifiedTracesDirFile.getAbsolutePath();
            writer.print(String.format("ModifiedTraceDirectory = %s%s", tmp, sep));
            tmp = modifiedTraceSpecFile.getAbsolutePath();
            writer.print(String.format("ModifiedTraceChannelFile = %s%s", tmp, sep));
        }
    }
}
