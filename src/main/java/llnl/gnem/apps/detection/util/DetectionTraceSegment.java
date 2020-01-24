/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util;

import llnl.gnem.apps.detection.core.dataObjects.DecimatedStreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.Trigger;
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
