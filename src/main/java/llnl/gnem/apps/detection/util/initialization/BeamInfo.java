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
package llnl.gnem.apps.detection.util.initialization;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;



/**
 * Created by dodge1
 * Date: Feb 12, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class BeamInfo {
    public static final int DEGREES_PER_RADIAN = 180;

    private double northSlowness;
    private double eastSlowness;
    private double downSlowness;

    public BeamInfo(double northSlowness,
            double eastSlowness,
            double downSlowness) {
        this.northSlowness = northSlowness;
        this.eastSlowness = eastSlowness;
        this.downSlowness = downSlowness;
    }

    public BeamInfo(BeamParams bp) {
        double baz = bp.getBaz();
        double velocity = bp.getVelocity();
        double theta = baz * Math.PI / DEGREES_PER_RADIAN;
        eastSlowness = (1.0 / velocity) * Math.sin(theta);
        northSlowness = (1.0 / velocity) * Math.cos(theta);
        downSlowness = 0.0;
    }

    public double getNorthSlowness() {
        return northSlowness;
    }

    public double getEastSlowness() {
        return eastSlowness;
    }

    public double getDownSlowness() {
        return downSlowness;
    }

    public void setNorthSlowness(double northSlowness) {
        this.northSlowness = northSlowness;
    }

    public void setEastSlowness(double eastSlowness) {
        this.eastSlowness = eastSlowness;
    }

    public void setDownSlowness(double downSlowness) {
        this.downSlowness = downSlowness;
    }

    public Vector3D getSlownessVector() {
        return new Vector3D(northSlowness, eastSlowness, downSlowness);
    }
}