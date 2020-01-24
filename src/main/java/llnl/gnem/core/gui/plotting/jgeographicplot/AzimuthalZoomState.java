package llnl.gnem.core.gui.plotting.jgeographicplot;

import llnl.gnem.core.gui.plotting.ZoomState;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;

/**
 * This class is part of the implementation of a "Zoom Stack" for azimuthal geographic
 * plots. It stores the origin of the zoom state and the radius of the zoom and allows
 * access to those values.
 */
public class AzimuthalZoomState implements ZoomState {

    private double centerLat;
    private double centerLon;
    private double degreeRadius;


    /**
     * Constructor for the AzimuthalZoomState that takes the origin as a lat-lon pair.
     *
     * @param centerLat    The latitude of the origin
     * @param centerLon    The longitude of the origin
     * @param degreeRadius The radius of the zoom state in degrees
     */
    public AzimuthalZoomState( double centerLat, double centerLon, double degreeRadius )
    {
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        this.degreeRadius = degreeRadius;
    }

    /**
     * Constructor for the AzimuthalZoomState that takes the origin as a Coordinate object
     *
     * @param center       The origin of the zoom state
     * @param degreeRadius The radius of the zoom state in degrees
     */
    public AzimuthalZoomState( Coordinate center, double degreeRadius )
    {
        centerLat = center.getWorldC1();
        centerLon = center.getWorldC2();
        if( centerLon < -180 )
            centerLon += 360;
        this.degreeRadius = degreeRadius;
    }


    /**
     * Gets the latitude of the zoom state origin
     *
     * @return The latitude value
     */
    public double getCenterLat()
    {
        return centerLat;
    }

    /**
     * Gets the longitude of the zoom state origin
     *
     * @return The longitude value
     */
    public double getCenterLon()
    {
        return centerLon;
    }

    /**
     * Gets the radius of the zoom state in degrees
     *
     * @return The radius value
     */
    public double getDegreeRadius()
    {
        return degreeRadius;
    }


    /**
     * Gets the origin of the zoom state as a Coordinate object
     *
     * @return The zoom state origin
     */
    public Coordinate getCenterCoordinate()
    {
        Coordinate c = new Coordinate( 0, 0 );
        c.setWorldC1( centerLat );
        c.setWorldC2( centerLon );
        return c;
    }
}
