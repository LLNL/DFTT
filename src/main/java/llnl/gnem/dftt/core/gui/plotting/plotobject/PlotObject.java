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

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;

/*
 * COPYRIGHT NOTICE GnemUtils Version 1.0 Copyright (C) 2002 Lawrence Livermore
 * National Laboratory.
 */
/**
 * Base class for all types of objects that can be displayed in the axis.
 *
 * @author Doug Dodge
 */
public abstract class PlotObject {

    /**
     * Controls visibility of this object
     */
    protected boolean visible;
    /**
     * Vector of shape objects that correspond to this object. Used for hit
     * testing
     */
    protected ArrayList<Shape> region;
    /**
     * Controls ability to drag in the X-direction
     */
    protected boolean canDragX;
    /**
     * Controls ability to drag in the Y-direction
     */
    protected boolean canDragY;
    protected JBasicPlot owner;
    private boolean selectable = true;

    /**
     * Constructor for the PlotObject object
     */
    public PlotObject() {
        visible = true;
        region = new ArrayList<>();
        canDragX = false;
        canDragY = false;
    }

    /**
     * Gets the visible attribute of the Plot Object
     *
     * @return The visible value
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visible attribute of the PlotObject
     *
     * @param v The new visible value
     */
    public void setVisible(boolean v) {
        visible = v;
    }

    /**
     * Renders the object to the supplied graphics context
     *
     * @param g The graphics context
     * @param owner The JBasicPlot that owns this object
     */
    abstract public void render(Graphics g, JBasicPlot owner);

    /**
     * Clears the vector containing selection regions. This should be done prior
     * to rendering of a JSubPlot so that regions that will not be displayed at
     * the current zoom level but that were previously visible cannot be
     * inadvertently selected.
     */
    public void clearSelectionRegion() {
        if (region != null) {
            region.clear();
        }
        if (hasContainedObjects()) {
            ArrayList<? extends PlotObject> plotObjects = getContainedObjects();
            for (PlotObject anObj : plotObjects) {
                anObj.clearSelectionRegion();
            }
        }

    }

    /**
     * Move this object to a new position in the subplot
     *
     * @param owner The JBasicPlot that owns this plot object
     * @param graphics
     * @param dx Amount to shift in the x-direction (real-world coordinates)
     * @param dy Amount to shift in the y-direction (real-world coordinates)
     */
    public abstract void ChangePosition(JBasicPlot owner, Graphics graphics, double dx, double dy);

    public void setSelected(boolean selected, Graphics g) {
        // Default implementation is to do nothing. Subclasses can implement alternatives.
    }

    public JBasicPlot getOwner() {
        return owner;
    }

    public void setOwner(JBasicPlot plot) {
        owner = plot;
        if (hasContainedObjects()) {
            ArrayList<? extends PlotObject> plotObjects = getContainedObjects();
            for (PlotObject anObj : plotObjects) {
                anObj.setOwner(plot);
            }
        }

    }

    /**
     * Returns true if the point in user-space described by the input values is
     * inside this plot object. Used for hit-testing
     *
     * @param x x-value in user-space coordinates
     * @param y y-value in user-space coordinates
     * @return true if the point is inside this plot object
     */
    public boolean PointInside(int x, int y) {
        return isPointInside(x, y);
    }

    public PlotObject getSubObjectContainingPoint(int x, int y) {
        if (hasContainedObjects()) {
            ArrayList<? extends PlotObject> plotObjects = getContainedObjects();
            for (PlotObject anObj : plotObjects) {
                if (anObj.isSelectable() && anObj.PointInside(x, y)) {
                    return anObj;
                }
            }
        }
        return null;
    }

    private boolean isPointInside(int x, int y) {
        for (Shape s : region) {
            if (s.contains(x, y) && visible) {
                return true;
            }
        }
        return false;
    }

    public Rectangle getBounds() {
        if (region.size() < 1) {
            return null;
        } else {
            Shape result = region.get(0);
            Rectangle bounds = result.getBounds();
            for (int j = 1; j < region.size(); ++j) {
                Shape s = region.get(j);
                Rectangle r = s.getBounds();
                bounds.add(r);
            }
            return bounds;
        }

    }

    /**
     * Gets the capability of this object to be dragged in the x-direction
     *
     * @return true if this object can be dragged in the x-direction
     */
    public boolean getCanDragX() {
        return canDragX;
    }

    /**
     * Sets the ability of this object to be dragged in the x-direction
     *
     * @param v The new canDragX value
     */
    public void setCanDragX(boolean v) {
        canDragX = v;
    }

    /**
     * Gets the capability of this object to be dragged in the y-direction
     *
     * @return The canDragY value
     */
    public boolean getCanDragY() {
        return canDragY;
    }

    /**
     * Sets the ability of this object to be dragged in the y-direction
     *
     * @param v The new canDragY value
     */
    public void setCanDragY(boolean v) {
        canDragY = v;
    }

    /**
     * Gets information about whether this object contains any other objects
     * that can be interacted with
     *
     * @return true if this object contains other user-interface objects
     */
    public boolean hasContainedObjects() {
        return false;
    }

    /**
     * Gets a Vector of interactive objects contained by this object
     *
     * @return The containedObjects value
     */
    protected ArrayList<? extends PlotObject> getContainedObjects() {
        return new ArrayList<>();
    }

    protected void addToRegion(Shape o) {
        region.add(o);
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }
}
