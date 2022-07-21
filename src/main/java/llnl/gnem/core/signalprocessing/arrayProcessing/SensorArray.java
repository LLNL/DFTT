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
package llnl.gnem.core.signalprocessing.arrayProcessing;

/**
 * Copyright (c) 2007  Regents of the University of California
 * All rights reserved
 * Author:  Dave Harris
 * Created: Nov 6, 2006
 * Time: 7:35:45 PM
 * Last Modified: January 20, 2006
 */

import java.io.PrintStream;

import org.ojalgo.matrix.ComplexMatrix;

public interface SensorArray {

    public double[] getElementCoordinates(String elementName);

    public float[] getXArray(String[] list);

    public float[] getYArray(String[] list);

    public float[] getZArray(String[] list);

    public float[] getXArray();

    public float[] getYArray();

    public float[] getZArray();

    public ComplexMatrix calculateTheoreticalSteeringVector(float[] s, float freq);

    public void print(PrintStream ps);

}
