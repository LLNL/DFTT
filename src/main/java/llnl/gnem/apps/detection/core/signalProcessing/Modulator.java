package llnl.gnem.apps.detection.core.signalProcessing;

import java.util.ArrayList;

/**
 * Author:  Dave Harris
 * Created: Feb 15, 2008
 * Time: 3:37:42 PM
 * Last Modified: Feb 15, 2008
 */

// Implements a modulator using a refreshed coupled-form oscillator.
// The digital frequency is specified as a rational number 2pi(M/N) to permit
// an exact refresh every N samples.

public class Modulator {

  private final int    N;
  private final int    M;

  private final double Omega;

  private double C;                   // holds next cosine value
  private double S;                   // holds next sine value
  private final double dC;
  private final double dS;

  private int count;


// sign is +1 or -1
  
  public Modulator( int M, int N, int sign ) {
    this.M = M;
    this.N = N;
    Omega = 2.0 * Math.PI * ( (double) this.M / (double) this.N );
    C = 1.0;
    S = 0.0;
    dC = Math.cos( Omega );
    if ( sign >= 0 ) dS =  Math.sin( Omega );
    else             dS = -Math.sin( Omega );
    count = 0;
  }


  //              j Omega (n+1)     j Omega (n)   j Omega
  // Implements  e              =  e             e
  //
  private void update() {
    count++;
    if ( count == N ) {
      C = 1.0;
      S = 0.0;
      count = 0;
    }
    else {
      double tmp = C * dC  -  S * dS;
      S = S * dC  +   C * dS;
      C = tmp;
    }
  }



  public void modulateC( float[] x ) {        // real single-precision sequence in-place cosine modulation
    for ( int i = 0;  i < x.length;  i++ ) {
      x[i] = (float) ( C * x[i] );
      update();
    }
  }



  public void modulateS( float[] x ) {       // real single-precision sequence in-place sine modulation
    for ( int i = 0;  i < x.length;  i++ ) {
      x[i] = (float) ( S * x[i] );
      update();
    }
  }
  


  public void modulateC( double[] x ) {      // real double-precision sequence in-place cosine modulation
    for ( int i = 0;  i < x.length;  i++ ) {
      x[i] *= C;
      update();
    }
  }



  public void modulateS( double[] x ) {      // real double-precision sequence in-place sine modulation
    for ( int i = 0;  i < x.length;  i++ ) {
      x[i] *= S;
      update();
    }
  }



  public void modulate( float[] xr, float[] xi ) {  // complex single-precision in-place modulation
    for ( int i = 0;  i < xr.length;  i++ ) {
      float tmp = (float) ( C*xr[i] - S*xi[i] );
      xi[i]     = (float) ( C*xi[i] + S*xr[i] );
      xr[i]     = tmp;
      update();
    }
  }



  public void modulate( double[] xr, double[] xi ) {  // complex double-precision in-place modulation
    for ( int i = 0;  i < xr.length;  i++ ) {
      double tmp = C*xr[i] - S*xi[i];
      xi[i]      = C*xi[i] + S*xr[i];
      xr[i]      = tmp;
      update();
    }
  }



  public void modulate( float[][] x ) {                // complex single-precision in-place modulation
    modulate( x[0], x[1] );                            //   2-D array version
  }



  public void modulate( double[][] x ) {               // complex double-precision in-place modulation
    modulate( x[0], x[1] );                            //   2-D array version
  }



  public void modulateC( ArrayList< float[] > X ) {      // real single-precision in-place cosine modulation
                                                         // for an array of real signals
    int nch = X.size();                                  
    float[][] x = new float[nch][];
    for ( int ich = 0;  ich < nch;  ich++ ) x[ich] = X.get(ich);
    
    int n = x[0].length;
    for ( int i = 0;  i < n;  i++ ) {
      for ( int ich = 0;  ich < nch;  ich++ ) x[ich][i] = (float) ( C * x[ich][i] );
      update();
    }
    
  }
  


  public void modulateS( ArrayList< float[] > X ) {      // real single-precision in-place sine modulation
                                                         // for an array of real signals
    int nch = X.size();                                  
    float[][] x = new float[nch][];
    for ( int ich = 0;  ich < nch;  ich++ ) x[ich] = X.get(ich);
    
    int n = x[0].length;
    for ( int i = 0;  i < n;  i++ ) {
      for ( int ich = 0;  ich < nch;  ich++ ) x[ich][i] = (float) ( S * x[ich][i] );
      update();
    }
    
  }
  
  
  
  public void modulateCdp( ArrayList< double[] > X ) {    // real double-precision in-place cosine modulation
                                                          // for an array of real signals
    int nch = X.size();                                  
    double[][] x = new double[nch][];
    for ( int ich = 0;  ich < nch;  ich++ ) x[ich] = X.get(ich);
    
    int n = x[0].length;
    for ( int i = 0;  i < n;  i++ ) {
      for ( int ich = 0;  ich < nch;  ich++ ) x[ich][i] *=  C * x[ich][i];
      update();
    }
    
  }
  
  
  
  public void modulateSdp( ArrayList< double[] > X ) {    // real double-precision in-place sine modulation
                                                          // for an array of real signals
    int nch = X.size();                                  
    double[][] x = new double[nch][];
    for ( int ich = 0;  ich < nch;  ich++ ) x[ich] = X.get(ich);
    
    int n = x[0].length;
    for ( int i = 0;  i < n;  i++ ) {
      for ( int ich = 0;  ich < nch;  ich++ ) x[ich][i] *=  S * x[ich][i];
      update();
    }
    
  }

  
  
  public void modulateComplexArray( ArrayList< float[][] > X ) {  // complex single-precision in-place modulation
                                                                  // for an array of signals
    int nch = X.size();                                  
    float[][] xr = new float[nch][];
    float[][] xi = new float[nch][];
    for ( int ich = 0;  ich < nch;  ich++ ) {
      xr[ich] = X.get(ich)[0];
      xi[ich] = X.get(ich)[1];
    }
    
    int n = xr[0].length;
    for ( int i = 0;  i < n;  i++ ) {
      for ( int ich = 0;  ich < nch;  ich++ ) {
        float tmp  = (float) ( C*xr[ich][i] - S*xi[ich][i] );
        xi[ich][i] = (float) ( C*xi[ich][i] + S*xr[ich][i] );
        xr[ich][i] = tmp;
      }
      update();
    }
    
  }
  
  

  public void modulateComplexArraydp( ArrayList< double[][] > X ) {  // complex double-precision in-place modulation
                                                                     // for an array of signals
    int nch = X.size();                                  
    double[][] xr = new double[nch][];
    double[][] xi = new double[nch][];
    for ( int ich = 0;  ich < nch;  ich++ ) {
      xr[ich] = X.get(ich)[0];
      xi[ich] = X.get(ich)[1];
    }
    
    int n = xr[0].length;
    for ( int i = 0;  i < n;  i++ ) {
      for ( int ich = 0;  ich < nch;  ich++ ) {
        double tmp = C*xr[ich][i] - S*xi[ich][i];
        xi[ich][i] = C*xi[ich][i] + S*xr[ich][i];
        xr[ich][i] = tmp;
      }
      update();
    }
    
  }
  

}
