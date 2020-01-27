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

/**
 *
 * @author dbh
 */
public class SymmetricWindowGenerator {
    
    
    public static float[] createGaussian( int half ) {
        
        int N = 2*half + 1;
        float[] retval = new float[ N ];
        
        double alpha = 6.0/( half*half );
          
	retval[ half ] = 1.0f;
	for ( int i = 1;  i <= half;  i++ ) {
            float g = (float) Math.exp( -alpha*i*i );
            retval[ half + i ] = g;
	    retval[ half - i ] = g;
	}
        
        return retval;
    }
    
    
    
    public static float[] createHamming( int half ) {
                
        int N = 2*half + 1;
        float[] retval = new float[ N ];
        
        retval[ half ] = 1.0f;
        double s = Math.PI/half;
	for ( int i = 1;  i <= half;  i++ ) {
            float h = (float) ( 0.54 + 0.46*Math.cos( s*i ) );
            retval[ half + i ] = h;
	    retval[ half - i ] = h;
	}
        
        return retval;
    }
    
    
    
    public static float[] createSinc( int half, int n ) {
        
        int N = 2*half + 1;
        float[] retval = new float[ N ];
        
        retval[ half ] = 1.0f;
        double s = Math.PI/n;
	for ( int i = 1;  i <= half;  i++ ) {
            float h = (float) ( Math.sin( s*i ) / ( s*i ) );
            retval[ half + i ] = h;
	    retval[ half - i ] = h;
	}
        
        return retval;        
    }
    
}
