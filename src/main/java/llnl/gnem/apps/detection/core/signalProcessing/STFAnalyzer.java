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
