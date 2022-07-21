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

import weka.core.matrix.EigenvalueDecomposition;
import weka.core.matrix.Matrix;

public class Lanczos {

    private Matrix V;
    private Matrix T;
    private int n;
    private int dim;

    public Lanczos(double[][] A, double[] b, int dim) {

        n = A.length;
        this.dim = dim;

        V = new Matrix(n, dim);
        double[][] Va = V.getArray();

        T = new Matrix(dim, dim);
        double[][] Ta = T.getArray();

        double[] vj = new double[n];
        double[] vjp1 = new double[n];
        double[] u = new double[n];
        double[] w = new double[n];

        double beta = Math.sqrt(innerProduct(b, b));
        for (int i = 0; i < n; i++) {
            vj[i] = b[i] / beta;
            Va[i][0] = vj[i];
        }
        for (int i = 0; i < n; i++) {
            u[i] = innerProduct(A[i], vj);
        }

        for (int j = 0; j < dim; j++) {

            double a = innerProduct(vj, u);

            Ta[j][j] = a;

            if (j == dim - 1) {
                break;
            }

            for (int i = 0; i < n; i++) {
                w[i] = u[i] - a * vj[i];
            }

            beta = Math.sqrt(innerProduct(w, w));

            for (int i = 0; i < n; i++) {
                vjp1[i] = w[i] / beta;
                Va[i][j + 1] = vjp1[i];
            }

            for (int i = 0; i < n; i++) {
                u[i] = innerProduct(A[i], vjp1) - beta * vj[i];
            }

            Ta[j + 1][j] = beta;
            Ta[j][j + 1] = beta;

            System.arraycopy(vjp1, 0, vj, 0, n);

        }

    }

    private double innerProduct(double[] x, double[] y) {

        double p = 0.0;
        for (int i = 0; i < x.length; i++) {
            p += x[i] * y[i];
        }

        return p;
    }

    public Matrix getV() {
        return V;
    }

    public Matrix getT() {
        return T;
    }

    public double[] getDiagonal() {

        double[] retval = new double[dim];
        for (int i = 0; i < dim; i++) {
            retval[i] = T.get(i, i);
        }

        return retval;
    }

    public double[] getOffDiagonal() {

        double[] retval = new double[dim - 1];
        for (int i = 0; i < dim - 1; i++) {
            retval[i] = T.get(i, i + 1);
        }

        return retval;
    }

    public static void main(String[] args) {

        double[][] A = new double[25][25];
        double[] b = new double[25];

        for (int i = 0; i < 25; i++) {
            A[i][i] = 6.0;
        }
        A[0][0] = 5.0;
        A[24][24] = 5.0;
        for (int i = 0; i < 24; i++) {
            A[i][i + 1] = A[i + 1][i] = -4.0;
        }
        for (int i = 0; i < 23; i++) {
            A[i][i + 2] = A[i + 2][i] = 1.0;
        }

        b[0] = 1.0;
        for (int i = 1; i < 25; i++) {
            b[i] = -1.0 * b[i - 1];
        }

        Lanczos L = new Lanczos(A, b, 10);

        EigenvalueDecomposition E = new EigenvalueDecomposition(L.getT());

        E.getD().print(8, 5);

        L.getV().print(8, 5);
        L.getT().print(8, 5);

        double[] d = L.getDiagonal();
        for (double element : d) {
            System.out.println(element);
        }

        d = L.getOffDiagonal();
        for (double element : d) {
            System.out.println(element);
        }

    }

}
