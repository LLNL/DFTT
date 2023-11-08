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

import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import javax.swing.JPanel;

import org.apache.batik.svggen.SVGGraphics2DIOException;

import llnl.gnem.dftt.core.gui.plotting.transforms.CoordinateTransform;

/**
 * JPlotContainer is the base class for all plot containers that host a single
 * plot (JBasicPlot). The principal function of this class is to manage the
 * layout of the JBasicPlot and to render its border and background
 */
public abstract class JPlotContainer extends JPanel {

    private static final double DEFAULT_OFFSET = 17.0;
    private static final double DEFAULT_WIDTH = 16.0;
    protected CoordinateTransform coordTransform;
    protected double HorizontalOffset;
    // Distance in units from Canvas left edge to drawing area
    protected double VerticalOffset;
    // Distance in units from Canvas top edge to top of drawing area
    protected double BoxHeight;
    // Drawing area in drawing units
    protected double BoxWidth;
    // Drawing area in drawing units
    protected double borderWidth;
    // Border width is set independently, and is in millimeters
    // You can also access the drawing region dimensions in pixels. Note that
    // these values will typically change every time a form is resized, so they
    // must be used immediately after being obtained.
    protected int left;
    protected int top;
    protected int width;
    protected int height;
    protected Title title = null;
    protected DrawingUnits unitsMgr = null;
    protected Graphics ActiveGraphics;
    protected DrawingRegion plotRegion = null;
    protected DrawingRegion plotBorder = null;
    protected boolean showBorder;
    private static boolean allowXor = true;
    protected boolean forceFullRender = false;
    private boolean allowPlotHighlighting;

    public JPlotContainer(boolean allowPlotHighlighting) {
        ActiveGraphics = getGraphics();
        unitsMgr = new DrawingUnits();
        title = new Title();
        HorizontalOffset = DEFAULT_OFFSET;
        VerticalOffset = DEFAULT_OFFSET;
        borderWidth = DEFAULT_WIDTH;
        plotRegion = new DrawingRegion(allowPlotHighlighting);
        plotBorder = new DrawingRegion(allowPlotHighlighting);
        showBorder = true;
        this.allowPlotHighlighting = allowPlotHighlighting;
    }

    public abstract void Render(Graphics g);

    public abstract void Render(Graphics g, double HOffset, double VertOffset, double boxWidth, double boxHeight);

    public abstract void setUseClippingRegion(boolean value);

    public abstract boolean isUseClippingRegion();

    public abstract void scaleAllFonts(double scale);

    public boolean isAllowPlotHighlighting() {
        return allowPlotHighlighting;
    }

    public void setAllowPlotHighlighting(boolean allowPlotHighlighting) {
        this.allowPlotHighlighting = allowPlotHighlighting;
        plotRegion.setAllowPlotHighlighting(allowPlotHighlighting);
        plotBorder.setAllowPlotHighlighting(allowPlotHighlighting);
    }

    /**
     * Called by the graphics system when this component must be updated.
     *
     * @param g The graphics context on which to render the component.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Render(g);
    }

    /**
     * Gets the plotRegion of the JPlotContainer object. The PlotRegion is the
     * interior of the axis. This is a DrawingRegion object, and is used to
     * control how the interior and border are brushed and stroked.
     *
     * @return The plotRegion value
     */
    public DrawingRegion getPlotRegion() {
        return plotRegion;
    }

    public static void setAllowXor(boolean v) {
        allowXor = v;
    }

    public static boolean getAllowXor() {
        return allowXor;
    }

    public abstract void setPolyLineUsage(boolean value);

    /**
     * Gets the visible attribute of the JPlotContainer object. By default, a
     * JPlotContainer is visible, meaning that when it is rendered, the Canvas
     * gets updated. However, it can be set to be invisible.
     *
     * @return The visible value
     */
    public boolean getVisible() {
        return isVisible();
    }

    /**
     * Gets the current graphics context of the JPlotContainer object
     *
     * @return The active Graphics value
     */
    public Graphics getActiveGraphics() {
        return ActiveGraphics;
    }

    /**
     * Gets the plotBorder of the JPlotContainer object. This DrawingRegion
     * controls the brushing and stroking of the plot margin, the region outside
     * of the plot interior containing the axes with their annotations and the
     * plot title.
     *
     * @return The plotBorder value
     */
    public DrawingRegion getPlotBorder() {
        return plotBorder;
    }

    /**
     * Gets the title of the JPlotContainer object. The title is an object that
     * contains a String to be displayed at the top of the plot and a set of
     * methods controlling how that String is displayed.
     *
     * @return The title value
     */
    public Title getTitle() {
        return title;
    }

    /**
     * Sets the horizontal Offset of the JPlotContainer object. This is the
     * distance in millimeters from the left edge of the container of the
     * JPlotContainer to the left edge of the JPlotContainer left margin.
     *
     * @param v The new horizontal Offset value.
     */
    public void setHorizontalOffset(double v) {
        HorizontalOffset = v;
    }

    /**
     * Sets the verticalOffset attribute of the JPlotContainer object. This is
     * the distance in millimeters from the top of the container holding the
     * JPlotContainer to the top of the top margin of the JPlotContainer.
     *
     * @param v The new verticalOffset value
     */
    public void setVerticalOffset(double v) {
        VerticalOffset = v;
    }

    /**
     * Sets the height of the plot interior in millimeters.
     *
     * @param v The new box Height value
     */
    public void setBoxHeight(double v) {
        BoxHeight = v;
    }

    /**
     * Sets the width of the plot interior in millimeters
     *
     * @param v The new box Width value
     */
    public void setBoxWidth(double v) {
        BoxWidth = v;
    }

    /**
     * Gets the offset of the left edge of the plot interior in pixels relative
     * to the left edge of the graphics context.
     *
     * @return The plotLeft value
     */
    public int getPlotLeft() {
        return left;
    }

    /**
     * Gets the offset from the top of the graphics context to the top of the
     * plot interior in pixels.
     *
     * @return The plotTop value
     */
    public int getPlotTop() {
        return top;
    }

    /**
     * Gets the width of the plot interior in pixels
     *
     * @return The plotWidth value
     */
    public int getPlotWidth() {
        return width;
    }

    /**
     * Gets the height of the plot interior in pixels.
     *
     * @return The plotHeight value
     */
    public int getPlotHeight() {
        return height;
    }

    /**
     * Gets the width of the plot border in millimeters
     *
     * @return The borderWidth value
     */
    public double getBorderWidth() {
        return borderWidth;
    }

    /**
     * Sets the width (in mm) of the plot border
     *
     * @param v The new borderWidth value
     */
    public void setBorderWidth(double v) {
        borderWidth = v;
    }

    //
    /**
     * Sets the showBorder attribute of the JPlotContainer object.
     * JPlotContainer plots are, by default drawn with a border around the plot
     * region. The border can be filled with a color and pattern and can include
     * a bounding rectangle. To control whether the border is drawn or not, use
     * the ShowBorder method.
     *
     * @param v The new showBorder value
     */
    public void setShowBorder(boolean v) {
        showBorder = v;
    }

    /**
     * Gets the units manager object of the JPlotContainer object. The units
     * manager maps millimeters topixel values, and is used for laying out parts
     * of the plot. End users of JPlotContainer should have no need to access
     * the Units Manager.
     *
     * @return The unitsMgr value
     */
    public DrawingUnits getUnitsMgr() {
        return unitsMgr;
    }

    public CoordinateTransform getCoordinateTransform() {
        return coordTransform;
    }

    public void setCoordinateTransform(CoordinateTransform transform) {
        coordTransform = transform;
    }

    public boolean isForceFullRender() {
        return forceFullRender;
    }

    public void setForceFullRender(boolean forceFullRender) {
        this.forceFullRender = forceFullRender;
    }

    public void exportSVG(String filename)
            throws UnsupportedEncodingException, FileNotFoundException, SVGGraphics2DIOException {
        exportSVG(new File(filename));
    }

    public void exportSVG(File file)
            throws UnsupportedEncodingException, FileNotFoundException, SVGGraphics2DIOException {
        PlotPrinter.getInstance().exportSVG(this, file);
    }

    public static void printCurrentPlot(final JPlotContainer gui) {
        printCurrentPlot(gui, false);
    }

    public static void printCurrentPlot(final JPlotContainer component, boolean printImmediate) {
        PlotPrinter.getInstance().printCurrentPlot(component, printImmediate);
    }

    public void exportPlot() {
        exportSVG();
    }

    public void exportSVG() {
        PlotPrinter.getInstance().exportSVG(this);
    }

    public void print() {
        printCurrentPlot(this);
    }

    public void print(boolean printImmediate) {
        printCurrentPlot(this, printImmediate);
    }

    public static void printAllPlots(Collection<? extends JPlotContainer> plots) {
        PlotPrinter.getInstance().printAllPlots(plots);
    }
}