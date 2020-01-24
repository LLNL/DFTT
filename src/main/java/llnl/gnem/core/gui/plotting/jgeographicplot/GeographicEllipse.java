package llnl.gnem.core.gui.plotting.jgeographicplot;

import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.core.gui.plotting.transforms.CoordinateTransform;
import llnl.gnem.core.util.Geometry.EModel;
import llnl.gnem.core.util.Geometry.GeodeticCoordinate;

import java.awt.*;
import java.awt.geom.GeneralPath;
import llnl.gnem.core.util.Geometry.NEZCoordinate;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Jan 4, 2006
 */
public class GeographicEllipse extends PlotObject {

    private Color fillColor;
    private final Color edgeColor = Color.black;
    private final int alpha = 100;
    private double[] lat;
    private double[] lon;
    private boolean fillEllipse = true;

    public GeographicEllipse( double latCenter,
                              double lonCenter,
                              double smajax,
                              double sminax,
                              double strike )
    {
        initialize( latCenter, lonCenter, smajax, sminax, strike, 100, Color.red );
    }

    public GeographicEllipse( double latCenter,
                              double lonCenter,
                              double smajax,
                              double sminax,
                              double strike,
                              int nPoints,
                              Color fillColor )
    {
        initialize( latCenter, lonCenter, smajax, sminax, strike, nPoints, fillColor );
    }

    private void initialize( double latCenter,
                             double lonCenter,
                             double smajax,
                             double sminax,
                             double strike,
                             int nPoints,
                             Color fillColor )
    {
        this.fillColor = new Color( fillColor.getRed(),
                                    fillColor.getGreen(),
                                    fillColor.getBlue(),
                                    alpha );

        GeodeticCoordinate epicenter = new GeodeticCoordinate( latCenter, lonCenter, 0.0 );
        lat = new double[nPoints];
        lon = new double[nPoints];

        double theta = Math.PI / 180 * ( 90 - strike );
        double cosTheta = Math.cos( theta );
        double sinTheta = Math.sin( theta );
        double da = Math.PI * 2 / ( nPoints - 1 );
        for ( int j = 0; j < nPoints; ++j ){
            double t = j * da;
            double xtmp = smajax * Math.cos( t );
            double ytmp = sminax * Math.sin( t );
            Vector3D pos = new Vector3D(xtmp * sinTheta + ytmp * cosTheta, xtmp * cosTheta - ytmp * sinTheta, 0.0 );
            GeodeticCoordinate coord = EModel.getGeodeticCoords( epicenter, new NEZCoordinate(pos) );
            lat[j] = coord.getLat();
            lon[j] = coord.getLon();
        }
    }


    @Override
    public void render( Graphics g, JBasicPlot owner )
    {
        if( !isVisible() )
            return;

        Rectangle rect = owner.getPlotRegion().getRect();
        Graphics2D g2d = (Graphics2D) g;
        g2d.clip( rect );
        region.clear();
        GeneralPath path = new GeneralPath();
        CoordinateTransform coordTransform = owner.getCoordinateTransform();
        Coordinate coord = new Coordinate( 0, 0, lat[0], lon[0] );
        coordTransform.WorldToPlot( coord );
        path.moveTo( (float) coord.getX(), (float) coord.getY() );
        for ( int j = 1; j < lat.length; ++j ){
            coord.setWorldC1( lat[j] );
            coord.setWorldC2( lon[j] );
            coordTransform.WorldToPlot( coord );
            path.lineTo( (float) coord.getX(), (float) coord.getY() );

        }
        if( fillEllipse ){
            g2d.setColor( fillColor );
            g2d.fill( path );
        }
        g2d.setColor( edgeColor );
        g2d.setStroke( new BasicStroke( 1.0F ) );
        g2d.draw( path );
        addToRegion( path );
    }

    @Override
    public void ChangePosition( JBasicPlot owner, Graphics graphics, double dx, double dy )
    {

    }

    public void setFillEllipse( boolean fillEllipse )
    {
        this.fillEllipse = fillEllipse;
    }
}
