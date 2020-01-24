package llnl.gnem.apps.detection.core.signalProcessing;

import java.util.Arrays;

/**
 * Author:  Dave Harris
 * Lawrence Livermore National Laboratory
 * Created: Feb 19, 2008
 * Time: 3:20:05 PM
 * Last Modified: Feb 19, 2008
 */

public class OverlapAdd_dp {

  private double[]      kernel;
  private double[]      shiftRegister;
  private int           kernelLength;
  private int           N;
  private int           log2N;
  private RFFTdp        fft;
  
  private int           dataBlockLength;
  private double[]      dataDFT;
  
  private double[]      product;
  
  private OverlapAdd_dp  master;


  
  // master constructor
  
  public OverlapAdd_dp( double[] kernel, int dataBlockSize ) {
    
    master = null;
    
    dataBlockLength = dataBlockSize;
    kernelLength    = kernel.length;

    // calculate fft size

    log2N           = 0;
    N               = 1;
    while( N  <  kernelLength + dataBlockLength - 1 ) {
      log2N++;
      N *= 2;
    }
    
    // instantiate fft

    fft = new RFFTdp( log2N );
    
    // pad, reverse and transform kernel
 
    this.kernel = Sequence.pad( kernel, N );
    fft.dft( this.kernel );
    
    // allocate shift register and workspace 

    shiftRegister = new double[ N ];
    product       = new double[ N ];
  }
  
  
  
  // slave constructor
  
  public OverlapAdd_dp( double[] kernel, OverlapAdd_dp master ) {

    this.master = master;
    
    dataBlockLength = master.dataBlockLength;
    kernelLength    = kernel.length;
    
    // use the master fft resource 
    
    log2N       = master.log2N;
    N           = master.N;
    fft         = master.fft;
    
    // pad, reverse and transform kernel
    
    this.kernel  = Sequence.pad( kernel, N );
//    Sequence.reverse( this.kernel );
    fft.dft( this.kernel );
    
    // allocate shift register and share workspace
    
    shiftRegister = new double[N];
    product       = master.product;
  }

  

  //
  
  public void initialize( double value ) {
    Arrays.fill( shiftRegister, value );
  }
  
  
  
  // master filtering operation
  
  public void filter( double[] dataBlock, double[] result, int offset ) {
    
      dataDFT = Sequence.pad( dataBlock, N );
      fft.dft( dataDFT );
    
      fft.dftproduct( dataDFT, kernel, product, 1.0 );
      fft.idft( product );
    
      Sequence.zshift( shiftRegister, -dataBlockLength );

      for ( int i = 0; i < dataBlockLength + kernelLength - 1; i++ ) shiftRegister[i] += product[i];         // overlap add
      System.arraycopy( shiftRegister, 0, result, offset, dataBlockLength );
    
  }
  
  
  
  // slave filtering operation
  
  public void filter( double[] result, int offset ) {
    
      fft.dftproduct( master.dataDFT, kernel, product, 1.0 );
      fft.idft( product );
    
      Sequence.zshift( shiftRegister, -dataBlockLength );

      for ( int i = 0; i < dataBlockLength + kernelLength - 1; i++ ) shiftRegister[i]    += product[i];         // overlap add
      System.arraycopy( shiftRegister, 0, result, offset, dataBlockLength );
    
  }
  
  
  
  public void flush( double[] result, int offset ) {
      
      Sequence.zshift( shiftRegister, -dataBlockLength );
      System.arraycopy( shiftRegister, 0, result, offset, dataBlockLength );
      
  }
  
}
