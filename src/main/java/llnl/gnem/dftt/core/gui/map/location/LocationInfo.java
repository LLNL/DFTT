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
package llnl.gnem.dftt.core.gui.map.location;

import llnl.gnem.dftt.core.geom.GeographicCoordinate;
import llnl.gnem.dftt.core.geom.Location;
import llnl.gnem.dftt.core.gui.map.ViewPort;
import llnl.gnem.dftt.core.gui.map.internal.Measurable;
import llnl.gnem.dftt.core.util.Geometry.EModel;

public abstract class LocationInfo<T extends Location<GeographicCoordinate>> implements Comparable<Object>, Measurable {
    public static final int CRUDE_DEG_TO_KM = 56;
    private T location;

    protected LocationInfo(T location) {
        this.location = location;
    }
    
    public void setLocation(T location) {
        this.location = location;
    }

    public T getLocation() {
        return location;
    }

    public String getName() {
        return location.getName();
    }

    public double getLat() {
        return getCoordinate().getLat();
    }

    public double getLon() {
        return getCoordinate().getLon();
    }

    public double distance(LocationInfo other) {
        return getCoordinate().getDistance(other.getCoordinate());
    }

    public double delta(LocationInfo other) {
        return getCoordinate().getDelta(other.getCoordinate());
    }

    public double quickDistance(LocationInfo other) {
        return getCoordinate().quickDistance(other.getCoordinate());
    }
    
    public GeographicCoordinate getCoordinate() {
        return location.getCoordinate();
    }
//
//    public Position getPosition() {
//        return new Position(Angle.fromDegrees(getLat()), Angle.fromDegrees(getLon()), 0);
//    }

    @Override
    public int compareTo(Object obj) {
        LocationInfo other = (LocationInfo) obj;
        return location.compareTo(other.location);
    }

    public abstract String getMapAnnotation();

    @Override
    public double distanceFrom(Measurable other) {
        return quickDistance((LocationInfo) other);
    }

    @Override
    public boolean isInside(ViewPort viewport) {
        final double distanceToViewCenter = EModel.getDelta(
                getCoordinate().getLat(), getCoordinate().getLon(), viewport.getLat(), viewport.getLon());

        return distanceToViewCenter < (viewport.getRadiusDegrees() / 2);
    }

    @Override
    public boolean intersects(ViewPort viewport) {
        return false;
    }
}
