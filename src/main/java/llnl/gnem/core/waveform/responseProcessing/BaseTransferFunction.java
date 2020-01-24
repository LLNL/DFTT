package llnl.gnem.core.waveform.responseProcessing;

import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;

public class BaseTransferFunction implements TransferFunction {

    protected  Complex[] function;
    protected  double[] frequencies;
    
    protected BaseTransferFunction() {};
    
    public BaseTransferFunction(double[] frequencies, Complex[] result) {
        function = result.clone();
        this.frequencies = Arrays.copyOf(frequencies, frequencies.length);
    }
    
    @Override
    public double[] getAmplitude() {
        double[] result = new double[function.length];
        for (int j = 0; j < result.length; ++j) {
            result[j] = function[j].abs();
        }
        return result;
    }

    @Override
    public double[] getPhase() {
        double conv = 180 / Math.PI;
        double[] result = new double[function.length];
        for (int j = 0; j < result.length; ++j) {
            double real = function[j].getReal();
            double imag = function[j].getImaginary();
            double phi = Math.atan2(imag, real);
            result[j] = phi * conv;
        }
        return result;
    }

    @Override
    public double[] getFrequencies() {
        return Arrays.copyOf(frequencies, frequencies.length);
    }
}
