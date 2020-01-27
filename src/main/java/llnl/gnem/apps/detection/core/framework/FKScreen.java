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
package llnl.gnem.apps.detection.core.framework;

import java.util.ArrayList;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.signalProcessing.FKMeasurement;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author harris
 */
public class FKScreen {

    private static final int NUM_SLOWNESSES = 100;

    // screen for use with uncalibrated FK test
    public static FKScreenResults computeFKScreenResults(float smax,
            float[] xnorth,
            float[] xeast,
            ArrayList<float[]> waveforms,
            float delta,
            float[] flimits,
            float[] targetSlowness,
            float slownessTolerance,
            float FKQualityThreshold) {

        FKMeasurement measurement = new FKMeasurement(smax, NUM_SLOWNESSES, xnorth, xeast, waveforms, delta, flimits[0], flimits[1]);
        ApplicationLogger.getInstance().log(Level.FINEST, String.format("FK Quality = %f", measurement.getQuality()));
        float[] sest = measurement.getSlownessEstimate();
        float eps = (sest[0] - targetSlowness[0]) * (sest[0] - targetSlowness[0])
                + (sest[1] - targetSlowness[1]) * (sest[1] - targetSlowness[1]);
        ApplicationLogger.getInstance().log(Level.FINEST, String.format("FK Error = %f", eps));
        boolean passed = measurement.getQuality() >= FKQualityThreshold && Math.sqrt(eps) <= slownessTolerance;
        return new FKScreenResults(passed, sest[0], sest[1], measurement.getQuality());
    }

    // screen for use with calibrated FK test
    public static boolean passesScreen(float smax,
            float[] xnorth,
            float[] xeast,
            ArrayList<float[]> waveforms,
            float delta,
            float[] flimits,
            float correlationWindowLength,
            ArrayList<float[]> referenceWaveforms,
            float slownessTolerance,
            float FKQualityThreshold) {

        boolean retval = true;
        FKMeasurement measurement = new FKMeasurement(smax, NUM_SLOWNESSES,
                xnorth,
                xeast,
                waveforms,
                delta,
                flimits[0],
                flimits[1],
                correlationWindowLength,
                referenceWaveforms);
        if (measurement.getQuality() < FKQualityThreshold) {
            retval = false;
        }
        float[] sest = measurement.getSlownessEstimate();
        float eps = sest[0] * sest[0] + sest[1] * sest[1];
        if (Math.sqrt(eps) > slownessTolerance) {
            retval = false;
        }

        return retval;
    }
}
