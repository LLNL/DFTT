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

import org.ojalgo.matrix.Primitive32Matrix;
import org.ojalgo.matrix.Primitive32Matrix.DenseReceiver;
import org.ojalgo.matrix.store.RawStore;

public class TridiagonalSolver {

    private double[] x;

    public TridiagonalSolver(double[] a, double[] b, double[] c, double[] d) {

        int n = b.length;

        x = new double[n];

        for (int i = 2; i <= n; i++) {
            double w = a[i - 1] / b[i - 2];
            b[i - 1] -= w * c[i - 2];
            d[i - 1] -= w * d[i - 2];
        }

        x[n - 1] = d[n - 1] / b[n - 1];

        for (int i = n - 1; i > 0; i--) {
            x[i - 1] = (d[i - 1] - c[i - 1] * x[i]) / b[i - 1];
        }

    }

    public double[] getX() {
        return x;
    }

    public static void main(String[] args) {

        Random R = new Random();

        DenseReceiver X = Primitive32Matrix.FACTORY.makeDense(10000, 20);
        for (int i = 0; i < 10000; i++) {
            for (int j = 0; j < 20; j++) {
                X.set(i, j, R.nextGaussian());
            }
        }

        Primitive32Matrix A = X.get().transpose().multiply(X.get());

        double[] b = new double[20];
        b[0] = 1.0;
        for (int i = 2; i < 20; i++) {
            b[i] = -1.0 * b[i - 1];
        }

        int n = 10;

        Lanczos lanczos = new Lanczos(A.toRawCopy2D(), b, n);

        Primitive32Matrix T = Primitive32Matrix.FACTORY.makeWrapper(RawStore.wrap(lanczos.getT().getArray()));
        double[] a = new double[n];
        b = new double[n];
        double[] c = new double[n];
        double[] d = new double[n];

        System.out.println(T.get(8, 5));

        for (int i = 0; i < n; i++) {
            b[i] = T.get(i, i);
        }
        for (int i = 1; i < n; i++) {
            a[i] = T.get(i, i - 1);
        }
        for (int i = 0; i < n - 1; i++) {
            c[i] = T.get(i, i + 1);
        }

        d[0] = 1.0;
        for (int i = 1; i < n; i++) {
            d[i] = 1.0 * d[i - 1];
        }

        DenseReceiver D = Primitive32Matrix.FACTORY.makeDense(n, 1);
        for (int i = 0; i < n; i++) {
            D.set(i, 0, d[i]);
        }

        TridiagonalSolver solver = new TridiagonalSolver(a, b, c, d);

        double[] x = solver.getX();

        X = Primitive32Matrix.FACTORY.makeDense(n, 1);
        for (int i = 0; i < n; i++) {
            X.set(i, 0, x[i]);
        }

        System.out.println("Solver x (x1000): ");
        System.out.println(X.get().multiply(1000).get(8, 5));

        System.out.println("\n Weka x (x1000): ");
        System.out.println(T.invert().multiply(D.get()).multiply(1000).get(8, 5));
    }

}
