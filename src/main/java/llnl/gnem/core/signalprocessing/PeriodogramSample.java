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
package llnl.gnem.core.signalprocessing;

import java.io.PrintStream;

/**
 *
 * @author dodge1
 */
public class PeriodogramSample {

    private final double f;
    private final double v;

    public PeriodogramSample(SpectralSample s) {
        f = s.getFrequency();
        double tmp = s.getAbsValue();
        v = tmp * tmp;
    }

    public PeriodogramSample(double f, double v) {
        this.f = f;
        this.v = v;
    }

    public PeriodogramSample(SpectralSample sX, SpectralSample sY) {
        double fX = sX.getFrequency();
        double fY = sY.getFrequency();
        if (fX != fY) {
            throw new IllegalStateException("Samples are at different frequencies!");
        }
        f = fX;
        v = sX.conjugate().times(sY).getAbsValue();
    }

    @Override
    public String toString() {
        return String.format("F = %f, V = %f", f, v);
    }

    /**
     * @return the f
     */
    public double getFrequency() {
        return f;
    }

    /**
     * @return the v
     */
    public double getValue() {
        return v;
    }

    public void print(PrintStream out) {
        out.println(this);
    }

}
