/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.dataObjects;

import java.util.Collection;
import java.util.Map;
import llnl.gnem.apps.detection.classify.TriggerClassification;
import llnl.gnem.core.util.PairT;

/**
 *
 * @author dodge1
 */
public class DetectionObjects {

    private final Map<Integer, TriggerClassification> triggerClassificationMap;
    private final Collection<PairT<Integer, Double>> detTimes;
    private final Collection<Double> U;
    private final int maxDetectionId;

    public DetectionObjects(Map<Integer, 
            TriggerClassification> triggerClassificationMap, 
            Collection<PairT<Integer, 
                    Double>> detTimes, Collection<Double> U, 
                    int maxDetectionId) {
        this.triggerClassificationMap = triggerClassificationMap;
        this.detTimes = detTimes;
        this.U = U;
        this.maxDetectionId = maxDetectionId;
    }

    public Map<Integer, TriggerClassification> getTriggerClassificationMap() {
        return triggerClassificationMap;
    }

    public Collection<PairT<Integer, Double>> getDetTimes() {
        return detTimes;
    }

    public Collection<Double> getU() {
        return U;
    }

    public int getMaxDetectionId() {
        return maxDetectionId;
    }
    
    public int getRowsRetrieved() {
        return triggerClassificationMap.size();
    }
    

}
