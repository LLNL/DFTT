//                                                                            FilterBankdp.java

package llnl.gnem.core.signalprocessing.filter;
/**
 * Copyright (c) 2006  Regents of the University of California
 * All rights reserved
 * Author:  Dave Harris
 * Created: Jan 25, 2006
 * Time: 5:57:03 PM
 * Last Modified: Jan 25, 2006
 */

import llnl.gnem.core.signalprocessing.FFTdp;
import llnl.gnem.core.signalprocessing.DoubleSequence;

public class FilterBankdp {

// instance variables

  private FFTdp          fft;
  private DoubleSequence window;
  private int            N;           // number of bands;  must be a power of two
  private int            n;
  private DoubleSequence STFT;        // holds short-time Fourier Transform
                                      // equivalent to the current time-step
                                      // values of all bands in the filter bank


  public FilterBankdp( int _N, int _n ) {
    N = _N;
    n = _n;
    int m = 1;
    int M = 2;
    while ( M < N ) {
      m++;
      M *= 2;
    }
    fft = new FFTdp( m, 1 );

// window sequence - baseband prototype filter impulse response
//
//  ( 0.54 + 0.46 * cos( pi/(nN) * j ) * sin( pi/N * j ) / j )  ;  j = -nN,...,0,...,nN

    int half = N * n;
    int nc = 2 * half + 1;
    double[] w = new double[nc];
    for ( int i = 0; i < nc; i++ ) {
      int j = i - half;
      if ( j == 0 )
        w[ i ] = 1.0;
      else {
        double x = (double) j;
        w[ i ] = ( 0.54 + 0.46 * Math.cos( Math.PI / ( (double) half ) * x ) );
        x *= ( Math.PI / ( (double) N ) );
        w[ i ] *= Math.sin( x ) / x;
      }
    }
    window = new DoubleSequence( w );
  }



  public void filter( DoubleSequence S, int index ) {
    STFT = ( S.window( index - n * N, window ) ).alias( N );
    STFT.cshift( index );
    STFT.dftRX( fft );
  }



  public void filterAsynchronous( DoubleSequence S, int index ) {
    STFT = ( S.window( index - n * N, window ) ).alias( N );
    STFT.dftRX( fft );
  }



  // single-sample synthesis - inefficient, but useful for testing

  public float synthesis( DoubleSequence S, float[] Hr, float[] Hi, int index ) {
    float retval = 0.0f;

    //  initialization for coupled-form oscillator

    double t = 2.0 * Math.PI / ( (double) N );
    double c1 = Math.cos( t * index );
    double s1 = Math.sin( t * index );
    double c = c1;
    double s = s1;
    double SHr, SHi;
    filter( S, index );
    double[] transform = STFT.getArray();
    retval += Hr[ 0 ] * transform[ 0 ];  // special case at d.c.
    int half = N / 2;
    for ( int i = 1; i < half; i++ ) {
      SHr = Hr[ i ] * transform[ i ] - Hi[ i ] * transform[ N - i ];
      SHi = Hr[ i ] * transform[ N - i ] + Hi[ i ] * transform[ i ];
      t = SHr * c - SHi * s;
      retval += 2.0f * (float) t;

      // coupled form oscillator update

      t = c * c1 - s * s1;
      s = c * s1 + s * c1;
      c = t;
    }
    retval += (float) ( Hr[ half ] * transform[ half ] * c );  // special case at pi
    return retval / ( (float) N );
  }



  public double[] get( int index ) {

    double[] retval = null;
    if ( index >= 0 && index <= N / 2 ) {
      retval = new double[2];
      if ( index == 0 )
        retval[ 0 ] = STFT.get( 0 );
      else if ( index == N / 2 )
        retval[ 0 ] = STFT.get( N / 2 );
      else {
        retval[ 0 ] = STFT.get( index );
        retval[ 1 ] = STFT.get( N - index );
      }
    }
    return retval;
  }

}