package llnl.gnem.core.traveltime;

public interface SinglePhaseTraveltimeCalculator {

    double getTT(Point3D evLoc, Point3D stLoc);

    double getTT1D(double delta, double depth);

    double get1DHslowness(double delta, double depth);

    String getPhase();
}
