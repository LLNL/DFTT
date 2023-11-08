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
package llnl.gnem.dftt.core.signalprocessing.filter;
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
