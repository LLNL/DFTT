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
import llnl.gnem.dftt.core.gui.plotting.plotobject.PlotObject;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * The JWindowHandle class controls a graphical representation of a "handle" on
 * the right end of a window associated with a pick. The JWindowHandle can be
 * used to drag the left-hand edge of the window right or left, thus increasing
 * or decreasing the duration of the window.
 *
 * @author Doug Dodge
 */
public class JWindowHandle extends PlotObject {
    /**
     * Gets the Pick associated with the JWindowRegion associated with this
     * JWindowHandle object. Essentially, this pick is the window handle for the
     * left-edge of the JWindowRegion.
     *
     * @return The associated Pick
     */
    public VPickLine getAssociatedPick()
    {
        return associatedPick;
    }

    /**
     * Sets the color of the JWindowHandle object. This is the color displayed
     * when the window and associated pick are not in a selected state.
     *
     * @param c The new color value
     */
    public void setColor( Color c )
    {
        color = c;
        renderColor = selected ? selectedColor : color;
    }

    /**
     * Sets the selectedColor attribute of the JWindowHandle object. This is the
     * color displayed when the window and associated pick are in a selected
     * state.
     *
     * @param c The new selectedColor value
     */
    public void setSelectedColor( Color c )
    {
        selectedColor = c;
    }

    /**
     * Change the position in the plot of the JWindowHandle. This method should
     * only be called by the mouse motion listener of the JMultiAxisPlot. This
     * method will notify any observers of the Pick's PickDataBridge so it should
     * not be called by any observers. Doing so could cause infinite recursion.
     *
     * @param axis     The JBasicPlot that owns this object
     * @param graphics  The Graphics on which to render this component.
     * @param dx       Amount to shift this JWindowHandle in the X-direction ( in
     *                 real-world coordinates ).
     * @param dy       Amount to shift this JWindowHandle in the Y-direction ( in
     *                 real-world coordinates ). Usually this value will be zero, since in
     */
    @Override
    public void ChangePosition( JBasicPlot axis, Graphics graphics, double dx, double dy )
    {
        double duration = associatedWindow.getDuration();
        duration += dx;
        if( forceInBounds ){
            if( associatedPick.getXval() + duration > maxBound )
                duration = maxBound - associatedPick.getXval();
        }
        if( duration < 0 ){
            duration = 0;
        }
        PickDataBridge wd = associatedPick.getDataBridge();
        if( wd != null ){
            wd.setDuration( duration, this );
        }
        associatedWindow.setDuration( duration );
        axis.getOwner().repaint();
    }

    public void ChangeByAndRender( double dx )
    {
        Graphics g = owner.getOwner().getGraphics();
        double duration = associatedWindow.getDuration();
        duration += dx;
        if( forceInBounds ){
            if( associatedPick.getXval() + duration > maxBound )
                duration = maxBound - associatedPick.getXval();
        }
        if( duration < 0 ){
            duration = 0;
        }
        associatedWindow.setDuration( duration );
        owner.getOwner().repaint();
    }

    /**
     * Constructor for the JWindowHandle object
     *
     * @param associatedPick The pick that is attached to this JWindowHandle
     */
    JWindowHandle( VPickLine associatedPick )
    {
        this.associatedPick = associatedPick;
        associatedWindow = associatedPick.getWindow();
        canDragY = false;
        canDragX = associatedPick.getDraggable();
        color = Color.black;
        selectedColor = Color.red;
        renderColor = color;
        selected = false;
        visible = associatedWindow.isVisible();
        width = 4;
        forceInBounds = false;
        maxBound = Double.MAX_VALUE;
    }

    public void setForceInBounds( boolean v )
    {
        forceInBounds = v;
    }

    public void setUpperBound( double v )
    {
        maxBound = v;
    }

    public boolean getForceInBounds()
    {
        return forceInBounds;
    }

    public double getUpperBound()
    {
        return maxBound;
    }

    /**
     * Sets the selected state of the JWindowHandle object. This involves changing
     * the color to and from the "selected" color to the "normal" color.
     *
     * @param selected true or false
     * @param g        The graphics context on which to render this window.
     */
    @Override
    public void setSelected( boolean selected, Graphics g )
    {
        if( this.selected == selected ){
            return;
        }
        renderColor = selected ? selectedColor : color;
        this.selected = selected;
        owner.getOwner().repaint();
    }

    boolean getSelected()
    {
        return selected;
    }

    /**
     * render this window to the supplied graphics context.
     *
     * @param g      The graphics context on which to render this window.
     * @param axisIn The JBasicPlot that owns this window.
     */
    @Override
    public void render( Graphics g, JBasicPlot axisIn )
    {
        if( !isVisible() || !owner.getCanDisplay() ){
            return;
        }
        JSubplot axis = (JSubplot) axisIn;
        double Pos = associatedPick.getXval() + associatedWindow.getDuration();
        if( Pos < axis.getXaxis().getMin() || Pos > axis.getXaxis().getMax() )
            return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.clip( owner.getPlotRegion().getRect() );

        // Remove any pre-existing regions before creating new...
        region.clear();
        g2d.setColor( renderColor );
        g2d.setStroke( new BasicStroke( width ) );

        // Get the X-positions of the rectangle ends first in data units, and then in pixels
        //  XValueMapper vm = axis.getOwner().getXMapper();
        int pos = associatedWindow.getHandleX() + width-1; //(int) vm.getXpixel(Pos);

        // Draw the line.
        int bot = associatedWindow.getHandleBottom() - 2; //associatedPick.getLineBottom() - 2;
        int top = associatedWindow.getHandleTop() + 3; //associatedPick.getLineTop() + 2;
        g2d.drawLine( pos, bot, pos, top );
        int tol = 3;
        addToRegion( new Rectangle2D.Double( pos - tol, top, 2 * tol, bot - top ) );
    }

    private final JWindowRegion associatedWindow;
    private final VPickLine associatedPick;
    private Color color;
    private Color selectedColor;
    private boolean selected;
    private int width;
    private Color renderColor;
    private boolean forceInBounds;
    private double maxBound;

    public void setWidth(int width) {
        this.width = width;
    }
}

