package llnl.gnem.core.geom;

import net.jcip.annotations.Immutable;

/**
 *
 * @author addair1
 * @param <T>
 */
@Immutable
public class Location<T extends Coordinate> implements Comparable<Location> {
    private final T coord;
    private final String name;
    
    public Location(T coord) {
        this(coord, coord.toString());
    }
    
    public Location(T coord, String name) {
        this.coord = coord;
        this.name = name;
    }
    
    public T getCoordinate() {
        return coord;
    }
    
    public String getName() {
        return name;
    }
    
    public double getDistance(Location<T> other) {
        return coord.getDistance(other.coord);
    }

    @Override
    public int compareTo(Location other) {
        return name.compareTo(other.name);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        if (this.coord != other.coord && (this.coord == null || !this.coord.equals(other.coord))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.coord != null ? this.coord.hashCode() : 0);
        hash = 73 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
