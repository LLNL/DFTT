/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.classify;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import weka.core.Attribute;
import weka.core.FastVector;

/**
 *
 * @author dodge1
 */
public class LabeledFeature implements LAF {
    private final Boolean valid;
    private final double snr;
    private final double amplitude;
    private final double timeCentroid;
    private final double timeSigma;
    private final double temporalSkewness;
    private final double temporalKurtosis;
    private final double freqSigma;
    private final double tbp;
    private final double skewness;
    private final double kurtosis;
    private final double rawSkewness;
    private final double rawKurtosis;
    private final double freqCentroid;

    private static final String[] attributes = {"snr", "amplitude", "timeCentroid", "timeSigma",
        "temporalSkewness", "temporalKurtosis","freqSigma", "tbp", "skewness", "kurtosis", 
        "rawSkewness", "rawKurtosis", "freqCentroid"};

    @Override
    public List<String> getAttributeList() {
        return Arrays.asList(attributes);
    }

    public static FastVector getAttributes() {
        int attributeIndex = 0;
        FastVector attr = new FastVector(attributes.length + 1);
        for (String feature : attributes) {
            attr.addElement(new Attribute(feature, attributeIndex++));
        }

        FastVector labels = new FastVector(2);
        labels.addElement("valid");
        labels.addElement("invalid");
        Attribute classAttribute = new Attribute("Class", labels, attributeIndex++);
        attr.addElement(classAttribute);

        return attr;

    }

    @Override
    public List<Double> getValues() {
        return Arrays.asList(snr,
                amplitude,
                timeCentroid,
                timeSigma,
                temporalSkewness,
                temporalKurtosis,
                freqSigma,
                tbp,
                skewness,
                kurtosis,
                rawSkewness,
                rawKurtosis,
                freqCentroid
        );
    }

    @Override
    public Status getLabel() {
        if (valid != null) {
            return valid ? Status.valid : Status.invalid;
        } else {
            return null;
        }
    }

    public LabeledFeature(Boolean valid, 
            double snr, 
            double amplitude,
            double timeCentroid,
            double timeSigma,
            double temporalSkewness,
            double temporalKurtosis,
            double freqSigma, 
            double tbp,
            double skewness,
            double kurtosis, 
            double rawSkewness, 
            double rawKurtosis, 
            double freqCentroid) {
        this.valid = valid;
        this.snr = snr;
        this.amplitude = amplitude;
        this.timeCentroid = timeCentroid;
        this.timeSigma = timeSigma;
        this.temporalSkewness = temporalSkewness;
        this.temporalKurtosis = temporalKurtosis;
        this.freqSigma = freqSigma;
        this.tbp = tbp;
        this.skewness = skewness;
        this.kurtosis = kurtosis;
        this.rawSkewness = rawSkewness;
        this.rawKurtosis = rawKurtosis;
        this.freqCentroid = freqCentroid;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @return the snr
     */
    public double getSnr() {
        return snr;
    }

    /**
     * @return the timeCentroid
     */
    public double getTimeCentroid() {
        return timeCentroid;
    }

    /**
     * @return the timeSigma
     */
    public double getTimeSigma() {
        return timeSigma;
    }

    /**
     * @return the freqSigma
     */
    public double getFreqSigma() {
        return freqSigma;
    }

    /**
     * @return the tbp
     */
    public double getTbp() {
        return tbp;
    }

    /**
     * @return the skewness
     */
    public double getSkewness() {
        return skewness;
    }

    /**
     * @return the kurtosis
     */
    public double getKurtosis() {
        return kurtosis;
    }

    public double getRawSkewness() {
        return rawSkewness;
    }

    public double getRawKurtosis() {
        return rawKurtosis;
    }

    public double getFreqCentroid() {
        return freqCentroid;
    }

    public Boolean getValid() {
        return valid;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getTemporalSkewness() {
        return temporalSkewness;
    }

    public double getTemporalKurtosis() {
        return temporalKurtosis;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.valid);
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.snr) ^ (Double.doubleToLongBits(this.snr) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.amplitude) ^ (Double.doubleToLongBits(this.amplitude) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.timeCentroid) ^ (Double.doubleToLongBits(this.timeCentroid) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.timeSigma) ^ (Double.doubleToLongBits(this.timeSigma) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.temporalSkewness) ^ (Double.doubleToLongBits(this.temporalSkewness) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.temporalKurtosis) ^ (Double.doubleToLongBits(this.temporalKurtosis) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.freqSigma) ^ (Double.doubleToLongBits(this.freqSigma) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.tbp) ^ (Double.doubleToLongBits(this.tbp) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.skewness) ^ (Double.doubleToLongBits(this.skewness) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.kurtosis) ^ (Double.doubleToLongBits(this.kurtosis) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.rawSkewness) ^ (Double.doubleToLongBits(this.rawSkewness) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.rawKurtosis) ^ (Double.doubleToLongBits(this.rawKurtosis) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.freqCentroid) ^ (Double.doubleToLongBits(this.freqCentroid) >>> 32));
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
        final LabeledFeature other = (LabeledFeature) obj;
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
        if (Double.doubleToLongBits(this.freqSigma) != Double.doubleToLongBits(other.freqSigma)) {
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
        if (Double.doubleToLongBits(this.rawSkewness) != Double.doubleToLongBits(other.rawSkewness)) {
            return false;
        }
        if (Double.doubleToLongBits(this.rawKurtosis) != Double.doubleToLongBits(other.rawKurtosis)) {
            return false;
        }
        if (Double.doubleToLongBits(this.freqCentroid) != Double.doubleToLongBits(other.freqCentroid)) {
            return false;
        }
        if (!Objects.equals(this.valid, other.valid)) {
            return false;
        }
        return true;
    }

}