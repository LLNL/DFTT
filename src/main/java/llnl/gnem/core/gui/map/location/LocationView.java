package llnl.gnem.core.gui.map.location;

import java.util.Collection;

/**
 *
 * @author addair1
 */
public interface LocationView<T extends LocationInfo> {
    void clearLocations();

    void addLocations(Collection<T> locations);

    void updateCurrentLocation(T currentLocation);

    void updateLocations(Collection<T> locations);

    void retrievalIsCompleted();

    void locationWasRemoved(T location);
}
