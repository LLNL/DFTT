/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.signalprocessing.arrayProcessing;

/**
 *
 * @author dodge1
 */
public class SlownessValue {
    private final double sNorth;  // x-coordinate
    private final double sEast;  // y-coordinate

    public SlownessValue(double sNorth, double sEast) {
        this.sNorth = sNorth;
        this.sEast = sEast;
    }

    public double getsNorth() {
        return sNorth;
    }

    public double getsEast() {
        return sEast;
    }

    public double getsX() {
        return sNorth;
    }

    public double getsY() {
        return sEast;
    }
    
    
    public double getVelocity() {
        return 1.0 / Math.sqrt(sNorth * sNorth + sEast * sEast);
    }
    
    public double getAzimuth()
    {
        double theta = Math.atan2(sEast, sNorth);
        double tmp = Math.toDegrees(theta);
        if (tmp < 0) {
            tmp += 360;
        }
        return tmp;
    }


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.sNorth) ^ (Double.doubleToLongBits(this.sNorth) >>> 32));
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.sEast) ^ (Double.doubleToLongBits(this.sEast) >>> 32));
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
        final SlownessValue other = (SlownessValue) obj;
        if (Double.doubleToLongBits(this.sNorth) != Double.doubleToLongBits(other.sNorth)) {
            return false;
        }
        if (Double.doubleToLongBits(this.sEast) != Double.doubleToLongBits(other.sEast)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SlownessValue{" + "sNorth(x)=" + sNorth + ", sEast(y)=" + sEast + "velocity = " + this.getVelocity()+ " azimuth = " + this.getAzimuth() + '}';
    }
    
}
