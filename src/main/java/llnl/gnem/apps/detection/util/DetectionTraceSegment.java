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
package llnl.gnem.apps.detection.util;

import llnl.gnem.apps.detection.core.dataObjects.DecimatedStreamSegment;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Trigger;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.seismogram.BasicSeismogram;
import llnl.gnem.core.waveform.classification.SeismogramFeatures;

/**
 *
 * @author dodge1
 */
public class DetectionTraceSegment {


    private final BasicSeismogram seismogram;
    private final SeismogramFeatures features;
   
    private final double triggerOffset;

    public DetectionTraceSegment(
            BasicSeismogram seismogram,
            SeismogramFeatures features,
             double triggerOffset) {
        this.seismogram = seismogram;
        this.features = features;
    
        this.triggerOffset = triggerOffset;
    }
    
    
    public static  DetectionTraceSegment buildDetectionTraceSegment(DecimatedStreamSegment segment, int channel, Trigger trigger, String streamName) {
        float[] data = segment.getChannelData(channel);
        double rate = 1.0 / segment.getDecimatedSampleInterval();
        double trigTime = trigger.getTriggerTime().epochAsDouble();

        float[] tmpData = new float[data.length];
        System.arraycopy(data, 0, tmpData, 0, data.length);
        BasicSeismogram seismogram = new BasicSeismogram(null, segment.getSta(channel), segment.getChan(channel), 
                tmpData, rate, new TimeT(segment.getStartTime().epochAsDouble()));
        double leadSeconds = 0;
        double lagSeconds = ProcessingPrescription.getInstance().getMaxTemplateLength();
        double startOffset = Math.max(trigTime - segment.getStartTime().epochAsDouble() - leadSeconds, 0);
        seismogram.cut(startOffset, startOffset + leadSeconds + lagSeconds);
        
        TimeT adjustedTriggerTime = new TimeT(trigger.getTriggerTime().epochAsDouble());
        double triggerOffset = adjustedTriggerTime.getEpochTime() - seismogram.getTimeAsDouble();
       
        SeismogramFeatures features = new SeismogramFeatures(seismogram, adjustedTriggerTime.getEpochTime(), lagSeconds);
        DetectionTraceSegment dts = new DetectionTraceSegment(seismogram, features, triggerOffset);
        return dts;
    }

    /**
     * @return the filtered
     */
    public BasicSeismogram getSeismogram() {
        return seismogram;
    }

    /**
     * @return the filteredFeatures
     */
    public SeismogramFeatures getFeatures() {
        return features;
    }

    /**
     * @return the triggerOffset
     */
    public double getTriggerOffset() {
        return triggerOffset;
    }
}
