package llnl.gnem.apps.detection.util.initialization;

import llnl.gnem.apps.detection.core.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import llnl.gnem.apps.detection.core.dataObjects.ChannelSubstitution;
import llnl.gnem.apps.detection.database.ChannelSubstitutionDAO;
import llnl.gnem.core.util.FileInputArrayLoader;
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
    private double fixedRawSampleRate;
    private Map<String, ChannelSubstitution> substitutionMap;

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

    /**
     * @return the minJdateToProcess
     */
    public int getMinJdateToProcess() {
        return minJdateToProcess;
    }

    /**
     * @return the maxJdateToProcess
     */
    public int getMaxJdateToProcess() {
        return maxJdateToProcess;
    }

    public boolean isCreateBootDetectorsIfNeeded() {
        System.out.println("fill  in code for setting whether boot detectors are auto-created (ProcessingPrescription)!");
        return true;
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
                String[] tokens = line.split("\\s+");
                if (tokens.length == 2) {
                    channels.add(new StreamKey(tokens[0], tokens[1]));
                }
            }
        }
    }

    private void getStreamInfo(String streamsFileName) throws Exception {
        String[] filenames = FileInputArrayLoader.fillStrings(streamsFileName);
        Map<String, StreamInfo> streams = new HashMap<>();
        for (String file : filenames) {
            StreamInfo info = new StreamInfo(file);
            streams.put(info.getStreamName(), info);
        }
        StreamsConfig.getInstance().populateMap(streams);
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

    /**
     * @return the configFileDirectory
     */
    public File getConfigFileDirectory() {
        return configFileDirectory;
    }

    public String getConfigFileName() {
        return parfile;
    }

    /**
     * @return the configFileBytes
     */
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

    private void getProcessedTraceSpecs(Properties propertyList) throws IOException {
        writeModifiedTraces = Boolean.parseBoolean(propertyList.getProperty(WRITE_MODIFIED_TRACES_PROP).trim());

        if (writeModifiedTraces) {
            String deimatedTraceChannelFile = propertyList.getProperty(MODIFIED_TRACE_CHANNEL_FILE_PROP);
            if (deimatedTraceChannelFile == null || deimatedTraceChannelFile.equals(SET_IF_REQUIRED_VALUE)) {
                throw new IllegalStateException("No decimated trace channel file was specified, but writeDecimatedTraces is true!");
            }
            String[] lines = FileInputArrayLoader.fillStrings(deimatedTraceChannelFile);
            for (String line : lines) {
                String[] tokens = line.split("\\s+");
                if (tokens.length == 2) {
                    modifiedChannels.add(new StreamKey(tokens[0], tokens[1]));
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
        propertyList.setProperty(CONFIG_NAME_PROP, MUST_BE_SET);
        propertyList.setProperty(MIN_TEMPLATE_LENGTH_PROP, "10");
        propertyList.setProperty(FORCE_FIXED_TEMPLATE_LENGTHS_PROP, "false");
        propertyList.setProperty(FIXED_RAW_SAMPLERATE, "-1");

    }

    public void initialize(String parfile) throws Exception {
        this.parfile = parfile;
        configFileBytes = FileUtil.readBytesFromFile(parfile);
        try (FileInputStream infile = new FileInputStream(parfile)) {
            propertyList.load(infile);

            File file = new File(parfile);
            configFileDirectory = file.getAbsoluteFile().getParentFile();
            if (configFileDirectory == null) {
                file = new File(".");
                configFileDirectory = file.getAbsoluteFile();
            }
        }

        retrieveConfigName(propertyList);
        numberOfThreads = Integer.parseInt(propertyList.getProperty(NUMBER_OF_THREADS_PROP).trim());

        writeDetectionStatistics = Boolean.parseBoolean(propertyList.getProperty(WRITE_DETECTION_STATISTICS_PROP).trim());
        String tmp = propertyList.getProperty(DETECTION_STATISTIC_DIRECTORY_PROP);
        if (tmp == null || tmp.trim().isEmpty() || tmp.equals(SET_IF_REQUIRED_VALUE)) {
            tmp = new File(".").getAbsolutePath();
        }
        detectionStatisticDirectory = new File(tmp);

        tmp = propertyList.getProperty(RAW_TRACE_DIRECTORY_PROP);
        if (tmp == null || tmp.trim().isEmpty() || tmp.equals(SET_IF_REQUIRED_VALUE)) {
            tmp = new File(".").getAbsolutePath();
        }
        rawTraceDirectory = new File(tmp);

        tmp = propertyList.getProperty(MODIFIED_TRACE_DIRECTORY_PROP);
        if (tmp == null || tmp.trim().isEmpty() || tmp.equals(SET_IF_REQUIRED_VALUE)) {
            tmp = new File(".").getAbsolutePath();
        }
        modifiedTraceDirectory = new File(tmp);

        maxTemplateLength = Double.parseDouble(propertyList.getProperty(MAX_TEMPLATE_LENGTH_PROP).trim());
        loadOnlyDetectorsFromSpecifiedRunid = Boolean.parseBoolean(propertyList.getProperty(LOAD_ONLY_DETECTORS_FROM_SPECIFIED_RUNID_PROP).trim());
        runidForDetectorRetrieval = Integer.parseInt(propertyList.getProperty(RUNID_FOR_DETECTOR_RETRIEVAL_PROP).trim());

        minJdateToProcess = Integer.parseInt(propertyList.getProperty(MIN_JDATE_TO_PROCESS_PROP).trim());
        maxJdateToProcess = Integer.parseInt(propertyList.getProperty(MAX_JDATE_TO_PROCESS_PROP).trim());
        tmp = propertyList.getProperty(STREAMS_FILE_PROP).trim();
        if (tmp != null) {
            getStreamInfo(tmp);
        }

        fixedRawSampleRate = Double.parseDouble(propertyList.getProperty(FIXED_RAW_SAMPLERATE).trim());

        createConfiguration = Boolean.parseBoolean(propertyList.getProperty(CREATE_CONFIGURATION_PROP).trim());
        getRawTraceSpecs(propertyList);
        getProcessedTraceSpecs(propertyList);
        minTemplateLength = Double.parseDouble(propertyList.getProperty(MIN_TEMPLATE_LENGTH_PROP).trim());
        forceFixedTemplateLengths = Boolean.parseBoolean(propertyList.getProperty(FORCE_FIXED_TEMPLATE_LENGTHS_PROP).trim());

        String channelSubstitutionFile = propertyList.getProperty("ChannelSubstitutionFile");
        if (channelSubstitutionFile != null) {
            substitutionMap = ChannelSubstitutionDAO.getInstance().getChannelSubstitutions(channelSubstitutionFile);
        } else {
            substitutionMap = new HashMap<>();
        }

    }

    private void retrieveConfigName(Properties propertyList) {
        configName = propertyList.getProperty(CONFIG_NAME_PROP).trim();
        if (configName.isEmpty() || configName.equals(MUST_BE_SET)) {
            throw new IllegalStateException("No Valid ConfigurationName was set!");
        }
    }

    /**
     * @return the fixedRawSampleRate
     */
    public double getFixedRawSampleRate() {
        return fixedRawSampleRate;
    }

    public boolean isFixRawSampleRate() {
        return fixedRawSampleRate > 0;
    }

    /**
     * @return the substitutionMap
     */
    public Map<String, ChannelSubstitution> getSubstitutionMap() {
        return substitutionMap;
    }

}
