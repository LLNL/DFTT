/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.signalprocessing.statistics;

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
