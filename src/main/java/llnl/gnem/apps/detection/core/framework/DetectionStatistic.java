/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework;

import java.util.Map;
import java.util.TreeMap;
import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class DetectionStatistic {

    private final float[] detectionStatistic;
    private final TimeT time;
    private final double sampleRate;
    private final DetectorInfo detectorInfo;
    private final boolean valid;
    

    public int size() {
        return detectionStatistic.length;
    }

    public DetectionStatistic(float[] detectionStatistic, 
            TimeT time, 
            double sampleRate, 
            DetectorInfo detectorInfo) {
        this.detectionStatistic = detectionStatistic.clone();
        this.time = time;
        this.sampleRate = sampleRate;
        this.detectorInfo = detectorInfo;
        valid = true;
    }
    
    public DetectionStatistic( int statisticLength,
            TimeT time, 
            double sampleRate, 
            DetectorInfo detectorInfo) {
        this.detectionStatistic = new float[statisticLength];
        this.time = time;
        this.sampleRate = sampleRate;
        this.detectorInfo = detectorInfo;
        valid = false;
    }

    @Override
    public String toString() {
        return String.format("DetStatistic of length %d, starting at %s for detectorid %d", 
                detectionStatistic.length,time,getDetectorInfo().getDetectorid());
    }

    public float[] getStatistic() {
        return detectionStatistic.clone();
    }

    public TimeT getTime() {
        return time;
    }

    public double getSampleRate() {
        return sampleRate;
    }


    public static DetectionStatistic combine(DetectionStatistic[] statistics) {
        Map<Double, DetectionStatistic> timeMap = new TreeMap<>();
        int totalLength = 0;
        for (DetectionStatistic ds : statistics) {
            timeMap.put(ds.getTime().getEpochTime(), ds);
            totalLength += ds.size();
        }

        DetectionStatistic first = null;
        for (Double v : timeMap.keySet()) {
            DetectionStatistic ds = timeMap.get(v);
            if (first == null) {
                first = ds;
            }
        }
        float[] array = new float[totalLength];
        int offset = 0;
        for (Double v : timeMap.keySet()) {
            DetectionStatistic ds = timeMap.get(v);
            System.arraycopy(ds.detectionStatistic, 0, array, offset, ds.size());
            offset += ds.size();
        }
        return new DetectionStatistic(array, 
                first.time, 
                first.sampleRate, first.getDetectorInfo());
    }

    /**
     * @return the detectorInfo
     */
    public DetectorInfo getDetectorInfo() {
        return detectorInfo;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

   

}
