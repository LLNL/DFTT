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


import org.apache.commons.math3.complex.Complex;

import llnl.gnem.dftt.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author dodge1
 */
public class SpectralEdgeFrequency {
    private final double edgeFrequency;

    public SpectralEdgeFrequency(double edgeFrequency) {
        this.edgeFrequency = edgeFrequency;
    }
    
    public SpectralEdgeFrequency( TimeSeries ts, double energyFraction)
    {
        
        Complex[] transformed = ts.FFT();
        int npts = transformed.length;
        
        double totalEnergy = 0;
        for( int j = 0; j < npts/2; ++j){
            double v = transformed[j].abs();
            totalEnergy += (v*v);
        }
       
        double threshold = totalEnergy * energyFraction;
        double energy = 0;
        double delFreq = ts.getSamprate() /2 / (npts/2-1);
        
        double edgeFreq = ts.getSamprate() / 2;
        for (int j = 0; j < npts / 2; ++j) {
            double v = transformed[j].abs();
            energy += v*v;
            if( energy >= threshold){
                edgeFreq = j * delFreq;
                break;
            }
        }
        
        edgeFrequency = edgeFreq;
    }

    /**
     * @return the edgeFrequency
     */
    public double getEdgeFrequency() {
        return edgeFrequency;
    }
    
}
