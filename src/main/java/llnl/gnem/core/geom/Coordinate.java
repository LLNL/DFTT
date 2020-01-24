package llnl.gnem.core.geom;

/**
 *
 * @author addair1
 * @param <T>
 */
public interface Coordinate<T extends Coordinate<T>> {
    public double getDistance(T other);
    
    public double getElevation();
}
