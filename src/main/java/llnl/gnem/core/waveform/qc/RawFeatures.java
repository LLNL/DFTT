package llnl.gnem.core.waveform.qc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.waveform.seismogram.TimeSeries;
import llnl.gnem.core.waveform.classification.Feature;
import llnl.gnem.core.waveform.classification.SeismogramFeatures;

/**
 *
 * @author addair1
 */
public class RawFeatures extends QCFeatures {

    private final double extr;
    private final RawAttributes rawAttributes;
    private final List<Double> rawValues;

    public RawFeatures(TimeSeries seismogram, double pickEpochTime, double duration) {
        super(seismogram, pickEpochTime, duration);
        TimeSeries cut = makeTrimmedCopy(seismogram, pickEpochTime, duration);
        this.rawAttributes = new RawAttributes(cut);
        extr = cut.getNsamp() > 3 ? cut.computeExtremeStat() : 0;

        // create the rawValues field
        List<Feature> features = getFeatures();
        ArrayList<Double> vals = new ArrayList<>(features.size());
        for (FeatureType feature : FeatureType.values()) {
            vals.add(this.getValue(feature));
        }
        vals.add((double) rawAttributes.getNglitches());
        vals.add(rawAttributes.getDistinctValueRatio());
        vals.add((double) rawAttributes.getNumDiscontinuities());
        vals.add(rawAttributes.getAvgDiscontinuityValue());
        vals.add(rawAttributes.getMaxDiscontinuityValue());
        vals.add(extr);

        this.rawValues = vals;
    }

    public RawFeatures(double pickEpochTime, double windowDuration, double snr,
            double amplitude, double t0, double tSigma,
            double temporalSkewness, double temporalKurtosis,
            double temporalHyperKurtosis, double temporalHyperFlatness,
            double fSigma, double tbp,
            double packetSigma,
            double activity, double mobility, double complexity,
            double spectralEdgeFreq, double energyEdgeTime, double extr, int nglitch,
            double dropoutFraction, double dropoutImportance,
            double distinctValueRatio, int numDiscontinuities,
            double discontinuityAverageValue, double discontinuityMaxValue,
            double skewness, double kurtosis, int numModes, double omega0) {
        super(pickEpochTime, windowDuration,
                snr, amplitude, t0, tSigma, temporalSkewness, temporalKurtosis,
                fSigma, activity, mobility, complexity,
                spectralEdgeFreq, energyEdgeTime, skewness, kurtosis, numModes, omega0);

        this.rawValues = Arrays.asList(new Double[]{
            snr,
            amplitude,
            t0,
            tSigma,
            temporalSkewness,
            temporalKurtosis,
            temporalHyperKurtosis,
            temporalHyperFlatness,
            fSigma,
            tbp,
            packetSigma,
            activity,
            mobility,
            complexity,
            spectralEdgeFreq,
            energyEdgeTime,
            skewness,
            kurtosis,
            (double) numModes,
            (double) nglitch,
            dropoutFraction,
            dropoutImportance,
            distinctValueRatio,
            (double) numDiscontinuities,
            discontinuityAverageValue,
            discontinuityMaxValue,
            extr});

        rawAttributes = new RawAttributes(nglitch, distinctValueRatio, numDiscontinuities, discontinuityAverageValue, discontinuityMaxValue, dropoutFraction, dropoutImportance);
        this.extr = extr;
    }

    public static int featureCount() {
        return QCFeatures.FeatureType.values().length + RawFeature.values().length;
    }

    public static List<Feature> getFeatures() {
        List<Feature> features = new ArrayList<>();
        features.addAll(Arrays.asList(QCFeatures.FeatureType.values())); //19
        features.addAll(Arrays.asList(RawFeature.values())); // 8
        return features;
    }

    private static TimeSeries makeTrimmedCopy(TimeSeries ts, Double pickEpochTime, double duration) {
        TimeSeries copy = new TimeSeries(ts);
        Epoch window = new Epoch(pickEpochTime, pickEpochTime + duration);
        return copy.crop(window);
    }

    @Override
    public List<Feature> getAvailableFeatures() {
        return getFeatures();
    }

    public double getDiscontinuityAverageValue() {
        return rawAttributes.getAvgDiscontinuityValue();
    }

    public double getDiscontinuityMaxValue() {
        return rawAttributes.getMaxDiscontinuityValue();
    }

    public double getDistinctValueRatio() {
        return rawAttributes.getDistinctValueRatio();
    }

    public double getExtremeStat() {
        return extr;
    }

    public int getNumDiscontinuities() {
        return rawAttributes.getNumDiscontinuities();
    }

    public int getNumGlitches() {
        return rawAttributes.getNglitches();
    }

    @Override
    public List<Double> getValues() {
        return rawValues;
    }

    @Override
    public double getValue(Feature feature) {
        if (feature.name().equals(RawFeature.DISC_AVG_VALUE.name())) {
            return getDiscontinuityAverageValue();
        } else if (feature.name().equals(RawFeature.NGLITCH.name())) {
            return this.getNumGlitches();
        } else if (feature.name().equals(RawFeature.VAL_RATIO.name())) {
            return this.getDistinctValueRatio();
        } else if (feature.name().equals(RawFeature.NUM_DISC.name())) {
            return this.getNumDiscontinuities();
        } else if (feature.name().equals(RawFeature.DISC_MAX_VALUE.name())) {
            return this.getDiscontinuityMaxValue();
        } else if (feature.name().equals(RawFeature.EXTREME_STAT.name())) {
            return this.getExtremeStat();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.SPECTRAL_EDGE_FREQ.name())) {
            return this.getSpectralEdgeFrequency();

        } else if (feature.name().equals(SeismogramFeatures.FeatureType.TBP.name())) {
            return this.getTimeBandWidthProduct();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.FREQ_SIGMA.name())) {
            return this.getFrequencySigma();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.COMPLEXITY.name())) {
            return this.getComplexity();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.MOBILITY.name())) {
            return this.getMobility();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.ACTIVITY.name())) {
            return this.getActivity();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.TEMPORAL_KURTOSIS.name())) {
            return this.getTemporalKurtosis();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.TEMPORAL_SKEWNESS.name())) {
            return this.getTemporalSkewness();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.TIME_SIGMA.name())) {
            return this.getTimeCentroidSigma();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.TIME_CENTROID.name())) {
            return this.getTimeCentroid(false);
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.AMPLITUDE.name())) {
            return this.getAmplitude();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.SNR.name())) {
            return this.getSnr();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.TEMPORAL_EDGE_TIME.name())) {
            return this.getSignalEnd();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.KURTOSIS.name())) {
            return this.getKurtosis();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.SKEWNESS.name())) {
            return this.getSkewness();
        } else if (feature.name().equals(SeismogramFeatures.FeatureType.NUM_MODES.name())) {
            return this.getNumModes();
        } else {
            throw new IllegalArgumentException("Unrecognized feature: " + feature);
        }

    }

    public enum RawFeature implements Feature {

        NGLITCH, DROPOUT_FRAC, DROPOUT_IMP, VAL_RATIO, NUM_DISC, DISC_AVG_VALUE, DISC_MAX_VALUE, EXTREME_STAT
    }
}
