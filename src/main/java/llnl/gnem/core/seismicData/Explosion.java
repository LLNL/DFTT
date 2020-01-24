package llnl.gnem.core.seismicData;

import llnl.gnem.core.geom.CartesianCoordinate;
import llnl.gnem.core.geom.Coordinate;
import llnl.gnem.core.geom.GeographicCoordinate;
import net.jcip.annotations.Immutable;

/**
 *
 * @author addair1
 */
@Immutable
public class Explosion<T extends Coordinate> extends Event<T> {
    public Explosion(T coord, double yield) {
        super(coord, Energy.fromYield(yield));
    }
    
    public double getHOB() {
        return getCoordinate().getElevation();
    }
    
    public double getYield() {
        return getEnergy().getYield();
    }
    
    public static Explosion<GeographicCoordinate> fromGeo(double lat, double lon, double hob, double yield) {
        return new Explosion<GeographicCoordinate>(new GeographicCoordinate(lat, lon, hob), yield);
    }
    
    public static Explosion<CartesianCoordinate> fromCartesian(double lat, double lon, double hob, double yield) {
        return new Explosion<CartesianCoordinate>(new CartesianCoordinate(lat, lon, hob), yield);
    }
}
