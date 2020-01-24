package llnl.gnem.apps.detection.core.signalProcessing;

/**
 *
 * @author Dave Harris
 */
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Copyright (c) 2005 Regents of the University of California All rights
 * reserved Author: Dave Harris Created: Dec 12, 2005 Time: 12:47:36 PM Last
 * Modified: Dec 14, 2005
 * <p/>
 * <p/>
 * | | 2
 * computes: sum | sum z exp( -j*omega * (sn*xn + se*xe ) )| k | i i k i i |
 * <p/>
 * smax start here o x x x | x x x x -> evaluate in + horizontal direction
 * return in scanned order in this direction x x x x | x x x x
 * <p/>
 * x x x x | x x x x
 * <p/>
 * x x x x | x x x o end here -smax ------------------------------- smax x x x x
 * | x x x x
 * <p/>
 * x x x x | x x x x lower part obtained by symmetry
 * <p/>
 * x x x x | x x x x
 * <p/>
 * x x x x | x x x x
 * -smax
 * <p/>
 * Calls the narrowband FK routine FKCartesian.evaluate repeatedly and
 * accumulates the result to form a wideband FK spectrum (Kvaerna et al.
 * algorithm). This algorithm does nothing more than calculate the energy in
 * rudimentary frequency-domain beams on a Cartesian grid of slowness values.
 */
public class BroadbandFK {

    private float[][] fks;
    private float E;

    /**
     * @param smax float maximum slowness to be evaluated in sec/km
     * @param ns int number of slowness samples in one dimension (total grid has
     * ns*ns samples)
     * @param xnorth float[] north offset of sensors in km (dnorth)
     * @param xeast float[] east offset of sensors in km (deast)
     * @param waveforms ArrayList< float[] > ArrayList containing waveforms in
     * float[]'s
     * @param delta float sampling interval of waveform data in seconds
     * @param f1 float low frequency for FK evaluation
     * @param f2 float high frequency for FK evaluation
     */
    public BroadbandFK(float smax,
            int ns,
            float[] xnorth,
            float[] xeast,
            ArrayList<float[]> waveforms,
            float delta,
            float f1,
            float f2) {

        int nc = xnorth.length;

        // Compute discrete Fourier transforms of waveforms
        int npts = waveforms.get(0).length;
        int nfft = 1;
        int m = 0;
        while (nfft < npts) {
            nfft *= 2;
            m += 1;
        }
        RFFT fft = new RFFT(m);

        ArrayList<float[]> DFTs = new ArrayList<>();
        for (int i = 0; i < nc; i++) {
            float[] x = new float[nfft];
            float[] y = waveforms.get(i);
            System.arraycopy(y, 0, x, 0, npts);
            fft.dft(x);
            DFTs.add(x);
        }

        // Evaluate FK spectra
        float fmax = (1.0f / (2.0f * delta));
        float df = fmax / ((float) (nfft / 2));
        int n1 = Math.round(f1 / df);
        int n2 = Math.round(f2 / df);
        if (n1 == 0) {
            throw new IllegalArgumentException("Input data not long enough to evaluate minimum frequency.");
        }
        if (n2 >= nfft) {
            String msg = String.format("Decimated sample rate is too low for FFT size in BroadbandFK. Decrease either the decimation rate or the max FK frequence!");
            throw new IllegalArgumentException(msg);
        }

        FKCartesian evaluator = new FKCartesian(smax, ns, xnorth, xeast);

        float[] fk;
        fks = new float[ns][ns];
        float[] Zr = new float[nc];
        float[] Zi = new float[nc];
        E = 0.0f;
        for (int n = n1; n <= n2; n++) {
            float omega = 2.0f * (float) Math.PI * n * df;
            evaluator.initialize(omega);
            for (int i = 0; i < nc; i++) {
                float[] x = DFTs.get(i);
                Zr[i] = x[n];
                Zi[i] = x[nfft - n];
                E += Zr[i] * Zr[i] + Zi[i] * Zi[i];
            }
            fk = evaluator.evaluate(Zr, Zi);

            for (int i = 0; i < ns; i++) {
                for (int j = 0; j < ns; j++) {
                    fks[i][j] += fk[i * ns + j];
                }
            }

        }

    }

    /**
     * @param smax float maximum slowness to be evaluated in sec/km
     * @param ns int number of slowness samples in one dimension (total grid has
     * ns*ns samples)
     * @param xnorth float[] north offset of sensors in km (dnorth)
     * @param xeast float[] east offset of sensors in km (deast)
     * @param waveforms ArrayList< float[] > ArrayList containing waveforms in
     * float[]'s
     * @param delta float sampling interval of waveform data in seconds
     * @param f1 float low frequency for FK evaluation
     * @param f2 float high frequency for FK evaluation
     * @param windowLength float length of correlation window in seconds (10%
     * cosine taper)
     * @param referenceWaveforms ArrayList< float[] > ArrayList containing
     * reference waveforms in float[]'s
     */
    public BroadbandFK(float smax,
            int ns,
            float[] xnorth,
            float[] xeast,
            ArrayList<float[]> waveforms,
            float delta,
            float f1,
            float f2,
            float windowLength,
            ArrayList<float[]> referenceWaveforms) {

        int nch = xnorth.length;

        // Compute correlation functions, then window and transform
        int N = waveforms.get(0).length + referenceWaveforms.get(0).length - 1;
        int nfft = 1;
        int m = 0;
        while (nfft < N) {
            nfft *= 2;
            m += 1;
        }
        RFFT fft = new RFFT(m);

        ArrayList<float[]> DFTs = new ArrayList<>();
        float[] x = new float[nfft];
        float[] xr = new float[nfft];
        float[] product = new float[nfft];
        float[] w = new float[nfft];

        int wlength = Math.round(windowLength / delta);
        for (int i = 0; i < wlength; i++) {
            w[i] = 1.0f;
        }
        int tlength = wlength / 10;
        for (int i = 0; i < tlength; i++) {
            w[i] = (float) (0.5 * (1.0 - Math.cos((Math.PI * i) / tlength)));
            w[wlength - i] = w[i];
        }
        Sequence.cshift(w, -wlength / 2);

        for (int ich = 0; ich < nch; ich++) {

            // scale new waveforms and transform
            Arrays.fill(x, 0.0f);
            float[] tmp = waveforms.get(ich);
            float E = 0.0f;
            for (int j = 0; j < tmp.length; j++) {
                E += tmp[j] * tmp[j];
            }
            float scale = 1.0f / (float) Math.sqrt(E);
            for (int j = 0; j < tmp.length; j++) {
                x[j] = scale * tmp[j];
            }
            fft.dft(x);

            // scale reference waveforms and transform
            Arrays.fill(xr, 0.0f);
            tmp = referenceWaveforms.get(ich);
            E = 0.0f;
            for (int j = 0; j < tmp.length; j++) {
                E += tmp[j] * tmp[j];
            }
            scale = 1.0f / (float) Math.sqrt(E);
            for (int j = 0; j < tmp.length; j++) {
                xr[j] = scale * tmp[j];
            }
            fft.dft(xr);

            // compute correlation function
            fft.dftproduct(x, xr, product, -1.0f);
            fft.idft(product);
            for (int j = 0; j < nfft; j++) {
                product[j] *= w[j];
            }
            fft.dft(product);
            DFTs.add(product);
        }

        // Evaluate FK spectra
        float fmax = (1.0f / (2.0f * delta));
        float df = fmax / ((float) (nfft / 2));
        int n1 = Math.round(f1 / df);
        int n2 = Math.round(f2 / df);
        if (n1 == 0) {
            throw new IllegalArgumentException("Input data not long enough to evaluate minimum frequency.");
        }

        FKCartesian evaluator = new FKCartesian(smax, ns, xnorth, xeast);

        float[] fk;
        fks = new float[ns][ns];
        float[] Zr = new float[nch];
        float[] Zi = new float[nch];
        E = 0.0f;
        for (int n = n1; n <= n2; n++) {
            float omega = 2.0f * (float) Math.PI * n * df;
            evaluator.initialize(omega);
            for (int i = 0; i < nch; i++) {
                x = DFTs.get(i);
                Zr[i] = x[n];
                Zi[i] = x[nfft - n];
                E += Zr[i] * Zr[i] + Zi[i] * Zi[i];
            }
            fk = evaluator.evaluate(Zr, Zi);

            for (int i = 0; i < ns; i++) {
                for (int j = 0; j < ns; j++) {
                    fks[i][j] += fk[i * ns + j];
                }
            }

        }

    }

    public float[][] getFKSpectrum() {
        return fks;
    }

    public float getEnergy() {
        return E;
    }

}
