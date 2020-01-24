/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.traveltime.Ak135;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import llnl.gnem.core.traveltime.Point3D;

/**
 *
 * @author myers30
 */
public class Ak135TraveltimeCalculator {

    HashMap<String, TTDistDepth> phaseTTs = new HashMap<>();
    HashMap<String, EllipticityCorrection> phaseEllips = new HashMap<>();

    public Ak135TraveltimeCalculator() {
    }

    public Ak135TraveltimeCalculator(String phase) throws IOException {
        TTDistDepth tt = new TTDistDepth(phase);
        EllipticityCorrection ellip = new EllipticityCorrection(phase);
        phaseTTs.put(phase, tt);
        phaseEllips.put(phase, ellip);
    }

    public Ak135TraveltimeCalculator(String[] phases) throws IOException {
        for (int i = 0; i < phases.length; i++) {
            String phase = phases[i];
            TTDistDepth tt = new TTDistDepth(phase);
            EllipticityCorrection ellip = new EllipticityCorrection(phase);
            phaseTTs.put(phase, tt);
            phaseEllips.put(phase, ellip);
        }
    }

    public Ak135TraveltimeCalculator(ArrayList<?> phases) throws IOException {
        for (int i = 0; i < phases.size(); i++) {
            String phase = (String) phases.get(i);
            TTDistDepth tt = new TTDistDepth(phase);
            EllipticityCorrection ellip = new EllipticityCorrection(phase);
            phaseTTs.put(phase, tt);
            phaseEllips.put(phase, ellip);
        }
    }

    public boolean containsPhase(String phase) {
        return (this.phaseTTs.containsKey(phase));
    }

    public void addPhase(String phase) throws IOException {
        if (!(this.containsPhase(phase))) {
            TTDistDepth tt = new TTDistDepth(phase);
            EllipticityCorrection ellip = new EllipticityCorrection(phase);
            phaseTTs.put(phase, tt);
            phaseEllips.put(phase, ellip);
        }
    }

    public float getTT(String phase, Point3D evLoc, Point3D stLoc) {
        float dist = evLoc.dist_geog(stLoc);
        float ellip = 0.0f;
        float tt = this.getTT1D(phase, dist, -evLoc.getzElev());
        if (tt > 0) {
            ellip = this.getEllipCorr(phase, evLoc, stLoc);
        }
        return (tt + ellip);
    }

    public float getTT1D(String phase, float dist, float depth) {
        TTDistDepth ttModel = this.phaseTTs.get(phase);
        float tt = (float)ttModel.getTime(dist, depth);
        return (tt);
    }

    public float getTT1D(String phase, Point3D evLoc, Point3D stLoc) {
        float dist = evLoc.dist_geog(stLoc);
        float tt = this.getTT1D(phase, dist, -evLoc.getzElev());
        return (tt);
    }

    public float getEllipCorr(String phase, Point3D evLoc, Point3D stLoc) {
        EllipticityCorrection ellip = this.phaseEllips.get(phase);
        float ellipCorr = ellip.computeEllipCorrection(evLoc, stLoc);
        return (ellipCorr);
    }
}
