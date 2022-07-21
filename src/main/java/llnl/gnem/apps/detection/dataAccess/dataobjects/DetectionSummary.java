/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.dataobjects;

import java.util.Objects;

/**
 *
 * @author dodge1
 */
public class DetectionSummary {

    private final int detectionid;
    private final int triggerid;
    private final int runid;
    private final int detectorid;
    private final double detectionStatistic;
    private final double triggerTime;
    private final double signalDuration;
    private final DetectorType detectorType;

    public DetectionSummary(int detectionid, 
            int triggerid, 
            int runid, 
            int detectorid, 
            double detectionStatistic, 
            double triggerTime, 
            double signalDuration, 
            DetectorType detectorType) {
        this.detectionid = detectionid;
        this.triggerid = triggerid;
        this.runid = runid;
        this.detectorid = detectorid;
        this.detectionStatistic = detectionStatistic;
        this.triggerTime = triggerTime;
        this.signalDuration = signalDuration;
        this.detectorType = detectorType;
    }

    public int getDetectionid() {
        return detectionid;
    }

    public int getTriggerid() {
        return triggerid;
    }

    public int getRunid() {
        return runid;
    }

    public int getDetectorid() {
        return detectorid;
    }

    public double getDetectionStatistic() {
        return detectionStatistic;
    }

    public double getTriggerTime() {
        return triggerTime;
    }

    public double getSignalDuration() {
        return signalDuration;
    }

    public DetectorType getDetectorType() {
        return detectorType;
    }

    @Override
    public String toString() {
        return "DetectionSummary{" + "detectionid=" + detectionid + ", triggerid=" + triggerid + ", runid=" + runid + ", detectorid=" + detectorid + ", detectionStatistic=" + detectionStatistic + ", triggerTime=" + triggerTime + ", signalDuration=" + signalDuration + ", detectorType=" + detectorType + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.detectionid;
        hash = 79 * hash + this.triggerid;
        hash = 79 * hash + this.runid;
        hash = 79 * hash + this.detectorid;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.detectionStatistic) ^ (Double.doubleToLongBits(this.detectionStatistic) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.triggerTime) ^ (Double.doubleToLongBits(this.triggerTime) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.signalDuration) ^ (Double.doubleToLongBits(this.signalDuration) >>> 32));
        hash = 79 * hash + Objects.hashCode(this.detectorType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DetectionSummary other = (DetectionSummary) obj;
        if (this.detectionid != other.detectionid) {
            return false;
        }
        if (this.triggerid != other.triggerid) {
            return false;
        }
        if (this.runid != other.runid) {
            return false;
        }
        if (this.detectorid != other.detectorid) {
            return false;
        }
        if (Double.doubleToLongBits(this.detectionStatistic) != Double.doubleToLongBits(other.detectionStatistic)) {
            return false;
        }
        if (Double.doubleToLongBits(this.triggerTime) != Double.doubleToLongBits(other.triggerTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.signalDuration) != Double.doubleToLongBits(other.signalDuration)) {
            return false;
        }
        if (this.detectorType != other.detectorType) {
            return false;
        }
        return true;
    }



}
