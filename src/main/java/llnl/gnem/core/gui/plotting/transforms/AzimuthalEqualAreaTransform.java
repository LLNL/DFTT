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
package llnl.gnem.core.gui.plotting.transforms;

import llnl.gnem.core.gui.plotting.AxisScale;

public class AzimuthalEqualAreaTransform implements CoordinateTransform {

    /**
     * Constructor that takes the origin of the coordinate system.
     *
     * @param originLat Latitude of origin in degrees
     * @param originLon Longitude of origin in degrees
     */
    public AzimuthalEqualAreaTransform(double originLat, double originLon) {
        phi1 = forceOriginLatInBounds(originLat) * DegreesToRadians;
        lambda0 = forceOriginLonInBounds(originLon) * DegreesToRadians;
        //     boundingRange = 171;
        boundingRange = 165;
    }

    /**
     * Initialize the CoordinateTransform object given the current dimensions of
     * the axis on which the plot is being rendered. This must be done at the
     * start of rendering when the plot has resized.
     *
     * @param minLat The minimum latitude in degrees
     * @param maxLat The maximum latitude in degrees
     * @param axisXmin The minimum (horizontal) pixel of the plot region
     * @param axisWidth The width of the plot region
     * @param minLon The minimum longitude in degrees ( -180 LTEQ minLon LTEQ
     * 180 )
     * @param maxLon The maximum longitude in degrees ( minLon LT maxLon LTEQ
     * 180 )
     * @param axisYmin The minimum (vertical) pixel of the plot region
     * @param axisHeight The height of the plot region
     */
    public void initialize(double minLat, double maxLat, int axisXmin, int axisWidth,
            double minLon, double maxLon, int axisYmin, int axisHeight) {

    }

    public void initialize(double originLat, double originLon, double degreeRadius, int axisXmin, int axisWidth,
            int axisYmin, int axisHeight) {
        double maxRadius = 180;
        double minRadius = 0.01;
        xCenter = axisXmin + axisWidth / 2;
        yCenter = axisYmin + axisHeight / 2;
        radiusInPixels = Math.min(axisWidth, axisHeight) / 2;

        degreeRadius = Math.abs(degreeRadius);
        if (degreeRadius >= maxRadius) {
            degreeRadius = maxRadius;
        }

        if (degreeRadius < minRadius) {
            degreeRadius = minRadius;
        }

        // Temporarily set origin to (0,0) in order to get scale factor...
        phi1 = 0;
        lambda0 = 0;
        sinphi1 = 0;
        cosphi1 = 1;
        setScaleFactor(degreeRadius);

        // Now set variables used for defining the bounding circle
        toXY(0, -180);
        double radius = Math.abs(x * scaleFactor);
        globeLeft = xCenter - radius;
        globeTop = yCenter - radius;
        globeDiameter = radius * 2;

        phi1 = forceOriginLatInBounds(originLat) * DegreesToRadians;
        lambda0 = forceOriginLonInBounds(originLon) * DegreesToRadians;
        sinphi1 = Math.sin(phi1);
        cosphi1 = Math.cos(phi1);
        width = axisWidth;
    }

    private double forceOriginLonInBounds(double lon) {
        while (lon > 360) {
            lon -= 360;
        }
        while (lon < -360) {
            lon += 360;
        }
        if (lon > 180) {
            lon -= 360;
        }

        if (lon < -180) {
            lon += 360;
        }
        return lon;
    }

    private double forceOriginLatInBounds(double lat) {
        if (lat > 90) {
            lat = 90;
        }
        if (lat < -90) {
            lat = -90;
        }
        return lat;
    }

    /**
     * Populates the plot part of the Coordinate object by applying the
     * transform from World to plot. Assumes the World part of the Coordinate
     * object has been set and the CoordinateTransform object has been
     * initialized.
     *
     * @param v The Coordinate object with its World values set. After
     * returning, the Plot values will be set. The World values are not
     * modified.
     */
    public void WorldToPlot(Coordinate v) {
        toXY(v.getWorldC1(), v.getWorldC2());
        v.setX(xCenter + x * scaleFactor);
        v.setY(yCenter - y * scaleFactor);
    }

    /**
     * Populates the world part of the Coordinate object by applying the
     * transform from plot to world. Assumes the plot part of the Coordinate
     * object has been set and the CoordinateTransform object has been
     * initialized.
     *
     * @param v The Coordinate object with its plot values set. After returning,
     * the world values will be set. The plot values are not modified.
     */
    public void PlotToWorld(Coordinate v) {
        double X = v.getX();
        double Y = v.getY();

        x = (X - xCenter) / scaleFactor;
        y = (yCenter - Y) / scaleFactor;
        // Force in bounds as necessary...
        double SS = x * x + y * y;
        if (SS > 4) {
            double scale = Math.sqrt(4 / SS);
            x *= scale;
            y *= scale;
        }

        toLatLon(x, y);
        v.setWorldC1(lat);
        v.setWorldC2(lon);
    }

    /**
     * Determine whether a point is in range for the purpose of plotting
     *
     * @param v A Coordinate object containing the point to be tested.
     * @return Returns true if the supplied coordinate is for a point greater
     * than the current bounding range.
     */
    public boolean isOutOfBounds(Coordinate v) {
        lat = v.getWorldC1() * DegreesToRadians;
        lon = v.getWorldC2() * DegreesToRadians;
        double sinlat = Math.sin(lat);
        double coslat = Math.cos(lat);
        double delta = Math.acos(sinphi1 * sinlat + cosphi1 * coslat * Math.cos(lambda0 - lon));
        return delta * RadiansToDegrees > boundingRange;
    }

    public void setXScale(AxisScale scale) {
        // No action needed
    }

    public void setYScale(AxisScale scale) {
        // No Action needed
    }

    public AxisScale getXScale() {
        return AxisScale.NA;
    }

    public AxisScale getYScale() {
        return AxisScale.NA;
    }

    public double getGlobeLeft() {
        return globeLeft;
    }

    public double getGlobeTop() {
        return globeTop;
    }

    public double getGlobeDiameter() {
        return globeDiameter;
    }

    public double getWorldDistance(Coordinate c1, Coordinate c2) {
        double lat1 = c1.getWorldC1();
        double lon1 = c1.getWorldC2();
        double lat2 = c2.getWorldC1();
        double lon2 = c2.getWorldC2();
        return getDelta(lat1, lon1, lat2, lon2);
    }

    private void toLatLon(double x, double y) {
        double rho = Math.sqrt(x * x + y * y);
        if (rho == 0) {
            lat = phi1 * RadiansToDegrees;
            lon = lambda0 * RadiansToDegrees;
            return;
        }

        double c = 2 * Math.asin(rho / 2);
        double sinc = Math.sin(c);
        double cosc = Math.cos(c);
        double phi = Math.asin(cosc * sinphi1 + y * sinc * cosphi1 / rho);
        lat = phi * RadiansToDegrees;

        double lambda;
        if (sinphi1 == 1) {
            lambda = lambda0 + Math.atan2(x, -y);
        } else if (sinphi1 == -1) {
            lambda = lambda0 + Math.atan2(x, y);
        } else {
            double num = x * sinc;
            double denom = rho * cosphi1 * cosc - y * sinphi1 * sinc;
            lambda = lambda0 + Math.atan2(num, denom);
        }
        lon = lambda * RadiansToDegrees;
    }

    private void toXY(double lat, double lon) {
        // There is a singularity for lat = -90 when the origin lat is 90
        if (lat == -90 && sinphi1 == 1) {
            lat = -89.99;
        }

        // There is a singularity for lat = -90 when the origin lat is -90
        if (lat == 90 && sinphi1 == -1) {
            lat = 89.99;
        }

        double phi = lat * DegreesToRadians;
        double lambda = lon * DegreesToRadians;
        double lambdadiff = lambda - lambda0;
        double cosdiff = Math.cos(lambdadiff);
        double sinphi = Math.sin(phi);
        double cosphi = Math.cos(phi);
        double denom = 1 + sinphi1 * sinphi + cosphi1 * cosphi * cosdiff;
        double xnum = cosphi * Math.sin(lambdadiff);
        double ynum = cosphi1 * sinphi - sinphi1 * cosphi * cosdiff;
        if (denom == 0) {
            if (cosphi1 == 0) {
                throw new IllegalStateException("This should not happen");
            }
            //     if( Math.abs( xnum ) < 1.0e-10 && Math.abs( ynum ) < 1.0e-10 ) {
            x = Math.sqrt(-4 * cosphi * cosdiff / cosphi1);
            if (lon < 0) {
                x *= -1;
            }
            y = -4 * (cosphi1 * sinphi - sinphi1 * cosphi * cosdiff) * sinphi1 / cosphi1;
            //     }
            //     else
            //         throw new IllegalStateException( "This should not happen!" );
        } else {
            double kp = Math.sqrt(2 / denom);
            x = xnum * kp;
            y = ynum * kp;
        }
        //    System.out.println( "Lat = " + lat + ", lon = " + lon + ", x = " + x + ", y = " + y );
    }

    private void setScaleFactor(double radius) {
        toXY(0.0, radius);
        scaleFactor = radiusInPixels / Math.sqrt(x * x + y * y);
    }

    private double getDelta(double lat1, double lon1, double lat2, double lon2) {
        double sinlat2 = Math.sin(lat2 * DegreesToRadians);
        double coslat2 = Math.cos(lat2 * DegreesToRadians);
        lon2 *= DegreesToRadians;
        lon1 *= DegreesToRadians;
        double sinlat1 = Math.sin(lat1 * DegreesToRadians);
        double coslat1 = Math.cos(lat1 * DegreesToRadians);
        double delta = Math.acos(sinlat1 * sinlat2 + coslat1 * coslat2 * Math.cos(lon1 - lon2));
        return delta * RadiansToDegrees;
    }

    @Override
    public int getWidthPixels() {
        return width;
    }

    private double phi1; // origin latitude in radians
    private double lambda0; // origin longitude in radians

    private double x; // x-coordinate before scaling and translating
    private double y; // y-coordinate before scaling and translating
    private double lat; // Conversion from x-y stored in instance variable lat - lon temporarily
    private double lon;
    private double sinphi1; // precompute this at initialization time
    private double cosphi1; // precompute this at initialization time
    private int width;

    private double scaleFactor; // Varies from 0 if requested radius is zero degrees to 1 if requested radius is 180 degrees.

    // Origin of pixel coordinates to which mapped data are referenced
    private int xCenter;
    private int yCenter;
    private int radiusInPixels;
    private double boundingRange;

    private double globeLeft;
    private double globeTop;
    private double globeDiameter;

    private final static double DegreesToRadians = Math.PI / 180.0;
    private final static double RadiansToDegrees = 180.0 / Math.PI;
}
