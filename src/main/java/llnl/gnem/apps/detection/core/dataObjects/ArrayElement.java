package llnl.gnem.apps.detection.core.dataObjects;

import llnl.gnem.core.metadata.site.core.CssSite;


public class ArrayElement extends CssSite {

	double  dz;             // positive up
	
	
	public ArrayElement( CssSite site, double  dz ) {
		
	  super( site.getSta(),  
		 site.getOndate(),  
		 site.getOffdate(),  
		 site.getLat(), 
		 site.getLon(), 
		 site.getElevation(), 
		 site.getStaname(), 
		 site.getStatype(), 
		 site.getRefsta(), 
		 site.getDnorth(), 
		 site.getDeast()    );
	  
	  this.dz = dz;
	}
	
	
	
	double delayInSeconds( float[] s ) {
		if ( s.length == 2 )      return delayInSeconds( s[0], s[1] );
		else if ( s.length == 3 ) return delayInSeconds( s[0], s[1], s[2] );
		else throw new IllegalStateException( "Slowness vector length " + s.length + " not correct for delay calculation" );
	}
	
	
	
	double delayInSeconds( float sn, float se ) {
		return delayInSeconds( sn, se, 0.0f );
	}
	
	
	// slowness vector points back toward the source in the local coordinate (x,y,z) frame
	
	double delayInSeconds( float sn, float se, float sz ) {
		return -(sn*getDnorth() + se*getDeast() + sz*dz);
	}
	
	
    @Override
	public String toString() {
		return String.format( "%s lat: %f lon: %f elev: %f dn: %f de: %f dz: %f", getSta(), getLat(), getLon(), getElevation(), getDnorth(), getDeast(), dz );
	}

    }
