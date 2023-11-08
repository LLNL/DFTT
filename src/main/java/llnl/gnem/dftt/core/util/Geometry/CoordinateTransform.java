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
package llnl.gnem.dftt.core.util.Geometry;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import llnl.gnem.dftt.core.util.FileUtil.FileManager;
import llnl.gnem.dftt.core.util.MathFunctions.MathFunction;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;


/**
 * User: Eric Matzel Date: Mar 23, 2010
 */
public class CoordinateTransform
{

    private final static double HALF_CIRCLE = 180.0;
    public final static double RADIANS_TO_DEGREES = HALF_CIRCLE / Math.PI;
    public final static double DEGREES_TO_RADIANS = Math.PI / HALF_CIRCLE;

    /**
     * Spherical Coordinates to Cartesian Coordinates
     *
     * @param theta angle x to y (a.k.a. longitude in RADIANS)
     * @param phi angle from x-y plane to z (a.k.a. latitude in RADIANS)
     * @param radius length of the vector from the origin at (0,0,0)
     * @return a Point3d object in Cartesian coordinates
     */
    public static Vector3D SphericalToCartesian(double theta, double phi, double radius)
    {
        double x = radius * Math.cos(phi) * Math.cos(theta);
        double y = radius * Math.cos(phi) * Math.sin(theta);
        double z = radius * Math.sin(phi);

        Vector3D result = new Vector3D(x, y, z);

        return result;
    }

    /**
     * Spherical Coordinates to Cartesian Coordinates
     *
     * @param point - a Point3d object in format point.x = theta, point.y = phi,
     * point.z = radius
     *
     * Note theta and phi need to be in RADIANS
     * @return a Point3d object in cartesian x,y,z coordinates
     */
    public static Vector3D SphericalToCartesian(Vector3D point)
    {
        double theta = point.getX();
        double phi = point.getY();
        double radius = point.getZ();

        return SphericalToCartesian(theta, phi, radius);
    }

    /**
     * Cartesian coordinates x,y,z to Spherical coordinates theta, phi, r
     *
     * @param point a Point3d object x,y,z
     * @return a Point3d object in format point.x = theta, point.y = phi,
     * point.z = radius (theta and phi in RADIANS)
     */
    public static Vector3D CartesianToSpherical(Vector3D point)
    {
        return CartesianToSpherical(point.getX(), point.getY(), point.getZ());
    }

    /**
     * Cartesian coordinates x,y,z to Spherical coordinates theta, phi, r
     *
     * @param x
     * @param y
     * @param z
     * @return a Point3d object in format point.x = theta, point.y = phi,
     * point.z = r (theta and phi in RADIANS)
     */
    public static Vector3D CartesianToSpherical(double x, double y, double z)
    {
        double theta = Math.atan2(y, x);
        double phi = Math.atan2(z, Math.sqrt(x * x + y * y));
        double radius = Math.sqrt(x * x + y * y + z * z);

        Vector3D result = new Vector3D(theta, phi, radius);

        return result;
    }

    /**
     * Cartesian coordinates x,y,z to Polar coordinates theta, rho, z
     *
     * @param point a Point3d object x,y,z
     * @return a Point3d object in format point.x = theta, point.y = rho,
     * point.z = z
     */
    public static Vector3D CartesianToPolar(Vector3D point)
    {
        return CartesianToPolar(point.getX(), point.getY(), point.getZ());
    }

    /**
     * Cartesian coordinates x,y,z to Polar coordinates theta, rho, z
     *
     * @param x
     * @param y
     * @param z
     * @return a Point3d object in format point.x = theta, point.y = rho,
     * point.z = z
     */
    public static Vector3D CartesianToPolar(double x, double y, double z)
    {
        double theta = Math.atan2(y, x);
        double rho = Math.sqrt(x * x + y * y);

        Vector3D result = new Vector3D(theta, rho, z);
        return result;
    }

    /**
     * Cartesian coordinates x,y to Polar coordinates theta, phi, z = 0
     *
     * @param x
     * @param y
     * @return a Point3d object in format point.x = theta, point.y = rho,
     * point.z = z
     */
    public static Vector3D CartesianToPolar(double x, double y)
    {
        return CartesianToPolar(x, y, 0);
    }

    /**
     * Polar coordinates theta, rho, z to Cartesian coordinates x,y,z
     *
     * @param theta angle from x to y
     * @param rho distance from origin (0,0,0)
     * @param z distance along the z-axis
     * @return a Point3d object (x,y,z)
     */
    public static Vector3D PolarToCartesian(double theta, double rho, double z)
    {
        System.out.println("PolarToCartesian code not written");

        double x = rho * Math.cos(theta);
        double y = rho * Math.sin(theta);
        // z = z

        Vector3D result = new Vector3D(x, y, z);
        return result;
    }

    /**
     * Polar coordinates theta, rho, (z = 0) to Cartesian coordinates x, y, (z =
     * 0)
     *
     * @param theta angle from x to y
     * @param rho distance from origin (0,0)
     * @return a Point3d object (x,y,0)
     */
    public static Vector3D PolarToCartesian(double theta, double rho)
    {
        return PolarToCartesian(theta, rho, 0);
    }

    /**
     * Polar coordinates theta, rho, (z = 0) to Cartesian coordinates x, y, z
     *
     * @param point a Point3d object in format point.x = theta, point.y = rho,
     * point.z = z
     * @return a Point3d object (x,y,z)
     */
    public static Vector3D PolarToCartesian(Vector3D point)
    {
        double theta = point.getX();
        double rho = point.getY();
        double z = point.getZ();

        return PolarToCartesian(theta, rho, z);
    }

    /**
     * A convenience method to allow SphericalToCartesian conversion from
     * longitude, latitude (DEGREES) instead of theta,phi (RADIANS)
     *
     * @param longitude - the longitude in DEGREES
     * @param latitude - the latitude in DEGREES
     * @param radius - the radius to the point in space NOTE: units of radius (e.g. meters, km, miles) determine the units of XYZ
     * @return a Point3d object (x,y,z)
     */
    public static Vector3D LonLatRadiusToXYZ(double longitude, double latitude, double radius)
    {
        double theta = longitude * DEGREES_TO_RADIANS;
        double phi = latitude * DEGREES_TO_RADIANS;

        return SphericalToCartesian(theta, phi, radius);
    }

    /**
     * A convenience method to allow SphericalToCartesian conversion from
     * longitude, latitude (DEGREES) instead of theta,phi (RADIANS)
     * NOTE: units of radius (e.g. meters, km, miles) determine the units of XYZ
     *
     * @param lonlatradpoint = a Point3d object (longitude, latitude, radius)
     * @return a Point3d object (x,y,z)
     */
    public static Vector3D LonLatRadiusToXYZ(Vector3D lonlatradpoint)
    {
        double longitude = lonlatradpoint.getX();
        double latitude = lonlatradpoint.getY();
        double radius = lonlatradpoint.getZ();

        return LonLatRadiusToXYZ(longitude, latitude, radius);
    }

    /**
     * A convenience method to allow CartesianToSpherical conversion from X,Y,Z
     * to longitude, latitude (DEGREES) and radius
     * NOTE: units of xyz (e.g. meters, km, miles) determine the units of radius
     *
     * @param x
     * @param y
     * @param z
     * @return a Point3d object (longitude, latitude, radius)
     */
    public static Vector3D XYZToLonLatRadius(double x, double y, double z)
    {
        Vector3D result = CartesianToSpherical(x, y, z);

        return new Vector3D(result.getX() * RADIANS_TO_DEGREES, result.getY() * RADIANS_TO_DEGREES, result.getZ() );
    }

    /**
     * A convenience method to allow CartesianToSpherical conversion from X,Y,Z
     * to longitude, latitude (DEGREES) and radius
     *
     * @param xyzpoint - a Point3d object (X, Y, Z)
     * @return a Point3d object (longitude, latitude, radius)
     */
    public static Vector3D XYZToLonLatRadius(Vector3D xyzpoint)
    {
        return XYZToLonLatRadius(xyzpoint.getX(), xyzpoint.getY(), xyzpoint.getZ());
    }

    public static double GeodeticToGeocentricLatitude(double geodeticlatitude)
    {
        double DegreesToRadians = Math.PI / 180.;
        double ecc = 0.081819190842621; // WGS84 eccentricity
        double phi = geodeticlatitude * DegreesToRadians;

        double a = (1 - (ecc * ecc)) * Math.sin(phi);
        double b = Math.cos(phi);
        double geocentriclatitude = Math.atan2(a, b) / DegreesToRadians;
        return geocentriclatitude;
    }

    public static double GeocentricToGeodeticLatitude(double geocentriclatitude)
    {
        double DegreesToRadians = Math.PI / 180.;
        double ecc = 0.081819190842621; // WGS84 eccentricity
        double phi = geocentriclatitude * DegreesToRadians;

        double a = (1 - (ecc * ecc)) * Math.cos(phi);
        double b = Math.sin(phi);
        double geodeticlatitude = Math.atan2(b, a) / DegreesToRadians;
        return geodeticlatitude;
    }

    /**
     * Convert from Longitude Latitude coordinate system to the Universal
     * Transverse Mercator (UTM) System
     *
     * @param longitude
     * @param latitude
     * @return
     */
    public static String LonLatToUTM(double longitude, double latitude)
    {
        //TODO System.out.println("Still developing LonLatToUTM");

        CoordinateConversion coordconv = new CoordinateConversion();
        String UTM = coordconv.latLon2UTM(latitude, longitude);
        return UTM;
        // Note want a different return type
    }

    /**
     * Convert from the Universal Transverse Mercator System to Longitude,
     * Latitude
     *
     * @param easting
     * @param northing
     * @param Zone
     * @param NS
     * @return
     */
    public static double[] UTMToLonLat(double easting, double northing, int longZone, String latZone)
    {
        //TODO System.out.println("Still developing UTMToLonLat");

        String UTM = "";
        UTM = longZone + " " + latZone + " " + ((int) easting) + " " + ((int) northing);

        CoordinateConversion coordconv = new CoordinateConversion();
        return coordconv.utm2LatLon(UTM);

        // Note want a different return type
    }

    //-----TODO move the following methods to more appropriate classes-------------------------------------------------------------
    static void createHemisphereSources(double centerlat, double centerlon, double radius, double hemisphereradius, int maxpoints)
    {
        //int maxpoints = 10000; // create 10,000 points
        //double centerlat = 40;
        //double centerlon = -121;
        //double radius = 6371; radius to center of the earth

        Vector3D xyzresult = LonLatRadiusToXYZ(centerlon, centerlat, radius);

        double x0 = xyzresult.getX();
        double y0 = xyzresult.getY();
        double z0 = xyzresult.getZ();

        double r = hemisphereradius;

        System.out.println(x0 + "\t" + y0 + "\t" + z0);

        int index = 0;
        while (index < maxpoints)
        {
            double dx = MathFunction.randomBetween(-r, r); // creates a random

            double dymax = Math.sqrt(r * r - dx * dx); // y^2 = r^2 - x^2
            double dy = MathFunction.randomBetween(-dymax, dymax);

            double dz = Math.sqrt(dymax * dymax - dy * dy); //z^2 = r^2 - x^2 - y^2  Note this only uses the positive half of the result space.

            double x = x0 + dx;
            double y = y0 + dy;
            double z1 = z0 + dz; // Note minus sign means that radius will be smaller. depth is greater
            double z2 = z0 - dz;

            Vector3D lonlatradius1 = XYZToLonLatRadius(x, y, z1);
            Vector3D lonlatradius2 = XYZToLonLatRadius(x, y, z2);

            // check whether hemisphere's radius is above the original radius
            // save only the portion of the sphere that falls below the reference radius
            if (lonlatradius1.getZ() < radius) // forces the hemisphere to fall below the original radius point
            {
                //System.out.println(x + "\t" + y + "\t" + z1 + "\t" + dx + "\t" + dy + "\t" + dz + "\t" + lonlatradius1.x + "\t" + lonlatradius1.y + "\t" + lonlatradius1.z);
                writeSW4source(lonlatradius1.getX(), lonlatradius1.getY(), 1000 * (radius - lonlatradius1.getZ()));
                index = index + 1;
            }
            if (lonlatradius2.getZ() < radius) // forces the hemisphere to fall below the original radius point
            {
                //System.out.println(x + "\t" + y + "\t" + z2 + "\t" + dx + "\t" + dy + "\t" + dz + "\t" + lonlatradius2.x + "\t" + lonlatradius2.y + "\t" + lonlatradius2.z);
                writeSW4source(lonlatradius2.getX(), lonlatradius2.getY(), 1000 * (radius - lonlatradius2.getZ())); // note conversion from radius in km to depth in m
                index = index + 1;
            }

        }
    }

    /**
     * Write SW4/WPP style source output
     */
    static void writeSW4source(double longitude, double latitude, double depth)
    {
        String dfmt4 = "%-10.4f";// Note -10.4f aligns to the left +10.4.f aligns to right

        double t0 = 0.100;
        double M0 = 1e10;
        double mxx = 1. / 3.;
        double myy = 1. / 3.;
        double mzz = 1. / 3.;
        double mxy = 0.;
        double mxz = 0.;
        double myz = 0.;

        depth = depth - 1500; //TODO replace this is a special case for Newberry - reference radius MSL versus ~min surface radius

        String sourceline = "source lon=" + String.format(dfmt4, longitude)
                + "  lat=" + String.format(dfmt4, latitude)
                + "  z=" + String.format(dfmt4, depth)
                + "  type=Dirac t0=" + t0
                + "  m0=" + M0
                + "  mxx=" + String.format(dfmt4, mxx)
                + "  mxy=" + String.format(dfmt4, mxy)
                + "  myy=" + String.format(dfmt4, myy)
                + "  mxz=" + String.format(dfmt4, mxz)
                + "  myz=" + String.format(dfmt4, myz)
                + "  mzz=" + String.format(dfmt4, mzz);

        System.out.println(sourceline);
    }

}

/**
 * Conversion to/from Swiss projection to Latitude Longitude
 *
 * Based on the equations in "Formulas and constants for the calculation of the
 * Swiss conformal cylindrical projection and for the transformation between
 * coordinate systems" published by swisstopo, May 2008
 *
 * @author matzel1
 */
class SwissProjection
{

    /**
     * 4 Approximate solution for the transformation CH1903 ⇔ WGS84
     */
    /**
     * Approximate method (from section 4.1), should be accurate to ~ 1 meter
     *
     * formulas for the direct transformation of: ellipsoidal ETRS89 or WGS84
     * coordinates to Swiss projection coordinates
     *
     * @param longitude in decimal degrees
     * @param latitude in decimal degrees
     * @return Easting, Northing in meters
     *
     */
    public static double[] LonLatToSwissProjection(double longitude, double latitude)
    {

        //1. The latitudes phi and longitudes lambda have to be converted into arc seconds ["] 
        double latsec = latitude * 3600;
        double lonsec = longitude * 3600;

        //2. The following auxiliary values have to be calculated (differences of latitude and longitude relative to the projection centre in Bern in the unit [10000"]): 
        double phiprime = (latsec - 169028.66) / 10000;
        double lambdaprime = (lonsec - 26782.5) / 10000;
        double lambdaprime2 = lambdaprime * lambdaprime;
        double phiprime2 = phiprime * phiprime;
        double lambdaprime3 = lambdaprime2 * lambdaprime;
        double phiprime3 = phiprime2 * phiprime;

        double Easting = 600072.37 + 211455.93 * lambdaprime - 10938.51 * lambdaprime * phiprime - 0.36 * lambdaprime * phiprime2 - 44.54 * lambdaprime3;// in meters
        double Northing = 200147.07 + 308807.95 * phiprime + 3745.25 * lambdaprime2 + 76.63 * phiprime2 - 194.56 * lambdaprime2 * phiprime + 119.79 * phiprime3;// in meters

        double[] result =
        {
            Easting, Northing
        };
        return result;

    }

    /**
     * Approximate method (from section 4.2). Should be accurate to
     * approximately 0.12" longitude and 0.08" in latitude
     *
     * formulas for the direct transformation of: Swiss projection coordinates
     * to ellipsoidal ETRS89 or WGS84 coordinates
     *
     * @param Easting : in meters
     * @param Northing: in meters
     * @return latitude, longitude in decimal degrees
     *
     */
    public static double[] SwissProjectionToLonLat(double Easting, double Northing)
    {
        //1. The projection coordinates y (easting) and x (northing) have to be converted into the civilian system (Bern = 0 / 0) and have to expressed in the unit [1000 km] : 
        double yprime = (Easting - 600000) / 1000000; // units from meters to 1000 km;
        double xprime = (Northing - 200000) / 1000000; // units from meters to 1000 km;
        double yprime2 = yprime * yprime;
        double xprime2 = xprime * xprime;
        double yprime3 = yprime2 * yprime;
        double xprime3 = xprime2 * xprime;

        //2. The longitude and latitude have to be calculated in the unit [10000"] 
        double lambdaprime = 2.6779094 + 4.728982 * yprime + 0.791484 * yprime * xprime + 0.1306 * yprime * xprime2 - 0.0436 * yprime3;
        double phiprime = 16.9023892 + 3.238272 * xprime - 0.270978 * yprime2 - 0.002528 * xprime2 - 0.0447 * yprime2 * xprime - 0.0140 * xprime3;

        //3. Longitude and latitude have to be converted to the unit [°] 
        double longitude = lambdaprime * 100 / 36;
        double latitude = phiprime * 100 / 36;

        double[] result =
        {
            longitude, latitude
        };

        return result;
    }

    /**
     * A utility test method to read an East,North catalog and return the
     * equivalent longitudes and latitudes.
     */
    public static void BaselCatalog()
    {
        File file = FileManager.openFile("Open a catalog file", null, null)[0];

        ArrayList<String> textrowlist = FileManager.createTextRowCollection(file);
        for (String row : textrowlist)
        {
            try
            {
                StringTokenizer tokenizer = new StringTokenizer(row);
                long evid = Long.parseLong(tokenizer.nextToken());
                double Easting = Double.parseDouble(tokenizer.nextToken()); // a number in meters ~ 600,000
                double Northing = Double.parseDouble(tokenizer.nextToken());// a number in meters ~ 200,000 
                double Zum_elevation = Double.parseDouble(tokenizer.nextToken()); // Basel catelog is m below sea level. z positive downward

                // calculate the latitude and longitude here
                double[] lonlat = SwissProjectionToLonLat(Easting, Northing);

                System.out.println(row + "\t" + lonlat[0] + "\t" + lonlat[1] +"\t");

            }
            catch (Exception e)
            {
            }
        }

    }
}

/*
 * Author: Sami Salkosuo, sami.salkosuo@fi.ibm.com
 *
 * (c) Copyright IBM Corp. 2007
 */
class CoordinateConversion
{

    public CoordinateConversion()
    {

    }

    public double[] utm2LatLon(String UTM)
    {
        UTM2LatLon c = new UTM2LatLon();
        return c.convertUTMToLatLong(UTM);
    }

    public String latLon2UTM(double latitude, double longitude)
    {
        LatLon2UTM c = new LatLon2UTM();
        return c.convertLatLonToUTM(latitude, longitude);

    }

    private void validate(double latitude, double longitude)
    {
        if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0
                || longitude >= 180.0)
        {
            throw new IllegalArgumentException(
                    "Legal ranges: latitude [-90,90], longitude [-180,180).");
        }

    }

    public String latLon2MGRUTM(double latitude, double longitude)
    {
        LatLon2MGRUTM c = new LatLon2MGRUTM();
        return c.convertLatLonToMGRUTM(latitude, longitude);

    }

    public double[] mgrutm2LatLon(String MGRUTM)
    {
        MGRUTM2LatLon c = new MGRUTM2LatLon();
        return c.convertMGRUTMToLatLong(MGRUTM);
    }

    public double degreeToRadian(double degree)
    {
        return degree * Math.PI / 180;
    }

    public double radianToDegree(double radian)
    {
        return radian * 180 / Math.PI;
    }

    private double POW(double a, double b)
    {
        return Math.pow(a, b);
    }

    private double SIN(double value)
    {
        return Math.sin(value);
    }

    private double COS(double value)
    {
        return Math.cos(value);
    }

    private double TAN(double value)
    {
        return Math.tan(value);
    }

    public String getLongZone(double longitude)
    {
        LatLon2UTM c = new LatLon2UTM();
        return c.getLongZone(longitude);
    }

    public String getLatZone(double latitude)
    {
        //if (latitude > 0)
        {
            //    return "N";
        }
        //else
        {
            LatZones latZones = new LatZones();
            return latZones.getLatZone(latitude);
        }
    }

    private class LatLon2UTM
    {

        public String convertLatLonToUTM(double latitude, double longitude)
        {
            validate(latitude, longitude);
            String UTM = "";

            setVariables(latitude, longitude);

            String longZone = getLongZone(longitude);
            LatZones latZones = new LatZones();
            String latZone = latZones.getLatZone(latitude);

            double _easting = getEasting();
            double _northing = getNorthing(latitude);

            UTM = longZone + " " + latZone + " " + ((int) _easting) + " "
                    + ((int) _northing);
            // UTM = longZone + " " + latZone + " " + decimalFormat.format(_easting) +
            // " "+ decimalFormat.format(_northing);

            return UTM;

        }

        protected void setVariables(double latitude, double longitude)
        {
            latitude = degreeToRadian(latitude);
            rho = equatorialRadius * (1 - e * e)
                    / POW(1 - POW(e * SIN(latitude), 2), 3 / 2.0);

            nu = equatorialRadius / POW(1 - POW(e * SIN(latitude), 2), (1 / 2.0));

            double var1;
            if (longitude < 0.0)
            {
                var1 = ((int) ((180 + longitude) / 6.0)) + 1;
            }
            else
            {
                var1 = ((int) (longitude / 6)) + 31;
            }
            double var2 = (6 * var1) - 183;
            double var3 = longitude - var2;
            p = var3 * 3600 / 10000;

            S = A0 * latitude - B0 * SIN(2 * latitude) + C0 * SIN(4 * latitude) - D0
                    * SIN(6 * latitude) + E0 * SIN(8 * latitude);

            K1 = S * k0;
            K2 = nu * SIN(latitude) * COS(latitude) * POW(sin1, 2) * k0 * (100000000)
                    / 2;
            K3 = ((POW(sin1, 4) * nu * SIN(latitude) * Math.pow(COS(latitude), 3)) / 24)
                    * (5 - POW(TAN(latitude), 2) + 9 * e1sq * POW(COS(latitude), 2) + 4
                    * POW(e1sq, 2) * POW(COS(latitude), 4))
                    * k0
                    * (10000000000000000L);

            K4 = nu * COS(latitude) * sin1 * k0 * 10000;

            K5 = POW(sin1 * COS(latitude), 3) * (nu / 6)
                    * (1 - POW(TAN(latitude), 2) + e1sq * POW(COS(latitude), 2)) * k0
                    * 1000000000000L;

            A6 = (POW(p * sin1, 6) * nu * SIN(latitude) * POW(COS(latitude), 5) / 720)
                    * (61 - 58 * POW(TAN(latitude), 2) + POW(TAN(latitude), 4) + 270
                    * e1sq * POW(COS(latitude), 2) - 330 * e1sq
                    * POW(SIN(latitude), 2)) * k0 * (1E+24);

        }

        protected String getLongZone(double longitude)
        {
            double longZone = 0;
            if (longitude < 0.0)
            {
                longZone = ((180.0 + longitude) / 6) + 1;
            }
            else
            {
                longZone = (longitude / 6) + 31;
            }
            String val = String.valueOf((int) longZone);
            if (val.length() == 1)
            {
                val = "0" + val;
            }
            return val;
        }

        protected double getNorthing(double latitude)
        {
            double northing = K1 + K2 * p * p + K3 * POW(p, 4);
            if (latitude < 0.0)
            {
                northing = 10000000 + northing;
            }
            return northing;
        }

        protected double getEasting()
        {
            return 500000 + (K4 * p + K5 * POW(p, 3));
        }

        // Lat Lon to UTM variables
        // equatorial radius
        double equatorialRadius = 6378137;

        // polar radius
        double polarRadius = 6356752.314;

        // flattening
        double flattening = 0.00335281066474748;// (equatorialRadius-polarRadius)/equatorialRadius;

        // inverse flattening 1/flattening
        double inverseFlattening = 298.257223563;// 1/flattening;

        // Mean radius
        double rm = POW(equatorialRadius * polarRadius, 1 / 2.0);

        // scale factor
        double k0 = 0.9996;

        // eccentricity
        double e = Math.sqrt(1 - POW(polarRadius / equatorialRadius, 2));

        double e1sq = e * e / (1 - e * e);

        double n = (equatorialRadius - polarRadius)
                / (equatorialRadius + polarRadius);

        // r curv 1
        double rho = 6368573.744;

        // r curv 2
        double nu = 6389236.914;

        // Calculate Meridional Arc Length
        // Meridional Arc
        double S = 5103266.421;

        double A0 = 6367449.146;

        double B0 = 16038.42955;

        double C0 = 16.83261333;

        double D0 = 0.021984404;

        double E0 = 0.000312705;

        // Calculation Constants
        // Delta Long
        double p = -0.483084;

        double sin1 = 4.84814E-06;

        // Coefficients for UTM Coordinates
        double K1 = 5101225.115;

        double K2 = 3750.291596;

        double K3 = 1.397608151;

        double K4 = 214839.3105;

        double K5 = -2.995382942;

        double A6 = -1.00541E-07;

    }

    private class LatLon2MGRUTM extends LatLon2UTM
    {

        public String convertLatLonToMGRUTM(double latitude, double longitude)
        {
            validate(latitude, longitude);
            String mgrUTM = "";

            setVariables(latitude, longitude);

            String longZone = getLongZone(longitude);
            LatZones latZones = new LatZones();
            String latZone = latZones.getLatZone(latitude);

            double _easting = getEasting();
            double _northing = getNorthing(latitude);
            Digraphs digraphs = new Digraphs();
            String digraph1 = digraphs.getDigraph1(Integer.parseInt(longZone),
                    _easting);
            String digraph2 = digraphs.getDigraph2(Integer.parseInt(longZone),
                    _northing);

            String easting = String.valueOf((int) _easting);
            if (easting.length() < 5)
            {
                easting = "00000" + easting;
            }
            easting = easting.substring(easting.length() - 5);

            String northing;
            northing = String.valueOf((int) _northing);
            if (northing.length() < 5)
            {
                northing = "0000" + northing;
            }
            northing = northing.substring(northing.length() - 5);

            mgrUTM = longZone + latZone + digraph1 + digraph2 + easting + northing;
            return mgrUTM;
        }
    }

    private class MGRUTM2LatLon extends UTM2LatLon
    {

        public double[] convertMGRUTMToLatLong(String mgrutm)
        {
            double[] latlon =
            {
                0.0, 0.0
            };
            // 02CNR0634657742
            int zone = Integer.parseInt(mgrutm.substring(0, 2));
            String latZone = mgrutm.substring(2, 3);

            String digraph1 = mgrutm.substring(3, 4);
            String digraph2 = mgrutm.substring(4, 5);
            easting = Double.parseDouble(mgrutm.substring(5, 10));
            northing = Double.parseDouble(mgrutm.substring(10, 15));

            LatZones lz = new LatZones();
            double latZoneDegree = lz.getLatZoneDegree(latZone);

            double a1 = latZoneDegree * 40000000 / 360.0;
            double a2 = 2000000 * Math.floor(a1 / 2000000.0);

            Digraphs digraphs = new Digraphs();

            double digraph2Index = digraphs.getDigraph2Index(digraph2);

            double startindexEquator = 1;
            if ((1 + zone % 2) == 1)
            {
                startindexEquator = 6;
            }

            double a3 = a2 + (digraph2Index - startindexEquator) * 100000;
            if (a3 <= 0)
            {
                a3 = 10000000 + a3;
            }
            northing = a3 + northing;

            zoneCM = -183 + 6 * zone;
            double digraph1Index = digraphs.getDigraph1Index(digraph1);
            int a5 = 1 + zone % 3;
            double[] a6 =
            {
                16, 0, 8
            };
            double a7 = 100000 * (digraph1Index - a6[a5 - 1]);
            easting = easting + a7;

            setVariables();

            double latitude = 0;
            latitude = 180 * (phi1 - fact1 * (fact2 + fact3 + fact4)) / Math.PI;

            if (latZoneDegree < 0)
            {
                latitude = 90 - latitude;
            }

            double d = _a2 * 180 / Math.PI;
            double longitude = zoneCM - d;

            if (getHemisphere(latZone).equals("S"))
            {
                latitude = -latitude;
            }

            latlon[0] = latitude;
            latlon[1] = longitude;
            return latlon;
        }
    }

    private class UTM2LatLon
    {

        double easting;

        double northing;

        int zone;

        String southernHemisphere = "ACDEFGHJKLM";

        protected String getHemisphere(String latZone)
        {
            String hemisphere = "N";
            if (southernHemisphere.indexOf(latZone) > -1)
            {
                hemisphere = "S";
            }
            return hemisphere;
        }

        public double[] convertUTMToLatLong(String UTM)
        {
            double[] latlon =
            {
                0.0, 0.0
            };
            String[] utm = UTM.split(" ");
            zone = Integer.parseInt(utm[0]);
            String latZone = utm[1];
            easting = Double.parseDouble(utm[2]);
            northing = Double.parseDouble(utm[3]);
            String hemisphere = getHemisphere(latZone);
            double latitude = 0.0;
            double longitude = 0.0;

            if (hemisphere.equals("S"))
            {
                northing = 10000000 - northing;
            }
            setVariables();
            latitude = 180 * (phi1 - fact1 * (fact2 + fact3 + fact4)) / Math.PI;

            if (zone > 0)
            {
                zoneCM = 6 * zone - 183.0;
            }
            else
            {
                zoneCM = 3.0;

            }

            longitude = zoneCM - _a3;
            if (hemisphere.equals("S"))
            {
                latitude = -latitude;
            }

            latlon[0] = latitude;
            latlon[1] = longitude;
            return latlon;

        }

        protected void setVariables()
        {
            arc = northing / k0;
            mu = arc
                    / (a * (1 - POW(e, 2) / 4.0 - 3 * POW(e, 4) / 64.0 - 5 * POW(e, 6) / 256.0));

            ei = (1 - POW((1 - e * e), (1 / 2.0)))
                    / (1 + POW((1 - e * e), (1 / 2.0)));

            ca = 3 * ei / 2 - 27 * POW(ei, 3) / 32.0;

            cb = 21 * POW(ei, 2) / 16 - 55 * POW(ei, 4) / 32;
            cc = 151 * POW(ei, 3) / 96;
            cd = 1097 * POW(ei, 4) / 512;
            phi1 = mu + ca * SIN(2 * mu) + cb * SIN(4 * mu) + cc * SIN(6 * mu) + cd
                    * SIN(8 * mu);

            n0 = a / POW((1 - POW((e * SIN(phi1)), 2)), (1 / 2.0));

            r0 = a * (1 - e * e) / POW((1 - POW((e * SIN(phi1)), 2)), (3 / 2.0));
            fact1 = n0 * TAN(phi1) / r0;

            _a1 = 500000 - easting;
            dd0 = _a1 / (n0 * k0);
            fact2 = dd0 * dd0 / 2;

            t0 = POW(TAN(phi1), 2);
            Q0 = e1sq * POW(COS(phi1), 2);
            fact3 = (5 + 3 * t0 + 10 * Q0 - 4 * Q0 * Q0 - 9 * e1sq) * POW(dd0, 4)
                    / 24;

            fact4 = (61 + 90 * t0 + 298 * Q0 + 45 * t0 * t0 - 252 * e1sq - 3 * Q0
                    * Q0)
                    * POW(dd0, 6) / 720;

            //
            lof1 = _a1 / (n0 * k0);
            lof2 = (1 + 2 * t0 + Q0) * POW(dd0, 3) / 6.0;
            lof3 = (5 - 2 * Q0 + 28 * t0 - 3 * POW(Q0, 2) + 8 * e1sq + 24 * POW(t0, 2))
                    * POW(dd0, 5) / 120;
            _a2 = (lof1 - lof2 + lof3) / COS(phi1);
            _a3 = _a2 * 180 / Math.PI;

        }

        double arc;

        double mu;

        double ei;

        double ca;

        double cb;

        double cc;

        double cd;

        double n0;

        double r0;

        double _a1;

        double dd0;

        double t0;

        double Q0;

        double lof1;

        double lof2;

        double lof3;

        double _a2;

        double phi1;

        double fact1;

        double fact2;

        double fact3;

        double fact4;

        double zoneCM;

        double _a3;

        double b = 6356752.314;

        double a = 6378137;

        double e = 0.081819191;

        double e1sq = 0.006739497;

        double k0 = 0.9996;

    }

    private class Digraphs
    {

        private Map digraph1 = new Hashtable();

        private Map digraph2 = new Hashtable();

        private String[] digraph1Array =
        {
            "A", "B", "C", "D", "E", "F", "G", "H",
            "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z"
        };

        private String[] digraph2Array =
        {
            "V", "A", "B", "C", "D", "E", "F", "G",
            "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V"
        };

        public Digraphs()
        {
            digraph1.put(new Integer(1), "A");
            digraph1.put(new Integer(2), "B");
            digraph1.put(new Integer(3), "C");
            digraph1.put(new Integer(4), "D");
            digraph1.put(new Integer(5), "E");
            digraph1.put(new Integer(6), "F");
            digraph1.put(new Integer(7), "G");
            digraph1.put(new Integer(8), "H");
            digraph1.put(new Integer(9), "J");
            digraph1.put(new Integer(10), "K");
            digraph1.put(new Integer(11), "L");
            digraph1.put(new Integer(12), "M");
            digraph1.put(new Integer(13), "N");
            digraph1.put(new Integer(14), "P");
            digraph1.put(new Integer(15), "Q");
            digraph1.put(new Integer(16), "R");
            digraph1.put(new Integer(17), "S");
            digraph1.put(new Integer(18), "T");
            digraph1.put(new Integer(19), "U");
            digraph1.put(new Integer(20), "V");
            digraph1.put(new Integer(21), "W");
            digraph1.put(new Integer(22), "X");
            digraph1.put(new Integer(23), "Y");
            digraph1.put(new Integer(24), "Z");

            digraph2.put(new Integer(0), "V");
            digraph2.put(new Integer(1), "A");
            digraph2.put(new Integer(2), "B");
            digraph2.put(new Integer(3), "C");
            digraph2.put(new Integer(4), "D");
            digraph2.put(new Integer(5), "E");
            digraph2.put(new Integer(6), "F");
            digraph2.put(new Integer(7), "G");
            digraph2.put(new Integer(8), "H");
            digraph2.put(new Integer(9), "J");
            digraph2.put(new Integer(10), "K");
            digraph2.put(new Integer(11), "L");
            digraph2.put(new Integer(12), "M");
            digraph2.put(new Integer(13), "N");
            digraph2.put(new Integer(14), "P");
            digraph2.put(new Integer(15), "Q");
            digraph2.put(new Integer(16), "R");
            digraph2.put(new Integer(17), "S");
            digraph2.put(new Integer(18), "T");
            digraph2.put(new Integer(19), "U");
            digraph2.put(new Integer(20), "V");

        }

        public int getDigraph1Index(String letter)
        {
            for (int i = 0; i < digraph1Array.length; i++)
            {
                if (digraph1Array[i].equals(letter))
                {
                    return i + 1;
                }
            }

            return -1;
        }

        public int getDigraph2Index(String letter)
        {
            for (int i = 0; i < digraph2Array.length; i++)
            {
                if (digraph2Array[i].equals(letter))
                {
                    return i;
                }
            }

            return -1;
        }

        public String getDigraph1(int longZone, double easting)
        {
            int a1 = longZone;
            double a2 = 8 * ((a1 - 1) % 3) + 1;

            double a3 = easting;
            double a4 = a2 + ((int) (a3 / 100000)) - 1;
            return (String) digraph1.get(new Integer((int) Math.floor(a4)));
        }

        public String getDigraph2(int longZone, double northing)
        {
            int a1 = longZone;
            double a2 = 1 + 5 * ((a1 - 1) % 2);
            double a3 = northing;
            double a4 = (a2 + ((int) (a3 / 100000)));
            a4 = (a2 + ((int) (a3 / 100000.0))) % 20;
            a4 = Math.floor(a4);
            if (a4 < 0)
            {
                a4 = a4 + 19;
            }
            return (String) digraph2.get(new Integer((int) Math.floor(a4)));

        }

    }

    private class LatZones
    {

        private char[] letters =
        {
            'A', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Z'
        };

        private int[] degrees =
        {
            -90, -84, -72, -64, -56, -48, -40, -32, -24, -16,
            -8, 0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 84
        };

        private char[] negLetters =
        {
            'A', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'L', 'M'
        };

        private int[] negDegrees =
        {
            -90, -84, -72, -64, -56, -48, -40, -32, -24,
            -16, -8
        };

        private char[] posLetters =
        {
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Z'
        };

        private int[] posDegrees =
        {
            0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 84
        };

        private int arrayLength = 22;

        public LatZones()
        {
        }

        public int getLatZoneDegree(String letter)
        {
            char ltr = letter.charAt(0);
            for (int i = 0; i < arrayLength; i++)
            {
                if (letters[i] == ltr)
                {
                    return degrees[i];
                }
            }
            return -100;
        }

        public String getLatZone(double latitude)
        {
            int latIndex = -2;
            int lat = (int) latitude;

            if (lat >= 0)
            {
                int len = posLetters.length;
                for (int i = 0; i < len; i++)
                {
                    if (lat == posDegrees[i])
                    {
                        latIndex = i;
                        break;
                    }

                    if (lat > posDegrees[i])
                    {
                        continue;
                    }
                    else
                    {
                        latIndex = i - 1;
                        break;
                    }
                }
            }
            else
            {
                int len = negLetters.length;
                for (int i = 0; i < len; i++)
                {
                    if (lat == negDegrees[i])
                    {
                        latIndex = i;
                        break;
                    }

                    if (lat < negDegrees[i])
                    {
                        latIndex = i - 1;
                        break;
                    }
                    else
                    {
                        continue;
                    }

                }

            }

            if (latIndex == -1)
            {
                latIndex = 0;
            }
            if (lat >= 0)
            {
                if (latIndex == -2)
                {
                    latIndex = posLetters.length - 1;
                }
                return String.valueOf(posLetters[latIndex]);
            }
            else
            {
                if (latIndex == -2)
                {
                    latIndex = negLetters.length - 1;
                }
                return String.valueOf(negLetters[latIndex]);

            }
        }

    }

}
