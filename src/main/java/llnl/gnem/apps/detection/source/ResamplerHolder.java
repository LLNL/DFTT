/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.source;

import com.oregondsp.signalProcessing.resampling.Resampler;

/**
 *
 * @author dodge1
 */
public class ResamplerHolder {

    private final Resampler resampler;
    private final double seriesStartTime;
    private final double delta;
    private long pointsProcessed;

    public ResamplerHolder(Resampler resampler, double seriesStartTime, double delta) {
        this.resampler = resampler;
        this.seriesStartTime = seriesStartTime;
        this.delta = delta;
        pointsProcessed = 0;
    }

    public double getCurrentBufferStartTime() {
        return seriesStartTime + (pointsProcessed) * delta;
    }

    public void incrementPointsProcessed(int npts) {
        pointsProcessed += npts;
    }

    public Resampler getResampler() {
        return resampler;
    }
}
