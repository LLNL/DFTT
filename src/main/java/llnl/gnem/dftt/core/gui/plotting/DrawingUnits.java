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
package llnl.gnem.dftt.core.gui.plotting;

import java.awt.Toolkit;

/**
 * Translate between real-world coordinates (mm) and pixels for placement of
 * axis components. (Not used for translation of plot objects( lines, points,
 * etc ). That translation is done by the ValueMapper class. This class will
 * get constructed using the Graphics context of the device on which the axis
 * will be rendered. However, Any specific rendering of a plot could be on an
 * arbitrary device. Therefore, the setCanvas method must be called by JSubPlot
 * at
 * the start of each rendering.
 *
 * @author Doug Dodge
 */
public class DrawingUnits {

    private static final double MILLIMETERS_PER_INCH = 25.4;
    private static final double DEFAULT_PIXELS_PER_INCH = Toolkit.getDefaultToolkit().getScreenResolution();
    private static double PIXELS_PER_INCH = DEFAULT_PIXELS_PER_INCH;

    public static void setDPI(int dpi) {
        PIXELS_PER_INCH = dpi;
    }

    public static void setToDefault() {
        PIXELS_PER_INCH = DEFAULT_PIXELS_PER_INCH;
    }

    static double getScale(int dpi) {
        return dpi / PIXELS_PER_INCH;
    }

    static double getPixelsToMM(int pixels) {
        double inches = pixels / PIXELS_PER_INCH;
        return inches * MILLIMETERS_PER_INCH;
    }

    /**
     * Gets the horizontal pixel equivalent of a value in millimeters for this
     * graphics context
     *
     * @param u Input value in millimeters
     * @return The equivalent value in pixels
     */
    public int getHorizUnitsToPixels(double u) {
        return (int) (u * PIXELS_PER_INCH / MILLIMETERS_PER_INCH);
    }

    /**
     * Gets the vertical pixel equivalent of a value in millimeters for this
     * graphics context
     *
     * @param u Input value in millimeters
     * @return The equivalent value in pixels
     */
    public int getVertUnitsToPixels(double u) {
        return (int) (u * PIXELS_PER_INCH / MILLIMETERS_PER_INCH);
    }

    public double getVerticalUnitsToPixels(double u) {
        return u * PIXELS_PER_INCH / MILLIMETERS_PER_INCH;
    }

    /**
     * Gets the horizontal millimeters equivalent of a value in pixelsfor this
     * graphics context
     *
     * @param u Input value in pixels
     * @return The equivalent value in millimeters
     */
    public double getHorizPixelsToUnits(int u) {
        return u / (PIXELS_PER_INCH / MILLIMETERS_PER_INCH);
    }

    /**
     * Gets the vertical millimeters equivalent of a value in pixelsfor this
     * graphics context
     *
     * @param u Input value in pixels
     * @return The equivalent value in millimeters
     */
    public double getVertPixelsToUnits(int u) {
        return u / (PIXELS_PER_INCH / MILLIMETERS_PER_INCH);
    }

    /**
     * Gets the average pixel equivalent of one millimeter for this graphics
     * context
     *
     * @return The equivalent value in pixels
     */
    public double getPixelsPerUnit() {
        return PIXELS_PER_INCH / MILLIMETERS_PER_INCH;
    }
}
