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
package llnl.gnem.core.util.Geometry;

import java.io.Serializable;
import java.util.Collection;


public class GeodeticCoordinate implements Serializable{

    private final double lat;
    private final double lon;
    private final double depthKm;
    private final double elevationKm;
    private static final long serialVersionUID = 4746887058932889511L;

    public static GeodeticCoordinate getCentroid(Collection<GeodeticCoordinate> values) {
        if (values.isEmpty()) {
            return null;
        }
        double latc = 0;
        double lonc = 0;
        double depthc = 0;
        for (GeodeticCoordinate gc : values) {
            latc += gc.lat;
            lonc += gc.lon;
            depthc = gc.depthKm;
        }
        latc /= values.size();
        lonc /= values.size();
        depthc /= values.size();
        return new GeodeticCoordinate(latc, lonc, depthc);
    }

    public GeodeticCoordinate() {
        lat=0;
        lon=0;
        depthKm=0;
        elevationKm=0;
    }
/**
 * Constructs a GeodeticCoordinate from lat, lon, depth.
 * @param lat
 * @param lon
 * @param depth Depth is in km with positive down. Depth = 0 is on the ellipsoid.
 */
    public GeodeticCoordinate(double lat, double lon)
    {
        this.lat = lat;
        this.lon = lon;
        this.depthKm= 0;
        this.elevationKm=0;
    }
    public GeodeticCoordinate(double lat, double lon, double depth) {
        this.lat=lat;
        this.lon=lon;
        this.depthKm=depth;
        elevationKm=0;
    }
    
    public GeodeticCoordinate(double lat, double lon, double depth, double elevKm) {
        this.lat=lat;
        this.lon=lon;
        this.depthKm=depth;
        elevationKm=elevKm;
    }

    public GeodeticCoordinate(GeodeticCoordinate old) {
        lat = old.getLat();
        lon = old.getLon();
        depthKm = old.getDepthKm();
        this.elevationKm = old.elevationKm;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getDepthKm() {
        return depthKm;
    }


    public double getElevationKm() {
        return elevationKm;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.depthKm) ^ (Double.doubleToLongBits(this.depthKm) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.elevationKm) ^ (Double.doubleToLongBits(this.elevationKm) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeodeticCoordinate other = (GeodeticCoordinate) obj;
        if (Double.doubleToLongBits(this.lat) != Double.doubleToLongBits(other.lat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lon) != Double.doubleToLongBits(other.lon)) {
            return false;
        }
        if (Double.doubleToLongBits(this.depthKm) != Double.doubleToLongBits(other.depthKm)) {
            return false;
        }
        if (Double.doubleToLongBits(this.elevationKm) != Double.doubleToLongBits(other.elevationKm)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GeodeticCoordinate{" + "lat=" + lat + ", lon=" + lon + ", depthKm=" + depthKm + ", elevationKm=" + elevationKm + '}';
    }
    
    
}
