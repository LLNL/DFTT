package llnl.gnem.core.traveltime.Ak135;

import java.io.Serializable;
import llnl.gnem.core.traveltime.Point3D;
import llnl.gnem.core.traveltime.SinglePhaseTraveltimeCalculator;
import llnl.gnem.core.util.Geometry.EModel;

public class AirTraveltimeCalculator implements SinglePhaseTraveltimeCalculator, Serializable{

    private static final long serialVersionUID = 672075859936562861L;
    
    
    private final double speedMpS;
    public AirTraveltimeCalculator()
    {
        double temp = 0;
        speedMpS = calculateSpeed(temp);
    }
    
    public AirTraveltimeCalculator( double tempCelcius )
    {
      
        speedMpS = calculateSpeed(tempCelcius);
        System.out.println(speedMpS);
    }

    private double calculateSpeed(double tempCelcius) {
        return 331.3 + 0.606 * tempCelcius; // From http://en.wikipedia.org/wiki/Speed_of_sound
    }
    
    public double getTraveltimeDistMeters(double distMeters)
    {
        if( distMeters < 0 ){
            throw new IllegalArgumentException( "Distance cannot be negative!");
            
        }
        return distMeters / speedMpS;
    }
    
    public double getTraveltimeDistKm( double dist )
    {
        return getTraveltimeDistMeters( dist * 1000);
    }
    
    public double getTraveltimeDistDeg( double delta )
    {
        double dist = EModel.getKilometersPerDegree() * delta;
        return getTraveltimeDistKm(dist);
    }

    @Override
    public double getTT(Point3D evLoc, Point3D stLoc) {
        double delta = evLoc.dist_geog(stLoc);
        return getTraveltimeDistDeg(delta);
    }

    @Override
    public double getTT1D(double delta, double depth) {
        return getTraveltimeDistDeg(delta);
    }

    @Override
    public String getPhase() {
        return "A";
    }

    @Override
    public double get1DHslowness(double delta, double depth) {
        return 1000 / speedMpS; // slowness must be in s / km
    }
    
}
