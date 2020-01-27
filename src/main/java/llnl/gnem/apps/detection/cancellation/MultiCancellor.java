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

import java.util.ArrayList;

import Jama.Matrix;
import com.oregondsp.io.SACFileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


//
// ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-ox+-
//0    ox+-ox+-ox+-
//     ox+-ox+-ox+-
//1                    ox+-ox+-ox+-
//0                                                        ox+-ox+-ox+-
//                                                         ox+-ox+-ox+-
//0                                                                ox+-ox+-ox+-
//                                                                 ox+-ox+-ox+-
//1                                                                        ox+-ox+-ox+-

public class MultiCancellor {
	
    private final ArrayList< float[][] > U;
    private final int                    ntemplates;
    private int                          nrows;
    private final float[]                x;
    private final int                    nch;
    private final int                    n;
    private final int                    N;
	
/// TODO:  reduce template set to those with detections	
	
    public MultiCancellor( CancellationTemplate[] templates, float[][] buffer ) {
        
        ntemplates = templates.length;
        nrows  = 0;
        
        for ( CancellationTemplate template : templates ) {
            Matrix u = template.getU();
            nrows    = Math.max( u.getRowDimension(), nrows );
        }
        
        U = new ArrayList<>();
        for ( int it = 0;  it < ntemplates;  it++ ) {
            
            Matrix u = templates[it].getU();
            int    nr = u.getRowDimension();
            int    nc = u.getColumnDimension();
            
            float[][] tmp = new float[nrows][nc];
            for ( int i = 0;  i < nr;  i++ ) {
                for ( int j = 0;  j < nc;  j++ ) {
                    tmp[i][j] = (float) u.get( i, j );
                }
            }
            U.add( tmp );
        }
            
        nch = buffer.length;
        n   = buffer[0].length;
        N   = n*nch;
        x   = new float[ N ];
        for ( int ich = 0;  ich < nch;  ich++ ) {
            for ( int i = 0;  i < n;  i++ ) {
                x[ i*nch + ich ] = buffer[ich][i];
            }
        }

    }
    
    
    
    public void cancel( ArrayList< Peak > peaks, float[][] residuals ) {
        
        int npeaks = peaks.size();
        
        int[] lags = new int[ npeaks ];
        int[] ids  = new int[ npeaks ];
        
        int ipeak = 0;
        int tdim  = 0;
        for ( Peak peak : peaks ) {
            lags[ipeak]  = peak.index;
            int id   = peak.ID;
            ids[ipeak++] = id;
            tdim    += U.get(id)[0].length;
        }
        
        Matrix R = new Matrix( tdim, tdim );
        Matrix c = new Matrix( tdim, 1 );

        int[] uindex = new int[ tdim ];
        int[] ucol   = new int[ tdim ];
        int[] lag    = new int[ tdim ];
        int col = 0;
        for ( ipeak = 0;  ipeak < npeaks;  ipeak++ ) {
            int dim = U.get( ids[ipeak] )[0].length;
            for ( int id = 0;  id < dim; id++ ) {
                uindex[ col ] = ids[ipeak];
                ucol[ col ]   = id;
                lag[ col ]    = lags[ipeak]*nch;
                col++;
            }
        }
        
        // compute c
        
        for ( int i = 0;  i < tdim;  i++ ) {
            float[][] u = U.get( uindex[ i ] );
            double tmp = 0.0;
            int    ci  = ucol[ i ];
            int    l   = lag[ i ];
            for ( int j = 0;  j < nrows;  j++ ) {
                tmp  +=  u[j][ci] * x[j+l];
            }
            c.set( i, 0, tmp );
        }
               
        // compute R

        for ( int i = 0;  i < tdim;  i++ ) {
            
            int li = lag[i];
            
            for ( int j = 0;  j < tdim;  j++ ) {
                
                int lj = lag[j];
                
                int low  = Math.max( li, lj );
                int high = Math.min( li+nrows-1, lj+nrows-1 );
                
                double tmp = 0.0;
                if ( high >= low ) {
                    
                    float[][] ui = U.get( uindex[i] );
                    int       ci = ucol[ i ];
                    
                    float[][] uj = U.get( uindex[j] );
                    int       cj = ucol[ j ];
                    
                    for ( int k = low;  k <= high;  k++ ) tmp  +=  ui[k-li][ci] * uj[k-lj][cj];
                }
                
                R.set( i, j, tmp );
            }
        }
        
        // compute a
        
        Matrix a = R.inverse().times( c );
//System.out.println( "R" );
//R.print(8,3);
//System.out.println( "c" );
//c.print(8,3);
//a.print(8,3);
        
        // cancel
        
        float[] e = new float[ x.length ];
        System.arraycopy( x, 0, e, 0, x.length );
        
        for ( int i = 0;  i < tdim;  i++ ) {
            float     fa = (float) a.get( i, 0 );
            int       l  = lag[i];
            int       ci = ucol[i];
            float[][] u  = U.get( uindex[i] );
            for ( int j = 0;  j < nrows;  j++ ) {
                e[j+l]  -=  fa *u[j][ci];
            }
        }
        
        
        for ( int ich = 0;  ich < nch;  ich++ ) {
            for ( int i = 0;  i < n;  i++ ) {
                residuals[ich][i] = e[ i*nch + ich ];
            }
        }

    }

    
    
        
    
    
    private void dumpSACfile( String path, float[] x ) {
        try {
            SACFileWriter writer = new SACFileWriter( path );
            writer.getHeader().b     = 0.0f;
            writer.getHeader().delta = 1.0f;
            writer.writeFloatArray( x );
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(DiscreteSegmentedCancellor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
