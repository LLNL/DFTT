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
 * Created: Feb 13, 2008
 * Time: 2:56:11 PM
 * Last Modified: Feb 15, 2008
 */

public abstract class FFTdp {

   protected static final double SQRT2OVER2 = Math.sqrt(2)/2;
   protected static final double SQRT2      = Math.sqrt(2);

   protected double[] ct1;
   protected double[] ct3;
   protected double[] st1;
   protected double[] st3;
   protected int[]   itab;
   
   protected int     log2N;
   protected int     N;
   protected int     nbit;
  
  
  
   public FFTdp( int log2N ) {
  
    // ------- sin/cos table -------
      
     this.log2N = log2N;
     N = 1 << log2N;  
      
     int tablen = N/8;
     ct1 = new double[tablen];
     ct3 = new double[tablen];
     st1 = new double[tablen];
     st3 = new double[tablen];

     double ang = 2.0*Math.PI/N;
     for ( int i = 0;  i < tablen;  i++ ) {
        double phase = ang*i;
        ct1[i] = Math.cos(  phase);
        ct3[i] = Math.cos(3*phase);
        st1[i] = Math.sin(  phase);
        st3[i] = Math.sin(3*phase);
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

  public void dftproduct( double[] x, double[] y, double[] product, double c ) {  
    
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
  
  public void dftproduct( double[][] x, double[][] y, double[][] product, double c ) {  
    
    for ( int i = 0;  i < N;  i++ ) {
      product[0][i] = x[0][i] * y[0][i]  -  c * x[1][i] * y[1][i];
      product[1][i] = x[0][i] * y[1][i]  +  c * x[1][i] * y[0][i];
    }
    
  }

}
