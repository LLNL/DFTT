/*
 * Developed for BAA11-26 by Deschutes Signal Processing LLC
 */

package llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation;

import llnl.gnem.apps.detection.core.framework.detectors.subspace.SingleChannelCorrelator;
import llnl.gnem.apps.detection.core.signalProcessing.OverlapAdd_dp;
import llnl.gnem.apps.detection.core.signalProcessing.RFFTdp;
import llnl.gnem.apps.detection.core.signalProcessing.Sequence;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author harris2
 */
public class SingleChannelSubspace {
    
    private final ArrayList< SingleChannelCorrelator > correlators;
    private final OverlapAdd_dp                        envelopeSmoother;
    private final double[]                             correlationTrace;
    private final double[]                             projection;
    private final double[]                             envelope;
    private final double[]                             power;
    private final int                                  ndim;
    private final int                                  segmentLength;

    
    
    public SingleChannelSubspace( float[][] template,
                                  int       ndim,
                                  int       templateLength,
                                  int       segmentLength,
                                  int       log2FFTSize       ) {

      this.ndim    = ndim;
      this.segmentLength = segmentLength;

      RFFTdp fft = new RFFTdp( log2FFTSize );

      correlators = new ArrayList< SingleChannelCorrelator >();

      for ( int id = 0;  id < this.ndim;  id++ )
        correlators.add( new SingleChannelCorrelator( template[id], segmentLength, fft ) );
      
      correlationTrace = new double[ segmentLength ];
      projection       = new double[ segmentLength ];
      envelope         = new double[ segmentLength ];
      power            = new double[ segmentLength ];
      
      double[] kernel = new double[ templateLength ];
      Arrays.fill(kernel, 1.0f);
      envelopeSmoother = new OverlapAdd_dp( kernel, segmentLength );
    }


    
    public void init() {
      for ( SingleChannelCorrelator COA : correlators ) COA.initialize();
      envelopeSmoother.initialize( 0.0 );
    }


    /**
     * Calculates the single channel subspace detection statistic.
     *
     * @param data    float[]  containing single-channel waveform
     * @param dataDFT double[] containing DFT of single-channel waveform.
     * @param detstat double[] containing detection statistic for this channel.
     */
    public void calculateStatistic( float[]  data,
                                    double[] dataDFT,
                                    double[] detstat   ) {
        
        // calculate power and envelope

        for ( int i = 0;  i < segmentLength;  i++ ) power[i] = data[i]*data[i];
        envelopeSmoother.filter( power, envelope, 0 );

        // projection calculation

        Sequence.zero( projection );

        for ( int idim = 0;  idim < ndim;  idim++ ) {
          correlators.get(idim).filter( dataDFT, correlationTrace );
          for ( int i = 0;  i < segmentLength;  i++ ) {
            projection[i]  +=  correlationTrace[i] * correlationTrace[i];
          }
        }

        for ( int i = 0;  i < segmentLength;  i++ ) {
            if ( envelope[i] <= 0.0f ) {
                detstat[ i ] = 0.0f;
            } else {
                detstat[ i ] = (float) (projection[i] / envelope[i]);
            }
        }

  }
    
}
