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
package llnl.gnem.core.polygon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PolygonSet implements Serializable{

    private final PolygonSetType type;
    private final String name;
    private final int id;
    private double minlat;
    private double maxlat;
    private double minlon;
    private double maxlon;
    private List<Polygon> polygons;
    private static final long serialVersionUID = -7164254139169865256L;

    public PolygonSet(PolygonSetType type, String name, int id,
            double minlat, double maxlat, double minlon, double maxlon) {
        this.type = type;
        this.name = name;
        this.id = id;
        this.minlat = minlat;
        this.maxlat = maxlat;
        this.minlon = minlon;
        this.maxlon = maxlon;
        polygons = null;
    }
    
    public PolygonSet(PolygonSetType type, String name, int id, Collection<Polygon> polygons )
    {
        this.type = type;
        this.name = name;
        this.id = id;
        this.polygons = new ArrayList<>(polygons);
        refreshSetBounds();
    }

    public void setLatBounds(double minlat, double maxlat) {
        this.minlat = minlat;
        this.maxlat = maxlat;
    }

    public void setLonBounds(double minlon, double maxlon) {
        this.minlon = minlon;
        this.maxlon = maxlon;
    }

    public final void refreshSetBounds() {
        Iterator<Polygon> polyIter = polygons.iterator();
         Double minLat = 90.0;
        Double maxLat = -90.0;
        Double minLon = 180.0;
        Double maxLon = -180.0;

        while (polyIter.hasNext()) {
            Polygon poly = polyIter.next();
            if (poly.getMinLat() < minLat) {
                minLat = poly.getMinLat();
            }
            if (poly.getMaxLat() > maxLat) {
                maxLat = poly.getMaxLat();
            }
            if (poly.getMinLon() < minLon) {
                minLon = poly.getMinLon();
            }
            if (poly.getMaxLon() > maxLon) {
                maxLon = poly.getMaxLon();
            }
        }

        // Update the PolygonSet values
        setLatBounds(minLat, maxLat);
        setLonBounds(minLon, maxLon);
    }

    public PolygonSetType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getMinLat() {
        return minlat;
    }

    public double getMaxLat() {
        return maxlat;
    }

    public double getMinLon() {
        return minlon;
    }

    public double getMaxLon() {
        return maxlon;
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean contains(double lat, double lon) {
        if (lat > maxlat || lat < minlat) {
            return false;
        }
        if (lon > maxlon || lon < minlon) {
            return false;
        }

        for (Polygon poly : getPolygons()) {
            if (poly.contains(lat, lon)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasPolygons() {
        return polygons != null;
    }

    public void removePolygon(Polygon poly) {
        polygons.remove(poly);
    }

    public void setPolygons(List<Polygon> polygons) {
        this.polygons = polygons;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }
}
