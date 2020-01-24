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
public class DbLabeledFeature {
    private final int triggerid;
    private final LAF feature;

    public DbLabeledFeature(int triggerid, LAF feature) {
        this.triggerid = triggerid;
        this.feature = feature;
    }

    /**
     * @return the triggerid
     */
    public int getTriggerid() {
        return triggerid;
    }

    /**
     * @return the feature
     */
    public LAF getFeature() {
        return feature;
    }
    
}
