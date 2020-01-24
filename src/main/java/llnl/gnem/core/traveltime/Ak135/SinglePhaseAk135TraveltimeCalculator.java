/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.traveltime.Ak135;

import llnl.gnem.core.traveltime.Point3D;
import llnl.gnem.core.traveltime.SinglePhaseTraveltimeCalculator;

/**
 *
 * @author myers30
 */
public class SinglePhaseAk135TraveltimeCalculator implements SinglePhaseTraveltimeCalculator {

    private final EllipticityCorrection ellipticityCorrection;
    private final TTDistDepth distDepth;
    private final String phase;

    public SinglePhaseAk135TraveltimeCalculator(String phase,
            TTDistDepth distDepth,
            EllipticityCorrection ellipticityCorrection) {
        this.phase = phase;
        this.distDepth = distDepth;
        this.ellipticityCorrection = ellipticityCorrection;
    }

    @Override
    public double getTT(Point3D evLoc, Point3D stLoc) {
        double delta = evLoc.dist_geog(stLoc);
        double ellip = 0.0f;
        double tt = distDepth.getTime((float) delta, -evLoc.getzElev());
        if (tt > 0 && ellipticityCorrection != null) {
            ellip = ellipticityCorrection.computeEllipCorrection(evLoc, stLoc);
        }
        return (tt + ellip);
    }

    @Override
    public double getTT1D(double delta, double depth) {
        return distDepth.getTime(delta, depth);
    }

    @Override
    public String getPhase() {
        return phase;
    }

    @Override
    public double get1DHslowness(double delta, double depth) {
        double ttime = getTT1D(delta, depth);
        double dx = 0.001; // km
        double dDeg = dx / 111.19;
        double ttime2 = getTT1D(delta + dDeg, depth);
        double dt = ttime2 - ttime;
        return dt / dx;
    }
}
