/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.signalprocessing;

import java.util.ArrayList;
import llnl.gnem.core.waveform.classification.MomentEstimates;

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
