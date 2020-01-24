package llnl.gnem.apps.detection.core.framework.detectors.power;

import llnl.gnem.apps.detection.core.signalProcessing.OverlapAdd_dp;

import java.io.IOException;
import java.util.Arrays;
import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;
import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.apps.detection.core.framework.detectors.AbstractSimpleDetector;
import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;

/**
 *
 * Created originally by Dave Harris
 *
 * Implements the STA and LTA calculations on the data stream with FIR filters.
 *
 * Refactored by dodge1 Date: Oct 8, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 *
 * Modified by Dave Harris 11/30/2011 for NORSAR BAA11
 *
 * Modified by Dave Harris 2/27/2013 for NORSAR BAA11
 * 
 * Refactored by DBH on 12/15/2015 to incorporate channel lookup
 */
public class STALTADetector extends AbstractSimpleDetector {

    private static final double INITIAL_VALUE = 1000.0;
    private final OverlapAdd_dp shortTermAverage;
    private final OverlapAdd_dp longTermAverage;
    private final float[]       w;

    public STALTADetector( int                 detectorid,
                           STALTASpecification specification,
                           double              sampleRate,
                           String              streamName,
                           int                 decimatedBlockSize ) throws IOException {

        super( detectorid, sampleRate, streamName, decimatedBlockSize, specification );

        int STALength = (int) (specification.getSTADuration() * sampleRate);
        int gapLength = (int) (specification.getGapDuration() * sampleRate);
        int LTALength = (int) (specification.getLTADuration() * sampleRate);
        int N = STALength + gapLength + LTALength;

        double[] STAKernel = new double[N];
        Arrays.fill( STAKernel, 0.0 );
        double scale = 1.0 / ( (double) STALength );
        for ( int i = 0;  i < STALength;  i++ ) {
            STAKernel[i] = scale;
        }

        double[] LTAKernel = new double[N];
        Arrays.fill( LTAKernel, 0.0 );
        scale = 1.0f / ( (float) LTALength );
        for ( int i = 0;  i < LTALength;  i++ ) {
            LTAKernel[ i + gapLength + STALength ] = scale;
        }

        shortTermAverage = new OverlapAdd_dp( STAKernel, decimatedSegmentLength );
        longTermAverage  = new OverlapAdd_dp( LTAKernel, shortTermAverage );

        shortTermAverage.initialize( 0.0 );
        longTermAverage.initialize( INITIAL_VALUE );

        detectorDelayInSeconds = specification.getSTADuration() / 2.0;
        
        w = new float[ specification.getNumChannels() ];
        w[0] = 1.0f;
    }
    

    @Override
    public DetectionStatistic produceStatistic( TransformedStreamSegment segment ) {

        double[] instantaneousPower = new double[ decimatedSegmentLength ];
        Arrays.fill( instantaneousPower, 0.0 );

        for ( int i = 0;  i < getNumChannels();  i++ ) {
            float[] tmp = segment.getChannelData( getStaChanKey(i) );
            for ( int j = 0;  j < decimatedSegmentLength;  j++ ) {
                instantaneousPower[j] += tmp[j] * tmp[j] * w[i];
            }
        }

        double[] sta = new double[ decimatedSegmentLength ];
        shortTermAverage.filter( instantaneousPower, sta, 0 );
        double[] lta = new double[ decimatedSegmentLength ];
        longTermAverage.filter( lta, 0 );

        int offset = detectionStatistic.length - decimatedSegmentLength;
        for ( int i = 0;  i < decimatedSegmentLength;  i++ ) {
            if (lta[i] > 0.0f) {
                detectionStatistic[i + offset] = (float) (sta[i] / lta[i]);
            } else {
                detectionStatistic[i + offset] = 0.0f;
            }
        }
        DetectorInfo detectorInfo = new DetectorInfo( getdetectorid(),
                                                      getName(), 
                                                      getDetectorType(),
                                                      getProcessingDelayInSeconds(), 
                                                      getSpecification(), 
                                                      this.getDetectorDelayInSeconds(),
                                                      null, 
                                                      null, 
                                                      null);
        return new DetectionStatistic( detectionStatistic,
                segment.getStartTime(),
                segment.getSamplerate(),
                                       detectorInfo );
    }

}
