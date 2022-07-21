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
package llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation;

import llnl.gnem.apps.detection.core.dataObjects.AbstractEmpiricalSpecification;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.TriggerPositionType;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList; 
import llnl.gnem.core.util.StreamKey;


public class ArrayCorrelationSpecification extends AbstractEmpiricalSpecification {

    private static final long serialVersionUID = -3702489157499009753L;
  
  private final float  energyCaptureThreshold;
  private final float  STADuration;
  private final float  gapDuration;
  private final float  LTADuration;
  
  /*
   * Constructor to support instantiation of SubspaceDetectors from a flat file specification
   */
  public ArrayCorrelationSpecification( InputStream stream ) throws IOException {
    
    super( stream );
    
    energyCaptureThreshold = Float.parseFloat( parameterList.getProperty( "energyCapture", "0.7"  ) );
    gapDuration            = Float.parseFloat( parameterList.getProperty( "gapDuration",   "1.0"  ) );
    STADuration            = Float.parseFloat( parameterList.getProperty( "STADuration",   "6.0"  ) );
    LTADuration            = Float.parseFloat( parameterList.getProperty( "LTADuration",   "60.0" ) );
    
    triggerPositionType    = TriggerPositionType.STATISTIC_MAX;
    detectorType           = DetectorType.ARRAY_CORRELATION;
  }
  
  
  /*
   * Constructor to support instantiation of detectors stored in the database.
   */
  public ArrayCorrelationSpecification( float                   threshold,
		                        float                   blackoutPeriod,
		                        double                  offsetSecondsToWindowStart,
                                        double                  windowDurationSeconds,
                                        float                   energyCaptureThreshold,
                                        float                   STADuration,
                                        float                   LTADuration,
                                        float                   gapDuration,
		                        ArrayList< StreamKey > staChanList        )  {
	  
	  super( threshold, blackoutPeriod, staChanList, offsetSecondsToWindowStart, windowDurationSeconds );
	  
	  this.energyCaptureThreshold     = energyCaptureThreshold;
          this.STADuration                = STADuration;
          this.LTADuration                = LTADuration;
          this.gapDuration                = gapDuration;
	  triggerPositionType             = TriggerPositionType.STATISTIC_MAX;
	  detectorType                    = DetectorType.ARRAY_CORRELATION;
  }
  
  
  
  public float getEnergyCaptureThreshold() { return energyCaptureThreshold; }
  
  
    
  public float getStaDuration() { return STADuration; }

    
  public float getLtaDuration() { return LTADuration; }

    
  public float getGapDuration() { return gapDuration; }


  

@Override
  public boolean spawningEnabled() { return false; }



  public static void printSpecificationTemplate( PrintStream ps ) {
            
    AbstractEmpiricalSpecification.printSpecificationTemplate( ps );
            
    ps.println( "energyCapture              = <0.0 <= energy capture value <= 1.0>" );
    ps.println( "STADuration                = <float, default 3.0>"                 );
    ps.println( "LTADuration                = <float, default 30.0>"                );
    ps.println( "gapDuration                = <float, default 1.0>"                 );
    ps.println( "detectorType               = ArrayCorrelation"                     );
  }
  
  
  
    @Override
  public void printSpecification( PrintStream ps ) {
            
    super.printSpecification( ps );
            
    ps.println();
    ps.println( "energyCapture              = " + energyCaptureThreshold     );
    ps.println( "STADuration                = " + STADuration                );
    ps.println( "LTADuration                = " + LTADuration                );
    ps.println( "gapDuration                = " + gapDuration                );
    ps.println( "spawning enabled: false"                                    ); 
  }  
  


}
