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

import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenRange;
import llnl.gnem.apps.detection.core.dataObjects.SpecificationFactory;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayDetectorSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTASpecification;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;

import llnl.gnem.apps.detection.core.framework.detectors.bulletin.BulletinSpecification;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.FileInputArrayLoader;
import llnl.gnem.dftt.core.util.FileUtil.DriveMapper;
import llnl.gnem.dftt.core.util.PairT;
import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class StreamInfo {

    //--------------------------------------------
    // Wide-band processing properties...
    private final double passBandLowFrequency;
    private final double passBandHighFrequency;
    private final int preprocessorFilterOrder;
    private final double subspaceThresholdValue;
    private final double subspaceBlackoutPeriod;
    private final double energyCaptureThreshold;
    private final boolean spawnCorrelationDetectors;
    private final boolean produceTriggers;
    private final boolean loadCorrelatorsFromDb;
    private final String streamName;
    private final Collection<StreamKey> channels;
    private final BootDetectorParams bootDetectorParams;
    private final Collection<DetectorSpecification> detectorSpecifications;
    private final File configFileDir;
    private final String configFileName;
    private final double snrThreshold;
    private final double minEventDuration;
    private final boolean useConfigFileThreshold;
    private int decimationRate;
    private double blockSizeSeconds;

    private final FKScreenParams fKScreenParams;
    private final boolean triggerOnlyOnCorrelators;
    private int undecimatedBlockSize;
    private int decimatedBlockSize;

    private boolean useDynamicThresholds;
    private int statsRefreshIntervalInBlocks;
    private double minComputedThreshold;
    private double maxComputedThreshold;

    public Collection<StreamKey> getChannels() {
        return new ArrayList<>(channels);
    }

    public StreamInfo(String parfile) throws Exception {
        File aFile = new File(parfile);
        configFileName = aFile.getName();
        configFileDir = aFile.getParentFile();
        Properties propertyList = getPropertyList(parfile);
        detectorSpecifications = new ArrayList<>();
        bootDetectorParams = new BootDetectorParams();

        streamName = propertyList.getProperty("StreamName");
        if (streamName == null || streamName.isEmpty()) {
            throw new IllegalStateException("StreamName was not specified in " + parfile);
        }

        String streamChannelFile = propertyList.getProperty("StreamChannelFile");
        if (streamChannelFile == null || streamChannelFile.isEmpty()) {
            throw new IllegalStateException("StreamChannelFile was not specified for stream " + streamName);
        }

        channels = new ArrayList<>(retrieveStreamChannels(DriveMapper.getInstance().maybeMapPath(streamChannelFile)));
        if (channels.isEmpty()) {
            throw new IllegalStateException("No channels were specified in " + streamChannelFile);
        }

        retrieveBootDetectorInfo(propertyList);

        PairT<Double, Double> band = parsePassband(propertyList);
        passBandLowFrequency = band.getFirst();
        passBandHighFrequency = band.getSecond();
        preprocessorFilterOrder = Integer.parseInt(propertyList.getProperty("PreprocessorFilterOrder", "6"));

        blockSizeSeconds = Double.parseDouble(propertyList.getProperty("BlockSizeSeconds", "300.0").trim());
        decimationRate = Integer.parseInt(propertyList.getProperty("DecimationRate", "1"));
        statsRefreshIntervalInBlocks = Integer.parseInt(propertyList.getProperty("StatsRefreshIntervalInBlocks", "1000"));

        subspaceThresholdValue = Double.parseDouble(propertyList.getProperty("SubspaceThresholdValue", "0.1").trim());
        subspaceBlackoutPeriod = Double.parseDouble(propertyList.getProperty("SubspaceBlackoutPeriod", "3.0").trim());
        energyCaptureThreshold = Double.parseDouble(propertyList.getProperty("EnergyCaptureThreshold", "0.9").trim());

        spawnCorrelationDetectors = Boolean.parseBoolean(propertyList.getProperty("SpawnCorrelationDetectors", "true").trim());

        produceTriggers = Boolean.parseBoolean(propertyList.getProperty("ProduceTriggers", "true").trim());
        triggerOnlyOnCorrelators = Boolean.parseBoolean(propertyList.getProperty("TriggerOnlyOnCorrelators", "false").trim());
        loadCorrelatorsFromDb = Boolean.parseBoolean(propertyList.getProperty("LoadCorrelatorsFromDb", "true").trim());

        snrThreshold = Double.parseDouble(propertyList.getProperty("SnrThreshold", "2.0").trim());
        minEventDuration = Double.parseDouble(propertyList.getProperty("MinEventDuration", "10.0").trim());
        useConfigFileThreshold = Boolean.parseBoolean(propertyList.getProperty("UseConfigFileThreshold", "true").trim());
        useDynamicThresholds = Boolean.parseBoolean(propertyList.getProperty("UseDynamicThresholds", "false").trim());
        
        minComputedThreshold = Double.parseDouble(propertyList.getProperty("MinComputedThreshold", "0.05").trim());
        maxComputedThreshold = Double.parseDouble(propertyList.getProperty("MaxComputedThreshold", "0.65").trim());

        boolean screenPowerTriggers = Boolean.parseBoolean(propertyList.getProperty("FKScreenPowerTriggers", "false").trim());
        boolean computeFKParams = Boolean.parseBoolean(propertyList.getProperty("ComputeAndSaveFKParams", "false").trim());
        boolean requireMinimumVelocity = Boolean.parseBoolean(propertyList.getProperty("RequireMinimumVelocity", "false").trim());
        boolean requireMaximumVelocity = Boolean.parseBoolean(propertyList.getProperty("RequireMaximumVelocity", "false").trim());

        double FKSMax = Double.parseDouble(propertyList.getProperty("FKSMax", "0.4").trim());

        double fkAzTolerance = Double.parseDouble(propertyList.getProperty("FKAzimuthTolerance", "15.0").trim());
        double fkVelTolerance = Double.parseDouble(propertyList.getProperty("FKVelocityTolerance", "3.0").trim());
        FKScreenRange fkScreenRange = new FKScreenRange(fkAzTolerance, fkVelTolerance);

        double minFKFrequency = Double.parseDouble(propertyList.getProperty("MinFKFrequency", "0.1").trim());
        double maxFKFrequency = Double.parseDouble(propertyList.getProperty("MaxFKFrequency", "8.0").trim());
        double minFKQuality = Double.parseDouble(propertyList.getProperty("MinFKQuality", "1.0").trim());
        double fkWindowLength = Double.parseDouble(propertyList.getProperty("FKWindowLength", "10.0").trim());
        double minVelocity = Double.parseDouble(propertyList.getProperty("MinimumVelocity", "5.0").trim());
        double maxVelocity = Double.parseDouble(propertyList.getProperty("MaximumVelocity", "25.0").trim());

        fKScreenParams = new FKScreenParams(fkScreenRange, FKSMax, minFKFrequency,
                maxFKFrequency, fkWindowLength, minFKQuality, minVelocity, maxVelocity, computeFKParams,
                screenPowerTriggers, requireMinimumVelocity, requireMaximumVelocity);

        String fileList = propertyList.getProperty("DetectorSpecificationFileList");
        if (fileList != null) {
            fileList = fileList.trim();
            ApplicationLogger.getInstance().log(Level.INFO, "Preparing to create new detectors identified in " + fileList);
            String[] files = FileInputArrayLoader.fillStrings(fileList);
            for (String file : files) {
                DetectorSpecification specification = SpecificationFactory.getSpecification(file.trim());
                detectorSpecifications.add(specification);
            }
        }
    }

    public double getPassBandLowFrequency() {
        return passBandLowFrequency;
    }

    public double getPassBandHighFrequency() {
        return passBandHighFrequency;
    }

    public int getPreprocessorFilterOrder() {
        return preprocessorFilterOrder;
    }

    public double getSubspaceThresholdValue() {
        return subspaceThresholdValue;
    }

    public double getSubspaceBlackoutPeriod() {
        return subspaceBlackoutPeriod;
    }

    public boolean isSpawnCorrelationDetectors() {
        return spawnCorrelationDetectors;
    }

    public boolean isProduceTriggers() {
        return produceTriggers;
    }

    public boolean isTriggerOnlyOnCorrelators() {
        return triggerOnlyOnCorrelators;
    }

    public double getMinComputedThreshold() {
        return minComputedThreshold;
    }

    public double getMaxComputedThreshold() {
        return maxComputedThreshold;
    }

    private Properties getPropertyList(String filename) throws IOException {

        Properties propertyList = new Properties();
        String tmp = new String(Files.readAllBytes(Paths.get(filename)));
        propertyList.load(new StringReader(tmp.replace("\\", "\\\\")));
        return propertyList;

    }

    private PairT<Double, Double> parsePassband(Properties propertyList) {
        String passband = propertyList.getProperty("Passband", "2.0 5.0");

        StringTokenizer st = new StringTokenizer(passband);
        if (st.countTokens() != 2) {
            throw new IllegalStateException("Passband required two values!");
        }
        double low = Double.parseDouble(st.nextToken());
        double high = Double.parseDouble(st.nextToken());
        return new PairT<>(low, high);
    }

    public String getStreamName() {
        return streamName;
    }

    private Collection<StreamKey> retrieveStreamChannels(String streamChannelFile) throws IOException {
        String[] lines = FileInputArrayLoader.fillStrings(streamChannelFile);
        Collection<StreamKey> result = new ArrayList<>();
        for (String line : lines) {
            String[] tokens = line.trim().split("\\s+");
            switch (tokens.length) {
                case 2:
                    // Old-style sta-chan description
                    result.add(new StreamKey(tokens[0], tokens[1]));
                    break;
                case 4:
                    //net-sta-chan-locid
                    result.add(new StreamKey(tokens[0], tokens[1], tokens[2], tokens[3]));
                    break;
                case 5:
                    //AGENCY-net-sta-chan-locid
                    result.add(new StreamKey(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4]));
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private void retrieveBootDetectorInfo(Properties propertyList) throws Exception {
        String bootDetectorFile = propertyList.getProperty("BootDetectorsFile");
        if (bootDetectorFile != null && !bootDetectorFile.isEmpty()) {
            String[] lines = FileInputArrayLoader.fillStrings(DriveMapper.getInstance().maybeMapPath(bootDetectorFile));
            for (String line : lines) {
                int cindex = line.indexOf("#");
                if (cindex >= 0) {
                    line = line.substring(0, cindex);
                }
                if (line.isEmpty()) {
                    continue;
                }
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length == 2) {
                    DetectorType type = DetectorType.valueOf(tokens[0]);
                    String filename = DriveMapper.getInstance().maybeMapPath(tokens[1]);
                    switch (type) {
                        case STALTA:
                            STALTASpecification params = getStaLtaParams(filename);
                            bootDetectorParams.addStaLtaParams(streamName, params);
                            break;
                        case ARRAYPOWER:
                            ArrayDetectorSpecification params2 = getArrayParams(filename);
                            bootDetectorParams.addArrayParams(streamName, params2);
                            break;
                        case BULLETIN:
                            BulletinSpecification params3 = BulletinSpecification.getSpecificationFromFile(filename);
                            bootDetectorParams.addBulletinParams(streamName, params3);
                            break;
                    }
                }
            }
        }
    }

    private STALTASpecification getStaLtaParams(String filename) throws FileNotFoundException, IOException {

        return STALTASpecification.getSpecificationFromFile(filename);
    }

    private ArrayDetectorSpecification getArrayParams(String filename) throws Exception {
        int jdate = ProcessingPrescription.getInstance().getMinJdateToProcess();
        return ArrayDetectorSpecification.create(filename, jdate);

    }

    public BootDetectorParams getBootDetectorParams() {
        return bootDetectorParams;
    }

    public double getEnergyCaptureThreshold() {
        return energyCaptureThreshold;
    }

    public boolean isLoadCorrelatorsFromDb() {
        return loadCorrelatorsFromDb;
    }

    Collection<DetectorSpecification> getDetectorSpecifications() {
        return new ArrayList<>(detectorSpecifications);
    }

    /**
     * @return the configFileDir
     */
    public File getConfigFileDir() {
        return configFileDir;
    }

    public String getConfigFileName() {
        return configFileName;
    }

    /**
     * @return the snrThreshold
     */
    public double getSnrThreshold() {
        return snrThreshold;
    }

    /**
     * @return the minEventDuration
     */
    public double getMinEventDuration() {
        return minEventDuration;
    }

    boolean isUseConfigFileThreshold() {
        return useConfigFileThreshold;
    }

    /**
     * @return the decimationRate
     */
    public int getDecimationRate() {
        return decimationRate;
    }

    /**
     * @return the fKScreenParams
     */
    public FKScreenParams getfKScreenParams() {
        return fKScreenParams;
    }

    public boolean isArrayStream() {
        if (this.bootDetectorParams.isHasArrayBootDetector()) {
            return true;
        }
        if (fKScreenParams.isComputeFKOnTriggers()) {
            return true;
        }

        return detectorSpecifications.stream().anyMatch((spec) -> (spec.isArraySpecification()));
    }

    void validateStreamParams(double rate) {
        double maxWindowLength = ProcessingPrescription.getInstance().getMaxTemplateLength();
        if (blockSizeSeconds < 0) {
            blockSizeSeconds = 2 * maxWindowLength;
        }
        if (blockSizeSeconds < 2 * maxWindowLength) {
            String msg = String.format("For stream %s block size in seconds is %f. This is less than 2 * max window length of %f seconds!",
                    getStreamName(), blockSizeSeconds, maxWindowLength);
            throw new IllegalStateException(msg);
        }
        if (undecimatedBlockSize < 0) {

        }

        decimationRate = maybeComputeDecimationRate(rate);
        if (undecimatedBlockSize % decimationRate != 0) {
            String msg = String.format("Supplied decimate rate rate for stream %s is %d. This does not evenly divide the block size!",
                    getStreamName(), decimationRate);
            throw new IllegalStateException(msg);
        }
        double decimatedRate = rate / decimationRate;
        if (decimatedRate / 2 <= passBandHighFrequency) {
            String msg = String.format("Decimated sample rate for stream %s is %f. This is too low to support a filter band extending to %f!",
                    getStreamName(), decimatedRate, passBandHighFrequency);
            throw new IllegalStateException(msg);
        }
        decimatedBlockSize = undecimatedBlockSize / decimationRate;
    }

    /**
     * @return the blockSizeSeconds
     */
    public double getBlockSizeSeconds() {
        return blockSizeSeconds;
    }

    private int maybeComputeDecimationRate(double rate) {
        undecimatedBlockSize = (int) (blockSizeSeconds * rate);
        if (decimationRate >= 1) {
            return decimationRate;
        }
        int arate = (int) (rate / 2 / passBandHighFrequency);
        while (undecimatedBlockSize % arate != 0) {
            arate -= 1;
        }
        return arate;
    }

    /**
     * @return the undecimatedBlockSize
     */
    public int getUndecimatedBlockSize() {
        return undecimatedBlockSize;
    }

    /**
     * @return the decimatedBlockSize
     */
    public int getDecimatedBlockSize() {
        return decimatedBlockSize;
    }

    boolean isUseDynamicThresholds() {
        return useDynamicThresholds;
    }

    int getStatsRefreshIntervalInBlocks() {
        return statsRefreshIntervalInBlocks;
    }

}
