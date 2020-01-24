/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.triggerProcessing;

import com.oregondsp.util.TimeStamp;
import java.util.stream.Stream;
import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;

/**
 *
 * @author dodge
 */
public class TriggerData {

    private final DetectorInfo detectorInfo;
    private final int index;
    private final TimeStamp triggerTime;
    private final float statistic;

    public TriggerData(DetectorInfo detectorInfo, int index, TimeStamp triggerTime, float statistic) {
        this.detectorInfo = detectorInfo;
        this.index = index;
        this.triggerTime = triggerTime;
        this.statistic = statistic;

    }

    @Override
    public String toString() {
        return "TriggerData{" + "detectorInfo=" + detectorInfo + ", index=" + index + ", triggerTime=" + triggerTime + ", statistic=" + statistic + '}';
    }

    /**
     * @return the detectorInfo
     */
    public DetectorInfo getDetectorInfo() {
        return detectorInfo;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the triggerTime
     */
    public TimeStamp getTriggerTime() {
        return triggerTime;
    }

    /**
     * @return the statistic
     */
    public float getStatistic() {
        return statistic;
    }

    public boolean isContained(StreamSegment processedStream, double leadSeconds, double lagSeconds) {
        Epoch required = new Epoch(getTriggerTime().epochAsDouble() - leadSeconds, getTriggerTime().epochAsDouble() + lagSeconds);
        return processedStream.includes(required);
    }
}