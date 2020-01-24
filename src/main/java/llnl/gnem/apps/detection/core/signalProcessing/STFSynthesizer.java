/*
 * Copyright (c) 2015 Deschutes Signal Processing LLC
 * Author:  David B. Harris
 * Created: Jul 17, 2015
 */
package llnl.gnem.apps.detection.core.signalProcessing;

/**
 *
 * @author User Name
 */
public class STFSynthesizer {
    
  private final int     nfft;
  private final int     narrowbandDecrate;
  private final float[] hs;     // synthesis window
  private final float[] SR;     // shift register - state information for overlap add
  private final float[] w;      // phase shift operator (see Crochiere, 1980)

  private final float[] z;

  private final RFFT    dft;
 
 

  public STFSynthesizer( int nfft, int narrowbandDecrate ) {

    this.narrowbandDecrate = narrowbandDecrate;

    int N     = 2;
    int log2N = 1;
    while ( N < nfft ) {
      N *= 2;
      log2N++;
    }
    
    if ( N != nfft ) throw new IllegalStateException( "N not a power of two:  " + N );
    
    this.nfft = N;

    // initialize synthesis window and phase shift operator

    int half = N/2;
//    float[] tmp = ( new ProlateSpheroidalWindow( N + 1, 1.75 / ( N + 1 ) ) ).getArray();
    float[] h   = SymmetricWindowGenerator.createSinc(half, narrowbandDecrate );
    float[] tmp = SymmetricWindowGenerator.createHamming( half );
    hs = new float[ N ];
    for ( int i = 0;  i < N;  i++ ) hs[i] = h[i]*tmp[i];

    w = new float[N];
    w[ 0 ] = 1.0f;
    for (int j = 1; j < N; j++) w[ j ] = -w[ j - 1 ];

    // initialize shift register

    SR = new float[N];

    // initialize work space and fft

    z   = new float[N];
    dft = new RFFT( log2N );

  }



  public void synthesize( float[] STFT,  float[] validSamples, long globalIndex ) {
       
    // phase shift - rotation by N/2 in the time domain
       
    for ( int i = 0;  i < nfft;  i++ ) z[i] = w[i]*STFT[i];
       
    // synthesis
       
    dft.idft( z );
    Sequence.cshift( z, (int) (-globalIndex % nfft) );
    for ( int i = 0;  i < nfft;  i++ ) SR[i] = SR[i] + hs[i]*z[i];
       
    // extract decrate finished values
   
    System.arraycopy( SR, 0, validSamples, 0, narrowbandDecrate );

    // shift register by decrate samples - zero fill
       
    Sequence.zshift( SR, -narrowbandDecrate );
  }
 
}
