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
package llnl.gnem.apps.detection.core.framework.localImplementation;

import Jampack.JampackException;
import Jampack.JampackParameters;

import com.oregondsp.util.TimeStamp;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.dataObjects.SpecificationFactory;
import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;
import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;
import llnl.gnem.apps.detection.core.framework.localImplementation.statistics.DetectionStatisticWriter;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.util.StreamKey;

/**
 * Author: Dave Harris
 *
 */
public class DetectionFramework {

    private ContinuousDataStream         dataStream;                     // Acquires continuous, multichannel data and serves it as segments
    private final ArrayList<Detector>    detectorList;                   // A list of detectors of varying types - each detector
    // produces a detection statistic and stores it for both
    // data segments
    private final StreamPrescription     streamPrescription;
    private final ProcessingPrescription processingPrescription;
    private final String                 streamName;
    private int segmentLength;

    public DetectionFramework( String ProcessingSpecificationFile,
            String StreamSpecificationFile,
                               String DetectorSpecificationFile    ) throws IOException, JampackException, ParseException, SQLException {

        streamPrescription     = new StreamPrescription( StreamSpecificationFile );
        processingPrescription = ProcessingPrescription.getInstance();
        processingPrescription.initialize( ProcessingSpecificationFile );

        segmentLength = processingPrescription.getDataBlockSize();

        dataStream    = new MultichannelSACDataStream( streamPrescription, processingPrescription );

        streamName    = streamPrescription.getStreamName();

        StreamKey[] tmpc = dataStream.getStaChanArray();
        ArrayList< StreamKey > chans = new ArrayList<  >();
        for ( StreamKey sck : tmpc ) chans.add( sck );
        Collections.sort( chans );
        System.out.println( "\nStream channels: " );
        for ( StreamKey chan : chans ) System.out.println( chan );
        System.out.println();

        detectorList = new ArrayList<>();

        DetectorSpecification spec = SpecificationFactory.getSpecification( DetectorSpecificationFile );

        spec.printSpecification( System.out );

        Detector detector = null;

        switch ( spec.getDetectorType() ) {
            case SUBSPACE:
                detector = new SubspaceDetector( 1, 
                                                 (SubspaceSpecification) spec, 
                                                 processingPrescription, 
                                                 dataStream.getSamplingRate(), 
                                                 streamName, 
                                                 dataStream.getFFTSize(), 
                                                 processingPrescription.getDecimatedDataBlockSize() );
                ApplicationLogger.getInstance().log(Level.INFO, String.format("Created subspace detector %d with threshold = %f.", detector.getdetectorid(),detector.getSpecification().getThreshold()));
                break;
//            case STALTA:
//                detector = new STALTADetector( persistence, (STALTASpecification) spec, null, preprocessor, preprocessor.getDecimatedSamplingRate(), preprocessor.getStreamName() );
//                break;
//            case ARRAYPOWER:
//                detector = new ArrayPowerDetector( persistence, (ArrayDetectorSpecification) spec, null, preprocessor, preprocessor.getDecimatedSamplingRate(), preprocessor.getStreamName() );
//                break;
            case FSTATISTIC:
                throw new UnsupportedOperationException( "FStatistic not implemented" );
            default:
                throw new UnsupportedOperationException( "Detector type unspecified" );
        }
        detectorList.add( detector );
        maybeInitializeWriter( detector );

//    System.out.println( "Detector delay in samples:  " + ((ArrayDetector1) detector ).getProcessingDelayInSamples() );

    }

    

    public void run() throws Exception {

        System.out.println( "# stream samples: " + dataStream.numSamplesAvailable() );

        while ( dataStream.numSamplesAvailable() > 0 ) {

            TimeStamp T = dataStream.getTimeStamp();

            System.out.println(T);

            TransformedStreamSegment segment = dataStream.getSegment( segmentLength );

            for ( Detector D : detectorList ) {
                DetectionStatistic statistic = D.calculateDetectionStatistic( segment );
                maybeWriteStatistic( statistic );
            }

        }

    }

    
    
    private void maybeWriteStatistic ( DetectionStatistic statistic ) {
        
        try {
            DetectionStatisticWriter.getInstance().appendData( statistic.getDetectorInfo().getDetectorName(),
                                                               statistic.getStatistic(),
                                                               new TimeStamp(statistic.getTime().getEpochTime()),
                                                               statistic.getDetectorInfo().getProcessingDelay() );
        } catch (IOException ex) {
            Logger.getLogger( DetectionFramework.class.getName()).log(Level.WARNING, "Failed appending detection statistic!", ex );
        }
    }

    
    
    private void maybeInitializeWriter( Detector detector ) {
                      
        double decimatedSampleRate = processingPrescription.getSampleRate() / processingPrescription.getDecimationRate();
        double sampleInterval      = 1.0 / decimatedSampleRate;
        try {
            DetectionStatisticWriter.getInstance().initializeWriter( streamName,
                                                                     detector.getName(),
                                                                     sampleInterval,
                                                                     detector.getDetectorType(),
                                                                     detector.getdetectorid()   );
        } catch (FileSystemException ex) {
            Logger.getLogger( DetectionFramework.class.getName()).log( Level.WARNING, "Failed initializing statistic writer!", ex );
        }
    }

    

    public void releaseResources() throws IOException {
        dataStream.close();
    }

    

    public static void main(String[] args) {

        try {
            JampackParameters.setBaseIndex(0);
        } catch (JampackException e) {
            e.printStackTrace();
        }

        if (args.length != 3) {
            System.err.println("Usage: detection.localImplementation.DetectionFramework <Processing Specification File> <Stream Specification File> <Detector Specification File>");
            System.exit(0);
        }
        try {
            DetectionFramework framework = new DetectionFramework(args[0], args[1], args[2]);

            long t0 = System.currentTimeMillis();
            framework.run();
            long t1 = System.currentTimeMillis();

            System.out.println( "Elapsed time: " + ((t1 - t0) / 1000.0) );
            framework.releaseResources();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
