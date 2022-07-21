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
package llnl.gnem.apps.detection.core.framework.detectors.power;

import java.io.IOException;
import java.util.ArrayList;

import llnl.gnem.apps.detection.core.dataObjects.AbstractSpecification;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.TriggerPositionType;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;

import llnl.gnem.core.util.StreamKey;


public class STALTASpecification extends AbstractSpecification implements Serializable{

    private static final long serialVersionUID = -2735008024400573448L;
	
	private final float   STADuration;
	private final float   LTADuration;
	private final float   gapDuration;
	private final boolean spawnOnTriggers;
	
        
        public static STALTASpecification getSpecificationFromFile( String filename ) throws FileNotFoundException, IOException
        {
            try(FileInputStream stream = new FileInputStream(filename)) {
                return new STALTASpecification(stream);
            }
        }
  
	public STALTASpecification( InputStream stream ) throws IOException {
		
            super( stream );
		
	    STADuration = Float.parseFloat( parameterList.getProperty( "STADuration",  "4.0" ) );
	    LTADuration = Float.parseFloat( parameterList.getProperty( "LTADuration", "40.0" ) );
	    gapDuration = Float.parseFloat( parameterList.getProperty( "gapDuration",  "2.0" ) );
	    
		  
	    spawnOnTriggers     = Boolean.parseBoolean( parameterList.getProperty( "enableSpawning", "true" ) );
	    
	    triggerPositionType = TriggerPositionType.THRESHOLD_EXCEED_POINT;
	    detectorType        = DetectorType.STALTA;  
	}
	
	
	
	  /*
	   * Constructor to support instantiation of detectors stored in the database.
	   */
	  public STALTASpecification( float                   threshold,
			              float                   blackoutPeriod,
			              ArrayList< StreamKey > staChanList, 
			              float                   STADuration,
			              float                   LTADuration,
			              float                   gapDuration,
			              boolean                 enableSpawning )  {
		  
		  super( threshold, blackoutPeriod, staChanList );
		  
		  this.STADuration = STADuration;
		  this.LTADuration = LTADuration;
		  this.gapDuration = gapDuration;
		  
		  spawnOnTriggers  = enableSpawning;
		  
		  triggerPositionType    = TriggerPositionType.THRESHOLD_EXCEED_POINT;
		  detectorType           = DetectorType.STALTA; 
	  }




	public float getSTADuration() {     return STADuration;     }
	


	public float getLTADuration() {	    return LTADuration;	    }


	
	public float getGapDuration() {	    return gapDuration;	    }


	
    @Override
	public boolean  spawningEnabled() { return spawnOnTriggers; }
        
        
        
        public static void printSpecificationTemplate( PrintStream ps ) {
            
            AbstractSpecification.printSpecificationTemplate( ps );
            
            ps.println( "STADuration    = <duration (sec)>"   );
            ps.println( "LTADuration    = <duration (sec)>"   );
            ps.println( "gapDuration    = <duration (sec)>"   );
            ps.println( "enableSpawning = false"  );
            ps.println( "detectorType   = STALTA" );
        }
        
        
        
    @Override
        public void printSpecification( PrintStream ps ) {
            
            super.printSpecification( ps );
            
            ps.println();
            ps.println( "STADuration     = " + STADuration );
            ps.println( "LTADuration     = " + LTADuration );
            ps.println( "gapDuration     = " + gapDuration );
            ps.println( "spawning enabled: " + spawnOnTriggers ); 
        }
        
           
    @Override
    public boolean isArraySpecification() {
        return false;
    }

	
}
