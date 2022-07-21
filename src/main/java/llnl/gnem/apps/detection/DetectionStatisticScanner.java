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
package llnl.gnem.apps.detection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.TriggerPositionType;
import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;
import llnl.gnem.apps.detection.triggerProcessing.TriggerData;
import llnl.gnem.apps.detection.util.SubspaceThreshold;
import llnl.gnem.apps.detection.util.TimeStamp;

/**
 *
 * @author dodge1
 */
public class DetectionStatisticScanner {

    Map<Integer, Queue<DetectionStatistic>> detectorStatisticMap;
    Map<Integer, Integer> detectorOverRunMap;
    private final boolean triggerOnlyOnCorrelators;
    private final String streamName;

    public boolean isTriggerOnlyOnCorrelators() {
        return triggerOnlyOnCorrelators;
    }

    public DetectionStatisticScanner(boolean triggerOnlyOnCorrelators, String streamName) {
        detectorStatisticMap = new ConcurrentHashMap<>();
        detectorOverRunMap = new ConcurrentHashMap<>();
        this.triggerOnlyOnCorrelators = triggerOnlyOnCorrelators;
        this.streamName = streamName;
    }

    public void addStatistic(DetectionStatistic statistic) {
        Queue<DetectionStatistic> statisticPair = detectorStatisticMap.get(statistic.getDetectorInfo().getDetectorid());
        if (statisticPair == null) {
            statisticPair = new ArrayBlockingQueue<>(2);
            detectorStatisticMap.put(statistic.getDetectorInfo().getDetectorid(), statisticPair);
            detectorOverRunMap.put(statistic.getDetectorInfo().getDetectorid(), 0);
        }
        if (statisticPair.size() == 2) { //Remove the oldest statistic to make room for the new
            statisticPair.remove();
        }
        statisticPair.add(statistic);
    }

    public Collection<TriggerData> scanForTriggers(boolean useDynamicThreshold) {
        Collection<TriggerData> result = new ArrayList<>();
        for (int detectorid : detectorStatisticMap.keySet()) {
            int lastBlockOverrun = detectorOverRunMap.get(detectorid);
            Queue<DetectionStatistic> statisticPair = detectorStatisticMap.get(detectorid);

            DetectionStatistic[] statistics = statisticPair.toArray(new DetectionStatistic[1]);
            if (statistics.length == 2) {
                DetectionStatistic combined = DetectionStatistic.combine(statistics);
                DetectorInfo detectorInfo = combined.getDetectorInfo();
                double threshold = detectorInfo.getThreshold();
                if (detectorInfo.getDetectorType() == DetectorType.SUBSPACE) {
                    if (!useDynamicThreshold) {
                        SubspaceThreshold.getInstance().setThresholdFromDb(detectorInfo.getThreshold());
                        threshold = SubspaceThreshold.getInstance().getDesiredThreshold(streamName);
                    }
                }

                if (triggerOnlyOnCorrelators && detectorInfo.getDetectorType() != DetectorType.SUBSPACE) {
                    continue;
                }
                int blackoutSamples = (int) (detectorInfo.getBlackoutInterval() * combined.getSampleRate());
                TriggerPositionType triggerPositionType = detectorInfo.getTriggerPositionType();

                float[] statistic = combined.getStatistic();
                int blockSize = statistics[0].size();
                int index = blockSize / 2 + lastBlockOverrun;
                int finish = index + blockSize;
                while (index < finish) {
                    if (statistic[index] > threshold) {

                        int initialTriggerIndex = index;
                        float maxDetStat = statistic[index];

                        int maxIndex = index;
                        int blackOutCount = 0;
                        while (index < finish && (blackOutCount < blackoutSamples || statistic[index] > threshold)) {
                            if (statistic[index] > maxDetStat) {
                                maxDetStat = statistic[index];
                                maxIndex = index;
                            }
                            index++;
                            blackOutCount++;
                        }
                        int overRun = index - finish;
                        if (overRun < 0) {
                            overRun = 0;
                        }

                        detectorOverRunMap.put(detectorid, overRun);

                        int correctedIndex = triggerPositionType == TriggerPositionType.STATISTIC_MAX ? maxIndex : initialTriggerIndex;
                        TimeStamp triggerTime = getCorrectedTriggerTime(combined, correctedIndex);
                        int finalIndex = (int) Math.round((triggerTime.epochAsDouble() - combined.getTime().getEpochTime()) * combined.getSampleRate());
                        TriggerData trigger = new TriggerData(detectorInfo, finalIndex, triggerTime, maxDetStat);
                        result.add(trigger);
                    } else {
                        ++index;
                    }
                }
            }
        }
        return result;
    }

    private TimeStamp getCorrectedTriggerTime(DetectionStatistic combined, int index) {
        double uncorrectedTriggerTime = combined.getTime().getEpochTime() + index / combined.getSampleRate();
        return new TimeStamp(uncorrectedTriggerTime - combined.getDetectorInfo().getDetectorDelayInSeconds());
    }

    public void removeDetector(Integer detectorid) {
        detectorStatisticMap.remove(detectorid);
        detectorOverRunMap.remove(detectorid);
    }

}
