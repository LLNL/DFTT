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
