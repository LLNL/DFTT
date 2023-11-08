/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.dftt.core.traveltime.Ak135;

import llnl.gnem.dftt.core.traveltime.Point3D;
import llnl.gnem.dftt.core.traveltime.SinglePhaseTraveltimeCalculator;

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
