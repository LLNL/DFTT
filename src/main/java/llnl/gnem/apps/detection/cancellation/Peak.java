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

