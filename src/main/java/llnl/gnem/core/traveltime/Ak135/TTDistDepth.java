package llnl.gnem.core.traveltime.Ak135;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 *
 * @author myers30
 */
public final class TTDistDepth implements Serializable {
    private final float[] dists;
    private final float[] depths;
    private final float[][] times;
    static final long serialVersionUID = 8927755899212689014L;

    public TTDistDepth(String phase) throws IOException {
        this(new TTDistDepthLoader(phase));
    }

    public TTDistDepth(String path, String file) throws IOException {
        this(new TTDistDepthLoader(path, file));

    }

    public TTDistDepth(InputStream stream) throws IOException {
        this(new TTDistDepthLoader(stream));
    }

    public TTDistDepth(TTDistDepthLoader loader) {
        dists = loader.getDists();
        depths = loader.getDepths();
        times = loader.getTimes();
    }

    public TTDistDepth(TTDistDepth old) {
        dists = old.dists.clone();
        depths = old.depths.clone();
        times = old.times.clone();
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
     *
     * @param dist
     * @param depth
     * @return
     */
    public double getTime(double dist, double depth) {
        int depthIdx0 = this.neighborDepthIdx((float) depth);
        int distIdx0 = this.neighborDistIdx((float) dist);
        int depthIdx1 = depthIdx0 + 1;
        int distIdx1 = distIdx0 + 1;
        float[] weights = new float[4];
        float[] traveltimes = new float[4];
        float[] scaledDiffs = new float[2];
        if (distIdx0 == this.dists.length - 1) {
            scaledDiffs[0] = 1;
            distIdx1--;

        }
        scaledDiffs[0] = 1 - ((float) dist - this.dists[distIdx0])
                / (this.dists[distIdx1] - this.dists[distIdx0]);
        if (depthIdx0 == this.depths.length - 1) {
            scaledDiffs[1] = 1;
            depthIdx1--;
        }
        scaledDiffs[1] = 1 - ((float) depth - this.depths[depthIdx0])
                / (this.depths[depthIdx1] - this.depths[depthIdx0]);
        // weights are for dist/depths points clockwise from point of
        // least distance and depth
        traveltimes[0] = this.times[depthIdx0][distIdx0];
        traveltimes[1] = this.times[depthIdx1][distIdx0];
        traveltimes[2] = this.times[depthIdx1][distIdx1];
        traveltimes[3] = this.times[depthIdx0][distIdx1];
        /*
         * weights[0] = (scaledDiffs[0]+scaledDiffs[1])/4.0f; weights[1] =
         * (scaledDiffs[0]+(1-scaledDiffs[1]))/4.0f; weights[2] =
         * ((1-scaledDiffs[0])+(1-scaledDiffs[1]))/4.0f; weights[3] =
         * ((1-scaledDiffs[0])+scaledDiffs[1])/4.0f;
         */
        weights[0] = scaledDiffs[0] * scaledDiffs[1];
        weights[1] = scaledDiffs[0] * (1 - scaledDiffs[1]);
        weights[2] = (1 - scaledDiffs[0]) * (1 - scaledDiffs[1]);
        weights[3] = (1 - scaledDiffs[0]) * scaledDiffs[1];
        float travelTime = traveltimes[0] * weights[0]
                + traveltimes[1] * weights[1]
                + traveltimes[2] * weights[2]
                + traveltimes[3] * weights[3];
        return (travelTime);
    }
}
