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
package llnl.gnem.apps.detection.core.signalProcessing;
/**
 * Author:  Dave Harris
 * Lawrence Livermore National Laboratory
 * Created: Feb 24, 2008
 * Time: 10:17:32 AM
 * Last Modified: Feb 24, 2008
 */


public class WindowFIRDesign {


  //  Here OmegaP is expressed as digital frequency - parameterized as a fraction of PI.
  
  public static void impulseResponse( double[] w, double OmegaP, int order ) {

// Hamming window applied to ideal band pass filter impulse response
//
//  ( 0.54 + 0.46 * cos( pi/(nN) * j ) * sin( PI * OmegaP * j ) / j )  ;  j = -order,...,0,...,order

    int nc = 2 * order + 1;
    for ( int i = 0; i < nc; i++ ) {
      int j = i - order;
      if ( j == 0 )
        w[ i ] = 1.0f;
      else {
        w[ i ] = (float) ( ( 0.54 + 0.46 * Math.cos( Math.PI / ( (double) order ) * ( (double) j ) ) ) );
        double x = Math.PI * OmegaP * ( (double) j );
        w[ i ] *=  Math.sin( x ) / x;
      }
      w[i] *= OmegaP;
    }

  }


  public static float[] frequencyResponse( double OmegaP, int order ) {

    int N = 2 * order + 1;

    int M = 1;
    int log2M = 0;
    while ( M < 4*N ) {
      log2M++;
      M *= 2;
    }

    float[] x = new float[M];
    double[] w = new double[N];
    impulseResponse( w, OmegaP, order );
    for ( int i = 0; i < N; i++ ) x[ i ] = (float) w[ i ];
    RFFT fft = new RFFT( log2M );
    fft.dft( x );
    
    float[] retval = new float[ M/2 + 1 ];
    
    retval[0]   = (float) Math.abs( x[0] );
    retval[M/2] = (float) Math.abs( x[M/2] );
    
    for ( int i = 1;  i < M/2;  i++ ) retval[i] = (float) Math.sqrt( x[i]*x[i] + x[M-i]*x[M-i] );
    
    return retval;
  }

}
