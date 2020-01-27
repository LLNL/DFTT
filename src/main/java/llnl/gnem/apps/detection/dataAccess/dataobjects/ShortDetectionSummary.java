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
