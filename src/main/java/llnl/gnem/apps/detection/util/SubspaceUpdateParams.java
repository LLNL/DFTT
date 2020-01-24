/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util;

/**
 *
 * @author dodge1
 */
public class SubspaceUpdateParams {

    private boolean updateOnDetection;
    private double updateThreshold = 0.8;
    private double lambda = 0.9;
    private double energyCapture = 0.8;

    private SubspaceUpdateParams() {
    }

    public static SubspaceUpdateParams getInstance() {
        return SubspaceUpdateParamsHolder.INSTANCE;
    }

    public void setUpdateOnDetection(boolean value) {
        updateOnDetection = value;
    }

    public boolean isUpdateOnDetection() {
        return updateOnDetection;
    }

    public double getUpdateThreshold() {
        return updateThreshold;
    }

    /**
     * @param updateThreshold the updateThreshold to set
     */
    public void setUpdateThreshold(double updateThreshold) {
        this.updateThreshold = updateThreshold;
    }

    /**
     * @return the lambda
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * @param lambda the lambda to set
     */
    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    /**
     * @return the energyCapture
     */
    public double getEnergyCapture() {
        return energyCapture;
    }

    /**
     * @param energyCapture the energyCapture to set
     */
    public void setEnergyCapture(double energyCapture) {
        this.energyCapture = energyCapture;
    }

    private static class SubspaceUpdateParamsHolder {

        private static final SubspaceUpdateParams INSTANCE = new SubspaceUpdateParams();
    }
}
