/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.dataObjects;

/**
 *
 * @author dodge1
 */
public class SubspaceParameters {
    private final double detectionThreshold;
    private final double energyCapture;
    private final double blackoutSeconds;
    
    public SubspaceParameters(double detectionThreshold,double energyCapture,double blackoutSeconds)
    {
        this.detectionThreshold = detectionThreshold;
        this.energyCapture= energyCapture;
        this.blackoutSeconds = blackoutSeconds;
    }

    /**
     * @return the detectionThreshold
     */
    public double getDetectionThreshold() {
        return detectionThreshold;
    }

    /**
     * @return the energyCapture
     */
    public double getEnergyCapture() {
        return energyCapture;
    }

    /**
     * @return the blackoutSeconds
     */
    public double getBlackoutSeconds() {
        return blackoutSeconds;
    }
}
