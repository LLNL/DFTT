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

public class Polygon extends llnl.gnem.core.polygon.BasePolygon {

    private static final long serialVersionUID = -8925155950211896078L;
    private int polyid;
    final String polyName;
    double minlat, maxlat, minlon, maxlon;

    public Polygon(int id, String polyName, Vertex[] verts) {
        super(verts);
        this.polyid = id;
        this.polyName = polyName;
        minlat = maxlat = verts[0].getLat();
        minlon = maxlon = verts[0].getLon();
        for (Vertex vert : verts) {
            if (vert.getLat() > maxlat) {
                maxlat = vert.getLat();
            } else if (vert.getLat() < minlat) {
                minlat = vert.getLat();
            }
            if (vert.getLon() > maxlon) {
                maxlon = vert.getLon();
            } else if (vert.getLon() < minlon) {
                minlon = vert.getLon();
            }
        }
    }

    public Polygon(int id, String polyName, Vertex[] verts, double minLat, double maxLat, double minLon, double maxLon) {
        super(verts);
        this.polyid = id;
        this.polyName = polyName;
        this.minlat = minLat;
        this.maxlat = maxLat;
        this.minlon = minLon;
        this.maxlon = maxLon;
    }

    public int getPolyId() {
        return polyid;
    }

    public String getName() {
        return polyName;
    }

    @Override
    public double getMinLat() {
        return minlat;
    }

    @Override
    public double getMaxLat() {
        return maxlat;
    }

    @Override
    public double getMinLon() {
        return minlon;
    }

    @Override
    public double getMaxLon() {
        return maxlon;
    }

    @Override
    public boolean contains(double x, double y) {
        if (x < minlat || x > maxlat || y < minlon || y > maxlon) {
            return false;
        }
        return super.contains(x, y);
    }

    @Override
    public boolean contains(Vertex v) {
        if (v.getLat() < minlat || v.getLat() > maxlat || v.getLon() < minlon || v.getLon() > maxlon) {
            return false;
        }
        return super.contains(v);
    }

    /**
     * @param polyid the polyid to set
     */
    public void setPolyid(int polyid) {
        this.polyid = polyid;
    }
}
