/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework;

import Jampack.JampackException;
import java.io.IOException;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayDetectorSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayPowerDetector;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationDetector;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTADetector;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTASpecification;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;

/**
 *
 * @author dodge1
 */
public class FileBasedDetectorFactory {

    public static Detector createDetectorFromFiles( DetectorSpecification   spec,
                                                    int                     detectorid,
                                                    ConcreteStreamProcessor processor,
                                                    int                     decimatedBlockSize    ) throws IOException, UnsupportedOperationException, JampackException {
        PreprocessorParams params              = processor.getParams();
        String             streamName          = processor.getStreamName();
        double             decimatedSampleRate = params.getPreprocessorParams().getDecimatedSampleRate();
        
        Detector detector = null;
        switch ( spec.getDetectorType() ) {
            case SUBSPACE:
                detector = new SubspaceDetector( detectorid, (SubspaceSpecification) spec, params, decimatedSampleRate, streamName, processor.getFFTSize(), decimatedBlockSize );
                break;
            case ARRAY_CORRELATION:
                detector = new ArrayCorrelationDetector(detectorid, (ArrayCorrelationSpecification) spec, params, decimatedSampleRate, streamName, decimatedBlockSize);
                break;
            case STALTA:
                detector = new STALTADetector( detectorid, (STALTASpecification) spec,  decimatedSampleRate, streamName, decimatedBlockSize );
                break;
            case ARRAYPOWER:
                detector = new ArrayPowerDetector( detectorid, (ArrayDetectorSpecification) spec, decimatedSampleRate, streamName, decimatedBlockSize );
                break;
            case FSTATISTIC:
                throw new UnsupportedOperationException( "FStatistic not implemented" );
            default:
                throw new UnsupportedOperationException( "Detector type unspecified" );
        }
        return detector;
    }
    
}
