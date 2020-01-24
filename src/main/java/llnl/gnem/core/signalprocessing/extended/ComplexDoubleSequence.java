/**
 *  @author Tim Paik
 *  Created: 6/10/04
 *  Last modified: 6/10/04
 */

package llnl.gnem.core.signalprocessing.extended;


import java.io.PrintStream;

/**
 * A DoubleComplexSequence is a sequence of complex numbers that usually
 * represents a signal. The DoubleComplexSequence uses two arrays, one to
 * represent the real parts of the complex numbers, and one to represent
 * the imaginary parts. The nth number in the DoubleComplexSequence is
 * seqvalues[n] + imagvalues[n]*i. All numbers in the DoubleComplexSequence
 * use double precision, hence the name.
 */
public class ComplexDoubleSequence {
    ////////////////////////
    //   PRIVATE FIELDS   //
    ////////////////////////

    private double[] seqvalues;
    private double[] imagvalues;

    //////////////////////
    //   CONSTRUCTORS   //
    //////////////////////

    /**
     * Constructs a DoubleComplexSequence of length n.
     *
     * @param n - the length of this DoubleComplexSequence
     */
    public ComplexDoubleSequence(int n) {
        seqvalues = new double[n];
        imagvalues = new double[n];
    }

    /**
     * Constructs a Complex Sequence of length n where each element is
     * equal to value + 0i.
     *
     * @param n     - the length of this DoubleComplexSequence
     * @param value - the initial value of the real part of each element
     *              in this DoubleComplexSequence
     */
    public ComplexDoubleSequence(int n, double value) {
        seqvalues = new double[n];
        imagvalues = new double[n];

        for (int i = 0; i < n; i++) {
            seqvalues[i] = value;
            imagvalues[i] = 0;
        }
    }

    /**
     * Constructs a Complex Sequence of length n where each element is
     * equal to real + imag*i.
     *
     * @param n    - the length of this DoubleComplexSequence
     * @param real - the initial value of the real part of each element
     *             in this DoubleComplexSequence
     * @param imag - the initial value of the imaginary part of each
     *             element in this DoubleComplexSequence
     */
    public ComplexDoubleSequence(int n, double real, double imag) {
        seqvalues = new double[n];
        imagvalues = new double[n];

        for (int i = 0; i < n; i++) {
            seqvalues[i] = real;
            imagvalues[i] = imag;
        }
    }

    /**
     * Constructs a DoubleComplexSequence using the values of a double array
     * as the real values of the signal.
     *
     * @param v - the double array representing the initial values of the
     *          real parts of this DoubleComplexSequence
     */
    public ComplexDoubleSequence(double[] v) {
        seqvalues = v;
        imagvalues = new double[v.length];
    }

    /**
     * Constructs a DoubleComplexSequence using the values of two double arrays
     * as the real and imaginary parts of the values of a signal.
     *
     * @param u - the double array representing the initial values of the
     *          real parts of this DoubleComplexSequence
     * @param v - the double array representing the initial values of the
     *          imaginary parts of this DoubleComplexSequence
     * @throws SignalProcessingException - if the two arrays have different lengths
     */
    public ComplexDoubleSequence(double[] u, double[] v) throws SignalProcessingException {
        if (u.length != v.length) {
            throw new SignalProcessingException
                    ("Attempted to construct a DoubleComplexSequence with arrays of different length");
        } else {
            seqvalues = u;
            imagvalues = v;
        }
    }

    /**
     * Constructs a DoubleComplexSequence using the values of a ComplexSequence.
     *
     * @param S - the DoubleComplexSequence containing the initial values of this
     *          DoubleComplexSequence
     */
    public ComplexDoubleSequence(ComplexSequence S) {
        seqvalues = new double[S.length()];
        imagvalues = new double[S.length()];

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = (double) S.getR(i);
            imagvalues[i] = (double) S.getI(i);
        }
    }

    /**
     * Constructs a DoubleComplexSequence using the values of a Sequence. The imaginary
     * values are set to 0.
     *
     * @param S - the Sequence containing the initial real values of this
     *          DoubleComplexSequence
     */
    public ComplexDoubleSequence(RealSequence S) {
        seqvalues = new double[S.length()];
        imagvalues = new double[S.length()];

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = S.get(i);
            imagvalues[i] = 0;
        }
    }

    /**
     * Constructs a DoubleComplexSequence using the values of another DoubleComplexSequence.
     *
     * @param S - the DoubleSequence containing the initial real values of this
     *          DoubleComplexSequence
     */
    public ComplexDoubleSequence(RealDoubleSequence S) {
        seqvalues = new double[S.length()];
        imagvalues = new double[S.length()];

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = S.get(i);
            imagvalues[i] = 0;
        }
    }

    /**
     * Constructs a DoubleComplexSequence using the values of another DoubleComplexSequence.
     *
     * @param S - the DoubleComplexSequence containing the initial values of this
     *          DoubleComplexSequence
     */
    public ComplexDoubleSequence(ComplexDoubleSequence S) {
        seqvalues = new double[S.seqvalues.length];
        imagvalues = new double[S.seqvalues.length];

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = S.seqvalues[i];
            imagvalues[i] = S.imagvalues[i];
        }
    }

    /////////////////////////
    //   OTHER FUNCTIONS   //
    /////////////////////////

    /**
     * Obtains values from a DoubleSequence and places them into the real array of this
     * DoubleComplexSequence. The imaginary parts of this DoubleComplexSequence are reset
     * to 0.
     *
     * @param ds - the DoubleSequence carrying the new real values of this
     *           DoubleComplexSequence
     * @throws SignalProcessingException - if the DoubleSequence is not the same length as
     *                                   this DoubleComplexSequence
     */
    public void getValues(RealDoubleSequence ds) throws SignalProcessingException {
        if (seqvalues.length != ds.length())
            throw new SignalProcessingException
                    ("Attempted to copy values from DoubleSequence into DoubleComplexSequence of different length");

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = ds.get(i);
            imagvalues[i] = 0;
        }
    }

    /**
     * Obtains values from a ComplexSequence and places them into the arrays of this
     * DoubleComplexSequence.
     *
     * @param cs - the ComplexSequence carrying the new real values of this
     *           DoubleComplexSequence
     * @throws SignalProcessingException - if the ComplexSequence is not the same length as
     *                                   this DoubleComplexSequence
     */
    public void getValues(ComplexSequence cs) throws SignalProcessingException {
        if (seqvalues.length != cs.length())
            throw new SignalProcessingException
                    ("Attempted to copy values from ComplexSequence into DoubleComplexSequence of different length");

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = cs.getR(i);
            imagvalues[i] = cs.getI(i);
        }
    }

    /**
     * Obtains values from a DoubleComplexSequence and places them into the arrays of this
     * DoubleComplexSequence.
     *
     * @param dcs - the DoubleComplexSequence carrying the new values of this
     *            DoubleComplexSequence
     * @throws SignalProcessingException - if the other DoubleComplexSequence is not the
     *                                   same length as this DoubleComplexSequence
     */
    public void getValues(ComplexDoubleSequence dcs) throws SignalProcessingException {
        if (seqvalues.length != dcs.length())
            throw new SignalProcessingException
                    ("Attempted to copy values from one DoubleComplexSequence into another " +
                    "DoubleComplexSequence of different length");

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = (dcs.get(i))[0];
            imagvalues[i] = (dcs.get(i))[1];
        }
    }

    /**
     * Obtains values from a Sequence and places them into the real array of this
     * DoubleComplexSequence. The imaginary parts of this DoubleComplexSequence are reset
     * to 0.
     *
     * @param s - the Sequence carrying the new real values of this
     *          DoubleComplexSequence
     * @throws SignalProcessingException - if the Sequence is not the same length as
     *                                   this DoubleComplexSequence
     */
    public void getValues(RealSequence s) throws SignalProcessingException {
        if (seqvalues.length != s.length())
            throw new SignalProcessingException
                    ("Attempted to copy values from Sequence into DoubleComplexSequence of different length");

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = s.get(i);
            imagvalues[i] = 0;
        }
    }

    /**
     * Obtains values from a double array and places them into the real array of this
     * DoubleComplexSequence. The imaginary parts of this DoubleComplexSequence are reset
     * to 0.
     *
     * @param s - the double array carrying the new real values of this
     *          DoubleComplexSequence
     * @throws SignalProcessingException - if the double array is not the same length as
     *                                   this DoubleComplexSequence
     */
    public void getValues(double[] s) throws SignalProcessingException {
        if (seqvalues.length != s.length)
            throw new SignalProcessingException
                    ("Attempted to copy values from double array into DoubleComplexSequence of different length");

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = s[i];
            imagvalues[i] = 0;
        }
    }

    /**
     * Obtains values from two double arrays and places them into the arrays of this
     * DoubleComplexSequence.
     *
     * @param s - the double array carrying the new real values of this
     *          DoubleComplexSequence
     * @param t - the double array carrying the new imaginary values of this
     *          DoubleComplexSequence
     * @throws SignalProcessingException - if either of the double arrays is not the
     *                                   same length as this DoubleComplexSequence
     */
    public void getValues(double[] s, double[] t) throws SignalProcessingException {
        if (seqvalues.length != s.length || seqvalues.length != t.length)
            throw new SignalProcessingException
                    ("Attempted to copy values from double array into DoubleComplexSequence of different length");

        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = s[i];
            imagvalues[i] = t[i];
        }
    }

    /**
     * Accesses the nth value of the DoubleComplexSequence. The first value
     * of the array is the real part, the second is the imaginary part.
     *
     * @param n - the index of the value returned.
     * @return a double[] representing the nth value of the DoubleComplexSequence.
     */
    public double[] get(int n) {
        double[] cval = new double[2];
        cval[0] = seqvalues[n];
        cval[1] = imagvalues[n];
        return cval;
    }

    /**
     * Accesses the real part of the nth value of this DoubleComplexSequence.
     *
     * @param n - the index of the complex value
     * @return a double representing the real part of the nth value
     *         of this DoubleComplexSequence.
     */
    public double getR(int n) {
        return seqvalues[n];
    }

    /**
     * Accesses the imaginary part of the nth value of this DoubleComplexSequence.
     *
     * @param n - the index of the complex value
     * @return a double representing the imaginary part of the nth value of this
     *         DoubleComplexSequence.
     */
    public double getI(int n) {
        return imagvalues[n];
    }

    /**
     * Gets the array representing the real values of this DoubleComplexSequence.
     *
     * @return - the array representing the reals of this DoubleComplexSequence.
     */
    public double[] getRealArray() {
        return seqvalues;
    }

    /**
     * Gets the array representing the imaginary values of this
     * DoubleComplexSequence.
     *
     * @return the array representing the imaginary values of this
     *         DoubleComplexSequence.
     */
    public double[] getImagArray() {
        return imagvalues;
    }

    /**
     * Gets the length of this DoubleComplexSequence.
     *
     * @return the length of this DoubleComplexSequence.
     */
    public int length() {
        return seqvalues.length;
    }

    /**
     * Finds the element in this DoubleComplexSequence with the largest
     * modulus, then returns the modulus of the element.
     *
     * @return the modulus of the largest element in this DoubleComplexSequence
     */
    public double extremum() {
        double smax = 0.0f;
        double sabs = 0.0f;
        for (int i = 0; i < seqvalues.length; i++) {
            double x = seqvalues[i];
            double y = imagvalues[i];
            sabs = x * x + y * y;
            if (sabs > smax) {
                smax = sabs;
            }
        }
        return Math.sqrt(smax);
    }

    /**
     * Finds the element in this DoubleComplexSequence with the largest
     * modulus, then returns the index of the element.
     *
     * @return the index of the element in this DoubleComplexSequence
     *         with the largest modulus.
     */
    public int extremumIndex() {
        double smax = 0.0f;
        double sabs = 0.0f;
        int index = 0;
        for (int i = 0; i < seqvalues.length; i++) {
            double x = seqvalues[i];
            double y = imagvalues[i];
            sabs = x * x + y * y;
            if (sabs > smax) {
                smax = sabs;
                index = i;
            }
        }
        return index;
    }

    /**
     * Subtracts the mean value of this DoubleComplexSequence from every
     * element in this ComplexSequence. Modifies this DoubleComplexSequence.
     */
    public void rmean() {
        double smean = 0.0f;
        double imean = 0.0f;
        for (int i = 0; i < seqvalues.length; i++) {
            smean += seqvalues[i];
            imean += imagvalues[i];
        }
        smean /= seqvalues.length;
        imean /= imagvalues.length;
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] -= smean;
            imagvalues[i] -= imean;
        }
    }

    /**
     * Zeroes out this DoubleComplexSequence.
     */
    public void zero() {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = 0.0f;
            imagvalues[i] = 0.0f;
        }
    }

    /**
     * Modifies this DoubleComplexSequence by scaling this DoubleComplexSequence
     * by a double-precision value.
     *
     * @param a - the value to scale this DoubleComplexSequence by.
     */
    public void scaleBy(double a) {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] *= a;
            imagvalues[i] *= a;
        }
    }

    /**
     * Modifies this DoubleComplexSequence by reversing this DoubleComplexSequence.
     */
    public void reverse() {
        double tmp, tmp2;
        int j = seqvalues.length - 1;
        int i = 0;
        while (true) {
            if (j <= i) break;
            tmp = seqvalues[i];
            tmp2 = imagvalues[i];
            seqvalues[i] = seqvalues[j];
            imagvalues[i] = imagvalues[j];
            seqvalues[j] = tmp;
            imagvalues[j] = tmp2;
            i++;
            j--;
        }
    }

    /**
     * Modifies this DoubleComplexSequence by shifting it over a certain amount
     * of samples and replaces the empty values with zeroes.
     *
     * @param shift - the amount of samples to shift the DoubleComplexSequence by.
     *              A positive shift indicates a shift right.
     */
    public void zshift(int shift) {
        int n;
        int srcptr, dstptr;
        if (Math.abs(shift) > seqvalues.length) {
            zero();
        } else if (shift < 0) {                                    // left shift
            n = seqvalues.length + shift;
            dstptr = 0;
            srcptr = -shift;
            for (int i = 0; i < n; i++) {
                seqvalues[dstptr] = seqvalues[srcptr];
                imagvalues[dstptr++] = imagvalues[srcptr++];
            }

            zero(seqvalues, seqvalues.length + shift, -shift);        // zero high end
            zero(imagvalues, imagvalues.length + shift, -shift);      // zero high end
        } else if (shift > 0) {                                         // right shift
            n = seqvalues.length - shift;
            dstptr = seqvalues.length - 1;
            srcptr = dstptr - shift;
            for (int i = 0; i < n; i++) {
                seqvalues[dstptr] = seqvalues[srcptr];
                imagvalues[dstptr--] = imagvalues[srcptr--];
            }

            zero(seqvalues, 0, shift);                                // zero low end
            zero(imagvalues, 0, shift);                               // zero low end
        }
    }

    /**
     * Modifies this DoubleComplexSequence by shifting it over a certain
     * amount of samples. Any values shifted "off" the sequence reappear on the
     * other end of this DoubleComplexSequence.
     *
     * @param shift - the amount of samples to shift the DoubleComplexSequence by.
     *              A positive shift indicates a shift right.
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
        double[] buffer2 = new double[bsize];
        int n = seqvalues.length;

        // two cases - right and left shifts


        int i, j;
        if (shift > 0) {                      // right shift

            shift = shift % n;                    // prevent extraneous transfers

            j = n - shift;
            for (i = 0; i < shift; i++) {
                buffer[i] = seqvalues[j];
                buffer2[i] = imagvalues[j++];
            }

            j = n - 1;
            i = j - shift;
            while (i >= 0) {
                seqvalues[j] = seqvalues[i];
                imagvalues[j--] = imagvalues[i--];
            }
            for (i = 0; i < shift; i++) {
                seqvalues[i] = buffer[i];
                imagvalues[i] = buffer2[i];
            }
        } else if (shift < 0) {                 // left shift

            shift = shift % n;                    // prevent extraneous transfers

            for (i = 0; i < -shift; i++) {
                buffer[i] = seqvalues[i];
                buffer2[i] = imagvalues[i];
            }

            j = 0;
            i = -shift;
            while (i < n) {
                seqvalues[j] = seqvalues[i];
                imagvalues[j++] = imagvalues[i++];
            }

            j = n + shift;
            for (i = 0; i < -shift; i++) {
                seqvalues[j] = buffer[i];
                imagvalues[j++] = buffer2[i];
            }
        }
    }


    /**
     * Multiplies a subsequence of a DoubleComplexSequence by a window beginning
     * at the index start and returns the windowed subsequence. Assumes the
     * DoubleComplexSequence is equal to zero outside of its legal range.
     *
     * @param start-  the index at which window should start multiplying.
     * @param window- the DoubleComplexSequence with which to multiply this DoubleComplexSequence by to get
     *                the new DoubleComplexSequence.
     * @return - a new DoubleComplexSequence with the multiplied values in the window.
     */
    public ComplexDoubleSequence window(int start, ComplexDoubleSequence window) throws Exception {

        int n = window.length();
        double[] newseqvalues = new double[n];
        double[] newimagvalues = new double[n];

        // check for overlap - if none, return with zero subsequence

        if (start < seqvalues.length && start + n > 0) {
            int index0 = Math.max(0, -start);
            int index1 = Math.min(seqvalues.length - start, n);

            double[] windowseqvalues = window.seqvalues;
            double[] windowimagvalues = window.imagvalues;

            for (int i = index0; i < index1; i++) {
                newseqvalues[i] = (seqvalues[i + start] * windowseqvalues[i]) -
                        (imagvalues[i + start] * windowimagvalues[i]);
                newimagvalues[i] = (seqvalues[i + start] * windowimagvalues[i]) +
                        (imagvalues[i + start] * windowseqvalues[i]);
            }

            return new ComplexDoubleSequence(newseqvalues, newimagvalues);
        } else {
            return this;
        }
    }

    /**
     * Aliases this DoubleComplexSequence into another DoubleComplexSequence
     * of length N. Does not modify this DoubleComplexSequence.
     *
     * @param N - the length of the new aliased DoubleComplexSequence
     * @return a new DoubleComplexSequence of length N representing the aliased
     *         DoubleComplexSequence
     */
    public ComplexDoubleSequence alias(int N) {
        try {
            double[] newseqvalues = new double[N];
            double[] newimagvalues = new double[N];
            int index = 0;
            for (int i = 0; i < seqvalues.length; i++) {
                newseqvalues[index] += seqvalues[i];
                newimagvalues[index++] += imagvalues[i];
                if (index == N) index = 0;
            }
            return new ComplexDoubleSequence(newseqvalues, newimagvalues);
        } catch (Exception e) {
            System.err.println("alias(): This error should never be seen.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Modifies this DoubleComplexSequence by stretching it by a factor n.
     * The extra spaces between signals are filled with zeroes.
     *
     * @param n - the factor by which to stretch this DoubleComplexSequence by
     */
    public void stretch(int n) {
        int length = seqvalues.length;
        double[] sptr = new double[n * length];
        double[] iptr = new double[n * length];
        zero(sptr, 0, n * length);
        for (int i = length - 1; i >= 0; i--) {
            sptr[n * i] = seqvalues[i];
            iptr[n * i] = imagvalues[i];
        }
        seqvalues = sptr;
        imagvalues = iptr;
    }

    /**
     * Decimates this DoubleComplexSequence by choosing every nth sample, where n = factor.
     * Modifies this DoubleComplexSequence.
     *
     * @param factor - the factor by which to decimate this DoubleComplexSequence by
     */
    public void decimate(int factor) {
        int dlen = seqvalues.length / factor;
        if (dlen * factor < seqvalues.length) dlen++;
        double[] dptr1 = new double[dlen];
        double[] dptr2 = new double[dlen];
        for (int i = 0; i < dlen; i++) {
            dptr1[i] = seqvalues[i * factor];
            dptr2[i] = imagvalues[i * factor];
        }
        seqvalues = dptr1;
        imagvalues = dptr2;
    }

    /**
     * Modifies this DoubleComplexSequence by cropping out all values outside of the interval
     * specified by the input values. Returns true if the cut is successful, false otherwise.
     *
     * @param i1 - the left index of the cutting region.
     * @param i2 - the right index of the cutting region.
     * @return true if the cut is successful (if the region is inside the bounds of the
     *         original region)
     */
    public boolean cut(int i1, int i2) {
        if (i2 < i1) return false;
        if (i1 < 0) return false;
        if (i2 > seqvalues.length - 1) return false;

        int n = i2 - i1 + 1;
        double[] newseqvalues = new double[n];
        double[] newimagvalues = new double[n];
        for (int i = 0; i < n; i++) {
            newseqvalues[i] = seqvalues[i + i1];
            newimagvalues[i] = imagvalues[i + i1];
        }
        seqvalues = newseqvalues;
        imagvalues = newimagvalues;
        return true;
    }

    /**
     * Modifies this DoubleComplexSequence by subtracting another DoubleComplexSequence from it.
     * Lines up the DoubleComplexSequences at 0 and subtracts every index up to the length
     * of the shorter DoubleComplexSequence.
     *
     * @param S - the DoubleComplexSequence to subtract from this DoubleComplexSequence.
     */
    public void minusEquals(ComplexDoubleSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        for (int i = 0; i < n; i++) {
            seqvalues[i] -= S.seqvalues[i];
            imagvalues[i] -= S.imagvalues[i];
        }
    }

    /**
     * Modifies this DoubleComplexSequence by adding another DoubleComplexSequence to it.
     * Lines up the DoubleComplexSequences at 0 and adds every index up to the length
     * of the shorter DoubleComplexSequence.
     *
     * @param S - the DoubleComplexSequence to add to this DoubleComplexSequence.
     */
    public void plusEquals(ComplexDoubleSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        for (int i = 0; i < n; i++) {
            seqvalues[i] += S.seqvalues[i];
            imagvalues[i] += S.imagvalues[i];
        }
    }

    /**
     * Modifies this DoubleComplexSequence by dividing it by another DoubleComplexSequence.
     * Lines up the DoubleComplexSequences at 0 and subtracts every index up to the length
     * of the shorter DoubleComplexSequence.
     *
     * @param S - the DoubleComplexSequence to divide this DoubleComplexSequence by.
     */
    public void divideBy(ComplexDoubleSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        double a, b, c, d;
        for (int i = 0; i < n; i++) {
            a = seqvalues[i];
            b = imagvalues[i];
            c = S.seqvalues[i];
            d = S.imagvalues[i];

            seqvalues[i] = (a * c - b * d) / (c * c + d * d);
            imagvalues[i] = (b * c + a * d) / (c * c + d * d);
        }
    }

    /**
     * Finds the dot product between this DoubleComplexSequence and another DoubleComplexSequence.
     * <p></p>
     * If the DoubleComplexSequences have different lengths, then the dot product takes the
     * dot product of the overlapping region of the two DoubleComplexSequences, lining up the
     * DoubleComplexSequences at index 0.
     *
     * @param S - the other DoubleComplexSequence in the dot product
     * @return the value of the dot product
     */
    public double[] dotprod(ComplexDoubleSequence S) {
        int n = Math.min(seqvalues.length, S.seqvalues.length);
        double realval = 0.0f;
        double imagval = 0.0f;
        double[] realx = seqvalues;
        double[] realy = S.seqvalues;
        double[] imagx = imagvalues;
        double[] imagy = S.imagvalues;

        for (int i = 0; i < n; i++) {
            realval += (realx[i] * realy[i] - imagx[i] * imagy[i]);
            imagval += (realx[i] * imagy[i] + imagx[i] * realy[i]);
        }

        double[] retvals = new double[2];
        retvals[0] = realval;
        retvals[1] = imagval;
        return retvals;
    }

    /**
     * Finds the DoubleSequence containing the square of the modulus of each
     * value in this DoubleComplexSequence.
     *
     * @return a new DoubleSequence containing the square of the modulus of each
     *         value in this DoubleComplexSequence
     */
    public RealDoubleSequence sqr() {
        double[] tmp = new double[seqvalues.length];
        for (int i = 0; i < seqvalues.length; i++) {
            double a = seqvalues[i];
            double b = imagvalues[i];

            tmp[i] = (a * a + b * b);
        }
        return new RealDoubleSequence(tmp);
    }

    /**
     * Finds the DoubleSequence containing the modulus of each value in this
     * DoubleComplexSequence.
     *
     * @return a new DoubleSequence containing the modulus of each value in
     *         this DoubleComplexSequence
     */
    public RealDoubleSequence modulus() {
        double[] arr = new double[seqvalues.length];

        for (int i = 0; i < seqvalues.length; i++) {
            double a = seqvalues[i];
            double b = imagvalues[i];

            arr[i] = Math.sqrt(a * a + b * b);
        }
        return new RealDoubleSequence(arr);
    }

    /**
     * Modifies this DoubleComplexSequence by adding zeroes to the end of this
     * DoubleComplexSequence, up to the specified length. If the
     * new length is shorter than this DoubleComplexSequence's length, there is no change.
     *
     * @param newlength - the new length for this DoubleComplexSequence.
     */
    public void pad_to(int newlength) {
        int n = seqvalues.length;
        if (newlength > n) {
            double[] tmp1 = new double[newlength];
            double[] tmp2 = new double[newlength];
            for (int i = 0; i < n; i++) {
                tmp1[i] = seqvalues[i];
                tmp2[i] = imagvalues[i];
            }
            for (int i = n; i < newlength; i++) {
                tmp1[i] = 0.0d;
                tmp2[i] = 0.0d;
            }
            seqvalues = tmp1;
            imagvalues = tmp2;
        }
    }

    /**
     * Modifies this DoubleComplexSequence by taking the discrete Fourier transform of
     * this DoubleComplexSequence.
     */
    public void dft() {
        ComplexFFTD fft = new ComplexFFTD(seqvalues.length, 1);

        int n = 1;
        while (n < seqvalues.length) {
            n *= 2;
        }
        pad_to(n);

        fft.cfft(seqvalues, imagvalues);
    }

    /**
     * Modifies this DoubleComplexSequence by taking the discrete Fourier transform of
     * this DoubleComplexSequence. Saves time by reusing an old ComplexFFTD. Does
     * not use the arbitrary radix stage of the ComplexFFTD.
     *
     * @throws SignalProcessingException - if a ComplexFFTD too small for this
     *                                   DoubleComplexSequence is passed as a parameter
     */
    public void dft(ComplexFFTD fft) throws SignalProcessingException {
        if (seqvalues.length > fft.length())
            throw new SignalProcessingException("ComplexFFTD is too small for DoubleComplexSequence");

        pad_to(fft.length());
        fft.cfft(seqvalues, imagvalues);
    }

    /**
     * Modifies this DoubleComplexSequence by taking the discrete Fourier transform of
     * this DoubleComplexSequence. Uses the arbitrary radix stage of the ComplexFFTD.
     */
    public void dftRX(int radix) {
        int newlength = seqvalues.length;
        while (newlength % radix != 0)
            newlength++;

        ComplexFFTD fft = new ComplexFFTD(newlength / radix, radix);

        int n = 1;
        while (n < newlength) {
            n *= 2;
        }
        pad_to(n * radix);

        fft.cfftRX(seqvalues, imagvalues);
    }

    /**
     * Modifies this DoubleComplexSequence by taking the discrete Fourier transform of
     * this DoubleComplexSequence. Uses the arbitrary radix stage of a pre-made ComplexFFTD.
     *
     * @throws SignalProcessingException - if a ComplexFFTD too small for this
     *                                   DoubleComplexSequence is passed as a parameter
     */
    public void dftRX(ComplexFFTD fft) throws SignalProcessingException {
        if (seqvalues.length > fft.length())
            throw new SignalProcessingException("ComplexFFTD is too small for DoubleComplexSequence");
        pad_to(fft.length());
        fft.cfftRX(seqvalues, imagvalues);
    }

    /**
     * Modifies this DoubleComplexSequence by taking the inverse discrete Fourier transform
     * of this DoubleComplexSequence. Does not have much meaning if the DoubleComplexSequence
     * does not have a length that is a power of 2.
     */
    public void idft() {
        ComplexFFTD fft = new ComplexFFTD(seqvalues.length, 1);

        int n = 1;
        while (n < seqvalues.length) {
            n *= 2;
        }
        pad_to(n);

        fft.cifft(seqvalues, imagvalues);
    }

    /**
     * Modifies this DoubleComplexSequence by taking the inverse discrete Fourier transform
     * of this DoubleComplexSequence. Does not have much meaning if the DoubleComplexSequence
     * does not have a length that is a power of 2. Saves time by using a pre-made ComplexFFTD.
     *
     * @param fft - the ComplexFFTD object to take the inverse FFT of this DoubleComplexSequence.
     */
    public void idft(ComplexFFTD fft) {
        int n = 1;
        while (n < seqvalues.length) {
            n *= 2;
        }
        pad_to(n);

        fft.cifft(seqvalues, imagvalues);
    }

    /**
     * Modifies this DoubleComplexSequence by replacing each element in this DoubleComplexSequence
     * by its conjugate.
     */
    public void conjugate() {
        for (int i = 0; i < imagvalues.length; i++)
            imagvalues[i] = -imagvalues[i];
    }

    public void times(int c)
    {
        for ( int i = 0; i < seqvalues.length; i++ )
        {
            seqvalues[ i ]  = seqvalues[i]  * (double) c;
            imagvalues[ i ] = imagvalues[i] * (double) c;
        }
    }
    public void times(float c)
    {
        for ( int i = 0; i < seqvalues.length; i++ )
        {
            seqvalues[ i ]  = seqvalues[i]  * (double) c;
            imagvalues[ i ] = imagvalues[i] * (double) c;
        }
    }
    public void times(double c)
    {
        for ( int i = 0; i < seqvalues.length; i++ )
        {
            seqvalues[ i ]  = seqvalues[i]  * c;
            imagvalues[ i ] = imagvalues[i] * c;
        }
    }
    /**
     * Returns a DoubleComplexSequence representing the product between two DoubleComplexSequences.
     *
     * @param x - the first DoubleComplexSequence
     * @param y - the second DoubleComplexSequence
     * @param c - either 1 or -1
     * @return the product between two DoubleComplexSequences
     */
    static public ComplexDoubleSequence dftprod(ComplexDoubleSequence x, ComplexDoubleSequence y, double c) {
        int n = x.length();
        ComplexDoubleSequence tmp;

        if (n != y.length())
            tmp = null;
        else {
            tmp = new ComplexDoubleSequence(n);
            double[] xpreal = x.getRealArray();
            double[] xpimag = x.getImagArray();
            double[] ypreal = y.getRealArray();
            double[] ypimag = y.getImagArray();
            double[] tpreal = tmp.getRealArray();
            double[] tpimag = tmp.getImagArray();

            for (int i = 0; i < n; i++) {
                tpreal[i] = xpreal[i] * ypreal[i] - c * xpimag[i] * ypimag[i];     // todo this needs to assign to tmp
                tpimag[i] = xpimag[i] * ypreal[i] + c * xpreal[i] * ypimag[i];
            }
        }
        return tmp;
    }

    /**
     * Modifies this DoubleComplexSequence by aliasing it by a factor.
     *
     * @param factor - the factor by which to alias this DoubleComplexSequence by
     */
    public void dftAlias(int factor) {
        ComplexDoubleSequence dcs = alias(seqvalues.length / factor);
        double[] newreals = new double[seqvalues.length / factor];
        double[] newimags = new double[seqvalues.length / factor];
        for (int i = 0; i < newreals.length; i++) {
            newreals[i] = dcs.seqvalues[i] / factor;
            newimags[i] = dcs.imagvalues[i] / factor;
        }
        seqvalues = newreals;
        imagvalues = newimags;
    }

    /**
     * Prints this DoubleComplexSequence out to a PrintStream.
     *
     * @param ps - the PrintStream accepting the print data.
     */
    public void print(PrintStream ps) {
        for (int i = 0; i < seqvalues.length; i++) {
            ps.println(seqvalues[i] + "\t" + imagvalues[i]);
        }
    }

    /**
     * Modifies this DoubleComplexSequence by setting a value at a given index
     * to a new value.
     *
     * @param i - the index of the changed value
     * @param f - the real part of the new value
     * @param g - the imaginary part of the new value
     */
    public void set(int i, double f, double g) {
        if (i >= 0 & i < seqvalues.length) {
            seqvalues[i] = f;
            imagvalues[i] = g;
        }
    }

    /**
     * Modifies this DoubleComplexSequence by setting every value in this
     * DoubleComplexSequence to a constant real value.
     *
     * @param c - the new value for every element in this DoubleComplexSequence
     */
    public void setConstant(double c) {
        for (int i = 0; i < seqvalues.length; i++) {
            seqvalues[i] = c;
            imagvalues[i] = 0;
        }
    }

    /**
     * Zeroes out a part of an array.
     *
     * @param s        - the array to be zeroed out
     * @param start    - the starting index at which to start the zeroes
     * @param duration - the amount of zeroes to place into the array
     */
    protected void zero(double[] s, int start, int duration) {
        int j = start;
        for (int i = 0; i < duration; i++) {
            s[j++] = 0.0f;
        }
    }
}
