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
package llnl.gnem.dftt.core.optimization;

import java.io.PrintStream;

public class Grid {

    private final int dimension;
    private final float[] center;
    private final float[] lower;
    private final float[] dx;
    private final int[] nx;
    private final int[] factor;

    public Grid() {
        dimension = 0;
        center = null;
        lower = null;
        dx = null;
        nx = null;
        factor = null;
    }

    public Grid(Grid G) {
        dimension = G.dimension;
        center = new float[dimension];
        dx = new float[dimension];
        nx = new int[dimension];
        lower = new float[dimension];
        factor = new int[dimension];
        for (int i = 0; i < dimension; i++) {
            center[i] = G.center[i];
            lower[i] = G.lower[i];
            dx[i] = G.dx[i];
            nx[i] = G.nx[i];
            factor[i] = G.factor[i];
        }
    }

    public Grid(int _dimension, float[] _center, float[] _dx, int[] _nx) {
        dimension = _dimension;
        center = new float[dimension];
        dx = new float[dimension];
        nx = new int[dimension];
        lower = new float[dimension];
        factor = new int[dimension];
        for (int i = 0; i < dimension; i++) {
            center[i] = _center[i];
            dx[i] = _dx[i];
            nx[i] = _nx[i];
            lower[i] = center[i] - ((nx[i] - 1) * dx[i]) / 2.0f;
        }
        int n = 1;
        for (int i = 0; i < dimension; i++) {
            n *= nx[i];
        }
        for (int i = 0; i < dimension; i++) {
            n /= nx[i];
            factor[i] = n;
        }
    }

    public void contract(float factor) {

        for (int i = 0; i < dimension; i++) {
            dx[i] /= factor;
            lower[i] = center[i] - ((nx[i] - 1) * dx[i]) / 2.0f;
        }

    }

    public void recenter(float[] _center) {

        for (int i = 0; i < dimension; i++) {
            center[i] = _center[i];
            lower[i] = center[i] - ((nx[i] - 1) * dx[i]) / 2.0f;
        }

    }

    public int size() {

        int retval = 1;
        for (int i = 0; i < dimension; i++) {
            retval *= nx[i];
        }
        return retval;
    }

    private int[] indices(int index) {

        int[] retval = new int[dimension];
        int quotient;
        int remainder = index;

        for (int i = 0; i < dimension; i++) {
            quotient = remainder / factor[i];
            remainder -= quotient * factor[i];
            retval[i] = quotient;
        }
        return retval;
    }

    public float[] get(int index) {
        float retval[] = null;
        if (index >= 0 && index < size()) {
            retval = new float[dimension];
            int[] iarray = indices(index);
            for (int i = 0; i < dimension; i++) {
                retval[i] = lower[i] + dx[i] * iarray[i];
            }
        }
        return retval;
    }

    public boolean interior(int index) {

        boolean retval = true;
        int[] iarray = indices(index);
        for (int i = 0; i < dimension; i++) {
            if (iarray[i] <= 0 || iarray[i] >= nx[i] - 1) {
                retval = false;
            }
        }
        return retval;
    }

    public void print(PrintStream ps) {
        float[] x;
        String S;
        for (int i = 0; i < size(); i++) {
            x = get(i);
            S = new String();
            for (int j = 0; j < dimension; j++) {
                S = S + "  " + x[j];
            }
        }
    }

}
