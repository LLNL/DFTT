/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util;

/**
 *
 * @author dodge1
 */
public class PowerDetThreshold {
    private Double commandLineOverride = null;
    private double dbThreshold;
    private PowerDetThreshold() {
    }
    
    public static PowerDetThreshold getInstance() {
        return PowerDetThresholdHolder.INSTANCE;
    }

    public void setCommandlineOverride(double value) {
        commandLineOverride = value;
    }
    
    public void setThresholdFromDb(double value) {
        dbThreshold = value;
    }
    
    public double getThreshold()
    {
        return commandLineOverride != null ? commandLineOverride : dbThreshold;
    }
    
    private static class PowerDetThresholdHolder {

        private static final PowerDetThreshold INSTANCE = new PowerDetThreshold();
    }
}
