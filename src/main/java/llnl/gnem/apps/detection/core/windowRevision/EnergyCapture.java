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

import java.util.concurrent.Callable;

import weka.core.matrix.EigenvalueDecomposition;
import weka.core.matrix.Matrix;

public class EnergyCapture implements Callable<EnergyCapturePair> {

    private double[][] X;
    private int sequenceIndex;
    private int rowOffset;
    private int nrows;
    private int ncols;

    private double[][] XTX;

    public EnergyCapture(int nrows, int ncols, double[][] X) {

        sequenceIndex = -1;
        this.X = X;
        XTX = new double[ncols][ncols];
        this.nrows = nrows;
        this.ncols = ncols;

    }

    public void setValues(int sequenceIndex, int rowOffset) {
        this.sequenceIndex = sequenceIndex;
        this.rowOffset = rowOffset;
    }

    @Override
    public EnergyCapturePair call() throws Exception {

        // form X'*X
        for (int i = 0; i < ncols; i++) {
            double[] Xi = X[i];

            for (int j = i; j < ncols; j++) {
                double[] Xj = X[j];

                double tmp = 0.0;
                for (int k = 0; k < nrows; k++) {
                    tmp += Xi[k + rowOffset] * Xj[k + rowOffset];
                }
                XTX[i][j] = tmp;
                XTX[j][i] = tmp;
            }
        }

        // normalize
        for (int i = 0; i < ncols; i++) {
            double s = Math.sqrt(XTX[i][i]);
            for (int k = 0; k < ncols; k++) {
                XTX[i][k] /= s;
                XTX[k][i] /= s;
            }
        }

        // Compute principal eigenvalue
        double EC = 0.0;

        if (ncols < 11) {
            EigenvalueDecomposition ED = new EigenvalueDecomposition(new Matrix(XTX));
            double[] e = ED.getRealEigenvalues();
            EC = e[e.length - 1] / (ncols);

        } else {

            int dim = 2 * (int) (Math.ceil(Math.sqrt(ncols)));
            double[] b = new double[ncols];

            b[0] = 1.0;
            for (int i = 1; i < ncols; i++) {
                b[i] = -1.0 * b[i - 1];
            }

            Lanczos lanczos = new Lanczos(XTX, b, dim);

            TridiagonalSubspaceIteration TSI = new TridiagonalSubspaceIteration(lanczos.getDiagonal(), lanczos.getOffDiagonal());

            try {
                RayleighQuotientIteration RQI = new RayleighQuotientIteration(lanczos.getDiagonal(), lanczos.getOffDiagonal(), TSI.getEigenvector(), TSI.getEigenvalue());
                EC = RQI.getMu() / (ncols);
            } catch (Exception ex) {
                return null;
            }

        }

        return new EnergyCapturePair(sequenceIndex, EC);

    }

}
