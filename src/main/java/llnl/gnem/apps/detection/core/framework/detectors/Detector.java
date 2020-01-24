// Software developed for AFRL BAA10-20 by Deschutes Signal Processing LLC
// Author:  David B. Harris
// Modified:  12/15/2015 to add getNumChannels and getStaChanKey  DBH
//
package llnl.gnem.apps.detection.core.framework.detectors;

import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;
import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.core.util.StreamKey;

public interface Detector {

    int                   getdetectorid();

    DetectorType          getDetectorType();

    double                getProcessingDelayInSeconds();

    DetectionStatistic    calculateDetectionStatistic( TransformedStreamSegment segment );

    String                getName();

    DetectorSpecification getSpecification();

    double                getDetectorDelayInSeconds();
    
    int                   getNumChannels();
    
    StreamKey            getStaChanKey( int index );
    
}
