/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.traveltime;

/**
 * A geocentric location on the surface of the earth, in longitude and
 * latitude degrees.
 *
 * @author Steve Myers

 */
public class Point2D {
    public static final double CLOSE_ENOUGH = .00001;
    public static final double ELLIPTICITY = 0.99330552180201;
    public static final double EQUITORIAL_RADIUS = 6378137.0d;
    public static final double KM_PER_DEGREE = 111.18;
    public static final double POLAR_RADIUS = 6356752.0d;

    protected static final float UNSET = 999.0f;
    /** geocentric Longitude (x coordinate) in degrees. */
    private final float xLon;
    /** geocentric Latitude (y coordinate) in degrees. */
    private final float yLat;
    /** geodesic Longitude (x coordinate) in degrees. */
    private final float geoLon;
    /** geodesic Latitude (y coordinate) in degrees. */
    private final float geoLat;
    /** sine of the geocentric latitude. */
    private final double s1;
    /** cosine of the geocentric latitude. */
    private final double c1;

    public static Point2D getNullPoint() {
        return new Point2D();
    }

    private Point2D() {
        xLon = UNSET;
        yLat = UNSET;
        geoLon = UNSET;
        geoLat = UNSET;
        s1 = UNSET;
        c1 = UNSET;
    }
    
    public Point2D(Point2D source)
    {
        this.xLon = source.xLon;
        this.yLat = source.yLat;
        this.geoLon = source.geoLon;
        this.geoLat = source.geoLat;
        this.s1 = source.s1;
        this.c1 = source.c1;
    }

    /**
     * @param lon geodetic longitude (x coordinate) in degrees.
     * @param lat geodetic latitude (y coordinate) in degrees.
     */
    public Point2D(float lon, float lat) {
        this.geoLon = lon;
        this.geoLat = lat;
        this.xLon = lon;
        this.yLat = (float) Point2D.geodetic2geocentric((double) lat);
        // Convert to radians.
        double yLat1 = Math.toRadians(this.geoLat);

        // Calculate geocentric latitude.
        // Use GRS80 reference ellipsoid.
        // a = 6378137.0 meters (equatorial radius)
        // b = 6356752.0 meters (polar radius)
        // f = (b/a)^2 = 0.99330552180201

        // yLat is already geocentric
        yLat1 = Math.atan2(Math.cos(yLat1), ELLIPTICITY * Math.sin(yLat1));

        // Calculate trig.
        s1 = Math.sin(yLat1);
        c1 = Math.cos(yLat1);

    }

    /**
     * @param geocentric determines whether lat lon are geocentric or geodetic
     * @param lon longitude (x coordinate) in degrees.
     * @param lat  latitude (y coordinate) in degrees.
     */
    public Point2D(float lon, float lat, boolean geocentric) {
        if (geocentric) {
            this.xLon = lon;
            this.yLat = lat;
            this.geoLon = lon;
            this.geoLat = (float) Point2D.geocentric2geodetic((double) lat);
        } else {
            this.geoLon = lon;
            this.geoLat = lat;
            this.xLon = lon;
            this.yLat = (float) Point2D.geodetic2geocentric((double) lat);
        }
        double yLat1 = Math.toRadians(this.geoLat);
        yLat1 = Math.atan2(Math.cos(yLat1), ELLIPTICITY * Math.sin(yLat1));

        // Calculate trig.
        s1 = Math.sin(yLat1);
        c1 = Math.cos(yLat1);
    }

    /**
     * Returns geocentric lat (degrees) given geodetic lat (degrees)
     * Uses GRS80 equitorial and polar radii
     * @param lat
     * @return
     */
    public static double geodetic2geocentric(double lat) {
        return (Math.toDegrees(Math.atan((POLAR_RADIUS / EQUITORIAL_RADIUS) * (POLAR_RADIUS / EQUITORIAL_RADIUS)
                * Math.tan(Math.toRadians((double) lat)))));
    }

    /**
     * Returns geocentric lat (radians) given geodetic lat (radians)
     * Uses GRS80 equitorial and polar radii
     * @param lat
     * @return
     */
    public static double geocentric2geodetic(double lat) {
        return (Math.toDegrees(Math.atan((EQUITORIAL_RADIUS / POLAR_RADIUS) * (EQUITORIAL_RADIUS / POLAR_RADIUS)
                * Math.tan(Math.toRadians((double) lat)))));
    }

    /**
     * Check if this location is equal to a second location, that is, are
     * both their latitude and longitude the same?
     *
     * @param loc2 second location to compare.
     */
    public boolean equals(Point2D loc2) {
        if (xLon == loc2.xLon && yLat == loc2.yLat) {
            return (true);
        }
        return (false);
    }

    public boolean isSameLoc(Point2D h2, float toleranceKm) {
        boolean isSameLoc = false;
        float distDeg = this.dist_geog(h2);
        if (distDeg * KM_PER_DEGREE <= toleranceKm) {
            isSameLoc = true;
        }
        return (isSameLoc);

    }

    /**
     * Returns angular distance between this location and a second location,
     * utilizing the GRS80 reference ellipsoid:<br>
     * equatorial radius = 6378137.0 meters<br>
     * polar radius      = 6356752.0 meters<br><p>
     *
     * @param loc2 second location to compute the distance to.
     * @return distance between this location and the given location, in
     * degrees.
     */
    public float dist_geog(Point2D loc2) {
        // Optimize by saving values that will stay the same each time.
        //Are location values valid?
        if (this.yLat == UNSET || this.xLon == UNSET || loc2.yLat == UNSET || loc2.xLon == UNSET) {
            return UNSET;
        }
//    SCM LLNL 11/29/2000: If the locations are less the 1m apart then dist=0;
        if (Math.abs(geoLat - loc2.geoLat) < .00001 && Math.abs(xLon - loc2.xLon) < .00001) {
            return (0f);
        }

        // Convert to radians.
        double lon_dist = Math.toRadians(xLon - loc2.xLon);

        // Calculate trig.
        double s2 = loc2.getSinLat();
        double c2 = loc2.getCosLat();
        double cd = Math.cos(lon_dist);

        // Calculate distance.
        //return((float)(Math.toDegrees(Math.acos ( c1*c2 + s1*s2*cd ))));
        return ((float) (Math.toDegrees(Math.acos(c1 * c2 + s1 * s2 * cd))));

    }

    /**
     * Returns azimuth from this location to the input location,
     * utilizing the GRS80 reference ellipsoid:<br>
     * equatorial radius = 6378137.0 meters<br>
     * polar radius      = 6356752.0 meters<br><p>
     *
     * @param loc2 second location to compute the distance to.
     * @return azimuth between this location and the given location, in
     * degrees.
     */
    public float baz_geog(Point2D loc2) {
        // Optimize by saving values that will stay the same each time.
        //Are location values valid?
        if (this.yLat == UNSET || this.xLon == UNSET || loc2.yLat == UNSET || loc2.xLon == UNSET) {
            return UNSET;
        }
//    SCM LLNL 11/29/2000: If the locations are less the 1m apart then az=0;
        if (Math.abs(geoLat - loc2.geoLat) < CLOSE_ENOUGH && Math.abs(xLon - loc2.xLon) < CLOSE_ENOUGH) {
            return (0f);
        }

        // Convert to radians.
        double lon_dist = Math.toRadians(xLon - loc2.xLon);

        // Calculate trig.
        double s2 = loc2.getSinLat();
        double c2 = loc2.getCosLat();
        double cd = Math.cos(lon_dist);
        double sd = Math.sin(lon_dist);

        // Calculate azimuth.
        float az = (float) (Math.toDegrees(Math.atan2(-s2 * sd, s1 * c2 - c1 * s2 * cd)));
        if (az < 0) {
            az = az + 360;
        }
        return (az);
    }

    /**
     *
     * @param lat1  geodetic
     * @param lon1
     * @param lat2  geodetic
     * @param lon2
     * @return
     */
    public static float dist_geog(float lat1, float lon1, float lat2, float lon2) {
        // Convert to radians.
        double s1, s2, c1, c2, cd;
        // Calculate geocentric latitude.
        // Use GRS80 reference ellipsoid.
        // a = 6378137.0 meters (equatorial radius)
        // b = 6356752.0 meters (polar radius)
        // f = (b/a)^2 = 0.99330552180201

        // Convert to radians.

        double yLat1 = Math.toRadians(lat1);
        double yLat2 = Math.toRadians(lat2);
        if (Math.abs(lon1 - lon2) < CLOSE_ENOUGH && Math.abs(lat1 - lat2) < CLOSE_ENOUGH) {
            return (0f);
        }
        double lon_dist = Math.toRadians(lon1 - lon2);
        yLat1 = Math.atan2(Math.cos(yLat1), ELLIPTICITY * Math.sin(yLat1));
        yLat2 = Math.atan2(Math.cos(yLat2), ELLIPTICITY * Math.sin(yLat2));

        // double temp1 = 90-Math.toDegrees(yLat1);
        // double temp2 = 90-Math.toDegrees(yLat2);

        // Calculate trig.
        s1 = Math.sin(yLat1);
        c1 = Math.cos(yLat1);
        s2 = Math.sin(yLat2);
        c2 = Math.cos(yLat2);
        cd = Math.cos(lon_dist);

        // Calculate distance.
        return ((float) (Math.toDegrees(Math.acos(c1 * c2 + s1 * s2 * cd))));
    }

    public static float baz_geog(float lat1, float lon1, float lat2, float lon2) {
        // Convert to radians.
        double s1, s2, c1, c2, cd, sd;
        // Calculate geocentric latitude.
        // Use GRS80 reference ellipsoid.
        // a = 6378137.0 meters (equatorial radius)
        // b = 6356752.0 meters (polar radius)
        // f = (b/a)^2 = 0.99330552180201

        // Convert to radians.
        double yLat1 = Math.toRadians(lat1);
        double yLat2 = Math.toRadians(lat2);
        double lon_dist = Math.toRadians(lon1 - lon2);
        yLat1 = Math.atan2(Math.cos(yLat1), ELLIPTICITY * Math.sin(yLat1));
        yLat2 = Math.atan2(Math.cos(yLat2), ELLIPTICITY * Math.sin(yLat2));


        // Calculate trig.
        s1 = Math.sin(yLat1);
        c1 = Math.cos(yLat1);
        s2 = Math.sin(yLat2);
        c2 = Math.cos(yLat2);
        cd = Math.cos(lon_dist);
        sd = Math.cos(lon_dist);

        // Calculate azimuth.
        float az = (float) (Math.toDegrees(Math.atan2(-s2 * sd, s1 * c2 - c1 * s2 * cd)));
        if (az < 0) {
            az = az + 360;
        }
        return (az);
    }

    /**
     * Get the sine of the geocentric latitude.
     *
     * @return sine of the geocentric latitude.
     */
    public double getSinLat() {
        return (s1);
    }

    /**
     * Get the cosine of the geocentric latitude.
     *
     * @return cosine of the geocentric latitude.
     */
    public double getCosLat() {
        return (c1);
    }

    public float getxLon() {
        return xLon;
    }

    public float getyLat() {
        return yLat;
    }

    public float getGeoLon() {
        return geoLon;
    }

    public float getGeoLat() {
        return geoLat;
    }
}
