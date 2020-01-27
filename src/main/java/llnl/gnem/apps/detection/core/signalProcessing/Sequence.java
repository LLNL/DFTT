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

import java.util.Arrays;

/**
 * Author:  Dave Harris
 * Lawrence Livermore National Laboratory
 * Created: Feb 18, 2008
 * Time: 9:20:16 AM
 * Last Modified: Feb 18, 2008
 */



public class Sequence {
  
  
  // rmean group
  
  public static void rmean( float[] x ) {
    int N = x.length;
    float mean = 0.0f;
    for ( int i = 0;  i < N;  i++ ) mean += x[i];
    mean /= N;
    for ( int i = 0;  i < N;  i++ ) x[i] -= mean;
  }
  
  
  
  public static void rmean( double[] x ) {
    int N = x.length;
    double mean = 0.0;
    for ( int i = 0;  i < N;  i++ ) mean += x[i];
    mean /= N;
    for ( int i = 0;  i < N;  i++ ) x[i] -= mean;
  }
  
  
  
  public static void rmean( float[][] x ) {
    for ( int i = 0;  i < x.length;  i++ ) rmean( x[i] );
  }  
  
  
  
  public static void rmean( double[][] x ) {
    for ( int i = 0;  i < x.length;  i++ ) rmean( x[i] );
  }
  
  
  
  // zero sequence group
  
  public static void zero( float[] x ) {
    Arrays.fill( x, 0.0f );
  }
  
  
  
  public static void zero( double[] x ) {
    Arrays.fill( x, 0.0 );
  }
  
  
  
  public static void zero( float[][] x ) {
    for ( int i = 0;  i < x.length;  i++ )  Arrays.fill( x[i], 0.0f );
  }
  
  
  
  public static void zero( double[][] x ) {
    for ( int i = 0;  i < x.length;  i++ )  Arrays.fill( x[i], 0.0 );
  }


  // pad sequence group

  public static float[] pad( float[] x, int n ) {
    int N = Math.max( x.length, n );
    float[] retval = new float[N];
    for ( int i = 0;  i < x.length;  i++ ) retval[i] = x[i];
    if ( x.length < N ) {
      for ( int i = x.length;  i < N;  i++ ) retval[i] = 0.0f;
    }
    return retval;
  }



  public static double[] pad( double[] x, int n ) {
    int N = Math.max( x.length, n );
    double[] retval = new double[N];
    for ( int i = 0;  i < x.length;  i++ ) retval[i] = x[i];
    if ( x.length < N ) {
      for ( int i = x.length;  i < N;  i++ ) retval[i] = 0.0;
    }
    return retval;
  }



  public static float[][] pad( float[][] x, int n ) {
    float[][] retval = new float[ x.length ][];
    for ( int i = 0;  i < x.length;  i++ ) retval[i] = Sequence.pad( x[i], n );
    return retval;
  }



  public static double[][] pad( double[][] x, int n ) {
    double[][] retval = new double[ x.length ][];
    for ( int i = 0;  i < x.length;  i++ ) retval[i] = Sequence.pad( x[i], n );
    return retval;
  }





  // reverse sequence group - in place

  public static void reverse( float[] x ) {
    int i = 0;
    int ir = x.length - 1;
    while ( i < ir ) {
      float tmp = x[ir];
      x[ir--]   = x[i];
      x[i++]    = tmp;
    }
  }



  public static void reverse( double[] x ) {
    int i = 0;
    int ir = x.length - 1;
    while ( i < ir ) {
      double tmp = x[ir];
      x[ir--]    = x[i];
      x[i++]     = tmp;
    }
  }



  public static void reverse( float[][] x ) {
    for ( int i = 0;  i < x.length;  i++ ) reverse( x[i] );
  }



  public static void reverse( double[][] x ) {
    for ( int i = 0;  i < x.length;  i++ ) reverse( x[i] );
  }





  // zshift sequence group - in place

  public static void zshift( float[] x, int shift ) {
    
    int n = x.length;
    int src, dst;
    if ( shift >= n  ||  -shift >= n ) {   // shift off of end of array
      Arrays.fill( x, 0.0f );
      return;
    }
    
    if ( shift > 0) {                                              // shift to right
      
      dst = n - 1;
      src = dst - shift;
      while ( src >= 0 )  x[ dst-- ] = x[ src-- ];

      Arrays.fill( x, 0, shift, 0.0f );

    }
    else if ( shift < 0 ) {                                        // shift to left

      dst = 0;
      src = -shift;
      while ( src < n )  x[ dst++ ] = x[ src++ ];

      Arrays.fill( x, n + shift, n, 0.0f );     // zero high end
      
    }
    
  }
    

  
  public static void zshift( double[] x, int shift ) {
    
    int n = x.length;
    int src, dst;
    if ( shift >= n  ||  -shift >= n ) {   // shift off of end of array
      Arrays.fill( x, 0.0f );
      return;
    }
    
    if ( shift > 0) {                                              // shift to right
      
      dst = n - 1;
      src = dst - shift;
      while ( src >= 0 )  x[ dst-- ] = x[ src-- ];

      Arrays.fill( x, 0, shift, 0.0f );

    }
    else if ( shift < 0 ) {                                        // shift to left

      dst = 0;
      src = -shift;
      while ( src < n )  x[ dst++ ] = x[ src++ ];

      Arrays.fill( x, n + shift, n, 0.0f );     // zero high end
      
    }
    
  }  
  
  
  
  public static void zshift( float[][] x, int shift ) {
    for ( int i = 0;  i < x.length;  i++ ) zshift( x[i], shift );
  }
  
  
  
  public static void zshift( double[][] x, int shift ) {
    for ( int i = 0;  i < x.length;  i++ ) zshift( x[i], shift );
  }
  
  
  
  
  
  //  sequence circular shift group  - in place

  public static void cshift( float[] x, int shift ) {

    //  Arguments:
    //  ----------
    
    //  float x            array of floats to be shifted.
    //  int   shift        number of samples to shift.

    //                      a negative number indicates a shift left.
    //                      a positive number indicates a shift right.
    //                      zero indicates no shift.

    int n = x.length;

    shift = shift % n;    // prevent extraneous transfers
    
    int altshift = shift;                           // investigate smaller shift
    if ( shift > 0 )      altshift = shift - n;
    else if ( shift < 0 ) altshift = shift + n;

    if ( Math.abs( shift ) > Math.abs( altshift ) ) shift = altshift;

    int bsize = Math.abs( shift );
    if ( bsize == 0 ) return;
    float[] buffer = new float[ bsize ];
    
// two cases - right and left shifts

    int i, j;
    if ( shift > 0 ) {                      // right shift

      j = n - shift;
      for ( i = 0; i < shift; i++ ) buffer[ i ] = x[ j++ ];
      j = n - 1;
      i = j - shift;
      while ( i >= 0 ) x[ j-- ] = x[ i-- ];
      for ( i = 0; i < shift; i++ ) x[ i ] = buffer[ i ];

    }

    else if ( shift < 0 ) {                 // left shift

      for ( i = 0; i < -shift; i++ ) buffer[ i ] = x[ i ];
      j = 0;
      i = -shift;
      while ( i < n ) x[ j++ ] = x[ i++ ];
      j = n + shift;
      for ( i = 0; i < -shift; i++ ) x[ j++ ] = buffer[ i ];

    }

  }



  public static void cshift( double[] x, int shift ) {

    //  Arguments:
    //  ----------

    //  double x            array of doubles to be shifted.
    //  int    shift        number of samples to shift.

    //                      a negative number indicates a shift left.
    //                      a positive number indicates a shift right.
    //                      zero indicates no shift.

    int n = x.length;

    shift = shift % n;    // prevent extraneous transfers
    
    int altshift = shift;                           // investigate smaller shift
    if ( shift > 0 )      altshift = shift - n;
    else if ( shift < 0 ) altshift = shift + n;

    if ( Math.abs( shift ) > Math.abs( altshift ) ) shift = altshift;

    int bsize = Math.abs( shift );
    if ( bsize == 0 ) return;
    double[] buffer = new double[ bsize ];
    
// two cases - right and left shifts

    int i, j;
    if ( shift > 0 ) {                      // right shift

      j = n - shift;
      for ( i = 0; i < shift; i++ ) buffer[ i ] = x[ j++ ];
      j = n - 1;
      i = j - shift;
      while ( i >= 0 ) x[ j-- ] = x[ i-- ];
      for ( i = 0; i < shift; i++ ) x[ i ] = buffer[ i ];

    }

    else if ( shift < 0 ) {                 // left shift

      for ( i = 0; i < -shift; i++ ) buffer[ i ] = x[ i ];
      j = 0;
      i = -shift;
      while ( i < n ) x[ j++ ] = x[ i++ ];
      j = n + shift;
      for ( i = 0; i < -shift; i++ ) x[ j++ ] = buffer[ i ];

    }

  }
  
  
  
  public static void cshift( float[][] x, int shift ) {
    for ( int i = 0;  i < x.length;  i++ ) cshift( x[i], shift );
  }
  
  
  
  public static void cshift( double[][] x, int shift ) {
    for ( int i = 0;  i < x.length;  i++ ) cshift( x[i], shift );
  }
  
  
  
  
// decimate group - with source and destination
  
 public static void decimate( float[] x, float[] y, int decrate ) {
   int ix = 0;
   int iy = 0;
   while ( ix < x.length) {
     y[iy++] = x[ix];
     ix += decrate;
   }
 }
  
  
  
  public static void decimate( double[] x, double[] y, int decrate ) {
   int ix = 0;
   int iy = 0;
   while ( ix < x.length) {
     y[iy++] = x[ix];
     ix += decrate;
   }
 }
  
  
  
  public static void decimate( float[][] x, float[][] y, int decrate ) {
   for ( int i = 0;  i < x.length;  i++ ) decimate( x[i], y[i], decrate );
 }


  
  public static void decimate( double[][] x, double[][] y, int decrate ) {
   for ( int i = 0;  i < x.length;  i++ ) decimate( x[i], y[i], decrate );
 }
  
  
  
  // stretch group - with source and destination
  
  public static void stretch( float[] src, float[] dst, int factor ) {
    Arrays.fill( dst, 0.0f );
    int n = (dst.length - 1) / factor + 1;
    n = Math.min( src.length, n );
    for ( int i = 0;  i < n;  i++ ) {
      dst[i*factor] = src[i];
    }
  }
  


  public static void stretch( double[] src, double[] dst, int factor ) {
    Arrays.fill( dst, 0.0f );
    int n = (dst.length - 1) / factor + 1;
    n = Math.min( src.length, n );
    for ( int i = 0;  i < n;  i++ ) {
      dst[i*factor] = src[i];
    }
  }
  
  
  
   public static void stretch( float[][] src, float[][] dst, int factor ) {
    for ( int j = 0;  j < Math.min( src.length, dst.length );  j++ ) {
      stretch( src[j], dst[j], factor );
    }
  } 
  
  
  
   public static void stretch( double[][] src, double[][] dst, int factor ) {
    for ( int j = 0;  j < Math.min( src.length, dst.length );  j++ ) {
      stretch( src[j], dst[j], factor );
    }
  } 
  
  
  
  
  // alias group - with source and destination
  //   alias src to length of destination
  
  public static void alias( float[] src, float[] dst ) {
    int N = dst.length;
    Arrays.fill( dst, 0.0f );
    for ( int i = 0; i < src.length; i++ ) dst[ i % N ] += src[ i ];
  }
  
  
  
  public static void alias( double[] src, double[] dst ) {
    int N = dst.length;
    Arrays.fill( dst, 0.0 );
    for ( int i = 0; i < src.length; i++ ) dst[ i % N ] += src[ i ];
  }
  
  
  
  public static void alias( float[][] src, float[][] dst ) {
    for ( int i = 0;  i < src.length;  i++ ) alias( src[i], dst[i] );
  }
  
  
  
  public static void alias( double[][] src, double[][] dst ) {
    for ( int i = 0;  i < src.length;  i++ ) alias( src[i], dst[i] );
  }
  
  
  
  
//  window group  
  
  public static void window( float[] x, int src, float[] w, float[] y, int dst ) {
    
    int nw = w.length;
    int nx = x.length;
    int ny = y.length;

    int iy = 0;  
    while ( iy < dst ) y[iy++] = 0.0f;
    
    int ix = src;
    for ( int iw = 0;  iw < nw;  iw++ ) {
      if ( 0 <= ix &&  ix < nx  &&  0 <= iy  &&  iy < ny )  y[iy]  =  w[iw] * x[ix];
      ix++;
      iy++;
    }
    
    while ( iy < ny ) y[iy++] = 0.0f;

  }  
 
  
  
  public static void window( double[] x, int src, double[] w, double[] y, int dst ) {
    
    int nw = w.length;
    int nx = x.length;
    int ny = y.length;

    int iy = 0;  
    while ( iy < dst ) y[iy++] = 0.0;
    
    int ix = src;
    for ( int iw = 0;  iw < nw;  iw++ ) {
      if ( 0 <= ix &&  ix < nx  &&  0 <= iy  &&  iy < ny )  y[iy]  =  w[iw] * x[ix];
      ix++;
      iy++;
    }
    
    while ( iy < ny ) y[iy++] = 0.0;

  }  
   
 

  // interprets the x, w and y sequences as complex
  
  public static void window( float[][] x, int src, float[][] w, float[][] y, int dst ) {
    
    int nw = w[0].length;
    int nx = x[0].length;
    int ny = y[0].length;

    int iy = 0;  
    while ( iy < dst ) {
      y[0][iy] = 0.0f;
      y[1][iy] = 0.0f;
      iy++;
    }
 
    int ix = src;
    for ( int iw = 0;  iw < nw;  iw++ ) {
      if ( 0 <= ix &&  ix < nx  &&  0 <= iy  &&  iy < ny )  {
        y[0][iy]  =  w[0][iw] * x[0][ix]  -  w[1][iw] * x[1][ix];
        y[1][iy]  =  w[0][iw] * x[1][ix]  +  w[1][iw] * x[0][ix];
      }
      ix++;
      iy++;
    }
    
    while ( iy < ny ) {
      y[0][iy] = 0.0f;
      y[1][iy] = 0.0f;
      iy++;
    }
    
  }
  
  
  
  
  // interprets the x and y sequences as complex
  
  public static void window( double[][] x, int src, double[][] w, double[][] y, int dst ) {
    
    int nw = w[0].length;
    int nx = x[0].length;
    int ny = y[0].length;

    int iy = 0;  
    while ( iy < dst ) {
      y[0][iy] = 0.0;
      y[1][iy] = 0.0;
      iy++;
    }
    
    int ix = src;
    for ( int iw = 0;  iw < nw;  iw++ ) {
      if ( 0 <= ix &&  ix < nx  &&  0 <= iy  &&  iy < ny )  {
        y[0][iy]  =  w[0][iw] * x[0][ix]  -  w[1][iw] * x[1][ix];
        y[1][iy]  =  w[0][iw] * x[1][ix]  +  w[1][iw] * x[0][ix];
      }
      ix++;
      iy++;
    } 
    
    while ( iy < ny ) {
      y[0][iy] = 0.0;
      y[1][iy] = 0.0;
      iy++;
    }
    
  }
}
