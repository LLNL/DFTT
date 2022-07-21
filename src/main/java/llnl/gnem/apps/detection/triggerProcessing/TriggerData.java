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
package llnl.gnem.apps.detection.triggerProcessing;

import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;
import llnl.gnem.apps.detection.util.TimeStamp;
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
