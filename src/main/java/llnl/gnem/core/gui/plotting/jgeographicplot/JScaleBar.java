package llnl.gnem.core.gui.plotting.jgeographicplot;

import llnl.gnem.core.gui.plotting.HorizAlignment;
import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.TickMetrics;
import llnl.gnem.core.gui.plotting.VertAlignment;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PlotAxis;
import llnl.gnem.core.gui.plotting.plotobject.DataText;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.core.gui.plotting.transforms.CoordinateTransform;
import llnl.gnem.core.util.Geometry.EModel;
import llnl.gnem.core.polygon.Vertex;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * User: Doug
 * Date: Jan 2, 2006
 * Time: 2:02:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class JScaleBar extends PlotObject {

    private double kmLength;
    private int nDivisions;
    private double latCenter;
    private double lonCenter;
    private double barHalfLength;

    public JScaleBar( double degreeRadius, double latCenter, double lonCenter )
    {
        kmLength = 0.1;
        nDivisions = 6;
        double wkm = degreeRadius * EModel.getKilometersPerDegree() * 1.5;
        TickMetrics tm = PlotAxis.defineAxis( 0, wkm );
        kmLength = tm.getMax();
        nDivisions = (int) Math.round( kmLength / tm.getIncrement() ) + 1;

        this.latCenter = latCenter;
        this.lonCenter = lonCenter;
        barHalfLength = kmLength / EModel.getKilometersPerDegree() / 2;
    }

    public void render( Graphics g, JBasicPlot owner )
    {
        CoordinateTransform ct = owner.getCoordinateTransform();
        int tickHeight = 5;
        Vertex westEnd = EModel.reckon( latCenter, lonCenter, barHalfLength, 270.0 );
        Coordinate cWest = new Coordinate( 0, 0, westEnd.getLat(), westEnd.getLon() );
        ct.WorldToPlot( cWest );
        Vertex eastEnd = EModel.reckon( latCenter, lonCenter, barHalfLength, 90.0 );

        Coordinate cEast = new Coordinate( 0, 0, eastEnd.getLat(), eastEnd.getLon() );
        ct.WorldToPlot( cEast );

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke( new BasicStroke( 2.0F ) );
        g2d.setColor( Color.BLACK );
        GeneralPath p = new GeneralPath();
        p.moveTo( (float) cWest.getX(), (float) cWest.getY() );
        p.lineTo( (float) cEast.getX(), (float) cWest.getY() );
        double width = cEast.getX() - cWest.getX();
        double dw = width / ( nDivisions - 1 );
        for ( int j = 0; j < nDivisions; ++j ){
            float x = (float) ( cWest.getX() + j * dw );
            p.moveTo( x, (float) cWest.getY() );
            p.lineTo( x, (float) cWest.getY() - tickHeight );
        }
        g2d.draw( p );

        double temp = cWest.getY() + tickHeight;
        cWest.setY( temp );
        cEast.setY( temp );
        ct.PlotToWorld( cWest );
        ct.PlotToWorld( cEast );


        DataText dt = new DataText( cWest.getWorldC1(), cWest.getWorldC2(), "0" );
        dt.setHorizontalAlignment( HorizAlignment.CENTER );
        dt.setVerticalAlignment( VertAlignment.TOP );
        dt.render( g, owner );
        if( kmLength >= 1 )
            dt = new DataText( cEast.getWorldC1(), cEast.getWorldC2(), String.format( "%4.0f km", kmLength ) );
        else
            dt = new DataText( cEast.getWorldC1(), cEast.getWorldC2(), String.format( "%4.0f m", kmLength * 1000 ) );

        dt.setHorizontalAlignment( HorizAlignment.CENTER );
        dt.setVerticalAlignment( VertAlignment.TOP );
        dt.render( g, owner );

    }

    public void ChangePosition( JBasicPlot owner, Graphics graphics, double dx, double dy )
    {

    }


}
