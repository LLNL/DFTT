/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
