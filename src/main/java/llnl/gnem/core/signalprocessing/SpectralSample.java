/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.signalprocessing;

import java.io.PrintStream;
import org.apache.commons.math3.complex.Complex;

/**
 *
 * @author dodge1
 */
public class SpectralSample {
    private final double frequency;
    private final Complex value;

    public SpectralSample(double frequency, Complex value) {
        this.frequency = frequency;
        this.value = value;
    }
    
    @Override
    public String toString()
    {
        return String.format("F=%f, Value = %s", frequency, value.toString());
    }

    public double getFrequency() {
        return frequency;
    }

    public Complex getValue() {
        return value;
    }
    
    public double getAbsValue()
    {
        return value.abs();
    }

    void print(PrintStream out) {
        out.println(this);
    }

    SpectralSample conjugate() {
        return new SpectralSample(frequency, value.conjugate());
    }

    SpectralSample times(SpectralSample other) {
        return new SpectralSample(frequency, value.multiply(other.value));
    }
    
}
