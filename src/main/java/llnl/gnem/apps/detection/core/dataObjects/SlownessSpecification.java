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
package llnl.gnem.apps.detection.core.dataObjects;

import java.io.Serializable;

/**
 *
 * @author dodge1
 */
public class SlownessSpecification implements Serializable{
    private final double velocity;
    private final double backAzimuth;
    private final double sx;
    private final double sy;
    static final long serialVersionUID = -2230511175693064101L;

    @Override
    public String toString() {
        return "SlownessSpecification{" + "velocity=" + velocity + ", backAzimuth=" + backAzimuth + '}';
    }
    
    public SlownessSpecification( double velocity, double backAzimuth)
    {
        this.velocity = velocity;
        this.backAzimuth = backAzimuth;
        sx = (1.0f / velocity * (Math.cos(Math.toRadians(backAzimuth))));
        sy = (1.0f / velocity * (Math.sin(Math.toRadians(backAzimuth))));
    }
    
    public float[] getSlownessVector()
    {
        float[] slownessVector = new float[2];
        slownessVector[0] = (float)sx;
        slownessVector[1] = (float)sy;
        return slownessVector;
    }

    /**
     * @return the velocity
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * @return the backAzimuth
     */
    public double getBackAzimuth() {
        return backAzimuth;
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
}
