/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.signalprocessing;

import java.util.ArrayList;
import llnl.gnem.core.signalprocessing.extended.ComplexSequence;
import llnl.gnem.core.signalprocessing.extended.SignalProcessingException;
import org.apache.commons.math3.complex.Complex;

/**
 *
 * @author dodge1
 */
public class SpectralOps {

    public static int nextPowerOf2(final int a) {
        int b = 1;
        while (b < a) {
            b = b << 1;
        }
        return b;
    }

    public static float[] fftshift(float[] data) {
        int n = data.length;
        int m = n / 2;
        for (int j = 0; j < m; ++j) {
            float tmp = data[m + j];
            data[m + j] = data[j];
            data[j] = tmp;
        }
        return data;
    }

    public static Complex[] fftshift(Complex[] data) {
        int n = data.length;
        int m = n / 2;
        for (int j = 0; j < m; ++j) {
            Complex tmp = data[m + j];
            data[m + j] = data[j];
            data[j] = tmp;
        }
        return data;
    }

    public static ComplexSpectrum computeCrossSpectrum(float[] f, float[] g, double dt) {
        if (f.length != g.length) {
            throw new IllegalStateException("f and g must have the same length!");
        }
        ComplexSpectrum fS = computeSpectrum(f, dt);
        ComplexSpectrum gS = computeSpectrum(g, dt);
        ComplexSpectrum fSconj = fS.conjugate();
        return fSconj.times(gS);
    }

    public static ArrayList<PeriodogramSample> computeCoherence(float[] x, float[] y, double dt) {
        if (x.length != y.length) {
            throw new IllegalStateException("f and g must have the same length!");
        }
        WelchPSD psdXY = new WelchPSD(x, y, dt);
        WelchPSD psdX = new WelchPSD(x, dt);
        WelchPSD psdY = new WelchPSD(y, dt);
        return combinePSDs(psdXY, psdX, psdY);
    }

    public static ArrayList<PeriodogramSample> computeCoherence(float[] x, float[] y, double dt, int windowLength) {
        if (x.length != y.length) {
            throw new IllegalStateException("f and g must have thesame length!");
        }
        WelchPSD psdXY = new WelchPSD(x, y, dt, windowLength);
        WelchPSD psdX = new WelchPSD(x, dt, windowLength);
        WelchPSD psdY = new WelchPSD(y, dt, windowLength);
        return combinePSDs(psdXY, psdX, psdY);
    }

    public static ArrayList<PeriodogramSample> computeCoherence(float[] x, float[] y, double dt, int windowLength, int noverlap) {
        if (x.length != y.length) {
            throw new IllegalStateException("f and g must have thesame length!");
        }
        WelchPSD psdXY = new WelchPSD(x, y, dt, windowLength, noverlap);
        WelchPSD psdX = new WelchPSD(x, dt, windowLength, noverlap);
        WelchPSD psdY = new WelchPSD(y, dt, windowLength, noverlap);
        return combinePSDs(psdXY, psdX, psdY);
    }

    public static ArrayList<PeriodogramSample> computeCoherence(float[] x, float[] y, double dt, int windowLength, int noverlap, int nfft) {
        if (x.length != y.length) {
            throw new IllegalStateException("f and g must have thesame length!");
        }
        WelchPSD psdXY = new WelchPSD(x, y, dt, windowLength, noverlap, nfft);
        WelchPSD psdX = new WelchPSD(x, dt, windowLength, noverlap, nfft);
        WelchPSD psdY = new WelchPSD(y, dt, windowLength, noverlap, nfft);
        return combinePSDs(psdXY, psdX, psdY);
    }

    public static ArrayList<PeriodogramSample> computeCoherence(float[] x, float[] y, double dt, int windowLength, int noverlap, int nfft, WindowFunction.WindowType windowType) {
        if (x.length != y.length) {
            throw new IllegalStateException("f and g must have thesame length!");
        }
        WelchPSD psdXY = new WelchPSD(x, y, dt, windowLength, noverlap, nfft, windowType);
        WelchPSD psdX = new WelchPSD(x, dt, windowLength, noverlap, nfft, windowType);
        WelchPSD psdY = new WelchPSD(y, dt, windowLength, noverlap, nfft, windowType);
        return combinePSDs(psdXY, psdX, psdY);
    }

    private static ArrayList<PeriodogramSample> combinePSDs(WelchPSD psdXY, WelchPSD psdX, WelchPSD psdY) {
        ArrayList<PeriodogramSample> xy = psdXY.getOneSidedPSD();
        ArrayList<PeriodogramSample> xx = psdX.getOneSidedPSD();
        ArrayList<PeriodogramSample> yy = psdY.getOneSidedPSD();

        ArrayList<PeriodogramSample> result = new ArrayList<>();
        for (int j = 0; j < xy.size(); ++j) {
            PeriodogramSample psxy = xy.get(j);
            PeriodogramSample psxx = xx.get(j);
            PeriodogramSample psyy = yy.get(j);
            double f = psxy.getFrequency();
            double v = psxy.getValue() / Math.sqrt(psxx.getValue() * psyy.getValue());
            result.add(new PeriodogramSample(f, v));
        }
        return result;
    }

    public static ComplexSpectrum computeSpectrum(float[] data, double dt) {
        Complex[] v = fftshift(fft(data));
        int n = v.length;
        double df = 1.0 / dt / n;
        double fs = 1 / 2.0 / dt;

        double[] freq = new double[n];
        for (int j = 0; j < n; ++j) {
            double f = -fs + j * df;
            freq[j] = f;
        }
        ArrayList<SpectralSample> samples = new ArrayList<>();
        for (int j = 0; j < n; ++j) {
            SpectralSample samp = new SpectralSample(freq[j], v[j]);
            samples.add(samp);
        }
        return new ComplexSpectrum(samples);

    }
    
    public static double computeEntropy(float[] data) {
        Complex[] g = fft(data);
        double[] v = new double[g.length];
        int N = v.length;
        double sum = 0;
        for( int j = 0; j < N; ++j){
            double tmp = g[j].abs();
            v[j] = tmp*tmp /N;
            sum += v[j];
        }
        
        double entropy = 0;
        for(int j = 0; j < N; ++j){
            double p = v[j]/sum;
            if( p > 0){
                entropy += p * Math.log(p);
            }
        }
        
        return -entropy;
    }

    /**
     * The FFT of a real data series The input data are padded to alow the
     * recovery of the full data series (through ifft)
     *
     * Note: this method is complementary to the ifft method below
     *
     * @param data - the float valued data array
     * @return the complex valued array result
     */
    public static Complex[] fft(float[] data) {
        float[] x = data.clone();
        ComplexSequence seq = new ComplexSequence(x);
        seq.dft();

        Complex[] result = new Complex[seq.length()];
        for (int i = 0; i < seq.length(); i++) {
            result[i] = new Complex(seq.getR(i), seq.getI(i));
        }

        return result;
    }

    /**
     * The InverseFFT of a complex series.
     *
     * Note: if you used the fft method above, feeding the resulting complex
     * array into this method will return the original sequence back (plus some
     * zero padding)
     *
     * @param data the complex valued data array
     * @return the real part of the ifft
     * @throws SignalProcessingException Thrown for multiple type of procesing
     * errors.
     */
    public static Complex[] ifft(Complex[] data) throws SignalProcessingException {
        Complex[] x = data.clone();
        float[] realvalues = new float[x.length];
        float[] imagvalues = new float[x.length];

        for (int i = 0; i < x.length; i++) {
            realvalues[i] = (float) x[i].getReal();
            imagvalues[i] = (float) x[i].getImaginary();
        }
        ComplexSequence seq;
        seq = new ComplexSequence(realvalues, imagvalues);
        seq.idft();

        Complex[] result = new Complex[seq.length()];
        for (int i = 0; i < seq.length(); i++) {
            result[i] = new Complex(seq.getR(i), seq.getI(i));
        }

        return result;
    }

}
