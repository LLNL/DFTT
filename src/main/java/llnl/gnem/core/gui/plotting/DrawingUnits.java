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
package llnl.gnem.core.gui.plotting;


/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * Translate between real-world coordinates (mm) and pixels for placement of
 * axis components. (Not used for translation of plot objects( lines, points,
 * etc ). That translation is done by the ValueMapper class. This class will
 * get constructed using the Graphics context of the device on which the axis
 * will be rendered. However, Any specific rendering of a plot could be on an
 * arbitrary device. Therefore, the setCanvas method must be called by JSubPlot at
 * the start of each rendering.
 *
 * @author Doug Dodge
 */
public class DrawingUnits {

    /**
     * Gets the horizontal pixel equivalent of a value in millimeters for this
     * graphics context
     *
     * @param u Input value in millimeters
     * @return The equivalent value in pixels
     */
    public int getHorizUnitsToPixels( double u )
    {
        return (int) ( u * PixelsPerHorizUnit );
    }

    /**
     * Gets the vertical pixel equivalent of a value in millimeters for this
     * graphics context
     *
     * @param u Input value in millimeters
     * @return The equivalent value in pixels
     */
    public int getVertUnitsToPixels( double u )
    {
        return (int) ( u * PixelsPerVertUnit );
    }

    /**
     * Gets the horizontal millimeters equivalent of a value in pixelsfor this
     * graphics context
     *
     * @param u Input value in pixels
     * @return The equivalent value in millimeters
     */
    public double getHorizPixelsToUnits( int u )
    {
        return u / PixelsPerHorizUnit;
    }

    /**
     * Gets the vertical millimeters equivalent of a value in pixelsfor this
     * graphics context
     *
     * @param u Input value in pixels
     * @return The equivalent value in millimeters
     */
    public double getVertPixelsToUnits( int u )
    {
        return u / PixelsPerVertUnit;
    }

    /**
     * Gets the average pixel equivalent of one millimeter for this graphics
     * context
     *
     * @return The equivalent value in pixels
     */
    public double getPixelsPerUnit()
    {
        return ( PixelsPerHorizUnit + PixelsPerVertUnit ) / 2;
    }

    private final double PixelsPerHorizUnit = 72.0 / 25.4;
    // Units are assumed to be millimeters
    private final double PixelsPerVertUnit = 72.0 / 25.4;
    // Units are assumed to be millimeters
}

