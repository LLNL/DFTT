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
package llnl.gnem.apps.detection.core.framework.localImplementation;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.oregondsp.signalProcessing.filter.iir.PassbandType;
import java.io.PrintStream;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.framework.detectors.PersistentProcessingParameters;
import llnl.gnem.apps.detection.core.signalProcessing.IIRFilterType;

public class ProcessingPrescription implements PreprocessorParams {

  private double           samplingRate;

  private PassbandType     passbandType;  
  private int              filterOrder;
  private double           f1;
  private double           f2;
  private double           eps;
  private int              decimationRate;
  private int              decimatedBlockSize;
  private double           maxTemplateLength;
  
  private String           detStatPath;
  private boolean          writingDetStats;
  
  private static           ProcessingPrescription instance = null;
   
    
  
  private ProcessingPrescription() {}
  
  
  public static ProcessingPrescription getInstance() {
      if ( instance == null ) instance = new ProcessingPrescription();
      return instance;
  }
  
  
  public void initialize( String parfile ) {
    
    Properties parameterList = new Properties();
    try {
        
      parameterList.load( new FileInputStream( parfile ) );
      
      String type           = parameterList.getProperty( "passbandType", "BANDPASS" );
      switch ( type ) {
          case "LOWPASS":
              passbandType = PassbandType.LOWPASS;
              break;
          case "BANDPASS":
              passbandType = PassbandType.BANDPASS;
              break;
          case "HIGHPASS":
              passbandType = PassbandType.HIGHPASS;
              break;
      }
      
      filterOrder           = Integer.parseInt(   parameterList.getProperty( "filterOrder",                   "4" ) );
      f1                    = Double.parseDouble( parameterList.getProperty( "lowcutoff",                   "2.0" ) );
      f2                    = Double.parseDouble( parameterList.getProperty( "highcutoff",                  "6.0" ) );
      eps                   = Double.parseDouble( parameterList.getProperty( "eps",                       "0.001" ) );
      decimatedBlockSize    = Integer.parseInt(   parameterList.getProperty( "decimatedBlockSize",        "10000" ) );
      decimationRate        = Integer.parseInt(   parameterList.getProperty( "decimationRate",                "1" ) );
      maxTemplateLength     = Double.parseDouble( parameterList.getProperty( "maxTemplateLength",         "100.0" ) );
      
      detStatPath           = parameterList.getProperty( "detectionStatisticPath",          "." );
      String isWriting      = parameterList.getProperty( "isWritingDetectionStatistics", "true" );
      if ( isWriting.equals( "true" ) ) 
          writingDetStats = true;
      else
          writingDetStats = false;
      
    } catch ( IOException e ) {
      e.printStackTrace();
    }
    
  }
  

  @Override
  public int getPreprocessorFilterOrder() {
    return filterOrder; 
  }

  
  @Override
  public int getDataBlockSize() {
    return decimatedBlockSize * decimationRate;
  }

  
  @Override
  public double getPassBandHighFrequency() {
    return f2;
  }

  
  @Override
  public double getPassBandLowFrequency() {
    return f1;
  }


  @Override
  public int getDecimatedDataBlockSize() {
    return decimatedBlockSize;
  }



  @Override
  public int getDecimationRate() {
    return decimationRate;
  }
  
  
  
  public double getMaxTemplateLength() {
      return maxTemplateLength;
  }
  
  
  
  public void setSamplingRate( double samplingRate ) {
      this.samplingRate = samplingRate;
  }
  

  @Override
  public PersistentProcessingParameters getPreprocessorParams() {
      
    return new PersistentProcessingParameters( IIRFilterType.BUTTERWORTH,
                                               PassbandType.BANDPASS,
                                               f1,
                                               f2,
                                               filterOrder,
                                               eps,
                                               samplingRate,
                                               decimationRate           );
  }
  
  
  
  public void print( PrintStream ps ) {
      ps.println( "sampling rate:            " + samplingRate );
      ps.println( "passband type:            " + passbandType );
      ps.println( "filter order:             " + filterOrder  );
      ps.println( "pass band low frequency:  " + f1 );
      ps.println( "pass band high frequency: " + f2 );
      ps.println( "Chebyshev ripple factor:  " + eps );
      ps.println( "decimation rate:          " + decimationRate );
      ps.println( "decimated block size:     " + decimatedBlockSize );
      ps.println( "data block size:          " + getDataBlockSize() );
      ps.println( "maximum template length:  " + maxTemplateLength  );
      ps.println(" writing detection stats:  " + writingDetStats );
      ps.println( "detstat path:             " + detStatPath );
  }
  
  
    @Override
    public PreprocessorParams changeBlockSize(double blockSizeSeconds) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public double getSampleRate() {
        return samplingRate;
    }

    
    
    public boolean writingDetectionStatistics() {
        return writingDetStats;
}
    
    
    
    public String getDetectionStatisticPath() {
        return detStatPath;
    }
    
    
    
//    public static int getRunid() {
//        return 1;
//    }

}
