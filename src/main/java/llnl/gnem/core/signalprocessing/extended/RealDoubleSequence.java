//                                                      DoubleSequence.java
//
//  Copyright (c)  2001  Regents of the University of California
//  All rights reserved
//
//* @author   Dave Harris
//*  Created:       12/30/03 by modification from single-precision Sequence class
//*  Last Modified:  12/30/03
package llnl.gnem.core.signalprocessing.extended;

import java.io.PrintStream;

/**
 * A DoubleSequence is a DoubleSequence of complex numbers that usually
 * represents a signal. It uses a double arrray to represent these signals.
 * <p>
 * </p>
 * A DoubleSequence has two different formats, depending on whether it
 * represents a real signal, or the dft of a real signal. A DoubleSequence
 * representing the dft of a real DoubleSequence is held in Sorensen AND
 * Bonzanigo's format:
 * <p>
 * </p>
 * Xr(0), Xr(1), ..., Xr(N/2), Xi(N/2-1), ..., Xi(1)
 * <p>
 * </p>
 * A DoubleSequence is placed into dft format by a call to dft() or dftRX().
 * Once in dft format, a DoubleSequence may call any method that begins with
 * dft. Furthermore, a DoubleSequence may not call any method that begins with
 * dft unless it is in dft format. A DoubleSequence exits dft format by a call
 * to idft() or idftRX().
 * <p>
 * </p>
 * Most of the interface for this class was written by Dave Harris on 11/13/98.
 * <p>
 * </p>
 *
 * @author Timothy Paik written in June 2004
 */
public class RealDoubleSequence {

    ////////////////////////
    //   PRIVATE FIELDS   //
    ////////////////////////
    private double[] seqvalues;

    //////////////////////
    //   CONSTRUCTORS   //
    //////////////////////
    /**
     * Constructs a DoubleSequence of length n.
     *
     * @param n - the length of the array in this DoubleSequence
     */
    public RealDoubleSequence(int n) {
        seqvalues = new double[n];
    }

    /**
     * Constructs a DoubleSequence of length n containing n values of x.
     *
     * @param n - the length of the arrays in this DoubleSequence
     * @param x - the value filled in the real-value array.
     */
    public RealDoubleSequence(int n, double x) {
        seqvalues = new double[n];
        for (int i = 0; i < n; i++) {
            seqvalues[i] = x;
        }
    }

    /**
     * Constructs a DoubleSequence about a double array.
     *
     * @param v - the double array representing the real values of this
     * DoubleSequence
     */
    public RealDoubleSequence(double[] v) {
        seqvalues = v;
    }

    /**
     * Constructs a DoubleSequence using the values of a DoubleSequence.
     *
     * @param S - the DoubleSequence containing the initial values of this
     * DoubleSequence
     */
    public RealDoubleSequence(RealDoubleSequence S) {
        seqvalues = new double[S.seqvalues.length];
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = S.seqvalues[i];
        }
    }

    /**
     * Constructs a DoubleSequence using the values of a Sequence.
     *
     * @param S - the Sequence containing the initial real values of this
     * DoubleSequence
     */
    public RealDoubleSequence(RealSequence S) {
        float[] v = S.getArray();
        seqvalues = new double[v.length];
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = (double) v[i];
        }
    }

    /////////////////////////
    //   OTHER FUNCTIONS   //
    /////////////////////////
    /**
     * Copies the values from a DoubleSequence into this DoubleSequence.
     *
     * @param s - the DoubleSequence to get the values from.
     * @throws SignalProcessingException - if the length of the DoubleSequence
     * is different from the length of this DoubleSequence
     */
    public void getValues(RealSequence s) throws SignalProcessingException {
        if (seqvalues.length != s.length()) {
            throw new SignalProcessingException("Attempted to copy values from Sequence into a DoubleSequence of different length");
        }

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = s.get(i);
        }
    }

    /**
     * Copies the values from a DoubleSequence into this DoubleSequence.
     *
     * @param ds - the DoubleSequence to get the values from.
     * @throws SignalProcessingException - if the length of the DoubleSequence
     * is different from the length of this DoubleSequence
     */
    public void getValues(RealDoubleSequence ds) throws Exception {
        if (seqvalues.length != ds.length()) {
            throw new SignalProcessingException("Attempted to copy values from DoubleSequence into another DoubleSequence of different length");
        }

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = ds.get(i);
        }
    }

    public void getValues(double[] s) throws SignalProcessingException {
        if (seqvalues.length != s.length) {
            throw new SignalProcessingException("Attempted to copy values from double array into a DoubleSequence of different length");
        }

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = s[i];
        }
    }

    public RealDoubleSequence getSequence() {
        double[] retval = new double[seqvalues.length];
        for (int i = 0; i < seqvalues.length; i++) {
            retval[i] = seqvalues[i];
        }
        return new RealDoubleSequence(retval);
    }

    /**
     * Accesses the nth value of this DoubleSequence.
     *
     * @param n - the index of the value returned.
     * @return a double representing the nth value of the DoubleSequence.
     */
    public double get(int n) {
        return seqvalues[n];
    }

    /**
     * Gets the array representing the real values of this DoubleSequence.
     *
     * @return the array representing the reals of this DoubleSequence.
     */
    public double[] getArray() {
        return seqvalues;
    }

    /**
     * Gets the length of this DoubleSequence.
     *
     * @return the length of this DoubleSequence.
     */
    public int length() {
        return seqvalues.length;
    }

    /**
     * Returns the value of the largest element in this DoubleSequence.
     *
     * @return the largest element in this DoubleSequence
     */
    public double extremum() {
        double smax = 0.0;
        double sabs = 0.0;
        for (int i = 0; i < seqvalues.length; i++) {
            sabs = Math.abs(seqvalues[i]);
            if (sabs > smax) {
                smax = sabs;
            }
        }
        return smax;
    }

    /**
     * Returns the index of the largest element in this DoubleSequence.
     *
     * @return the index of the largest element in this DoubleSequence.
     */
    public int extremumIndex() {
        double smax = 0.0;
        double sabs = 0.0;
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

    /**
     * Subtracts the mean value of this DoubleSequence from every element in
     * this DoubleSequence.
     */
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

    /**
     * Zeroes out this DoubleSequence.
     */
    public void zero() {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = 0.0;
        }
    }

    /**
     * Modifies this DoubleSequence by scaling this DoubleSequence by a double
     * value.
     *
     * @param a - the value to scale this DoubleSequence by.
     */
    public void scaleBy(double a) {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] *= a;
        }
    }

    /**
     * Modifies this DoubleSequence by reversing this DoubleSequence.
     */
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

    /**
     * Modifies this DoubleSequence by shifting this DoubleSequence over a
     * certain amount of samples and replacing the empty values with zeroes.
     *
     * @param shift - the amount of samples to shift this DoubleSequence by. A
     * positive shift indicates a shift right.
     */
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

    /**
     * Modifies this DoubleSequence by shifting it over a certain amount of
     * samples. Any values shifted "off" the DoubleSequence reappear on the
     * other end of this DoubleSequence.
     *
     * @param shift - the amount of samples to shift this DoubleSequence by. A
     * positive shift indicates a shift right.
     */
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

    // Multiplies a subsequence of a DoubleSequence by a window beginning at the index start
    // and returns the windowed subDoubleSequence.
    // Assumes the DoubleSequence is equal to zero outside of its legal range.
    //
    //       ****************************************************************
    //   +++++++++++++++++++++++++++++++
    //                                                      +++++++++++++++++++++
    /**
     * Multiplies a subsequence of a DoubleSequence by a window beginning at the
     * index start and returns the windowed subsequence. Assumes the
     * DoubleSequence is equal to zero outside of its legal range.
     *
     * @param start- the index at which window should start multiplying.
     * @param window- the DoubleSequence with which to multiply this
     * DoubleSequence by to get the new DoubleSequence.
     * @return - a new DoubleSequence with the multiplied values in the window.
     */
    public RealDoubleSequence window(int start, RealDoubleSequence window) {

        int n = window.length();
        double[] newseqvalues = new double[n];

        // check for overlap - if none, return with zero subDoubleSequence
        if (start < seqvalues.length && start + n > 0) {

            int index0 = Math.max(0, -start);
            int index1 = Math.min(seqvalues.length - start, n);

            double[] windowvalues = window.seqvalues;
            for (int i = index0; i < index1; i++) {
                newseqvalues[i] = seqvalues[i + start] * windowvalues[i];
            }

        }

        return new RealDoubleSequence(newseqvalues);
    }

    /**
     * Aliases this DoubleSequence into another DoubleSequence of length N. Does
     * not modify this DoubleSequence.
     *
     * @param N - the length of the new aliased DoubleSequence
     * @return a new DoubleSequence of length N representing the aliased
     * DoubleSequence
     */
    public RealDoubleSequence alias(int N) {
        double[] newseqvalues = new double[N];
        int index = 0;
        for (int i = 0; i < seqvalues.length; i++) {
            newseqvalues[index++] += seqvalues[i];
            if (index == N) {
                index = 0;
            }
        }
        return new RealDoubleSequence(newseqvalues);
    }

    /**
     * Modifies this DoubleSequence by stretching it out by a factor n. Fills
     * the extra spaces in the DoubleSequence with zeroes.
     *
     * @param factor - the factor by which to stretch this DoubleSequence out by
     */
    public void stretch(int factor) {
        int n = seqvalues.length;
        double[] sptr = new double[factor * n];
        zero(sptr, 0, factor * n);
        for (int i = n - 1; i >= 0; i--) {
            sptr[factor * i] = seqvalues[i];
        }
        seqvalues = sptr;
    }

    /**
     * Decimates this DoubleSequence by choosing every nth sample, where n =
     * factor. Modifies this DoubleSequence. Disposes of the original arrays
     * that hold the original values for this DoubleSequence.
     *
     * @param factor - the factor by which to decimate this DoubleSequence by.
     */
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

    /**
     * Modifies this DoubleSequence by cropping out all values outside of the
     * interval specified by the input values. Returns true if the cut is
     * successful, false otherwise.
     *
     * @param i1 - the left index of the cutting region.
     * @param i2 - the right index of the cutting region.
     * @return true if the cut is successful (if the region is inside the bounds
     * of the original region)
     */
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

    /**
     * Modifies this DoubleSequence by subtracting another DoubleSequence from
     * it. Lines up the ComplexSequences at 0 and subtracts every index up to
     * the length of the shorter DoubleSequence.
     *
     * @param S - the DoubleSequence to subtract from this DoubleSequence.
     */
    public void minusEquals(RealDoubleSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        for (int i = 0; i < n; i++) {
            seqvalues[i] -= S.seqvalues[i];
        }
    }

    /**
     * Modifies this DoubleSequence by adding another DoubleSequence to it.
     * Lines up the DoubleSequences at 0 and adds every index up to the length
     * of the shorter DoubleSequence.
     *
     * @param S - the DoubleSequence to add to this DoubleSequence.
     */
    public void plusEquals(RealDoubleSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        for (int i = 0; i < n; i++) {
            seqvalues[i] += S.seqvalues[i];
        }
    }

    /**
     * Modifies this DoubleSequence by dividing it by another DoubleSequence.
     * Lines up the DoubleSequences at 0 and divides every index up to the
     * length of the shorter DoubleSequence.
     *
     * @param S - the DoubleSequence to divide this DoubleSequence by.
     */
    public void divideBy(RealDoubleSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        for (int i = 0; i < n; i++) {
            seqvalues[i] /= S.seqvalues[i];
        }
    }

    /**
     * Finds the dot product between this DoubleSequence and another
     * DoubleSequence. If the DoubleSequences have different lengths, then the
     * dot product takes the dot product of the overlapping region of the two
     * DoubleSequences, lining up the DoubleSequences at index 0.
     *
     * @param S - the other DoubleSequence in the dot product.
     * @return the value of the dot product in a double array form: the first
     * value is the real part, the second is the imaginary
     */
    public double dotprod(RealDoubleSequence S) {
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
     * Finds the DoubleSequence containing the square of each value in this
     * DoubleSequence.
     *
     * @return a new DoubleSequence containing the square of each value in this
     * DoubleSequence
     */
    public RealDoubleSequence sqr() {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] *= seqvalues[i];
        }
        return this;
    }

    /**
     * Finds the DoubleSequence containing the square root of each value in this
     * DoubleSequence.
     *
     * @return a new DoubleSequence containing the square root of each value in
     * this DoubleSequence
     */
    public RealDoubleSequence sqrt() {
        for (int i = 0; i < seqvalues.length; i++) {
            if (seqvalues[i] < 0.0) {
                seqvalues[i] = 0.0;
            } else {
                seqvalues[i] = Math.sqrt(seqvalues[i]);
            }
        }
        return this;
    }

    /**
     * Modifies this DoubleSequence by padding it to a larger length with zero
     * values. If the new length is shorter than this DoubleSequence's length,
     * there is no change to this DoubleSequence.
     *
     * @param newlength - the new length for this DoubleSequence.
     */
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

    /**
     * Modifies this DoubleSequence by taking the discrete Fourier transform of
     * this DoubleSequence. Puts this DoubleSequence into Sorensen AND
     * Bonzanigo's format as mentioned in the comments about the DoubleSequence
     * class.
     */
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

    /**
     * Modifies this DoubleSequence by taking the discrete Fourier transform of
     * this DoubleSequence. Uses the arbitrary radix stage of a pre-made FFT
     * object. Puts this DoubleSequence into Sorensen AND Bonzanigo's format as
     * mentioned in the comments about the DoubleSequence class.
     */
    public void dftRX(FFTdp fft) {
        pad_to(fft.fftsize());
        fft.rvfftRX(seqvalues);
    }

    /**
     * Modifies this DoubleSequence by replacing each element in this
     * DoubleSequence by its conjugate. This method only has meaning when this
     * DoubleSequence represents the discrete Fourier transform of another
     * DoubleSequence. If used on a normal DoubleSequence, the abstraction is
     * broken.
     */
    public void dftConjugate() {
        int n = seqvalues.length;
        for (int i = n / 2 + 1; i < n; i++) {
            seqvalues[i] = -seqvalues[i];
        }
    }

    /**
     * Modifies this DoubleSequence by taking the inverse discrete Fourier
     * transform of this DoubleSequence. Does not have much meaning if the
     * DoubleSequence does not have a length that is a power of 2, or if the
     * DoubleSequence is not in Sorenson AND Bonzanigo's format.
     */
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

    /**
     * Modifies this DoubleSequence by taking the inverse discrete Fourier
     * transform of this DoubleSequence. Uses the arbitrary radix stage of the
     * inverse Fourier transform.
     */
    public void idftRX(FFTdp fft) {
        pad_to(fft.fftsize());
        fft.irvfftRX(seqvalues);
    }

    /**
     * Returns a DoubleSequence representing the product between two
     * DoubleSequences. Only has meaning if both DoubleSequences are placed in
     * Sorenson AND Bonzanigo's format.
     *
     * @param x - the first DoubleSequence
     * @param y - the second DoubleSequence
     * @param c - either 1 or -1
     * @return the product between two DoubleSequences representing dft's of
     * other DoubleSequences
     */
    static public RealDoubleSequence dftprod(RealDoubleSequence x, RealDoubleSequence y, double c) {
        int n = x.length();
        int half = n / 2;
        RealDoubleSequence tmp;

        if (n != y.length() || half * 2 != n) {
            tmp = null;
        } else {
            tmp = new RealDoubleSequence(n);
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

    /**
     * Modifies this DoubleSequence by aliasing it by a factor.
     *
     * @param factor - the factor by which to alias this DoubleSequence by
     */
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

    /**
     * Prints this DoubleSequence out to a PrintStream.
     *
     * @param ps - the PrintStream accepting the print data.
     */
    public void print(PrintStream ps) {
        for (int i = 0; i < seqvalues.length; i++) {
            ps.println(seqvalues[i]);
        }
    }

    /**
     * Modifies this DoubleSequence by setting a value at a given index to a new
     * value.
     *
     * @param i - the index of the changed value
     * @param c - the real part of the new value
     */
    public void set(int i, double c) {
        if (i >= 0 & i < seqvalues.length) {
            seqvalues[i] = c;
        }
    }

    /**
     * Modifies this DoubleSequence by setting every value in this
     * DoubleSequence to a constant real value.
     *
     * @param c - the new value for every element in this DoubleSequence
     */
    public void setConstant(double c) {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = c;
        }
    }

    /**
     * Zeroes out a part of an array.
     *
     * @param s - the array to be zeroed out
     * @param start - the starting index at which to start the zeroes
     * @param duration - the amount of zeroes to place into the array
     */
    protected void zero(double[] s, int start, int duration) {
        int j = start;
        for (int i = 0; i < duration; i++) {
            s[j++] = 0.0;
        }
    }
}
