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

import llnl.gnem.core.signalprocessing.TimeFreqStatistics;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.seriesMathHelpers.SampleStatistics;
import llnl.gnem.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author dodge1
 */
public class TimeSeriesFeatures {

    private static final double TAPER_PERCENT = 2.0;
    private static final int SMOOTHING_FACTOR = 30;
    private static final double ENVELOPE_AREA_TERM_FRACTION = 0.8;
    private static final double LW_POST_PICK_SECONDS = 30.0;
    private static final double ADJUSTMENT_INCREMENT_SECONDS = 0.2;
    private static final int ADJUSTMENT_MAX_SECONDS = 10;
    private static final int ADJUSTMENT_POST_WIN_SECONDS = 2;
    private static final int ADJUSTMENT_PRE_WIN_SECONDS = 10;
    private static final double TIME_SLICE_DURATION_SECONDS = 10.0;
    private static final int NUM_TIME_SLICES = 200;
    private static final int SNR_POSTWIN_SECONDS = 30;
    private static final int SNR_PREWIN_SECONDS = 10;

    private final double timeCentroid;
    private final double sigmaT;
    private final double sigmaF;
    private final double timeBandwidthProduct;
    private final double temporalKurtosis;
    private final double temporalSkewness;
    private final double kurtosis;
    private final double skewness;
    private final Double amplitude;
    private final double onsetAdjustment;
    private final Epoch analysisEpoch;
    private final double signalStartOffset;
    private final float[] envelope;
    private final Spectrogram spectrogram;
    private final double snr;
    private final double signalEnd;

    public static enum FeatureType {

        SNR, AMPLITUDE, TIME_CENTROID, TIME_SIGMA, TEMPORAL_SKEWNESS, TEMPORAL_KURTOSIS,
        FREQ_SIGMA, TBP, SKEWNESS, KURTOSIS, DURATION
    };

    public FeatureType[] getAvailableFeatures() {
        return FeatureType.values();
    }

    public String toHtmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");

        sb.append(String.format("Onset Adjustment: %5.2f<br>", getOnsetAdjustment()));
        sb.append(String.format("SNR: %5.2f<br>", snr));
        sb.append(String.format("Amplitude: %5.2f<br>", amplitude));

        sb.append(String.format("Kurtosis: %5.2f<br>", getKurtosis()));
        sb.append(String.format("Skewness: %5.2f<br>", getSkewness()));

        sb.append(String.format("TemporalSkewness: %8.2f<br>", getTemporalSkewness()));
        sb.append(String.format("TemporalKurtosis: %8.2f<br>", getTemporalKurtosis()));
        sb.append(String.format("Time-Sigma: %8.2f<br>", getTimeSigma()));
        sb.append(String.format("Freq-Sigma: %8.2f<br>", getFrequencySigma()));
        sb.append(String.format("TBP: %8.2f<br>", getTimeBandwidthProduct()));
        sb.append(String.format("Signal End: %8.2f<br>", getSignalEnd()));

        sb.append("</html>");

        return sb.toString();
    }

    public TimeSeriesFeatures(TimeSeries ts, Epoch analysisEpoch, double signalStartOffset, double minFreq, double maxFreq) {
        if (!ts.getEpoch().isSuperset(analysisEpoch)) {
            throw new IllegalStateException("Feature epoch is not a subset of time series epoch!");
        }

        this.signalStartOffset = signalStartOffset;
        onsetAdjustment = maybeAdjustOnset(ts, analysisEpoch, signalStartOffset);
        this.analysisEpoch = onsetAdjustment == 0 ? new Epoch(analysisEpoch) : new Epoch(analysisEpoch.getTime().getEpochTime() + onsetAdjustment, analysisEpoch.getEnd() + onsetAdjustment);
        TimeSeries copy = new TimeSeries(ts);
        copy.trimTo(analysisEpoch);
        copy.RemoveMean();
        copy.Taper(TAPER_PERCENT);

        spectrogram = new Spectrogram(copy, analysisEpoch, NUM_TIME_SLICES, TIME_SLICE_DURATION_SECONDS, minFreq, maxFreq);

        envelope = SeriesMath.MeanSmooth(SeriesMath.envelope(copy.getData()), SMOOTHING_FACTOR);
        int termIndex = getTermination(envelope, copy.getDelta());
        double termTime = copy.getTimeAsDouble() + copy.getDelta() * termIndex;
        signalEnd = copy.getDelta() * termIndex;
        double signalStartTime = this.analysisEpoch.getTime().getEpochTime() + signalStartOffset;
        Epoch signalEpoch = new Epoch(signalStartTime, termTime);
        TimeSeries signal = copy.crop(signalEpoch);

        snr = computeSNR(copy, signalStartTime);
        TimeFreqStatistics tf = new TimeFreqStatistics(signal.getData(), signal.getDelta());
        timeCentroid = tf.getT0();
        sigmaT = tf.getSigma();
        sigmaF = tf.getSigmaf();
        timeBandwidthProduct = sigmaT * sigmaF;
        MomentEstimates me = new MomentEstimates(signal.getData(), signal.getDelta());
        temporalKurtosis = me.getTemporalKurtosis();
        temporalSkewness = me.getTemporalSkewness();

        SampleStatistics ss = new SampleStatistics(signal.getData());
        kurtosis = ss.getKurtosis();
        skewness = ss.getSkewness();

        amplitude = computeAmplitude(signal);
    }

    private double maybeAdjustOnset(TimeSeries ts, Epoch epoch, double offsetSeconds) {
        double preWinLen = ADJUSTMENT_PRE_WIN_SECONDS;
        double postWinLen = ADJUSTMENT_POST_WIN_SECONDS;
        double requestedTestSeconds = ADJUSTMENT_MAX_SECONDS;
        double adjustmentAmount = ADJUSTMENT_INCREMENT_SECONDS;

        double pickTime = epoch.getTime().getEpochTime() + offsetSeconds;
        double prePickSeconds = pickTime - ts.getTimeAsDouble();
        double allowableBackupSeconds = requestedTestSeconds <= prePickSeconds + preWinLen ? requestedTestSeconds : prePickSeconds - preWinLen;

        double postPickSeconds = ts.getEndtimeAsDouble() - pickTime;
        double allowableForwardSeconds = requestedTestSeconds + postWinLen <= postPickSeconds ? requestedTestSeconds : postPickSeconds - postWinLen;

        double unadjustedSNR = ts.getSnr(pickTime, preWinLen, postWinLen);
        double maxSNR = unadjustedSNR;
        double timeOfMax = pickTime;
        for (double aTime = pickTime - allowableBackupSeconds; aTime <= pickTime + allowableForwardSeconds; aTime += adjustmentAmount) {
            double snrEstimate = ts.getSnr(aTime, preWinLen, postWinLen);
            if (snrEstimate > maxSNR) {
                maxSNR = snrEstimate;
                timeOfMax = aTime;
            }
        }

        if (maxSNR >= unadjustedSNR + 1 && maxSNR >= 3) {
            return timeOfMax - pickTime;
        } else {
            return 0;
        }
    }

    private int getTermination(float[] data, double dt) {
        double total = 0;

        for (int j = 1; j < data.length; ++j) {
            double s = dt * (data[j - 1] + data[j]) / 2;
            total += s;
        }

        double sum = 0;
        for (int j = 1; j < data.length; ++j) {
            double s = dt * (data[j - 1] + data[j]) / 2;
            sum += s;
            if (sum >= ENVELOPE_AREA_TERM_FRACTION * total) {
                return j;
            }
        }
        return data.length - 1;
    }

    private Double computeAmplitude(TimeSeries seismogram) {
        double maxWindow = seismogram.getLengthInSeconds();
        double postWinLen = Math.min(maxWindow, LW_POST_PICK_SECONDS);

        if (postWinLen > 0) {
            float[] data = seismogram.getSubSection(seismogram.getTime(), postWinLen);
            return SeriesMath.getPeakToPeakAmplitude(data) / 2;
        } else {
            return null;
        }
    }

    public Spectrogram getSpectrogram() {
        return spectrogram;
    }

    public double getEnvelopeStartTime() {
        return analysisEpoch.getTime().getEpochTime();
    }

    public float[] getEnvelope() {
        return envelope.clone();
    }

    public double getSignalStartAdjustment() {
        return onsetAdjustment;
    }

    public Epoch getAnalysisEpoch() {
        return analysisEpoch;
    }

    private double computeSNR(TimeSeries copy, double signalStartTime) {
        double prewin = SNR_PREWIN_SECONDS;
        double postWin = SNR_POSTWIN_SECONDS;
        return copy.getSnr(signalStartTime, prewin, postWin);
    }

    /**
     * @return the timeCentroid
     */
    public double getTimeCentroid() {
        return timeCentroid;
    }

    /**
     * @return the sigmaT
     */
    public double getSigmaT() {
        return sigmaT;
    }

    /**
     * @return the sigmaF
     */
    public double getSigmaF() {
        return sigmaF;
    }

    /**
     * @return the timeBandwidthProduct
     */
    public double getTimeBandwidthProduct() {
        return timeBandwidthProduct;
    }


    /**
     * @return the temporalKurtosis
     */
    public double getTemporalKurtosis() {
        return temporalKurtosis;
    }

    /**
     * @return the temporalSkewness
     */
    public double getTemporalSkewness() {
        return temporalSkewness;
    }

    /**
     * @return the kurtosis
     */
    public double getKurtosis() {
        return kurtosis;
    }

    /**
     * @return the skewness
     */
    public double getSkewness() {
        return skewness;
    }

    /**
     * @return the amplitude
     */
    public Double getAmplitude() {
        return amplitude;
    }

    /**
     * @return the onsetAdjustment
     */
    public double getOnsetAdjustment() {
        return onsetAdjustment;
    }

    /**
     * @return the signalStartOffset
     */
    public double getSignalStartOffset() {
        return signalStartOffset;
    }

    /**
     * @return the snrEstimate
     */
    public double getSnr() {
        return snr;
    }

    /**
     * @return the signalEnd
     */
    public double getSignalEnd() {
        return signalEnd;
    }

    public double getFrequencySigma() {
        return Math.sqrt(sigmaF);
    }

    public double getTimeSigma() {
        return Math.sqrt(sigmaT);
    }

    public double getValue(FeatureType type) {
        switch (type) {
            case TBP:
                return this.getTimeBandwidthProduct();
            case FREQ_SIGMA:
                return this.getFrequencySigma();
            case TEMPORAL_KURTOSIS:
                return this.getTemporalKurtosis();
            case TEMPORAL_SKEWNESS:
                return this.getTemporalSkewness();
            case TIME_SIGMA:
                return this.getTimeSigma();
            case TIME_CENTROID:
                return this.getTimeCentroid();
            case AMPLITUDE:
                return this.getAmplitude();
            case SNR:
                return this.getSnr();
            case KURTOSIS:
                return this.getKurtosis();
            case SKEWNESS:
                return this.getSkewness();
            case DURATION:
                return this.getSignalEnd() - signalStartOffset;
            default:
                throw new IllegalArgumentException("Illegal FeatureType: " + type);
        }
    }

}
