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
package llnl.gnem.core.gui.map;

import llnl.gnem.core.util.Geometry.EModel;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */

@ThreadSafe
public class ViewPort {

    private final double lat;
    private final double lon;
    private final double radiusDegrees;
    private final double eyeElevation;
    private final double minLon, maxLon, minLat, maxLat;


    public ViewPort(double lat, double lon, double radiusDegrees, double elevation)
    {
        this.lat = lat;
        this.lon = lon;
        this.radiusDegrees = radiusDegrees;
        this.minLat = lat - radiusDegrees;
        this.maxLat = lat + radiusDegrees;
        this.minLon = lon - radiusDegrees;
        this.maxLon = lon + radiusDegrees;
        eyeElevation = elevation;
    }

    @Override
    public String toString()
    {
        return String.format("Lat: %f, Lon: %f, Radius: %f, Elevation: %f",lat,lon,radiusDegrees, eyeElevation);
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getRadiusDegrees() {
        return radiusDegrees;
    }

    public boolean enclosesPole() {
        return maxLat > 89.9 || minLat < -89.9;
    }

    public boolean intersectsPrimeMeridion()
    {
        return (minLon < 0 && maxLon > 0) ||
                (minLon < -180 && maxLon > -180) ||
                (maxLon > 180 && minLon < 180);
    }

    public double getMinLat()
    {
        return minLat;
    }

    public double getMaxLat()
    {
        return maxLat;
    }

    public double getMinLon()
    {
        return minLon;
    }

    public double getMaxLon()
    {
        return maxLon;
    }

    public double getEyeElevationKm()
    {
        return eyeElevation / 1000;
    }

    public double getHorizontalShiftPercent(ViewPort zoomStartViewPort) {
        double shiftDegrees = EModel.getDelta(lat, lon, zoomStartViewPort.lat, zoomStartViewPort.lon);
        return shiftDegrees / radiusDegrees * 100;
    }

}
