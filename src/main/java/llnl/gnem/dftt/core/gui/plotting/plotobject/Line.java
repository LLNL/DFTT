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
package llnl.gnem.dftt.core.gui.plotting.plotobject;

import java.awt.Color;
import java.util.Arrays;

import llnl.gnem.dftt.core.gui.plotting.PaintMode;
import llnl.gnem.dftt.core.gui.plotting.PenStyle;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.dftt.core.polygon.BinarySearch;
import llnl.gnem.dftt.core.util.Pair;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */
/**
 * A class that encapsulates a multi-segment line that can be rendered in a
 * JSubplot
 *
 * @author Doug Dodge
 */
@SuppressWarnings({"AssignmentToNull", "ClassWithTooManyConstructors"})
public class Line extends AbstractLine {

    private float[] xArray;
    private float[] yArray;

    public Line() {
        xArray = null;
        yArray = null;
    }

    public Line(Color c, PaintMode m, PenStyle s, int w) {
        super(c, m, s, w);
        xArray = null;
        yArray = null;
    }

    /**
     * Constructor for the Line object
     *
     * @param x The Xvalues
     * @param y The Yvalues
     * @param c The color of the Line
     * @param m The PaintMode of the Line
     * @param s The PenStyle of the Line
     * @param w The width of the Line
     */
    public Line(float[] x, float[] y, Color c, PaintMode m, PenStyle s, int w) {
        super(c, m, s, w);
        int N = x.length;
        if (N > y.length) {
            N = y.length;
        }
        if (N < 2) {
            return;
        }
        fillXarray(x);
        fillYarray(y);
    }

    /**
     * Constructor for the Line object
     *
     * @param x The Xvalues
     * @param y The Yvalues
     */
    public Line(float[] x, float[] y) {
        super();
        int N = x.length;
        if (N > y.length) {
            N = y.length;
        }
        if (N < 2) {
            return;
        }
        fillXarray(x);
        fillYarray(y);
    }

    /**
     * Constructor for the Line object
     *
     * @param Start The starting value of X
     * @param Inc   The X-increment
     * @param y     The Yvalues
     * @param c     The color of the Line
     * @param m     The PaintMode of the Line
     * @param s     The PenStyle of the Line
     * @param w     The width of the Line
     */
    public Line(double Start, double Inc, float[] y, Color c, PaintMode m, PenStyle s, int w) {
        super(Start, Inc, c, m, s, w);
        int N = y.length;
        if (N < 2) {
            return;
        }
        fillYarray(y);
    }

    /**
     * Constructor for the Line object
     *
     * @param Start The starting value of X
     * @param Inc   The X-increment
     * @param y     The Yvalues
     */
    public Line(double Start, double Inc, float[] y) {
        this(Start, Inc, y, 1);
    }

    public Line(double start, double inc, float[] y, int lineWidth) {
        this(start, inc, y, Color.blue, PaintMode.COPY, PenStyle.SOLID, lineWidth);
    }

    /**
     * Constructor for the Line object
     *
     * @param x The Xvalues
     * @param y The Yvalues
     * @param c The color of the Line
     * @param m The PaintMode of the Line
     * @param s The PenStyle of the Line
     * @param w The width of the Line
     */
    public Line(float[] x, float[] y, Color c, PaintMode m, PenStyle s, int w, float[] sigmaY) {
        super(0.0, 0.0, c, m, s, w, SymbolStyle.ERROR_BAR, sigmaY);

        if (sigmaY.length != y.length) {
            throw new IllegalArgumentException("The sigmaY array is not the same length as the Y array!");
        }

        int N = x.length;
        if (N > y.length) {
            N = y.length;
        }
        if (N < 2) {
            return;
        }
        fillXarray(x);
        fillYarray(y);
    }

    public void replaceYarray(float[] y) {
        if (y == null || y.length < 2) {
            throw new IllegalArgumentException("Replacement Y-array is invalid. ");
        }
        if (yArray != null && xArray != null && yArray.length != y.length) {
            throw new IllegalArgumentException("Replacement Y-array must be same length as original.");
        }

        int N = y.length;
        yArray = new float[N];
        System.arraycopy(y, 0, yArray, 0, N);
        resetBounds();
    }

    public void updateArrays(float[] x, float[] y) {
        int N = x.length;
        if (y.length != N) {
            throw new IllegalArgumentException("Input x and y arrays have different lengths!");
        }

        setIncrement(0);
        setStart(0);
        xArray = new float[N];
        yArray = new float[N];
        System.arraycopy(x, 0, xArray, 0, N);
        System.arraycopy(y, 0, yArray, 0, N);
    }

    public double getXBegin() {
        if (xArray != null && xArray.length > 0) {
            return xArray[0];
        } else {
            return getStart();
        }
    }

    public double getXEnd() {
        if (xArray != null && xArray.length > 0) {
            return xArray[xArray.length - 1];
        } else {
            return getStart() + getIncrement() * (yArray.length - 1);
        }
    }

    /**
     * Gets the xdata of the Line
     *
     * @return The xdata value
     */
    public float[] getXdata() {
        return xArray;
    }

    /**
     * Gets the ydata of the Line
     *
     * @return The ydata value
     */
    public float[] getYdata() {
        return yArray;
    }

    protected final void fillYarray(float[] y) {
        int N = y.length;
        yArray = new float[N];
        System.arraycopy(y, 0, yArray, 0, N);
    }

    protected final void fillXarray(float[] x) {
        int N = x.length;
        xArray = new float[N];
        System.arraycopy(x, 0, xArray, 0, N);
    }

    public int length() {
        return yArray != null ? yArray.length : 0;
    }

    @Override
    public synchronized void updateXValues(double dx) {
        if (xArray != null) {
            int N = xArray.length;
            for (int j = 0; j < N; ++j) {
                xArray[j] += (float) dx;
            }
        } else {
            setStart(getStart() + (float) dx);
        }
        resetBounds();
    }

    @Override
    protected void updateYValues(float dy) {
        int N = yArray.length;
        for (int j = 0; j < N; ++j) {
            yArray[j] += dy;
        }
        resetBounds();
    }

    public Pair getSubsectionData(double startTime, double end, int minSamples) {
        if (end <= startTime) {
            throw new IllegalArgumentException("Sub-section end value is <= sub-section start value!");
        }

        int[] currentBounds = getTimeIndex(startTime);
        int idxStart = currentBounds[0];
        currentBounds = getTimeIndex(end);
        int idxEnd = currentBounds[1];
        if (idxStart < 0) {
            idxStart = 0;
        }
        if (idxEnd > yArray.length - 1) {
            idxEnd = yArray.length - 1;
        }

        int maxAvailableIdx = yArray.length - 1;
        int N = idxEnd - idxStart + 1;
        while (N < minSamples) {
            if (idxStart > 0) {
                --idxStart;
            }
            if (idxEnd < maxAvailableIdx) {
                ++idxEnd;
            }
            N = idxEnd - idxStart + 1;
        }
        float[] xOut = new float[N];
        if (xArray != null) {
            System.arraycopy(xArray, idxStart, xOut, 0, N);
        } else {
            for (int j = 0; j < N; ++j) {
                xOut[j] = (float) (getStart() + (j + idxStart) * getIncrement());
            }
        }

        float[] yOut = new float[N];
        System.arraycopy(yArray, idxStart, yOut, 0, N);
        return new Pair(xOut, yOut);
    }

    private int[] getTimeIndex(double requestedTime) {
        if (xArray != null) {
            return BinarySearch.bounds(xArray, (float) requestedTime);
        } else if (getIncrement() > 0) {
            int[] result = new int[2];
            double val = (requestedTime - getStart()) / getIncrement();
            result[0] = (int) Math.floor(val);
            result[1] = (int) Math.ceil(val);
            return result;
        } else {
            throw new IllegalStateException("Cannot determine sub-section start index!");
        }
    }

    @Override
    protected int getMinIndex() {
        if (owner != null && owner instanceof JSubplot) {
            JSubplot plot = (JSubplot) owner;
            double xmin = plot.getXaxis().getMin();
            double ymin = plot.getYaxis().getMin();
            double ymax = plot.getYaxis().getMax();

            int[] bounds = getTimeIndex(xmin);
            int requestedMin = bounds[0] - 1;
            // Include one point outside axis range so that for lines crossing the axis
            // but without any points inside, something still gets plotted.
            int provisionalIdx = Math.max(0, requestedMin);
            if (yArray != null) {

                // Even after the x-values move into range, Y may be out of range. Iterate
                // until first y-value >= ymin.
                for (int j = provisionalIdx; j < yArray.length; ++j) {
                    if (yArray[j] >= ymin || yArray[j] <= ymax) {
                        return Math.max(0, j - 1);
                    }
                }
            }

            // Something is fishy. Return the value when x first exceeded its minimum.
            return provisionalIdx;
        } else {
            return 0;
        }
    }

    @Override
    protected int getMaxIndex() {
        if (owner != null && owner instanceof JSubplot) {
            JSubplot plot = (JSubplot) owner;
            double xmax = plot.getXaxis().getMax();
            double ymin = plot.getYaxis().getMin();
            double ymax = plot.getYaxis().getMax();

            int[] bounds = getTimeIndex(xmax);
            int requestedMax = bounds[1] + 1;
            // Even after the x-values move into range, Y may be out of range. Iterate backwards
            // until first y-value >= ymin.
            int provisionalIdx = requestedMax;
            if (yArray != null && yArray.length > 1) {
                int N = yArray.length - 1;
                provisionalIdx = Math.min(N, requestedMax);
                for (int j = provisionalIdx; j >= 0; --j) {
                    if (yArray[j] >= ymin || yArray[j] <= ymax) {
                        return Math.min(N, j + 1);
                    }
                }
            }

            // Something is fishy. Return the value when x first exceeded its minimum.
            return provisionalIdx;
        } else {
            return yArray.length - 1;
        }
    }

    @Override
    public int getXSize() {
        return xArray != null ? xArray.length : 0;
    }

    @Override
    public int getYSize() {
        return yArray != null ? yArray.length : 0;
    }

    @Override
    public double getXDataMin() {
        return getDataMin(xArray);
    }

    @Override
    public double getXDataMax() {
        return getDataMax(xArray);
    }

    @Override
    public double getYDataMin() {
        return getDataMin(yArray);
    }

    @Override
    public double getYDataMax() {
        return getDataMax(yArray);
    }

    @Override
    protected double getXValue(int i) {
        if (getIncrement() > 0) {
            return getStart() + i * getIncrement();
        } else if (xArray != null) {
            return xArray[i];
        } else {
            throw new IllegalStateException("Increment not set and xArray is null");
        }
    }

    @Override
    protected double getYValue(int i) {
        return yArray[i];
    }

    public Double getValueAt(double xValue) {
        if (yArray != null && yArray.length > 0) {
            if (xArray != null && xArray.length > 0) {
                int index = Arrays.binarySearch(xArray, (float) xValue);
                return getYValue(index);
            } else {
                long index = Math.round((xValue - getStart()) / getIncrement());
                return getYValue((int) index);

            }
        }
        return null;
    }
}
