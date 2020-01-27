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
package llnl.gnem.apps.detection.triggerProcessing;

import java.io.Serializable;
import llnl.gnem.core.signalprocessing.statistics.SignalPairStats;
import llnl.gnem.core.signalprocessing.statistics.TimeBandwidthComponents;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.seriesMathHelpers.SampleStatistics;
import llnl.gnem.core.waveform.classification.Feature;
import llnl.gnem.core.waveform.classification.MomentEstimates;
import llnl.gnem.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author dodge1
 */
public class SeismogramFeatures implements Serializable {

    private static final long serialVersionUID = 5733235350232066271L;

    private final Double snr;
    private final Double amplitude;
    private final MomentEstimates moments;
    private final double pickEpochTime;
    private final double windowLength;
    private final double signalDuration;

    private final double kurtosis;
    private final double skewness;
    private final SignalPairStats.T0AndSigma t0AndSigma;
    private final Double tbp;
    private final Double f0;
    private final Double sigmaf;
    
    public double getFrequencyCentroid()
    {
        return f0;
    }

    public double getPickEpochTime() {
        return pickEpochTime;
    }

    public double getWindowLength() {
        return windowLength;
    }

    /**
     * @return the signalDuration
     */
    public double getSignalDuration() {
        return signalDuration;
    }

    public static enum FeatureType implements Feature {

        SNR, AMPLITUDE, TIME_CENTROID, TIME_SIGMA, TEMPORAL_SKEWNESS, TEMPORAL_KURTOSIS,
        FREQ_SIGMA, TBP,
        SKEWNESS, KURTOSIS, SIGNAL_LENGTH,FREQ_CENTROID

    };

    public SeismogramFeatures(TimeSeries ts, double pickEpochTime, double windowLength) {
        this.pickEpochTime = pickEpochTime;
        this.windowLength = windowLength;
    
        Epoch window = new Epoch(pickEpochTime, pickEpochTime + windowLength);
        TimeSeries copy = ts.crop(window);
        float[] data = copy.getData();
        t0AndSigma = SignalPairStats.computeT0AndSigma(data, ts.getDelta());
        signalDuration = t0AndSigma.getT0() + 2.5 * t0AndSigma.getSigma();
        double snrWinLen = signalDuration/2;
        snr = ts.getSnr(pickEpochTime, snrWinLen, snrWinLen);

        float[] ampData = ts.getSubSection(pickEpochTime, snrWinLen);
           
        amplitude = SeriesMath.getPeakToPeakAmplitude(ampData) / 2;

        moments = new MomentEstimates(data, ts.getDelta());

        TimeBandwidthComponents tbc = SignalPairStats.computeTimeBandwidthProduct(data, ts.getDelta());
        tbp = tbc != null ? tbc.getTBP() : null;
        f0 = tbc != null ? tbc.getFrequencyCentroid() : null;
        sigmaf = tbc != null ? tbc.getFrequencySigma() : null;

        SampleStatistics ss = new SampleStatistics(data);
        kurtosis = ss.getKurtosis();
        skewness = ss.getSkewness();
    }

    public String toHtmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");

        sb.append(String.format("SNR: %5.2f<br>", snr));
        sb.append(String.format("Amplitude: %5.2f<br>", amplitude));

        sb.append(String.format("Kurtosis: %5.2f<br>", getKurtosis()));
        sb.append(String.format("Skewness: %5.2f<br>", getSkewness()));

        sb.append(String.format("TemporalSkewness: %8.2f<br>", getTemporalSkewness()));
        sb.append(String.format("TemporalKurtosis: %8.2f<br>", getTemporalKurtosis()));
        sb.append(String.format("Freq-Sigma: %8.2f<br>", getFrequencySigma()));
        sb.append(String.format("TBP: %8.2f<br>", getTimeBandWidthProduct()));
        sb.append(String.format("TemporalEdgeTime: %8.2f<br>", getSignalDuration()));

        sb.append("</html>");

        return sb.toString();
    }

    public String toCSV() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%5.2f,", snr).trim());
        sb.append(String.format("%5.2f,", amplitude).trim());

        sb.append(String.format("%5.2f,", getKurtosis()).trim());
        sb.append(String.format("%5.2f,", getSkewness()).trim());

        sb.append(String.format("%8.2f,", getTemporalSkewness()).trim());
        sb.append(String.format("%8.2f,", getTemporalKurtosis()).trim());
        sb.append(String.format("%8.2f,", getFrequencySigma()).trim());
        sb.append(String.format("%8.2f,", getTimeBandWidthProduct()).trim());
        sb.append(String.format("%8.2f,", getSignalDuration()).trim());

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

    public Double getTimeCentroid(boolean asEpochTime) {
        return asEpochTime ? t0AndSigma.getT0() + pickEpochTime : t0AndSigma.getT0();
    }

    public Double getTimeCentroidSigma() {
        return moments != null ? moments.getSigma() : null;
    }

    public Double getSkewness() {
        return skewness;
    }

    public Double getKurtosis() {
        return kurtosis;
    }

    public Double getTemporalSkewness() {
        return moments != null ? moments.getTemporalSkewness() : null;
    }

    public Double getTemporalKurtosis() {
        return moments != null ? moments.getTemporalKurtosis() : null;
    }

    public Double getFrequencySigma() {
        return sigmaf;
    }

    public Double getTimeBandWidthProduct() {
        return tbp;
    }

    public void listFeatures() {
        for (FeatureType type : FeatureType.values()) {
            System.out.println(String.format("%s: %f", type.toString(), getValue(type)));
        }
    }

    public Double getValue(FeatureType type) {
        switch (type) {
            case TBP:
                return this.getTimeBandWidthProduct();
            case FREQ_SIGMA:
                return this.getFrequencySigma();
            case TEMPORAL_KURTOSIS:
                return this.getTemporalKurtosis();
            case TEMPORAL_SKEWNESS:
                return this.getTemporalSkewness();
            case TIME_SIGMA:
                return this.getTimeCentroidSigma();
            case TIME_CENTROID:
                return this.getTimeCentroid(false);
            case AMPLITUDE:
                return this.getAmplitude();
            case SNR:
                return this.getSnr();
            case KURTOSIS:
                return this.getKurtosis();
            case SKEWNESS:
                return this.getSkewness();
            case SIGNAL_LENGTH:
                return this.getSignalDuration();
            case FREQ_CENTROID:
                return this.getFrequencyCentroid();
            default:
                throw new IllegalArgumentException("Illegal FeatureType: " + type);
        }
    }

}
