/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay;

import java.util.Collection;
import llnl.gnem.apps.detection.core.dataObjects.TriggerDataFeatures;

/**
 *
 * @author dodge1
 */
public interface SingleDetectionView {

    public void traceWasAdded();

    public void clear();

    public void setTriggerStatistics(TriggerDataFeatures result);

    public void setFeatureValues(String featureName, Collection<Double> result);

    public void detectionRetrieved();
    
}
