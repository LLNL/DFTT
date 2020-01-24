/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.configuration;

/**
 *
 * @author dodge1
 */
public class MatchedFieldParameters {
    private final double detectionThreshold;
    private final double blackoutSeconds;
    private final int matchedFieldBands;        // number of bands for Matched Field Detector
    private final int matchedFieldDesignFactor; // parameter controlling filter bank design characteristics (sharpness of filter cutoff)
    private final int matchedFieldDimension;    // dimension of Matched Field Detector defined directly
    private final float staDuration;
    private final float ltaDuration;
    private final float staLtaDelaySeconds;
    
    public MatchedFieldParameters(double detectionThreshold,
            double energyCapture,
            double blackoutSeconds,
            int matchedFieldBands,
            int matchedFieldDesignFactor,
            int matchedFieldDimension,
            float staDuration,
            float ltaDuration,
            float staLtaDelaySeconds)
    {
        this.detectionThreshold = detectionThreshold;
        this.blackoutSeconds = blackoutSeconds;
        this.matchedFieldBands = matchedFieldBands;
        this.matchedFieldDesignFactor = matchedFieldDesignFactor;
        this.matchedFieldDimension = matchedFieldDimension;
        this.staDuration = staDuration;
        this.ltaDuration = ltaDuration;
        this.staLtaDelaySeconds = staLtaDelaySeconds;
    }

    /**
     * @return the detectionThreshold
     */
    public double getDetectionThreshold() {
        return detectionThreshold;
    }


    /**
     * @return the blackoutSeconds
     */
    public double getBlackoutSeconds() {
        return blackoutSeconds;
    }

    /**
     * @return the matchedFieldBands
     */
    public int getMatchedFieldBands() {
        return matchedFieldBands;
    }

    /**
     * @return the matchedFieldDesignFactor
     */
    public int getMatchedFieldDesignFactor() {
        return matchedFieldDesignFactor;
    }

    /**
     * @return the matchedFieldDimension
     */
    public int getMatchedFieldDimension() {
        return matchedFieldDimension;
    }

    /**
     * @return the staDuration
     */
    public float getStaDuration() {
        return staDuration;
    }

    /**
     * @return the ltaDuration
     */
    public float getLtaDuration() {
        return ltaDuration;
    }

    /**
     * @return the staLtaDelaySeconds
     */
    public float getStaLtaDelaySeconds() {
        return staLtaDelaySeconds;
    }
}
