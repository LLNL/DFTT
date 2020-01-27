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
package llnl.gnem.apps.detection.core.framework.detectors.subspace;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import java.util.ArrayList;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.signalProcessing.RFFTdp;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.StreamKey;

public class Projection {


    private int     delay;
    private float   cmax;



    public Projection( SubspaceTemplate existingTemplate, ArrayList< float[][] > newTemplateRepresentation, ArrayList< StreamKey > chanIDs, int shiftRange ) {

      int nchannels      = existingTemplate.getnchannels();
      int templateLength = existingTemplate.getTemplateLength();
      
      if ( nchannels != chanIDs.size() ) {
        throw new IllegalStateException( "Templates being compared have different numbers of channels" );
      }
      
      ArrayList< StreamKey > existingKeys = existingTemplate.getStaChanList();
      
      for ( int j = 0;  j < nchannels;  j++ ) {
          if ( !existingKeys.get(j).equals( chanIDs.get(j) ) ) throw new IllegalStateException( "Channel mismatch: " + existingKeys.get(j) + "  " + chanIDs.get(j) );
      }
      
      int template2length = newTemplateRepresentation.get(0)[0].length;
      if ( templateLength != template2length ) {
        throw new IllegalStateException( "Templates being compared have different lengths" );
      }
        
      Matrix U1t  = existingTemplate.getU().transpose();
      int    dim2 = newTemplateRepresentation.size();
      Matrix U2   = new Matrix( template2length*nchannels, dim2 );
      double[][] U2a = U2.getArray();
      for ( int idim = 0;  idim < dim2;  idim++ ) {
        float[][] tmp = newTemplateRepresentation.get(idim);
        for ( int ich = 0;  ich < nchannels;  ich++ ) {
          int j = ich*templateLength;
          for ( int i = 0;  i < templateLength;  i++ ) {
            U2a[j+i][idim] = tmp[ich][i];
          }
        }
      }
      
      cmax  = 0.0f;
      delay = 0;
      
      for ( int shift = -shiftRange;  shift <= shiftRange;  shift++ ) {
      
        Matrix U2s = shiftTemplate( U2, shift, nchannels, templateLength );
        Matrix C   = U1t.times(U2);
        SingularValueDecomposition svd = new SingularValueDecomposition( C );
        float c = (float) svd.getSingularValues()[0];
        if ( c > cmax ) {
          cmax  = c;
          delay = -shift;
        }
                
      }

    }
    
    
    
    public Projection( SubspaceTemplate template1, SubspaceTemplate template2 ) {
        findAlignedProjections( template1, template2, -1 );
    }
    
    
    
    public Projection( SubspaceTemplate template1, SubspaceTemplate template2, int n ) {
        findAlignedProjections( template1, template2, n );
    }
    
    
    
    private void findAlignedProjections( SubspaceTemplate template1, SubspaceTemplate template2, int n ) {
        
        if ( !template1.consistent( template2 ) ) throw new IllegalStateException( "Templates are inconsistent" );
        
        int nch = template1.getnchannels();
        
        int n1 = template1.getTemplateLength();
        int n2 = template2.getTemplateLength();
        
        if ( n == -1 ) n = Math.max( n1, n2 );
        
        if ( n < Math.max( n1, n2 ) ) {
            throw new IllegalStateException( "Projection - value for parameter n < max( n1, n2 ), template lengths" );
        }
        
        // find fft size
        
        int log2N = 2;
        int N    = 4;
        while ( N < 2*n - 1 ) {
            log2N++;
            N *= 2;
        }
        
        // get dimensions of templates
        
        int dim1 = template1.getdimension();
        int dim2 = template2.getdimension();
        String msg = String.format("Template1 of dimension %d and length %d, Template2 of dimension %d and length %d",dim1,n1, dim2,n2);
        ApplicationLogger.getInstance().log(Level.FINE, msg);
        
        // pull templates into double[dimension][channel][samples]
        
        ArrayList< float[][] > t1 = template1.getRepresentation();
        ArrayList< float[][] > t2 = template2.getRepresentation();
        
        double[][][] r1 = new double[ dim1 ][ nch ][ N ];
        double[][][] r2 = new double[ dim2 ][ nch ][ N ];
        
        // unpack first template
        int usableDim = Math.min(dim1, dim2);
        for ( int idim = 0;  idim < usableDim;  idim++ ) {
            
            float[][] proxy = t1.get( idim );
            for ( int ich = 0;  ich < nch;  ich++ ) {
                float[] p = proxy[ich];
                for ( int i = 0;  i < n1;  i++ ) r1[idim][ich][i] = p[i];
            }

        }
             
        // unpack second template - don't assume channels are in the same order
        //   as in the first template
        
        ArrayList< StreamKey > channels1 = template1.getStaChanList();
        ArrayList< StreamKey > channels2 = template2.getStaChanList();
        
        for ( int idim = 0;  idim < usableDim;  idim++ ) {
            
            float[][] proxy = t2.get( idim );
            for ( int ich = 0;  ich < nch;  ich++ ) {
                int index = channels2.indexOf( channels1.get(ich) );
                float[] p = proxy[ index ];
                for ( int i = 0;  i < n2;  i++ ) r2[idim][ich][i] = p[i];
            }

        }
 
        // calculate correlations
        
        double[][][] c = new double[ N ][ dim1 ][ dim2 ];
        
        for ( int idim1 = 0;  idim1 < usableDim;  idim1++ ) {
            for ( int idim2 = 0;  idim2 < dim2;  idim2++ ) {
                double[] tmp = correlate( r1[ idim1 ], r2[ idim2 ], nch, N, log2N );
                for ( int i = 0;  i < N;  i++ ) {
                    c[i][idim1][idim2] = tmp[i];
                }
            }
        }
        
        // calculate SVDs
        
        int maxDelay = 0;
        cmax         = 0.0f;
        
        //   positive lags
        
        for ( int i = 0;  i < n2;  i++ ) {
            Matrix C = new Matrix( c[i] );
            SingularValueDecomposition svd = new SingularValueDecomposition( C );
            float ct = (float) svd.getSingularValues()[0];
            if ( ct > cmax ) {
                cmax  = ct;
                delay = i;
            }
        }
        
        //   negative lags
        
        for ( int i = 1;  i < n1;  i++ ) {
            Matrix C = new Matrix( c[N-i] );
            SingularValueDecomposition svd = new SingularValueDecomposition( C );
            float ct = (float) svd.getSingularValues()[0];
            if ( ct > cmax ) {
                cmax  = ct;
                delay = -i;
            }
        }
        
    }
    
    
    
    public static Matrix checkTemplate( SubspaceTemplate template ) {
        
        ArrayList< float[][] > r = template.getRepresentation();
        int nch  = template.getnchannels();
        int n    = template.getTemplateLength();
        int ndim = template.getdimension();
        
        double[][] Ta = new double[ n*nch ][ ndim ];
        
        for ( int id = 0;  id < ndim;  id++ ) {
            float[][] dim = r.get( id );
            for ( int ich = 0;  ich < nch;  ich++ ) {
                for ( int i = 0;  i < n;  i++ ) Ta[ ich*n + i ][ id ] = dim[ich][i];
            }
        }
        
        Matrix T = new Matrix( Ta );
        
        return T.transpose().times( T );        
    }
    
    
    
    private double[] correlate( double[][] x, double[][] y, int nch, int N, int log2N ) {
        
        RFFTdp fft = new RFFTdp( log2N );
        
        double[] accum = new double[ N ];
        double[] c     = new double[ N ];
        
        double[] tx    = new double[ N ];
        double[] ty    = new double[ N ];
        
        for ( int ich = 0;  ich < nch;  ich++ ) {
            System.arraycopy( x[ich], 0, tx, 0, N );
            System.arraycopy( y[ich], 0, ty, 0, N );
            fft.dft( tx );
            fft.dft( ty );
            fft.dftproduct( tx, ty, c, -1 );
            fft.idft( c );
            for ( int i = 0;  i < N;  i++ ) accum[i] += c[i];
        }
        
        return accum;
    }
    
    
    
    private static Matrix shiftTemplate( Matrix U, int shift, int nchannels, int templateLength ) {
        
      int nrows = nchannels*templateLength;
      int ncols = U.getColumnDimension();
      
      Matrix retval;
      if ( shift == 0 ) {
          
        retval = U.copy();
        
      }
      else {
          
        retval = new Matrix( nrows, ncols );
        double[][] newArray = retval.getArray();
        double[][] Ua       = U.getArray();
          
        if ( shift > 0 ) {
          
          for ( int icol = 0;  icol < ncols;  icol++ ) {
            for ( int ich = 0;  ich < nchannels;  ich++ ) {
              int j = ich*templateLength;
              for ( int i = 0;  i < templateLength-shift;  i++ ){
                newArray[j+i+shift][icol] = Ua[j+i][icol];
              }
            }
          }
            
        }
        else {  // shift < 0
          
          for ( int icol = 0;  icol < ncols;  icol++ ) {
            for ( int ich = 0;  ich < nchannels;  ich++ ) {
              int j = ich*templateLength;
              for ( int i = -shift;  i < templateLength;  i++ ){
                newArray[j+i+shift][icol] = Ua[j+i][icol];
              }
            }
          }
         
        }
        
      }
      
      return retval;
    }
    
    

    public static ArrayList< float[] > getRegisteredSegment( SubspaceTemplate template, float[][] preprocessedDataFromStream ) {
        
      // construct projection matrix from template
        
      int nchannels      = template.getnchannels();
      int templateLength = template.getTemplateLength();

      Matrix U  = template.getU().transpose();

      int N = preprocessedDataFromStream[0].length;
      
      int nsteps = N - templateLength + 1;
      
      int    bestFit      = 0;
      
      Matrix x = new Matrix( nchannels*templateLength, 1 );
      
      if ( nsteps > 1 ) {

        int    ptr          = 0;
        double bestFitValue = 0.0;
        while ( ptr < nsteps ) {

          for ( int ich = 0;  ich < nchannels;  ich++ ) {
            int j = ich*templateLength;
            for ( int i = 0;  i < templateLength;  i++ ) {
              x.set( j+i, 0, preprocessedDataFromStream[ich][i+ptr] ); 
            }
          }
          
          Matrix a = U.times( x );
          double value = ( a.transpose().times(a) ).get(0,0);
          if ( value > bestFitValue ) {
            bestFitValue = value;
            bestFit = ptr;
          }
          
          ptr++;
        }
        
      }
      
      ArrayList< float[] > retval = new ArrayList<>();
      for ( int ich = 0;  ich < nchannels;  ich++ ) {
        float[] tmp = new float[ templateLength ];
        System.arraycopy( preprocessedDataFromStream[ich], bestFit, tmp, 0, templateLength );
        retval.add( tmp );
      }
        
      return retval;   
    }
    
    

    public int getDecimatedDelay() {
        return delay;
    }
    
    

    public float getProjectionValue() {
        return cmax;
    }
    
     
}
