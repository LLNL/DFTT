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
package llnl.gnem.dftt.core.gui.plotting.jgeographicplot;

import java.awt.Cursor;
import java.awt.Graphics;
import java.util.Observer;
import java.util.Stack;

import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;
import llnl.gnem.dftt.core.gui.plotting.JPlotContainer;
import llnl.gnem.dftt.core.gui.plotting.MouseMode;
import llnl.gnem.dftt.core.gui.plotting.transforms.AzimuthalEqualAreaTransform;

/**
 * JGeographicPlot is a container for JBasicPlot specializations that draw maps.
 * JGeographic plot contains a single instance of a JBasicPlot specialization.
 * Currently,
 * that instance has been set to be a JAzimuthalPlot, but when other types of
 * geographic
 * plots have been created, this will be generalized.
 * Will not inspect for: IfStatementWithTooManyBranches
 */
public final class JGeographicPlot extends JPlotContainer {

    private final JAzimuthalPlot plot;
    private final JAzimuthalPlotMouseListener mouseListener;
    private final Stack<MouseMode> mouseModes;
    private final Cursor defaultCursor;
    private final Cursor handCursor;
    private final Cursor crossCursor;
    private MouseMode mouseMode;

    public JGeographicPlot() {
        super(true);
        coordTransform = new AzimuthalEqualAreaTransform(0, 0);
        plot = new JAzimuthalPlot(this);

        mouseListener = new JAzimuthalPlotMouseListener(this);
        mouseMode = MouseMode.SELECT_ZOOM;
        defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        handCursor = new Cursor(Cursor.HAND_CURSOR);
        crossCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        mouseModes = new Stack<MouseMode>();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addKeyListener(mouseListener);
        addMouseWheelListener(mouseListener);
    }

    /**
     * Gets the contained JBasicPlot.
     *
     * @return The JBasicPlot held by this container.
     */
    public JBasicPlot getPlot() {
        return plot;
    }

    @Override
    public void setPolyLineUsage(boolean value) {
        plot.setPolyLineUsage(value);
    }

    public void setRenderGridLines(boolean render) {
        plot.setRenderGridLines(render);
    }

    public boolean isRenderGridLines() {
        return plot.isRenderGridLines();
    }

    /**
     * render this component to the supplied graphics context. To render on the
     * screen, call the render() method. To render on some other Graphics, call
     * the render method that takes a Graphics reference and the 4 position
     * values. The position values are all in Drawing Units, e.g. mm.
     *
     * @param g The graphics context on which to render this object
     */
    @Override
    public void Render(Graphics g) {
        ActiveGraphics = g;
        BoxHeight = unitsMgr.getVertPixelsToUnits(getHeight()) - 2 * VerticalOffset;
        BoxWidth = unitsMgr.getHorizPixelsToUnits(getWidth()) - 2 * HorizontalOffset;
        Render(ActiveGraphics, HorizontalOffset, VerticalOffset, BoxWidth, BoxHeight);
    }

    /**
     * render to some graphics context, supplying positioning parameters
     * appropriate for the device on which rendering will occur.
     *
     * @param g          The graphics context
     * @param HOffset    The horizontal offset in mm
     * @param VertOffset The vertical offset in mm
     * @param boxWidth   The width of the plot region in mm
     * @param boxHeight  The height of the plot region in mm
     */
    @Override
    public void Render(Graphics g, double HOffset, double VertOffset, double boxWidth, double boxHeight) {
        if (!getVisible() || g == null) {
            return;
        }
        left = unitsMgr.getHorizUnitsToPixels(HOffset);
        top = unitsMgr.getVertUnitsToPixels(VertOffset);
        width = unitsMgr.getHorizUnitsToPixels(boxWidth);
        height = unitsMgr.getVertUnitsToPixels(boxHeight);
        int bWidth = unitsMgr.getHorizUnitsToPixels(borderWidth);

        // Color the border region and box
        if (showBorder) {
            plotBorder.setRect(left - bWidth, top - bWidth, height + 2 * bWidth, width + 2 * bWidth);
            plotBorder.render(g);
        }

        // Color the plotting region and box
        plotRegion.setRect(left, top, height, width);
        plotRegion.render(g);

        // Don't render interior if it cannot show up.
        if (height < 2 || width < 2) {
            return;
        }

        // render the plot title
        title.Render(g, left, top, width, unitsMgr);

        plot.Render(g);
    }

    public void addPlotObjectObserver(Observer o) {
        getMouseListener().addPlotObjectObserver(o);
    }

    public JAzimuthalPlotMouseListener getMouseListener() {
        return mouseListener;
    }

    public void setMouseMode(MouseMode newMode) {
        mouseModes.push(getMouseMode());
        mouseMode = newMode;
        setCursorForMouseModeChange();
    }

    private void setCursorForMouseModeChange() {
        if (getMouseMode() == MouseMode.PAN) {
            setCursor(handCursor);
        } else if (getMouseMode() == MouseMode.SELECT_REGION) {
            setCursor(crossCursor);
        } else if (getMouseMode() == MouseMode.CREATE_PICK) {
            setCursor(crossCursor);
        } else {
            setCursor(defaultCursor);
        }
    }

    public void restorePreviousMouseMode() {
        if (!mouseModes.empty()) {
            mouseMode = mouseModes.pop();
            setCursorForMouseModeChange();
        }
    }

    public MouseMode getMouseMode() {
        return mouseMode;
    }

    @Override
    public void setUseClippingRegion(boolean value) {
        //
    }

    @Override
    public boolean isUseClippingRegion() {
        return true;
    }

    @Override
    public void scaleAllFonts(double scale) {
        //
    }
}
