/*
 * Author:  D. B. Harris
 * Created:  January 22, 2015
 */
package llnl.gnem.apps.detection.cancellation.subspace;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import llnl.gnem.apps.detection.cancellation.CancellationTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.MultichannelCorrelator;
import llnl.gnem.apps.detection.core.signalProcessing.OverlapAdd_dp;
import llnl.gnem.apps.detection.core.signalProcessing.RFFTdp;
import llnl.gnem.apps.detection.core.signalProcessing.Sequence;


/**
 * Revised by D. Harris from earlier versions on January 22-23, 31, 2015
 * More revisions 2/13/2015 and 2/18/2015
 *
 */
public class CancellingSubspaceDetector {
	
    private final CancellationTemplate                          template;
    private final int                               ID;

    private final ArrayList<MultichannelCorrelator> correlators;
    private final OverlapAdd_dp                     envelopeSmoother;
    private final double[]                          multichannelCorrelation;
    private final double[]                          projection;
    private final double[]                          envelope;
    private final double[]                          sumsqr;
    private final int                               ndim;
    private final int                               nchannels;
    private final int                               segmentLength;
    private final ArrayList< double[] >             dataDFT;
    
    private final RFFTdp                            fft;

    

    public CancellingSubspaceDetector( CancellationTemplate template, int segmentLength, int ID ) throws IOException {
    	
        this.template       = template;
        this.nchannels      = template.getNumChannels();
        this.segmentLength  = segmentLength;
        this.ID             = ID;
        
        int templateLength = template.getTemplateLength();
        
        // calculate FFT size

        int N = 1;
        int log2N = 0;
        while ( N < segmentLength + templateLength - 1 ) {
            N *= 2;
            log2N++;
        }
        
        // instantiate fft and allocate space for dfts of data
        
        fft = new RFFTdp( log2N );
        
        dataDFT = new ArrayList<>();
        for ( int ich = 0;  ich < nchannels;  ich++ ) {
          dataDFT.add( new double[N] );
        }

        // instantiate MultichannelCorrelators
        
        ndim = template.getDimension();
        
        correlators = new ArrayList<>();
        for ( int idim = 0;  idim < ndim;  idim++ ) {

            correlators.add( new MultichannelCorrelator( template.getComponent( idim ),
                                                         nchannels,
                                                         templateLength,
                                                         segmentLength,
                                                         log2N    )  );
        }

        multichannelCorrelation = new double[ segmentLength ];
        projection              = new double[ segmentLength ];
        envelope                = new double[ segmentLength ];
        sumsqr                  = new double[ segmentLength ];

        for ( MultichannelCorrelator MC : correlators ) {
            MC.init();
        }
        
        double[] kernel = new double[ templateLength ];
        Arrays.fill(kernel, 1.0f);
        envelopeSmoother = new OverlapAdd_dp( kernel, segmentLength );
        envelopeSmoother.initialize( 0.0 );

    }
    
    

    public float[] produceStatistic( float[][] data, int ptr ) {

        // calculate power over all channels and DFTs of channel data

        Sequence.zero(sumsqr);

        for (int ich = 0; ich < nchannels; ich++) {
            float[] d = data[ich];
            double[] dtmp = dataDFT.get( ich );          
            Sequence.zero( dtmp );
            for ( int j = 0;  j < segmentLength;  j++ ) dtmp[j] = d[j+ptr];
            fft.dft( dtmp );        
            for (int j = 0; j < segmentLength; j++) {
                sumsqr[j] += d[j+ptr] * d[j+ptr];
            }           
        }

        // projection calculation

        Sequence.zero(projection);

        for ( int idim = 0;  idim < ndim;  idim++ ) {
            correlators.get(idim).correlate( dataDFT, multichannelCorrelation );
            for (int i = 0; i < segmentLength; i++) {
                projection[i] += multichannelCorrelation[i] * multichannelCorrelation[i];
            }
        }
        
        // envelope calculation

        envelopeSmoother.filter( sumsqr, envelope, 0 );
        
        float[] detectionStatistic = new float[ segmentLength ];

        for (int i = 0; i < segmentLength; i++) {
            if ( envelope[i] <= 0.0f ) {
                detectionStatistic[ i] = 0.0f;
            } else {
                detectionStatistic[ i ] = (float) (projection[i] / envelope[i]);
            }
        }
        
        return detectionStatistic;
    }
    
    
    
    public int getID(){
        return ID;
    }

    
    
    public CancellationTemplate getTemplate() {
    	return template;
    }
    
    
    
    public String toString() {
        return ID + "  " + template.toString();
    }
}
