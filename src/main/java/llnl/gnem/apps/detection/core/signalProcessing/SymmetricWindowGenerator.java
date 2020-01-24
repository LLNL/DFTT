/*
 * Developed for BAA13 under AFRL funding through NORSAR
 * Author:  David B. Harris
 * Created: Jul 14, 2015
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
