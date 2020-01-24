/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.classify;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author dodge1
 */
public class ClassifierManager {
    private final Map<Integer, DetectorClassifier> classifierMap;
    private ClassifierManager() {
        classifierMap = new ConcurrentHashMap<>();
    }
    
    public static ClassifierManager getInstance() {
        return ClassifierManagerHolder.INSTANCE;
    }
    
    private static class ClassifierManagerHolder {

        private static final ClassifierManager INSTANCE = new ClassifierManager();
    }
    
    public DetectorClassifier getClassifier(int streamid ) throws Exception
    {
        DetectorClassifier classifier = classifierMap.get(streamid);
        if( classifier == null ){
            classifier = new RandomForestTwoStageDetectorClassifier(streamid);
            classifierMap.put(streamid, classifier);
            return classifier;
        }
        else
        return classifier;
    }
}
