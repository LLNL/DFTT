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

import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;
import llnl.gnem.dftt.core.gui.plotting.colormap.Colormap;
import llnl.gnem.dftt.core.gui.plotting.colormap.HotColormap;
import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.dftt.core.gui.plotting.transforms.CoordinateTransform;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Pcolor is a PlotObject that renders an evenly-sampled function of two variables
 * using color to represent the dependent variable.
 * It is based loosely on the Matlab pcolor command, hence, the name. A
 * Pcolor object once created, can be added to any  descendent of JBasicPlot and
 * will be automatically rendered whenever the JBasicPlot is rendered. By default,
 * the Pcolor object will render itself using a HotColormap to represent the range
 * of Z values. The HotColormap uses the same RGB sequence as the Matlab Hot colormap.
 * However, other Colormap objects can be created and substituted as required to give
 * different presentation looks.
 *
 * @see llnl.gnem.plotting.colormap.Colormap
 * @see llnl.gnem.plotting.colormap.HotColormap
 */
public class Pcolor extends PlotObject {

    private final double[][] zValues;
    final double[] xValues;
    final double[] yValues;
    private Colormap colormap;
    private double zMin;
    private double zMax;
    private int alpha = 255;
    Color[][] colors;


    /**
     * The constructor for the Pcolor object.
     *
     * @param zValues A two-dimensional array containing the dependent variable values.
     *                The first index of zValues corresponds to the Y values array. Hence the length of
     *                zValues must equal the length of yValues. The second index of zValues corresponds
     *                to the X values array. Hence for any j ( 0 < j < yValues.length ), zValues[j].length
     *                must equal xValues.length.
     * @param xValues An array of monotonically increasing values representing
     *                the X independent variable. xValues must have a length of at least 2.
     * @param yValues An array of monotonically increasing values representing the
     *                Y independent variable. yValues must have a length of at least 2.
     */
    public Pcolor( final double[][] zValues, final double[] xValues, final double[] yValues )
    {
        if( zValues == null )
            throw new IllegalArgumentException( "Z values array is null!" );
        if( xValues == null )
            throw new IllegalArgumentException( "X values array is null!" );
        if( yValues == null )
            throw new IllegalArgumentException( "Y values array is null!" );
        if( xValues.length < 2 )
            throw new IllegalArgumentException( "There must be at least two X values!" );
        if( yValues.length < 2 )
            throw new IllegalArgumentException( "There must be at least two Y values!" );
        if( zValues.length != yValues.length )
            throw new IllegalArgumentException( "Number of rows in Z array is not equal to the number of elements in the Y array!" );

        zMin = Double.MAX_VALUE;
        zMax = -getzMin();
        for ( int j = 0; j < yValues.length; ++j ){
            if( zValues[j] == null )
                throw new IllegalArgumentException( "Row " + j + " of the Z array has no columns!" );
            if( zValues[j].length != xValues.length )
                throw new IllegalArgumentException( "Row " + j + " of the Z array is not the same length as the X array!" );
            for ( int k = 0; k < zValues[j].length; ++k ){
                zMin = Math.min( getzMin(), zValues[j][k] );
                zMax = Math.max( getzMax(), zValues[j][k] );
            }
        }
        this.xValues = xValues;
        this.yValues = yValues;
        this.zValues = zValues;
        colormap = new HotColormap( getzMin(), getzMax() );
        colors = new Color[zValues.length][zValues[0].length];
        assignColors();
    }

    /**
     * The constructor for the Pcolor object.
     *
     * @param zValues A two-dimensional array containing the dependent variable values.
     *                The first index of zValues corresponds to the Y values array. Hence the length of
     *                zValues must equal the length of yValues. The second index of zValues corresponds
     *                to the X values array. Hence for any j ( 0 < j < yValues.length ), zValues[j].length
     *                must equal xValues.length.
     * @param xValues An array of monotonically increasing values representing
     *                the X independent variable. xValues must have a length of at least 2.
     * @param yValues An array of monotonically increasing values representing the
     *                Y independent variable. yValues must have a length of at least 2.
     */
    public Pcolor( final double[][] zValues, final double[] xValues, final double[] yValues, Colormap colormap )
    {
        if( zValues == null )
            throw new IllegalArgumentException( "Z values array is null!" );
        if( xValues == null )
            throw new IllegalArgumentException( "X values array is null!" );
        if( yValues == null )
            throw new IllegalArgumentException( "Y values array is null!" );
        if( xValues.length < 2 )
            throw new IllegalArgumentException( "There must be at least two X values!" );
        if( yValues.length < 2 )
            throw new IllegalArgumentException( "There must be at least two Y values!" );
        if( zValues.length != yValues.length )
            throw new IllegalArgumentException( "Number of rows in Z array is not equal to the number of elements in the Y array!" );

        zMin = Double.MAX_VALUE;
        zMax = -getzMin();
        for ( int j = 0; j < yValues.length; ++j ){
            if( zValues[j] == null )
                throw new IllegalArgumentException( "Row " + j + " of the Z array has no columns!" );
            if( zValues[j].length != xValues.length )
                throw new IllegalArgumentException( "Row " + j + " of the Z array is not the same length as the X array!" );
            for ( int k = 0; k < zValues[j].length; ++k ){
                zMin = Math.min( getzMin(), zValues[j][k] );
                zMax = Math.max( getzMax(), zValues[j][k] );
            }
        }
        this.xValues = xValues;
        this.yValues = yValues;
        this.zValues = zValues;

        this.colormap = colormap;
        this.colormap.setMinMax(zMin, zMax);
        colors = new Color[zValues.length][zValues[0].length];
        assignColors();
    }

    /**
     * The constructor for the Pcolor object given the X,Y,Z arrays as well as a Color Map and min/max values.
     *
     * @param zValues A two-dimensional array containing the dependent variable values.
     *                The first index of zValues corresponds to the Y values array. Hence the length of
     *                zValues must equal the length of yValues. The second index of zValues corresponds
     *                to the X values array. Hence for any j ( 0 < j < yValues.length ), zValues[j].length
     *                must equal xValues.length.
     * @param xValues An array of monotonically increasing values representing
     *                the X independent variable. xValues must have a length of at least 2.
     * @param yValues An array of monotonically increasing values representing the
     *                Y independent variable. yValues must have a length of at least 2.
     * @param map     - a predefined Colormap
     * @param zMin    - a preset Minimum Z value to plot
     * @param zMax    - a preset Maximum Z value to plot
     */
    public Pcolor( final double[][] zValues, final double[] xValues, final double[] yValues, Colormap map, double zMin, double zMax )
    {
        if( zValues == null )
            throw new IllegalArgumentException( "Z values array is null!" );
        if( xValues == null )
            throw new IllegalArgumentException( "X values array is null!" );
        if( yValues == null )
            throw new IllegalArgumentException( "Y values array is null!" );
        if( xValues.length < 2 )
            throw new IllegalArgumentException( "There must be at least two X values!" );
        if( yValues.length < 2 )
            throw new IllegalArgumentException( "There must be at least two Y values!" );
        if( zValues.length != yValues.length )
            throw new IllegalArgumentException( "Number of rows in Z array is not equal to the number of elements in the Y array!" );

        this.zMin = zMin;
        this.zMax = zMax;

        this.xValues = xValues;
        this.yValues = yValues;
        this.zValues = zValues;

        colormap = map;
        colormap.setMinMax( zMin, zMax );
        colors = new Color[zValues.length][zValues[0].length];
        assignColors();
    }


    /**
     *
     * @param x
     * @param y
     * @return
     */
    public double getClosestZ( double x, double y )
    {
        double dx = xValues[1] - xValues[0];
        double dy = yValues[1] - yValues[0];
        int xIdx = (int)Math.round( (x - xValues[0]) / dx );
        if( xIdx < 0 ) xIdx = 0;
        if( xIdx > xValues.length-1) xIdx = xValues.length-1;

        int yIdx = (int)Math.round( (y - yValues[0]) / dy );
        if( yIdx < 0 ) yIdx = 0;
        if( yIdx > yValues.length-1) yIdx = yValues.length-1;
        return zValues[yIdx][xIdx];

    }
    /**
     * Substitutes another Colormap for the current Colormap.
     *
     * @param map The new Colormap to use.
     */
    public void setColormap( Colormap map )
    {
        colormap = map;
        colormap.setMinMax( getzMin(), getzMax() );
        assignColors();
    }

    public void setMinMax( double zMin, double zMax )
    {
        this.zMin = zMin;
        this.zMax = zMax;
        assignColors();
    }

    /**
     * Renders this object onto the supplied graphics context owned by owner.
     *
     * @param g     The graphics context to use.
     * @param owner The JBasicPlot that owns this PlotObject.
     */
    public void render( Graphics g, JBasicPlot owner )
    {
        if( !isVisible() )
            return;

        region.clear();
        int numXpts = xValues.length;
        int numYpts = yValues.length;
        double xRange = xValues[numXpts - 1] - xValues[0];
        double cellWidth = xRange / ( numXpts - 1 );
        double yRange = yValues[numYpts - 1] - yValues[0];
        double cellHeight = yRange / ( numYpts - 1  );


        CoordinateTransform ct = owner.getCoordinateTransform();
        Coordinate coord = new Coordinate( 0, 0 );
        Graphics2D g2d = (Graphics2D) g;
        g2d.clip( owner.getPlotRegion().getRect() );
        double halfWidth = cellWidth / 2;
        double halfHeight = cellHeight / 2;

        int minX = Integer.MAX_VALUE;
        int maxX = - minX;
        int minY = minX;
        int maxY = maxX;
        for ( int j = 0; j < numYpts; ++j ){
            double y = yValues[j];
            double ylow = y - halfHeight;
            double yhigh = y + halfHeight;
            for ( int k = 0; k < numXpts; ++k ){
                double x = xValues[k];
                double xlow = x - halfWidth;
                double xhigh = x + halfWidth;
                GeneralPath path = new GeneralPath();
                coord.setWorldC1( xlow );
                coord.setWorldC2( ylow );
                ct.WorldToPlot( coord );
                path.moveTo( (float) coord.getX(), (float) coord.getY() );
                if( coord.getX() < minX ) minX = (int)coord.getX();
                if( coord.getX() > maxX ) maxX = (int)coord.getX();
                if( coord.getY() < minY ) minY = (int)coord.getY();
                if( coord.getY() > maxY ) maxY = (int)coord.getY();
                coord.setWorldC2( yhigh );
                ct.WorldToPlot( coord );
                path.lineTo( (float) coord.getX(), (float) coord.getY() );
                if( coord.getX() < minX ) minX = (int)coord.getX();
                if( coord.getX() > maxX ) maxX = (int)coord.getX();
                if( coord.getY() < minY ) minY = (int)coord.getY();
                if( coord.getY() > maxY ) maxY = (int)coord.getY();
                coord.setWorldC1( xhigh );
                ct.WorldToPlot( coord );
                path.lineTo( (float) coord.getX(), (float) coord.getY() );
                if( coord.getX() < minX ) minX = (int)coord.getX();
                if( coord.getX() > maxX ) maxX = (int)coord.getX();
                if( coord.getY() < minY ) minY = (int)coord.getY();
                if( coord.getY() > maxY ) maxY = (int)coord.getY();
                coord.setWorldC2( ylow );
                ct.WorldToPlot( coord );
                path.lineTo( (float) coord.getX(), (float) coord.getY() );
                if( coord.getX() < minX ) minX = (int)coord.getX();
                if( coord.getX() > maxX ) maxX = (int)coord.getX();
                if( coord.getY() < minY ) minY = (int)coord.getY();
                if( coord.getY() > maxY ) maxY = (int)coord.getY();
                g2d.setColor( colors[j][k] );
                g2d.fill( path );
            }

        }
        this.addToRegion(new Rectangle(minX,minY,maxX-minX,maxY-minY));
    }


    private void assignColors()
    {
        int numXpts = xValues.length;
        int numYpts = yValues.length;
        for ( int j = 0; j < numYpts; ++j ){
            for ( int k = 0; k < numXpts; ++k ){
                Color c = colormap.getColor( zValues[j][k] );
                colors[j][k] = null;
                colors[j][k] = new Color( c.getRed(), c.getGreen(), c.getBlue(), alpha );
                c = null;
            }
        }
    }

    @Override
    public void ChangePosition( JBasicPlot owner, Graphics graphics, double dx, double dy )
    {

    }


    public void setAlpha( int alpha )
    {
        this.alpha = alpha;
        assignColors();
    }

    public double getzMin()
    {
        return zMin;
    }

    public double getzMax()
    {
        return zMax;
    }
}
