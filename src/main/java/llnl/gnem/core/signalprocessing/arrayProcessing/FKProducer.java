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
package llnl.gnem.core.signalprocessing.arrayProcessing;

import java.util.ArrayList;

import org.ojalgo.RecoverableCondition;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.Primitive64Store;
import org.ojalgo.matrix.store.RawStore;
import org.ojalgo.matrix.task.InverterTask;

public class FKProducer {

    public FKResult produce(float smax, int ns, float[] xnorth, float[] xeast, ArrayList<float[]> waveforms, float delta, float f1, float f2) {

        BroadbandFK BBFK = new BroadbandFK(smax, ns, xnorth, xeast, waveforms, delta, f1, f2, true);
        int nch = xnorth.length;
        return measureFK(BBFK, nch, ns, smax);
    }

    private FKResult measureFK(BroadbandFK BBFK, int nch, int ns, float smax) {
        try {
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
            if (ixmax < 0 || ixmax > ns - 1 || iymax < 0 || iymax > ns - 1) { // something is wrong, so bail out.
                // Create default result...
                return new FKResult();
            }

            // quadratic refinement
            double[][] Ua = { { 1, 2, 1, -1, -1, 1 }, { 0, 0, 1, 0, -1, 1 }, { 1, -2, 1, 1, -1, 1 }, { 1, 0, 0, -1, 0, 1 }, { 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 1, 0, 1 }, { 1, -2, 1, -1, 1, 1 },
                    { 0, 0, 1, 0, 1, 1 }, { 1, 2, 1, 1, 1, 1 } };
            RawStore U = RawStore.wrap(Ua);

            Primitive64Store f = Primitive64Store.FACTORY.make(9, 1);

            f.set(0, 0, fks[ixmax - 1][iymax - 1]);
            f.set(1, 0, fks[ixmax][iymax - 1]);
            f.set(2, 0, fks[ixmax + 1][iymax - 1]);
            f.set(3, 0, fks[ixmax - 1][iymax]);
            f.set(4, 0, fks[ixmax][iymax]);
            f.set(5, 0, fks[ixmax + 1][iymax]);
            f.set(6, 0, fks[ixmax - 1][iymax + 1]);
            f.set(7, 0, fks[ixmax][iymax + 1]);
            f.set(8, 0, fks[ixmax + 1][iymax + 1]);

            MatrixStore<Double> UTU = U.transpose().multiply(U);

            MatrixStore<Double> est = InverterTask.PRIMITIVE.make(UTU).invert(UTU).multiply(U.transpose().multiply(f));
            Primitive64Store A = Primitive64Store.FACTORY.make(2, 2);
            Primitive64Store b = Primitive64Store.FACTORY.make(2, 1);
            A.set(0, 0, est.get(0, 0));
            A.set(0, 1, est.get(1, 0));
            A.set(1, 0, est.get(1, 0));
            A.set(1, 1, est.get(2, 0));
            b.set(0, 0, est.get(3, 0));
            b.set(1, 0, est.get(4, 0));
            double c = est.get(5, 0);

            MatrixStore<Double> is = InverterTask.PRIMITIVE.make(A).invert(A).multiply(b).multiply(-0.5);
            double fkmaxc = is.transpose().multiply(A.multiply(is)).add(is.transpose().multiply(b)).get(0, 0) + c;

            float ds = 2 * smax / (ns - 1);
            float snorth = smax - (ixmax + is.get(0, 0).floatValue()) * ds;
            float seast = (iymax + is.get(1, 0).floatValue()) * ds - smax;
            SlownessValue result = new SlownessValue(snorth, seast);
            double quality = fkmaxc / BBFK.getEnergy() / nch;

            float[] sx = new float[ns];
            float[] sy = new float[ns];
            double range = 2 * smax;
            double dSlow = range / (ns - 1);
            for (int k = 0; k < ns; ++k) {
                sx[k] = (float) (-smax + k * dSlow);
                sy[k] = sx[k];
            }
            //or plotting need to flip the y-axis
            float[][] fksPlot = new float[ns][ns];
            for (int k = 0; k < ns; ++k) {
                int m = ns - k - 1;
                for (int j = 0; j < ns; ++j) {
                    fksPlot[m][j] = fks[k][j];
                }
            }
            return new FKResult(sx, sy, fksPlot, result, fkmaxc, quality);
        } catch (RecoverableCondition e) {
            throw new RuntimeException(e);
        }
    }

}
