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
package llnl.gnem.dftt.core.traveltime;

/** 
 * A geocentric location on the surface of the earth, in longitude and
 * latitude degrees, with the addition of elevation in meters as the third
 * dimension.
 *
 * @author S. Myers
 * @version 1.0
 */
public class Point3D extends Point2D {

    public static Point3D NullPoint() {
        return new Point3D();
    }
    /**
     * Elevation (z coordinate) in meters.
     */
    private final float zElev;

    private Point3D() {
        super(Point2D.getNullPoint());
        zElev = Point2D.UNSET;

    }

    /**
     * @param xLon geodetic longitude (x coordinate) in degrees.
     * @param yLat geodetic latitude (y coordinate) in degrees.
     * @param zElev elevation (z coordinate) in meters.
     */
    public Point3D(float xLon, float yLat, float zElev) {
        super(xLon, yLat);
        this.zElev = zElev;
    }

    /**
     * @param geocentric determines whether lat lon are geocentric or geodetic
     * @param xLon longitude (x coordinate) in degrees.
     * @param yLat latitude (y coordinate) in degrees.
     * @param zElev elevation (z coordinate) in meters.
     */
    public Point3D(float xLon, float yLat, float zElev, boolean geocentric) {
        super(xLon, yLat, geocentric);
        this.zElev = zElev;
    }

    /**
     * Get the 2D location by ignoring the elevation.
     *
     * @return 2D location corresponding to this 3D location.
     */
    public Point2D getPoint2D() {
        return (new Point2D(this.getGeoLon(), this.getGeoLat()));
    }

    /**
     * Check if this location is equal to a second location, that is, are
     * their latitude, longitude, and elevation all the same?
     *
     * @param loc2 second location to compare.
     */
    public boolean equals(Point3D loc2) {
        if (zElev == loc2.zElev) {
            if (super.equals(loc2)) {
                return (true);
            }
        }
        return (false);
    }

    public boolean isSameLoc(Point3D h2, float toleranceKm) {
        boolean isSameLoc = false;
        if (super.isSameLoc(h2.getPoint2D(), toleranceKm)) {
            if ((this.zElev - h2.zElev) <= toleranceKm) {
                isSameLoc = true;
            }
        }
        return (isSameLoc);
    }

    public boolean isSameLoc(float lat, float lon, float elev, float toleranceKm) {
        Point3D h2 = new Point3D(lon, lat, elev);
        return (this.isSameLoc(h2, toleranceKm));
    }

    public float getzElev() {
        return zElev;
    }
}
