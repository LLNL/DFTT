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
package llnl.gnem.core.waveform.qc;

import java.io.Serializable;
import java.util.Objects;
import llnl.gnem.core.util.Epoch;

/**
 *
 * @author dodge1
 */
public class DataSpike implements DataDefect, Serializable {

    private static final long serialVersionUID = 5526867389113412947L;

    private final Epoch epoch;
    private final double preSpikeMean;
    private final double preSpikeStd;
    private final double maxDeviation;
    private final double localZScore;
    private final double spikeBegin;
    private final double spikeEnd;
    private final double timeSeriesStartTime;

    public DataSpike(double timeSeriesStartTime, double spikeBegin, double spikeEnd,
            double preSpikeMean, double preSpikeStd, double maxDeviation,
            double localZScore) {
        this.epoch = new Epoch(timeSeriesStartTime + spikeBegin, timeSeriesStartTime + spikeEnd);
        this.preSpikeMean = preSpikeMean;
        this.preSpikeStd = preSpikeStd;
        this.maxDeviation = maxDeviation;
        this.localZScore = localZScore;
        this.spikeBegin = spikeBegin;
        this.spikeEnd = spikeEnd;
        this.timeSeriesStartTime = timeSeriesStartTime;
    }
    
    @Override
    public String toString()
    {
        return String.format("Spike of amplitude %8.3f from %5.2f s to %5.2f s (duration = %4.2f) with localZ= %5.3f in segment starting %s.",
                maxDeviation,spikeBegin,spikeEnd,getDuration(),localZScore, epoch.getbeginning());
    }

    public static DataSpike merge(DataSpike first, DataSpike second) {
        if (first.epoch.getStart() > second.epoch.getStart()) {
            throw new IllegalArgumentException("Time order of Spikes is reversed!");
        }
        if (first.timeSeriesStartTime != second.timeSeriesStartTime) {
            throw new IllegalArgumentException("Spikes not from same waveform segment!");
        }
        double devMax = Math.max(first.maxDeviation, second.maxDeviation);
        return new DataSpike(first.timeSeriesStartTime, first.spikeBegin,
                second.spikeEnd,
                first.preSpikeMean,
                first.preSpikeStd, devMax,
                second.localZScore);
    }

    @Override
    public Epoch getEpoch() {
        return new Epoch(epoch);
    }

    public double getDuration() {
        return epoch.duration();
    }

    @Override
    public DefectType getDefectType() {
        return DefectType.SPIKE;
    }

    /**
     * @return the preSpikeMean
     */
    public double getPreSpikeMean() {
        return preSpikeMean;
    }

    /**
     * @return the preSpikeStd
     */
    public double getPreSpikeStd() {
        return preSpikeStd;
    }

    /**
     * @return the maxDeviation
     */
    public double getMaxDeviation() {
        return maxDeviation;
    }

    public double getStart() {
        return epoch.getStart();
    }

    public double getEnd() {
        return epoch.getEnd();
    }

    /**
     * @return the localZScore
     */
    public double getLocalZScore() {
        return localZScore;
    }

    /**
     * @return the spikeBegin
     */
    public double getSpikeBegin() {
        return spikeBegin;
    }

    /**
     * @return the spikeEnd
     */
    public double getSpikeEnd() {
        return spikeEnd;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.epoch);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.preSpikeMean) ^ (Double.doubleToLongBits(this.preSpikeMean) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.preSpikeStd) ^ (Double.doubleToLongBits(this.preSpikeStd) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.maxDeviation) ^ (Double.doubleToLongBits(this.maxDeviation) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.localZScore) ^ (Double.doubleToLongBits(this.localZScore) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.spikeBegin) ^ (Double.doubleToLongBits(this.spikeBegin) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.spikeEnd) ^ (Double.doubleToLongBits(this.spikeEnd) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.timeSeriesStartTime) ^ (Double.doubleToLongBits(this.timeSeriesStartTime) >>> 32));
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
        final DataSpike other = (DataSpike) obj;
        if (Double.doubleToLongBits(this.preSpikeMean) != Double.doubleToLongBits(other.preSpikeMean)) {
            return false;
        }
        if (Double.doubleToLongBits(this.preSpikeStd) != Double.doubleToLongBits(other.preSpikeStd)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxDeviation) != Double.doubleToLongBits(other.maxDeviation)) {
            return false;
        }
        if (Double.doubleToLongBits(this.localZScore) != Double.doubleToLongBits(other.localZScore)) {
            return false;
        }
        if (Double.doubleToLongBits(this.spikeBegin) != Double.doubleToLongBits(other.spikeBegin)) {
            return false;
        }
        if (Double.doubleToLongBits(this.spikeEnd) != Double.doubleToLongBits(other.spikeEnd)) {
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
