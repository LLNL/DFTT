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
package llnl.gnem.dftt.core.waveform.classification;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.math3.complex.Complex;

import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.SeriesMath;
import llnl.gnem.dftt.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author dodge1
 */
public class Spectrogram implements Serializable{

    ArrayList<SpectrogramSlice> slices;

    static class SpectrogramSample implements Serializable{

        private final double freq;
        private final double power;

        public SpectrogramSample(double freq, double power) {
            this.freq = freq;
            this.power = power;
        }

        /**
         * @return the freq
         */
        public double getFreq() {
            return freq;
        }

        /**
         * @return the power
         */
        public double getPower() {
            return power;
        }

    }

    static class SpectrogramSlice implements Serializable{

        double time;
        ArrayList<SpectrogramSample> samples;

        SpectrogramSlice(double time) {
            this.time = time;
            samples = new ArrayList<>();
        }

        private void addSample(SpectrogramSample sample) {
            samples.add(sample);
        }

        double[] getFrequencyValues() {
            double[] result = new double[samples.size()];
            for (int j = 0; j < samples.size(); ++j) {
                result[j] = samples.get(j).freq;
            }
            return result;
        }

        double getMaxFrequency() {
            double max = 0;
            for (int j = 0; j < samples.size(); ++j) {
                if (samples.get(j).freq > max) {
                    max = samples.get(j).freq;
                }
            }
            return max;
        }
    }

    public Spectrogram(TimeSeries ts, Epoch requestedInterval, int numTimeSlices, double sliceDuration,  double minFreq, double maxFreq) {
        slices = new ArrayList<>();

        double duration = requestedInterval.duration();
        double timeStep = duration / numTimeSlices;
        for (int j = 0; j < numTimeSlices; ++j) {
            double sliceStart = timeStep * j - sliceDuration / 2;
            double t0 = sliceStart + sliceDuration / 2 + requestedInterval.getTime().getEpochTime();
            SpectrogramSlice slice = new SpectrogramSlice(t0);
            float[] data = ts.getSubSection(requestedInterval.getTime().getEpochTime() + sliceStart, sliceDuration);
            SeriesMath.Taper(data, 2.0);
            Complex[] result = SeriesMath.fft(data);
            int N = result.length / 2;
            double fny = 1.0 / ts.getDelta() / 2;
            double df = fny / N;
            for (int m = 0; m <= N; ++m) {
                Complex tmp = result[m];
                double v = tmp.abs();
                double power = v * v;
                double freq = m * df;
                if (freq <= maxFreq && freq >= minFreq) {
                    SpectrogramSample sample = new SpectrogramSample(freq, power);
                    slice.addSample(sample);
                }
            }
            slices.add(slice);
        }
    }

    public double[] getTimeValues() {
        double[] result = new double[slices.size()];
        for (int j = 0; j < slices.size(); ++j) {
            result[j] = slices.get(j).time;
        }
        return result;
    }

    public double[] getFrequencyValues() {
        return slices.get(0).getFrequencyValues();
    }

    public double getMaxFrequency() {

        return slices.get(0).getMaxFrequency();
    }

    public double[][] getPowerValues() {
        int ntimes = slices.size();
        int nfreqs = slices.get(0).samples.size();
        double[][] result = new double[nfreqs][ntimes];
        for (int j = 0; j < ntimes; ++j) {
            SpectrogramSlice slice = slices.get(j);
            for (int m = 0; m < nfreqs; ++m) {
                result[m][j] = slice.samples.get(m).power;
            }
        }
        return result;
    }
    
}
