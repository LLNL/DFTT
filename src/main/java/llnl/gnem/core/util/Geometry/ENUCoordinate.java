package llnl.gnem.core.util.Geometry;

public class ENUCoordinate {

    private final double xEastMeters;
    private final double yNorthMeters;
    private final double zUpMeters;

    public ENUCoordinate(double xEastMeters, double yNorthMeters, double zUpMeters) {
        this.xEastMeters = xEastMeters;
        this.yNorthMeters = yNorthMeters;
        this.zUpMeters = zUpMeters;
    }

    @Override
    public String toString() {
        return "ENUCoordinate{" + "xEastMeters=" + xEastMeters + ", yNorthMeters=" + yNorthMeters + ", zUpMeters=" + zUpMeters + '}';
    }

    /**
     * @return the xEastMeters
     */
    public double getxEastMeters() {
        return xEastMeters;
    }

    /**
     * @return the yNorthMeters
     */
    public double getyNorthMeters() {
        return yNorthMeters;
    }

    /**
     * @return the zUpMeters
     */
    public double getzUpMeters() {
        return zUpMeters;
    }

    double getSeparation(ENUCoordinate other) {
        double dx = xEastMeters - other.xEastMeters;
        double dy = yNorthMeters - other.yNorthMeters;
        double dz = zUpMeters - other.zUpMeters;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}
