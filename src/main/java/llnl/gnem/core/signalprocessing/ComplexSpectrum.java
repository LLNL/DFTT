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
import java.util.ArrayList;

/**
 *
 * @author dodge1
 */
public class ComplexSpectrum {
    private final ArrayList<SpectralSample> samples;

    public ComplexSpectrum(ArrayList<SpectralSample> samples) {
        this.samples = samples;
    }

    public void print(PrintStream out) {
        for( SpectralSample samp : samples){
            samp.print(out);
        }
    }

    public ComplexSpectrum conjugate() {
        ArrayList<SpectralSample> result = new ArrayList<>();
        for( SpectralSample samp : samples){
            result.add(samp.conjugate());
        }
        return new ComplexSpectrum(result);
    }
    
    public Periodogram toPeriodogram()
    {
        return new Periodogram(this);
    }

    public ComplexSpectrum times(ComplexSpectrum other) {
        ArrayList<SpectralSample> result = new ArrayList<>();
        for( int j = 0; j < samples.size(); ++j){
            SpectralSample x = samples.get(j);
            SpectralSample y = other.samples.get(j);
            SpectralSample z = x.times(y);
            result.add(z);
        }
        return new ComplexSpectrum(result);
    }
    
    public SpectralSample getSample(int index){
        return samples.get(index);
    }

    public int size() {
        return samples.size();
    }
    
}
