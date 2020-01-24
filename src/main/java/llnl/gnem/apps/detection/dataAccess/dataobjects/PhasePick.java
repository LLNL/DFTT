/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.dataobjects;

import java.util.Objects;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class PhasePick {
    private final int pickid;
    private final int configid;
    private final Integer detectionid;
    private final String phase;
    private final double time;
    private final double std;

    public PhasePick(int pickid, int detectorid, Integer configid, String phase, double time, double std) {
        this.pickid = pickid;
        this.configid = detectorid;
        this.detectionid = configid;
        this.phase = phase;
        this.time = time;
        this.std = std;
    }

    public int getPickid() {
        return pickid;
    }

    public int getConfigid() {
        return configid;
    }

    public Integer getDetectionid() {
        return detectionid;
    }

    public String getPhase() {
        return phase;
    }

    public double getTime() {
        return time;
    }

    public double getStd() {
        return std;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.pickid;
        hash = 97 * hash + this.configid;
        hash = 97 * hash + Objects.hashCode(this.detectionid);
        hash = 97 * hash + Objects.hashCode(this.phase);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.time) ^ (Double.doubleToLongBits(this.time) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.std) ^ (Double.doubleToLongBits(this.std) >>> 32));
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
        final PhasePick other = (PhasePick) obj;
        if (this.pickid != other.pickid) {
            return false;
        }
        if (this.configid != other.configid) {
            return false;
        }
        if (Double.doubleToLongBits(this.time) != Double.doubleToLongBits(other.time)) {
            return false;
        }
        if (Double.doubleToLongBits(this.std) != Double.doubleToLongBits(other.std)) {
            return false;
        }
        if (!Objects.equals(this.phase, other.phase)) {
            return false;
        }
        if (!Objects.equals(this.detectionid, other.detectionid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        TimeT tmp = new TimeT(time);
        return "PhasePick{" + "pickid=" + pickid + ", configid=" + configid + ", detectionid=" + detectionid + ", phase=" + phase + ", time=" + tmp + ", std=" + std + '}';
    }
    
}
