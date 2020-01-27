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
package llnl.gnem.apps.detection.triggerProcessing;

import llnl.gnem.apps.detection.core.dataObjects.ArrayConfiguration;
import llnl.gnem.apps.detection.core.dataObjects.ArrayElement;
import llnl.gnem.apps.detection.core.signalProcessing.FKMeasurement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.seismogram.BasicSeismogram;

/**
 *
 * @author dodge1
 */
public class SlownessProcessor {

    public static WindowStats measureWindow(ArrayConfiguration arrayConfig, Collection<? extends BasicSeismogram> traces, TimeT windowStart, double windowDuration, double fkMaxSlowness, double minFKFreq, double maxFKFreq) {
        ArrayList<float[]> waveforms = new ArrayList<>();
        float[] dNorth = new float[traces.size()];
        float[] dEast = new float[traces.size()];
        int j = 0;
        float delta = 0;
        ArrayList<Double> snrList = new ArrayList<>();
        for (BasicSeismogram seis : traces) {
            seis.RemoveMean();
            StreamKey key = seis.getStreamKey();

            ArrayElement element = arrayConfig.getElement(key.getSta());
            if (element == null) {
                throw new IllegalStateException("Failed to retrieve ArrayElement for key: " + key);
            }

            float[] pre = buildSingleWaveform(seis, new TimeT(windowStart.getEpochTime() - windowDuration), windowDuration);
            float[] post = buildSingleWaveform(seis, windowStart, windowDuration);
            double aSignal = getAmplitude(post);
            double aNoise = getAmplitude(pre);
            snrList.add(aSignal / aNoise);
            dEast[j] = (float) element.getDeast();
            dNorth[j++] = (float) element.getDnorth();
            waveforms.add(post);
            delta = (float) seis.getDelta();
        }
        double snr = SeriesMath.getMedian(snrList);

        double energy = computeAverageEnergy(waveforms);
        float smax = (float) fkMaxSlowness;
        int NUM_SLOWNESSES = 100;
        float[] flimits = {(float) minFKFreq, (float) maxFKFreq};
        FKMeasurement measurement = new FKMeasurement(smax, NUM_SLOWNESSES, dNorth, dEast, waveforms, delta, flimits[0], flimits[1]);
        ApplicationLogger.getInstance().log(Level.FINEST, String.format("FK Quality = %f", measurement.getQuality()));
        return new WindowStats(measurement, snr, energy, windowStart, windowDuration);

    }

    public static double computeAverageEnergy(ArrayList<float[]> waveforms) {
        double energy = 0;
        if (!waveforms.isEmpty()) {
            for (float[] arr : waveforms) {
                energy += SeriesMath.getEnergy(arr);
            }
            energy /= waveforms.size();
        }
        return energy;
    }

    private static float[] buildSingleWaveform(BasicSeismogram seis, TimeT windowStart, double windowDuration) {
        BasicSeismogram tmp = new BasicSeismogram(seis);
        TimeT end = new TimeT(windowStart.getEpochTime() + windowDuration);
        tmp.cut(windowStart, end);
        return tmp.getData();
    }

    private static double getAmplitude(float[] data) {
        SeriesMath.removeMean(data);
        float[] v = SeriesMath.abs(data);
        return SeriesMath.getMedian(v);
    }

}
