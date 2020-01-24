package llnl.gnem.core.waveform.qc;

import java.util.List;

import llnl.gnem.core.waveform.seismogram.TimeSeries;
import llnl.gnem.core.waveform.classification.Feature;
import llnl.gnem.core.waveform.classification.HjorthParams;
import llnl.gnem.core.waveform.classification.MomentEstimates;
import llnl.gnem.core.waveform.classification.SeismogramFeatures;
import llnl.gnem.core.waveform.classification.SpectralEdgeFrequency;

/**
 *
 * @author dodge1
 */
public abstract class QCFeatures extends SeismogramFeatures {

    public QCFeatures(TimeSeries seismogram, double pickEpochTime, double duration) {
        super(seismogram, pickEpochTime, duration);
    }

    public QCFeatures(double pickEpochTime,
            double windowDuration,
            double snr,
            double amplitude,
            double t0,
            double tSigma,
            double temporalSkewness,
            double temporalKurtosis,
            double fSigma,
            double activity,
            double mobility,
            double complexity,
            double spectralEdgeFreq,
            double energyEdgeTime,
            double skewness,
            double kurtosis,
            int numModes,
            double omega0) {
        super(snr,
                amplitude,
                new MomentEstimates(t0, tSigma, temporalSkewness, temporalKurtosis),
                new HjorthParams(activity, mobility, complexity),
                new SpectralEdgeFrequency(spectralEdgeFreq),
                pickEpochTime, windowDuration, skewness, kurtosis, numModes, t0, tSigma, omega0, fSigma);

    }

    public abstract List<Feature> getAvailableFeatures();

    public abstract List<Double> getValues();

    public abstract double getValue(Feature feature);
}
