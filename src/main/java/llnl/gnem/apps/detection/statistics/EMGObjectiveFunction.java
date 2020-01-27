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
package llnl.gnem.apps.detection.statistics;

import llnl.gnem.core.optimization.ObjectiveFunction;
import llnl.gnem.core.signalprocessing.statistics.EMGDistribution;

/**
 *
 * @author dodge1
 */
public class EMGObjectiveFunction implements ObjectiveFunction {

    private final float[] bins;
    private final float[] values;

    public EMGObjectiveFunction(float[] bins, float[] values) {
        this.bins = bins.clone();
        this.values = values.clone();
    }

    @Override
    public int dimension() {
        return 3;
    }

    @Override
    public double evaluate(float[] p) {
        EMGDistribution dist = new EMGDistribution(p[0], p[1], p[2]);
        if (p[0] <= 0 || p[1] <= 0 || p[2] <= 0) {
            return 9999.9;
        }
        float[] est = dist.getDensity(bins);
        float sum = 0;
        for (int j = 0; j < est.length; ++j) {
            float res = est[j] - values[j];
            sum += res * res;
        }
        if (Float.isNaN(sum)) {
            return 9999.9;
        } else {
            return Math.sqrt(sum) / est.length;
        }
    }

    @Override
    public float[] gradient(float[] parameters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
