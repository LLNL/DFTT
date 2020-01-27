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
