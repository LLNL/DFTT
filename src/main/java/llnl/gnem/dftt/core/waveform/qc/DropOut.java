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
package llnl.gnem.dftt.core.waveform.qc;

import java.io.Serializable;
import java.util.Objects;
import llnl.gnem.dftt.core.util.Epoch;

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
