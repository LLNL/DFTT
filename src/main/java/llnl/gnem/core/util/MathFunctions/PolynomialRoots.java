/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.util.MathFunctions;

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
