/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.signalprocessing;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author dodge1
 */
public class WelchPSD {

    private final Periodogram periodogram;

    public WelchPSD(float[] samples, double dt) {
        int nsamps = samples.length;
        int numSections = 8;

        int windowLength = nsamps * 2 / (numSections + 1);// 50% overlap
        int noverlap = windowLength / 2;

        int nfft = Math.max(256, SpectralOps.nextPowerOf2(windowLength));
        WindowFunction.WindowType windowType = WindowFunction.WindowType.HAMMING;

        ArrayList<Periodogram> periodograms = createPeriodogramList(samples, windowLength, noverlap, nfft, windowType, dt);
        periodogram = Periodogram.average(periodograms);
    }

    public WelchPSD(float[] samples, double dt, int windowLength) {
        this(samples, dt, windowLength, 0);
    }

    public WelchPSD(float[] samples, double dt, int windowLength, int noverlap) {
        this(samples, dt, windowLength, noverlap, Math.max(256, SpectralOps.nextPowerOf2(windowLength)));
    }

    public WelchPSD(float[] samples, double dt, int windowLength, int noverlap, int nfft) {
        this(samples, dt, windowLength, noverlap, nfft, WindowFunction.WindowType.HAMMING);
    }

    public WelchPSD(float[] samples, double dt, int windowLength, int noverlap, int nfft, WindowFunction.WindowType windowType) {
        ArrayList<Periodogram> periodograms = createPeriodogramList(samples, windowLength, noverlap, nfft, windowType, dt);
        periodogram = Periodogram.average(periodograms);

    }

    private ArrayList<Periodogram> createPeriodogramList(float[] samples, int windowLength, int noverlap, int nfft, WindowFunction.WindowType windowType, double dt) throws IllegalStateException {
        int nsamps = samples.length;
        if (windowLength > nsamps) {
            throw new IllegalStateException("Supplied windowLength > data length!");
        }
        if (noverlap < 0 || noverlap >= nsamps) {
            throw new IllegalStateException("noverlap must be between 0 and data length-1!");
        }
        if (nfft < windowLength) {
            nfft = SpectralOps.nextPowerOf2(windowLength);
        }
        int numSections = (nsamps - windowLength) / (windowLength - noverlap) + 1;
        float[] window = WindowFunction.getWindow(windowType, windowLength);
        float[] padded = new float[nfft];
        ArrayList<Periodogram> periodograms = new ArrayList<>();
        for (int j = 0; j < numSections; ++j) {
            int offset = j * (windowLength - noverlap);
            Arrays.fill(padded, 0);
            System.arraycopy(samples, offset, padded, 0, windowLength);
            for (int k = 0; k < windowLength; ++k) {
                padded[k] *= window[k];
            }
            ComplexSpectrum cs = SpectralOps.computeSpectrum(padded, dt);
            periodograms.add(cs.toPeriodogram());
        }
        return periodograms;
    }

     public WelchPSD(float[] x, float[] y, double dt) {
        int nsamps = x.length;
        int numSections = 8;

        int windowLength = nsamps * 2 / (numSections + 1);// 50% overlap
        int noverlap = windowLength / 2;

        int nfft = Math.max(256, SpectralOps.nextPowerOf2(windowLength));
        WindowFunction.WindowType windowType = WindowFunction.WindowType.HAMMING;

        ArrayList<Periodogram> periodograms = createPeriodogramList(x,y, windowLength, noverlap, nfft, windowType, dt);
        periodogram = Periodogram.average(periodograms);
    }
   

    public WelchPSD(float[] x, float[] y, double dt, int windowLength) {
        this(x,y, dt, windowLength, 0);
    }

    public WelchPSD(float[] x, float[] y, double dt, int windowLength, int noverlap) {
        this(x,y, dt, windowLength, noverlap, Math.max(256, SpectralOps.nextPowerOf2(windowLength)));
    }

    public WelchPSD(float[] x, float[] y, double dt, int windowLength, int noverlap, int nfft) {
        this(x,y, dt, windowLength, noverlap, nfft, WindowFunction.WindowType.HAMMING);
    }

    public WelchPSD(float[] x, float[] y, double dt, int windowLength, int noverlap, int nfft, WindowFunction.WindowType windowType) {
        ArrayList<Periodogram> periodograms = createPeriodogramList(x,y, windowLength, noverlap, nfft, windowType, dt);
        periodogram = Periodogram.average(periodograms);

    }

    private ArrayList<Periodogram> createPeriodogramList(float[] x, float[] y, int windowLength, int noverlap, int nfft, WindowFunction.WindowType windowType, double dt) throws IllegalStateException {
        int nsamps = x.length;
        int ysamps = y.length;
        if( nsamps != ysamps){
            throw new IllegalStateException("Arrays must have equal lengths!");
        }
        if (windowLength > nsamps) {
            throw new IllegalStateException("Supplied windowLength > data length!");
        }
        if (noverlap < 0 || noverlap >= nsamps) {
            throw new IllegalStateException("noverlap must be between 0 and data length-1!");
        }
        if( windowLength < noverlap){
            throw new IllegalStateException("windowLength must be at least noverlap samples!");
        }
        if (nfft < windowLength) {
            nfft = SpectralOps.nextPowerOf2(windowLength);
        }
        int numSections = (nsamps - windowLength) / (windowLength - noverlap) + 1;
        float[] window = WindowFunction.getWindow(windowType, windowLength);
        float[] xPad = new float[nfft];
        float[] yPad = new float[nfft];
        ArrayList<Periodogram> periodograms = new ArrayList<>();

        for (int j = 0; j < numSections; ++j) {
            int offset = j * (windowLength - noverlap);
            Arrays.fill(xPad, 0);
            Arrays.fill(yPad, 0);
            System.arraycopy(x, offset, xPad, 0, windowLength);
            System.arraycopy(y, offset, yPad, 0, windowLength);
            for (int k = 0; k < windowLength; ++k) {
                xPad[k] *= window[k];
                yPad[k] *= window[k];
            }
            ComplexSpectrum csX = SpectralOps.computeSpectrum(xPad, dt);
            ComplexSpectrum csY = SpectralOps.computeSpectrum(yPad, dt);
            periodograms.add(new Periodogram(csX, csY));
        }
        return periodograms;
    }

    public ArrayList<PeriodogramSample> getTwoSidedPSD() {
        return periodogram.getTwoSidedPSD();
    }

    public ArrayList<PeriodogramSample> getOneSidedPSD() {
        return periodogram.getOneSidedPSD();
    }
}
