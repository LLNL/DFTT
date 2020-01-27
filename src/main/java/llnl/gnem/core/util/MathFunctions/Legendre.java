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

/**
 * User: matzel
 * Date: Oct 16, 2006
 * Time: 3:12:46 PM
 */
public class Legendre
{
    /**
     * Pn(z) == 1/2^n  * SUM[k=0:n/2]  [ -1^k  * binomial(n,k) * binomial (2n - 2k, n) * z ^ (n-2*k)
     */
    public static double P(int n, double z)
    {
        double prefix = 1 / Math.pow(2, n);
        double sum = 0.;
        for (int k = 0; k <= n / 2; k++)
        {
            try
            {

                double sign = Math.pow(-1, k);
                double b1 = MathFunction.binomial(n, k);
                double b2 = MathFunction.binomial((2 * n - 2 * k), n);
                double zn2k = Math.pow(z, n - 2 * k);

                sum = sum + sign * b1 * b2 * zn2k;

            }
            catch (Exception e)
            {
            }
        }

        return prefix * sum;
    }

    /**
     * Pmu,n(z)
     *
     public static double P(int n, double mu, double z)
     {

     for (int k = 0; k <= n; k++)
     {

     }
     }*/

}
