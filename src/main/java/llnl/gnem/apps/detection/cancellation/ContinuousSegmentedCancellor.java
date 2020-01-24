/*
 * Copyright (C) 2015 Deschutes Signal Processing LLC
 * Author:  David B. Harris
 */
package llnl.gnem.apps.detection.cancellation;

import Jama.Matrix;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author dbh
 */
public class ContinuousSegmentedCancellor extends AbstractSegmentedCancellor {
    	
    private static final int             NTASKS   = 8;
    private static final ExecutorService service  = Executors.newFixedThreadPool(NTASKS );
	  
    private final ExecutorCompletionService< BlockCancellor >  completionService;
    
    
    private final int      N;
    private final float[]  x;
    private final int      dim;
    private final int      templateLength;
    private final Matrix   U;
    private final Matrix[] R;
    private final Matrix[] Rt;
    
    private final ArrayList< BlockCancellor > blockCancellors;
    
    
    public ContinuousSegmentedCancellor( int        segmentLength, 
                double     delta, 
                CancellationTemplate[] templates, 
                double     eps )        {
        
        super( segmentLength, delta, templates );
        
        // combine templates
        
        N = 2*segmentLength;
        x = new float[ N*numChannels ];
        
        // concatenate columns of template matrices
        
        int ntemplates = templates.length;
        int d  = 0;
        int tL = 0;
        for ( int it = 0;  it < ntemplates;  it++ ) {
            tL = Math.max( tL, templates[it].getTemplateLength() );
            d += templates[it].getDimension();
        }
        
        templateLength = tL;
        dim            = d;

	double[][] Ua = new double[ templateLength*numChannels ][ dim ];
	  
	int columnOffset = 0;
	for ( int it = 0;  it < ntemplates;  it++ ) {
		  
            Matrix     u  = templates[ it ].getU();
            double[][] ua = u.getArray();
            d  = u.getColumnDimension();
            System.out.println( "  template " + it + " dimension: " + d );

            for ( int irow = 0;  irow < ua.length;  irow++ ) {
                for ( int icol = 0;  icol < ua[0].length;  icol++ ) Ua[irow][icol + columnOffset] = ua[ irow ][ icol ];
            }
            columnOffset += d;
	}
	U  = new Matrix( Ua );        

	R  = new Matrix[ templateLength ];
	Rt = new Matrix[ templateLength ];
		
	int nrows = numChannels*templateLength;
		
	for ( int p = 0;  p < templateLength;  p++ ) {
			
            R[p]       = new Matrix( dim, dim );
            int offset = p*numChannels;
			
            for ( int i = 0;  i < dim;  i++ ) {
				
		for ( int j = 0;  j < dim;  j++ ) {
					
                    double tmp = 0.0;
                    for ( int k = offset;  k < nrows;  k++ ) {
			tmp  +=  Ua[k][i]*Ua[k-offset][j];
                    }
					
                    R[p].set( i, j, tmp );
		}
				
            }
			
            Rt[p] = R[p].transpose();
        } 
		
        R[0].timesEquals(  1.0 + eps );
        Rt[0].timesEquals( 1.0 + eps );
        
        blockCancellors = new ArrayList<>();
        for ( int i = 0;  i < NTASKS;  i++ ) blockCancellors.add( new BlockCancellor( U, R, Rt, numChannels, 3*segmentLength/NTASKS, eps ) );
        
        completionService = new ExecutorCompletionService<>( service );
    }
    
    // |...............................|...............................
    // |***|***|***|***|***|***|***|***|***|***|***|***|***|***|***|***
    //             |----------|
    //                 |----------|
    //                     |----------|
    //                         |----------|
    //                             |----------|
    //                                 |----------|
    //                                     |----------|
    //                                         |----------|
    
    @Override
    public void processSegment() {

        for ( int ich = 0;  ich < numChannels;  ich++ ) {
            for ( int i = 0;  i < N;  i++ ) {
                x[ i*numChannels + ich ] = buffer[ich][i];
            }
        }
        
        int n = segmentLength/NTASKS;
        
        // submitting tasks
        
        int ptr = 3*n;
        for ( int it = 0;  it < NTASKS;  it++ ) {
            
            BlockCancellor bc = blockCancellors.get(it);
            bc.setBlock( x, ptr );
            completionService.submit( bc );
            System.out.println( "Submitting task for block:  " + ptr );
            
            ptr += n;
        }
	  
	// process completed tasks
	  
	for ( int it = 0;  it < NTASKS;  it++ ) {
	  		  
            Future< BlockCancellor > future;
            try {
	  	future = completionService.take();
	  	BlockCancellor bc = future.get();
	  	float[] resid  = bc.getResidual();
	  	int     offset = bc.getIndex();
	  	for ( int ich = 0;  ich < numChannels;  ich++ ) {
                    for ( int i = 0;  i < n;  i++ ) {
	  		residuals[ich][i + offset + n] = resid[ (i+n)*numChannels + ich];
                    }
                }
                System.out.println( "Finished block:  " + bc.getIndex() );
            } catch (InterruptedException | ExecutionException exception) {
                exception.printStackTrace();
            }

	}
        
    }
    

    
    @Override
    public void print( PrintStream ps ) {
        for (CancellationTemplate template : templates) {
            ps.println(template.toString());
        }
    }
    

	
    public void shutdown() {
	service.shutdownNow();
    }
    
}
