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

import java.util.Arrays;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.seriesMathHelpers.DiscontinuityCollection;
import llnl.gnem.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author dodge1
 */
public class RawAttributes {

    private static final int WINDOW_LENGTH = 100;
    private static final int NUM_SAMPLES = 200;
    private static final int DISC_WIN_LENGTH = 20;
    private static final double GLITCH_THRESHOLD = 20.0;
    private static final double DISC_THRESHOLD = 10;
    private static final double PERCENTILE = .999;
    public static final int MIN_ALLOWABLE_AMPLITUDE = 1000;

    /**
     * @param aComputeDiscontinuities the computeDiscontinuities to set
     */
    public static void setComputeDiscontinuities(boolean aComputeDiscontinuities) {
        computeDiscontinuities = aComputeDiscontinuities;
    }
    private final int nglitches;
    private final double distinctValueRatio;
    private final int numDiscontinuities;
    private final double avgDiscontinuityValue;
    private final double maxDiscontinuityValue;
    private final double avgDiscontinuityKurtosis;
    private final double maxDiscontinuityKurtosis;
    private static boolean computeDiscontinuities = true;

    public RawAttributes(int nglitches,
            double distinctValueRatio,
            int numDiscontinuities,
            double avgDiscontinuityValue,
            double maxDiscontinuityValue,
            double dropoutFraction,
            double dropoutImportance) {
        this.nglitches = nglitches;
        this.distinctValueRatio = distinctValueRatio;
        this.numDiscontinuities = numDiscontinuities;
        this.avgDiscontinuityValue = avgDiscontinuityValue;
        this.maxDiscontinuityValue = maxDiscontinuityValue;
        this.avgDiscontinuityKurtosis = 0;
        this.maxDiscontinuityKurtosis = 0;
    }

    public RawAttributes(TimeSeries seismogram) {
        float[] data = seismogram.getData();

        double minGlitchAmp = determineMinGlitchAmp(data);
        nglitches = SeriesMath.removeGlitches(data, WINDOW_LENGTH, minGlitchAmp, GLITCH_THRESHOLD);
        distinctValueRatio = seismogram.getDistinctValueRatio(NUM_SAMPLES);
        if (computeDiscontinuities) {
            DiscontinuityCollection discontinuities = seismogram.findDiscontinuities(DISC_WIN_LENGTH, DISC_THRESHOLD);
            numDiscontinuities = discontinuities.size();
            avgDiscontinuityValue = discontinuities.getAverageDeviation();
            maxDiscontinuityValue = discontinuities.getMaxDeviation();
            avgDiscontinuityKurtosis = discontinuities.getAverageKurtosis();
            maxDiscontinuityKurtosis = discontinuities.getMaxKurtosis();
        } else {
            numDiscontinuities = 0;
            avgDiscontinuityValue = 0;
            maxDiscontinuityValue = 0;
            avgDiscontinuityKurtosis = 0;
            maxDiscontinuityKurtosis = 0;
        }
    }

    private double determineMinGlitchAmp(float[] data) {
        float[] sorted = SeriesMath.abs(data);
        Arrays.sort(sorted);
        int index = (int) (PERCENTILE * sorted.length);
        double minGlitchAmp = sorted[index];
        return Math.max(minGlitchAmp, MIN_ALLOWABLE_AMPLITUDE);
    }


    /**
     * @return the nglitches
     */
    public int getNglitches() {
        return nglitches;
    }

    /**
     * @return the distinctValueRatio
     */
    public double getDistinctValueRatio() {
        return distinctValueRatio;
    }

    /**
     * @return the numDiscontinuities
     */
    public int getNumDiscontinuities() {
        return numDiscontinuities;
    }

    /**
     * @return the avgDiscontinuityValue
     */
    public double getAvgDiscontinuityValue() {
        return avgDiscontinuityValue;
    }

    /**
     * @return the maxDiscontinuityValue
     */
    public double getMaxDiscontinuityValue() {
        return maxDiscontinuityValue;
    }

    /**
     * @return the avgDiscontinuityKurtosis
     */
    public double getAvgDiscontinuityKurtosis() {
        return avgDiscontinuityKurtosis;
    }

    /**
     * @return the maxDiscontinuityKurtosis
     */
    public double getMaxDiscontinuityKurtosis() {
        return maxDiscontinuityKurtosis;
    }
}
