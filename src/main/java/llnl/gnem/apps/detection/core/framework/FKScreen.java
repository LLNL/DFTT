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
