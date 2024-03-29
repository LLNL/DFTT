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

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import llnl.gnem.dftt.core.gui.plotting.AxisScale;
import llnl.gnem.dftt.core.gui.plotting.HorizAlignment;
import llnl.gnem.dftt.core.gui.plotting.TickDir;
import llnl.gnem.dftt.core.gui.plotting.TickLabel;
import llnl.gnem.dftt.core.gui.plotting.TickMetrics.LinearTickMetrics;
import llnl.gnem.dftt.core.gui.plotting.TickMetrics.LogTickMetrics;

/**
 * Base class for axes contained in a JSubPlot.
 *
 * @author Doug Dodge
 */
public abstract class PlotAxis {

    private final static double MIN_RESOLVABLE_RANGE_FRACTION = 1.0 / 1000000;

    /**
     * Information about the tick to be rendered on this axis.
     */
    protected TickData ticks;
    /**
     * Information about the label associated with this axis.
     */
    protected LabelData label;
    /**
     * The pen color of this axis.
     */
    protected Color axisColor;
    /**
     * The pen width used to render this axis.
     */
    protected int axisPenWidth;
    /**
     * Controls whether this axis will be rendered.
     */
    protected boolean visible;
    /**
     * Description of the Field
     */
    protected boolean fullyDecorateAxis;
    /**
     * Description of the Field
     */
    protected double minFullyDecoratedAxisLength;

    /**
     * Constructor for the PlotAxis object
     */
    public PlotAxis() {
        ticks = new TickData();
        label = new LabelData();

        axisColor = Color.black;
        axisPenWidth = 1;
        visible = true;
        fullyDecorateAxis = false;
        minFullyDecoratedAxisLength = 25.0;
    }
    // ---------------------------------------------------------------------------

    /**
     * Gets the number of minor ticks that will be rendered by this axis
     *
     * @return The number of minor ticks
     */
    public int getNumMinorTicks() {
        return ticks.getNumMinor();
    }

    /**
     * Sets the numMinorTicks attribute of the PlotAxis object
     *
     * @param v The new numMinorTicks value
     */
    public void setNumMinorTicks(int v) {
        int val = v >= 0 ? v : 0;
        ticks.setNumMinor(val);
    }

    /**
     * Gets the number of major ticks that will be rendered by this axis
     *
     * @return The number of major ticks
     */
    public double getMajorTickLen() {
        return ticks.getMajorLen();
    }

    /**
     * Sets the majorTickLen attribute of the PlotAxis object
     *
     * @param v The new majorTickLen value
     */
    public void setMajorTickLen(double v) {
        double val = v >= 0 ? v : 0;
        ticks.setMajorLen(val);
    }

    /**
     * Gets the length in millimeters of the minor ticks in this axis
     *
     * @return The minor tick length (mm)
     */
    public double getMinorTickLen() {
        return ticks.getMinorLen();
    }

    /**
     * Sets the minorTickLen attribute of the PlotAxis object
     *
     * @param v The new minorTickLen value
     */
    public void setMinorTickLen(double v) {
        double val = v >= 0 ? v : 0;
        ticks.setMinorLen(val);
    }

    /**
     * Gets the direction (in/out) of ticks drawn on this axis.
     *
     * @return The tick Direction value
     */
    public TickDir getTickDirection() {
        return ticks.getDir();
    }

    /**
     * Sets the tickDirection attribute of the PlotAxis object
     *
     * @param v The new tickDirection value
     */
    public void setTickDirection(TickDir v) {
        ticks.setDir(v);
    }

    /**
     * Gets the visibility of ticks in this axis
     *
     * @return The visibility value
     */
    public boolean getTicksVisible() {
        return ticks.isVisible();
    }

    /**
     * Sets the ticksVisible attribute of the PlotAxis object
     *
     * @param v The new ticksVisible value
     */
    public void setTicksVisible(boolean v) {
        ticks.setVisible(v);
    }

    /**
     *
     * @return The label Font
     */
    public Font getTickFont() {
        return ticks.getFont();
    }

    /**
     *
     * @param font
     */
    public void setTickFont(Font font) {
        ticks.setFont(font);
    }

    /**
     * Gets the tickFontName attribute of the PlotAxis object
     *
     * @return The tickFontName value
     */
    public String getTickFontName() {
        return ticks.getFontName();
    }

    /**
     * Sets the tickFontName attribute of the PlotAxis object
     *
     * @param v The new tickFontName value
     */
    public void setTickFontName(String v) {
        ticks.setFontName(v);
    }

    /**
     * Gets the tickFontColor attribute of the PlotAxis object
     *
     * @return The tickFontColor value
     */
    public Color getTickFontColor() {
        return ticks.getFontColor();
    }

    /**
     * Sets the tickFontColor attribute of the PlotAxis object
     *
     * @param v The new tickFontColor value
     */
    public void setTickFontColor(Color v) {
        ticks.setFontColor(v);
    }

    /**
     * Gets the tickFontSize attribute of the PlotAxis object
     *
     * @return The tickFontSize value
     */
    public int getTickFontSize() {
        return ticks.getFontSize();
    }

    /**
     * Sets the tickFontSize attribute of the PlotAxis object
     *
     * @param v The new tickFontSize value
     */
    public void setTickFontSize(int v) {
        ticks.setFontSize(v);
    }

    /**
     * Gets the labelColor attribute of the PlotAxis object
     *
     * @return The labelColor value
     */
    public Color getLabelColor() {
        return label.getColor();
    }

    /**
     * Sets the labelColor attribute of the PlotAxis object
     *
     * @param v The new labelColor value
     */
    public void setLabelColor(Color v) {
        label.setColor(v);
    }

    /**
     *
     * @return The label Font
     */
    public Font getLabelFont() {
        return label.getFont();
    }

    /**
     *
     * @param font
     */
    public void setLabelFont(Font font) {
        label.setFont(font);
    }

    public int getLabelFontSize() {
        return label.getSize();
    }

    /**
     *
     * @param size
     */
    public void setLabelFontSize(int size) {
        label.setSize(size);
    }

    /**
     * Gets the labelOffset attribute of the PlotAxis object
     *
     * @return The labelOffset value
     */
    public double getLabelOffset() {
        return label.getOffset();
    }

    /**
     * Sets the labelOffset attribute of the PlotAxis object
     *
     * @param v The new labelOffset value
     */
    public void setLabelOffset(double v) {
        label.setOffset(v);
    }

    /**
     * Gets the labelText attribute of the PlotAxis object
     *
     * @return The labelText value
     */
    public String getLabelText() {
        return label.getText();
    }

    /**
     * Sets the labelText attribute of the PlotAxis object
     *
     * @param v The new labelText value
     */
    public void setLabelText(String v) {
        label.setText(v);
    }

    /**
     * Gets the labelVisible attribute of the PlotAxis object
     *
     * @return The labelVisible value
     */
    public boolean getLabelVisible() {
        return label.isVisible();
    }

    /**
     * Sets the labelVisible attribute of the PlotAxis object
     *
     * @param v The new labelVisible value
     */
    public void setLabelVisible(boolean v) {
        label.setVisible(visible);
    }

    /**
     * Gets the axisColor attribute of the PlotAxis object
     *
     * @return The axisColor value
     */
    public Color getAxisColor() {
        return axisColor;
    }

    /**
     * Sets the axisColor attribute of the PlotAxis object
     *
     * @param v The new axisColor value
     */
    public void setAxisColor(Color v) {
        axisColor = v;
    }

    /**
     * Gets the axisPenWidth attribute of the PlotAxis object
     *
     * @return The axisPenWidth value
     */
    public int getAxisPenWidth() {
        return axisPenWidth;
    }

    /**
     * Sets the axisPenWidth attribute of the PlotAxis object
     *
     * @param v The new axisPenWidth value
     */
    public void setAxisPenWidth(int v) {
        axisPenWidth = v > 0 ? v : 1;
    }

    /**
     * Gets the visible attribute of the PlotAxis object
     *
     * @return The visible value
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * Sets the visible attribute of the PlotAxis object
     *
     * @param v The new visible value
     */
    public void setVisible(boolean v) {
        visible = v;
    }

    /**
     * Gets suitable axis minimum, maximum, and increment values based on the
     * input range. Converted from C++ version by Doug Dodge. Copyright (c)
     * 2000, Original version by Michael P.D. Bramley. Permission is granted to
     * use this code without restriction as long as this copyright notice
     * appears in all source files. Author: Michael P.D. Bramley Synopsis:
     * Function to define axis based on range of data using properties of
     * decimal place-value system and linearity of axis.
     *
     * @param minIn The minimum data value
     * @param maxIn The maximum data value.
     * @return A TickMetrics object holding the suggested axis minimum, maximum,
     *         and increment values.
     */
    public static LinearTickMetrics defineAxis(double minIn, double maxIn) {
        // define local variables...
        if (minIn > maxIn) {
            double tmp = minIn;
            minIn = maxIn;
            maxIn = tmp;
        }
        double min = minIn;
        double max = maxIn;
        double inc;
        double testInc;

        double eps = MIN_RESOLVABLE_RANGE_FRACTION;
        double range = max - min;
        // range of data

        int i = 0;
        // counter

        // don't create problems -- solve them
        if (range < 0) {
            return new LinearTickMetrics(0.0, 0.0, 0.0, true);
        } // handle special case of repeated values
        else if (range == 0) {
            min = Math.ceil(max) - 1;
            max = min + 1;
            inc = 1;
            return new LinearTickMetrics(min, max, inc, true);
        } else if (range <= eps) {
            return new LinearTickMetrics(min, max, range / 10, true);
        }

        // compute candidate for increment
        testInc = Math.pow(10.0, Math.ceil(Math.log10(range / 10)));

        // establish maximum scale value...
        double testMax = ((long) (max / testInc)) * testInc;
        if (testMax < max) {
            testMax += testInc;
        }

        // establish minimum scale value...
        double testMin = testMax;

        do {
            ++i;
            testMin -= testInc;
            if (Math.abs(testMin - min) < eps) {
                break;
            }
        } while (testMin >= min);
        // subtracting small values can screw up the scale limits,
        // eg: if DefineAxis is called with (min,max)=(0.01, 0.1),
        // then the calculated scale is 1.0408E17 TO 0.05 BY 0.01.
        // the following if statement corrects for this...

        if (Math.abs(testMin) < range * MIN_RESOLVABLE_RANGE_FRACTION) {
            testMin = 0;
        }

        // adjust for too few tick marks
        if (i < 6) {
            testInc /= 2;
            if ((testMin + testInc) <= min) {
                testMin += testInc;
            }
            if ((testMax - testInc) >= max) {
                testMax -= testInc;
            }
        }

        // pass back axis definition to caller
        min = testMin;
        max = testMax;
        inc = testInc;
        return new LinearTickMetrics(min, max, inc, true);
    }

    public static LinearTickMetrics defineAxis(double min, double max, boolean fullyDecorate) {
        return defineAxis(min, max);
    }

    public static LogTickMetrics defineLogAxis(double minIn, double maxIn, boolean fullyDecorate) {
        double logMin = Math.log10(minIn);
        double logMax = Math.log10(maxIn);
        int displayMin = (int) logMin;
        int displayMax = (int) logMax;
        if (displayMin > logMin) {
            --displayMin;
        }
        if (displayMax < logMax) {
            ++displayMax;
        }

        return new LogTickMetrics(displayMin, displayMax, fullyDecorate);
    }

    /**
     * render the ticks for this axis.
     *
     * @param g     The graphics context.
     * @param min   The minimum value of the axis.
     * @param max   The maximum value of the axis.
     * @param Scale Controls whether the axis has a linear or log(10) scale.
     */
    protected void renderTicks(Graphics g, double min, double max, AxisScale Scale) {
        if (!ticks.isVisible()) {
            return;
        }
        if (Scale == AxisScale.LINEAR) {
            renderLinearTicks(g, min, max);
        } else {
            renderLogTicks(g, min, max);
        }
    }
    // ---------------------------------------------------------------------------

    /**
     * render ticks for the linear scale case
     *
     * @param g     The graphics context
     * @param minIn The minimum value of the axis
     * @param maxIn The maximum value of the axis
     */
    protected void renderLinearTicks(Graphics g, double minIn, double maxIn) {
        double min = minIn;
        double max = maxIn;
        double displayMin = min;
        double displayMax = max;
        LinearTickMetrics ticks = defineAxis(min, max, fullyDecorateAxis);
        while (ticks.hasNext()) {
            double val = ticks.getNext();
            if (fullyDecorateAxis) {
                double inc2 = ticks.getIncrement() / (getNumMinorTicks() + 1);
                for (int j = 1; j <= getNumMinorTicks(); ++j) {
                    double v = val + j * inc2;
                    if (v >= displayMin && v <= displayMax) {
                        renderTick(g, v, new TickLabel(), false, HorizAlignment.CENTER);
                    }
                    // render minor tick
                }
            }
            if (val >= displayMin && val <= displayMax) {
                // Prevent the zero tick label from being displayed as a very small exponential
                // number.
                double test = Math.abs(val);
                if (test < ticks.getIncrement() / 1000.0) {
                    val = 0;
                }
                String tmp = formatValue(val);
                renderTick(g, val, new TickLabel(tmp), true, HorizAlignment.CENTER);
                // render major tick
            }
        }
    }

    /**
     * render ticks for the log(10) case
     *
     * @param g     The graphics context
     * @param minIn The minimum value of the axis
     * @param max   The maximum value of the axis
     */
    protected void renderLogTicks(Graphics g, double minIn, double max) {
        if (max <= 0) {
            throw new IllegalStateException("Max is <= 0 and Xscale is logarithmic!");
        }

        double min = minIn;
        if (min <= 0) {
            min = max / 100;
        }
        LogTickMetrics ticks = defineLogAxis(min, max, fullyDecorateAxis);
        while (ticks.hasNext()) {
            double val = ticks.getNext();
            if (fullyDecorateAxis) {
                for (int j = 2; j <= 9; ++j) {
                    double v = val * j;
                    if (v >= min && v <= max) {
                        renderTick(g, v, new TickLabel(), false, HorizAlignment.CENTER);
                    }
                    // render minor tick
                }
            }
            if (val >= min && val <= max) {
                String tmp = formatValue(val);
                renderTick(g, val, new TickLabel(tmp), true, HorizAlignment.CENTER);
                // render major tick
            }
        }
    }
//---------------------------------------------------------------------------

    private static String formatValue(double val) {
        String tmp = String.format("%.6G", val);
        int idxE = tmp.indexOf('E');
        if (idxE > 0) {
            String part2 = tmp.substring(idxE);
            int idxLastChr = idxE - 1;
            int idxDot = tmp.indexOf('.');
            if (idxDot > 0) {
                while (idxLastChr > idxDot) {
                    if (tmp.charAt(idxLastChr) != '0') {
                        break;
                    }
                    --idxLastChr;
                }
                if (tmp.charAt(idxLastChr) == '.') {
                    ++idxLastChr;
                }
                tmp = tmp.substring(0, idxLastChr + 1) + part2;
            }

        } else {
            int idxLastChr = tmp.length() - 1;
            int idxDot = tmp.indexOf('.');
            if (idxDot > 0) {
                while (idxLastChr > idxDot) {
                    if (tmp.charAt(idxLastChr) != '0') {
                        break;
                    }
                    --idxLastChr;
                }
                if (tmp.charAt(idxLastChr) == '.') {
                    --idxLastChr;
                }
                tmp = tmp.substring(0, idxLastChr + 1);
            }
        }
        return tmp;
    }
//---------------------------------------------------------------------------

    /**
     * render a single tick. This action is performed by derived classes because
     * some of the specifics depend on the nature of the derived class
     *
     * @param g       The graphics context
     * @param val     The axis value at the position of the tick
     * @param label   A label that may be associated with the tick
     * @param isMajor true if this is a major tick mark
     */
    protected abstract void renderTick(Graphics g, double val, TickLabel label, boolean isMajor,
            HorizAlignment alignment);

}
