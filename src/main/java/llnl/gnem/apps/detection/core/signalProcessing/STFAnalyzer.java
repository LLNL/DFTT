//  STFAnalyzer.java - class to implement short-time Fourier analysis with a Gaussian window
//  Author:  David B. Harris
//  Deschutes Signal Processing LLC
//  Developed for BAA11-26
//  Modified:  7/14/2015 for BAA13  DBH, DSP LLC


package llnl.gnem.apps.detection.core.signalProcessing;




public class STFAnalyzer {
	
	private int           nfft;
	private final float[] h;
	private final int     halfLength;
        private final int     M;
	private final RFFT    fft;
	private final float[] windowedSequence;
	
	
	/**
         * @param windowType     SymmetricWindowType (either GAUSSIAN or SINC)
	 * @param N              DFT size - this must be a power of 2
	 * @param designFactor   Analysis filter (window) design parameter, usually 2 or 3, controls resolution.
	 */
	public STFAnalyzer( SymmetricWindowType windowType, int N, int designFactor ) {
            
          //  set up dft
		
	  int log2nfft = 1;
	  nfft         = 2;
	  while ( nfft < N ) {
	    nfft *= 2;
	    log2nfft++;
	  }
          
          if ( N != nfft ) throw new IllegalStateException( "N not a power of two:  " + N );
		
	  fft = new RFFT( log2nfft );
	  
	  // create baseband window
		
	  halfLength = N*designFactor;
	  M          = 2*halfLength + 1;
          
          switch( windowType ) {
              
              case GAUSSIAN:
                  h = SymmetricWindowGenerator.createGaussian( halfLength );
                  break;
              case SINC:
                  h = SymmetricWindowGenerator.createSinc( halfLength, N);
                  float[] tmp = SymmetricWindowGenerator.createHamming( halfLength );
                  for ( int i = 0;  i < h.length;  i++ ) h[i] *= tmp[i];
                  break;
              default:
                  h = null;
                  throw new IllegalStateException( "Unsupported window type: " + windowType );
          }
	  
	  // set up temporary work spaces
	  
	  windowedSequence = new float[ M ];
	}
        
        
	
	// generate STFT
	
	public void filter( float[] x, float[] STFT, int localIndex, long globalIndex ) {
	  
	  if ( localIndex - halfLength < 0  ||  localIndex + halfLength >= x.length ) {
	    throw new ArrayIndexOutOfBoundsException( "localIndex out of bounds: " + localIndex + "  " + halfLength + "  " + x.length );
          }
	  
          int ptr = localIndex - halfLength;
          for ( int i = 0;  i < M;  i++ ) {
            windowedSequence[i] = x[ptr++]*h[i];
          }
	  
	  Sequence.alias( windowedSequence, STFT );
	  
          Sequence.cshift( STFT, (int) (globalIndex % nfft) );
	  
	  fft.dft( STFT );
	  
	}
	
	
	
	public int     fftsize() { return nfft; }
	
	
	
	public float[] getWindow() { return h; }
	
}
