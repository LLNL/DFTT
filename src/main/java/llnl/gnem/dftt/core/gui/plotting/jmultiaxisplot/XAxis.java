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
package llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.NumberFormat;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */
import llnl.gnem.dftt.core.gui.plotting.DrawingUnits;
import llnl.gnem.dftt.core.gui.plotting.HorizAlignment;
import llnl.gnem.dftt.core.gui.plotting.TickDir;
import llnl.gnem.dftt.core.gui.plotting.TickLabel;
import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.dftt.core.gui.plotting.transforms.CoordinateTransform;

/**
 * A class that renders a horizontal X-axis with either linear or log(10) scale.
 *
 * @author Doug Dodge
 */
public class XAxis extends PlotAxis {

    private double ypos;
    // Position in real-world units up from bottom of axis
    private int MajorTickEndPixel;
    private int MinorTickEndPixel;
    private int yPosPixel;
    private int tickLabelTop;
    protected final JMultiAxisPlot plot;
    private double xmin;
    private double xmax;
    private TickScaleFunc tickScaleFunction;

    /**
     * Constructor for the XAxis object
     *
     * @param plot
     *             The JMultiAxisPlot containing this x-axis. All subplots in a
     *             JMultiAxisPlot have common X-axis values. Although individual
     *             subplots may have different X-axis limits, at a given
     *             horizontal offset from a JMultiAxisPlot vertical border, all
     *             subplots will have the same X-value.
     */
    public XAxis(JMultiAxisPlot plot) {
        this.plot = plot;
        ypos = 0.0;
    }

    /**
     * Gets the offset in millimeters of the X-axis from the bottom of the
     * Y-axis.
     *
     * @return The offset in millimeters
     */
    public double getYpos() {
        return ypos;
    }

    /**
     * Shifts the axis up or down. An XAxis is always aligned horizontally so
     * that it extends from one end of the plotting area to the other. However,
     * it can be shifted up and down. The YPosition is the vertical position in
     * Drawing units e.g. mm from the bottom of the Y-axis.
     *
     * @param v
     *          The shift in millimeters from the bottom of the Y-axis.
     */
    public void setYpos(double v) {
        ypos = v;
    }

    /**
     * Gets the min attribute of the XAxis object
     *
     * @return The min value
     */
    public double getMin() {
        return xmin;
    }

    /**
     * Sets the min attribute of the XAxis object
     *
     * @param v
     *          The new min value
     */
    public void setMin(double v) {
        xmin = v;
    }

    /**
     * Gets the max attribute of the XAxis object
     *
     * @return The max value
     */
    public double getMax() {
        return xmax;
    }

    /**
     * Sets the max attribute of the XAxis object
     *
     * @param v
     *          The new max value
     */
    public void setMax(double v) {
        xmax = v;
    }

    /**
     * render this X-axis to the supplied graphics context
     *
     * @param g
     *                   The Graphics context on which to render the axis
     * @param LeftMargin
     *                   The pixel value of the left edge of the X-axis.
     * @param TopMargin
     *                   The pixel value of the top end of the Y-axis
     * @param BoxHeight
     *                   The length of the Y-axis in pixels
     * @param BoxWidth
     *                   The width of the X-axis in pixels.
     */
    public void Render(Graphics g, int LeftMargin, int TopMargin, int BoxHeight, int BoxWidth) {
        if (!visible) {
            return;
        }
        Graphics2D g2d = (Graphics2D) g;

        // Set to copy over pixels
        g2d.setPaintMode();
        g2d.setColor(axisColor);
        g2d.setStroke(new BasicStroke(axisPenWidth));

        // Draw the axis line...
        DrawingUnits du = plot.getUnitsMgr();
        yPosPixel = TopMargin + BoxHeight - du.getVertUnitsToPixels(ypos);
        g2d.drawLine(LeftMargin, yPosPixel, LeftMargin + BoxWidth, yPosPixel);
        if (ticks.getDir() == TickDir.IN) {
            MajorTickEndPixel = yPosPixel - du.getVertUnitsToPixels(ticks.getMajorLen());
            MinorTickEndPixel = yPosPixel - du.getVertUnitsToPixels(ticks.getMinorLen());
            tickLabelTop = yPosPixel + du.getVertUnitsToPixels(1.0);
        } else {
            MajorTickEndPixel = yPosPixel + du.getVertUnitsToPixels(ticks.getMajorLen());
            MinorTickEndPixel = yPosPixel + du.getVertUnitsToPixels(ticks.getMinorLen());
            tickLabelTop = MajorTickEndPixel + du.getVertUnitsToPixels(1.0);
        }

        // Save old font and color
        Font oldFont = g2d.getFont();
        Color oldColor = g2d.getColor();

        // Create new font and color
        g2d.setColor(getTickFontColor());
        g2d.setFont(new Font(getTickFontName(), Font.PLAIN, getTickFontSize()));

        // Only draw all tick information if axis is over a minimum length
        fullyDecorateAxis = du.getHorizUnitsToPixels(minFullyDecoratedAxisLength) <= BoxWidth;
        CoordinateTransform ct = plot.getCoordinateTransform();
        renderTicks(g2d, xmin, xmax, ct.getXScale());

        if (getLabelVisible() && fullyDecorateAxis) {
            drawAxisLabel(g2d, LeftMargin, BoxWidth);
        }

        // restore old values
        g2d.setColor(oldColor);
        g2d.setFont(oldFont);
    }
    // ---------------------------------------------------------------------------

    /**
     * Produce a string for a tick label
     *
     * @param value
     *              Description of the Parameter
     * @return The valueString value
     */
    String getValueString(double value) {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(5);
        return f.format(value);
    }

    /**
     * render a single tick (possibly rendering a tick label as well).
     *
     * @param g
     *                  The graphics context
     * @param val
     *                  The X-value of the tick
     * @param label
     *                  The label associated with the tick
     * @param isMajor
     *                  true if this is a major tick.
     * @param alignment
     */
    @Override
    protected void renderTick(Graphics g, double val, TickLabel label, boolean isMajor, HorizAlignment alignment) {
        Graphics2D g2d = (Graphics2D) g;
        CoordinateTransform ct = plot.getCoordinateTransform();
        Coordinate c = new Coordinate(0.0, 0.0, val, 0.0);

        ct.WorldToPlot(c);
        int xPixelVal = (int) c.getX();
        if (isMajor) {
            g2d.drawLine(xPixelVal, yPosPixel, xPixelVal, MajorTickEndPixel);
            if (label.hasLabel1()) {
                FontMetrics fm = g2d.getFontMetrics();
                int advance = fm.stringWidth(label.getLabel1());
                int pos = getTextXpos(xPixelVal, advance, alignment);
                String label1;
                if (tickScaleFunction != null) {
                    try {
                        label1 = tickScaleFunction.func(label.getLabel1());
                    } catch (Exception e) {
                        label1 = label.getLabel1();
                    }
                } else {
                    label1 = label.getLabel1();
                }
                g2d.drawString(label1, pos, tickLabelTop + fm.getMaxAscent());

            }
            if (label.hasLabel2()) {
                FontMetrics fm = g2d.getFontMetrics();
                int advance = fm.stringWidth(label.getLabel2());
                int pos = getTextXpos(xPixelVal, advance, alignment);
                if (pos > 250 || alignment == HorizAlignment.LEFT)// Need to
                                                                  // leave
                                                                  // room for
                                                                  // the axis
                                                                  // reference
                                                                  // time
                {
                    g2d.drawString(label.getLabel2(), pos, tickLabelTop + 2 * fm.getMaxAscent());
                }
            }
        } else {
            g2d.drawLine(xPixelVal, yPosPixel, xPixelVal, MinorTickEndPixel);
        }
    }
    // ---------------------------------------------------------------------------

    private int getTextXpos(int xPixelVal, int advance, HorizAlignment alignment) {
        switch (alignment) {
            case CENTER:
                return xPixelVal - advance / 2;
            case LEFT:
                return xPixelVal;
            case RIGHT:
                return xPixelVal - advance;
            default:
                return xPixelVal;
        }
    }

    private void drawAxisLabel(Graphics g, int LeftMargin, int BoxWidth) {
        if (getLabelText().length() < 1) {
            return;
        }
        Graphics2D g2d = (Graphics2D) g;
        int xpos = LeftMargin + BoxWidth / 2;
        int aYpos = yPosPixel + plot.getUnitsMgr().getVertUnitsToPixels(getLabelOffset());

        // Save old font and color
        Font oldFont = g2d.getFont();
        Color oldColor = g2d.getColor();

        // Create new font and color
        g2d.setColor(getLabelColor());
        g2d.setFont(getLabelFont());

        // Layout and render text
        FontMetrics fm = g2d.getFontMetrics();
        int advance = fm.stringWidth(getLabelText());
        g2d.drawString(getLabelText(), xpos - advance / 2, aYpos + fm.getMaxAscent());

        // restore old values
        g2d.setColor(oldColor);
        g2d.setFont(oldFont);
    }
    // ---------------------------------------------------------------------------

    /**
     * @param tickScaleFunc
     */
    public void setTickScaleFunction(TickScaleFunc tickScaleFunc) {
        this.tickScaleFunction = tickScaleFunc;
    }
}
