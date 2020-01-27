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


import java.util.Vector;

import org.apache.commons.math3.complex.Complex;

public class FFT {

    //  workspace for the radix-R extenders rvfftRX and irvfftRX

    private Vector<float[]> xr;

    private int m;

    private int R;

    private int M; // = 2^m

    // constructors

    public FFT(int _m, int _R) {

        m = _m;

        R = _R;

        M = 1;

        for (int i = 0; i < m; i++)
            M *= 2;

        if (R > 1) {

            xr = new Vector<float[]>(R);

            for (int r = 0; r < R; r++) {

                xr.addElement(new float[M]);


            }

        } else
            xr = null;

    }

    public int fftsize() {

        return R * M;

    }

    public static Complex[] realFFT(float[] realArray) {

        if (realArray == null)
            return null;

        int N = realArray.length;

        if (N < 1)
            return new Complex[0];

        // Pad input to power of two if necessary...

        int n = 1;

        int order = 0;

        while (n < N) {

            n *= 2;

            ++order;

        }

        if (n > N) {

            float[] tmp = realArray;

            realArray = new float[n];

            for (int j = 0; j < N; ++j)
                realArray[j] = tmp[j];

            for (int j = N; j < n; ++j)
                realArray[j] = 0.0F;

        }

        // Do the forward transform on the (possibly padded) real Array

        rvfft(realArray, order);

        int M = n / 2 + 1;

        Complex[] result = new Complex[M];

        result[0] = new Complex(realArray[0], 0.0);

        for (int j = 1; j < M - 1; ++j)
            result[j] = new Complex(realArray[j], realArray[n - j]);

        result[M - 1] = new Complex(realArray[n / 2], 0.0);

        return result;

    }

    /* CC=============================================================CC
       CC                                                             CC
       CC      A real-valued, in-place, split-radix FFT program       CC
       CC      Real input and output data in arrays X                 CC
       CC      Length is N=2**m                                       CC
       CC      Decimation-in-time, cos/sin in second loop             CC
       CC      and computed recursively                               CC
       CC      Output in order:                                       CC
       CC              [ Re(0),Re(1),....,Re(N/2),Im(N/2-1),...Im(1)] CC
       CC                                                             CC
       CC      H.V. Sorensen,    Rice University,    Oct. 1985        CC
       CC                        Arpa address: hvs@rice.edu           CC
       CC  Modified:                                                  CC
       CC      F. Bonzanigo,     ETH-Zurich,         Sep. 1986        CC
       CC      H.V. Sorensen,    Rice University     Mar. 1987        CC
       CC                                                             CC
       CC=============================================================CC   */

    //  C++ / ANSI C conversion by D. Harris  5/20/95

    //  C++ to Java conversion by D. Harris   5/14/99

    public static void rvfft(float[] x, int _m) {

        int n = 1;

        for (int i = 0; i < _m; i++)
            n *= 2;

        //-------Digit reverse counter-----------------------------------

        int j = 1;

        int k;

        float t;

        for (int i = 1; i < n; i++) {

            if (i < j) {

                t = x[j - 1];

                x[j - 1] = x[i - 1];

                x[i - 1] = t;

            }

            k = n / 2;

            while (k < j) {

                j -= k;

                k = k / 2;

            }

            j += k;

        }

        //-----Length Eo butterflies------------------------------------

        int is = 1;

        int id = 4;

        int i0;

        do {

            for (i0 = is; i0 <= n; i0 += id) {

                t = x[i0 - 1];

                x[i0 - 1] = t + x[i0];

                x[i0] = t - x[i0];

            }

            is = 2 * id - 1;

            id *= 4;

        } while (is < n);

        //-------L shaped butterflies------------------------------------

        int n2 = 2;

        int n4;

        for (k = 2; k <= _m; k++) {

            n2 *= 2;

            n4 = n2 / 4;

            stage(n, n2, n4, x, n4, 2 * n4, 3 * n4);

        }

    }

    private static void stage(int N,
                              int N2,
                              int N4,
                              float[] X,
                              int OFF2,
                              int OFF3,
                              int OFF4) {

        /*C===============================================================C
          C     Function STAGE - the work-horse of the FFT                C
          C===============================================================C  */

        int N8 = N2 / 8;

        int IS = 0;

        int ID = N2 * 2;

        int I1, I2, I, J, JN;

        float T1, T2, T3, T4, T5, T6;

        double E;

        double SW1I, SS3, SD1, SD3, CW1R, CC3, CD1, CD3;

        double DT1, DT3;

        do {

            for (I1 = IS; I1 < N; I1 += ID) {

                T1 = X[OFF4 + I1] + X[OFF3 + I1];

                X[OFF4 + I1] = X[OFF4 + I1] - X[OFF3 + I1];

                X[OFF3 + I1] = X[I1] - T1;

                X[I1] = X[I1] + T1;

            }

            IS = 2 * ID - N2;

            ID = 4 * ID;

        } while (IS < N);

        if (N4 > 1) {

            IS = 0;

            ID = N2 * 2;

            do {

                for (I2 = IS + N8; I2 < N; I2 += ID) {

                    T1 =
                            (float) ((X[OFF3 + I2] + X[OFF4 + I2])
                            * .7071067811865475);

                    T2 =
                            (float) ((X[OFF3 + I2] - X[OFF4 + I2])
                            * .7071067811865475);

                    X[OFF4 + I2] = X[OFF2 + I2] - T1;

                    X[OFF3 + I2] = -X[OFF2 + I2] - T1;

                    X[OFF2 + I2] = X[I2] - T2;

                    X[I2] = X[I2] + T2;

                }

                IS = 2 * ID - N2;

                ID = 4 * ID;

            } while (IS < N);

            if (N8 > 1) {

                E = 2. * 3.14159265358979323 / N2;

                SW1I = Math.sin(E);

                SD1 = SW1I;

                SD3 = (3. - 4. * SD1 * SD1) * SD1;

                SS3 = SD3;

                CW1R = Math.cos(E);

                CD1 = CW1R;

                CD3 = (4. * CD1 * CD1 - 3.) * CD1;

                CC3 = CD3;

                for (J = 2; J <= N8; J++) {

                    IS = 0;

                    ID = 2 * N2;

                    JN = N4 - 2 * J + 2;

                    do {

                        for (I = IS + J; I <= N; I += ID) {

                            I1 = I - 1;

                            I2 = I1 + JN;

                            T1 =
                                    (float) (X[OFF3
                                    + I1] * CW1R
                                    + X[OFF3
                                    + I2] * SW1I);

                            T2 =
                                    (float) (X[OFF3
                                    + I2] * CW1R
                                    - X[OFF3
                                    + I1] * SW1I);

                            T3 =
                                    (float) (X[OFF4
                                    + I1] * CC3
                                    + X[OFF4
                                    + I2] * SS3);

                            T4 =
                                    (float) (X[OFF4
                                    + I2] * CC3
                                    - X[OFF4
                                    + I1] * SS3);

                            T5 = T1 + T3;

                            T6 = T2 + T4;

                            T3 = T1 - T3;

                            T4 = T2 - T4;

                            T2 = T6 + X[OFF2 + I2];

                            X[OFF3 + I1] = T6 - X[OFF2 + I2];

                            X[OFF4 + I2] = T2;

                            T2 = X[OFF2 + I1] - T3;

                            X[OFF3 + I2] = -X[OFF2 + I1] - T3;

                            X[OFF4 + I1] = T2;

                            T1 = X[I1] + T5;

                            X[OFF2 + I2] = X[I1] - T5;

                            X[I1] = T1;

                            T1 = X[I2] + T4;

                            X[I2] = X[I2] - T4;

                            X[OFF2 + I1] = T1;

                        }

                        IS = 2 * ID - N2;

                        ID = 4 * ID;

                    } while (IS < N);

                    DT1 = CW1R * CD1 - SW1I * SD1;

                    SW1I = CW1R * SD1 + SW1I * CD1;

                    CW1R = DT1;

                    DT3 = CC3 * CD3 - SS3 * SD3;

                    SS3 = CC3 * SD3 + SS3 * CD3;

                    CC3 = DT3;

                }

            }

        }

    }

    /*CC===============================================================CC
    CC                                                                 CC
    CC	A real-valued, in-place, split-radix IFFT program	           CC
    CC	Symmetric input and real output data in arrays X               CC
    CC	Length is N=2**m				                               CC
    CC	Decimation-in-frequency, cos/sin in second loop		           CC
    CC	and is computed recursively                                    CC
    CC	Input order :						                           CC
    CC		[ Re(0),Re(1),....,Re(N/2),Im(N/2-1),...Im(1)]	           CC
    CC								                                   CC
    CC	H.V. Sorensen,    Rice University,    Nov. 1985		           CC
    CC                        Email address: hvs@rice.edu              CC
    CC  Modified:                                                      CC
    CC      F. Bonzanigo,     ETH-Zurich,         Sep. 1986            CC
    CC      H.V. Sorensen,    Rice University     Mar. 1987            CC
    CC								                                   CC
    CC=================================================================CC */

    //  Conversion to C++ / ANSI C by  D. Harris  5/20/95

    //  Conversion from C++ to Java by D. Harris  5/14/99

    public static void irvfft(float[] x, int _m) {

        int j = 1;

        int k;

        float t;

        int n = 1;

        for (int i = 0; i < _m; i++)
            n *= 2;

        //-------L shaped butterflies-------------------------------------

        int n2 = 2 * n;

        int n4;

        for (k = 1; k < _m; k++) {

            n2 /= 2;

            n4 = n2 / 4;

            istage(n, n2, n4, x, n4, 2 * n4, 3 * n4);

        }

        //-----Length Eo butterflies-----------------------------------

        int is = 1;

        int id = 4;

        int i1;

        do {

            for (i1 = is; i1 <= n; i1 += id) {

                t = x[i1 - 1];

                x[i1 - 1] = t + x[i1];

                x[i1] = t - x[i1];

            }

            is = 2 * id - 1;

            id *= 4;

        } while (is < n);

        //-------Digit reverse counter-----------------------------------

        for (int i = 1; i < n; i++) {

            if (i < j) {

                t = x[j - 1];

                x[j - 1] = x[i - 1];

                x[i - 1] = t;

            }

            k = n / 2;

            while (k < j) {

                j -= k;

                k = k / 2;

            }

            j += k;

        }

        //-------Divide by N---------------------------------------------

        for (int i = 0; i < n; i++)
            x[i] /= n;

    }

    private static void istage(int N,
                               int N2,
                               int N4,
                               float[] X,
                               int OFF2,
                               int OFF3,
                               int OFF4) {

        /*C===============================================================C
          C     function istage - the work-horse of the IFFT              C
          C===============================================================C */

        int N8 = N4 / 2;

        int IS = 0;

        int ID = 2 * N2;

        int I1, I2, I, J, JN;

        float T1, T2, T3, T4, T5;

        double E;

        double SW1I, SS3, SD1, SD3, CW1R, CC3, CD1, CD3;

        double DT1, DT3;

        do {

            for (I1 = IS; I1 < N; I1 += ID) {

                T1 = X[I1] - X[OFF3 + I1];

                X[I1] = X[I1] + X[OFF3 + I1];

                X[OFF2 + I1] = 2 * X[OFF2 + I1];

                T2 = 2 * X[OFF4 + I1];

                X[OFF4 + I1] = T1 + T2;

                X[OFF3 + I1] = T1 - T2;

            }

            IS = 2 * ID - N2;

            ID = 4 * ID;

        } while (IS < N);

        if (N4 > 1) {

            IS = 0;

            ID = 2 * N2;

            do {

                for (I1 = IS + N8; I1 < N; I1 += ID) {

                    T1 =
                            (float) ((X[OFF2 + I1] - X[I1])
                            * 1.4142135623730950488);

                    T2 =
                            (float) ((X[OFF4 + I1] + X[OFF3 + I1])
                            * 1.4142135623730950488);

                    X[I1] = X[I1] + X[OFF2 + I1];

                    X[OFF2 + I1] = X[OFF4 + I1] - X[OFF3 + I1];

                    X[OFF3 + I1] = -T2 - T1;

                    X[OFF4 + I1] = -T2 + T1;

                }

                IS = 2 * ID - N2;

                ID = 4 * ID;

            } while (IS < N - 1);

            if (N8 > 1) {

                E = 6.283185307179586 / N2;

                SW1I = Math.sin(E);

                SD1 = SW1I;

                SD3 = (3.0 - 4.0 * SD1 * SD1) * SD1;

                SS3 = SD3;

                CW1R = Math.cos(E);

                CD1 = CW1R;

                CD3 = (4.0 * CD1 * CD1 - 3.0) * CD1;

                CC3 = CD3;

                for (J = 2; J <= N8; J++) {

                    IS = 0;

                    ID = 2 * N2;

                    JN = N4 - 2 * J + 2;

                    do {

                        for (I = IS + J; I <= N; I += ID) {

                            I1 = I - 1;

                            I2 = I1 + JN;

                            T1 = X[I1] - X[OFF2 + I2];

                            X[I1] = X[I1] + X[OFF2 + I2];

                            T2 = X[I2] - X[OFF2 + I1];

                            X[I2] = X[OFF2 + I1] + X[I2];

                            T3 = X[OFF4 + I2] + X[OFF3 + I1];

                            X[OFF2 + I2] = X[OFF4 + I2] - X[OFF3 + I1];

                            T4 = X[OFF4 + I1] + X[OFF3 + I2];

                            X[OFF2 + I1] = X[OFF4 + I1] - X[OFF3 + I2];

                            T5 = T1 - T4;

                            T1 = T1 + T4;

                            T4 = T2 - T3;

                            T2 = T2 + T3;

                            X[OFF3 + I1] = (float) (T5 * CW1R + T4 * SW1I);

                            X[OFF3 + I2] = (float) (-T4 * CW1R + T5 * SW1I);

                            X[OFF4 + I1] = (float) (T1 * CC3 - T2 * SS3);

                            X[OFF4 + I2] = (float) (T2 * CC3 + T1 * SS3);

                        }

                        IS = 2 * ID - N2;

                        ID = 4 * ID;

                    } while (IS < N);

                    DT1 = CW1R * CD1 - SW1I * SD1;

                    SW1I = CW1R * SD1 + SW1I * CD1;

                    CW1R = DT1;

                    DT3 = CC3 * CD3 - SS3 * SD3;

                    SS3 = CC3 * SD3 + SS3 * CD3;

                    CC3 = DT3;

                }

            }

        }

    }

    public void rvfftRX(float[] x) {
        // degenerate case
        if (R == 1) {
            rvfft(x, m);
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

        double twoPI = 2.0 * Math.PI;
        int half_M = M / 2;
        int N = M * R;
        int half_N = N / 2;
        int n;
        float[] xrptr;

        // decimate sequence and compute length-M DFTs
        for (int r = 0; r < R; r++) {
            xrptr = xr.elementAt(r);
            n = r;

            for (int q = 0; q < M; q++) {
                xrptr[q] = x[n];
                n += R;
            }

            rvfft(xrptr, m);
        }

        double dWr, dWi;
        double ddWr, ddWi;

        // precompute table of powers of WR using coupled-form oscillator
        int nw = (R - 1) * (R - 1) + 1;
        double[] WRr = new double[nw];
        double[] WRi = new double[nw];
        double arg;

        WRr[0] = 1.0;
        WRi[0] = 0.0;

        arg = twoPI / R;
        dWr = Math.cos(arg);
        dWi = -Math.sin(arg);

        for (int j = 1; j < nw; j++) {
            WRr[j] = dWr * WRr[j - 1] - dWi * WRi[j - 1];
            WRi[j] = dWr * WRi[j - 1] + dWi * WRr[j - 1];
        }

        // compute partial result    Xr[i] * WN^(i*r)
        //   for
        //   X[i+p*M] = sum(r = 0 : R-1) WR^(p*r) * Xr[i] * WN^(i*r)

        double tmp;
        float[] tmpr = new float[R];
        float[] tmpi = new float[R];
        int k;
        double WNr, WNi;
        float Yr, Yi;

        arg = twoPI / N;
        ddWr = Math.cos(arg);
        ddWi = -Math.sin(arg);

        dWr = 1.0;
        dWi = 0.0;

        for (int i = 0; i < M; i++) {
            // extract Xr[i] for each r
            if (i > 0 && i < half_M) {
                for (int r = 0; r < R; r++) {
                    xrptr = xr.elementAt(r);
                    tmpr[r] = xrptr[i];
                    tmpi[r] = xrptr[M - i];
                }
            } else if (i > half_M) {
                for (int r = 0; r < R; r++) {
                    xrptr = xr.elementAt(r);
                    tmpr[r] = xrptr[M - i];
                    tmpi[r] = -xrptr[i];
                }
            } else {
                for (int r = 0; r < R; r++) {
                    xrptr = xr.elementAt(r);
                    tmpr[r] = xrptr[i];
                    tmpi[r] = 0.0f;
                }

            }

            // scale XR[i] by (WN^i)^r
            WNr = dWr;
            WNi = dWi;

            for (int r = 1; r < R; r++) {
                tmp = WNr * tmpr[r] - WNi * tmpi[r];
                tmpi[r] = (float) (WNr * tmpi[r] + WNi * tmpr[r]);
                tmpr[r] = (float) tmp;

                tmp = WNr * dWr - WNi * dWi;
                WNi = WNr * dWi + WNi * dWr;
                WNr = tmp;
            }

            tmp = dWr * ddWr - dWi * ddWi;
            dWi = dWr * ddWi + dWi * ddWr;
            dWr = tmp;

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
                x[N - i] = Yi;
            }

            //    all other cases
            //      note that for real x[n], X[k] need be computed only through
            //      k = N/2 due to conjugate symmetry

            //      example:  N = 12 = 3 * 2^2;   R = 3;  M = 4
            //      k  0  1  2  3  4  5  6  7  8  9 10 11
            //      p  0  0  0  0  1  1  1  1  2  2  2  2
            //      i  0  1  2  3  0  1  2  3  0  1  2  3

            //      samples 7 through 11 obtained by symmetry
            //      need compute samples only through p = (R-1)/2

            int pmax = (R - 1) / 2;
            int pr;
            k = i + M;

            for (int p = 1; p < pmax; p++) {
                Yr = 0.0f;
                Yi = 0.0f;
                pr = 0;

                for (int r = 0; r < R; r++) {
                    Yr += (float) (WRr[pr] * tmpr[r] - WRi[pr] * tmpi[r]);
                    Yi += (float) (WRr[pr] * tmpi[r] + WRi[pr] * tmpr[r]);
                    pr += p;
                }

                x[k] = Yr;
                x[N - k] = Yi;
                k += M;
            }

            //      special case p = (R-1)/2
            k = i + pmax * M;

            if (k <= half_N) {
                Yr = 0.0f;
                pr = 0;

                for (int r = 0; r < R; r++) {
                    Yr += (float) (WRr[pr] * tmpr[r] - WRi[pr] * tmpi[r]);
                    pr += pmax;
                }

                x[k] = Yr;

                if (k < half_N) {
                    Yi = 0.0f;
                    pr = 0;

                    for (int r = 0; r < R; r++) {
                        Yi += (float) (WRr[pr] * tmpi[r] + WRi[pr] * tmpr[r]);
                        pr += pmax;
                    }

                    x[N - k] = Yi;
                }
            }
        }
    }


    public void irvfftRX(float[] x) {

        // degenerate case

        if (R == 1) {

            irvfft(x, m);

            return;

        }

        // R-factor extender for power of two, real-valued inverse FFT

        // Assumes  N = R*M

        //  where M = 2^m and R is odd and small, e.g. 3, 5, 7

        // computes x[n] = 1/N sum(k = 0 : N-1) X[k] e^(j*2*pi*k*n / N)

        // makes substitutions:

        //   n = R*q + r       q = 0, ..., M-1     r = 0, ..., R-1

        //   k = i + p*M       i = 0, ..., M-1     p = 0, ..., R-1

        // to obtain:

        // x_r[q] = x[ q*R + r ]  =  1/M sum(i = 0 : M-1) X_r[i] e^(j*2*pi*i*q / M)

        //   where:

        // X_r[i] = ( 1/R sum(p = 0 : R-1) X[i+p*M] WR^(p*r) ) WN^(i*r)

        //  and  WR = e^(2*pi/R)  and  WN = e^(2*pi/N)

        int N = M * R;

        int half_N = N / 2;

        // special case r = 0

        float Yr, Yi;

        float[] xrptr = xr.elementAt(0);

        int k;

        for (int i = 0; i <= M / 2; i++) {

            Yr = 0.0f;

            Yi = 0.0f;

            k = i;

            for (int p = 0; p < R; p++) {

                if (k <= half_N) {

                    Yr += x[k];

                    if (k > 0 && k < half_N)
                        Yi += x[N - k];

                } else {

                    Yr += x[N - k];

                    Yi -= x[k];

                }

                k += M;

            }

            xrptr[i] = Yr / R;

            if (i != 0 && i != M / 2)
                xrptr[M - i] = Yi / R;

        }

        irvfft(xrptr, m);

        // remaining cases:  r = 1, ..., R-1

        // X_r[i] = ( 1/R sum(p=0:R-1) X[i+p*M] WR^(p*r) )  WN^(i*r)

        double WNr, WNi;

        double dWNr, dWNi;

        double tmpr, tmpi;

        double[] WRr = new double[R];

        double[] WRi = new double[R];

        double dWRr, dWRi;

        WRr[0] = 1.0;

        WRi[0] = 0.0;

        double twoPI = 2.0 * Math.PI;

        for (int r = 1; r < R; r++) {

            xrptr = xr.elementAt(r);

            // precompute table of (WR^r)^p with coupled form oscillator

            dWRr = Math.cos((twoPI * r) / R);

            dWRi = Math.sin((twoPI * r) / R);

            for (int p = 1; p < R; p++) {

                WRr[p] = dWRr * WRr[p - 1] - dWRi * WRi[p - 1];

                WRi[p] = dWRr * WRi[p - 1] + dWRi * WRr[p - 1];

            }

            dWNr = Math.cos(twoPI * r / N);

            dWNi = Math.sin(twoPI * r / N);

            WNr = 1.0 / (R);

            WNi = 0.0;

            for (int i = 0; i <= M / 2; i++) {

                Yr = 0.0f;

                Yi = 0.0f;

                k = i;

                for (int p = 0; p < R; p++) {

                    if (k > 0 && k < half_N) {

                        tmpr = x[k];

                        tmpi = x[N - k];

                    } else if (k > half_N) {

                        tmpr = x[N - k];

                        tmpi = -x[k];

                    } else {

                        tmpr = x[k];

                        tmpi = 0.0;

                    }

                    Yr += WRr[p] * tmpr - WRi[p] * tmpi;

                    Yi += WRr[p] * tmpi + WRi[p] * tmpr;

                    k += M;

                }

                xrptr[i] = (float) (WNr * Yr - WNi * Yi);

                if (i != 0 && i != M / 2)
                    xrptr[M - i] = (float) (WNr * Yi + WNi * Yr);

                tmpr = dWNr * WNr - dWNi * WNi;

                WNi = dWNr * WNi + dWNi * WNr;

                WNr = tmpr;

            }

            irvfft(xrptr, m);

        }

        // interleave resulting signals

        int n;

        for (int r = 0; r < R; r++) {

            xrptr = xr.elementAt(r);

            n = r;

            for (int q = 0; q < M; q++) {

                x[n] = xrptr[q];

                n += R;

            }

        }

    }

}