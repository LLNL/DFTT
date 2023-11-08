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
package llnl.gnem.dftt.core.signalprocessing.arrayProcessing;

/**
 * Copyright (c) 2007  Regents of the University of California
 * All rights reserved
 * Author:  Dave Harris
 * Created: Nov 6, 2006
 * Time: 7:09:59 PM
 * Last Modified: Nov 6, 2006
 */

import java.io.PrintStream;

import org.ojalgo.matrix.ComplexMatrix;
import org.ojalgo.matrix.ComplexMatrix.DenseReceiver;
import org.ojalgo.scalar.ComplexNumber;

public abstract class AbstractSensorArray implements SensorArray {

    protected String name;
    protected int nch;

    protected double[] x; // element position east in kilometers
    protected double[] y; // element position north in kilometers
    protected double[] z; // element depth compared to datum in kilometers

    protected double[] lat; // element latitude in degrees
    protected double[] lon; // element longitude in degrees
    protected double[] elev; // element elevation in kilometers

    protected double referenceLatitude;
    protected double referenceLongitude;
    protected double referenceElevation;

    protected String[] elementNames;

    public AbstractSensorArray(int nch, String name) {

        this.name = new String(name);
        this.nch = nch;

        x = new double[nch];
        y = new double[nch];
        z = new double[nch];
        lat = new double[nch];
        lon = new double[nch];
        elev = new double[nch];

        elementNames = new String[nch];

    }

    @Override
    public double[] getElementCoordinates(String elementName) {

        double[] retval = null;
        for (int i = 0; i < elementNames.length; i++) {
            if (elementName.indexOf(elementNames[i]) > -1) {
                retval = new double[3];
                retval[0] = x[i];
                retval[1] = y[i];
                retval[2] = z[i];
                break;
            }
        }

        return retval;
    }

    @Override
    public void print(PrintStream ps) {
        ps.println(name);
        ps.println("  " + ((float) referenceLatitude) + "  " + ((float) referenceLongitude) + "  " + ((float) referenceElevation));
        ps.println();
        for (int i = 0; i < elementNames.length; i++) {
            ps.println("  " + elementNames[i] + "  " + ((float) x[i]) + "  " + ((float) y[i]) + "  " + ((float) z[i]));
        }
    }

    protected void computeXYZ() {
        double scaleNorth = 111.12;
        double scaleEast = 111.12 * Math.cos(Math.PI / 180.0 * referenceLatitude);
        for (int i = 0; i < nch; i++) {
            x[i] = scaleEast * (lon[i] - referenceLongitude);
            y[i] = scaleNorth * (lat[i] - referenceLatitude);
            z[i] = -(elev[i] - referenceElevation);
        }
    }

    @Override
    public float[] getXArray(String[] list) {

        float[] retval = new float[list.length];

        for (int j = 0; j < list.length; j++) {
            retval[j] = 9999.99f;
            for (int i = 0; i < elementNames.length; i++) {
                if (list[j].indexOf(elementNames[i]) > -1) {
                    retval[j] = (float) x[i];
                    break;
                }
            }
        }

        return retval;
    }

    @Override
    public float[] getXArray() {
        float[] retval = new float[nch];
        for (int i = 0; i < nch; i++) {
            retval[i] = (float) x[i];
        }
        return retval;
    }

    @Override
    public float[] getYArray() {
        float[] retval = new float[nch];
        for (int i = 0; i < nch; i++) {
            retval[i] = (float) y[i];
        }
        return retval;
    }

    @Override
    public float[] getZArray() {
        float[] retval = new float[nch];
        for (int i = 0; i < nch; i++) {
            retval[i] = (float) z[i];
        }
        return retval;
    }

    @Override
    public float[] getYArray(String[] list) {

        float[] retval = new float[list.length];

        for (int j = 0; j < list.length; j++) {
            retval[j] = 9999.99f;
            for (int i = 0; i < elementNames.length; i++) {
                if (list[j].indexOf(elementNames[i]) > -1) {
                    retval[j] = (float) y[i];
                    break;
                }
            }
        }

        return retval;
    }

    @Override
    public float[] getZArray(String[] list) {

        float[] retval = new float[list.length];

        for (int j = 0; j < list.length; j++) {
            retval[j] = 9999.99f;
            for (int i = 0; i < elementNames.length; i++) {
                if (list[j].indexOf(elementNames[i]) > -1) {
                    retval[j] = (float) z[i];
                    break;
                }
            }
        }

        return retval;
    }

    @Override
    public ComplexMatrix calculateTheoreticalSteeringVector(float[] s, float freq) {
        return calculateTheoreticalSteeringVector(s, freq, x, y, z);
    }

    public ComplexMatrix calculateTheoreticalSteeringVector(float[] s, float freq, double[] xs, double[] ys, double[] zs) {
        int ns = xs.length;
        DenseReceiver e = ComplexMatrix.FACTORY.makeDense(ns, 1);
        double scale = 1.0 / Math.sqrt(ns);
        for (int ich = 0; ich < ns; ich++) {
            float delay = (float) (s[0] * xs[ich] + s[1] * ys[ich] + s[2] * zs[ich]);
            //      float delay = s[0]*x[ich] + s[1]*y[ich];                 // neglecting elevation corrections
            double phase = 2.0 * Math.PI * freq * delay;
            e.set(ich, 0, ComplexNumber.of(scale * Math.cos(phase), scale * Math.sin(phase)));
        }
        return e.get();
    }

}
