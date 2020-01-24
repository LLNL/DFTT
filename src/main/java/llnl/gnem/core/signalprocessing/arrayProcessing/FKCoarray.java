//                                                          FKCoarray.java
//
//  Evaluates FK spectrum on a Cartesian grid
//
//  Copyright (c) 2001  Regents of the University of California
//  All rights reserved

//  Author:  Dave Harris
//  Created:        September 6, 2001 by translation from C++
//  Last Modified:  October 15, 2001

//  Small memory footprint version - uses coupled form oscillator to
//    compute plane-wave steering vectors on-the-fly

package llnl.gnem.core.signalprocessing.arrayProcessing;

import Jampack.Zmat;
import Jampack.Trace;
import Jampack.Znum;


public class FKCoarray {

// instance variables

  float[] fks;
  int     nch;               // number of array channels
  float   max_k;             // maximum absolute wavenumber, i.e.
                             //   the FK spectrum is computed from
                             //   -max_k to max_k in kx and in ky
  float   dk;                // spacing between FK samples
  int     nk;                // # FK samples in one dimension
  float[] cx;
  float[] cy;
  double[] decx;
  double[] desx;
  double[] decy;
  double[] desy;
  int nc;


  public FKCoarray( float[] x, float[] y, int _nch, float _max_k, int _nk ) {

    //  float[] x                  array element x offset in kilometers
    //  float[] y                  array element y offset in kilometers
    //  int     _nch               number of array elements
    //  float   _max_k             maximum wavenumber (cycles/km)
    //  int     _nk                number of wavenumber samples in each dimension

    nch   = _nch;
    max_k = _max_k;
    nk    = _nk;
    fks   = null;                  // allocate upon evaluation

    // phasor computations only on off-diagonal, upper-triangular elements of
    //   covariance matrix

    nc = ( nch * ( nch - 1 ) ) / 2;
    double twopi = 2.0 * Math.PI;
    dk = (float) ( twopi * 2.0 * max_k / (double) ( nk - 1 ) );
    int ic = 0;
    cx   = new float[nc];
    cy   = new float[nc];
    decx = new double[nc];
    desx = new double[nc];
    decy = new double[nc];
    desy = new double[nc];
    for ( int m = 0; m < nch - 1; m++ ) {
      for ( int n = m + 1; n < nch; n++ ) {
        cx[ ic ] = ( x[ m ] - x[ n ] );
        cy[ ic ] = ( y[ m ] - y[ n ] );
        decx[ ic ] = Math.cos( dk * cx[ ic ] );
        desx[ ic ] = Math.sin( dk * cx[ ic ] );
        decy[ ic ] = Math.cos( dk * cy[ ic ] );
        desy[ ic ] = Math.sin( dk * cy[ ic ] );
        ic++;
      }
    }
  }


  public void evaluate( Zmat covm ) {

    // trace of covariance matrix

    double s0 = Trace.o( covm ).re;

    // reorder covariance matrix

    float[] rr = new float[nc];
    float[] ri = new float[nc];
    int ic = 0;
    for ( int m = 0; m < nch - 1; m++ ) {
      for ( int n = m + 1; n < nch; n++ ) {
        Znum Z = covm.get( m, n );
        rr[ ic ] = (float) Z.re;
        ri[ ic ] = (float) Z.im;
        ic++;
      }
    }
    double[] eca = new double[nc];
    double[] esa = new double[nc];
    double[] ec  = new double[nc];
    double[] es  = new double[nc];

    // initialize steering vector - scan from lower left corner

    double twopi = 2.0 * Math.PI;
    double arg;
    double dotr, doti, t;
    for ( ic = 0; ic < nc; ic++ ) {
      arg = -twopi * max_k * ( cx[ ic ] + cy[ ic ] );
      eca[ ic ] = Math.cos( arg );
      esa[ ic ] = Math.sin( arg );
    }
    fks = new float[nk * nk];
    int counter = 0;
    int reverse_counter = nk * nk - 1;
    for ( int iy = 0; iy < nk; iy++ ) {
      for ( ic = 0; ic < nc; ic++ ) {
        ec[ ic ] = eca[ ic ];
        es[ ic ] = esa[ ic ];
      }
      for ( int ix = 0; ix < nk; ix++ ) {
        if ( counter < reverse_counter ) {
          dotr = 0.0;
          doti = 0.0;
          for ( ic = 0; ic < nc; ic++ ) {

            // partial Fourier sum

            dotr = dotr  +  rr[ ic ] * ec[ ic ];
            doti = doti  +  ri[ ic ] * es[ ic ];

            // kx update of steering vector

            t        = ec[ ic ] * decx[ ic ]  -  es[ ic ] * desx[ ic ];
            es[ ic ] = ec[ ic ] * desx[ ic ]  +  es[ ic ] * decx[ ic ];
            ec[ ic ] = t;
          }

          // store results

          fks[ counter ]         = (float) ( s0 + 2.0 * ( dotr - doti ) );
          fks[ reverse_counter ] = (float) ( s0 + 2.0 * ( dotr + doti ) );
          counter++;
          reverse_counter--;
        } else
          break;
      }
      if ( counter > reverse_counter ) break;

      // ky update of steering vector

      for ( ic = 0; ic < nc; ic++ ) {
        t         = eca[ ic ] * decy[ ic ]  -  esa[ ic ] * desy[ ic ];
        esa[ ic ] = eca[ ic ] * desy[ ic ]  +  esa[ ic ] * decy[ ic ];
        eca[ ic ] = t;
      }
    }
  }

  // get 2-D FK spectrum scanned
  //   in lexicographic order

  public float[] getFKSpectrum() {
    return fks;
  }

  public float getKx( int index ) {
    int q = index / nk;
    int r = index - q * nk;
    return (float) ( -max_k + r * dk / (2.0 * Math.PI) );
  }


  public float getKy( int index ) {
    int q = index / nk;
    return (float) ( -max_k + q * dk / (2.0 * Math.PI) );
  }
}

