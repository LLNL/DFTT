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
package llnl.gnem.apps.detection.util.initialization;

import llnl.gnem.apps.detection.core.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import llnl.gnem.apps.detection.dataAccess.Util;
import llnl.gnem.core.dataAccess.SeismogramSourceInfo;

import llnl.gnem.core.util.FileInputArrayLoader;
import llnl.gnem.core.util.FileUtil.DriveMapper;
import llnl.gnem.core.util.StreamKey;

/**
 * Created by dodge1 Date: Sep 30, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ProcessingPrescription {

    private static final String SET_IF_REQUIRED_VALUE = "SetIfRequired";
    private static final String MUST_BE_SET = "MustBeSet";
    private static final String NUMBER_OF_THREADS_PROP = "NumberOfThreads";
    private static final String RAW_TRACE_CHANNEL_FILE_PROP = "RawTraceChannelFile";
    private static final String MODIFIED_TRACE_CHANNEL_FILE_PROP = "ModifiedTraceChannelFile";
    private static final String WRITE_MODIFIED_TRACES_PROP = "WriteModifiedTraces";
    private static final String WRITE_DETECTION_STATISTICS_PROP = "writeDetectionStatistics";
    private static final String WRITE_RAW_TRACES_PROP = "WriteRawTraces";
    private static final String CONFIG_NAME_PROP = "config_name";
    private static final String CREATE_CONFIGURATION_PROP = "CreateConfiguration";
    private static final String DETECTION_STATISTIC_DIRECTORY_PROP = "DetectionStatisticDirectory";
    private static final String MAX_TEMPLATE_LENGTH_PROP = "MaxTemplateLength";
    private static final String LOAD_ONLY_DETECTORS_FROM_SPECIFIED_RUNID_PROP = "LoadOnlyDetectorsFromSpecifiedRunid";
    private static final String MAX_JDATE_TO_PROCESS_PROP = "MaxJdateToProcess";
    private static final String MIN_JDATE_TO_PROCESS_PROP = "MinJdateToProcess";
    private static final String RAW_TRACE_DIRECTORY_PROP = "RawTraceDirectory";
    private static final String MODIFIED_TRACE_DIRECTORY_PROP = "ModifiedTraceDirectory";
    private static final String RUNID_FOR_DETECTOR_RETRIEVAL_PROP = "RunidForDetectorRetrieval";
    private static final String STREAMS_FILE_PROP = "StreamsFile";
    private static final String MIN_TEMPLATE_LENGTH_PROP = "MinTemplateLength";
    private static final String FORCE_FIXED_TEMPLATE_LENGTHS_PROP = "ForceFixedTemplateLengths";
    private static final String FIXED_RAW_SAMPLERATE = "SetRawSampRateTo";
    private static final String REPLACE_BULLETIN_DETECTOR_PROP = "ReplaceBulletinDetector";
    private static final String TARGET_BULLETIN_DETECTOR_STREAMID_PROP = "targetBulletinDetectorStreamid";
    private static final String SOURCE_IDENTIFIER = "SourceIdentifier";
    private static final String SOURCE_TYPE = "SourceType";
    private static final String RE_WINDOW_DETECTORS = "RewindowDetectors";
    private static final String RE_WINDOW_DETECTION_COUNT_THRESHOLD = "RewindowDetectionCountThreshold";
    private static final String RE_WINDOW_SLIDING_WINDOW_LENGTH_SECONDS = "RewindowSlidingWindowLengthSeconds";
    private static final String RE_WINDOW_PRE_TRIGGER_SECONDS = "RewindowPreTriggerSeconds";
    private static final String RE_WINDOW_MIN_WINDOW_LENGTH_SECONDS = "ReWindowMinWindowLengthSeconds";
    private static final String RE_WINDOW_MAX_WINDOW_LENGTH_SECONDS = "ReWindowMaxWindowLengthSeconds";
    private static final String RE_WINDOW_ANALYSIS_WINDOW_LENGTH_SECONDS = "RewindowAnalysisWindowLengthSeconds";

    private final Properties propertyList;
    private String configName;
    private boolean writeDetectionStatistics;
    private File detectionStatisticDirectory;
    private int numberOfThreads;
    private String parfile;
    private boolean loadOnlyDetectorsFromSpecifiedRunid;
    private int runidForDetectorRetrieval;
    private int minJdateToProcess = -1;
    private int maxJdateToProcess = 2286324;
    private double maxTemplateLength;
    private boolean createConfiguration = false;
    private boolean writeRawTraces;
    private File rawTraceDirectory;
    private final Collection<StreamKey> channels = new ArrayList<>();
    private final Collection<StreamKey> modifiedChannels = new ArrayList<>();
    private File configFileDirectory;
    private byte[] configFileBytes;
    private double minTemplateLength;
    private boolean forceFixedTemplateLengths;
    private boolean writeModifiedTraces;
    private File modifiedTraceDirectory;

    private boolean replaceBulletinDetector = false;
    private int targetBulletinDetectorStreamid = -1;
    private SeismogramSourceInfo sourceInfo = null;
    private boolean rewindowDetectors = false;
    private int rewindowDetectionCountThreshold = 10;
    private double rewindowSlidingWindowLengthSeconds = 10.0;
    private double rewindowPreTriggerSeconds = 50.0;
    private double reWindowMinWindowLengthSeconds = 20.0;
    private double reWindowMaxWindowLengthSeconds = 50.0;
    private double rewindowAnalysisWindowLengthSeconds = 400.0;

    private ProcessingPrescription() {
        propertyList = new Properties();
        propertyList.setProperty(WRITE_RAW_TRACES_PROP, "false");
        propertyList.setProperty(WRITE_MODIFIED_TRACES_PROP, "false");
        propertyList.setProperty(RAW_TRACE_CHANNEL_FILE_PROP, SET_IF_REQUIRED_VALUE);
        propertyList.setProperty(MODIFIED_TRACE_CHANNEL_FILE_PROP, SET_IF_REQUIRED_VALUE);

        propertyList.setProperty(NUMBER_OF_THREADS_PROP, "1");
        propertyList.setProperty(WRITE_DETECTION_STATISTICS_PROP, "true");
        propertyList.setProperty(DETECTION_STATISTIC_DIRECTORY_PROP, SET_IF_REQUIRED_VALUE);
        propertyList.setProperty(RAW_TRACE_DIRECTORY_PROP, SET_IF_REQUIRED_VALUE);
        propertyList.setProperty(MODIFIED_TRACE_DIRECTORY_PROP, SET_IF_REQUIRED_VALUE);

        propertyList.setProperty(MAX_TEMPLATE_LENGTH_PROP, "200");
        propertyList.setProperty(LOAD_ONLY_DETECTORS_FROM_SPECIFIED_RUNID_PROP, "false");
        propertyList.setProperty(RUNID_FOR_DETECTOR_RETRIEVAL_PROP, "-1");
        propertyList.setProperty(MIN_JDATE_TO_PROCESS_PROP, "-1");
        propertyList.setProperty(MAX_JDATE_TO_PROCESS_PROP, "2286324");
        propertyList.setProperty(STREAMS_FILE_PROP, MUST_BE_SET);
        propertyList.setProperty(CREATE_CONFIGURATION_PROP, "false");
        propertyList.setProperty(REPLACE_BULLETIN_DETECTOR_PROP, "false");
        propertyList.setProperty(CONFIG_NAME_PROP, MUST_BE_SET);
        propertyList.setProperty(MIN_TEMPLATE_LENGTH_PROP, "10");
        propertyList.setProperty(FORCE_FIXED_TEMPLATE_LENGTHS_PROP, "false");
        propertyList.setProperty(FIXED_RAW_SAMPLERATE, "-1");
        propertyList.setProperty(TARGET_BULLETIN_DETECTOR_STREAMID_PROP, "-1");
        propertyList.setProperty(SOURCE_TYPE, "CssDatabase");
        propertyList.setProperty(SOURCE_IDENTIFIER, "llnl.continuous_wfdisc");
        propertyList.setProperty(RE_WINDOW_DETECTORS, "false");
        propertyList.setProperty(RE_WINDOW_DETECTION_COUNT_THRESHOLD, "10");
        propertyList.setProperty(RE_WINDOW_SLIDING_WINDOW_LENGTH_SECONDS, "10");
        propertyList.setProperty(RE_WINDOW_PRE_TRIGGER_SECONDS, "50");
        propertyList.setProperty(RE_WINDOW_MIN_WINDOW_LENGTH_SECONDS, "20");
        propertyList.setProperty(RE_WINDOW_MAX_WINDOW_LENGTH_SECONDS, "50");
        propertyList.setProperty(RE_WINDOW_ANALYSIS_WINDOW_LENGTH_SECONDS, "400");
    }

    public void initialize(String parfile) throws Exception {
        this.parfile = parfile;
        configFileBytes = FileUtil.readBytesFromFile(parfile);
        String propertyFileContents = readPropertyFileContents(parfile);

        propertyList.load(new StringReader(propertyFileContents.replace("\\", "\\\\")));

        File file = new File(parfile);
        configFileDirectory = file.getAbsoluteFile().getParentFile();
        if (configFileDirectory == null) {
            file = new File(".");
            configFileDirectory = file.getAbsoluteFile();
        }

        retrieveConfigName(propertyList);
        numberOfThreads = Integer.parseInt(propertyList.getProperty(NUMBER_OF_THREADS_PROP).trim());

        writeDetectionStatistics = Boolean.parseBoolean(propertyList.getProperty(WRITE_DETECTION_STATISTICS_PROP).trim());
        String tmp = propertyList.getProperty(DETECTION_STATISTIC_DIRECTORY_PROP);
        if (tmp == null || tmp.trim().isEmpty() || tmp.equals(SET_IF_REQUIRED_VALUE)) {
            tmp = new File(".").getAbsolutePath();
        }
        String mapped = DriveMapper.getInstance().maybeMapPath(tmp);
        detectionStatisticDirectory = new File(mapped);

        tmp = propertyList.getProperty(RAW_TRACE_DIRECTORY_PROP);
        if (tmp == null || tmp.trim().isEmpty() || tmp.equals(SET_IF_REQUIRED_VALUE)) {
            tmp = new File(".").getAbsolutePath();
        }
        mapped = DriveMapper.getInstance().maybeMapPath(tmp);
        rawTraceDirectory = new File(mapped);

        tmp = propertyList.getProperty(MODIFIED_TRACE_DIRECTORY_PROP);
        if (tmp == null || tmp.trim().isEmpty() || tmp.equals(SET_IF_REQUIRED_VALUE)) {
            tmp = new File(".").getAbsolutePath();
        }
        mapped = DriveMapper.getInstance().maybeMapPath(tmp);
        modifiedTraceDirectory = new File(mapped);

        tmp = propertyList.getProperty(SOURCE_TYPE);
        SeismogramSourceInfo.SourceType type = SeismogramSourceInfo.SourceType.valueOf(tmp);
        tmp = propertyList.getProperty(SOURCE_IDENTIFIER);
        sourceInfo = new SeismogramSourceInfo(type, tmp);

        loadOnlyDetectorsFromSpecifiedRunid = Boolean.parseBoolean(propertyList.getProperty(LOAD_ONLY_DETECTORS_FROM_SPECIFIED_RUNID_PROP).trim());
        rewindowDetectors = Boolean.parseBoolean(propertyList.getProperty(RE_WINDOW_DETECTORS).trim());

        runidForDetectorRetrieval = Integer.parseInt(propertyList.getProperty(RUNID_FOR_DETECTOR_RETRIEVAL_PROP).trim());
        minJdateToProcess = Integer.parseInt(propertyList.getProperty(MIN_JDATE_TO_PROCESS_PROP).trim());
        maxJdateToProcess = Integer.parseInt(propertyList.getProperty(MAX_JDATE_TO_PROCESS_PROP).trim());
        rewindowDetectionCountThreshold = Integer.parseInt(propertyList.getProperty(RE_WINDOW_DETECTION_COUNT_THRESHOLD).trim());

        maxTemplateLength = Double.parseDouble(propertyList.getProperty(MAX_TEMPLATE_LENGTH_PROP).trim());
        rewindowSlidingWindowLengthSeconds = Double.parseDouble(propertyList.getProperty(RE_WINDOW_SLIDING_WINDOW_LENGTH_SECONDS).trim());
        rewindowPreTriggerSeconds = Double.parseDouble(propertyList.getProperty(RE_WINDOW_PRE_TRIGGER_SECONDS).trim());
        reWindowMinWindowLengthSeconds = Double.parseDouble(propertyList.getProperty(RE_WINDOW_MIN_WINDOW_LENGTH_SECONDS).trim());
        reWindowMaxWindowLengthSeconds = Double.parseDouble(propertyList.getProperty(RE_WINDOW_MAX_WINDOW_LENGTH_SECONDS).trim());
        rewindowAnalysisWindowLengthSeconds = Double.parseDouble(propertyList.getProperty(RE_WINDOW_ANALYSIS_WINDOW_LENGTH_SECONDS).trim());

        Util.populateArrayInfoModel(minJdateToProcess);

        tmp = propertyList.getProperty(STREAMS_FILE_PROP).trim();
        if (tmp != null) {
            getStreamInfo(DriveMapper.getInstance().maybeMapPath(tmp));
        }

        createConfiguration = Boolean.parseBoolean(propertyList.getProperty(CREATE_CONFIGURATION_PROP).trim());
        getRawTraceSpecs(propertyList);
        getProcessedTraceSpecs(propertyList);
        minTemplateLength = Double.parseDouble(propertyList.getProperty(MIN_TEMPLATE_LENGTH_PROP).trim());
        forceFixedTemplateLengths = Boolean.parseBoolean(propertyList.getProperty(FORCE_FIXED_TEMPLATE_LENGTHS_PROP).trim());

        replaceBulletinDetector = Boolean.parseBoolean(propertyList.getProperty(REPLACE_BULLETIN_DETECTOR_PROP).trim());
        targetBulletinDetectorStreamid = Integer.parseInt(propertyList.getProperty(TARGET_BULLETIN_DETECTOR_STREAMID_PROP).trim());

    }

    public double getMaxTemplateLength() {
        return maxTemplateLength;
    }

    public String getConfigName() {
        return configName;
    }

    public boolean isWriteDetectionStatistics() {
        return writeDetectionStatistics;
    }

    public File getDetectionStatisticDirectory() {
        return detectionStatisticDirectory;
    }

    public int getNumberOfThreads() {
        return numberOfThreads > 0 ? numberOfThreads : 1;
    }

    public String getParfile() {
        return parfile;
    }

    public boolean isLoadOnlyDetectorsFromSpecifiedRunid() {
        return loadOnlyDetectorsFromSpecifiedRunid;
    }

    public int getRunidForDetectorRetrieval() {
        return runidForDetectorRetrieval;
    }

    public int getMinJdateToProcess() {
        return minJdateToProcess;
    }

    public int getMaxJdateToProcess() {
        return maxJdateToProcess;
    }

    public boolean isCreateConfiguration() {
        return createConfiguration;
    }

    public boolean isWriteRawTraces() {
        return writeRawTraces;
    }

    public File getRawTraceDirectory() {
        return rawTraceDirectory;
    }

    public Collection<StreamKey> getRawTraceChannels() {
        return channels;
    }

    public File getConfigFileDirectory() {
        return configFileDirectory;
    }

    public String getConfigFileName() {
        return parfile;
    }

    public byte[] getConfigFileBytes() {
        return configFileBytes;
    }

    public double getMinTemplateLength() {
        return minTemplateLength;
    }

    public boolean isForceFixedTemplateLengths() {
        return forceFixedTemplateLengths;
    }

    public void validateStreamParams(double rate) {
        for (String name : StreamsConfig.getInstance().getStreamNames()) {
            StreamsConfig.getInstance().validateStreamParams(name, rate);
        }
    }

    public File getModifiedTraceDirectory() {
        return modifiedTraceDirectory;
    }

    public Collection<StreamKey> getModifiedTraceChannels() {
        return new ArrayList<>(modifiedChannels);
    }

    public boolean isWriteModifiedTraces() {
        return writeModifiedTraces;
    }

    public boolean isReplaceBulletinDetector() {
        return replaceBulletinDetector;
    }

    public int getTargetBulletinDetectorStreamid() {
        return targetBulletinDetectorStreamid;
    }

    public void setReplaceBulletinDetector(boolean value) {
        replaceBulletinDetector = value;
    }

    public void setTargetBulletinDetectorStreamid(int value) {
        targetBulletinDetectorStreamid = value;
    }

    public void setSeismogramSourceInfo(SeismogramSourceInfo ssi) {
        sourceInfo = ssi;
    }

    private String readPropertyFileContents(String parfile) throws IOException {
        return new String(Files.readAllBytes(Paths.get(parfile)));
    }

    public boolean isRewindowDetectors() {
        return rewindowDetectors;
    }

    public int getRewindowDetectionCountThreshold() {
        return rewindowDetectionCountThreshold;
    }

    public double getRewindowSlidingWindowLengthSeconds() {
        return rewindowSlidingWindowLengthSeconds;
    }

    public double getRewindowPreTriggerSeconds() {
        return rewindowPreTriggerSeconds;
    }

    public double getReWindowMinWindowLengthSeconds() {
        return reWindowMinWindowLengthSeconds;
    }

    public double getReWindowMaxWindowLengthSeconds() {
        return reWindowMaxWindowLengthSeconds;
    }

    public double getRewindowAnalysisWindowLengthSeconds() {
        return rewindowAnalysisWindowLengthSeconds;
    }

    public SeismogramSourceInfo getSeismogramSourceInfo() {
        if (sourceInfo == null) {
            return new SeismogramSourceInfo(SeismogramSourceInfo.SourceType.CssDatabase, "llnl.continuous_wfdisc");
        } else {
            return sourceInfo;
        }
    }

    private void getRawTraceSpecs(Properties propertyList) throws IOException, IllegalStateException {
        writeRawTraces = Boolean.parseBoolean(propertyList.getProperty(WRITE_RAW_TRACES_PROP).trim());

        if (writeRawTraces) {
            String rawTraceChannelFile = propertyList.getProperty(RAW_TRACE_CHANNEL_FILE_PROP);
            if (rawTraceChannelFile == null || rawTraceChannelFile.equals(SET_IF_REQUIRED_VALUE)) {
                throw new IllegalStateException("No raw trace channel file was specified, but writeRawTraces is true!");
            }
            String[] lines = FileInputArrayLoader.fillStrings(rawTraceChannelFile);
            for (String line : lines) {
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length == 2) { // Old-style sta-chan description
                    channels.add(new StreamKey(tokens[0], tokens[1]));
                } else if (tokens.length == 4) { //net-sta-chan-locid
                    channels.add(new StreamKey(tokens[0], tokens[1], tokens[2], tokens[3]));
                }
            }
        }
    }

    private void getStreamInfo(String streamsFileName) throws Exception {
        Path tmp = Paths.get(streamsFileName);
        List<String> lines = Files.readAllLines(tmp.normalize());
        Map<String, StreamInfo> streams = new HashMap<>();
        for (String file : lines) {
            StreamInfo info = new StreamInfo(DriveMapper.getInstance().maybeMapPath(file));
            streams.put(info.getStreamName(), info);
        }
        StreamsConfig.getInstance().populateMap(streams);
    }

    private void getProcessedTraceSpecs(Properties propertyList) throws IOException {
        writeModifiedTraces = Boolean.parseBoolean(propertyList.getProperty(WRITE_MODIFIED_TRACES_PROP).trim());

        if (writeModifiedTraces) {
            String deimatedTraceChannelFile = propertyList.getProperty(MODIFIED_TRACE_CHANNEL_FILE_PROP);
            if (deimatedTraceChannelFile == null || deimatedTraceChannelFile.equals(SET_IF_REQUIRED_VALUE)) {
                throw new IllegalStateException("No decimated trace channel file was specified, but writeDecimatedTraces is true!");
            }
            String[] lines = FileInputArrayLoader.fillStrings(deimatedTraceChannelFile);
            for (String line : lines) {
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length == 2) { // Old-style sta-chan description
                    modifiedChannels.add(new StreamKey(tokens[0], tokens[1]));
                } else if (tokens.length == 4) { //net-sta-chan-locid
                    modifiedChannels.add(new StreamKey(tokens[0], tokens[1], tokens[2], tokens[3]));
                }
            }
        }
    }

    private static class ProcessingPrescriptionHolder {

        private static final ProcessingPrescription INSTANCE = new ProcessingPrescription();
    }

    public static ProcessingPrescription getInstance() {
        return ProcessingPrescriptionHolder.INSTANCE;
    }

    private void retrieveConfigName(Properties propertyList) {
        configName = propertyList.getProperty(CONFIG_NAME_PROP).trim();
        if (configName.isEmpty() || configName.equals(MUST_BE_SET)) {
            throw new IllegalStateException("No Valid ConfigurationName was set!");
        }
    }

}
