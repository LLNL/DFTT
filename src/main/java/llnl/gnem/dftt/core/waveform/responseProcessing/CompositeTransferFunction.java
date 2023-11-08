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
package llnl.gnem.dftt.core.waveform.responseProcessing;

import org.apache.commons.math3.complex.Complex;

/**
 *
 * @author dodge1
 */
public class CompositeTransferFunction {

    private final double[] xre;
    private final double[] xim;
    private final double dataMultiplier;
    private final float nmScale;
    private final double[] frequencies;

    public CompositeTransferFunction(double[] xre, double[] xim, double multiplier, float scale, double[] frequencies) {
        this.xre = xre.clone();
        this.xim = xim.clone();
        dataMultiplier = multiplier;
        this.nmScale = scale;
        this.frequencies = frequencies.clone();
    }

    public Complex[] getValues() {
        Complex[] result = new Complex[xre.length];
        for (int j = 0; j < xre.length; ++j) {
            result[j] = new Complex(xre[j], xim[j]);
        }
        return result;
    }

    /**
     * @return the xre
     */
    public double[] getXre() {
        return xre.clone();
    }

    /**
     * @return the xim
     */
    public double[] getXim() {
        return xim.clone();
    }

    /**
     * @return the dataMultiplier
     */
    public double getDataMultiplier() {
        return dataMultiplier;
    }

    /**
     * @return the nmScale
     */
    public float getNmScale() {
        return nmScale;
    }

    public double[] getFrequencies() {
        return frequencies.clone();
    }
}
