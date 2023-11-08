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
import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * @author dodge1
 */
public class StreamsConfig {

    private final Map<String, StreamInfo> streamInfoMap;

    private StreamsConfig() {
        streamInfoMap = new ConcurrentHashMap<>();
    }

    public Collection<String> getStreamNames() {
        return streamInfoMap.keySet();
    }

    public static StreamsConfig getInstance() {
        return StreamsConfigHolder.instance;
    }

    public void populateMap(Map<String, StreamInfo> sourceMap) {
        streamInfoMap.clear();
        streamInfoMap.putAll(sourceMap);
    }

    public boolean isPopulated() {
        return !streamInfoMap.isEmpty();
    }

    public double getPassBandHighFrequency(String streamName) {
        return getInfo(streamName).getPassBandHighFrequency();
    }

    public StreamInfo getInfo(String streamName) {
        StreamInfo info = streamInfoMap.get(streamName);
        if (info != null) {
            return info;
        } else {
            throw new IllegalArgumentException("Supplied stream name: " + streamName + " not found in map!");
        }
    }

    public double getSubspaceThresholdValue(String streamName) {
        return getInfo(streamName).getSubspaceThresholdValue();
    }

    public double getSubspaceBlackoutPeriod(String streamName) {
        return getInfo(streamName).getSubspaceBlackoutPeriod();
    }

    public double getSubspaceEnergyCaptureThreshold(String streamName) {
        return getInfo(streamName).getEnergyCaptureThreshold();
    }

    public boolean isSpawnCorrelationDetectors(String streamName) {
        return getInfo(streamName).isSpawnCorrelationDetectors();
    }

    public boolean isProduceTriggers(String streamName) {
        return getInfo(streamName).isProduceTriggers();
    }

    public boolean isTriggerOnlyOnCorrelators(String streamName) {
        return getInfo(streamName).isTriggerOnlyOnCorrelators();
    }


    public double getMinComputedThreshold(String streamName) {
        return getInfo(streamName).getMinComputedThreshold();
    }

    public double getMaxComputedThreshold(String streamName) {
        return getInfo(streamName).getMaxComputedThreshold();
    }
    
    
    public boolean isLoadCorrelatorsFromDb(String streamName) {
        return getInfo(streamName).isLoadCorrelatorsFromDb();
    }

    public int getPreprocessorFilterOrder(String streamName) {
        return getInfo(streamName).getPreprocessorFilterOrder();
    }

    public int getStatsRefreshIntervalInBlocks(String streamName) {
        return getInfo(streamName).getStatsRefreshIntervalInBlocks();
    }

    /**
     * @param streamName
     * @return the snrThreshold
     */
    public double getSnrThreshold(String streamName) {
        return getInfo(streamName).getSnrThreshold();
    }

    /**
     * @param streamName
     * @return the minEventDuration
     */
    public double getMinEventDuration(String streamName) {
        return getInfo(streamName).getMinEventDuration();
    }

    public Collection<DetectorSpecification> getDetectorSpecifications(String streamName) {
        return getInfo(streamName).getDetectorSpecifications();
    }

    public boolean isUseConfigFileThreshold(String streamName) {
        return getInfo(streamName).isUseConfigFileThreshold();
    }

    public int getDecimatedDataBlockSize(String streamName) {
        return getInfo(streamName).getDecimatedBlockSize();
    }

    public int getDecimationRate(String streamName) {
        return getInfo(streamName).getDecimationRate();
    }

    public int getUndecimatedBlockSize() {
        int result = -1;
        for (StreamInfo info : streamInfoMap.values()) {
            int value = info.getDecimatedBlockSize() * info.getDecimationRate();
            if (result < 1) {
                result = value;
            } else if (result > 0 && result != value) {
                throw new IllegalStateException("Not all streams have the same undecimated block size!");
            }
        }
        return result;
    }

    public boolean isArrayStream(String streamName) {
        return getInfo(streamName).isArrayStream();
    }

    public FKScreenParams getFKScreenParams(String streamName) {
        return getInfo(streamName).getfKScreenParams();
    }

    void validateStreamParams(String name, double rate) {
        StreamInfo info = streamInfoMap.get(name);
        if (info != null) {
            info.validateStreamParams(rate);
        }
    }



    public boolean isUseDynamicThresholds(String streamName) {
        return getInfo(streamName).isUseDynamicThresholds();
    }

    public boolean isPositionRequired(String sta) {
        return streamInfoMap.values().stream().filter((info) -> (info.isArrayStream())).anyMatch((info) -> (info.getChannels().stream().anyMatch((key) -> (key.getSta().equals(sta)))));
    }

    private static class StreamsConfigHolder {

        private static final StreamsConfig instance = new StreamsConfig();
    }

    public double getMaxPassbandUpperCorner() {
        double maxCorner = -Double.MAX_VALUE;
        for (StreamInfo info : streamInfoMap.values()) {
            if (info.getPassBandHighFrequency() > maxCorner) {
                maxCorner = info.getPassBandHighFrequency();
            }
        }
        return maxCorner;
    }

    public double getPassBandLowFrequency(String streamName) {
        return getInfo(streamName).getPassBandLowFrequency();
    }

}
