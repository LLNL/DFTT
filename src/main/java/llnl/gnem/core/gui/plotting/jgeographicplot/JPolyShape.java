package llnl.gnem.core.gui.plotting.jgeographicplot;

import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.core.gui.plotting.transforms.CoordinateTransform;
import llnl.gnem.core.polygon.Vertex;

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
