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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.signalProcessing;

import llnl.gnem.core.signalprocessing.Sequence;
import llnl.gnem.core.signalprocessing.filter.IIRFilter;

/**
 *
 * @author dodge1
 */
public class SNRfromSTALTA {

    public static float calculateSNR(float[] beam, IIRFilter F, int S, int L, int power, int[] window) {  // version for unfiltered beams

        float[] tmp = new float[beam.length];

        F.filter(tmp);
        Sequence.reverse(tmp);
        F.initialize();
        F.filter(tmp);
        Sequence.reverse(tmp);

        tmp = stalta(tmp, S, L, power);

        float retval = 0.0f;
        for (int k = window[0]; k <= window[1]; k++) {
            retval = Math.max(retval, tmp[k]);
        }

        if (power == 2) {
            retval = (float) Math.sqrt(retval);
        }

        return retval;
    }

    public static float calculateSNR(float[] beam, int S, int L, int power, int[] window) {    // version for prefiltered beams

        float[] tmp = new float[beam.length];

        tmp = stalta(beam, S, L, power);

        float retval = 0.0f;
        for (int k = window[0]; k <= window[1]; k++) {
            retval = Math.max(retval, tmp[k]);
        }

        if (power == 2) {
            retval = (float) Math.sqrt(retval);
        }

        return retval;
    }

    public static float calculateSNR(float[] beam, int S, int L, int[] window) {    // version for prefiltered beams and rms measurement hardwired

        float[] tmp = new float[beam.length];

        tmp = stalta(beam, S, L, 2);

        float retval = 0.0f;
        for (int k = window[0]; k <= window[1]; k++) {
            retval = Math.max(retval, tmp[k]);
        }

        retval = (float) Math.sqrt(retval);

        return retval;
    }

    public static float[] stalta(float[] beam, int S, int L, int power) {

        int N = beam.length;

        float[] tmp = new float[N];

        if (power == 1) {
            for (int i = 0; i < N; i++) {
                tmp[i] = Math.abs(beam[i]);
            }
        } else if (power == 2) {
            for (int i = 0; i < N; i++) {
                tmp[i] = beam[i] * beam[i];
            }
        }

        float[] sta = new float[N];
        float[] lta = new float[N];

        int S2 = S / 2;

        // sta calculation
        for (int k = 0; k < N - S; k++) {
            float sum = 0.0f;
            for (int s = 0; s < S; s++) {
                sum += tmp[k + s];
            }
            sta[k + S2] = sum / S;
        }

        //    end conditions
        float C = sta[S2];
        for (int k = 0; k < S2; k++) {
            sta[k] = C;
        }

        for (int k = N - S2; k < N; k++) {
            sta[k] = sta[N - S2 - 1];
        }

        // lta calculation
        float a1 = 1.0f - 1.0f / ((float) L);
        float a2 = 1.0f / ((float) L);

        for (int k = 0; k < S2; k++) {
            lta[k] = C;
        }

        for (int k = S2; k < N; k++) {
            lta[k] = a1 * lta[k - 1] + a2 * sta[k - S2];
        }

        float[] retval = new float[N];
        for (int k = 0; k < L; k++) {
            retval[k] = sta[k] / C;
        }
        for (int k = L; k < N; k++) {
            retval[k] = sta[k] / lta[k - L];
        }

        return retval;
    }

}
