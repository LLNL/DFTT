package llnl.gnem.core.gui.map.location;

import llnl.gnem.core.geom.GeographicCoordinate;
import llnl.gnem.core.geom.Location;
import llnl.gnem.core.gui.map.ViewPort;
import llnl.gnem.core.gui.map.internal.Measurable;
import llnl.gnem.core.util.Geometry.EModel;

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
