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
package llnl.gnem.core.waveform.classification;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.core.signalprocessing.TimeFreqStatistics;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.seriesMathHelpers.SampleStatistics;
import llnl.gnem.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author dodge1
 */
public class SeismogramFeatures implements Serializable {

    public static final double PRE_PICK_SECONDS_LONG = 50.0;
    private static final double PRE_PICK_SECONDS_SHORT = 15.0;
    private static final double SW_POST_PICK_SECONDS = 10.0;
    private static final double LW_POST_PICK_SECONDS = 50.0;

    private final Double snr;
    private final Double amplitude;
    private final MomentEstimates moments;
    private final HjorthParams hjorthParams;
    private final double pickEpochTime;
    private final SpectralEdgeFrequency edgeFrequency;
    private final double windowLength;
    private double signalEnd;

    private final double kurtosis;
    private final double skewness;
    private final int numModes;
    private double t0;
    private double sigmaT;
    private double omega0;
    private double sigmaF;

    public SeismogramFeatures(Double snr,
            Double amplitude,
            MomentEstimates moments,
            HjorthParams hjorthParams,
            SpectralEdgeFrequency edgeFrequency,
            double pickEpochTime,
            double windowLength,
            double skewness,
            double kurtosis,
            int numModes,double t0,double sigmaT,double omega0,double sigmaF) {
        this.snr = snr;
        this.amplitude = amplitude;
        this.moments = moments;
        this.hjorthParams = hjorthParams;
        this.pickEpochTime = pickEpochTime;
        this.edgeFrequency = edgeFrequency;
        this.windowLength = windowLength;
        this.skewness = skewness;
        this.kurtosis = kurtosis;
        this.numModes = numModes;
        this.t0 = t0;
        this.sigmaT = sigmaT;
        this.omega0 = omega0;
        this.sigmaF = sigmaF;
    }

    public double getPickEpochTime() {
        return pickEpochTime;
    }

    public double getWindowLength() {
        return windowLength;
    }

    /**
     * @return the signalEnd
     */
    public double getSignalEnd() {
        return signalEnd;
    }

    /**
     * @return the numModes
     */
    public int getNumModes() {
        return numModes;
    }

    private double getFrequencyCentroid() {
        return omega0;
    }

    public static enum FeatureType implements Feature {

        SNR, AMPLITUDE, TIME_CENTROID, TIME_SIGMA, TEMPORAL_SKEWNESS, TEMPORAL_KURTOSIS,
        FREQ_CENTROID, FREQ_SIGMA, TBP,
        ACTIVITY, MOBILITY, COMPLEXITY,
        SPECTRAL_EDGE_FREQ, TEMPORAL_EDGE_TIME,
        SKEWNESS, KURTOSIS, NUM_MODES

    };

    private static Double getMaxSwSnr(TimeSeries seismogram, double pickEpochTime) {
        return getSnrForPostWindow(seismogram, pickEpochTime, SW_POST_PICK_SECONDS);
    }

    private static Double getSnrForPostWindow(TimeSeries seismogram, double pickEpochTime, double desiredPostWinLen) {
        double pickOffset = pickEpochTime - seismogram.getTimeAsDouble();
        double longPreWindowLen = Math.min(pickOffset, PRE_PICK_SECONDS_LONG)-1;
        double shortPreWindowLen = Math.min(pickOffset, PRE_PICK_SECONDS_SHORT);

        double maxWindow = seismogram.getEndtime().getEpochTime() - pickEpochTime;
        double postWinLen = Math.min(maxWindow, desiredPostWinLen);

        if (postWinLen > 0) {
            double snr1 = longPreWindowLen > 0 ? seismogram.getSnr(pickEpochTime, longPreWindowLen, postWinLen) : 0;
            double snr2 = shortPreWindowLen > 0 ? seismogram.getSnr(pickEpochTime, shortPreWindowLen, postWinLen) : 0;
            return Math.max(snr2, snr1);
        } else {
            return null;
        }

    }

    private static Double getMaxLwSnr(TimeSeries seismogram, double pickEpochTime) {
        return getSnrForPostWindow(seismogram, pickEpochTime, LW_POST_PICK_SECONDS);
    }

    public static Double getSNR(TimeSeries seismogram, double pickEpochTime) {
        return Math.max(getMaxSwSnr(seismogram, pickEpochTime), getMaxLwSnr(seismogram, pickEpochTime));
    }

    private Double computeAmplitude(TimeSeries seismogram, double pickEpochTime) {
        double maxWindow = seismogram.getEndtime().getEpochTime() - pickEpochTime;
        double postWinLen = Math.min(maxWindow, LW_POST_PICK_SECONDS);

        if (postWinLen > 0) {
            float[] data = seismogram.getSubSection(pickEpochTime, postWinLen);
            return SeriesMath.getPeakToPeakAmplitude(data) / 2;
        } else {
            return null;
        }
    }

    public SeismogramFeatures(TimeSeries ts, double pickEpochTime, double windowLength) {
        this.pickEpochTime = pickEpochTime;
        this.windowLength = windowLength;
        snr = getSNR(ts, pickEpochTime);

        Epoch window = new Epoch(pickEpochTime, pickEpochTime + windowLength);
        TimeSeries copy = ts.crop(window);
        double energyCapture = 0.9;

        signalEnd = windowLength;

        float[] data = copy.getData();

        amplitude = computeAmplitude(copy, pickEpochTime);
        TimeFreqStatistics tfs = new TimeFreqStatistics(data, ts.getDelta());
        t0 = tfs.getT0();
        sigmaT = tfs.getSigma();
        omega0 = tfs.getOmega0();
        sigmaF = tfs.getSigmaf();

        moments = new MomentEstimates(data, ts.getDelta());
        hjorthParams = new HjorthParams(data, ts.getDelta());

        edgeFrequency = new SpectralEdgeFrequency(copy, energyCapture);
        SampleStatistics ss = new SampleStatistics(data);
        kurtosis = ss.getKurtosis();
        skewness = ss.getSkewness();
        numModes = new ModeEstimator().getModeCount(data);
    }

    public String toHtmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");

        sb.append(String.format("SNR: %5.2f<br>", snr));
        sb.append(String.format("Amplitude: %5.2f<br>", amplitude));

        sb.append(String.format("Kurtosis: %5.2f<br>", getKurtosis()));
        sb.append(String.format("Skewness: %5.2f<br>", getSkewness()));
        sb.append(String.format("NumModes: %d<br>", getNumModes()));

        sb.append(String.format("TemporalSkewness: %8.2f<br>", getTemporalSkewness()));
        sb.append(String.format("TemporalKurtosis: %8.2f<br>", getTemporalKurtosis()));
        sb.append(String.format("Freq-Sigma: %8.2f<br>", getFrequencySigma()));
        sb.append(String.format("TBP: %8.2f<br>", getTimeBandWidthProduct()));
        sb.append(String.format("Activity: %8.2f<br>", getActivity()));
        sb.append(String.format("Mobility: %8.2f<br>", getMobility()));
        sb.append(String.format("Complexity: %8.2f<br>", getComplexity()));
        sb.append(String.format("SpectralEdgeFreq: %8.2f<br>", getSpectralEdgeFrequency()));
        sb.append(String.format("TemporalEdgeTime: %8.2f<br>", getSignalEnd()));

        sb.append("</html>");

        return sb.toString();
    }

    public String toCSV() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%5.2f,", snr).trim());
        sb.append(String.format("%5.2f,", amplitude).trim());

        sb.append(String.format("%5.2f,", getKurtosis()).trim());
        sb.append(String.format("%5.2f,", getSkewness()).trim());
        sb.append(String.format("%d,", getNumModes()).trim());

        sb.append(String.format("%8.2f,", getTemporalSkewness()).trim());
        sb.append(String.format("%8.2f,", getTemporalKurtosis()).trim());
        sb.append(String.format("%8.2f,", getFrequencySigma()).trim());
        sb.append(String.format("%8.2f,", getTimeBandWidthProduct()).trim());
        sb.append(String.format("%8.2f,", getActivity()).trim());
        sb.append(String.format("%8.2f,", getMobility()).trim());
        sb.append(String.format("%8.2f,", getComplexity()).trim());
        sb.append(String.format("%8.2f,", getSpectralEdgeFrequency()).trim());
        sb.append(String.format("%8.2f,", getSignalEnd()).trim());

        return sb.toString();
    }

    /**
     * @return the snr
     */
    public Double getSnr() {
        return snr;
    }

    /**
     * @return the amplitude
     */
    public Double getAmplitude() {
        return amplitude;
    }

    public double getTimeCentroid(boolean asEpochTime) {
        return asEpochTime ? t0 + pickEpochTime : t0;
    }

    public double getTimeCentroidSigma() {
        return sigmaT;
    }

    public double getSkewness() {
        return skewness;
    }

    public double getKurtosis() {
        return kurtosis;
    }

    public double getTemporalSkewness() {
        return moments.getTemporalSkewness();
    }

    public double getTemporalKurtosis() {
        return moments.getTemporalKurtosis();
    }

    public double getActivity() {
        return hjorthParams.getActivity();
    }

    public double getMobility() {
        return hjorthParams.getMobility();
    }

    public double getComplexity() {
        return hjorthParams.getComplexity();
    }

    public double getFrequencySigma() {
        return sigmaF;
    }

    public double getTimeBandWidthProduct() {
        return sigmaT*sigmaF;
    }

    public double getSpectralEdgeFrequency() {
        return edgeFrequency.getEdgeFrequency();
    }

    public void listFeatures() {
        for (FeatureType type : FeatureType.values()) {
            System.out.println(String.format("%s: %f", type.toString(), getValue(type)));
        }
    }

    public double getValue(FeatureType type) {
        switch (type) {
            case SPECTRAL_EDGE_FREQ:
                return this.getSpectralEdgeFrequency();
            case TBP:
                return this.getTimeBandWidthProduct();
            case FREQ_SIGMA:
                return this.getFrequencySigma();
            case COMPLEXITY:
                return this.getComplexity();
            case MOBILITY:
                return this.getMobility();
            case ACTIVITY:
                return this.getActivity();
            case TEMPORAL_KURTOSIS:
                return this.getTemporalKurtosis();
            case TEMPORAL_SKEWNESS:
                return this.getTemporalSkewness();
            case TIME_SIGMA:
                return this.getTimeCentroidSigma();
            case TIME_CENTROID:
                return this.getTimeCentroid(false);
            case FREQ_CENTROID:
                return this.getFrequencyCentroid();
            case AMPLITUDE:
                return this.getAmplitude();
            case SNR:
                return this.getSnr();
            case TEMPORAL_EDGE_TIME:
                return this.getSignalEnd();
            case KURTOSIS:
                return this.getKurtosis();
            case SKEWNESS:
                return this.getSkewness();
            case NUM_MODES:
                return this.getNumModes();
            default:
                throw new IllegalArgumentException("Illegal FeatureType: " + type);
        }
    }
    
    

}
