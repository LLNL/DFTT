/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util;

import llnl.gnem.apps.detection.util.initialization.StreamsConfig;

/**
 *
 * @author dodge1
 */
public class SubspaceThreshold {

    private Double commandLineOverride = null;
    private double dbThreshold; // Value stored in the detector specification

    private SubspaceThreshold() {
    }

    public static SubspaceThreshold getInstance() {
        return SubspaceThresholdHolder.INSTANCE;
    }

    public void setCommandlineOverride(double value) {
        commandLineOverride = value;
    }

    public void setThresholdFromDb(double value) {
        dbThreshold = value;
    }

    public double getDesiredThreshold(String streamName) {
        if (commandLineOverride != null) {
            return commandLineOverride;
        } else {
            double threshold = dbThreshold;
            if (StreamsConfig.getInstance().isUseConfigFileThreshold(streamName)) {
                threshold = StreamsConfig.getInstance().getSubspaceThresholdValue(streamName);
            }
            return threshold;
        }
    }

    public double getNewDetectorThreshold(String streamName) {
        if (commandLineOverride != null) {
            return commandLineOverride;
        } else {
            return StreamsConfig.getInstance().getSubspaceThresholdValue(streamName);
        }
    }

    private static class SubspaceThresholdHolder {

        private static final SubspaceThreshold INSTANCE = new SubspaceThreshold();
    }
}
