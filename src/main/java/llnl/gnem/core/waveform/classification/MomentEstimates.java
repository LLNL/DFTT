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

import llnl.gnem.core.util.SeriesMath;

/**
 *
 * @author dodge1
 */
public class MomentEstimates {

    private final Double t0;
    private final Double sigma;
    private final Double temporalSkewness;
    private final Double temporalKurtosis;

    public MomentEstimates(double t0, double sigma, double skewness, double kurtosis) {
        this.t0 = t0;
        this.sigma = sigma;
        this.temporalSkewness = skewness;
        this.temporalKurtosis = kurtosis;
    }

    @Override
    public String toString() {
        return "MomentEstimates{" + "t0=" + t0 + ", sigma=" + sigma + ", temporalSkewness=" + temporalSkewness + ", temporalKurtosis=" + temporalKurtosis + '}';
    }

    public static Double computeCentroid(float[] x, double sampleInterval) {
        SeriesMath.Function energyKernel = (int index) -> Math.abs(x[index]);
        double energy = SeriesMath.getDefiniteIntegral(energyKernel, sampleInterval, 0, x.length - 1);
        if (energy <= 0) {
            return null;
        }
        SeriesMath.Function centroidKernel = (int index) -> (index * sampleInterval) * Math.abs(x[index]);
        double rawCentroid = SeriesMath.getDefiniteIntegral(centroidKernel, sampleInterval, 0, x.length - 1);
        return rawCentroid / energy;
    }

    public static Double computeSigma(float[] fOfT, double dt, Double centroid) {
        if (centroid == null) {
            return null;
        }
        final float[] x = SeriesMath.abs(fOfT);
        SeriesMath.Function energyKernel = (int index) -> x[index];
        SeriesMath.Function varianceKernel = (int idx) -> {
            double tmt0 = (((idx * dt)) - centroid);
            return (tmt0 * tmt0 * x[idx]);
        };
        double energy = SeriesMath.getDefiniteIntegral(energyKernel, dt, 0, x.length - 1);
        if (energy <= 0) {
            return null;
        }
        double variance = SeriesMath.getDefiniteIntegral(varianceKernel, dt, 0, x.length - 1) / energy;
        return Math.sqrt(variance);
    }

    public static Double computeSampleSkewness(float[] fOfT, double dt, Double firstMoment, Double sigma) {
        if (firstMoment == null || sigma == null || sigma <= 0) {
            return null;
        }
        final float[] x = SeriesMath.abs(fOfT);
        SeriesMath.Function energyKernel = (int index) -> x[index];
        double energy = SeriesMath.getDefiniteIntegral(energyKernel, dt, 0, x.length - 1);
        if (energy <= 0) {
            return null;
        }
        SeriesMath.MultiplyScalar(x, 1 / energy);
        SeriesMath.Function skewnessKernel = (int idx) -> {
            double tmt0 = (((idx * dt)) - firstMoment);
            return (tmt0 * tmt0 * tmt0 * (x[idx]));
        };
        return SeriesMath.getDefiniteIntegral(skewnessKernel, dt, 0, x.length - 1) / sigma / sigma / sigma;
    }

    public static Double computeSampleExcessKurtosis(float[] fOfT, double dt, Double firstMoment, Double sigma) {
        if (firstMoment == null || sigma == null || sigma <= 0) {
            return null;
        }
        final float[] x = SeriesMath.abs(fOfT);
        SeriesMath.Function energyKernel = (int index) -> x[index];
        double energy = SeriesMath.getDefiniteIntegral(energyKernel, dt, 0, x.length - 1);
        if (energy <= 0) {
            return null;
        }
        SeriesMath.MultiplyScalar(x, 1 / energy);
        SeriesMath.Function kurtosisKernel = (int idx) -> {
            double tmt0 = (((idx * dt)) - firstMoment);
            return (tmt0 * tmt0 * tmt0 * tmt0 * (x[idx]));
        };
        return SeriesMath.getDefiniteIntegral(kurtosisKernel, dt, 0, x.length - 1) / sigma / sigma / sigma / sigma - 3;
    }

    public MomentEstimates(final float[] fOfT, final double dt) {
        t0 = MomentEstimates.computeCentroid(fOfT, dt);
        sigma = MomentEstimates.computeSigma(fOfT, dt, t0);

        temporalSkewness = computeSampleSkewness(fOfT, dt, t0, sigma);

        temporalKurtosis = computeSampleExcessKurtosis(fOfT, dt, t0, sigma);

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
     * @return the temporalSkewness
     */
    public Double getTemporalSkewness() {
        return temporalSkewness;
    }

    /**
     * @return the temporalKurtosis
     */
    public Double getTemporalKurtosis() {
        return temporalKurtosis;
    }

}
