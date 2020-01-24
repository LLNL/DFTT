/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework;

/**
 *
 * @author dodge1
 */
public class FKScreenResults {

    private final boolean passed;
    private final double sx;
    private final double sy;
    private final double quality;
    private final double velocity;
    private final double azimuth;

    public FKScreenResults(boolean passed,
            double sx,
            double sy,
            double quality) {
        this.passed = passed;
        this.sx = sx;
        this.sy = sy;
        this.quality = quality;
        velocity = 1.0 / Math.sqrt(sx*sx + sy*sy);
        double theta = Math.atan2(sy,sx);
        double tmp  = Math.toDegrees(theta);
        if( tmp < 0){
            tmp += 360;
        }
        azimuth = tmp;
    }

    @Override
    public String toString() {
        return "FKScreenResults{" + "passed=" + passed + ", sx=" + sx + ", sy=" + sy + ", quality=" + quality + ", velocity=" + velocity + ", azimuth=" + azimuth + '}';
    }

    /**
     * @return the passed
     */
    public boolean isPassed() {
        return passed;
    }

    /**
     * @return the sx
     */
    public double getSx() {
        return sx;
    }

    /**
     * @return the sy
     */
    public double getSy() {
        return sy;
    }

    /**
     * @return the quality
     */
    public double getQuality() {
        return quality;
    }

    /**
     * @return the velocity
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * @return the azimuth
     */
    public double getAzimuth() {
        return azimuth;
    }
}
