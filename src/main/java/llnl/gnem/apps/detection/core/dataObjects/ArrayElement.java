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
            switch (s.length) {
                case 2:
                    return delayInSeconds( s[0], s[1] );
                case 3:
                    return delayInSeconds( s[0], s[1], s[2] );
                default:
                    throw new IllegalStateException( "Slowness vector length " + s.length + " not correct for delay calculation" );
            }
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
