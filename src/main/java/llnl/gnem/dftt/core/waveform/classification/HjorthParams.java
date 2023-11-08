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

import llnl.gnem.dftt.core.util.SeriesMath;

/**
 *
 * @author dodge1
 */
public class HjorthParams {

    private final double activity;
    private final double mobility;
    private final double complexity;

    public HjorthParams(double activity, double mobility, double complexity) {
        this.activity = activity;
        this.mobility = mobility;
        this.complexity = complexity;
    }

    public HjorthParams(final float[] fOfT, final double dt) {
        final float[] f2OfT = fOfT.clone();

        SeriesMath.Function activityKernel = new SeriesMath.Function() {
            @Override
            public double eval(int index) {
                return f2OfT[index];
            }
        };

        double area = SeriesMath.getDefiniteIntegral(activityKernel, dt, 0, f2OfT.length - 1);
        for (int j = 0; j < f2OfT.length; ++j) {
            f2OfT[j] = f2OfT[j] * f2OfT[j] / (float) area;
        }

        double T = dt * (fOfT.length - 1);
        double tmp = T > 0 ? SeriesMath.getDefiniteIntegral(activityKernel, dt, 0, f2OfT.length - 1) / T : 0;
        activity = Double.isNaN(tmp) ? 0 : tmp;

        final float[] deriv = fOfT.clone();
        for (int j = 0; j < deriv.length; ++j) {
            deriv[j] = deriv[j] / (float) area;
        }
        SeriesMath.Differentiate(deriv, 1.0 / dt);

        SeriesMath.Function mobilityKernel = new SeriesMath.Function() {
            @Override
            public double eval(int index) {
                return deriv[index] * deriv[index];
            }
        };

        tmp = T > 0 ? SeriesMath.getDefiniteIntegral(mobilityKernel, dt, 0, deriv.length - 1) / T : 0;
        mobility = Double.isNaN(tmp) ? 0 : tmp;
        SeriesMath.Differentiate(deriv, 1.0 / dt);

        SeriesMath.Function complexityKernel = new SeriesMath.Function() {
            @Override
            public double eval(int index) {
                return deriv[index] * deriv[index];
            }
        };

        tmp = T > 0 ? SeriesMath.getDefiniteIntegral(complexityKernel, dt, 0, deriv.length - 1) / T : 0;
        complexity = Double.isNaN(tmp) ? 0 : tmp;
    }

    /**
     * @return the activity
     */
    public double getActivity() {
        return activity;
    }

    /**
     * @return the mobility
     */
    public double getMobility() {
        return mobility;
    }

    /**
     * @return the complexity
     */
    public double getComplexity() {
        return complexity;
    }

}
