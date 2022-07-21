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

import java.util.Random;

import weka.core.matrix.EigenvalueDecomposition;
import weka.core.matrix.Matrix;

public class RayleighQuotientIteration {

    private static final double EPS = 1.0e-5;

    private double[] x;
    private double mu;
    private int n;

    public RayleighQuotientIteration(double[] diagonal, double[] offDiagonal, double[] x0, double mu0) {

        n = x0.length;
        double[] x = new double[n];
        System.arraycopy(x0, 0, x, 0, n);
        double mu_last = mu0;

        double[] at = new double[n];
        double[] bt = new double[n];
        double[] ct = new double[n];
        double[] dt = new double[n];

        System.arraycopy(x0, 0, dt, 0, n);
        int iterations = 0;
        do {

            System.arraycopy(diagonal, 0, bt, 0, n);
            for (int i = 0; i < n; i++) {
                bt[i] -= mu;
            }
            System.arraycopy(offDiagonal, 0, at, 1, n - 1);
            System.arraycopy(offDiagonal, 0, ct, 0, n - 1);

            TridiagonalSolver solver = new TridiagonalSolver(at, bt, ct, dt);

            x = solver.getX();
            double E = 0.0;
            for (int i = 0; i < n; i++) {
                E += x[i] * x[i];
            }
            E = Math.sqrt(E);
            for (int i = 0; i < n; i++) {
                x[i] /= E;
            }

            mu_last = mu;
            mu = quadraticForm(x, diagonal, offDiagonal);
            System.arraycopy(x, 0, dt, 0, n);
            ++iterations;
            if (iterations > 10) {
                throw new IllegalStateException("Failed to achieve termination criterion!");
            }
        } while (Math.abs(mu - mu_last) / (mu) > EPS);

    }

    public double quadraticForm(double[] x, double[] a, double[] b) {

        double retval = 0.0;
        for (int i = 0; i < n; i++) {
            retval += a[i] * x[i] * x[i];
        }
        double tmp = 0.0;
        for (int i = 0; i < n - 1; i++) {
            tmp += b[i] * x[i] * x[i + 1];
        }
        retval += 2.0 * tmp;

        return retval;
    }

    public double[] getX() {
        return x;
    }

    public double getMu() {
        return mu;
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

        Matrix ones = new Matrix(n, 1, 1.0);

        System.out.println("Gershgorin:");
        Matrix G = T.times(ones);
        G.print(8, 5);
        double gmax = 0.0;
        for (int i = 0; i < n; i++) {
            gmax = Math.max(gmax, G.get(i, 0));
        }

        EigenvalueDecomposition eig = new EigenvalueDecomposition(T);

        eig.getD().print(8, 5);

        double mu0 = gmax;

        RayleighQuotientIteration RQI = new RayleighQuotientIteration(lanczos.getDiagonal(), lanczos.getOffDiagonal(), x, mu0);

        System.out.println(RQI.getMu());

        Matrix I = Matrix.identity(n, n);

        System.out.println("mu0: " + mu0);

        Matrix ynext = (T.minus(I.times(mu0))).inverse().times(y);
        double E = Math.sqrt(ynext.transpose().times(ynext).get(0, 0));

        ynext = ynext.times(1.0 / E);

        double mu = ynext.transpose().times(T.times(ynext)).get(0, 0);
        y = ynext;

        System.out.println("mu: " + mu);

        for (int count = 0; count < 10; count++) {

            ynext = (T.minus(I.times(mu))).inverse().times(y);
            E = Math.sqrt(ynext.transpose().times(ynext).get(0, 0));

            ynext = ynext.times(1.0 / E);

            mu = ynext.transpose().times(T.times(ynext)).get(0, 0);
            y = ynext;

            System.out.println("mu: " + mu);

        }

    }

}
