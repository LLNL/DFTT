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
package llnl.gnem.core.gui.plotting.jgeographicplot;

import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.JPlotContainer;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.core.gui.plotting.transforms.CoordinateTransform;
import llnl.gnem.core.polygon.Vertex;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Created by: dodge1
 * Date: Feb 8, 2005
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class JSelectionCrosshair extends PlotObject {

    public JSelectionCrosshair()
    {
        initialize( 0.0, 0.0, 5.0, Color.red, 3 );
    }

    public JSelectionCrosshair( double centerLat, double centerLon, double degreeRadius, Color color, int width )
    {
        initialize( centerLat, centerLon, degreeRadius, color, width );
    }

    private void initialize( double centerLat, double centerLon, double degreeRadius, Color color, int width )
    {
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        this.degreeRadius = degreeRadius;
        EdgeColor = color;
        lineThickness = width;
        makeLatLine();
        makeLonLine();
    }

    private void makeLonLine()
    {
        vVertices = new Vertex[POINTS_PER_LINE];
        double latStart = centerLat - degreeRadius;
        if( latStart < -MAX_LAT )
            latStart = -MAX_LAT;
        double latEnd = latStart + 2 * degreeRadius;
        if( latEnd > MAX_LAT )
            latEnd = MAX_LAT;
        double increment = ( latEnd - latStart ) / ( POINTS_PER_LINE - 1 );
        for ( int j = 0; j < POINTS_PER_LINE; ++j ){
            double lat = latStart + j * increment;
            vVertices[j] = new Vertex( lat, centerLon );
        }
    }

    private void makeLatLine()
    {
        hVertices = new Vertex[POINTS_PER_LINE];
        double lonStart = centerLon - degreeRadius;
        if( lonStart < -MAX_LON )
            lonStart = -MAX_LON;
        double lonEnd = lonStart + 2 * degreeRadius;
        if( lonEnd > MAX_LON )
            lonEnd = MAX_LON;
        double increment = ( lonEnd - lonStart ) / ( POINTS_PER_LINE - 1 );
        for ( int j = 0; j < POINTS_PER_LINE; ++j ){
            double lon = lonStart + j * increment;
            hVertices[j] = new Vertex( centerLat, lon );
        }
    }


    public void render( Graphics g, JBasicPlot owner )
    {
        if( !isVisible() )
            return;

        if( g == null )
            return;

        Rectangle rect = owner.getPlotRegion().getRect();
        Graphics2D g2d = (Graphics2D) g;
        g2d.clip( rect );
        region.clear();
        GeneralPath path = new GeneralPath();
        coordTransform = owner.getCoordinateTransform();
        Coordinate coord = new Coordinate( 0, 0 );

        plotCrossBar( hVertices, coord, path );
        plotCrossBar( vVertices, coord, path );


        g2d.setColor( EdgeColor );
        g2d.setStroke( new BasicStroke( lineThickness ) );
        g2d.draw( path );

        addToRegion( path );
    }

    private void plotCrossBar( Vertex[] vertices, Coordinate coord, GeneralPath path )
    {
        coord.setWorldC1( vertices[0].getLat() );
        coord.setWorldC2( vertices[0].getLon() );
        coordTransform.WorldToPlot( coord );
        path.moveTo( (float) coord.getX(), (float) coord.getY() );
        for ( int j = 1; j < vertices.length; ++j ){
            coord.setWorldC1( vertices[j].getLat() );
            coord.setWorldC2( vertices[j].getLon() );
            coordTransform.WorldToPlot( coord );
            path.lineTo( (float) coord.getX(), (float) coord.getY() );
        }
    }

    public void ChangePosition( JBasicPlot owner, Graphics graphics, double dx, double dy )
    {

    }


    public void setPosition( JBasicPlot owner, double centerLat, double centerLon )
    {
        Graphics g = owner.getOwner().getGraphics();
        render( g, owner );
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        makeLatLine();
        makeLonLine();
        render( g, owner );
    }

    public Vertex getPosition()
    {
        return new Vertex( centerLat, centerLon );
    }

    public void setVisible( boolean visible )
    {
        if( owner != null ){
            JPlotContainer container = owner.getOwner();
            if( container != null ){
                Graphics g = container.getGraphics();
                if( g != null ){
                    render( g, owner );
                    this.visible = visible;
                    render( g, owner );
                }
                else
                    this.visible = visible;
            }
            else
                this.visible = visible;
        }
        else
            this.visible = visible;
    }

    private CoordinateTransform coordTransform;

    private Color EdgeColor = Color.red;

    private double centerLat;
    private double centerLon;
    private double degreeRadius;
    private int lineThickness;
    private static int POINTS_PER_LINE = 100;
    private Vertex[] hVertices;
    private Vertex[] vVertices;
    private static final int MAX_LAT = 90;
    private static final int MAX_LON = 180;
}
