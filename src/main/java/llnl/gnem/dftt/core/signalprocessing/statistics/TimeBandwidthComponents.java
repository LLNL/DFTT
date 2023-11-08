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
package llnl.gnem.dftt.core.signalprocessing.statistics;

/**
 *
 * @author dodge1
 */
public class TimeBandwidthComponents {
    private final double timeCentroid;
    private final double timeSigma;
    private final double frequencyCentroid;
    private final double frequencySigma;

    public TimeBandwidthComponents(double timeCentroid, double timeSigma, double frequencyCentroid, double frequencySigma) {
        this.timeCentroid = timeCentroid;
        this.timeSigma = timeSigma;
        this.frequencyCentroid = frequencyCentroid;
        this.frequencySigma = frequencySigma;
    }
    
    public double getTBP()
    {
        return timeSigma * frequencySigma;
    }

    public double getTimeCentroid() {
        return timeCentroid;
    }

    public double getTimeSigma() {
        return timeSigma;
    }

    public double getFrequencyCentroid() {
        return frequencyCentroid;
    }

    public double getFrequencySigma() {
        return frequencySigma;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.timeCentroid) ^ (Double.doubleToLongBits(this.timeCentroid) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.timeSigma) ^ (Double.doubleToLongBits(this.timeSigma) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.frequencyCentroid) ^ (Double.doubleToLongBits(this.frequencyCentroid) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.frequencySigma) ^ (Double.doubleToLongBits(this.frequencySigma) >>> 32));
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
        final TimeBandwidthComponents other = (TimeBandwidthComponents) obj;
        if (Double.doubleToLongBits(this.timeCentroid) != Double.doubleToLongBits(other.timeCentroid)) {
            return false;
        }
        if (Double.doubleToLongBits(this.timeSigma) != Double.doubleToLongBits(other.timeSigma)) {
            return false;
        }
        if (Double.doubleToLongBits(this.frequencyCentroid) != Double.doubleToLongBits(other.frequencyCentroid)) {
            return false;
        }
        if (Double.doubleToLongBits(this.frequencySigma) != Double.doubleToLongBits(other.frequencySigma)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TimeBandwidthComponents{" + "timeCentroid=" + timeCentroid + ", timeSigma=" + timeSigma + ", frequencyCentroid=" + frequencyCentroid + ", frequencySigma=" + frequencySigma + '}';
    }
    
    
}
