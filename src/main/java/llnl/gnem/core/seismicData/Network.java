package llnl.gnem.core.seismicData;

import llnl.gnem.core.geom.GeographicCoordinate;
import llnl.gnem.core.geom.Location;
import llnl.gnem.core.polygon.PolygonSet;

/**
 *
 * @author addair1
 */
public class Network extends Location<GeographicCoordinate> {
    private final PolygonSet polySet;

    public Network(double lat, double lon, PolygonSet polySet) {
        this(lat, lon, "-", polySet);
    }

    public Network(double lat, double lon, String name, PolygonSet polySet) {
        super(new GeographicCoordinate(lat, lon), name);
        this.polySet = polySet;
    }

    public PolygonSet getPolygonSet() {
        return polySet;
    }
}
