package llnl.gnem.core.seismicData;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import llnl.gnem.core.geom.CartesianCoordinate;
import llnl.gnem.core.geom.Coordinate;
import llnl.gnem.core.geom.GeographicCoordinate;
import llnl.gnem.core.geom.Location;

/**
 *
 * @author addair1
 * @param <T>
 */
public class Station<T extends Coordinate> extends Location<T> {
    public Station(T coord) {
        this(coord, "-");
    }
    
    @JsonCreator
    public Station(@JsonProperty("coordinate") T coord, @JsonProperty("name") String name) {
        super(coord, name);
    }

    @JsonIgnore
    public String getSta() {
        return getName();
    }

    @JsonIgnore
    public double getElev() {
        return getCoordinate().getElevation();
    }
    
    public static Station<GeographicCoordinate> fromGeo(double lat, double lon) {
        return fromGeo(lat, lon, 0.0);
    }
    
    public static Station<GeographicCoordinate> fromGeo(double lat, double lon, double elev) {
        return fromGeo(lat, lon, elev, "-");
    }
    
    public static Station<GeographicCoordinate> fromGeo(double lat, double lon, Double elev, String name) {
        return new Station<>(new GeographicCoordinate(lat, lon, elev), name);
    }
    
    public static Station<CartesianCoordinate> fromCartesian(double x, double y) {
        return fromCartesian(x, y, 0.0);
    }
    
    public static Station<CartesianCoordinate> fromCartesian(double x, double y, double elev) {
        return fromCartesian(x, y, elev, "-");
    }
    
    public static Station<CartesianCoordinate> fromCartesian(double x, double y, double elev, String name) {
        return new Station<>(new CartesianCoordinate(x, y, elev), name);
    }
}
