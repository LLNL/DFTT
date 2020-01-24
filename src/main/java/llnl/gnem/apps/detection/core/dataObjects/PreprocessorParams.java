/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.dataObjects;

import llnl.gnem.apps.detection.core.framework.detectors.PersistentProcessingParameters;


/**
 *
 * @author dodge1
 */
public interface PreprocessorParams {

    int                            getPreprocessorFilterOrder();

    int                            getDecimatedDataBlockSize();

    int                            getDecimationRate();

    double                         getPassBandHighFrequency();

    double                         getPassBandLowFrequency();

    int                            getDataBlockSize();
    
    PersistentProcessingParameters getPreprocessorParams();
    
    PreprocessorParams             changeBlockSize(double blockSizeSeconds);
    
    double                         getSampleRate();
}
