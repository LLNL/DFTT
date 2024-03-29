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

import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;
import llnl.gnem.dftt.core.gui.plotting.XaxisDir;
import llnl.gnem.dftt.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.dftt.core.gui.plotting.transforms.CartesianTransform;
import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */
/**
 * A class that collaborates with VPickLine to display interactive error bars
 * that are associated with a single pick.
 *
 * @author Doug Dodge
 */
public class VPickLineErrorBar extends PlotObject {

    private double std;
    private final VPickLine associatedPick;
    private double BracketWidth;
    private double handleWidth;
    private final PickErrorDir dir;
    private Color color;
    private final Color selectedColor;
    private Color renderColor;
    private boolean showHandles;
    private boolean selected;

    /**
     * Gets the error value associated with the VPickLineErrorBar object. This
     * is the absolute value of the difference between the VPickLine data value
     * and the data value of one of the associated error bars.
     *
     * @return The error value
     */
    public double getStd() {
        return std;
    }

    /**
     * Sets the value of the error for the VPickLineErrorBar object. This method
     * does not cause the error bar to be re-rendered.
     *
     * @param v The new error value in real-world coordinates
     */
    public void setStd(double v) {
        std = v >= 0 ? v : 0;
        PickDataBridge wd = associatedPick.getDataBridge();
        if (wd != null) {
            wd.setDeltim(std, this);
        }
    }

    /**
     * Sets the bracket Width of the VPickLineErrorBar object. Each error bar
     * has an inward-facing bracket at top and bottom. The width of this bracket
     * is specified in millimeters.
     *
     * @param v The new bracket Width value in millimeters
     */
    public void setBracketWidth(double v) {
        BracketWidth = Math.min(v, 0.0);
    }

    /**
     * Sets the handle Width of the VPickLineErrorBar object. Each error bar has
     * an optional square handle that may make it easier to select with the
     * mouse. This method sets the size of a side of the squares in millimeters.
     *
     * @param v The new handle Width value in millimeters.
     */
    public void setHandleWidth(double v) {
        handleWidth = Math.min(v, 0.0);
    }

    /**
     * Controls whether the error-bar handle is displayed.
     *
     * @param v The new showHandles value
     */
    public void setShowHandles(boolean v) {
        showHandles = v;
    }

    /**
     * Constructor for the VPickLineErrorBar object
     *
     * @param associatedPick A reference to the VPickLine object associated with
     * this error bar
     * @param std The separation distance in real-world coordinates of this
     * error bar from the VPickLine.
     * @param dir The specification of which side of the VPickLine to place this
     * error bar.
     */
    public VPickLineErrorBar(VPickLine associatedPick, double std, PickErrorDir dir) {
        this.std = std;
        this.associatedPick = associatedPick;
        canDragY = false;
        canDragX = associatedPick.getDraggable();
        BracketWidth = 3.0;
        handleWidth = 2.0;
        color = associatedPick.getColor();
        selectedColor = Color.red;
        selected = false;
        renderColor = color;
        showHandles = true;
        visible = false;
        this.dir = dir;
    }

    /**
     * Sets the color of the VPickLineErrorBar object
     *
     * @param c The new color value
     */
    public void setColor(Color c) {
        color = c;
        renderColor = c;
    }

    /**
     * Change the position of this error bar. This method should only be called
     * by the mouse motion listener associated with the JMultiAxisPlot
     * containing this object. The method notifies any observers registered with
     * its PickDataBridge, so if called by an observer could result in infinite
     * recursion.
     *
     * @param axis The JBasicPlot that owns this error bar
     * @param graphics
     * @param dx The x-shift in real-world coordinates
     * @param dy The y-shift in real-world coordinates
     */
    @Override
    public void ChangePosition(JBasicPlot axis, Graphics graphics, double dx, double dy) {
        Graphics g = axis.getOwner().getGraphics();
        render(g, axis);
        CartesianTransform ct = (CartesianTransform) axis.getCoordinateTransform();
        if (dir == PickErrorDir.RIGHT) {
            if (ct.getXAxisDir() == XaxisDir.RIGHT) {
                std += dx;
            } else {
                std -= dx;
            }
        } else if (ct.getXAxisDir() == XaxisDir.RIGHT) {
            std -= dx;
        } else {
            std += dx;
        }
        if (std < 0) {
            std = 0;
        }
        associatedPick.UpdateOther(this, axis, g);
        PickDataBridge wd = associatedPick.getDataBridge();
        if (wd != null) {
            wd.setDeltim(std, this);
        }
        render(g, axis);
    }

    /**
     * Produce a String representation of this object
     *
     * @return The String representation
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Pick Error Bar: Std = ");
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(5);
        s.append(f.format(std));
        return s.toString();
    }

    /**
     * render this error bar to the supplied graphics context.
     *
     * @param g The graphics context on which to render the error bar
     * @param axis The JBasicPlot that owns this error bar.
     */
    @Override
    public void render(Graphics g, JBasicPlot axis) {
        if (!isVisible() || !owner.getCanDisplay()) {
            return;
        }
        if (JMultiAxisPlot.getAllowXor()) {
            g.setXORMode(Color.white);
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.clip(owner.getPlotRegion().getRect());

        // Remove any pre-existing regions before creating new...
        region.clear();
        g2d.setColor(renderColor);
        g2d.setStroke(new BasicStroke(associatedPick.getWidth()));

        // Get the X-position of this bracket
        CartesianTransform ct = (CartesianTransform) axis.getCoordinateTransform();
        double Xpos;
        if (dir == PickErrorDir.RIGHT) {
            Xpos = associatedPick.getXval() + std;
            if (ct.getXAxisDir() == XaxisDir.LEFT) {
                Xpos -= 2 * std;
            }
        } else {
            Xpos = associatedPick.getXval() - std;
            if (ct.getXAxisDir() == XaxisDir.LEFT) {
                Xpos += 2 * std;
            }
        }
        Coordinate coord = new Coordinate(0.0, 0.0, Xpos, 0.0);
        ct.WorldToPlot(coord);
        int xpos = (int) coord.getX();

        // Now get the separation in pixels of the bracket from the pick. Use that
        // to make sure that the bracket legs do not extend past pick line
        coord.setWorldC1(associatedPick.getXval());
        ct.WorldToPlot(coord);
        int Xcenter = (int) coord.getX();
        int separation = Math.abs(xpos - Xcenter);
        int bwInt = axis.getUnitsMgr().getHorizUnitsToPixels(BracketWidth);
        bwInt = Math.min(bwInt, separation);

        // Draw the bracket as long as there is some separation from the pick line.
        int bot = associatedPick.getLineBottom();
        int top = associatedPick.getLineTop();
        GeneralPath p = new GeneralPath();
        int offset;
        if (dir == PickErrorDir.RIGHT) {
            offset = -bwInt;
        } else {
            offset = bwInt;
        }
        p.moveTo(xpos + offset, bot);
        p.lineTo(xpos, bot);
        p.lineTo(xpos, top);
        p.lineTo(xpos + offset, top);
        if (separation > 0) {
            // Don't want to XOR out the pick line
            g2d.draw(p);
        }

        // Add to the region vector
        int tol = 3;
        addToRegion(new Rectangle2D.Double(xpos - tol, top, 2 * tol, bot - top));

        // Add a little handle for use when std = 0
        if (showHandles) {
            int hw = axis.getUnitsMgr().getHorizUnitsToPixels(handleWidth);
            int center = (top + bot) / 2;
            int rectLeft;
            if (dir == PickErrorDir.RIGHT) {
                rectLeft = xpos + 1;
            } else {
                rectLeft = xpos - hw - 1;
            }
            Rectangle rect = new Rectangle(rectLeft, center - hw / 2, hw, hw);
            g2d.fill(rect);
            addToRegion(rect);
        }
    }

    /**
     * Sets the selected state of the VPickLineErrorBar object. Basically, this
     * amounts to changing the color in which the object is rendered. This
     * method will cause a re-render of the object.
     *
     * @param selected The new selected value
     * @param g The graphics context on which this object is rendered.
     */
    @Override
    public void setSelected(boolean selected, Graphics g) {
        if (this.selected == selected) {
            return;
        }
        VPickLineErrorBar other = associatedPick.getOther(this);
        render(g, owner);
        other.render(g, owner);
        renderColor = selected ? selectedColor : color;
        other.renderColor = renderColor;
        render(g, owner);
        other.render(g, owner);
        this.selected = selected;
        other.selected = selected;
    }

    public VPickLine getAssociatedPick() {
        return associatedPick;
    }

    boolean getSelected() {
        return selected;
    }
}
