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
public class SlownessRangeSpecification implements Serializable {

    private final SlownessSpecification nominal;
    private final double delSlow;
    static final long serialVersionUID = -8368921322301228741L;

    @Override
    public String toString() {
        return "SlownessRangeSpecification{" + "nominal=" + nominal + ", delSlow=" + delSlow + '}';
    }

    public SlownessRangeSpecification(SlownessSpecification nominal,
            FKScreenRange screenRange) {
        double dslow1 = 0;
        double minVel = nominal.getVelocity() - screenRange.getDelVelocity();
        if (minVel > 0) {
            double maxVel = nominal.getVelocity() + screenRange.getDelVelocity();
            dslow1 = (1 / minVel - 1 / maxVel) / 2;
        }

        double tanTheta = Math.tan(Math.toRadians(screenRange.getDelAzimuth()));
        double dslow2 = tanTheta / nominal.getVelocity();
        delSlow = Math.max(dslow2, dslow1);
        this.nominal = nominal;


    }

    public SlownessRangeSpecification(SlownessSpecification nominal,
            double delSlow) {
        this.delSlow = delSlow;
        this.nominal = nominal;
    }

    /**
     * @return the nominal
     */
    public SlownessSpecification getNominal() {
        return nominal;
    }

    /**
     * @return the delSlow
     */
    public double getDelSlow() {
        return delSlow;
    }
}
