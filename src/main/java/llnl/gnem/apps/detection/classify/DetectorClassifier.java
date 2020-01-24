/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.classify;

/**
 *
 * @author dodge1
 */
public interface DetectorClassifier {
    TriggerClassification classifyTrigger( LabeledFeature feature ) throws Exception;
    boolean isClassifierUsable();
}
