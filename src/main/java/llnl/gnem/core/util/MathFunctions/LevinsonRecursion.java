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
package llnl.gnem.core.util.MathFunctions;

import llnl.gnem.core.util.SeriesMath;

import java.util.Vector;

/**
 * User: Eric Matzel
 * Date: Sep 17, 2007
 *
 * Function to solve Durbin's problem - Toeplitz normal equations, right hand vector of autocorrelations
 * translation of Dave Harris's C program "levin"
 */
public class LevinsonRecursion
{
    /*                                                           PREWIT
 *
 *  Prewhitens an input sequence in-place.  Uses a low-order prediction error
 *    filter. Order selected for stability if needed.
 *
 *  Author:  George Randall after Dave Harris
 *
 *  Created: February 12, 1986
 *
 *  Last Modified:  February 12, 1986
 *                  July 7, 1998.  maf
 *
 *  Input arguments:
 *  ----- ----------
 *
 *    DATA                 REAL*4 array containing input sequence - contains
 *                         prewhitened sequence upon exit from this routine.
 *
 *    #SAMPLES             Number of data points in sequence.
 *
 *    PREDICTOR_ORDER      Order of prediction filter used to prewhiten
 *                         sequence. Truncated for stability
 *
 *  Output Arguments:
 *  ------ ----------
 *
 *    DATA                 As above.
 *
 *    ARRAY                Array of prewhitening filter coefficients.
 *
 *    ERROR_MESSAGE        CHARACTER*130 variable containing error message if
 *                         error is detected.  Equal to ' ' if no errors.
 *
 *  Linkage:     DIRCOR, LEVIN, PEF
 *
 * */
//#define	NCMAX	12

    void /*FUNCTION*/ prewit(float [] data, int nsamps, int order, float [] array, String kprefix, String errmsg)
    {

        int NCMAX = 12;
        int idx, jdx, j2, kdx, kb, torder;
        Double at, q;
        double [] cor = new double [NCMAX];

        //  Initializations

        //  Range check for predictor order
        if (order < 1 || order > NCMAX)
        {
            System.out.println("*** PREWIT  -  Predictor order out of bounds (1-12) ***");
        }

        //  Design the prewhitening filter
        for (int ii = 0; ii <= order; ii++)
        {
            cor[ii] = SeriesMath.crosscorrelate(data, data, ii);
        }

        Vector levinson = levinson(cor);
        Double [] Array = (Double[]) levinson.elementAt(0);
        Double [] Reflct = (Double []) levinson.elementAt(1);
        Double [] Sa = new Double[Reflct.length];

        /*  Check for stability, and truncate filter if necessary
       *  so the recursive de-whitener is safely stable
       *  Use an adaptation of LPTRN code from Markel and Gray
       *  found in the IEEE ASSP book of signal processing codes
       * */
        torder = order;
        /*
       *  If any reflection coefficients are too close to +1 or -1
       *  then the system is getting dangerous, so truncate, and
       *  then regenerate the filter coeffs for the truncated filter
       * */
        if (torder != order)
        {
            for (idx = 1; idx <= torder; idx++)
            {
                Sa[idx] = Reflct[idx];
            }
            for (jdx = 2; jdx <= torder; jdx++)
            {
                j2 = jdx / 2;
                q = Reflct[jdx];
                for (kdx = 1; kdx <= j2; kdx++)
                {
                    kb = jdx - kdx;
                    at = Sa[kdx] + q * Sa[kb];
                    Sa[kb] = Sa[kb] + q * Sa[kdx];
                    Sa[kdx] = at;
                }
            }
            for (idx = 1; idx <= torder; idx++)
            {
                Array[idx + 1] = Sa[idx];
            }
            for (idx = torder + 2; idx <= (order + 1); idx++)
            {
                Array[idx] = 0.;
            }
            Array[1] = 1.;
            order = torder;
        }

      /*  Prewhiten the data
       *
       *    Negate coefficients of prediction error filter generated by LEVIN.
       *    For historical reasons, different storage modes are used in LEVIN
       *    and PEF.
       * */
        float [] result = PredictionErrorFilter(data, Array, 0);

    }


    /**
     * @param data  - input data array
     * @param A     - an array of filter coefficients
     * @param delay - operator delay (number of samples)
     * @return result - the filtered output placed in the array
     */
    public static float [] PredictionErrorFilter(float [] data, Double [] A, int delay)
    {
        float [] result = new float[data.length];

        //    Filter data.
        for (int ii = 0; ii < data.length; ii++)
            for (int k = 0; k < A.length; k++)
            {
                result[ii] = result[ii] + (float) (A[k] * data[ii - delay - k]);
            }

        return result;
    }


    /**
     * Function to solve Durbin's problem - Toeplitz normal equations, right hand vector of autocorrelations
     * translation of Dave Harris's C function "levin.c"
     *
     * @param R Vector of autocorrelations               : size (n)
     * @return a vector containing two elements:
     *         the array of filter coefficients, A[]     : size (n)
     *         and the array of Reflection coefficients, Reflct[]  : size (n-1)
     */
    public static Vector levinson(double [] R)
    {
        int idx, jdx;
        double rhoDenom, rhoNum, rho;
        int n = R.length;

        if (n < 2) return null;

        double [] Temp = new double [n];
        Double [] A = new Double [n];
        Double [] Reflct = new Double [n - 1];

        //  Initialize first two coefficients
        A[0] = 1.;
        A[1] = - R[1] / R[0];
        Reflct[0] = A[1];

        /*  Using Levinson's recursion, determine the rest of the coefficients.
         *  It is assumed that the filter is of length N, including the lead
         *  coefficient which is always one.
         */

        if (n >= 3)
        {
            for (idx = 2; idx < n; idx++)
            {
                rhoNum = R[idx];
                rhoDenom = R[0];

                for (jdx = 1; jdx <= idx - 1; jdx++)
                {
                    rhoNum += A[jdx] * R[idx + 1 - jdx];
                    rhoDenom += A[jdx] * R[jdx];
                } /* end for(jdx) */

                rho = -rhoNum / rhoDenom;
                Reflct[idx - 1] = rho;

                for (jdx = 2; jdx <= idx - 1; jdx++)
                    Temp[jdx] = A[jdx] + rho * (A[idx + 1 - jdx]);

                for (jdx = 2; jdx <= idx - 1; jdx++)
                    A[jdx] = Temp[jdx];

                A[idx] = rho;

            } /* end for(idx) */
        } /* end if */

        Vector result = new Vector();
        result.add(A);
        result.add(Reflct);

        return result;
    }

    public static void toeplitz(double [] R, double [] y)
    {
        int n = y.length;
        int n1 = n - 1;

        double [] x = new double[n1];
        double [] g = new double[n1];
        double [] h = new double[n1];

        if (R[n1] == 0.0)
        {
            return; //1. singular principal minor
        }

        x[0] = y[0] / R[n1];

        if (n1 == 0)
            return;

        g[0] = R[n1 - 1] / R[n1];
        h[0] = R[n1 + 1] / R[n1];

        for (int m = 0; m < n; m++)
        {
            int m1 = m + 1;
            double snumerator = -y[m1];
            double sdenom = -R[n1];

            for (int j = 0; j < m + 1; j++)
            {
                snumerator = snumerator + R[n1 + m1 - j] * x[j];
                sdenom = sdenom + R[n1 + m1 - j] * g[m - j];
            }

            if (sdenom == 0.0)
            {
                return;//2. singular principal minor
            }

            x[m1] = snumerator / sdenom;

            for (int j = 0; j < m + 1; j++)
            {
                x[j] = x[j] - x[m1] * g[m - j];
            }

            if (m1 == n1)
            {
                return;
            }

            // Compute the numerator and denominator for G and H
            double sgn = -R[n1 - m1 - 1];
            double shn = -R[n1 + m1 + 1];
            double sgd = -R[n1];

            for (int j = 0; j < m + 1; j++)
            {
                sgn = sgn + R[n1 + j - m1] * g[j];
                shn = shn + R[n1 + m1 - j] * h[j];
                sgd = sgd + R[n1 + j - m1] * h[m - j];
            }
            if (sgd == 0.0)
            {
                return; //3. singular principal minor
            }

            g[m1] = sgn / sgd;
            h[m1] = shn / sdenom;

            int k = m;
            int m2 = (m + 2) >> 1;

            double pp = g[m1];
            double qq = h[m1];

            for (int j = 0; j < m2; j++)
            {
                double pt1 = g[j];
                double pt2 = g[k];
                double qt1 = h[j];
                double qt2 = h[k];
                g[j] = pt1 - pp * qt2;
                g[k] = pt2 - pp * qt1;
                h[j] = qt1 - qq * pt2;
                h[k--] = qt2 - qq * pt1;
            }
        }
        System.out.println("shouldn't reach here");
    }

}
