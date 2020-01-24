package llnl.gnem.core.traveltime.Ak135;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import llnl.gnem.core.traveltime.Point3D;
import llnl.gnem.core.traveltime.SinglePhaseTraveltimeCalculator;
import llnl.gnem.core.util.Geometry.EModel;

public class ConstantVelocityTraveltimeCalculator implements SinglePhaseTraveltimeCalculator, Serializable{

    static boolean supportsPhase(String phase) {
        Pattern pattern = Pattern.compile("([^a-zA-Z])+");
        Matcher matcher = pattern.matcher(phase);
        return matcher.find();
    }
    
    private final String phase;
    private final double velocity;
    public ConstantVelocityTraveltimeCalculator( String phase )
    {
        this.phase = phase;
        Pattern pattern = Pattern.compile("([^a-zA-Z])+");
        Matcher matcher = pattern.matcher(phase);
        if( matcher.find()){
            String number = matcher.group();
            velocity = Double.parseDouble(number);
        }
        else {
            throw new IllegalArgumentException( "Input phase string does not contain a number!");
        }
    }

    @Override
    public double getTT(Point3D evLoc, Point3D stLoc) {
        double delta = evLoc.dist_geog(stLoc);
        double dist = EModel.getKilometersPerDegree() * delta;
        return dist / velocity;
    }

    @Override
    public double getTT1D(double delta, double depth) {
        double dist = EModel.getKilometersPerDegree() * delta;
        return dist /velocity;
    }

    @Override
    public String getPhase() {
        return phase;
    }

    @Override
    public double get1DHslowness(double delta, double depth) {
        return 1/velocity;
    }
    
}
