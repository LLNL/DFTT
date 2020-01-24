package llnl.gnem.core.polygon;

import java.io.Serializable;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A class that encapsulates a 2-D point on the surface of the Earth described by a latitude and a longitude
 *
 * @author Doug Dodge
 */
public class Vertex implements Serializable {
    /**
     * Creates a new Vertex from a latitude and a longitude value
     *
     * @param Lat The latitude of the point
     * @param Lon The longitude of the point
     */
    public Vertex( double Lat, double Lon )
    {
        lat = ValidateLat( Lat );
        lon = ValidateLon( Lon );
    }

    /**
     * Copy Constructor for the Vertex object
     *
     * @param v The Vertex to be copied
     */
    public Vertex( Vertex v )
    {
        lat = v.lat;
        lon = v.lon;
    }

    /**
     * Gets the lat attribute of the Vertex object
     *
     * @return The lat value
     */
    public double getLat()
    {
        return lat;
    }

    /**
     * Gets the longitude of the Vertex
     *
     * @return The lon value
     */
    public double getLon()
    {
        return lon;
    }

    /**
     * Sets the latitude of the Vertex
     *
     * @param Lat The new lat value
     */
    public void setLat( double Lat )
    {
        lat = ValidateLat( Lat );
    }

    /**
     * Sets the longitude of the Vertex
     *
     * @param Lon The new lon value
     */
    public void setLon( double Lon )
    {
        lon = ValidateLon( Lon );
    }

    /**
     * Determine whether two Vertex objects describe the same point.
     *
     * @param o The Vertex to be tested
     * @return true if the two Vertex objects describe the same point.
     */
    public boolean equals( Object o )
    {
        if( o == this )
            return true;
        if( o instanceof Vertex ) {
            // No need to check for null because instanceof handles that check

            Vertex tmp = (Vertex) o;
            return tmp.lat == lat && tmp.lon == lon;
        }
        else
            return false;
    }

    /**
     * Return a unique hash code based on the latitude and longitude values
     *
     * @return The hash code
     */
    public int hashCode()
    {
        return new Double( lat ).hashCode() ^ new Double( lon ).hashCode();
    }

    /**
     * Returns a String description of the Vertex.
     *
     * @return The String description.
     */
    public String toString()
    {
        return "Lat = " + lat + ", Lon = " + lon;
    }

    private double ValidateLat( double Lat )
    {
        if( Lat > 90 )
            Lat = 90;
        if( Lat < -90 )
            Lat = -90;
        return Lat;
    }

    private double ValidateLon( double Lon )
    {
//        while( Lon > 180 ) Lon -= 360;

//        while( Lon < -180 ) Lon += 360;

        return Lon;
    }

    private double lat;
    private double lon;
}



