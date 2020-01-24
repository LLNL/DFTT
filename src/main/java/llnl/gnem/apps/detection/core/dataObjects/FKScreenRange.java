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
public class FKScreenRange implements Serializable {
    private final double delAzimuth;
    private final double delVelocity;
    static final long serialVersionUID = -976173760148023624L;
    
    public FKScreenRange( double delAzimuth, double delVelocity)
    {
        this.delAzimuth = delAzimuth;
        this.delVelocity = delVelocity;
    }

    /**
     * @return the delAzimuth
     */
    public double getDelAzimuth() {
        return delAzimuth;
    }

    /**
     * @return the delVelocity
     */
    public double getDelVelocity() {
        return delVelocity;
    }
}
