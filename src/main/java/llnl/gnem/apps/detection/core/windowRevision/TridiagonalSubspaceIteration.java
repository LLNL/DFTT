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

import java.util.Arrays;
import java.util.Random;

import weka.core.matrix.EigenvalueDecomposition;
import weka.core.matrix.Matrix;

// Subspace iteration for symmetric tridiagonal matrices T

public class TridiagonalSubspaceIteration {

    private double[] a; // diagonal
    private double[] b; // off-diagonal
    private int n;

    private double[][] X;
    private double[][] Z;
    private double[][] B;

    private double lambda;

    public TridiagonalSubspaceIteration(double[] a, double[] b) {

        n = a.length;
        this.a = a;
        this.b = b;

        X = new double[2][n];
        Z = new double[2][n];
        B = new double[2][2];

        // initial X

        Arrays.fill(X[0], 1.0);
        X[1][0] = 1.0;
        for (int i = 1; i < n; i++) {
            X[1][i] = -X[1][i - 1];
        }

        for (int iter = 0; iter < n / 2 + 1; iter++) {
            iteration();
        }

    }

    private void iteration() {

        double[] tmp0 = new double[n];
        double[] tmp1 = new double[n];

        // Z = T*T*T*X;

        Tx(X[0], tmp0);
        Tx(tmp0, tmp1);
        Tx(tmp1, Z[0]);
        Tx(X[1], tmp0);
        Tx(tmp0, tmp1);
        Tx(tmp1, Z[1]);

        // orthogonalize and normalize Z with Gram-Schmidt

        normalize(Z[0]);
        double z1Tz0 = innerProduct(Z[0], Z[1]);
        for (int i = 0; i < n; i++) {
            Z[1][i] -= z1Tz0 * Z[0][i];
        }
        normalize(Z[1]);

        // form B = Z'*T*Z

        Tx(Z[0], tmp0);
        Tx(Z[1], tmp1);
        B[0][0] = innerProduct(Z[0], tmp0);
        B[1][1] = innerProduct(Z[1], tmp1);
        B[0][1] = B[1][0] = innerProduct(Z[0], tmp1);

        // Givens rotation of B

        double[] cs = Givens();

        lambda = cs[0] * B[0][0] - cs[1] * B[1][0];

        // update X

        double c = cs[0];
        double s = cs[1];
        for (int i = 0; i < n; i++) {
            X[0][i] = Z[0][i] * c - Z[1][i] * s;
            X[1][i] = Z[0][i] * s + Z[1][i] * c;
        }

    }

    public double getEigenvalue() {
        return lambda;
    }

    public double[] getEigenvector() {
        return X[0];
    }

    // specialized matrix vector multiplication

    //
    //  a[0]  b[0]   0     0                             x[0]
    //  b[0]  a[1]  b[1]   0                             x[1]
    //   0    b[1]  a[2]  b[2]                           x[2]
    //   0     0    b[2]  a[3]                           x[3]
    //
    //
    //                            a[n-2]  b[n-2]        x[n-2]
    //                            b[n-2]  a[n-1]        x[n-1]

    private void Tx(double[] x, double[] y) {

        // end cases

        y[0] = a[0] * x[0] + b[0] * x[1];
        y[n - 1] = b[n - 2] * x[n - 2] + a[n - 1] * x[n - 1];

        // all others

        for (int i = 1; i < n - 1; i++) {
            y[i] = b[i - 1] * x[i - 1] + a[i] * x[i] + b[i] * x[i + 1];
        }

    }

    private double innerProduct(double[] x, double[] y) {

        double retval = 0.0;
        for (int i = 0; i < n; i++) {
            retval += x[i] * y[i];
        }

        return retval;
    }

    private void normalize(double[] x) {
        double tmp = 0.0;
        for (int i = 0; i < n; i++) {
            tmp += x[i] * x[i];
        }
        tmp = Math.sqrt(tmp);
        for (int i = 0; i < n; i++) {
            x[i] /= tmp;
        }
    }

    // stable Givens rotation from Golub and Van Loan, section 5.1.8

    private double[] Givens() {

        double c = 0.0;
        double s = 0.0;

        double a = Math.abs(B[0][0]);
        double b = Math.abs(B[1][0]);

        if (b < 1.0e-15) {
            c = 1.0;
            s = 0.0;
        } else if (b > a) {
            double t = -B[0][0] / B[1][0];
            s = 1.0 / Math.sqrt(1.0 + t * t);
            c = s * t;
        } else {
            double t = -B[1][0] / B[0][0];
            c = 1.0 / Math.sqrt(1.0 + t * t);
            s = c * t;
        }

        double[] retval = { c, s };

        return retval;
    }

    public static void main(String[] args) {

        Random R = new Random();

        Matrix X = new Matrix(10000, 20);
        for (int i = 0; i < 10000; i++) {
            for (int j = 0; j < 20; j++) {
                X.set(i, j, R.nextGaussian());
            }
        }

        Matrix A = X.transpose().times(X);

        double[] b = new double[20];
        b[0] = 1.0;
        for (int i = 2; i < 20; i++) {
            b[i] = 1.0 * b[i - 1];
        }

        int n = 10;

        Lanczos lanczos = new Lanczos(A.getArray(), b, n);

        double[] x = new double[n];
        Matrix y = new Matrix(n, 1);
        for (int i = 0; i < n; i++) {
            x[i] = R.nextGaussian();
            y.set(i, 0, x[i]);
        }

        Matrix T = lanczos.getT();

        System.out.println("T:");
        T.print(8, 5);

        EigenvalueDecomposition ED = new EigenvalueDecomposition(T);
        double[] e = ED.getRealEigenvalues();
        System.out.println("\nEigenvalues:");
        for (int i = 0; i < n; i++) {
            System.out.println("  " + e[i]);
        }

        TridiagonalSubspaceIteration TSI = new TridiagonalSubspaceIteration(lanczos.getDiagonal(), lanczos.getOffDiagonal());

        RayleighQuotientIteration RQI = new RayleighQuotientIteration(lanczos.getDiagonal(), lanczos.getOffDiagonal(), TSI.getEigenvector(), TSI.getEigenvalue());

        RQI.getMu();

    }

}
