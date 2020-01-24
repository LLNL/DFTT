/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.dataobjects;

import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class ShortDetectionSummary {

    private final int detectorid;
    private final int detectionid;

    private final double time;
    private final double detectionStatistic;

    public ShortDetectionSummary(int detectorid, int detectionid, double time, double detectionStatistic) {
        this.detectorid = detectorid;
        this.detectionid = detectionid;
        this.time = time;
        this.detectionStatistic = detectionStatistic;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + this.detectorid;
        hash = 83 * hash + this.detectionid;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.time) ^ (Double.doubleToLongBits(this.time) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.detectionStatistic) ^ (Double.doubleToLongBits(this.detectionStatistic) >>> 32));
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
        final ShortDetectionSummary other = (ShortDetectionSummary) obj;
        if (this.detectorid != other.detectorid) {
            return false;
        }
        if (this.detectionid != other.detectionid) {
            return false;
        }
        if (Double.doubleToLongBits(this.time) != Double.doubleToLongBits(other.time)) {
            return false;
        }
        if (Double.doubleToLongBits(this.detectionStatistic) != Double.doubleToLongBits(other.detectionStatistic)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ShortDetectionSummary{" + "detectorid=" + detectorid + ", detectionid=" + detectionid + ", time=" + new TimeT(time).toString() + ", detectionStatistic=" + detectionStatistic + '}';
    }

    public int getDetectorid() {
        return detectorid;
    }

    public int getDetectionid() {
        return detectionid;
    }

    public double getTime() {
        return time;
    }

    public double getDetectionStatistic() {
        return detectionStatistic;
    }

}
