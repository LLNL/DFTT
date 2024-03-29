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
package llnl.gnem.dftt.core.signalprocessing.statistics;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.special.Erf;

/**
 *
 * @author dodge1
 */
public class EMGDistribution {

    private final double mu;
    private final double sigma;
    private final double lambda;

    public EMGDistribution(double mu, double sigma, double lambda) {
        this.mu = mu;
        this.sigma = sigma;
        this.lambda = lambda;
    }

    @Override
    public String toString() {
        return "EMGDistribution{" + "mu=" + mu + ", sigma=" + sigma + ", lambda=" + lambda + '}';
    }

    public double getDensity(double x) {
        double t1 = 2 * mu + lambda * sigma * sigma - 2 * x;
        double t2 = (mu + lambda * sigma * sigma - x) / Math.sqrt(2.0) / sigma;
        return lambda / 2 * Math.exp(lambda / 2 * t1) * Erf.erfc(t2);
    }

    public float[] getDensity(float[] x) {
        float[] result = new float[x.length];
        for (int j = 0; j < x.length; ++j) {
            result[j] = (float) getDensity(x[j]);
        }
        return result;
    }

    /**
     * @return the mu
     */
    public double getMu() {
        return mu;
    }

    /**
     * @return the sigma
     */
    public double getSigma() {
        return sigma;
    }

    /**
     * @return the lambda
     */
    public double getLambda() {
        return lambda;
    }

    public double getMean() {
        return mu + 1 / lambda;
    }

    public double getCumulativeDensity(double c) {
        if (c < 0 || c > 1) {
            throw new IllegalArgumentException("Argument is out of range!");
        }
        double u = lambda * (c - mu);
        double v = lambda * sigma;
        NormalDistribution nd = new NormalDistribution(0.0, v);
        double phi1 = nd.cumulativeProbability(u);
        nd = new NormalDistribution(v * v, v);
        double phi2 = nd.cumulativeProbability(u);
        double t0 = v * v / 2 + Math.log(phi2) - u;
        return phi1 - Math.exp(t0);
    }

    public double getInverseCumulativeDensity(double p) {
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException("Argument is out of range!");
        }
        int nMax = 100;
        double tol = 0.00001;
        double lower = 0;
        double mid = 0.5;
        double upper = 1;

        int j = 0;
        while (j < nMax) {
            mid = (lower + upper) / 2;
            double pMid = getCumulativeDensity(mid);
            if ((upper - lower) / 2 < tol) {
                return mid;
            }
            if (pMid < p) {
                lower = mid;
            } else if (pMid > p) {
                upper = mid;
            } else {
                return mid;
            }
            ++j;
        }

        return mid;
    }
}
