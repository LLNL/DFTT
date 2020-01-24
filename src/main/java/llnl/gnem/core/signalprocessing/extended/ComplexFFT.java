/*

CODE TRANSLATED FROM C TO JAVA BY TIMOTHY PAIK
CREATED: 6/14/04
LAST MODIFIED: 6/14/04

From mrp@angmar.llnl.gov Wed Nov 16 15:44 PST 1994
Date: Wed, 16 Nov 1994 15:46:36 -0800
From: Mike Portnoff <mrp@angmar.llnl.gov>
To: dbh@s18.es.llnl.gov
Subject: Split-Radix Complex FFT (not my latest version but should work fine)
*/
/******************************************************************************
 *                                                                            *
 *  void cfft  (z, nz)     << forward fft >>                                  *
 *                                                                            *
 *      In-place, split-radix complex fft procedure using decimation-in-      *
 *      frequency with internal look-up tables for cos, sin and bit reverser. *
 *                                                                            *
 *  input/output                                                              *
 *      z     - array of nz complex floats to be transformed in place         *
 *              data are stored with real and imaginary parts interleaved     *
 *      nz    - transform size (must be an integer power of 2)                *
 *                                                                            *
 *  author:                                                                   *
 *      M. R. Portnoff,   Lawrence Livermore Nat'l. Lab      June 1991        *
 *                                                                            *
 *  acknowledgement:                                                          *
 *      internal procedures cfft_(), cfft_stage() and cfft_table() were       *
 *      adapted from fortran codes by H. V. Sorensen (Dec. 1984, Jul. 1987)   *
 *                                                                            *
 *      This program may be used and distributed freely                       *
 *      so long as this header is included                                    *
 ******************************************************************************/

package llnl.gnem.core.signalprocessing.extended;

import java.util.LinkedList;

/* This class's primary function is to produce FFTs and inverse FFTs of complex
 * sequences, which are represented by two arrays of doubles. The FFTs are
 * in-place, split-radix FFTs with an arbitrary radix stage. They use internal
 * look-up tables for cos/sin values and bit-reversers.
 * <p>
 * One instantiation of the ComplexFFT class holds its length and its own
 * cosine, sine and bit reversal tables, and thus can perform multiple FFTs of
 * the same length quickly.
 * <p>
 * Originally written as fortran codes by H. V. Sorensen (Dec. 1984, Jul. 1984).
 * Adapted to C by M. R. Portnoff (June 1991).
 * Adapted to Java by Timothy Paik (June 2004).
 * @author Timothy Paik
 */

public class ComplexFFT {
    private static final double SQRT2OV2 = Math.sqrt(2) / 2;
    private final int radix;
    private final int power_of_two;
    private final LinkedList<float[]> buffers;
    private final CFFT_Table table;

    /**
     * Creates an ComplexFFT object of length M*r, where M is the smallest
     * power of two greater than n, and r is an arbitrary number.
     *
     * @param n - a value determining the size of the power of two, M
     * @param r - an arbitrary radix
     */
    public ComplexFFT(int n, int r) {
        power_of_two = cfft_powerof2(n);
        radix = r;
        table = new CFFT_Table((1 << power_of_two));
        buffers = new LinkedList<>();
        for (int i = 0; i < 2 * radix; i++)
            buffers.add(new float[1 << power_of_two]);
    }
    
    public ComplexFFT(int powerOf2) {
        power_of_two = powerOf2;
        radix = 1;
        table = new CFFT_Table((1 << power_of_two));
        buffers = new LinkedList<>();
        for (int i = 0; i < 2 * radix; i++)
            buffers.add(new float[1 << power_of_two]);
    }

    /**
     * Returns the length of this ComplexFFT object.
     *
     * @return the length of the arrays that this ComplexFFT object is
     *         prepared to handle with the cfftRX function.
     */
    public int length() {
        return radix * (1 << power_of_two);
    }

    /**
     * Takes the forward fft of a complex sequence of numbers represented as
     * two arrays. The arrays must be of length M, where M is the power of
     * two specified by this FFT's power_of_two
     *
     * @param reals - the array representing the real values of the complex
     *              sequence
     * @param imags - the array representing the imaginary values of the
     *              complex sequence
     */
    public void cfft(float[] reals, float[] imags) {
        int i, id, is, its, i1, j, j0, k, l, m, m2, n, n2, n4, nbit;
        float t1;

        n = table.FFTSIZE;						/* fft size */
        m = table.LOGSIZE;						/* log2 (fft size) */

        /****************************************************************************
         * l-shaped butterflies                                                     *
         ****************************************************************************/
        its = 1;
        n2 = n << 1;				/* n2 = n*2 */
        for (k = 1; k <= (m - 1); k++) {
            n2 = n2 >> 1;			/* n2 = n2/2 etc. */
            n4 = n2 >> 2;
            cfft_stage(n, n2, n4, its, reals, imags);
            its = its << 1;
        }

        /****************************************************************************
         * length-two butterflies                                                   *
         ****************************************************************************/
        is = 1;
        id = 4;
        while (is < n) {
            for (i1 = is; i1 <= n; i1 += id) {
                t1 = reals[i1 - 1];
                reals[i1 - 1] = t1 + reals[i1];
                reals[i1] = t1 - reals[i1];
                t1 = imags[i1 - 1];
                imags[i1 - 1] = t1 + imags[i1];
                imags[i1] = t1 - imags[i1];
            }
            is = (id << 1) - 1;
            id <<= 2;
        }

        /****************************************************************************
         * bit-reverse counter                                                      *
         ****************************************************************************/
        m2 = m >> 1;
        nbit = 1 << m2;
        for (k = 1; k < nbit; k++) {
            j0 = nbit * table.BITREV[k];
            i = k;
            j = j0;
            for (l = 1; l <= table.BITREV[k]; l++) {
                t1 = reals[i];
                reals[i] = reals[j];
                reals[j] = t1;
                t1 = imags[i];
                imags[i] = imags[j];
                imags[j] = t1;
                i += nbit;
                j = j0 + table.BITREV[l];
            }
        }
    }

    /**
     * Takes the inverse fft of a complex sequence of numbers represented as
     * two arrays. The arrays must be of length M, where M is the power of
     * two specified by this FFT's power_of_two
     *
     * @param reals - the array representing the real values of the complex
     *              sequence
     * @param imags - the array representing the imaginary values of the
     *              complex sequence
     */
    public void cifft(float[] reals, float[] imags) {
        int i, j, fftsz;
        float scale, t1;

        /* calculate fwd fft */
        cfft(reals, imags);

        /* extract fftsz from table */
        fftsz = table.FFTSIZE;			/* fft size */

        /* reverse and scale data */
        scale = 1.0f / (fftsz);
        reals[0] *= scale;			/* z[DC] doesn't move  */
        imags[0] *= scale;
        reals[fftsz / 2] *= scale;			/* nor does z[Nyquist] */
        imags[fftsz / 2] *= scale;
        for (i = 1, j = fftsz - 1; i < fftsz / 2; i += 1, j -= 1) {
            t1 = reals[i];
            reals[i] = reals[j] * scale;
            reals[j] = t1 * scale;
            t1 = imags[i];
            imags[i] = imags[j] * scale;
            imags[j] = t1 * scale;
        }
    }

    /******************************************************************************
     *   void cfft_stage: computes one stage of a length n split-radix transform  *
     *                                                                            *
     *      M. R. Portnoff,   Lawrence Livermore Nat'l. Lab Jun. 1991             *
     *      based on fortran code by Henrik Sorensen, Rice University,  Jul. 1987 *
     ******************************************************************************/

    /**
     * Computes one stage of a length n split radix transform.
     *
     * @param n     - the length of the complex sequence
     * @param n2    - internal value needed for a stage
     * @param n4    - n2 / 2.
     * @param its   - internal value needed for a stage
     * @param reals - the array of doubles representing the real parts of the
     *              complex sequence
     * @param imags - the array of doubles representing the imaginary parts of
     *              the complex sequence
     */
    private void cfft_stage(int n, int n2, int n4, int its, float[] reals,
                            float[] imags) {
        int i, i1, it, j, jn, x;
        float t1, t2, t3, t4, t5;
        double w1r, w1i, w3r, w3i;
        int z2 = n4;
        int z3 = 2 * n4;
        int z4 = 3 * n4;
        int id = n2 << 1;
        int is = 0;
        int n8 = n4 >> 1;

        /****************************************************************************
         * zero butterfly                                                           *
         ****************************************************************************/
        while (is < n) {
            for (i1 = is; i1 < n; i1 += id) {
                t1 = reals[i1] - reals[i1 + z3];
                reals[i1] += reals[i1 + z3];
                t2 = imags[i1 + z2] - imags[i1 + z4];
                imags[i1 + z2] += imags[i1 + z4];
                reals[i1 + z3] = t1 + t2;
                t2 = t1 - t2;
                t1 = reals[i1 + z2] - reals[i1 + z4];
                reals[i1 + z2] += reals[i1 + z4];
                reals[i1 + z4] = t2;
                t2 = imags[i1] - imags[i1 + z3];
                imags[i1] += imags[i1 + z3];
                imags[i1 + z3] = t2 - t1;
                imags[i1 + z4] = t2 + t1;
            }
            is = (id << 1) - n2;
            id <<= 2;
        }

        if (n4 <= 1) return;

        /****************************************************************************
         * n/8 butterfly                                                            *
         ****************************************************************************/
        is = 0;
        id = n2 << 1;
        while (is < (n - 1)) {
            for (i1 = is + n8; i1 < n; i1 += id) {
                t1 = reals[i1] - reals[i1 + z3];
                reals[i1] += reals[i1 + z3];
                t2 = reals[i1 + z2] - reals[i1 + z4];
                reals[i1 + z2] += reals[i1 + z4];
                t3 = imags[i1] - imags[i1 + z3];
                imags[i1] += imags[i1 + z3];
                t4 = imags[i1 + z2] - imags[i1 + z4];
                imags[i1 + z2] += imags[i1 + z4];
                t5 = (float) ((t4 - t1) * SQRT2OV2);
                t1 = (float) ((t4 + t1) * SQRT2OV2);
                t4 = (float) ((t3 - t2) * SQRT2OV2);
                t2 = (float) ((t3 + t2) * SQRT2OV2);
                reals[i1 + z3] = t4 + t1;
                imags[i1 + z3] = t4 - t1;
                reals[i1 + z4] = t5 + t2;
                imags[i1 + z4] = t5 - t2;
            }
            is = (id << 1) - n2;
            id <<= 2;
        }
        if (n8 <= 1) return;

        /****************************************************************************
         * general butterfly -- two at a time                                       *
         ****************************************************************************/
        is = 1;
        its <<= 1;
        id = n2 << 1;
        while (is < n) {
            for (i = is; i <= n; i += id) {
                it = -2;
                jn = i + n4;
                for (j = 1; j <= (n8 - 1); j++) {
                    it += its;
                    w1r = table.WNr[radix * (it / 2 + 1)];
                    w3r = table.WNr[radix * (it / 2 + 1) * 3];
                    w1i = -table.WNi[radix * (it / 2 + 1)];
                    w3i = -table.WNi[radix * (it / 2 + 1) * 3];
                    x = i + j - 1;
                    t1 = reals[x] - reals[x + z3];
                    reals[x] += reals[x + z3];
                    t2 = reals[x + z2] - reals[x + z4];
                    reals[x + z2] += reals[x + z4];
                    t3 = imags[x] - imags[x + z3];
                    imags[x] += imags[x + z3];
                    t4 = imags[x + z2] - imags[x + z4];
                    imags[x + z2] += imags[x + z4];
                    t5 = t1 - t4;
                    t1 += t4;
                    t4 = t2 - t3;
                    t2 += t3;
                    reals[x + z3] = (float) (t1 * w1r - t4 * w1i);
                    imags[x + z3] = (float) (-t4 * w1r - t1 * w1i);
                    reals[x + z4] = (float) (t5 * w3r + t2 * w3i);
                    imags[x + z4] = (float) (t2 * w3r - t5 * w3i);

                    x = jn - j - 1;
                    t1 = reals[x] - reals[x + z3];
                    reals[x] += reals[x + z3];
                    t2 = reals[x + z2] - reals[x + z4];
                    reals[x + z2] += reals[x + z4];
                    t3 = imags[x] - imags[x + z3];
                    imags[x] += imags[x + z3];
                    t4 = imags[x + z2] - imags[x + z4];
                    imags[x + z2] += imags[x + z4];
                    t5 = t1 - t4;
                    t1 += t4;
                    t4 = t2 - t3;
                    t2 += t3;
                    reals[x + z3] = (float) (t1 * w1i - t4 * w1r);
                    imags[x + z3] = (float) (-t4 * w1i - t1 * w1r);
                    reals[x + z4] = (float) (-t5 * w3i - t2 * w3r);
                    imags[x + z4] = (float) (-t2 * w3i + t5 * w3r);
                }
            }
            is = (id << 1) - n2 + 1;
            id <<= 2;
        }
    }

    /******************************************************************************
     *  int cfft_powerof2:                                                        *
     *      p = cfft_powerof2(n)     returns the smallest non-negative integer p  *
     *                               such that n <= 2^p                           *
     *  author:                                                                   *
     *      michael portnoff,        LLNL,             aug. 1992                  *
     ******************************************************************************/

    /**
     * Finds the int value of the log base 2 of n.
     *
     * @param n - the number to take the log base 2 of
     * @return the log base 2 of n
     */
    public static int cfft_powerof2(int n) {
        int p = 0;
        while (n > (1 << p))
            p++;
        return p;
    }

    /**
     * Takes the complex fft of two arrays representing the real and complex
     * parts of a sequence. The arrays must be of length M*r, where M is the
     * power of two specified by this ComplexFFT and r is the arbitrary radix
     * factor specified by this ComplexFFT.
     *
     * @param x - the array of doubles representing the real parts of the
     *          complex sequence
     * @param y - the array of doubles representing the real parts of the
     *          complex sequence
     */
    public void cfftRX(float[] x, float[] y) {
        int R = radix;
        int m = power_of_two;
        int M = 1 << m;

        // degenerate case
        if (R == 1) {
            cfft(x, y);
            return;
        }

        // radix-R extender for real-valued, forward, power of 2 FFT
        // Computes X[k] = sum(n = 0 : N-1) x[n] e^(-j*2*pi*k*n/N);  k = 0, ..., N-1
        //  where N = M*R
        //  and   M = 2^m, R is odd and small, e.g. 3, 5, 7
        //  decimates x[n] into R subsequences
        //    n = R*q + r   q = 0, ..., M-1; r = 0, ..., R-1
        //    xr[q] = x[R*q + r]
        //    constructs length N DFT from R length M DFTs:
        //    Xr[i] = sum(q = 0 : M-1) xr[q] e^(-j*2*pi/M)
        //  evaluates X[k] in bands,
        //    k = i + p*M   i = 0, ..., M-1;   p = 0, ..., R-1

        //  X[i+p*M] = sum(r = 0 : R-1) WR^(p*r) * Xr[i] * WN^(i*r)

        //  WR = e^(-j*2*pi/R)      WN = e^(-j*2*pi/N)

        int n;
        float[] xrptr, xiptr;

        // decimate sequence and compute length-M DFTs
        for (int r = 0; r < R; r++) {
            xrptr = buffers.get(2 * r);
            xiptr = buffers.get(2 * r + 1);
            n = r;

            for (int q = 0; q < M; q++) {
                xrptr[q] = x[n];
                xiptr[q] = y[n];
                n += R;
            }

            cfft(xrptr, xiptr);
        }

        // compute partial result    Xr[i] * WN^(i*r)
        //   for
        //   X[i+p*M] = sum(r = 0 : R-1) WR^(p*r) * Xr[i] * WN^(i*r)

        double tmp;
        float[] tmpr = new float[R];
        float[] tmpi = new float[R];
        int k;
        float Yr, Yi;


        for (int i = 0; i < M; i++) {
            // extract Xr[i] for each r
            for (int r = 0; r < R; r++) {
                xrptr = buffers.get(2 * r);
                xiptr = buffers.get(2 * r + 1);
                tmpr[r] = xrptr[i];
                tmpi[r] = xiptr[i];
            }

            for (int r = 1; r < R; r++) {
                tmp = table.WNr[i * r] * tmpr[r] - table.WNi[i * r] * tmpi[r];
                tmpi[r] = (float) (table.WNr[i * r] * tmpi[r] + table.WNi[i * r] * tmpr[r]);
                tmpr[r] = (float) tmp;
            }


            //  compute X[i+p*M] = sum(r = 0 : R-1) WR^(p*r) * { Xr[i] * WN^(i*r) }
            //  for p = 0, ..., R-1
            //    special case p = 0
            Yr = 0.0f;
            Yi = 0.0f;

            for (int r = 0; r < R; r++) {
                Yr += tmpr[r];
                Yi += tmpi[r];
            }

            if (i == 0)
                x[i] = Yr;
            else {
                x[i] = Yr;
                y[i] = Yi;
            }

            //    all other cases

            int pr;
            k = i + M;

            for (int p = 1; p < R; p++) {
                Yr = tmpr[0];
                Yi = tmpi[0];
                pr = p;

                int r;
                for (r = 1; r < R; r++) {
                    Yr += (float) (table.WRr[pr] * tmpr[r] - table.WRi[pr] * tmpi[r]);
                    Yi += (float) (table.WRr[pr] * tmpi[r] + table.WRi[pr] * tmpr[r]);
                    pr += p;
                }

                x[k] = Yr;
                y[k] = Yi;
                k += M;
            }
        }
    }

    /**
     * A CFFT_Table holds the cos, sin, and bit reversal tables necessary for
     * the fft and inverse ffts. The cos and sin tables use a coupled-form
     * oscillator to calculate their values.<p>
     * <p></p>
     * The class is based upon Michael Portnoff's c code written in August 1992.
     * Michael Portnoff based his code off of fortran code written by Henrik
     * Sorensen in July 1987.
     *
     * @author Timothy Paik
     */
    private class CFFT_Table {
        final int FFTSIZE;
        final int LOGSIZE;
        final double[] WRr;
        final double[] WRi;
        final double[] WNr;
        final double[] WNi;
        final int[] BITREV;

        /**
         * Constructs a new CFFT_Table of length n.
         *
         * @param n - the length of the cos and sin tables in this CFFT_Table
         */
        CFFT_Table(int n) {
            int n2pow;
            int i, j, imax, lbss, n2, itabsz, nw;
            double ang;

            n2pow = cfft_powerof2(n);

            /* allocate table structure */
            FFTSIZE = n;
            LOGSIZE = n2pow;

            /****************************************************************************
             * sin / cos table                                                          *
             ****************************************************************************/

            // precompute table of powers of WR using coupled-form oscillator
            nw = (radix - 1) * (radix - 1) + 1;
            WRr = new double[nw];
            WRi = new double[nw];

            WRr[0] = 1.0;
            WRi[0] = 0.0;

            ang = 2 * Math.PI / radix;
            double x3 = Math.cos(ang);
            double y3 = -Math.sin(ang);

            for (j = 1; j < nw; j++) {
                WRr[j] = x3 * WRr[j - 1] - y3 * WRi[j - 1];
                WRi[j] = x3 * WRi[j - 1] + y3 * WRr[j - 1];
            }

            // precompute table of powers of WN using coupled-form oscillator
            nw = n * radix;
            WNr = new double[nw];
            WNi = new double[nw];

            WNr[0] = 1.0;
            WNi[0] = 0.0;

            ang = 2 * Math.PI / nw;
            x3 = Math.cos(ang);
            y3 = -Math.sin(ang);

            for (j = 1; j < nw; j++) {
                WNr[j] = x3 * WNr[j - 1] - y3 * WNi[j - 1];
                WNi[j] = x3 * WNi[j - 1] + y3 * WNr[j - 1];
            }

            /****************************************************************************
             * bit-reversal table                                                       *
             ****************************************************************************/

            itabsz = (int) (Math.sqrt((float) 2 * n)) + 1;
            BITREV = new int[itabsz];

            n2 = n2pow >> 1;
            if ((n2 << 1) != n2pow)
                n2++;
            BITREV[0] = 0;
            BITREV[1] = 1;
            imax = 1;
            for (lbss = 2; lbss <= n2; lbss++) {
                imax *= 2;
                for (i = 0; i < imax; i++) {
                    BITREV[i] *= 2;
                    BITREV[i + imax] = 1 + BITREV[i];
                }
            }
        }
    }
}
