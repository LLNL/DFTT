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

import com.oregondsp.signalProcessing.fft.CDFTdp;

/**
 *
 * @author addair1
 */
public class DCPFT {
    
    private int mpow2;
    private final CDFTdp fft;

    public DCPFT(int nfreq) {
        mpow2 = 0;

        while (Math.pow(2, mpow2) < nfreq) {
            mpow2 += 1;
        }

        
        fft = new CDFTdp(mpow2);
    }

    public void dcpft(double[] re, double[] im, int nfreq, int sgn) {
        double[] tre = new double[nfreq];
        double[] tim = new double[nfreq];
        if( sgn < 0 ){
            fft.evaluate(re, im, tre, tim);
            System.arraycopy(tre, 0, re, 0, nfreq);
            System.arraycopy(tim, 0, im, 0, nfreq);
        }
        else{
            fft.evaluateInverse(re, im, tre, tim);
            System.arraycopy(tre, 0, re, 0, nfreq);
            System.arraycopy(tim, 0, im, 0, nfreq);
            for( int j = 0; j < nfreq; ++j){
                re[j] *= nfreq;
                im[j] *= nfreq;
            }
        }

    }
}
