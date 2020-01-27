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
package llnl.gnem.apps.detection.core.dataObjects;

import llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay.FeatureColumn;

/**
 *
 * @author dodge1
 */
public class TriggerDataFeatures {
    private final double snr;
    private final double amplitude;
    private final double timeCentroid;
    private final double timeSigma;
    private final double temporalSkewness;
    private final double temporalKurtosis;
    private final double temporalHyperKurtosis;
    private final double temporalHyperFlatness;
    private final double frequencySigma;
    private final double tbp;
    private final double skewness;
    private final double kurtosis;
    private final double rawSignalKurtosis;
    private final double rawSignalSkewness;
    private final double freqCentroid;
    
    
    public double getFeatureValue(String featureName)
    {
        FeatureColumn col = FeatureColumn.valueOf(featureName);
        switch (col) {
            case SNR:
                return snr;
            case AMPLITUDE:
                return amplitude;
            case TIME_CENTROID:
                return timeCentroid;
            case TIME_SIGMA:
                return timeSigma;
            case TEMPORAL_SKEWNESS:
                return temporalSkewness;
            case TEMPORAL_KURTOSIS:
                return temporalKurtosis;
            case FREQ_SIGMA:
                return frequencySigma;
            case TBP:
                return tbp;
            case SKEWNESS:
                return skewness;
            case KURTOSIS:
                return kurtosis;
            case RAW_SKEWNESS:
                return rawSignalSkewness;
            case RAW_KURTOSIS:
                return rawSignalKurtosis;
            case FREQ_CENTROID:
                return freqCentroid;
            default:
                throw new IllegalStateException("Unrecognized Enum Value " + col);
        }
    }

    public TriggerDataFeatures(double snr, double amplitude, double timeCentroid, 
            double timeSigma, double temporalSkewness, double temporalKurtosis, 
            double temporalHyperKurtosis, double temporalHyperFlatness, 
            double frequencySigma, double tbp, double skewness, double kurtosis, 
            double rawSignalKurtosis, double rawSignalSkewness, double freqCentroid) {
        this.snr = snr;
        this.amplitude = amplitude;
        this.timeCentroid = timeCentroid;
        this.timeSigma = timeSigma;
        this.temporalSkewness = temporalSkewness;
        this.temporalKurtosis = temporalKurtosis;
        this.temporalHyperKurtosis = temporalHyperKurtosis;
        this.temporalHyperFlatness = temporalHyperFlatness;
        this.frequencySigma = frequencySigma;
        this.tbp = tbp;
        this.skewness = skewness;
        this.kurtosis = kurtosis;
        this.rawSignalKurtosis = rawSignalKurtosis;
        this.rawSignalSkewness = rawSignalSkewness;
        this.freqCentroid = freqCentroid;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.snr) ^ (Double.doubleToLongBits(this.snr) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.amplitude) ^ (Double.doubleToLongBits(this.amplitude) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.timeCentroid) ^ (Double.doubleToLongBits(this.timeCentroid) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.timeSigma) ^ (Double.doubleToLongBits(this.timeSigma) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.temporalSkewness) ^ (Double.doubleToLongBits(this.temporalSkewness) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.temporalKurtosis) ^ (Double.doubleToLongBits(this.temporalKurtosis) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.temporalHyperKurtosis) ^ (Double.doubleToLongBits(this.temporalHyperKurtosis) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.temporalHyperFlatness) ^ (Double.doubleToLongBits(this.temporalHyperFlatness) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.frequencySigma) ^ (Double.doubleToLongBits(this.frequencySigma) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.tbp) ^ (Double.doubleToLongBits(this.tbp) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.skewness) ^ (Double.doubleToLongBits(this.skewness) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.kurtosis) ^ (Double.doubleToLongBits(this.kurtosis) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.rawSignalKurtosis) ^ (Double.doubleToLongBits(this.rawSignalKurtosis) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.rawSignalSkewness) ^ (Double.doubleToLongBits(this.rawSignalSkewness) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.freqCentroid) ^ (Double.doubleToLongBits(this.freqCentroid) >>> 32));
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
        final TriggerDataFeatures other = (TriggerDataFeatures) obj;
        if (Double.doubleToLongBits(this.snr) != Double.doubleToLongBits(other.snr)) {
            return false;
        }
        if (Double.doubleToLongBits(this.amplitude) != Double.doubleToLongBits(other.amplitude)) {
            return false;
        }
        if (Double.doubleToLongBits(this.timeCentroid) != Double.doubleToLongBits(other.timeCentroid)) {
            return false;
        }
        if (Double.doubleToLongBits(this.timeSigma) != Double.doubleToLongBits(other.timeSigma)) {
            return false;
        }
        if (Double.doubleToLongBits(this.temporalSkewness) != Double.doubleToLongBits(other.temporalSkewness)) {
            return false;
        }
        if (Double.doubleToLongBits(this.temporalKurtosis) != Double.doubleToLongBits(other.temporalKurtosis)) {
            return false;
        }
        if (Double.doubleToLongBits(this.temporalHyperKurtosis) != Double.doubleToLongBits(other.temporalHyperKurtosis)) {
            return false;
        }
        if (Double.doubleToLongBits(this.temporalHyperFlatness) != Double.doubleToLongBits(other.temporalHyperFlatness)) {
            return false;
        }
        if (Double.doubleToLongBits(this.frequencySigma) != Double.doubleToLongBits(other.frequencySigma)) {
            return false;
        }
        if (Double.doubleToLongBits(this.tbp) != Double.doubleToLongBits(other.tbp)) {
            return false;
        }
        if (Double.doubleToLongBits(this.skewness) != Double.doubleToLongBits(other.skewness)) {
            return false;
        }
        if (Double.doubleToLongBits(this.kurtosis) != Double.doubleToLongBits(other.kurtosis)) {
            return false;
        }
        if (Double.doubleToLongBits(this.rawSignalKurtosis) != Double.doubleToLongBits(other.rawSignalKurtosis)) {
            return false;
        }
        if (Double.doubleToLongBits(this.rawSignalSkewness) != Double.doubleToLongBits(other.rawSignalSkewness)) {
            return false;
        }
        if (Double.doubleToLongBits(this.freqCentroid) != Double.doubleToLongBits(other.freqCentroid)) {
            return false;
        }
        return true;
    }

    
    
    public double getSnr() {
        return snr;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getTimeCentroid() {
        return timeCentroid;
    }

    public double getTimeSigma() {
        return timeSigma;
    }

    public double getTemporalSkewness() {
        return temporalSkewness;
    }

    public double getTemporalKurtosis() {
        return temporalKurtosis;
    }

    public double getTemporalHyperKurtosis() {
        return temporalHyperKurtosis;
    }

    public double getTemporalHyperFlatness() {
        return temporalHyperFlatness;
    }

    public double getFrequencySigma() {
        return frequencySigma;
    }

    public double getTbp() {
        return tbp;
    }

    public double getSkewness() {
        return skewness;
    }

    public double getKurtosis() {
        return kurtosis;
    }

    public double getRawSignalKurtosis() {
        return rawSignalKurtosis;
    }

    public double getRawSignalSkewness() {
        return rawSignalSkewness;
    }

    public double getFreqCentroid() {
        return freqCentroid;
    }
    
}
