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
package llnl.gnem.core.waveform.responseProcessing;

import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;

public class BaseTransferFunction implements TransferFunction {

    protected  Complex[] function;
    protected  double[] frequencies;
    
    protected BaseTransferFunction() {};
    
    public BaseTransferFunction(double[] frequencies, Complex[] result) {
        function = result.clone();
        this.frequencies = Arrays.copyOf(frequencies, frequencies.length);
    }
    
    @Override
    public double[] getAmplitude() {
        double[] result = new double[function.length];
        for (int j = 0; j < result.length; ++j) {
            result[j] = function[j].abs();
        }
        return result;
    }

    @Override
    public double[] getPhase() {
        double conv = 180 / Math.PI;
        double[] result = new double[function.length];
        for (int j = 0; j < result.length; ++j) {
            double real = function[j].getReal();
            double imag = function[j].getImaginary();
            double phi = Math.atan2(imag, real);
            result[j] = phi * conv;
        }
        return result;
    }

    @Override
    public double[] getFrequencies() {
        return Arrays.copyOf(frequencies, frequencies.length);
    }
}
