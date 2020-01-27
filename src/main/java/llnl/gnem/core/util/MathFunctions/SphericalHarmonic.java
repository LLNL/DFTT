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
 * Time: 2:50:45 PM
 */
public class SphericalHarmonic extends MathFunction
{
    /**
     * Spherical Harmonic Ymn(theta,phi)
     *
     *  |m| <= n
     *
     public static Complex Y(int n, int m, double theta, double phi)
     {
     Complex result = new Complex(0., 0.);

     if (n <= Math.abs(m)) return result;

     try
     {
     double numerator = (2 * n + 1) * factorial(n - m);
     double denominator = 4 * Math.PI * (n + m);

     Complex imag = new Complex(0, 1.);
     Complex eimphi = Complex.exp(imag.times(m * phi)); // e^(i*m*phi)
     Complex Pmn = Legendre.P(Math.cos(theta));

     result = eimphi.times(Pmn).times((numerator / denominator));

     }
     catch (Exception e)
     {
     }

     return result;
     }*/
}
