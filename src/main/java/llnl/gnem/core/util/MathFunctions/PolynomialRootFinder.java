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
 *
 * @author dodge1
 */
public class PolynomialRootFinder {

    public static PolynomialRoots cubicfcn(double a, double b, double c, double d) {
        /*
         * ----------------------------------------------------------------------
         * % Usage: [x,nroot]=cubicfcn(a, b, c, d) % % Solve a cubic equation
         * where a, b, c, and d are real. % a*x^3 + b*x^2 + c*x + d = 0 % %
         * Public Variables % a, b, c, d ... coefficients (input) % x ... three
         * (generally) complex solutions (output) % nroot ... number of roots
         * (output) % % Instructor: Nam Sun Wang %
         * ----------------------------------------------------------------------
         *
         * % Local Variables: % y1, y2, y3 ... three transformed solutions % %
         * Formula used are given in Tuma, "Engineering Mathematics Handbook",
         * p7 % (McGraw Hill, 1978). % Step 0: If a is 0. use the quadratic
         * formula to avoid dividing by 0. % Step 1: Calculate p and q % p = (
         * 3*c/a - (b/a)**2 ) / 3 % q = ( 2*(b/a)**3 - 9*b*c/a/a + 27*d/a ) / 27
         * % Step 2: Calculate discriminant D % D = (p/3)**3 + (q/2)**2 % Step
         * 3: Depending on the sign of D, we follow different strategy. % If
         * D<0, thre distinct real roots. % If D=0, three real roots of which at
         * least two are equal. % If D>0, one real and two complex roots. % Step
         * 3a: For D>0 and D=0, % Calculate u and v % u = cubic_root(-q/2 +
         * sqrt(D)) % v = cubic_root(-q/2 - sqrt(D)) % Find the three
         * transformed roots % y1 = u + v % y2 = -(u+v)/2 + i (u-v)*sqrt(3)/2 %
         * y3 = -(u+v)/2 - i (u-v)*sqrt(3)/2 % Step 3b Alternately, for D<0, a
         * trigonometric formulation is more convenient % y1 = 2 * sqrt(|p|/3) *
         * cos(phi/3) % y2 = -2 * sqrt(|p|/3) * cos((phi+pi)/3) % y3 = -2 *
         * sqrt(|p|/3) * cos((phi-pi)/3) % where phi =
         * acos(-q/2/sqrt(|p|**3/27)) % pi = 3.141592654... % Step 4 Finally,
         * find the three roots % x = y - b/a/3 %
         * ----------------------------------------------------------------------
         */


// Step 0: If a is 0 use the quadratic formula. -------------------------
        if (a == 0.) {
            return quadfcn(b, c, d);

        }

// Cubic equation with 3 roots
     

// Step 1: Calculate p and q --------------------------------------------
        double p = c / a - b * b / a / a / 3.;
        double q = (2. * b * b * b / a / a / a - 9. * b * c / a / a + 27. * d / a) / 27.;

// Step 2: Calculate DD (discriminant) ----------------------------------
        double DD = p * p * p / 27. + q * q / 4.;

// Step 3: Branch to different algorithms based on DD -------------------
        double y1;
        double y2 = 0;
        double y3 = 0;
        double y2r = 0;
        double y2i = 0;
        if (DD < 0.) {
//       Step 3b:
//       3 real unequal roots -- use the trigonometric formulation
            double phi = Math.acos(-q / 2. / Math.sqrt(Math.abs(p * p * p) / 27.));
            double temp1 = 2. * Math.sqrt(Math.abs(p) / 3.);
            y1 = temp1 * Math.cos(phi / 3.);
            y2 = -temp1 * Math.cos((phi + Math.PI) / 3.);
            y3 = -temp1 * Math.cos((phi - Math.PI) / 3.);
        } else {
//       Step 3a:
//       1 real root & 2 conjugate complex roots OR 3 real roots (some are equal)
            double temp1 = -q / 2. + Math.sqrt(DD);
            double temp2 = -q / 2. - Math.sqrt(DD);
            double u = Math.pow(Math.abs(temp1), (1. / 3.));
            double v = Math.pow(Math.abs(temp2), (1. / 3.));
            if (temp1 < 0.) {
                u = -u;
            }
            if (temp2 < 0.) {
                v = -v;
            }
            y1 = u + v;
            y2r = -(u + v) / 2.;
            y2i = (u - v) * Math.sqrt(3.) / 2.;
        }

// Step 4: Final transformation -----------------------------------------
        double temp1 = b / a / 3.;
        y1 = y1 - temp1;
        if (DD < 0.) {
            y2 = y2 - temp1;
            y3 = y3 - temp1;
        } else {
            y2r = y2r - temp1;
        }


// Assign answers -------------------------------------------------------
        if (DD < 0.) {
            return new PolynomialRoots(y1, y2, y3);

        } else if (DD == 0.) {
            return new PolynomialRoots(y1, y2r, y2r);

        } else {
            return new PolynomialRoots(y1, new org.apache.commons.math3.complex.Complex(y2r, y2i), new org.apache.commons.math3.complex.Complex(y2r, -y2i));

        }
    }

    /**
     * % Solve a quadratic equation where a, b, and c are real. % a*x*x + b*x +
     * c = 0 % % Public Variables % a, b, c ... coefficients (input) % x ... two
     * complex solutions (output) % nroot ... number of roots (output) % %
     * Programming Note: % [x,nroot] does NOT mean it is a matrix, for the
     * dimensions of x & root do not have to match. % When this function is
     * called without assigning to two variables, only the first variable "x"
     * shows up. % e.g., x=quadfcn(1,2,3) % x=quadfcn(1,2,3)+1 % When this
     * function is called by assigning to two variables, both "x" and "nroot"
     * are assigned (but they can have different dimensions). % e.g.,
     * [x,nroot]=quadfcn(1,2,3) % To extract out an element of the function,
     * perform masking with a dot product. % e.g., quadfcn(1,2,3)*[ 1 0 ]' ...
     * extract 1st element % quadfcn(1,2,3)*[ 0 1 ]' ... extract 2nd element
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    public static PolynomialRoots quadfcn(double a, double b, double c) {
        if (a == 0) {
            if (b == 0) //         We have a non-equation; therefore, we have no valid solution
            {
                return null;
            } else {
                return new PolynomialRoots(-c / b);
            }
        } //         We have a linear equation with 1 root.
        else //     We have a true quadratic equation.  Apply the quadratic formula to find two roots.
        {

            double DD = b * b - 4 * a * c;
            if (DD == 0) {
                return new PolynomialRoots(-b / 2 / a);
            } else if (DD > 0) {
                double x1 = (-b + Math.sqrt(DD)) / 2 / a;
                double x2 = (-b - Math.sqrt(DD)) / 2 / a;
                return new PolynomialRoots(x1, x2);
            }
            else{
                DD = Math.abs(DD);
                org.apache.commons.math3.complex.Complex x1 = new org.apache.commons.math3.complex.Complex(-b/2/a, Math.sqrt(DD)/2/a);
                org.apache.commons.math3.complex.Complex x2 = new org.apache.commons.math3.complex.Complex(-b/2/a, -Math.sqrt(DD)/2/a);
                return new PolynomialRoots(x1, x2);
            }
        }

    }
}