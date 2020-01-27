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

import Jama.Matrix;

import java.util.concurrent.Callable;

/**
 *
 * @author dbh
 */
public class BlockCancellor implements Callable< BlockCancellor >{
	
    private final int      nch;
    private final int      templateLength;
    private final int      dim;
    private final int      N;             // problem size (length of window)
    private final int      M;  
	
    private final Matrix   U;
    private final Matrix[] R;
    private final Matrix[] Rt;
    private final Matrix[] alpha;
    private final Matrix[] beta;	
    private final Matrix[] alpha_t;
    private final Matrix[] beta_t;
	
    private final Matrix[] C;
    private final Matrix[] a;
	
    private final float[]  x;
    private float[]        e;
    private int            pointer;
	
	
    public BlockCancellor( Matrix U, Matrix[] R, Matrix[] Rt, int nch, int N, double eps ) {
		
	this.U              = U;
        this.R              = R;
        this.Rt             = Rt;
        
        templateLength      = R.length;
        dim                 = U.getColumnDimension();
        
	this.nch            = nch;
	this.N              = N;
	M                   = N - templateLength + 1;
        
	alpha   = new Matrix[ M ];
	beta    = new Matrix[ M ];

	alpha_t = new Matrix[ M ];
	beta_t  = new Matrix[ M ];
		
	x = new float[ N*nch ];
		
	a = new Matrix[ M ];
                		
	C = new Matrix[M];
    }
	
	
	
    public  void setBlock( float[] x, int ptr ) {
        this.pointer = ptr;
        System.arraycopy( x, ptr*nch, this.x, 0, N*nch );
    }
	
	
	
    public float[] getResidual() {
	return e;
    }
	
	
	
    public int getIndex() {
        return pointer;
    }
	
	
	
    public  void computeC() {
		
        double[][] Ua = U.getArray();
        int n = U.getRowDimension();
		
        int ptr = 0;
        for ( int i = 0;  i < M;  i++ ) {
			
            C[i] = new Matrix( dim, 1 );
            double[][] Ca = C[i].getArray();
			
            for ( int id = 0;  id < dim;  id++ ) {
                double tmp = 0.0;
                for ( int j = 0;  j < n;  j++ )  tmp  +=  Ua[j][id] * x[ptr+j]; 
                Ca[id][0] = tmp;
            }
			
            ptr += nch;
        }
		
    }

	
	
    @Override
    public BlockCancellor call() throws Exception {
		
        computeC();
		
        // initialization
		
        alpha[0] = R[0].inverse();
        beta[0]  = alpha[0];
        a[0]     = alpha[0].times( C[0] );
		
        Matrix I = Matrix.identity( dim, dim );	
		
        // main iteration
		
        for ( int k = 1;  k < M;  k++ ) {
			
            Matrix mu    = new Matrix( dim, dim );
            Matrix gamma = new Matrix( dim, dim );
            Matrix eps   = new Matrix( dim, 1 );
			
            for ( int i = 1;  i <= Math.min( k, templateLength-1);  i++ ) {
                gamma = gamma.plus( Rt[i].times( alpha[k-i] ) );
                mu    = mu.plus(    R[i].times(  beta[i-1]  ) ); 
                eps   = eps.plus(   Rt[i].times( a[k-i]     ) );
            }
			
            Matrix theta  = I.minus( mu.times(gamma) ).inverse();
            Matrix phi    = gamma.times( theta ).times( -1.0 );
			
            Matrix phip   = I.minus( gamma.times( mu ) ).inverse();
            Matrix thetap = mu.times( phip ).times( -1.0 );
			
            alpha_t[0] = alpha[0].times( theta );
            alpha_t[k] = beta[k-1].times( phi );
			
            beta_t[0] = alpha[0].times( thetap );
            beta_t[k] = beta[k-1].times( phip );
			
            for ( int i = 1;  i < k;  i++ ) {
                alpha_t[i] = alpha[i].times( theta ).plus( beta[i-1].times(phi) );
                beta_t[i]  = alpha[i].times( thetap ).plus( beta[i-1].times(phip) );
            }
			
            for ( int i = 0;  i <= k;  i++ ) {
                alpha[i] = alpha_t[i];
                beta[i]  = beta_t[i];
            }
			
			
            Matrix d = C[k].minus( eps );
            for ( int i = 0;  i < k;  i++ ) {
                a[i] = a[i].plus( beta[i].times(d) );
            }
            a[k] = beta[k].times(d);
            
        }		

        int n = U.getRowDimension();
		
        e = x.clone();
		
        int ptr = 0;
        for ( int i = 0;  i < M;  i++ ) {
            Matrix tmp = U.times( a[i] );
            double[][] tmpa = tmp.getArray();
            for ( int j = 0;  j < n;  j++ )  e[ptr+j] -= (float) tmpa[j][0];
            ptr += nch;
        }
		
        return this;
    }

}
