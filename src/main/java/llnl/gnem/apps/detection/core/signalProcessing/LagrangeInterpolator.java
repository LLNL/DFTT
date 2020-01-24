package llnl.gnem.apps.detection.core.signalProcessing;




/**
 * Copyright (c) 2009  Lawrence Livermore National Laboratory
 * All rights reserved
 * Author:  Dave Harris
 * Created: Mar 18, 2009
 * Time: 2:20:04 PM
 * Last Modified: Mar 18, 2009
 */


public class LagrangeInterpolator {
  
  int      halfOrder;
  float    D;
  int      filterLength;
  
  float[]  h;


  public LagrangeInterpolator( int halfOrder, float D, int filterLength ) {
   
    this.halfOrder      = halfOrder;
    this.D              = D;
    this.filterLength   = filterLength;
    
    h = new float[ filterLength ];
    
    int center = nint( D );
    
    for ( int n = -halfOrder;  n <= halfOrder;  n++ ) {
      int j = n + center;
      float tmp = 1.0f;
      for ( int k = -halfOrder;  k <= halfOrder;  k++ ) {
        if ( k != n ) tmp *= ( D - center - k ) / ( n - k );
      }
      h[j] = tmp;
    }
    
  }
  
  
  
  public float[] getFIRKernel() {
    return h;
  }
  
  
  
  public float[] computeGroupDelay( int log2N ){
    
    int N = 1;
    int i = 0;
    while ( i < log2N ) {
      N *= 2;
      i++;
    }
    
    RFFT fft = new RFFT( log2N );
    
    float[] x  = Sequence.pad( h, N );
    float[] xn = new float[ N ];
    for ( i = 0;  i < N;  i++ ) xn[i] = i*x[i];
    
    fft.dft( x );
    fft.dft( xn );
    
    int half = N / 2;
    
    float[] gd = new float[ half + 1 ];
    if ( x[0] != 0.0f ) gd[0] = xn[0] / x[0];
    if ( x[half] != 0.0f ) gd[half] = xn[half] / x[half];
    for ( i = 1;  i < half;  i++ ) {
      float denom =  x[i]*x[i]  + x[N-i]*x[N-i];
      float numer = xn[i]*x[i] + xn[N-i]*x[N-i];
      if ( denom != 0.0f ) gd[i] = numer/denom;
    }
    
    return gd;
  }
  
  
  
  public static float[] interpolateTraceAroundSample( float[] trace, int sample, int interpolationRate ) {
    
    int N = 2*interpolationRate + 1;
    float[] retval = new float[ N ];
    int halfOrder = 10;
    
    for ( int i = -interpolationRate;  i <= interpolationRate;  i++ ) {
      
      float   D = ( (float) i ) / ( (float) interpolationRate );
      float[] c = new float[ 2*halfOrder + 1 ];
    
      for ( int n = -halfOrder;  n <= halfOrder;  n++ ) {
        int j = n + halfOrder;
        float tmp = 1.0f;
        for ( int k = -halfOrder;  k <= halfOrder;  k++ ) {
          if ( k != n ) tmp *= ( D - k ) / ( n - k );
        }
        c[j] = tmp;
      }

      float tmp = 0.0f;
      for ( int j = 0;  j < c.length;  j++ ) {
        tmp += c[j]*trace[ sample + j - halfOrder ];
      }
      
      retval[ i + interpolationRate ] = tmp;
    }
    
    return retval;
  }
  
  
  
  private static int nint( float x ) {
    if( x >= 0.0f )
      return ( (int) ( x + 0.5f ) );
    else
      return ( (int) ( x - 0.5f ) );
  }
  

}
