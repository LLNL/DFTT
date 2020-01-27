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
package llnl.gnem.core.signalprocessing.extended;

import java.io.PrintStream;

/**
 * A Sequence is a sequence of complex numbers that usually represents a signal.
 * It uses a float arrray to represent these signals.
 * <p>
 * </p>
 * A Sequence has two different formats, depending on whether it represents a
 * real signal, or the dft of a real signal. A Sequence representing the dft of
 * a real Sequence is held in Sorensen AND Bonzanigo's format:
 * <p>
 * </p>
 * Xr(0), Xr(1), ..., Xr(N/2), Xi(N/2-1), ..., Xi(1)
 * <p>
 * </p>
 * A Sequence is placed into dft format by a call to dft() or dftRX(). Once in
 * dft format, a Sequence may call any method that begins with dft. Furthermore,
 * a Sequence may not call any method that begins with dft unless it is in dft
 * format. A Sequence exits dft format by a call to idft() or idftRX().
 * <p>
 * </p>
 * Most of the interface for this class was written by Dave Harris on 11/13/98.
 * <p>
 * </p>
 *
 * @author Timothy Paik written in June 2004
 */
public class RealSequence {

  ////////////////////////
    //   PRIVATE FIELDS   //
    ////////////////////////
    private float[] seqvalues;

  //////////////////////
    //   CONSTRUCTORS   //
    //////////////////////
    /**
     * Constructs a Sequence of length n.
     *
     * @param n - the length of the array in this Sequence
     */
    public RealSequence(int n) {
        seqvalues = new float[n];
    }

    /**
     * Constructs a Sequence of length n containing n values of x.
     *
     * @param n - the length of the arrays in this Sequence
     * @param x - the value filled in the real-value array.
     */
    public RealSequence(int n, float x) {
        seqvalues = new float[n];
        for (int i = 0; i < n; i++) {
            seqvalues[ i] = x;
        }
    }

    /**
     * Constructs a Sequence about a float array.
     *
     * @param v - the float array representing the real values of this Sequence
     */
    public RealSequence(float[] v) {
        seqvalues = v;
    }

    /**
     * Constructs a Sequence using the values of a Sequence.
     *
     * @param S - the Sequence containing the initial real values of this
     * Sequence
     */
    public RealSequence(RealSequence S) {
        seqvalues = new float[S.seqvalues.length];
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[ i] = S.seqvalues[ i];
        }
    }

    /**
     * Constructs a Sequence using the values of a DoubleSequence.
     *
     * @param S - the DoubleSequence containing the initial real values of this
     * Sequence
     */
    public RealSequence(RealDoubleSequence S) {
        seqvalues = new float[S.length()];

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[ i] = (float) S.get(i);
        }
    }

  /////////////////////////
    //   OTHER FUNCTIONS   //
    /////////////////////////
    /**
     * Copies the values from a DoubleSequence into this Sequence.
     *
     * @param ds - the DoubleSequence to get the values from.
     * @throws SignalProcessingException - if the length of the DoubleSequence
     * is different from the length of this Sequence
     */
    public void getValues(RealDoubleSequence ds) throws SignalProcessingException {
        if (seqvalues.length != ds.length()) {
            throw new SignalProcessingException("Attempted to copy values from DoubleSequence into Sequence of different length");
        }

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[ i] = (float) ds.get(i);
        }
    }

    /**
     * Copies the values from a Sequence into this Sequence.
     *
     * @param s - the Sequence to get the values from.
     * @throws SignalProcessingException - if the length of the Sequence is
     * different from the length of this Sequence
     */
    public void getValues(RealSequence s) throws SignalProcessingException {
        if (seqvalues.length != s.length()) {
            throw new SignalProcessingException("Attempted to copy values from Sequence into another Sequence of different length");
        }

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[ i] = s.get(i);
        }
    }

    /**
     * Copies the values from a float array into this Sequence.
     *
     * @param s - the float array to get the values from.
     * @throws SignalProcessingException - if the length of the float array is
     * different from the length of this Sequence
     */
    public void getValues(float[] s) throws SignalProcessingException {
        if (seqvalues.length != s.length) {
            throw new SignalProcessingException("Attempted to copy values from double array into a DoubleSequence of different length");
        }

        System.arraycopy(s, 0, seqvalues, 0, seqvalues.length);
    }

    /**
     * Accesses the nth value of this Sequence.
     *
     * @param n - the index of the value returned.
     * @return a float representing the nth value of the Sequence.
     */
    public float get(int n) {
        return seqvalues[ n];
    }

    /**
     * Gets the array representing the real values of this Sequence.
     *
     * @return the array representing the reals of this Sequence.
     */
    public float[] getArray() {
        return seqvalues;
    }

    /**
     * Gets the length of this Sequence.
     *
     * @return the length of this Sequence.
     */
    public int length() {
        return seqvalues.length;
    }

    /**
     * Returns the value of the largest element in this Sequence.
     *
     * @return the largest element in this Sequence
     */
    public float extremum() {
        float smax = 0.0f;
        float sabs = 0.0f;
        for (int i = 0; i < seqvalues.length; i++) {
            sabs = Math.abs(seqvalues[ i]);
            if (sabs > smax) {
                smax = sabs;
            }
        }
        return smax;
    }

    /**
     * Returns the index of the largest element in this Sequence.
     *
     * @return the index of the largest element in this Sequence.
     */
    public int extremumIndex() {
        float smax = 0.0f;
        float sabs = 0.0f;
        int index = 0;
        for (int i = 0; i < seqvalues.length; i++) {
            sabs = Math.abs(seqvalues[ i]);
            if (sabs > smax) {
                smax = sabs;
                index = i;
            }
        }
        return index;
    }

    /**
     * Subtracts the mean value of this Sequence from every element in this
     * Sequence.
     */
    public void rmean() {
        float smean = 0.0f;
        for (int i = 0; i < seqvalues.length; i++) {
            smean += seqvalues[ i];
        }
        smean /= seqvalues.length;
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[ i] -= smean;
        }
    }

    /**
     * Zeroes out this Sequence.
     */
    public void zero() {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[ i] = 0.0f;
        }
    }

    /**
     * Modifies this Sequence by scaling this Sequence by a float value.
     *
     * @param a - the value to scale this Sequence by.
     */
    public void scaleBy(float a) {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[ i] *= a;
        }
    }

    /**
     * Modifies this Sequence by reversing this Sequence.
     */
    public void reverse() {
        float tmp;
        int j = seqvalues.length - 1;
        int i = 0;
        while (true) {
            if (j <= i) {
                break;
            }
            tmp = seqvalues[ i];
            seqvalues[ i] = seqvalues[ j];
            seqvalues[ j] = tmp;
            i++;
            j--;
        }
    }

    /**
     * Modifies this Sequence by shifting this Sequence over a certain amount of
     * samples and replacing the empty values with zeroes.
     *
     * @param shift - the amount of samples to shift this Sequence by. A
     * positive shift indicates a shift right.
     */
    public void zshift(int shift) {

        int n;
        int srcptr, dstptr;
        if (Math.abs(shift) > seqvalues.length) {
            zero(seqvalues, 0, seqvalues.length);   // zeroes sequence
        } else if (shift < 0) {                                    // left shift

            n = seqvalues.length + shift;
            dstptr = 0;
            srcptr = -shift;
            for (int i = 0; i < n; i++) {
                seqvalues[ dstptr++] = seqvalues[ srcptr++];
            }

            zero(seqvalues, seqvalues.length + shift, -shift);     // zero high end

        } else if (shift > 0) {                                    // right shift

            n = seqvalues.length - shift;
            dstptr = seqvalues.length - 1;
            srcptr = dstptr - shift;
            for (int i = 0; i < n; i++) {
                seqvalues[ dstptr--] = seqvalues[ srcptr--];
            }

            zero(seqvalues, 0, shift);                             // zero low end

        }

    }

    /**
     * Modifies this Sequence by shifting it over a certain amount of samples.
     * Any values shifted "off" the sequence reappear on the other end of this
     * Sequence.
     *
     * @param shift - the amount of samples to shift this Sequence by. A
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
        float[] buffer = new float[bsize];
        int n = seqvalues.length;

// two cases - right and left shifts
        int i, j;
        if (shift > 0) {                      // right shift

            shift = shift % n;                    // prevent extraneous transfers

            j = n - shift;
            for (i = 0; i < shift; i++) {
                buffer[ i] = seqvalues[ j++];
            }
            j = n - 1;
            i = j - shift;
            while (i >= 0) {
                seqvalues[ j--] = seqvalues[ i--];
            }
            for (i = 0; i < shift; i++) {
                seqvalues[ i] = buffer[ i];
            }

        } else if (shift < 0) {                 // left shift

            shift = shift % n;                    // prevent extraneous transfers

            for (i = 0; i < -shift; i++) {
                buffer[ i] = seqvalues[ i];
            }
            j = 0;
            i = -shift;
            while (i < n) {
                seqvalues[ j++] = seqvalues[ i++];
            }
            j = n + shift;
            for (i = 0; i < -shift; i++) {
                seqvalues[ j++] = buffer[ i];
            }

        }

    }

  // Multiplies a subsequence of a Sequence by a window beginning at the index start
    //   and returns the windowed subsequence.
    // Assumes the Sequence is equal to zero outside of its legal range.
    //
    //       ****************************************************************
    //   +++++++++++++++++++++++++++++++
    //                                                      +++++++++++++++++++++
    /**
     * Multiplies a subsequence of a RealSequence by a window beginning at the
     * index start and returns the windowed subsequence. Assumes the
     * RealSequence is equal to zero outside of its legal range.
     *
     * @param start- the index at which window should start multiplying.
     * @param window- the RealSequence with which to multiply this RealSequence
     * by to get the new RealSequence.
     * @return - a new RealSequence with the multiplied values in the window.
     */
    public RealSequence window(int start, RealSequence window) {

        int n = window.length();
        float[] newseqvalues = new float[n];

    // check for overlap - if none, return with zero subsequence
        if (start < seqvalues.length && start + n > 0) {

            int index0 = Math.max(0, -start);
            int index1 = Math.min(seqvalues.length - start, n);

            float[] windowvalues = window.seqvalues;
            for (int i = index0; i < index1; i++) {
                newseqvalues[ i] = seqvalues[ i + start] * windowvalues[ i];
            }

        }

        return new RealSequence(newseqvalues);
    }

    /**
     * Aliases this RealSequence into another RealSequence of length N. Does not
     * modify this RealSequence.
     *
     * @param N - the length of the new aliased RealSequence
     * @return a new RealSequence of length N representing the aliased
     * RealSequence
     */
    public RealSequence alias(int N) {
        float[] newseqvalues = new float[N];
        int index = 0;
        for (int i = 0; i < seqvalues.length; i++) {
            newseqvalues[ index++] += seqvalues[ i];
            if (index == N) {
                index = 0;
            }
        }
        return new RealSequence(newseqvalues);
    }

    /**
     * Modifies this RealSequence by stretching it out by a factor n. Fills the
     * extra spaces in the RealSequence with zeroes.
     *
     * @param factor - the factor by which to stretch this RealSequence out by
     */
    public void stretch(int factor) {
        int n = seqvalues.length;
        float[] sptr = new float[factor * n];
        zero(sptr, 0, factor * n);
        for (int i = n - 1; i >= 0; i--) {
            sptr[ factor * i] = seqvalues[ i];
        }
        seqvalues = sptr;
    }

    /**
     * Decimates this RealSequence by choosing every nth sample, where n =
     * factor. Modifies this RealSequence. Disposes of the original arrays that
     * hold the original values for this RealSequence.
     *
     * @param factor - the factor by which to decimate this RealSequence by.
     */
    public void decimate(int factor) {
        int dlen = seqvalues.length / factor;
        if (dlen * factor < seqvalues.length) {
            dlen++;
        }
        float[] dptr = new float[dlen];
        for (int i = 0; i < dlen; i++) {
            dptr[ i] = seqvalues[ i * factor];
        }
        seqvalues = dptr;
    }

    /**
     * Modifies this RealSequence by cropping out all values outside of the
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
        float[] newseqvalues = new float[n];
        for (int i = 0; i < n; i++) {
            newseqvalues[ i] = seqvalues[ i + i1];
        }
        seqvalues = newseqvalues;
        return true;
    }

    /**
     * Modifies this RealSequence by subtracting another RealSequence from it.
     * Lines up the ComplexSequences at 0 and subtracts every index up to the
     * length of the shorter RealSequence.
     *
     * @param S - the RealSequence to subtract from this RealSequence.
     */
    public void minusEquals(RealSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        for (int i = 0; i < n; i++) {
            seqvalues[ i] -= S.seqvalues[ i];
        }
    }

    /**
     * Modifies this RealSequence by adding another RealSequence to it. Lines up
     * the Sequences at 0 and adds every index up to the length of the shorter
     * RealSequence.
     *
     * @param S - the RealSequence to add to this RealSequence.
     */
    public void plusEquals(RealSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        for (int i = 0; i < n; i++) {
            seqvalues[ i] += S.seqvalues[ i];
        }
    }

    /**
     * Modifies this RealSequence by dividing it by another RealSequence. Lines
     * up the Sequences at 0 and divides every index up to the length of the
     * shorter RealSequence.
     *
     * @param S - the RealSequence to divide this RealSequence by.
     */
    public void divideBy(RealSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        for (int i = 0; i < n; i++) {
            seqvalues[ i] /= S.seqvalues[ i];
        }
    }

    /**
     * Finds the dot product between this RealSequence and another RealSequence.
     * If the Sequences have different lengths, then the dot product takes the
     * dot product of the overlapping region of the two Sequences, lining up the
     * Sequences at index 0.
     *
     * @param S - the other RealSequence in the dot product.
     * @return the value of the dot product in a float array form: the first
     * value is the real part, the second is the imaginary
     */
    public float dotprod(RealSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        float retval = 0.0f;
        float[] x = seqvalues;
        float[] y = S.seqvalues;
        for (int i = 0; i < n; i++) {
            retval += x[ i] * y[ i];
        }
        return retval;
    }

    /**
     * Finds the RealSequence containing the square of each value in this
     * RealSequence.
     *
     * @return a new RealSequence containing the square of each value in this
     * RealSequence
     */
    public RealSequence sqr() {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[ i] *= seqvalues[ i];
        }
        return this;
    }

    /**
     * Finds the RealSequence containing the square root of each value in this
     * RealSequence.
     *
     * @return a new RealSequence containing the square root of each value in
     * this RealSequence
     */
    public RealSequence sqrt() {
        for (int i = 0; i < seqvalues.length; i++) {
            if (seqvalues[ i] < 0.0f) {
                seqvalues[ i] = 0.0f;
            } else {
                seqvalues[ i] = (float) Math.sqrt(seqvalues[ i]);
            }
        }
        return this;
    }

    /**
     * Modifies this RealSequence by padding it to a larger length with zero
     * values. If the new length is shorter than this RealSequence's length,
     * there is no change to this RealSequence.
     *
     * @param newlength - the new length for this RealSequence.
     */
    public void pad_to(int newlength) {
        int n = seqvalues.length;
        if (newlength > n) {
            float[] tmp = new float[newlength];
            for (int i = 0; i < n; i++) {
                tmp[ i] = seqvalues[ i];
            }
            seqvalues = tmp;
            for (int i = n; i < newlength; i++) {
                seqvalues[ i] = 0.0f;
            }
        }
    }

    /**
     * Modifies this RealSequence by taking the discrete Fourier transform of
     * this RealSequence. Puts this RealSequence into Sorensen AND Bonzanigo's
     * format as mentioned in the comments about the RealSequence class.
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
        llnl.gnem.core.signalprocessing.FFT.rvfft(seqvalues, m);
    }

    /**
     * Modifies this RealSequence by taking the discrete Fourier transform of
     * this RealSequence. Uses the arbitrary radix stage of a pre-made FFT
     * object. Puts this RealSequence into Sorensen AND Bonzanigo's format as
     * mentioned in the comments about the RealSequence class.
     */
    public void dftRX(FFT fft) {
        pad_to(fft.fftsize());
        fft.rvfftRX(seqvalues);
    }

    /**
     * Modifies this RealSequence by replacing each element in this RealSequence
     * by its conjugate. This method only has meaning when this RealSequence
     * represents the discrete Fourier transform of another RealSequence. If
     * used on a normal RealSequence, the abstraction is broken.
     */
    public void dftConjugate() {
        int n = seqvalues.length;
        for (int i = n / 2 + 1; i < n; i++) {
            seqvalues[ i] = -seqvalues[ i];
        }
    }

    /**
     * Modifies this RealSequence by taking the inverse discrete Fourier
     * transform of this RealSequence. Does not have much meaning if the
     * RealSequence does not have a length that is a power of 2, or if the
     * RealSequence is not in Sorensen AND Bonzanigo's format.
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
        FFT.irvfft(seqvalues, m);
    }

    /**
     * Modifies this RealSequence by taking the inverse discrete Fourier
     * transform of this RealSequence. Uses the arbitrary radix stage of the
     * inverse Fourier transform.
     */
    public void idftRX(FFT fft) {
        pad_to(fft.fftsize());
        fft.irvfftRX(seqvalues);
    }

    /**
     * Returns a RealSequence representing the product between two Sequences.
     * Only has meaning if both Sequences are placed in Sorenson AND Bonzanigo's
     * format.
     *
     * @param x - the first RealSequence
     * @param y - the second RealSequence
     * @param c - either 1 or -1
     * @return the product between two Sequences representing dft's of other
     * Sequences
     */
    static public RealSequence dftprod(RealSequence x, RealSequence y, float c) {
        int n = x.length();
        int half = n / 2;
        RealSequence tmp;

        if (n != y.length() || half * 2 != n) {
            tmp = null;
        } else {
            tmp = new RealSequence(n);
            float[] xp = x.getArray();
            float[] yp = y.getArray();
            float[] tp = tmp.getArray();
            int k;
            tp[ 0] = xp[ 0] * yp[ 0];
            tp[ half] = xp[ half] * yp[ half];
            for (int i = 1; i < half; i++) {
                k = n - i;
                tp[ i] = xp[ i] * yp[ i] - c * xp[ k] * yp[ k];
                tp[ k] = xp[ k] * yp[ i] + c * xp[ i] * yp[ k];
            }
        }
        return tmp;
    }

    /**
     * Modifies this RealSequence by aliasing it by a factor.
     *
     * @param factor - the factor by which to alias this RealSequence by
     */
    public void dftAlias(int factor) {

    //
        //  Routine to alias FFT of an N-length RealSequence
        //  to allow the computation of x[n*factor] from
        //  its transform X(k).  Useful in computing downsampled
        //  sequences from their transforms.
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

        float[] ptr = new float[M];

        float Yrr, Yri;

        for (r = 0; r <= M / 2; r++) {
            Yrr = 0.0f;
            Yri = 0.0f;
            for (p = 0; p < factor; p++) {
                k = r + M * p;
                if (k <= N / 2) {
                    Yrr += seqvalues[ k];
                    if (k > 0 && k < N / 2) {
                        Yri += seqvalues[ N - k];
                    }
                } else {
                    Yrr += seqvalues[ N - k];
                    Yri -= seqvalues[ k];
                }
            }
            ptr[ r] = Yrr / factor;
            if (r != 0 && r != M / 2) {
                ptr[ M - r] = Yri / factor;
            }
        }

        seqvalues = ptr;
    }

    /**
     * Prints this RealSequence out to a PrintStream.
     *
     * @param ps - the PrintStream accepting the print data.
     */
    public void print(PrintStream ps) {
        for (int i = 0; i < seqvalues.length; i++) {
            ps.println(seqvalues[ i]);
        }
    }

    /**
     * Modifies this RealSequence by setting a value at a given index to a new
     * value.
     *
     * @param i - the index of the changed value
     * @param f - the real part of the new value
     */
    public void set(int i, float f) {
        if (i >= 0 & i < seqvalues.length) {
            seqvalues[ i] = f;
        }
    }

    /**
     * Modifies this RealSequence by setting every value in this RealSequence to
     * a constant real value.
     *
     * @param c - the new value for every element in this RealSequence
     */
    public void setConstant(float c) {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[ i] = c;
        }
    }

    public void times(int c) {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[ i] = seqvalues[i] * (float) c;
        }
    }

    public void times(float c) {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[ i] = seqvalues[i] * c;
        }
    }

    public void times(double c) {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[ i] = seqvalues[i] * (float) c;
        }
    }

    /**
     * Zeroes out a part of an array.
     *
     * @param s - the array to be zeroed out
     * @param start - the starting index at which to start the zeroes
     * @param duration - the amount of zeroes to place into the array
     */
    protected void zero(float[] s, int start, int duration) {
        int j = start;
        for (int i = 0; i < duration; i++) {
            s[ j++] = 0.0f;
        }
    }
}
