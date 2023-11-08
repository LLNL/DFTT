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
package llnl.gnem.apps.detection.dataAccess.dataobjects;

import java.util.Objects;
import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class PhasePick {

    private int pickid;
    private final int configid;
    private final Integer detectionid;
    private final String phase;
    private double time;
    private double std;
    private final StreamKey key;

    public PhasePick(int pickid, int configid, Integer detectionid, StreamKey key, String phase, double time, double std) {
        this.pickid = pickid;
        this.configid = configid;
        this.detectionid = detectionid;
        this.key = key;
        this.phase = phase;
        this.time = time;
        this.std = Math.abs(std);
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

    public void adjustPickTime(double deltaT) {
        time += deltaT;
    }

    public double getStd() {
        return std;
    }

    public void adjustStd(double deltaStd) {
        std += deltaStd;
        if (std < 0) {
            std = 0;
        }
    }

    public StreamKey getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "PhasePick{" + "pickid=" + pickid + ", configid=" + configid + ", detectionid=" + detectionid + ", phase=" + phase + ", time=" + time + ", std=" + std + ", key=" + key + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.pickid;
        hash = 97 * hash + this.configid;
        hash = 97 * hash + Objects.hashCode(this.detectionid);
        hash = 97 * hash + Objects.hashCode(this.phase);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.time) ^ (Double.doubleToLongBits(this.time) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.std) ^ (Double.doubleToLongBits(this.std) >>> 32));
        hash = 97 * hash + Objects.hashCode(this.key);
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
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }

    public void setPickid(long pickid) {
        this.pickid = (int) pickid;
    }

}
