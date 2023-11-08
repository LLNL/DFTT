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
package llnl.gnem.apps.detection.core.framework;

import java.util.Map;
import java.util.TreeMap;
import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;
import llnl.gnem.dftt.core.util.TimeT;

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
