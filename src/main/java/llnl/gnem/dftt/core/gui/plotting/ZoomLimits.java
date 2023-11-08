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

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * Class containing the axis limits for a particular stage of zooming for a
 * single JSubplot.
 *
 * @author Doug Dodge
 */
public class ZoomLimits {
    /**
     * Constructor for the ZoomLimits object
     *
     * @param xmin Minimum limit for the X-axis
     * @param xmax Maximum limit for the X-axis
     * @param ymin Minimum limit for the Y-axis
     * @param ymax Maximum limit for the Y-axis
     */
    public ZoomLimits( double xmin, double xmax, double ymin, double ymax )
    {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }

    public ZoomLimits(Limits xlimits, Limits ylimits)
    {
        xmin = xlimits.getMin();
        xmax = xlimits.getMax();
        ymin = ylimits.getMin();
        ymax = ylimits.getMax();
    }

    @Override
    public String toString() {
        return "ZoomLimits{" + "xmin=" + xmin + ", xmax=" + xmax + ", ymin=" + ymin + ", ymax=" + ymax + '}';
    }

    /**
     * Copy Constructor for the ZoomLimits object
     *
     * @param orig ZoomLimits object to copy
     */
    public ZoomLimits( ZoomLimits orig )
    {
        this.xmin = orig.xmin;
        this.xmax = orig.xmax;
        this.ymin = orig.ymin;
        this.ymax = orig.ymax;
    }

    /**
     * Minimum limit for the X-axis
     */
    public double xmin;
    /**
     * Maximum limit for the X-axis
     */
    public double xmax;
    /**
     * Minimum limit for the Y-axis
     */
    public double ymin;
    /**
     * Maximum limit for the Y-axis
     */
    public double ymax;
}


