/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.signalprocessing;

import java.io.PrintStream;

/**
 *
 * @author dodge1
 */
public class PeriodogramSample {

    private final double f;
    private final double v;

    public PeriodogramSample(SpectralSample s) {
        f = s.getFrequency();
        double tmp = s.getAbsValue();
        v = tmp * tmp;
    }

    public PeriodogramSample(double f, double v) {
        this.f = f;
        this.v = v;
    }

    public PeriodogramSample(SpectralSample sX, SpectralSample sY) {
        double fX = sX.getFrequency();
        double fY = sY.getFrequency();
        if (fX != fY) {
            throw new IllegalStateException("Samples are at different frequencies!");
        }
        f = fX;
        v = sX.conjugate().times(sY).getAbsValue();
    }

    @Override
    public String toString() {
        return String.format("F = %f, V = %f", f, v);
    }

    /**
     * @return the f
     */
    public double getFrequency() {
        return f;
    }

    /**
     * @return the v
     */
    public double getValue() {
        return v;
    }

    public void print(PrintStream out) {
        out.println(this);
    }

}
