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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import llnl.gnem.core.util.BandInfo;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.Passband;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.seismogram.TimeSeries;
import llnl.gnem.core.waveform.classification.Feature;

/**
 *
 * @author addair1
 */
public class FilteredFeatures extends QCFeatures {

    private final RawFeatures rawFeatures;
    private List<Double> values;

    public FilteredFeatures(TimeSeries filtered, TimeSeries raw, double pickEpochTime, double duration) {
        this(filtered, pickEpochTime, duration, new RawFeatures(raw, pickEpochTime, duration));
    }

    public FilteredFeatures(TimeSeries filtered, double pickEpochTime, double duration, RawFeatures rawFeatures) {
        super(filtered, pickEpochTime, duration);
        this.rawFeatures = rawFeatures;

        // populate values field for use in getValues method
        List<Feature> features = getFeatures();
        values = new ArrayList<>(features.size());
        for (FeatureType feature : FeatureType.values()) {
            values.add(this.getValue(feature));
        }

    }

    public FilteredFeatures(double pickEpochTime, double windowDuration, double snr, double amplitude,
            double t0, double tSigma, double temporalSkewness, double temporalKurtosis, 
            double fSigma, double tbp,
            double activity, double mobility,
            double complexity, double spectralEdgeFreq, double energyEdgeTime, RawFeatures rawFeatures,
            double skewness, double kurtosis, int numModes, double omega0) {
        super(pickEpochTime,
                windowDuration,
                snr,
                amplitude,
                t0,
                tSigma,
                temporalSkewness,
                temporalKurtosis,
                fSigma,
                activity,
                mobility,
                complexity,
                spectralEdgeFreq,
                energyEdgeTime,
                skewness,
                kurtosis,
                numModes, omega0);

        values = Arrays.asList(snr,
                amplitude,
                t0,
                tSigma,
                temporalSkewness,
                temporalKurtosis,
                fSigma,
                tbp,
                activity,
                mobility,
                complexity,
                spectralEdgeFreq);
        this.rawFeatures = rawFeatures;
    }

    public static FilteredFeatures createFrom(CssSeismogram seismogram, Epoch epoch, BandInfo band) {
        CssSeismogram raw = new CssSeismogram(seismogram);
        raw.trimTo(epoch);

        CssSeismogram filtered = new CssSeismogram(seismogram);
        filtered.removeTrend();
        filtered.triangleTaper(5.0);

        filtered.filter(2, Passband.BAND_PASS, band.getLowpass(), band.getHighpass(), false);
        filtered.trimTo(epoch);

        return new FilteredFeatures(filtered, raw, epoch.getStart(), epoch.duration());
    }

    public static List<Feature> getFeatures() {
        List<Feature> features = new ArrayList<>();
        features.addAll(Arrays.asList(QCFeatures.FeatureType.values()));
        return features;
    }

    @Override
    public List<Feature> getAvailableFeatures() {
        return getFeatures();
    }

    public RawFeatures getRawFeatures() {
        return rawFeatures;
    }

    @Override
    public List<Double> getValues() {
        return values;
    }

    @Override
    public double getValue(Feature feature) {
        return -1;
    }
}
