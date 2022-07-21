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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WindowDetails {

    private final static int NTHREADS = 70;

    private final ExecutorService service;
    private final ArrayList< EnergyCapture> tasks;

    private double[][] dataMatrix;
    private final int nch;
    private final int npts;
    private final int ndets;
    private final int slidingWindowLength;
    private final int decrate;
    private float[] energyCaptureTrace;
    private float threshold;

    public WindowDetails(ArrayList< float[][]> eventWaveforms, int nch, int npts, int slidingWindowLength, int decrate) {

        this.nch = nch;
        this.npts = npts;
        ndets = eventWaveforms.size();
        this.slidingWindowLength = slidingWindowLength;
        this.decrate = decrate;

        assembleDataMatrix(eventWaveforms);

        service = Executors.newFixedThreadPool(NTHREADS);
        tasks = new ArrayList<>();
        for (int i = 0; i < NTHREADS; i++) {
            tasks.add(new EnergyCapture(nch * slidingWindowLength, ndets, dataMatrix));
        }

        threshold = 0.0f;

    }

    private void assembleDataMatrix(ArrayList< float[][]> eventWaveforms) {

        dataMatrix = new double[ndets][npts * nch];

        for (int id = 0; id < ndets; id++) {

            float[][] tmp = eventWaveforms.get(id);

            for (int ich = 0; ich < nch; ich++) {
                for (int i = 0; i < npts; i++) {
                    dataMatrix[id][ich + i * nch] = tmp[ich][i];
                }
            }

        }

    }

    public float[] computeEnergyCaptureTrace() {

        int ptr = 0;
        boolean failure = false;
        energyCaptureTrace = new float[npts - slidingWindowLength + 1];

        do {

            for (int ii = 0; ii < NTHREADS; ii++) {

                int index = ptr + ii * decrate;

                if (index > npts - slidingWindowLength) {
                    break;
                }

                tasks.get(ii).setValues(index, index * nch);

            }

            try {

                List< Future< EnergyCapturePair>> futures = service.invokeAll(tasks);

                for (Future< EnergyCapturePair> future : futures) {
                    EnergyCapturePair pair = future.get();
                    if (pair == null) {
                        failure = true;
                    } else {
                        energyCaptureTrace[pair.index] = (float) pair.energyCapture;
                    }
                    ptr += decrate;
                }
            } catch (Exception err) {
                err.printStackTrace();
                failure = true;
            }

        } while (ptr < npts - slidingWindowLength);

        service.shutdown();

        return failure ? null : energyCaptureTrace;
    }

    public int[] thresholdTrace(float factor, int ncells, int minLength, int minDimensionForRefinement) {

        int[] retval = new int[2];
        Arrays.fill(retval, -1);

        // generate histogram of trace
        int[] hist = new int[ncells];

        float dx = 1.0f / ncells;

        for (int i = 0; i < energyCaptureTrace.length; i += decrate) {
            int icell = (int) (energyCaptureTrace[i] / dx);
            if (icell == ncells) {
                icell = ncells - 1;
            }
            hist[icell]++;
        }

        // find peak - defines noise floor
        int cmax = 0;
        int imax = -1;
        for (int i = 0; i < ncells; i++) {
            if (hist[i] > cmax) {
                cmax = hist[i];
                imax = i;
            }
        }

        threshold = factor * (imax * dx + dx / 2.0f);

        // scan trace for threshold crossings
        boolean det = false;

        int i = 0;

        do {
            if (energyCaptureTrace[i] > threshold && !det) {
                det = true;
                retval[0] = i;
                i += minLength * decrate;
            } else if (energyCaptureTrace[i] < threshold && det) {
                retval[1] = i;
                break;
            } else if (i >= energyCaptureTrace.length - 1 && det) {
                retval[1] = i;
                break;
            }

            i += decrate;
        } while (i < energyCaptureTrace.length - 1);

        if (retval[1] == -1) {
            retval[1] = energyCaptureTrace.length - 1;
        }

        retval[0] += slidingWindowLength;
        retval[1] += slidingWindowLength;
        retval[1] = Math.min(retval[1], npts - 1);

        return retval;
    }

    public float getThreshold() {
        return threshold;
    }

}
