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
package llnl.gnem.dftt.core.gui.plotting.transforms;

import llnl.gnem.dftt.core.gui.plotting.AxisScale;


public interface CoordinateTransform {

    /**
     * Initialize the CoordinateTransform object given the current dimensions of the axis
     * on which the plot is being rendered.  This must be done at the start of rendering when
     * the plot has resized.
     *
     * @param WorldC1Min  The minimum value of the first world coordinate
     * @param WorldC1Max  The maximum value of the first world coordinate
     * @param axisXmin    The minimum (horizontal) pixel of the plot region
     *                    assuming the x-axis direction is not reversed.
     * @param axisXwidth  The width of the plot region
     * @param WorldC2Min  The minimum value of the second world coordinate
     * @param WorldC2Max  The maximum value of the second world coordinate
     * @param axisYmin    The minimum (vertical) pixel of the plot region
     *                    assuming the y-axis direction is not reversed.
     * @param axisYheight The height of the plot region
     */
    public void initialize( double WorldC1Min, double WorldC1Max, int axisXmin, int axisXwidth,
                            double WorldC2Min, double WorldC2Max, int axisYmin, int axisYheight );

    /**
     * Populates the plot part of the Coordinate object by applying the transform
     * from World to plot. Assumes the World part of the Coordinate object has been set
     * and the CoordinateTransform object has been initialized.
     *
     * @param v The Coordinate object with its World values set. After returning, the
     *          Plot values will be set. The World values are not modified.
     */
    public void WorldToPlot( Coordinate v );

    /**
     * Populates the world part of the Coordinate object by applying the transform
     * from plot to world. Assumes the plot part of the Coordinate object has been set
     * and the CoordinateTransform object has been initialized.
     *
     * @param v The Coordinate object with its plot values set. After returning, the
     *          world values will be set. The plot values are not modified.
     */
    public void PlotToWorld( Coordinate v );

    /**
     * determines whether a specific coordinate value is within the current Transform bounds
     * Not all transforms have bounds,and implementations in which bounds are not important should
     * just return false.
     *
     * @param v The coordinate to be tested
     * @return true if the coordinate is out of bounds.
     */
    public boolean isOutOfBounds( Coordinate v );

    /**
     * Gets the distance in world coordinates between two points
     *
     * @param c1 The first coordinate
     * @param c2 The second coordinate
     * @return The distance
     */
    public double getWorldDistance( Coordinate c1, Coordinate c2 );

    public void setXScale( AxisScale scale );

    public void setYScale( AxisScale scale );

    public AxisScale getXScale();

    public AxisScale getYScale();

    public int getWidthPixels();
}
