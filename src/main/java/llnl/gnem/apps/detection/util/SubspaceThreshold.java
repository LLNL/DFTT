/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
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
