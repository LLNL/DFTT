/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util.configuration;

import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
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

    public ConfigFileWriter(String refSta,
            String configName,
            File configDirectory,
            Integer minDate,
            Integer maxDate,
            Collection<StreamKey> staChansToUse,
            double minTemplateLength,
            double maxTemplateLength,
            double minFrequency,
            double maxFrequency,
            double ssThresh,
            double staLtaThresh,
            int numThreads,
            DetectorType bootDetectorType,
            Double beamAzimuth,
            Double beamVelocity,
            File bulletinFile,
            double snrThreshold,
            double minEventDuration,
            double blockSizeSeconds,
            int decimationRate,
            boolean spawnCorrelationDetectors) throws IOException {
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
        File streamFile = createStream1();
        File streamsFile = createStreamsTxtFile(streamFile);
        File detStatDirFile = createDetStatDir();
        File rawTracesDirFile = createRawTraceDir();
        File rawTraceSpecFile = createRawTraceSpecFile();
        File modifiedTracesDirFile = createModifiedTraceDir();
        File modifiedTraceSpecFile = createModifiedTraceSpecFile();
        File channelSubstitutionFile = createChannelSubstitutionTxtFile();
        writeConfigFile(streamsFile, detStatDirFile, rawTracesDirFile, rawTraceSpecFile, modifiedTracesDirFile, modifiedTraceSpecFile, channelSubstitutionFile);
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
        File bootDetectorFile = new BootDetectorCreator(stream1Dir, refSta,
                staLtaThresh,
                staChansToUse,
                bootDetectorType,
                beamAzimuth,
                beamVelocity).createBootDetectors(bulletinFile);

        ExampleTemplateCreator etc = new ExampleTemplateCreator(stream1Dir, staChansToUse);
        Collection<File> templates = etc.makeTemplateDirectories();
        File templatesFile = makeTemplatesFile(templates);

        File streamChannelFile = createStreamChannelFile();

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
                writer.print(String.format("%s	%s %s", sc.getSta(), sc.getChan(), sep));
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
            writer.print(String.format("# The BlockSizeSeconds is the length in seconds of the detection statistic it must be longer than the maximum template length. %s", sep));
            writer.print(String.format("# If BlockSizeSeconds is set to -1, then the code will compute a blocksize = 1.5 * max template length. %s", sep));
            writer.print(String.format("BlockSizeSeconds = %f%s", blockSizeSeconds, sep));
            writer.print(String.format("# The decimation rate controls the down-sampling of the data. DecimationRate * Fmax must be less than 1/2 the data sample rate and must be an even divisor of (int)(BlockSize * sample rate). %s", sep));
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

            writer.print(String.format("# To specify detectors using user-supplied templates use a line like the following where the path is the name of the file with the specification file list..%s", sep));
            writer.print(String.format("#DetectorSpecificationFileList = %s%s%s", templatesFile.getAbsoluteFile(), sep, sep));

            ExampleCancellationTemplateCreator ectc = new ExampleCancellationTemplateCreator(stream1Dir, staChansToUse);
            File templateDir = ectc.makeTemplateDirectory();
            String paramFileName = "cancellation_params.txt";
            File cancelFile = ectc.makeTemplateDescriptorFile(templateDir, paramFileName, minFrequency, maxFrequency);
            writer.print(String.format("ApplyStreamCancellation = false%s", sep));
            writer.print(String.format("CancellorParamsFile = %s%s", cancelFile.getAbsolutePath(), sep));
            writer.print(String.format("CancellationTemplateSource = SUBSPACE_TEMPLATES\n"));
            File detidFile = new File(configDirectory, "detectorids.txt");
            writer.print(String.format("CancellationTemplateDetectoridFile = %s", detidFile.getAbsolutePath(), sep));

            writer.print(String.format("\nSpawnCorrelationDetectors = %s%s", spawnCorrelationDetectors ? "true" : "false", sep));
            writer.print(String.format("# If next line is true, then although spawners will run, they will not produce triggers.%s", sep));
            writer.print(String.format("TriggerOnlyOnCorrelators = false%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("LoadCorrelatorsFromDb = true%s", sep));
            writer.print(String.format("%s", sep));

            writer.print(String.format("# SNR Trigger Screening----------------------------%s", sep));
            writer.print(String.format("# Lengths are in seconds%s", sep));
            writer.print(String.format("SnrThreshold = %f%s", snrThreshold, sep));
            writer.print(String.format("# Duration Trigger screening ----------------------%s", sep));
            writer.print(String.format("# events must be at least this long in seconds to be a valid trigger%s", sep));
            writer.print(String.format("MinEventDuration = %f%s", minEventDuration, sep));
            writer.print(String.format("%s%s", sep, sep));

            writer.print(String.format("# Other Trigger screening ----------------------%s", sep));

            FKScreenParams.writeDefaultConfigInfo(writer, sep);

            writer.print(String.format("%s", sep));
            writer.print(String.format("# The framework can be run with detectors installed and producing detection statistics but with no triggers produced.%s", sep));
            writer.print(String.format("# Default is to produce triggers.%s", sep));
            writer.print(String.format("ProduceTriggers = true%s", sep));
            writer.print(String.format("%s", sep));
            String tmp = streamChannelFile.getAbsolutePath();
            writer.print(String.format("StreamChannelFile = %s%s", tmp.replace("\\", "\\\\"), sep));
            tmp = bootDetectorFile.getAbsolutePath();
            writer.print(String.format("BootDetectorsFile = %s%s%s", tmp.replace("\\", "\\\\"), sep, sep));

            return streamFile;
        }
    }

    private File createChannelSubstitutionTxtFile() throws FileNotFoundException {
        File streamsFile = new File(configDirectory, "channelSubstitution.txt");
        try (PrintWriter writer = new PrintWriter(streamsFile)) {
            writer.print("SHZ BHZ\n");
            writer.print("SHZ HHZ\n");
            writer.print("SHE BHE\n");
            writer.print("SHN BHN\n");
            return streamsFile;
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
                writer.print(String.format("%s	%s %s", sc.getSta(), sc.getChan(), sep));
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
                writer.print(String.format("%s	%s %s", sc.getSta(), sc.getChan(), sep));
            }
            return file;
        }

    }

    private void writeConfigFile(File streamsFile, File detStatDirFile, File rawTracesDirFile, File rawTraceSpecFile,
            File modifiedTracesDirFile, File modifiedTraceSpecFile, File channelSubstitutionFile) throws FileNotFoundException {
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

            writer.print(String.format("%s", sep));
            writer.println(String.format("#Stations operated for very long time periods may have instrument changes that result in different sample rates; e.g. BHZ may be 20Hz initially and change to 40 Hz later.", sep));
            writer.println(String.format("#In these cases, the raw sample rate must be fixed for correct operation. To accomplish this, set the raw rate as shown below.", sep));
            writer.println(String.format("#SetRawSampRateTo = 20", sep));
            writer.print(String.format("%s", sep));

            writer.print(String.format("%s", sep));
            String tmp = channelSubstitutionFile.getAbsolutePath();
            writer.println(String.format("#Stations operated for very long time periods may have instrument changes that result in different channel codes; e.g. SHZ may be replaced by BHZ.", sep));
            writer.println(String.format("#In these cases, the framework can substitute one channel for another: e.g. if SHZ is requested but not found, use BHZ instead. To accomplish this, specify a substitution file as shown below.", sep));
            writer.println(String.format("#ChannelSubstitutionFile = %s%s", tmp.replace("\\", "\\\\"), sep));
            writer.print(String.format("%s", sep));

            writer.print(String.format("# The streams file contains pointers to one or more streams that make up this configuration.%s", sep));
            tmp = streamsFile.getAbsolutePath();
            writer.print(String.format("StreamsFile = %s%s", tmp.replace("\\", "\\\\"), sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("# By default, all processing is done in a single thread. However, if multiple cores are available, it may be advantageous%s", sep));
            writer.print(String.format("# to use more than one thread. In some cases, having more threads than processors may still gain a performance increase.%s", sep));
            writer.print(String.format("# For example, if processes are waiting on I/O for a significant amount of time it may help to run another unblocked process%s", sep));
            writer.print(String.format("# on that thread. The code is parallelized on the stream group processing tasks and on the calculate detection statistic tasks.%s", sep));
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
            writer.print(String.format("%s", sep));
            writer.print(String.format("writeDetectionStatistics = false%s", sep));
            tmp = detStatDirFile.getAbsolutePath();
            writer.print(String.format("DetectionStatisticDirectory = %s%s", tmp.replace("\\", "\\\\"), sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("WriteRawTraces = false%s", sep));
            tmp = rawTracesDirFile.getAbsolutePath();
            writer.print(String.format("RawTraceDirectory = %s%s", tmp.replace("\\", "\\\\"), sep));
            tmp = rawTraceSpecFile.getAbsolutePath();
            writer.print(String.format("RawTraceChannelFile = %s%s", tmp.replace("\\", "\\\\"), sep));

            writer.print(String.format("%s", sep));
            writer.print(String.format("%s", sep));
            writer.print(String.format("WriteModifiedTraces = false%s", sep));
            tmp = modifiedTracesDirFile.getAbsolutePath();
            writer.print(String.format("ModifiedTraceDirectory = %s%s", tmp.replace("\\", "\\\\"), sep));
            tmp = modifiedTraceSpecFile.getAbsolutePath();
            writer.print(String.format("ModifiedTraceChannelFile = %s%s", tmp.replace("\\", "\\\\"), sep));
        }
    }
}
