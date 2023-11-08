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
package llnl.gnem.dftt.core.gui.plotting.beachball;

import llnl.gnem.dftt.core.util.PairT;

/**
 * Created by dodge1
 * Date: Mar 5, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class Boundaries {
    private final double[] x1;
    private final double[] y1;
    private final double[] x2;
    private final double[] y2;
    private final double[] xPaxis;
    private final double[] yPaxis;
    private final PairT<double[], double[]> boundingCircle;

    Boundaries(double[] x1, double[] y1, double[] x2, double[] y2, double[] xPaxis, double[] yPaxis, PairT<double[], double[]> boundingCircle) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.xPaxis = xPaxis;
        this.yPaxis = yPaxis;
        this.boundingCircle = boundingCircle;
    }

    public double[] getX1() {
        return x1;
    }

    public double[] getY1() {
        return y1;
    }

    public double[] getX2() {
        return x2;
    }

    public double[] getY2() {
        return y2;
    }

    public PairT<double[], double[]> getBoundingCircle() {
        return boundingCircle;
    }

    public double[] getXPaxis() {
        return xPaxis;
    }

    public double[] getYPaxis() {
        return yPaxis;
    }
}
