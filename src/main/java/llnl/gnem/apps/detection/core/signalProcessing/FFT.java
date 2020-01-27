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
 * Copyright (c) 2008  Lawrence Livermore National Laboratory
 * All rights reserved
 * Created: Feb 13, 2008
 * Time: 2:56:11 PM
 * Last Modified: Feb 13, 2008
 */

public abstract class FFT {

   protected static final float SQRT2OVER2 = (float) Math.sqrt(2)/2;
   protected static final float SQRT2      = (float) Math.sqrt(2);

   protected float[] ct1;
   protected float[] ct3;
   protected float[] st1;
   protected float[] st3;
   protected int[]   itab;

   protected int     log2N;
   protected int     N;
   protected int     nbit;



   public FFT( int log2N ) {

    // ------- sin/cos table -------

     this.log2N = log2N;
     N = 1 << log2N;

     int tablen = N/8;
     ct1 = new float[tablen];
     ct3 = new float[tablen];
     st1 = new float[tablen];
     st3 = new float[tablen];

     double ang = 2.0*Math.PI/N;
     for ( int i = 0;  i < tablen;  i++ ) {
        double phase = ang*i;
        ct1[i] = (float) Math.cos(  phase);
        ct3[i] = (float) Math.cos(3*phase);
        st1[i] = (float) Math.sin(  phase);
        st3[i] = (float) Math.sin(3*phase);
     }

     // ------- bit reversal table --------

     tablen = ((int) Math.sqrt( 2*N )) + 1;
     itab   = new int[ tablen ];
     int m2 = log2N / 2;
     nbit = 1 << m2;
     if ( 2*m2 != log2N ) m2++;
     itab[0] = 0;
     itab[1] = 1;
     int imax = 1;
     for ( int lbss = 2;  lbss <= m2;  lbss++ ) {
        imax = 2 * imax;
        for ( int i = 0;  i < imax;  i++ ) {
           itab[i]        = 2 * itab[i];
           itab[i + imax] = 1 + itab[i];
        }
     }

   }
  
  
  
  public int fftsize() { return N; }



  // real dft product                                 T                                  H
  // the constant c is +1 or -1 depending on whether X   Y  is desired (convolution) or X   Y is desired (correlation)

  public void dftproduct( float[] x, float[] y, float[] product, float c ) {

    int half = N / 2;

    int k;
    product[ 0 ]    = x[ 0 ] * y[ 0 ];
    product[ half ] = x[ half ] * y[ half ];
    for ( int i = 1; i < half; i++ ) {
      k = N - i;
      product[ i ] = x[ i ] * y[ i ]  -  c * x[ k ] * y[ k ];
      product[ k ] = x[ i ] * y[ k ]  +  c * x[ k ] * y[ i ];
    }
  }



  // complex dft product                              T                                  H
  // the constant c is +1 or -1 depending on whether X   Y  is desired (convolution) or X   Y is desired (correlation)

  public void dftproduct( float[][] x, float[][] y, float[][] product, float c ) {

    for ( int i = 0;  i < N;  i++ ) {
      product[0][i] = x[0][i] * y[0][i]  -  c * x[1][i] * y[1][i];
      product[1][i] = x[0][i] * y[1][i]  +  c * x[1][i] * y[0][i];
    }

  }



  // real spectrum calculation - assuming x is in packed real format
  //
  //    s[-half + 1] ... s[-1]    s[0] s[1] .... s[half]
  //

  public float[] spectrum( float[] x ) {

    float[] retval = new float[ N ];

    retval[ 0]    = Math.abs( x[0]   );
    retval[ N/2 ] = Math.abs( x[N/2] );
    for ( int i = 1;  i < N/2;  i++ ) {
      int j = N-i;
      retval[i] = (float) Math.sqrt( x[i]*x[i] + x[j]*x[j] );
      retval[j] = retval[i];
    }
    Sequence.cshift( retval, N/2 );

    return retval;
  }
  
  // complex spectrum calculation
  
  
  public float[] spectrum( float[][] x ) {

    float[] retval = new float[ N ];

    for ( int i = 0;  i < N;  i++ ) {
      retval[i] = (float) Math.sqrt( x[0][i]*x[0][i] + x[1][i]*x[1][i] );
    }
    Sequence.cshift( retval, N/2 );

    return retval;
  }

}
