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
package llnl.gnem.core.traveltime.Ak135;

import llnl.gnem.core.traveltime.Point3D;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 *
 * @author myers30
 */
public class EllipticityCorrection implements Serializable {

    private final float[] dists;
    private final float[] depths;
    private final float[][] t0;
    private final float[][] t1;
    private final float[][] t2;
    private static final double s3 = Math.sqrt(3.0) / 2.0;
    private transient Point3D evLoc;
    private transient double ecolat;  // event geocentric colatitude
    private transient float sc0;
    private transient float sc1;
    private transient float sc2;
    static final long serialVersionUID = -1690529028875797150L;

    public EllipticityCorrection(String phase) throws IOException {
        this(new EllipticityCorrectionLoader(phase));
    }

    public EllipticityCorrection(String path, String file) throws IOException {
        this(new EllipticityCorrectionLoader(path, file));
    }
    
    public EllipticityCorrection(InputStream stream) throws IOException {
        this(new EllipticityCorrectionLoader(stream));
    }
    
    public EllipticityCorrection(EllipticityCorrectionLoader loader) {
        dists = loader.getDists();
        depths = loader.getDepths();
        t0 = loader.getT0();
        t1 = loader.getT1();
        t2 = loader.getT2();
    }

    public EllipticityCorrection(EllipticityCorrection old) {
        dists = old.dists.clone();
        depths = old.depths.clone();
        t0 = old.t0.clone();
        t1 = old.t1.clone();
        t2 = old.t2.clone();
    }

    private int neighborDepthIdx(float depth) {
        int i = 0;
        while (depth > this.depths[i + 1]) {
            i++;
            if (i == this.depths.length - 1) {
                break;
            }
        }
        return (i);
    }

    private int neighborDistIdx(float dist) {
        int i = 0;
        while (dist > this.dists[i + 1]) {
            i++;
            if (i == this.dists.length - 1) {
                break;
            }
        }
        return (i);
    }

    /**
     * compute the ellipicity correction
     *
     * @param evLoc
     * @param stLoc
     * @return ellipticityCorrection (seconds)
     */
    public float computeEllipCorrection(Point3D evLoc, Point3D stLoc) {
        setEvLoc(evLoc);
        float ellipCorrection = computeEllipCorrection(stLoc);
        return (ellipCorrection);
    }

    /**
     * compute the ellipticity correction to a station (assumes evLoc is already
     * set);
     *
     * @param stLoc
     * @return
     */
    public float computeEllipCorrection(Point3D stLoc) {
        float dist = evLoc.dist_geog(stLoc);
        float az = evLoc.baz_geog(stLoc);
        float ellipCorrection = computeEllipCorrection(dist, az);
        return (ellipCorrection);
    }

    public float computeEllipCorrection(float dist, float az) {
        int depthIdx0 = this.neighborDepthIdx(-evLoc.getzElev());
        int distIdx0 = this.neighborDistIdx(dist);
        int depthIdx1 = depthIdx0 + 1;
        int distIdx1 = distIdx0 + 1;
        float[] weights = new float[4];
        float[] scaledDiffs = new float[2];
        if (distIdx0 == this.dists.length - 1) {
            scaledDiffs[0] = 1;
            distIdx1--;

        }
        scaledDiffs[0] = 1 - (dist - this.dists[distIdx0])
                / (this.dists[distIdx1] - this.dists[distIdx0]);
        if (depthIdx0 == this.depths.length - 1) {
            scaledDiffs[1] = 1;
            depthIdx1--;
        }
        scaledDiffs[1] = 1 - (-evLoc.getzElev() - this.depths[depthIdx0])
                / (this.depths[depthIdx1] - this.depths[depthIdx0]);

        // determine interpolation weights for table values
        weights[0] = scaledDiffs[0] * scaledDiffs[1];
        weights[1] = scaledDiffs[0] * (1 - scaledDiffs[1]);
        weights[2] = (1 - scaledDiffs[0]) * (1 - scaledDiffs[1]);
        weights[3] = (1 - scaledDiffs[0]) * scaledDiffs[1];


        // weights are for dist/depths points clockwise from point of
        // least distance and depth
         /*
         * tauValues[0] = this.t0[depthIdx0][distIdx0] * weights[0];
         * tauValues[1] = this.t0[depthIdx1][distIdx0] * weights[1];
         * tauValues[2] = this.t0[depthIdx1][distIdx1] * weights[2];
         * tauValues[3] = this.t0[depthIdx0][distIdx1] * weights[3];
         */


        // interpolate tau values
        float t0_temp = this.t0[depthIdx0][distIdx0] * weights[0]
                + this.t0[depthIdx1][distIdx0] * weights[1]
                + this.t0[depthIdx1][distIdx1] * weights[2]
                + this.t0[depthIdx0][distIdx1] * weights[3];

        float t1_temp = this.t1[depthIdx0][distIdx0] * weights[0]
                + this.t1[depthIdx1][distIdx0] * weights[1]
                + this.t1[depthIdx1][distIdx1] * weights[2]
                + this.t1[depthIdx0][distIdx1] * weights[3];

        float t2_temp = this.t2[depthIdx0][distIdx0] * weights[0]
                + this.t2[depthIdx1][distIdx0] * weights[1]
                + this.t2[depthIdx1][distIdx1] * weights[2]
                + this.t2[depthIdx0][distIdx1] * weights[3];




        float ellipCorrection = sc0 * t0_temp
                + sc1 * (float) Math.cos(Math.toRadians((double) az)) * t1_temp
                + sc2 * (float) Math.cos(2.0 * Math.toRadians((double) az)) * t2_temp;
        return (ellipCorrection);
    }

    public void setEvLoc(Point3D evLoc) {
        this.evLoc = evLoc;
        this.ecolat = Math.toRadians(90.0 - evLoc.getyLat());
        computePreliminaries();
    }

    private void computePreliminaries() {
        sc0 = (float) (0.25 * (1.0 + 3.0 * Math.cos(2.0 * ecolat)));
        sc1 = (float) (s3 * Math.sin(2.0 * ecolat));
        sc2 = (float) (s3 * Math.sin(ecolat) * Math.sin(ecolat));
    }
}
