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
package llnl.gnem.core.signalprocessing;

import llnl.gnem.core.util.SeriesMath;

import java.io.PrintStream;

public class DoubleSequence {

// instance variables
    private double[] seqvalues;

// static methods
    public DoubleSequence(int n) {
        seqvalues = new double[n];
    }

    public DoubleSequence() {
        seqvalues = null;
    }

    public DoubleSequence(int n, double value) {
        seqvalues = new double[n];
        for (int i = 0; i < n; i++) {
            seqvalues[i] = value;
        }
    }

    public DoubleSequence(double[] v) {
        seqvalues = v;
    }

    public DoubleSequence(Double[] v) {
        seqvalues = new double[v.length];
        for (int ii = 0; ii < v.length; ii++) {
            seqvalues[ii] = v[ii];
        }
    }

    public DoubleSequence(DoubleSequence S) {
        seqvalues = new double[S.seqvalues.length];
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = S.seqvalues[i];
        }
    }

    public DoubleSequence(Sequence S) {
        float[] v = S.getArray();
        seqvalues = new double[v.length];
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = (double) v[i];
        }
    }

    public Sequence getSequence() {
        float[] retval = new float[seqvalues.length];
        for (int i = 0; i < seqvalues.length; i++) {
            retval[i] = (float) seqvalues[i];
        }
        return new Sequence(retval);
    }

    public double get(int n) {
        return seqvalues[n];
    }

    /**
     * Get a subsection of the current DoubleSequence object
     *
     * @param start - the integer starting index
     * @param end - the integer ending index
     * @return - a DoubleSequence containing the desired points
     */
    public DoubleSequence getSubSection(int start, int end) {
        try {
            double[] subsection = new double[end - start + 1];
            for (int ii = start; ii <= end; ii++) {
                subsection[ii - start] = seqvalues[ii];
            }
            DoubleSequence result = new DoubleSequence(subsection);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public double[] getArray() {
        return seqvalues;
    }

    public int length() {
        return seqvalues.length;
    }

    public double extremum() {
        double smax = 0.0;
        double sabs;
        for (int i = 0; i < seqvalues.length; i++) {
            sabs = Math.abs(seqvalues[i]);
            if (sabs > smax) {
                smax = sabs;
            }
        }
        return smax;
    }

    public int extremumIndex() {
        double smax = 0.0;
        double sabs;
        int index = 0;
        for (int i = 0; i < seqvalues.length; i++) {
            sabs = Math.abs(seqvalues[i]);
            if (sabs > smax) {
                smax = sabs;
                index = i;
            }
        }
        return index;
    }

    public double min() {
        return SeriesMath.getMin(seqvalues);
    }

    public double max() {
        return SeriesMath.getMax(seqvalues);
    }

    public double[] getAbs() {
        return SeriesMath.abs(seqvalues);
    }

    public void rmean() {
        double smean = 0.0;
        for (int i = 0; i < seqvalues.length; i++) {
            smean += seqvalues[i];
        }
        smean /= seqvalues.length;
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] -= smean;
        }
    }

    public void zero() {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = 0.0;
        }
    }

    public void scaleBy(double a) {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] *= a;
        }
    }

    public void reverse() {
        double tmp;
        int j = seqvalues.length - 1;
        int i = 0;
        while (true) {
            if (j <= i) {
                break;
            }
            tmp = seqvalues[i];
            seqvalues[i] = seqvalues[j];
            seqvalues[j] = tmp;
            i++;
            j--;
        }
    }

    public void zshift(int shift) {

        int n;
        int srcptr, dstptr;
        if (Math.abs(shift) > seqvalues.length) {
            zero(seqvalues, 0, seqvalues.length);   // zeroes DoubleSequence
        } else if (shift < 0) {                                    // left shift

            n = seqvalues.length + shift;
            dstptr = 0;
            srcptr = -shift;
            for (int i = 0; i < n; i++) {
                seqvalues[dstptr++] = seqvalues[srcptr++];
            }

            zero(seqvalues, seqvalues.length + shift, -shift);     // zero high end

        } else if (shift > 0) {                                    // right shift

            n = seqvalues.length - shift;
            dstptr = seqvalues.length - 1;
            srcptr = dstptr - shift;
            for (int i = 0; i < n; i++) {
                seqvalues[dstptr--] = seqvalues[srcptr--];
            }

            zero(seqvalues, 0, shift);                             // zero low end

        }

    }

    public void cshift(int shift) {

        //  Arguments:
        //  ----------
        //  int shift           number of samples to shift.
        //                      a negative number indicates a shift left.
        //                      a positive number indicates a shift right.
        //                      zero indicates no shift.
        int bsize = Math.abs(shift);
        double[] buffer = new double[bsize];
        int n = seqvalues.length;

// two cases - right and left shifts
        int i, j;
        if (shift > 0) {                      // right shift

            shift = shift % n;                    // prevent extraneous transfers

            j = n - shift;
            for (i = 0; i < shift; i++) {
                buffer[i] = seqvalues[j++];
            }
            j = n - 1;
            i = j - shift;
            while (i >= 0) {
                seqvalues[j--] = seqvalues[i--];
            }
            for (i = 0; i < shift; i++) {
                seqvalues[i] = buffer[i];
            }

        } else if (shift < 0) {                 // left shift

            shift = shift % n;                    // prevent extraneous transfers

            for (i = 0; i < -shift; i++) {
                buffer[i] = seqvalues[i];
            }
            j = 0;
            i = -shift;
            while (i < n) {
                seqvalues[j++] = seqvalues[i++];
            }
            j = n + shift;
            for (i = 0; i < -shift; i++) {
                seqvalues[j++] = buffer[i];
            }

        }

    }

    // Multiplies a subDoubleSequence of a DoubleSequence by a window beginning at the index start
    //   and returns the windowed DoubleSequence.
    // Assumes the DoubleSequence is equal to zero outside of its legal range.
    //
    //       ****************************************************************
    //   +++++++++++++++++++++++++++++++
    //                                                      +++++++++++++++++++++
    public DoubleSequence window(int start, DoubleSequence window) {

        int n = window.length();
        double[] newseqvalues = new double[n];

        // check for overlap - if none, return with zero DoubleSequence
        if (start < seqvalues.length && start + n > 0) {

            int index0 = Math.max(0, -start);
            int index1 = Math.min(seqvalues.length - start, n);

            double[] windowvalues = window.seqvalues;
            for (int i = index0; i < index1; i++) {
                newseqvalues[i] = seqvalues[i + start] * windowvalues[i];
            }

        }

        return new DoubleSequence(newseqvalues);
    }

    public DoubleSequence alias(int N) {
        double[] newseqvalues = new double[N];
        int index = 0;
        for (int i = 0; i < seqvalues.length; i++) {
            newseqvalues[index++] += seqvalues[i];
            if (index == N) {
                index = 0;
            }
        }
        return new DoubleSequence(newseqvalues);
    }

    public void stretch(int factor) {
        int n = seqvalues.length;
        double[] sptr = new double[factor * n];
        zero(sptr, 0, factor * n);
        for (int i = n - 1; i >= 0; i--) {
            sptr[factor * i] = seqvalues[i];
        }
        seqvalues = sptr;
    }

    public void decimate(int factor) {
        int dlen = seqvalues.length / factor;
        if (dlen * factor < seqvalues.length) {
            dlen++;
        }
        double[] dptr = new double[dlen];
        for (int i = 0; i < dlen; i++) {
            dptr[i] = seqvalues[i * factor];
        }
        seqvalues = dptr;
    }

    public boolean cut(int i1, int i2) {
        if (i2 < i1) {
            return false;
        }
        if (i1 < 0) {
            return false;
        }
        if (i2 > seqvalues.length - 1) {
            return false;
        }
        int n = i2 - i1 + 1;
        double[] newseqvalues = new double[n];
        for (int i = 0; i < n; i++) {
            newseqvalues[i] = seqvalues[i + i1];
        }
        seqvalues = newseqvalues;
        return true;
    }

    public void minusEquals(DoubleSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        for (int i = 0; i < n; i++) {
            seqvalues[i] -= S.seqvalues[i];
        }
    }

    public void plusEquals(DoubleSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        for (int i = 0; i < n; i++) {
            seqvalues[i] += S.seqvalues[i];
        }
    }

    public void divideBy(DoubleSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        for (int i = 0; i < n; i++) {
            seqvalues[i] /= S.seqvalues[i];
        }
    }

    // These operations don't overwrite the original sequence
    public DoubleSequence times(double value) {
        return new DoubleSequence(SeriesMath.Multiply(seqvalues, value));
    }

    public DoubleSequence plus(DoubleSequence otherseq) {
        return new DoubleSequence(SeriesMath.Add(seqvalues, otherseq.getArray()));
    }

    public DoubleSequence minus(DoubleSequence otherseq) {
        return new DoubleSequence(SeriesMath.Subtract(seqvalues, otherseq.getArray()));
    }

    /**
     * Treat the series as a vector and calculate the norm (aka the direction or
     * the normal) of the vector
     *
     * @return the sqrt of the sum of the squares of all the elements of the
     * DoubleSequence
     */
    public double Norm() {
        return SeriesMath.getNorm(seqvalues);
    }

    /**
     * @return the sum of all the elements of the sequence
     */
    public double Sum() {
        return SeriesMath.getSum(seqvalues);
    }

    /**
     * Treat the Double Sequence as a vector and calculate the unit vector (aka
     * the direction vector or the normalized vector) based on the norm
     *
     * unit_vector[ii] = data[ii]/norm;
     *
     * Note if the data are a zero vector [0 0 0 ...0 ] the norm will be 0. - in
     * that case, return the original (zero) vector instead of a null unit
     * vector
     *
     * @param data is an Array containing the series values
     */
    public DoubleSequence getUnitVector() {
        return new DoubleSequence(SeriesMath.getUnitVector(seqvalues));
    }

    /**
     * Treat the DoubleSequences as vector and calculate the cross product of
     * this DoubleSequence (A) with another (B)
     *
     * @param B another vector as a DoubleSequence object
     * @return the cross product (AxB)
     */
    public DoubleSequence cross(DoubleSequence B) {
        if (length() != B.length()) {
            return null;
        }

        if (length() != 3) {
            // this method currently requires both DoubleSequences to be 3 elements long  (aka a vector in 3-D space)
            return null;
        }

        double[] result = new double[length()];
        result[0] = get(1) * B.get(2) - get(2) * B.get(1);    // i = a1*b2 - a2*b1
        result[1] = get(2) * B.get(0) - get(0) * B.get(2);    // j = a2*b0 - a0*b2
        result[2] = get(0) * B.get(1) - get(1) * B.get(0);    // k = a0*b1 - a1*b0

        return new DoubleSequence(result);
    }

    /**
     * The dot product of this DoubleSequence with another one
     *
     * @param S
     * @return the double valued result of the cross product between the two
     * sequences
     */
    public double dotprod(DoubleSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        double retval = 0.0;
        double[] x = seqvalues;
        double[] y = S.seqvalues;
        for (int i = 0; i < n; i++) {
            retval += x[i] * y[i];
        }
        return retval;
    }

    /**
     * Element-by-element multiplication, C = A.*B
     *
     * @param data1
     * @param data2
     * @return result[ii] = data1[ii] * data2[ii]
     */
    public DoubleSequence elementMultiplication(DoubleSequence B) {
        double[] data1 = seqvalues;
        double[] data2 = B.seqvalues;
        double[] result = SeriesMath.elementMultiplication(data1, data2);
        return new DoubleSequence(result);
    }

    /**
     * divide the individual elements of one data series by those of another
     *
     * @param data1 - the first data series
     * @param data2 - the denominator data series must have the same length as
     * the first
     * @return a new double[] series in which result[ii] = data1[ii]/data2[ii]
     *
     * Akin to array left division in Matlab A.\B
     */
    public DoubleSequence elementDivision(DoubleSequence B) {
        double[] data1 = seqvalues;
        double[] data2 = B.seqvalues;
        double[] result = SeriesMath.elementDivision(data1, data2);
        return new DoubleSequence(result);
    }

    public DoubleSequence sqr() {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] *= seqvalues[i];
        }
        return this;
    }

    public DoubleSequence sqrt() {
        for (int i = 0; i < seqvalues.length; i++) {
            if (seqvalues[i] < 0.0) {
                seqvalues[i] = 0.0;
            } else {
                seqvalues[i] = Math.sqrt(seqvalues[i]);
            }
        }
        return this;
    }

    public void pad_to(int newlength) {
        int n = seqvalues.length;
        if (newlength > n) {
            double[] tmp = new double[newlength];
            for (int i = 0; i < n; i++) {
                tmp[i] = seqvalues[i];
            }
            seqvalues = tmp;
            for (int i = n; i < newlength; i++) {
                seqvalues[i] = 0.0;
            }
        }
    }

    public void dft() {

        //  pad to next larger power of two, if necessary
        int m = 0;
        int n = 1;
        while (n < seqvalues.length) {
            m++;
            n *= 2;
        }
        pad_to(n);

        // fft
        FFTdp.rvfft(seqvalues, m);

    }

    public void dftRX(FFTdp fft) {
        pad_to(fft.fftsize());
        fft.rvfftRX(seqvalues);
    }

    public void dftConjugate() {
        int n = seqvalues.length;
        for (int i = n / 2 + 1; i < n; i++) {
            seqvalues[i] = -seqvalues[i];
        }
    }

    public void idft() {

        //  pad to next larger power of two, if necessary
        int m = 0;
        int n = 1;
        while (n < seqvalues.length) {
            m++;
            n *= 2;
        }
        pad_to(n);

        // inverse fft
        FFTdp.irvfft(seqvalues, m);

    }

    public void idftRX(FFTdp fft) {
        pad_to(fft.fftsize());
        fft.irvfftRX(seqvalues);
    }

    static public DoubleSequence dftprod(DoubleSequence x, DoubleSequence y, double c) {
        int n = x.length();
        int half = n / 2;
        DoubleSequence tmp;

        if (n != y.length() || half * 2 != n) {
            tmp = new DoubleSequence();
        } else {
            tmp = new DoubleSequence(n);
            double[] xp = x.getArray();
            double[] yp = y.getArray();
            double[] tp = tmp.getArray();
            int k;
            tp[0] = xp[0] * yp[0];
            tp[half] = xp[half] * yp[half];
            for (int i = 1; i < half; i++) {
                k = n - i;
                tp[i] = xp[i] * yp[i] - c * xp[k] * yp[k];
                tp[k] = xp[k] * yp[i] + c * xp[i] * yp[k];
            }
        }
        return tmp;
    }

    public void dftAlias(int factor) {

        //
        //  Routine to alias FFT of an N-length DoubleSequence
        //  to allow the computation of x[n*factor] from
        //  its transform X(k).  Useful in computing downsampled
        //  DoubleSequences from their transforms.
        //
        //               1  N-1       j2(pi)kn/M
        // x[n*factor] = -  sum X(k) e
        //               N  k=0
        //
        //               1  M-1   M  f-1          j2(pi)rn
        // x[n*factor] = -  sum ( -  sum X(r+Mp) e         )
        //               M  r=0   N  p=0
        //
        // M = N / factor, exactly an integer
        //
        // Assumes transform is held in Sorensen & Bonzanigo's format
        //
        // Xr(0), Xr(1), ..., Xr(N/2), Xi(N/2-1), ..., Xi(1)
        //
        int N = seqvalues.length;
        int M = N / factor;
        int k, r, p;

        double[] ptr = new double[M];

        double Yrr, Yri;

        for (r = 0; r <= M / 2; r++) {
            Yrr = 0.0;
            Yri = 0.0;
            for (p = 0; p < factor; p++) {
                k = r + M * p;
                if (k <= N / 2) {
                    Yrr += seqvalues[k];
                    if (k > 0 && k < N / 2) {
                        Yri += seqvalues[N - k];
                    }
                } else {
                    Yrr += seqvalues[N - k];
                    Yri -= seqvalues[k];
                }
            }
            ptr[r] = Yrr / factor;
            if (r != 0 && r != M / 2) {
                ptr[M - r] = Yri / factor;
            }
        }

        seqvalues = ptr;
    }

    public void print(PrintStream ps) {
        for (int i = 0; i < seqvalues.length; i++) {
            ps.println(seqvalues[i]);
        }
    }

    public void set(int i, double c) {
        if (i >= 0 & i < seqvalues.length) {
            seqvalues[i] = c;
        }
    }

    public void setConstant(double c) {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = c;
        }
    }

    protected void zero(double[] s, int start, int duration) {
        int j = start;
        for (int i = 0; i < duration; i++) {
            s[j++] = 0.0;
        }
    }

}
