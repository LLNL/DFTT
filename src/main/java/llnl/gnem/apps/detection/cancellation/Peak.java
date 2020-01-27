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
package llnl.gnem.apps.detection.cancellation;

import java.text.DecimalFormat;

public class Peak implements Comparable< Peak > {
	
	private static PeakSortParameter sortParameter = PeakSortParameter.DETSTAT;
    
	public int     ID;
	public int     index;
	public float   detstat;
	public boolean shadowed;
	
	
	
	public static void setPeakSortParameter( PeakSortParameter P ) {
		sortParameter = P;
	}
	    
	    
	public Peak( int ID, int index, float detstat ) {
	  this.ID      = ID;
	  this.index   = index;
	  this.detstat = detstat;
	  shadowed     = false;
	}
	        
	

	public int compareTo( Peak other ) {
		
	  int retval = 0;
	  
	  switch ( sortParameter ) {
	  
	    case DETSTAT:
	    	
		  if ( detstat > other.detstat ) 
			retval = 1;
		  else if ( detstat < other.detstat ) 
		    retval = -1;
		  
		  break;
		  
	    case INDEX:

		  if ( index > other.index ) 
			retval = 1;
		  else if ( index < other.index ) 
		    retval = -1;
		  
	      break;	    	
		  
	  }

	  return retval;
	}
	  
	
	        
	public String toString() {
	      
	  DecimalFormat D = new DecimalFormat( "0.0000" );
	  String retval = index + "  " + D.format( detstat ) + "  " + ID;
	      
	  return retval;
	}
	    
}

