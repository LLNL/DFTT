package llnl.gnem.core.util.Geometry;


public class ECEFCoordinate {
    private final double xMeters;
    private final double yMeters;
    private final double zMeters;

    public ECEFCoordinate(double x, double y, double z) {
        this.xMeters = x;
        this.yMeters = y;
        this.zMeters = z;
    }

    /**
     * @return the x
     */
    public double getX() {
        return xMeters;
    }

    /**
     * @return the y
     */
    public double getY() {
        return yMeters;
    }

    /**
     * @return the z
     */
    public double getZ() {
        return zMeters;
    }

    double getSeparationMeters(ECEFCoordinate other) {
       double dx = xMeters-other.xMeters;
       double dy = yMeters-other.yMeters;
       double dz = zMeters-other.zMeters;
       return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }
    
}
