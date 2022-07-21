/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
package llnl.gnem.apps.detection.core.windowRevision;

import java.util.ArrayList;

public class WindowSelector {

    private static final int NBINS = 200;   // number of bins for histograms of energy capture traces
    //private static final float FLOOR_FACTOR = 2.0f;  // determines window energy capture threshold by multiplying energy capture of histogram background noise peak 

    private int[] window;
    private final float[] energyCapture1;
    private float[] energyCapture2;
    private float energyCaptureThreshold;

    // Arguments:
    //
    // X                       ArrayList< float[][] >   ArrayList containing traces for detections, one multichannel waveform per entry
    //                                                    X.size() returns the number of detections
    //                                                    each entry is a float[nch][npts]
    // nch                     int                      number of channels in each multichannel waveform
    // npts                    int                      number of samples in each individual channel waveform
    // analysisWindowLength    int                      analysis window length in samples (suggest 10 seconds of samples, i.e. 200 for data sampled at 0.05 seconds)
    // decrate                 int                      decimation rate (recommend either 1 or 2)
    // minimumWindowLength     int                      minimum window length - assures that the returned window is at least this many samples long (suggest 25 seconds = 500 samples @ 20 sps)
    // refine                  boolean                  a flag that indicates whether the window is refined in a second pass by removing detections with SNR below a threshold
    // SNRThreshold            float                    SNR threshold to use to exclude low-SNR detections in second pass;  only used if refine = true  (suggest 1.5)
    public WindowSelector(ArrayList< float[][]> X, int nch, int npts, int analysisWindowLength, int decrate, int minimumWindowLength, boolean refine, float SNRThreshold, float floorFactor, int minDimensionForRefinement) {

        WindowDetails W = new WindowDetails(X, nch, npts, analysisWindowLength, decrate);

        energyCapture1 = W.computeEnergyCaptureTrace();
        if (energyCapture1 == null) {
            throw new IllegalStateException("Energy capture trace calculation failed!");
        }

        window = W.thresholdTrace(floorFactor, NBINS, minimumWindowLength, 5);

        energyCaptureThreshold = W.getThreshold();

        if (refine) {

            float[] snr = new float[X.size()];

            int n = window[1] - window[0] + 1;
            int N = window[0] + 1;

            for (int ix = 0; ix < X.size(); ix++) {

                float[][] x = X.get(ix);

                float e = 0.0f;
                float E = 0.0f;
                for (int ich = 0; ich < nch; ich++) {
                    float[] xp = x[ich];
                    for (int j = window[0]; j <= window[1]; j++) {
                        e += xp[j] * xp[j];
                    }
                    for (int j = 0; j < window[0]; j++) {
                        E += xp[j] * xp[j];
                    }
                }

                snr[ix] = (e / n) / (E / N);
            }

            ArrayList< float[][]> Y = new ArrayList<>();

            for (int ix = 0; ix < X.size(); ix++) {
                if (snr[ix] > SNRThreshold) {
                    Y.add(X.get(ix));
                }
            }

            if (Y.size() >= minDimensionForRefinement) {
                WindowDetails W1 = new WindowDetails(Y, nch, npts, analysisWindowLength, decrate);

                energyCapture2 = W1.computeEnergyCaptureTrace();
                if (energyCapture2 == null) {
                    throw new IllegalStateException("Refinement energy capture trace calculation failed!");
                }
                window = W1.thresholdTrace(floorFactor, NBINS, minimumWindowLength, 5);

                energyCaptureThreshold = W1.getThreshold();

            }

        }

    }

    public int[] getWindow() {
        return window;
    }

    public float[] getEnergyCapture1() {
        return energyCapture1;
    }

    public float[] getEnergyCapture2() {
        return energyCapture2;
    }

    public float getEnergyCaptureThreshold() {
        return energyCaptureThreshold;
    }

}
