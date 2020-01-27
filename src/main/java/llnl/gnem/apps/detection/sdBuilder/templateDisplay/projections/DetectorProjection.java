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
package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

/**
 *
 * @author dodge1
 */
public class DetectorProjection {
    private final int detectorid;
    private final int shift;
    private final double projection;

    public DetectorProjection(int detectorid, int shift, double projection) {
        this.detectorid = detectorid;
        this.shift = shift;
        this.projection = projection;
    }

    @Override
    public String toString() {
        return "DetectorProjection{" + "detectorid=" + detectorid + ", shift=" + shift + ", projection=" + projection + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + this.detectorid;
        hash = 13 * hash + this.shift;
        hash = 13 * hash + (int) (Double.doubleToLongBits(this.projection) ^ (Double.doubleToLongBits(this.projection) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DetectorProjection other = (DetectorProjection) obj;
        if (this.detectorid != other.detectorid) {
            return false;
        }
        if (this.shift != other.shift) {
            return false;
        }
        if (Double.doubleToLongBits(this.projection) != Double.doubleToLongBits(other.projection)) {
            return false;
        }
        return true;
    }

    /**
     * @return the detectorid
     */
    public int getDetectorid() {
        return detectorid;
    }

    /**
     * @return the shift
     */
    public int getShift() {
        return shift;
    }

    /**
     * @return the projection
     */
    public double getProjection() {
        return projection;
    }
    
}
