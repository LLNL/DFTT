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
