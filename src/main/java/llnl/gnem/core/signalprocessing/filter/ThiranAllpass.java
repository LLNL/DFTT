//                                                                               ThiranAllpass
//
// Implements third order Thiran allpass filter for interpolating digital signals to fractional delays.
// Intended to delay continuous data streams in contiguous blocks presented sequentially to the filter method.
// Introduces a two sample delay in the data stream.
// The fractional delayInSamples must be between 0 and 1 samples inclusive.


package llnl.gnem.core.signalprocessing.filter;
/**
 * Copyright (c) 2006  Regents of the University of California
 * All rights reserved
 * Author:  Dave Harris
 * Created: Dec 21, 2006
 * Time: 7:50:55 AM
 * Last Modified: Dec 21, 2006
 */

public class ThiranAllpass {

  private double a1;
  private double a2;
  private double a3;
  private double s0;
  private double s1;
  private double s2;
  private double s3;



  public ThiranAllpass ( double fractionalDelay ) {

    // coefficients of allpass fractional delay filter

    double D  = 2.0 + fractionalDelay;
    a1 = -3.0f * ( D - 3.0f ) / ( D + 1.0f );
    a2 = 3.0f * ( D - 2.0f ) * ( D - 3.0f ) / ( ( D + 1.0f ) * ( D + 2.0f ) );
    a3 = -( D - 1.0f ) * ( D - 2.0f ) * ( D - 3.0f ) / ( ( D + 1.0f ) * ( D + 2.0f ) * ( D + 3.0f ) );
    initialize();
  }



  public void initialize() {
    s0 = 0.0;
    s1 = 0.0;
    s2 = 0.0;
    s3 = 0.0;
  }



  public void filter( float[] dataBlock ) {

      for ( int i = 0;  i < dataBlock.length;  i++ ) {
          s0 = -a1 * s1 - a2 * s2 - a3 * s3 + dataBlock[i];
          dataBlock[i] = (float) ( a3 * s0 + a2 * s1 + a1 * s2 + s3 );
          s3 = s2;
          s2 = s1;
          s1 = s0;
      }

  }



  public float filter( float seqValue ) {
    float retval;
    s0 = -a1 * s1 - a2 * s2 - a3 * s3 + seqValue;
    retval = (float) ( a3 * s0 + a2 * s1 + a1 * s2 + s3 );
    s3 = s2;
    s2 = s1;
    s1 = s0;

    return retval;
  }



  public static int lowerint( double d ) {
    int retval = (int) d;
    if( d < 0.0  &&  (float) ( retval ) != d ) retval--;
    return retval;
  }

}
