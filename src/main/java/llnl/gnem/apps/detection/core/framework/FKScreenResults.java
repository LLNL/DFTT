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
