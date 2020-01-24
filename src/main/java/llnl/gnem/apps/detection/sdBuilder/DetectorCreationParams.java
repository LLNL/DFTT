/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder;

import llnl.gnem.apps.detection.core.dataObjects.DetectorType;


/**
 *
 * @author dodge1
 */
public class DetectorCreationParams {

    private double correlationWindowLength;
    private double prepickSeconds;
    private double correlationThreshold = 0.7;
    private double detectionThreshold = 0.5;
    private double energyCapture = 0.8;
    private double blackoutSeconds = 5.0;
    private DetectorType detectorType = DetectorType.SUBSPACE;
    private double staDuration = 1.0;
    private double ltaDuration = 10.0;
    private double staLtaDelaySeconds = 1.0;

    public DetectorCreationParams() {
        correlationWindowLength = 30;
        prepickSeconds = 30;

        correlationThreshold = 0.7;

        detectionThreshold = 0.5;
        energyCapture = 0.8;
        blackoutSeconds = 5.0;
        detectorType = DetectorType.SUBSPACE;
        staDuration = 1.0;
        ltaDuration = 10.0;
        staLtaDelaySeconds = 1.0;
    }

    public void setCorrelationWindowLength(double correlationWindowLength) {
        this.correlationWindowLength = correlationWindowLength;
    }

    /**
     * @return the correlationWindowLength
     */
    public double getCorrelationWindowLength() {
        return correlationWindowLength;
    }

    public double getPrePickSeconds() {
        return prepickSeconds;
    }

    /**
     * @param prepickSeconds the prepickSeconds to set
     */
    public void setPrepickSeconds(double prepickSeconds) {
        this.prepickSeconds = prepickSeconds;
    }

    void adjustPrepickSeconds(double deltaT) {
        prepickSeconds -= deltaT;
    }

    /**
     * @return the correlationThreshold
     */
    public double getCorrelationThreshold() {
        return correlationThreshold;
    }

    /**
     * @param correlationThreshold the correlationThreshold to set
     */
    public void setCorrelationThreshold(double correlationThreshold) {
        this.correlationThreshold = correlationThreshold;
    }

    public double getDetectionThreshold() {
        return detectionThreshold;
    }

    public double getEnergyCapture() {
        return energyCapture;
    }

    public double getBlackoutSeconds() {
        return blackoutSeconds;
    }

    /**
     * @param detectionThreshold the detectionThreshold to set
     */
    public void setDetectionThreshold(double detectionThreshold) {
        this.detectionThreshold = detectionThreshold;
    }

    /**
     * @param energyCapture the energyCapture to set
     */
    public void setEnergyCapture(double energyCapture) {
        this.energyCapture = energyCapture;
    }

    /**
     * @param blackoutSeconds the blackoutSeconds to set
     */
    public void setBlackoutSeconds(double blackoutSeconds) {
        this.blackoutSeconds = blackoutSeconds;
    }

    /**
     * @return the detectorType
     */
    public DetectorType getDetectorType() {
        return detectorType;
    }

    /**
     * @param detectorType the detectorType to set
     */
    public void setDetectorType(DetectorType detectorType) {
        this.detectorType = detectorType;
    }

    /**
     * @return the staDuration
     */
    public double getStaDuration() {
        return staDuration;
    }

    /**
     * @param staDuration the staDuration to set
     */
    public void setStaDuration(double staDuration) {
        this.staDuration = staDuration;
    }

    /**
     * @return the ltaDuration
     */
    public double getLtaDuration() {
        return ltaDuration;
    }

    /**
     * @param ltaDuration the ltaDuration to set
     */
    public void setLtaDuration(double ltaDuration) {
        this.ltaDuration = ltaDuration;
    }

    /**
     * @return the staLtaDelaySeconds
     */
    public double getStaLtaDelaySeconds() {
        return staLtaDelaySeconds;
    }

    /**
     * @param staLtaDelaySeconds the staLtaDelaySeconds to set
     */
    public void setStaLtaDelaySeconds(double staLtaDelaySeconds) {
        this.staLtaDelaySeconds = staLtaDelaySeconds;
    }
}
