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

import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;
import llnl.gnem.dftt.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.dftt.core.gui.plotting.transforms.CoordinateTransform;
import llnl.gnem.dftt.core.polygon.Vertex;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * JPolyShape is a filled polygon with edges that can be rendered separately from the
 * interior.
 */
public class JPolyShape extends PlotObject {
    public Vertex[] vertices;

    private Color FillColor = Color.white;
    private Color EdgeColor = Color.black;


    /**
     * The constructor for JPolyShape
     *
     * @param points An array of Vertex objects that collectively define the polygons
     *               vertices. This constructor creates a polygon with a white interior and black edges.
     */
    public JPolyShape( Vertex[] points )
    {
        vertices = points;
    }

    /**
     * Sets the color of the polygon interior.
     *
     * @param c The new interior color
     */
    public void setFillColor( Color c )
    {
        FillColor = c;
    }

    public void setEdgeColor( Color c )
    {
        EdgeColor = c;
    }

    /**
     * Renders this polygon with the current graphics context. As currently implemented,
     * if any part of the polygon is outside the axis limits, the polygon will not be rendered.
     * This behavior was chosen because for Azimuthal equal-area Coordinate Transforms, objects
     * at or near 180 degrees from the origin are distorted to fill the entire plot region.
     * If only the offending vertices are clipped, the polygon may become unrecognizable in shape.
     *
     * @param g     The graphics context on which to render this polygon.
     * @param owner The JBasicPlot on which this polygon is being rendered.
     */
    public void render( Graphics g, JBasicPlot owner )
    {
        if( !isVisible() )
            return;

        region.clear();
        GeneralPath path = new GeneralPath();
        CoordinateTransform coordTransform = owner.getCoordinateTransform();
        Coordinate coord = new Coordinate( 0, 0 );
        coord.setWorldC1( vertices[0].getLat() );
        coord.setWorldC2( vertices[0].getLon() );
        if( coordTransform.isOutOfBounds( coord ) )
            return;
        coordTransform.WorldToPlot( coord );
        path.moveTo( (float) coord.getX(), (float) coord.getY() );
        for ( int j = 1; j < vertices.length; ++j ){
            coord.setWorldC1( vertices[j].getLat() );
            coord.setWorldC2( vertices[j].getLon() );
            if( coordTransform.isOutOfBounds( coord ) )
                return;
            coordTransform.WorldToPlot( coord );
            path.lineTo( (float) coord.getX(), (float) coord.getY() );

        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor( FillColor );
        g2d.fill( path );
        g2d.setColor( EdgeColor );
        g2d.setStroke( new BasicStroke( 1.0F ) );
        g2d.draw( path );
        addToRegion( path );


    }

    public void ChangePosition( JBasicPlot owner, Graphics graphics, double dx, double dy )
    {

    }
}
