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
package llnl.gnem.core.gui.plotting.plotobject;

import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.core.gui.plotting.transforms.CoordinateTransform;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: dodge1
 * Date: Jul 22, 2005
 * Time: 4:34:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class LineWindowHandle extends PlotObject {
    /**
     * Gets the data value (real-world value) currently represented by this LineWindowHandle
     * object.
     *
     * @return The data value
     */
    public double getXval()
    {
        return xval;
    }

    /**
     * Gets the draggable attribute of the LineWindowHandle object. This attribute controls
     * whether the user can move the pick by dragging with the mouse.
     *
     * @return The draggable value
     */
    public boolean getDraggable()
    {
        return canDragX;
    }

    /**
     * Gets the color of the line rendered by this LineWindowHandle object
     *
     * @return The color value
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Gets the width of the line rendered by this LineWindowHandle object
     *
     * @return The width value in pixels
     */
    public int getWidth()
    {
        return width;
    }


    /**
     * Sets the (unselected) color of the LineWindowHandle object
     *
     * @param v The new color value
     */
    public void setColor( Color v )
    {
        color = v;
        renderColor = color;
    }

    /**
     * Sets the (selected) Color of the LineWindowHandle object
     *
     * @param v The new selected Color value
     */
    public void setSelectedColor( Color v )
    {
        selectedColor = v;
    }

    /**
     * Sets the width in pixels of the LineWindowHandle object
     *
     * @param v The new width value
     */
    public void setWidth( int v )
    {
        width = v;
    }


    /**
     * Change the position of this object within its owning subplot by an amount specified
     * by the input values.
     *
     * @param axisIn   The JBasicPlot that owns this LineWindowHandle
     * @param graphics
     * @param dx       The change in x-value (real-world coordinates)
     * @param dy       The change in y-value (real-world coordinates)
     */
    public void ChangePosition( JBasicPlot axisIn, Graphics graphics, double dx, double dy )
    {
        if( !canDragX )
            return;

        dx = applyWindowLimits( dx );

        JSubplot axis = (JSubplot) axisIn;
        if( graphics == null ){
            graphics = axis.getOwner().getGraphics();
            graphics.setXORMode( Color.white );
        }
        render( graphics, axis );

        setXval( getXval() + dx );

        owningWindow.windowHandleWasMoved( this, axisIn );
        render( graphics, axis );
    }

    private double applyWindowLimits( double dx )
    {
        double currentX = getXval();
        double tmpPos = currentX + dx;

        double minLimit = owningWindow.getMinimumLimit();
        if( tmpPos < minLimit ){
            dx = currentX - minLimit;
        }

        double maxLimit = owningWindow.getMaximumLimit();
        if( tmpPos > maxLimit ){
            dx = maxLimit - currentX;
        }

        dx = owningWindow.limitWindowCrush( this, dx );
        return dx;
    }


    /**
     * Sets the selected state of the LineWindowHandle object. This causes a re-rendering
     * to either the default color or to the selected color depending on the value
     * of the selected parameter.
     *
     * @param selected The new selected value
     * @param g        The graphics context on which the LineWindowHandle is being rendered.
     */
    public void setSelected( boolean selected, Graphics g )
    {
        if( this.selected != selected ){

            render( g, owner );
            renderColor = selected ? selectedColor : color;
            render( g, owner );
            this.selected = selected;
        }
    }

    public void setColorImmediate( Color v )
    {
        if( color == v )
            return;

        PlotObjectRenderer por = new PlotObjectRenderer( this );
        setColor( v );
        por.run();
    }


    public void setSelected( boolean selected )
    {
        setSelected( selected, this.getOwner().getOwner().getGraphics() );
    }

    /**
     * render this LineWindowHandle object and its contained PlotObjects to the supplied graphics
     * context.
     *
     * @param g     The graphics context on which to render the LineWindowHandle object
     * @param owner The JSubplot object that owns this LineWindowHandle.
     */
    public synchronized void render( Graphics g, JBasicPlot owner )
    {
        if( !isVisible() || g == null || owner == null || !owner.getCanDisplay() )
            return;

        Rectangle windowRect = owningWindow.getBoundingRect();
        if( windowRect == null )
            return;

        int top = windowRect.y;
        int height = windowRect.height;

        Graphics2D g2d = (Graphics2D) g;
        Rectangle rect = owner.getPlotRegion().getRect();
        g2d.clip( rect );
        // Remove any pre-existing regions before creating new...
        region.clear();
        g2d.setColor( renderColor );
        g2d.setStroke( new BasicStroke( width ) );
        CoordinateTransform ct = owner.getCoordinateTransform();
        Coordinate coord = new Coordinate( 0.0, 0.0, getXval(), 0.0 );
        ct.WorldToPlot( coord );
        int xpos = (int) coord.getX();
        int bottom = top + height;
        g.drawLine( xpos, bottom, xpos, top );

        // create a selection region
        int tol = 3;
        addToRegion( new Rectangle2D.Double( xpos - tol, top, 2 * tol, bottom - top ) );

    }

    public LineWindowHandle( LineWindow owningWindow, double xval )
    {
        this.owningWindow = owningWindow;
        this.setXval( xval );
        color = Color.black;
        renderColor = color;
        selectedColor = Color.red;
        width = 2;
        selected = false;
        canDragX = true;
    }

    public void startingDragOperation()
    {
        xvalAtDragStart = xval;
    }

    public void finishedDragOperation()
    {
        double dx = xval - xvalAtDragStart;
        owningWindow.windowHandleMoveComplete( this, dx );
    }

    private LineWindow owningWindow;
    private double xval;
    private Color color;
    private Color selectedColor;
    private Color renderColor;
    private int width;
    private boolean selected;
    private double xvalAtDragStart = 0;

    public void setXval( double xval )
    {
        this.xval = xval;
    }
}


