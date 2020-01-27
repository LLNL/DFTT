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
package llnl.gnem.core.signalprocessing.filter;

import llnl.gnem.core.signalprocessing.FFT;
import llnl.gnem.core.signalprocessing.Sequence;

public class FilterBank {

// instance variables

    private FFT fft;
    private Sequence window;
    private int N;                      // number of bands;  must be a power of two
    private int n;
    private Sequence STFT;              // holds short-time Fourier Transform
                                        // equivalent to the current time-step
                                        // values of all bands in the filter bank


    public FilterBank( int _N, int _n )
    {
        N = _N;
        n = _n;
        int m = 1;
        int M = 2;
        while( M < N ) {
            m++;
            M *= 2;
        }
        fft = new FFT( m, 1 );

// window sequence - baseband prototype filter impulse response
//
//  ( 0.54 + 0.46 * cos( pi/(nN) * j ) * sin( pi/N * j ) / j )  ;  j = -nN,...,0,...,nN

        int half = N * n;
        int nc = 2 * half + 1;
        float[] w = new float[nc];
        for ( int i = 0; i < nc; i++ ) {
            int j = i - half;
            if( j == 0 )
                w[i] = 1.0f;
            else {
                double x = (double) j;
                w[i] = (float) ( ( 0.54 + 0.46 * Math.cos( Math.PI / ( (double) half ) * x ) ) );
                x *= ( Math.PI / ( (double) N ) );
                w[i] *= Math.sin( x ) / x;
            }
        }
        window = new Sequence( w );
    }


    public void filter( Sequence S, int index )
    {
        STFT = ( S.window( index - n * N, window ) ).alias( N );
        STFT.cshift( index );
        STFT.dft();
//        STFT.dftRX( fft );
    }



    // single-sample synthesis - inefficient, but useful for testing

    public float synthesis( Sequence S, float[] Hr, float[] Hi, int index )
    {
        float retval = 0.0f;

        //  initialization for coupled-form oscillator

        double t = 2.0 * Math.PI / ( (double) N );
        double c1 = Math.cos( t * index );
        double s1 = Math.sin( t * index );
        double c = c1;
        double s = s1;
        float SHr, SHi;
        filter( S, index );
        float[] transform = STFT.getArray();
        retval += Hr[0] * transform[0];  // special case at d.c.
        int half = N / 2;
        for ( int i = 1; i < half; i++ ) {
            SHr = Hr[i] * transform[i] - Hi[i] * transform[N - i];
            SHi = Hr[i] * transform[N - i] + Hi[i] * transform[i];
            t = SHr * c - SHi * s;
            retval += 2.0f * (float) t;

            // coupled form oscillator update

            t = c * c1 - s * s1;
            s = c * s1 + s * c1;
            c = t;
        }
        retval += (float) ( Hr[half] * transform[half] * c );  // special case at pi
        return retval / ( (float) N );
    }


    public float[] get( int index )
    {
        float[] retval = null;
        if( index >= 0 && index <= N / 2 ) {
            retval = new float[2];
            if( index == 0 )
                retval[0] = STFT.get( 0 );
            else if( index == N / 2 )
                retval[0] = STFT.get( N / 2 );
            else {
                retval[0] = STFT.get( index );
                retval[1] = STFT.get( N - index );
            }
        }
        return retval;
    }

}