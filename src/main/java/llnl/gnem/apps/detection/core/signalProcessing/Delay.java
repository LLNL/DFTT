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

import com.oregondsp.signalProcessing.filter.iir.ThiranAllpass;

// Class to implement an arbitrary postive delay D ( in samples, with an integer part and a fractional integer part)
//   The integer part is implemented with a shift register and the fractional integer part is implemented with a
//   Thiran allpass filter.
//   The delay must be larger than 3 samples, since an order 4 Thiran allpass is being used for the
//   fractional delay (and it introduces an integer sample delay of 3 samples).

public class Delay {
	
	
	interface DelayImplementation {
		
		public float delay( float x );
		
		public void  delay( float[] x, float[] y );
	}
	
	
	
	class IntDelay implements DelayImplementation {
		
		private float[]       shiftRegister;
		private int           n;               // length of shift register
		
		
		IntDelay( float D ) {
		  n = Math.round(D);
		  shiftRegister = new float[n];
		}

		@Override
		public float delay( float x ) {
			
		  float  retval = shiftRegister[0];
		  
		  for ( int i = 0;  i < n-1;  i++ ) shiftRegister[i] = shiftRegister[i+1];
		  shiftRegister[n-1] = x;
		  
		  return retval;
		}

		@Override
		public void delay( float[] x, float[] y ) {
		  if ( x.length != y.length ) throw new IllegalStateException( "Delay - input and output arrays must have the same length" );
		  for ( int i = 0;  i < x.length;  i++ ) y[i] = delay( x[i] );
		}
		
	}
	
	
	
	class NonIntDelay implements DelayImplementation {
		
		private ThiranAllpass TA;
		private float[]       shiftRegister;
		private int           n;               // length of shift register
		
		
		NonIntDelay( float D ) {
			
			int d = (int) D;
			n     = d-3;
			TA = new ThiranAllpass( 4, D - (float) n );			
			shiftRegister = new float[n];
		}
		

		@Override
		public float delay( float x ) {
                    
                    float retval = 0.0f;
                    
                    if ( n == 0 ) {
                      retval = TA.filter( x );
                    }
                    else {
                        
		      retval = TA.filter( shiftRegister[0] );
                      		    
                      for ( int i = 0;  i < n-1;  i++ ) shiftRegister[i] = shiftRegister[i+1];
		      shiftRegister[n-1] = x;
                    }
			
	            return retval;
		}
		
		
		@Override
		public void delay( float[] x, float[] y ) {
			if ( x.length != y.length ) throw new IllegalStateException( "Delay - input and output arrays must have the same length" );
			for ( int i = 0;  i < x.length;  i++ ) y[i] = delay( x[i] );
		}
		
	}
	
	
	
	DelayImplementation implementation;

	
	
	
	public Delay( float D ) {
		
	  int d = (int) D;
		
	  if ( Math.abs( D - (float) d ) < 0.01 ) {
            implementation = new IntDelay(D);
	  }
	  else {
            implementation = new NonIntDelay(D);
	  }
		
	}
	
	
	
	public float delay( float x ) { return implementation.delay(x); }
	
	
	
        public void  delay( float[] x, float[] y ) { implementation.delay( x, y ); }
	

}
