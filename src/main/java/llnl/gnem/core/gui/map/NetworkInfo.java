package llnl.gnem.core.gui.map;

import llnl.gnem.core.gui.map.location.LocationInfo;
import llnl.gnem.core.polygon.PolygonSet;
import llnl.gnem.core.polygon.PolygonSetType;
import llnl.gnem.core.seismicData.Network;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class NetworkInfo extends LocationInfo<Network> {
    private static double mid(double min, double max) {
        return (min + max) * 0.5;
    }

    public NetworkInfo(String code, int id,
            double minLat, double maxLat, double minLon, double maxLon) {
        super(new Network(mid(minLat, maxLat), mid(minLon, maxLon), code,
                new PolygonSet(PolygonSetType.network, code, id, minLat, maxLat, minLon, maxLon)));
    }

    public NetworkInfo(PolygonSet polySet) {
        super(new Network(
                mid(polySet.getMinLat(), polySet.getMaxLat()),
                mid(polySet.getMinLon(), polySet.getMaxLon()),
                polySet.getName(), polySet));
    }

    public PolygonSet getPolySet() {
        return getLocation().getPolygonSet();
    }

    @Override
    public String getMapAnnotation() {
        return String.format("Network=%s MinLat,MinLon=[%5.3f,%5.3f] MaxLat,MaxLon=[%5.3f,%5.3f]",
                this.getName(),
                getPolySet().getMinLat(), getPolySet().getMinLon(),
                getPolySet().getMaxLat(), getPolySet().getMaxLon());
    }
}
