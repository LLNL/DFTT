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
package llnl.gnem.dftt.core.signalprocessing;

import java.util.ArrayList;
import llnl.gnem.dftt.core.waveform.classification.MomentEstimates;

/**
 *
 * @author dodge1
 */
public class TimeFreqStatistics {

    private final Double t0;
    private final Double sigma;
    private final Double omega0;
    private final Double sigmaf;

    public TimeFreqStatistics(float[] data, double dt) {
        ArrayList<PeriodogramSample> psd = new WelchPSD(data, dt).getOneSidedPSD();
        Double omega = computeSpectralCentroid(psd);
        Double freqSigma = computeSpectralWidth(psd, omega);

        t0 = MomentEstimates.computeCentroid(data, dt);
        sigma = MomentEstimates.computeSigma(data, dt, t0);
        omega0 = omega;
        sigmaf = freqSigma;
    }

    public Double getFrequencyCOV() {
        return (omega0 != null && sigmaf != null && omega0 > 0) ? sigmaf / omega0 : null;
    }

    private static Double computeSpectralCentroid(ArrayList<PeriodogramSample> psd) {
        float[] fdata = new float[psd.size()];
        double df = psd.get(1).getFrequency() - psd.get(0).getFrequency();
        int j = 0;
        for (PeriodogramSample ps : psd) {
            fdata[j++] = (float) ps.getValue();
        }
        return MomentEstimates.computeCentroid(fdata, df);
    }

    private static Double computeSpectralWidth(ArrayList<PeriodogramSample> psd, Double omega) {
        float[] fdata = new float[psd.size()];
        double dff = psd.get(1).getFrequency() - psd.get(0).getFrequency();
        int j = 0;
        for (PeriodogramSample ps : psd) {
            fdata[j++] = (float) ps.getValue();
        }
        return MomentEstimates.computeSigma(fdata, dff, omega);
    }

    /**
     * @return the t0
     */
    public Double getT0() {
        return t0;
    }

    /**
     * @return the sigma
     */
    public Double getSigma() {
        return sigma;
    }

    /**
     * @return the omega0
     */
    public Double getOmega0() {
        return omega0;
    }

    /**
     * @return the sigmaf
     */
    public Double getSigmaf() {
        return sigmaf;
    }

    public Double getTimeBandwidthProduct() {
        return sigma != null && sigmaf != null ? sigma * sigmaf : null;
    }

}
