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

import Jama.Matrix;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Measures peak position and peak quality for FK screen. Performs FK
 * calculation in both calibrated and uncalibrated modes.
 *
 * @author harris
 */
public class FKMeasurement {

    private float[] s;
    private float quality;
    private final int nch;
    private final int ns;
    private final float smax;

    public FKMeasurement(float smax,
            int ns,
            float[] xnorth,
            float[] xeast,
            ArrayList<float[]> waveforms,
            float delta,
            float f1,
            float f2) {

        BroadbandFK BBFK = new BroadbandFK(smax, ns, xnorth, xeast, waveforms, delta, f1, f2);
        nch = xnorth.length;
        this.ns = ns;
        this.smax = smax;
        measureFK(BBFK);

    }

    public FKMeasurement(float smax,
            int ns,
            float[] xnorth,
            float[] xeast,
            ArrayList<float[]> waveforms,
            float delta,
            float f1,
            float f2,
            float windowLength,
            ArrayList<float[]> referenceWaveforms) {

        BroadbandFK BBFK = new BroadbandFK(smax, ns, xnorth, xeast, waveforms, delta, f1, f2, windowLength, referenceWaveforms);
        nch = xnorth.length;
        this.ns = ns;
        this.smax = smax;

        measureFK(BBFK);
    }

    private void measureFK(BroadbandFK BBFK) {

        float[][] fks = BBFK.getFKSpectrum();

        // grid search for maximum 
        float fkmax = 0.0f;
        int ixmax = -1;
        int iymax = -1;
        for (int ix = 0; ix < ns; ix++) {
            for (int iy = 0; iy < ns; iy++) {
                if (fks[ix][iy] > fkmax) {
                    fkmax = fks[ix][iy];
                    ixmax = ix;
                    iymax = iy;
                }
            }
        }
        if (ixmax < 1 || ixmax >= ns - 1 || iymax < 1 || iymax >= ns - 1) { // something is wrong, so bail out.
            s = new float[2];
            s[0] = .1f;
            s[1] = s[0];
            quality = 0;
            return;
        }

        // quadratic refinement
        double[][] Ua = {{1, 2, 1, -1, -1, 1},
        {0, 0, 1, 0, -1, 1},
        {1, -2, 1, 1, -1, 1},
        {1, 0, 0, -1, 0, 1},
        {0, 0, 0, 0, 0, 1},
        {1, 0, 0, 1, 0, 1},
        {1, -2, 1, -1, 1, 1},
        {0, 0, 1, 0, 1, 1},
        {1, 2, 1, 1, 1, 1}};
        Matrix U = new Matrix(Ua);

        Matrix f = new Matrix(9, 1);

        f.set(0, 0, fks[ixmax - 1][iymax - 1]);
        f.set(1, 0, fks[ixmax][iymax - 1]);
        f.set(2, 0, fks[ixmax + 1][iymax - 1]);
        f.set(3, 0, fks[ixmax - 1][iymax]);
        f.set(4, 0, fks[ixmax][iymax]);
        f.set(5, 0, fks[ixmax + 1][iymax]);
        f.set(6, 0, fks[ixmax - 1][iymax + 1]);
        f.set(7, 0, fks[ixmax][iymax + 1]);
        f.set(8, 0, fks[ixmax + 1][iymax + 1]);

        Matrix UTU = U.transpose().times(U);
        Matrix est = UTU.inverse().times(U.transpose().times(f));
        Matrix A = new Matrix(2, 2);
        Matrix b = new Matrix(2, 1);
        A.set(0, 0, est.get(0, 0));
        A.set(0, 1, est.get(1, 0));
        A.set(1, 0, est.get(1, 0));
        A.set(1, 1, est.get(2, 0));
        b.set(0, 0, est.get(3, 0));
        b.set(1, 0, est.get(4, 0));
        double c = est.get(5, 0);

        Matrix is = A.inverse().times(b).times(-0.5);
        double fkmaxc = is.transpose().times(A.times(is)).plus(is.transpose().times(b)).get(0, 0) + c;

        float ds = 2 * smax / ((float) (ns - 1));
        s = new float[2];
        s[0] = smax - (ixmax + (float) is.get(0, 0)) * ds;
        s[1] = (iymax + (float) is.get(1, 0)) * ds - smax;

        quality = (float) fkmaxc / BBFK.getEnergy() / nch;
    }

    public float[] getSlownessEstimate() {
        return s;
    }

    public float getQuality() {
        return quality;
    }

    public double getVelocity() {
        float sx = s[0];
        float sy = s[1];
        return 1.0 / Math.sqrt(sx * sx + sy * sy);
    }
    
    public double getAzimuth()
    {
        float sx = s[0];
        float sy = s[1];
    
        double theta = Math.atan2(sy, sx);
        double tmp = Math.toDegrees(theta);
        if (tmp < 0) {
            tmp += 360;
        }
        return tmp;
    }
    
    @Override
    public String toString()
    {
        return String.format("velocity = %6.2f, azimuth = %6.2f, quality = %5.3f", getVelocity(),getAzimuth(), getQuality());
    }
}
