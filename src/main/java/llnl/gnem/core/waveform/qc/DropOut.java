/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.qc;

import java.io.Serializable;
import java.util.Objects;
import llnl.gnem.core.util.Epoch;

/**
 *
 * @author dodge1
 */
public class DropOut implements DataDefect, Serializable{

    private static final long serialVersionUID = -5953815873349905694L;
    private final Epoch epoch;
    private final double value;
    private final double dropoutBegin;
    private final double dropoutEnd;
    private final double timeSeriesStartTime;

    public DropOut(Epoch epoch, double value, double dropoutBegin, double dropoutEnd, double timeSeriesStartTime) {
        this.epoch = epoch;
        this.value = value;
        this.dropoutBegin = dropoutBegin;
        this.dropoutEnd = dropoutEnd;
        this.timeSeriesStartTime = timeSeriesStartTime;
    }
 
 
    @Override
    public Epoch getEpoch() {
        return epoch;
    }

    @Override
    public DefectType getDefectType() {
        return DefectType.DROPOUT;
    }
    
    
    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @return the dropoutBegin
     */
    public double getDropoutBegin() {
        return dropoutBegin;
    }

    /**
     * @return the dropoutEnd
     */
    public double getDropoutEnd() {
        return dropoutEnd;
    }


    /**
     * @return the timeSeriesStartTime
     */
    public double getTimeSeriesStartTime() {
        return timeSeriesStartTime;
    }

    @Override
    public String toString() {
        return "DropOut{" + "epoch=" + epoch + ", value=" + value + ", dropoutBegin=" + dropoutBegin + ", dropoutEnd=" + dropoutEnd + ", timeSeriesStartTime=" + timeSeriesStartTime + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.epoch);
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.dropoutBegin) ^ (Double.doubleToLongBits(this.dropoutBegin) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.dropoutEnd) ^ (Double.doubleToLongBits(this.dropoutEnd) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.timeSeriesStartTime) ^ (Double.doubleToLongBits(this.timeSeriesStartTime) >>> 32));
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
        final DropOut other = (DropOut) obj;
        if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        if (Double.doubleToLongBits(this.dropoutBegin) != Double.doubleToLongBits(other.dropoutBegin)) {
            return false;
        }
        if (Double.doubleToLongBits(this.dropoutEnd) != Double.doubleToLongBits(other.dropoutEnd)) {
            return false;
        }
        if (Double.doubleToLongBits(this.timeSeriesStartTime) != Double.doubleToLongBits(other.timeSeriesStartTime)) {
            return false;
        }
        if (!Objects.equals(this.epoch, other.epoch)) {
            return false;
        }
        return true;
    }


}
