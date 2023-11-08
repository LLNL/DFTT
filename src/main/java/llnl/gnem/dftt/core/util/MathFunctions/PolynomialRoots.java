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
package llnl.gnem.dftt.core.util.MathFunctions;

import java.util.ArrayList;

/**
 *
 * @author dodge1
 */
public class PolynomialRoots {

    private final ArrayList<Double> realRoots = new ArrayList<Double>();
    private final ArrayList<org.apache.commons.math3.complex.Complex> complexRoots = new ArrayList<org.apache.commons.math3.complex.Complex>();

    public PolynomialRoots(double r1, double r2, double r3) {
        realRoots.add(r1);
        realRoots.add(r2);
        realRoots.add(r3);
    }

    public PolynomialRoots(double r1, org.apache.commons.math3.complex.Complex r2, org.apache.commons.math3.complex.Complex r3) {
        realRoots.add(r1);
        complexRoots.add(r2);
        complexRoots.add(r3);
    }

    public PolynomialRoots(double r1) {
        realRoots.add(r1);
    }

    public ArrayList<Double> getRealRoots() {
        return new ArrayList<Double>(realRoots);
    }

    public ArrayList<org.apache.commons.math3.complex.Complex> getComplexRoots() {
        return new ArrayList<org.apache.commons.math3.complex.Complex>(complexRoots);
    }

    public PolynomialRoots(double r1, double r2) {
        realRoots.add(r1);
        realRoots.add(r2);

    }

    public PolynomialRoots(org.apache.commons.math3.complex.Complex r1, org.apache.commons.math3.complex.Complex r2) {

        complexRoots.add(r1);
        complexRoots.add(r2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!realRoots.isEmpty()) {
            sb.append("Real Roots:\n");
            for (Double v : realRoots) {
                sb.append(v);
                sb.append("\n");
            }
        }
        if ((!complexRoots.isEmpty())) {
            sb.append("Complex Roots:\n");
            for (org.apache.commons.math3.complex.Complex c : complexRoots) {
                sb.append(c.toString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
