/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.dataObjects;

/**
 *
 * @author dodge
 */
public class ArrayCorrelationParams extends SubspaceParameters {

    private final float staDuration;
    private final float ltaDuration;
    private final float staLtaDelaySeconds;

    public ArrayCorrelationParams(double detectionThreshold,
            double energyCapture,
            double blackoutSeconds,
            float staDuration,
            float ltaDuration,
            float staLtaDelaySeconds) {
        super(detectionThreshold, energyCapture, blackoutSeconds);
        this.staDuration = staDuration;
        this.ltaDuration = ltaDuration;
        this.staLtaDelaySeconds = staLtaDelaySeconds;
    }

    /**
     * @return the staDuration
     */
    public float getStaDuration() {
        return staDuration;
    }

    /**
     * @return the ltaDuration
     */
    public float getLtaDuration() {
        return ltaDuration;
    }

    /**
     * @return the staLtaDelaySeconds
     */
    public float getStaLtaDelaySeconds() {
        return staLtaDelaySeconds;
    }
}
