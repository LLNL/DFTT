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
package llnl.gnem.core.util;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * User: dodge1
 * Date: Feb 18, 2004
 * Time: 1:02:20 PM
 */
public class Utility {

    /**
     * Given a Vector of Strings, return a Single String which is a comma-separated, list of single-qouted Strings
     * one for each element in the Vector. For example. If the input Vector contains the Strings
     * "Hello" and "World", this method will return the String "'Hello', 'World'"
     *
     * @param str A vector containing Strings that are to be returned in a single-quoted list
     * @return The String containing a comma-separated, single-quoted list of Strings
     */
    public static String getQuotedList(List<String> str) {
        StringBuilder sb = new StringBuilder();
        int N = str.size();
        for (int j = 0; j < N; ++j) {
            sb.append('\'').append(str.get(j)).append('\'');
            if (j < N - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String BuildCommaDelimitedValueString(String[] intStrings) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < intStrings.length; i++) {
            sb.append(intStrings[i]);
            if (i < intStrings.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static void testInternetAccess(boolean quitOnFailure) throws IOException {
        InternetAccessChecker checker = new InternetAccessChecker();
        while (!checker.canAccessInternet()) {
            String failureOption = quitOnFailure ? "Quit" : "Ignore";
            Object[] options = {"Retry", failureOption};
            int answer = JOptionPane.showOptionDialog(null,
                    "Cannot Access Internet!\nHave you authenticated?",
                    "Error Accessing Internet",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, //do not use a custom Icon
                    options, //the titles of buttons
                    options[0]); //default button title
            if (answer == JOptionPane.NO_OPTION) {
                if (quitOnFailure) {
                    System.exit(1);
                } else {
                    return;
                }
            }
        }
    }

    public static class CheckInternetConnectionTask extends TimerTask {

        private final boolean quitOnFailure;

        public CheckInternetConnectionTask() {
            quitOnFailure = true;
        }

        public CheckInternetConnectionTask(boolean quitOnFailure) {
            this.quitOnFailure = quitOnFailure;
        }

        @Override
        public void run() {
            try {
                testInternetAccess(quitOnFailure);
            } catch (IOException ex) {
                Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public static double log10(double x) {
        return Math.log10(x);
    }

    /**
     * Method to round a double value to the given precision
     * @param val The value to be rounded.
     * @param precision The number of decimal places to round to.
     * Will not inspect for: MagicNumber
     * @return  The rounded value.
     */
    public static double round(double val, int precision) {

        // Multiply by 10 to the power of precision and add 0.5 for rounding up
        // Take the nearest integer smaller than this value
        double aval = Math.floor(val * Math.pow(10, precision) + 0.5);

        // Divide it by 10**precision to get the rounded value
        return aval / Math.pow(10, precision);
    }

    /**
     * Returns the coefficients of a straight line fit to the data in input arrays
     * x and y between the points idx1 through idx2 inclusive.
     * @param x A double array containing the x-values.
     * @param y A double array containing the y-values.
     * @param idx1  The index of the first element in the section to be fitted.
     * @param idx2 The index of the last element to include in the fit.
     * @return A Pair object whose first object is the intercept as a Double and whose second object
     * is the slope as a Double. If the data being fit define a vertical line, the
     * slope object will have the value Double.MAX_VALUE.
     */
    public static Pair getLineCoefficients(double[] x, double[] y, int idx1, int idx2) {
        int n = x.length;
        if (y.length != n) {
            throw new IllegalArgumentException("Input arrays are not equal in length!");
        }
        if (idx1 < 0 || idx1 >= n) {
            throw new IllegalArgumentException("Index 1 is out of bounds!");
        }
        if (idx2 < 0 || idx2 >= n) {
            throw new IllegalArgumentException("Index 2 is out of bounds!");
        }
        if (idx2 < idx1 + 1) {
            throw new IllegalArgumentException("Index 2 must be at least one greater than index 1!");
        }




        // First determine the slope and intercept of the best fitting line...
        int nSamps = idx2 - idx1 + 1;
        double xbar = 0.0;
        for (int j = idx1; j <= idx2; ++j) {
            xbar += x[j];
        }
        xbar /= nSamps;

        double ybar = 0.0;
        for (int j = idx1; j <= idx2; ++j) {
            ybar += y[j];
        }
        ybar /= nSamps;

        double SSX = 0.0;
        double SSXY = 0.0;
        for (int j = idx1; j <= idx2; ++j) {
            double tmp1 = x[j] - xbar;
            double tmp2 = y[j] - ybar;
            SSX += tmp1 * tmp1;
            SSXY += tmp1 * tmp2;
        }
        double B1 = Double.MAX_VALUE;
        if (SSX > Double.MIN_VALUE) {
            B1 = SSXY / SSX;
        }

        // The slope

        double B0 = ybar - B1 * xbar;
        return new Pair(B0, B1);

    }
}
