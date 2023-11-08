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
import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.dftt.core.gui.plotting.transforms.CoordinateTransform;

import java.awt.*;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Jan 5, 2006
 */
public class ResidualBar extends PlotObject {


    public VPickLine getAssociatedPick()
    {
        return associatedPick;
    }

    public void setColor( Color c )
    {
        color = c;
    }


    /**
     * Sets the visible attribute of the JWindowRegion object
     *
     * @param v The new visible value
     */
    public void setVisible( boolean v )
    {
        super.setVisible( v );
        associatedPick.getWindowHandle().setVisible( v );
    }


    public void ChangePosition( JBasicPlot axis, Graphics graphics, double dx, double dy )
    {
    }


    ResidualBar( VPickLine associatedPick, double residual, boolean visible )
    {
        this.associatedPick = associatedPick;
        canDragY = false;
        canDragX = false;
        color = Color.green;
        this.setResidual( residual );
        this.visible = visible;
    }


    /**
     * render this window to the supplied graphics context.
     *
     * @param g      The graphics context on which to render this window.
     * @param axisIn The JSubplot that owns this window.
     */
    public void render( Graphics g, JBasicPlot axisIn )
    {

        if( !isVisible() || !owner.getCanDisplay() || residual == -999 || residual == 0 ){
            return;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.clip( owner.getPlotRegion().getRect() );

        // Remove any pre-existing regions before creating new...
        region.clear();
        g2d.setColor( color );
        int bot = associatedPick.getLineBottom() - 2;
        int top = associatedPick.getLineTop() + 2;
        int height = bot - top;

        BarXValues barXValues = getBarXValues();
        if( getResidual() <= 0 )
            renderNegativeResidual( barXValues,
                                    top,
                                    height,
                                    g2d );
        else
            renderPositiveResidual( barXValues,
                                    top,
                                    height,
                                    g2d );

    }

    private BarXValues getBarXValues()
    {
        double pickPos = associatedPick.getXval();
        int sign = (int) Math.signum( getResidual() );
        CoordinateTransform ct = owner.getCoordinateTransform();

        double predicted = pickPos - getResidual();
        Coordinate coord = new Coordinate( 0.0, 0.0, predicted, 0.0 );
        ct.WorldToPlot( coord );


        coord = new Coordinate( 0.0, 0.0, predicted + yellowThreshold * sign, 0.0 );
        ct.WorldToPlot( coord );
        int yellowTransitionX = (int) coord.getX();


        coord.setWorldC1( predicted + redThreshold * sign );
        ct.WorldToPlot( coord );
        int redTransitionX = (int) coord.getX();


        coord.setWorldC1( pickPos );
        ct.WorldToPlot( coord );
        int left = (int) coord.getX() + 1;
        coord.setWorldC1( pickPos - getResidual() );
        ct.WorldToPlot( coord );
        int width = (int) coord.getX() - left;

        if( sign > 0 ){
            coord.setWorldC1( pickPos );
            ct.WorldToPlot( coord );
            int right = (int) coord.getX() - 1;
            coord.setWorldC1( pickPos - getResidual() );
            ct.WorldToPlot( coord );
            left = (int) coord.getX();
            width = right - left;
        }
        return new BarXValues( left,
                               yellowTransitionX,
                               redTransitionX,
                               width );

    }

    class BarXValues {
        public int left;
        public int yellowTransitionX;
        public int redTransitionX;
        public int width;

        public BarXValues( int left,
                           int yellowTransitionX,
                           int redTransitionX,
                           int width )
        {
            this.left = left;
            this.yellowTransitionX = yellowTransitionX;
            this.redTransitionX = redTransitionX;
            this.width = width;

        }
    }

    private void renderNegativeResidual( BarXValues xv,
                                         int top,
                                         int height,
                                         Graphics2D g2d )
    {
        if( height >= 1 ){
            Color yellowColor = new Color( 255, 255, 0 );
            Paint ryGradient = new GradientPaint( xv.redTransitionX, top, Color.red, xv.yellowTransitionX, top, yellowColor );
            Paint ygGradient = new GradientPaint( xv.yellowTransitionX, top, yellowColor, xv.left + xv.width, top, Color.green );
            if( xv.left <= xv.yellowTransitionX ){
                g2d.setPaint( ryGradient );
                g2d.fillRect( xv.left, top, xv.yellowTransitionX - xv.left, height );
            }

            g2d.setPaint( ygGradient );
            int width = Math.min( xv.width, xv.left + xv.width - xv.yellowTransitionX );
            g2d.fillRect( Math.max( xv.left, xv.yellowTransitionX ), top, width, height );
        }

    }


    private void renderPositiveResidual( BarXValues xv,
                                         int top,
                                         int height,
                                         Graphics2D g2d )
    {
        if( height >= 1 ){
            Color yellowColor = new Color( 255, 255, 0 );


            Paint gyGradient = new GradientPaint( xv.left, top, Color.green, xv.yellowTransitionX, top, yellowColor );
            g2d.setPaint( gyGradient );
            int width = Math.min( xv.width, xv.yellowTransitionX - xv.left );
            g2d.fillRect( xv.left, top, width, height );
            if( xv.left + xv.width > xv.yellowTransitionX ){
                Paint yrGradient = new GradientPaint( xv.yellowTransitionX, top, yellowColor, xv.redTransitionX, top, Color.red );
                g2d.setPaint( yrGradient );
                width = Math.min( xv.width, xv.left + xv.width - xv.yellowTransitionX );
                g2d.fillRect( xv.yellowTransitionX, top, width, height );
            }
        }


    }


    private VPickLine associatedPick;

    private Color color;
    private double residual;
    private static double yellowThreshold = 5.0; // seconds at which color has transitioned to yellow from green.
    private static double redThreshold = 10.0; // seconds at which color has transitioned from yellow to red.

    public static void setYellowThreshold( double value )
    {
        yellowThreshold = value;
    }

    public static void setRedThreshold( double value )
    {
        redThreshold = value;
    }


    public double getResidual()
    {
        return residual;
    }

    public void setResidual( double residual )
    {
        this.residual = residual;
    }
}

