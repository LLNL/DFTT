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
package llnl.gnem.apps.detection.core.signalProcessing;

import java.util.Arrays;

public class Beamformer {

    private final int nchannels;
    private Delay[] delays;

    public Beamformer(double[][] channelCoordinates, double backAzimuth, double apparentVelocity, double dt, double elevationCorrectionVelocity) {
// channelCoordinates are dnorth (km), deast (km)
        nchannels = channelCoordinates.length;

        delays = new Delay[nchannels];

        double[] delaysInSeconds = new double[nchannels];
        double[] s = new double[2];
        s[0] = Math.cos(Math.toRadians(backAzimuth)) / apparentVelocity; 
        s[1] = Math.sin(Math.toRadians(backAzimuth)) / apparentVelocity;

        for (int ich = 0; ich < nchannels; ich++) {
            double[] x = channelCoordinates[ich];
            delaysInSeconds[ich] = x[0] * s[0] + x[1] * s[1] - x[3] / elevationCorrectionVelocity;
        }

        double minDelay = Double.MAX_VALUE;

        for (int ich = 0; ich < nchannels; ich++) {
            minDelay = Math.min(minDelay, delaysInSeconds[ich]);
        }

        for (int ich = 0; ich < nchannels; ich++) {
            delaysInSeconds[ich] -= minDelay;
        }

        for (int ich = 0; ich < nchannels; ich++) {
            float delayInSamples = (float) (delaysInSeconds[ich] / dt) + 3.0f;
            delays[ich] = new Delay(delayInSamples);
        }

    }
    
    public Beamformer(float[] dnorth, float[] deast, double backAzimuth, double apparentVelocity, double dt){
        this(repack(dnorth,deast),backAzimuth,apparentVelocity,dt);
    }
    
    private static double[][] repack(float[] dnorth, float[] deast){
        double[][] result = new double[dnorth.length][];
        for(int j = 0; j <dnorth.length; ++j){
            result[j] = new double[]{dnorth[j],deast[j],0.0};
        }
        return result;
    }

    public Beamformer(double[][] channelCoordinates, double backAzimuth, double apparentVelocity, double dt) {

        nchannels = channelCoordinates.length;

        delays = new Delay[nchannels];

        double[] delaysInSeconds = new double[nchannels];
        double[] s = new double[2];
        s[0] = Math.cos(backAzimuth * Math.PI / 180.0) / apparentVelocity;
        s[1] = Math.sin(backAzimuth * Math.PI / 180.0) / apparentVelocity;

        for (int ich = 0; ich < nchannels; ich++) {
            double[] x = channelCoordinates[ich];
            delaysInSeconds[ich] = x[0] * s[0] + x[1] * s[1];
        }

        double minDelay = Double.MAX_VALUE;

        for (int ich = 0; ich < nchannels; ich++) {
            minDelay = Math.min(minDelay, delaysInSeconds[ich]);
        }

        for (int ich = 0; ich < nchannels; ich++) {
            delaysInSeconds[ich] -= minDelay;
        }

        for (int ich = 0; ich < nchannels; ich++) {
            float delayInSamples = (float) (delaysInSeconds[ich] / dt) + 3.0f;
            delays[ich] = new Delay(delayInSamples);
        }

    }

    public float[] beam(float[][] waveforms) {

        int n = waveforms[0].length;
        float[] retval = new float[n];
        float[] buffer = new float[n];

        Arrays.fill(retval, 0.0f);

        for (int ich = 0; ich < nchannels; ich++) {
            delays[ich].delay(waveforms[ich], buffer);
            for (int i = 0; i < n; i++) {
                retval[i] += buffer[i];
            }
        }

        for (int i = 0; i < n; i++) {
            retval[i] /= nchannels;
        }

        return retval;
    }

}
