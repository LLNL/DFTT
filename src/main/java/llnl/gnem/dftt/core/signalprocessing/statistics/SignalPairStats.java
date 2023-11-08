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

import java.util.ArrayList;
import java.util.Arrays;
import llnl.gnem.dftt.core.correlation.RealSequenceCorrelator;
import llnl.gnem.dftt.core.correlation.util.CorrelationMax;
import llnl.gnem.dftt.core.signalprocessing.PeriodogramSample;
import llnl.gnem.dftt.core.signalprocessing.SpectralOps;
import llnl.gnem.dftt.core.signalprocessing.WelchPSD;
import llnl.gnem.dftt.core.signalprocessing.WindowFunction;

/**
 *
 * @author dodge1
 */
public class SignalPairStats {

    private static final double COHERENT_POWER_THRESHOLD = 0.95;
    private double correlation;
    private double weightedT0;
    private double weightedSigma;
    private double t0;
    private double sigma;
    private double omega0;
    private double freqSigma;
    private double weightedOmega0;
    private double weightedFreqSigma;
    private final double tb;
    private final double weightedTb;
    private double shiftSeconds;
    private double thresholdFrequency;

    public SignalPairStats(SignalPair input) {
        SignalPair prepared = prepareData(input);
        computeTimeDomainStats(prepared);
        computeFrequencyDomainStats(prepared);
        tb = sigma * freqSigma;
        weightedTb = weightedSigma * weightedFreqSigma;
    }

    public static TimeBandwidthComponents computeTimeBandwidthProduct(float[] data, double dt) {
        double m0 = 0.0;
        double m1 = 0.0;
        double m2 = 0.0;
        int N = data.length;
        for (int i = 0; i < N; i++) {
            double v = data[i];
            double t = i * dt;
            double x2 = v * v;
            m0 += x2;
            m1 += t * x2;
            m2 += t * t * x2;
        }
        double t0 = m1 / m0;
        double sigma = Math.sqrt(m2 / m0 - (m1 * m1) / (m0 * m0));

        ArrayList<PeriodogramSample> psd = new WelchPSD(data, dt).getOneSidedPSD();
        m0 = 0.0;
        m1 = 0.0;
        m2 = 0.0;

        for (PeriodogramSample ps : psd) {
            double v = ps.getValue();
            double t = ps.getFrequency();
            double x2 = v;
            m0 += x2;
            m1 += t * x2;
            m2 += t * t * x2;
        }
        double f0 = m1 / m0;
        double sigmaf = Math.sqrt(m2 / m0 - (m1 * m1) / (m0 * m0));
        
        return new TimeBandwidthComponents(t0, sigma, f0, sigmaf);
    }

    private SignalPair prepareData(SignalPair input) {
        SignalPair trimmed = maybeTrimData(input);
        CorrelationMax cm = RealSequenceCorrelator.correlate(trimmed.getData1(), trimmed.getData2());
        this.correlation = cm.getCcMax();
        this.shiftSeconds = cm.getShift() * input.getDt();
        if (Math.abs(cm.getShift()) >= 1) {
            trimmed = alignData(trimmed, cm.getShift());
        }

        trimmed.applyWindowFunction(WindowFunction.WindowType.TUKEY, 0.05);
        return trimmed;
    }

    private SignalPair maybeTrimData(SignalPair input) {
        int idx1 = getTerminationIndex(input.getData1(), input.getDt());
        int idx2 = getTerminationIndex(input.getData2(), input.getDt());
        int lastIdx = Math.min(idx1, idx2);
        if (lastIdx < 32) {
            return input;
        }
        float[] d1 = new float[lastIdx];
        float[] d2 = new float[lastIdx];
        System.arraycopy(input.getData1(), 0, d1, 0, lastIdx);
        System.arraycopy(input.getData2(), 0, d2, 0, lastIdx);
        return new SignalPair(d1, d2, input.getDt());
    }

    private static int getTerminationIndex(float[] x, double dt) {

        float[] w = new float[x.length];
        Arrays.fill(w, 1.0f);
        T0AndSigma v = computeT0AndSigma(x, dt, w);
        double signalEnd = v.getT0() + 2.5 * v.getSigma();
        int lastIdx = (int) (signalEnd / dt);
        return Math.min(lastIdx, x.length - 1);

    }

    private static T0AndSigma computeT0AndSigma(float[] x, double dt, float[] w) {
        double m0 = 0.0;
        double m1 = 0.0;
        double m2 = 0.0;
        int N = x.length;
        for (int i = 0; i < N; i++) {
            double v = x[i] * w[i];
            double t = i * dt;
            double x2 = v * v;
            m0 += x2;
            m1 += t * x2;
            m2 += t * t * x2;
        }
        double t0 = m1 / m0;
        double sigma = Math.sqrt(m2 / m0 - (m1 * m1) / (m0 * m0));
        T0AndSigma v = new T0AndSigma(t0, sigma);
        return v;
    }

    public static T0AndSigma computeT0AndSigma(float[] x, double dt) {
        double m0 = 0.0;
        double m1 = 0.0;
        double m2 = 0.0;
        int N = x.length;
        for (int i = 0; i < N; i++) {
            double v = x[i];
            double t = i * dt;
            double x2 = v * v;
            m0 += x2;
            m1 += t * x2;
            m2 += t * t * x2;
        }
        double t0 = m1 / m0;
        double sigma = Math.sqrt(m2 / m0 - (m1 * m1) / (m0 * m0));
        T0AndSigma v = new T0AndSigma(t0, sigma);
        return v;
    }

    private void computeTimeDomainStats(SignalPair prepared) {
        int corrWindowWidth = 100;
        float[] w = runningCorrelation(prepared.getData1(), prepared.getData2(), corrWindowWidth);

        T0AndSigma v1 = computeT0AndSigma(prepared.getData1(), prepared.getDt(), w);
        T0AndSigma v2 = computeT0AndSigma(prepared.getData2(), prepared.getDt(), w);

        weightedT0 = (v1.getT0() + v2.getT0()) / 2;
        weightedSigma = (v1.getSigma() + v2.getSigma()) / 2;
        Arrays.fill(w, 1.0f);

        v1 = computeT0AndSigma(prepared.getData1(), prepared.getDt(), w);
        v2 = computeT0AndSigma(prepared.getData2(), prepared.getDt(), w);
        t0 = (v1.getT0() + v2.getT0()) / 2;
        sigma = (v1.getSigma() + v2.getSigma()) / 2;
    }

    private void computeFrequencyDomainStats(SignalPair pair) {

        ArrayList<PeriodogramSample> coherence = SpectralOps.computeCoherence(pair.getData1(), pair.getData2(), pair.getDt());
        ArrayList<PeriodogramSample> psd1 = new WelchPSD(pair.getData1(), pair.getDt()).getOneSidedPSD();
        double omega1 = computeSpectralCentroid(psd1);
        double freqSigma1 = Math.sqrt(computeSpectralVariance(psd1, omega1));
        ArrayList<PeriodogramSample> psd2 = new WelchPSD(pair.getData2(), pair.getDt()).getOneSidedPSD();
        double omega2 = computeSpectralCentroid(psd2);
        double freqSigma2 = Math.sqrt(computeSpectralVariance(psd2, omega2));

        omega0 = (omega1 + omega2) / 2;
        freqSigma = (freqSigma1 + freqSigma2) / 2;

        ArrayList<PeriodogramSample> psd1w = buildWeightedPeriodogram(coherence, psd1);

        double omega1w = computeSpectralCentroid(psd1w);
        double freqSigma1w = Math.sqrt(computeSpectralVariance(psd1w, omega1));

        ArrayList<PeriodogramSample> psd2w = buildWeightedPeriodogram(coherence, psd2);

        double omega2w = computeSpectralCentroid(psd2w);
        double freqSigma2w = Math.sqrt(computeSpectralVariance(psd2w, omega1));
        weightedOmega0 = (omega1w + omega2w) / 2;
        weightedFreqSigma = (freqSigma1w + freqSigma2w) / 2;
    }

    private ArrayList<PeriodogramSample> buildWeightedPeriodogram(ArrayList<PeriodogramSample> coherence, ArrayList<PeriodogramSample> psd1) {
        ArrayList<PeriodogramSample> psd1w = new ArrayList<>();
        double totalPower = 0;
        for (int j = 0; j < coherence.size(); ++j) {
            double f = coherence.get(j).getFrequency();
            double v = coherence.get(j).getValue() * psd1.get(j).getValue();
            totalPower += v;
            psd1w.add(new PeriodogramSample(f, v));
        }
        double powerSoFar = 0;
        double thresholdPower = COHERENT_POWER_THRESHOLD * totalPower;
        for (PeriodogramSample ps : psd1w) {
            double f = ps.getFrequency();
            double v = ps.getValue();
            powerSoFar += v;
            if (powerSoFar >= thresholdPower) {
                thresholdFrequency = f;
                break;
            }
        }
        return psd1w;
    }

    private double computeSpectralCentroid(ArrayList<PeriodogramSample> psd) {
        double m0 = 0.0;
        double m1 = 0.0;
        for (PeriodogramSample ps : psd) {
            m0 += ps.getValue();
            m1 += (ps.getFrequency() * ps.getValue());
        }
        return m1 / m0;
    }

    private double computeSpectralVariance(ArrayList<PeriodogramSample> psd, double omega) {
        double m0 = 0.0;
        double m2 = 0.0;
        for (PeriodogramSample ps : psd) {
            m0 += ps.getValue();
            double df = omega - ps.getFrequency();
            m2 += (df * df * ps.getValue());
        }
        return m2 / m0;
    }

    /**
     * @return the correlation
     */
    public double getCorrelation() {
        return correlation;
    }

    /**
     * @return the weightedT0
     */
    public double getWeightedT0() {
        return weightedT0;
    }

    /**
     * @return the weightedSigma
     */
    public double getWeightedSigma() {
        return weightedSigma;
    }

    /**
     * @return the t0
     */
    public double getT0() {
        return t0;
    }

    /**
     * @return the sigma
     */
    public double getSigma() {
        return sigma;
    }

    /**
     * @return the omega0
     */
    public double getOmega0() {
        return omega0;
    }

    /**
     * @return the freqSigma
     */
    public double getFreqSigma() {
        return freqSigma;
    }

    /**
     * @return the weightedOmega0
     */
    public double getWeightedOmega0() {
        return weightedOmega0;
    }

    /**
     * @return the weightedFreqSigma
     */
    public double getWeightedFreqSigma() {
        return weightedFreqSigma;
    }

    /**
     * @return the tb
     */
    public double getTb() {
        return tb;
    }

    /**
     * @return the weightedTb
     */
    public double getWeightedTb() {
        return weightedTb;
    }

    /**
     * @return the shiftSeconds
     */
    public double getShiftSeconds() {
        return shiftSeconds;
    }

    /**
     * @return the thresholdFrequency
     */
    public double getThresholdFrequency() {
        return thresholdFrequency;
    }

    public static class T0AndSigma {

        private final double t0;
        private final double sigma;

        private T0AndSigma(double t0, double sigma) {
            this.t0 = t0;
            this.sigma = sigma;
        }

        /**
         * @return the t0
         */
        public double getT0() {
            return t0;
        }

        /**
         * @return the sigma
         */
        public double getSigma() {
            return sigma;
        }

    }

    private SignalPair alignData(SignalPair pair, double shift) {
        int intShift = (int) Math.round(shift);
        float[] data1 = pair.getData1();
        float[] data2 = pair.getData2();
        int npts = data1.length;
        if (intShift > 0) {
            int M = npts - intShift;
            float[] newData1 = new float[M];
            float[] newData2 = new float[M];
            System.arraycopy(data1, intShift, newData1, 0, M);
            System.arraycopy(data2, 0, newData2, 0, M);
            return new SignalPair(newData1, newData2, pair.getDt());
        } else if (intShift < 0) {
            int M = npts + intShift;
            float[] newData1 = new float[M];
            float[] newData2 = new float[M];
            System.arraycopy(data1, 0, newData1, 0, M);
            System.arraycopy(data2, -intShift, newData2, 0, M);
            return new SignalPair(newData1, newData2, pair.getDt());
        } else {
            return pair;
        }
    }

    private static float[] runningCorrelation(float[] x, float[] y, int half) {

        int N = x.length;

        float[] w = new float[2 * half + 1];

        for (int i = -half; i <= half; i++) {
            w[i + half] = (float) (0.5 + 0.5 * Math.cos(Math.PI * i / half));
        }

        float[] retval = new float[N];

        for (int j = 0; j < N; ++j) {
            double xy = 0.0f;
            double xx = 0.0f;
            double yy = 0.0f;
            for (int i = -half; i <= half; i++) {
                if (j + i >= 0 && j + i < N) {
                    xy += x[j + i] * y[j + i] * w[i + half];
                    xx += x[j + i] * x[j + i] * w[i + half];
                    yy += y[j + i] * y[j + i] * w[i + half];
                }
            }
            retval[j] = (float) ((xy * xy) / (xx * yy));
        }

        return retval;
    }

}
