package llnl.gnem.core.signalprocessing.arrayProcessing;

import java.text.DecimalFormat;
import java.util.ArrayList;
import llnl.gnem.core.signalprocessing.FFT;

/**
 * Copyright (c) 2005 Regents of the University of California All rights
 * reserved Author: Dave Harris Created: Dec 12, 2005 Time: 12:47:36 PM Last
 * Modified: Dec 14, 2005
 * <p>
 * </p>
 * <p>
 * </p>
 * | | 2
 * computes: sum | sum z exp( -j*omega * (sn*xn + se*xe ) )| k | i i k i i |
 * <p>
 * </p>
 * smax start here o x x x | x x x x evaluate in + horizontal direction return
 * in scanned order in this direction x x x x | x x x x
 * <p>
 * </p>
 * x x x x | x x x x
 * <p>
 * </p>
 * x x x x | x x x o end here -smax ------------------------------- smax x x x x
 * | x x x x
 * <p>
 * </p>
 * x x x x | x x x x lower part obtained by symmetry
 * <p>
 * </p>
 * x x x x | x x x x
 * <p>
 * </p>
 * x x x x | x x x x
 * -smax
 * <p>
 * </p>
 * Calls the narrowband FK routine FKCartesian.evaluate repeatedly and
 * accumulates the result to form a wideband FK spectrum (Kvaerna et al.
 * algorithm). This algorithm does nothing more than calculate the energy in
 * rudimentary frequency-domain beams on a Cartesian grid of slowness values.
 */
public class BroadbandFK {

    /**
     * @param smax float maximum slowness to be evaluated in sec/km
     * @param ns int number of slowness samples in one dimension (total grid has
     * ns*ns samples)
     * @param xnorth float[] north offset of sensors in km (dnorth)
     * @param xeast float[] east offset of sensors in km (deast)
     * @param waveforms ArrayList float[] ArrayList containing waveforms in
     * float[]'s
     * @param delta float sampling interval of waveform data in seconds
     * @param f1 low frequency for FK evaluation
     * @param f2 high frequency for FK evaluation
     * @return float[] 2-D wideband FK spectrum represented as a 1-D array in
     * row-major order
     */
    public static float[] evaluate(float smax,
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

        ArrayList<float[]> DFTs = new ArrayList<>();
        for (int i = 0; i < nc; i++) {
            float[] x = new float[nfft];
            float[] y = waveforms.get(i);
            System.arraycopy(y, 0, x, 0, npts);
            FFT.rvfft(x, m);
            DFTs.add(x);
        }

        // Evaluate FK spectra
        float fmax = (1.0f / (2.0f * delta));
        float df = fmax / ((float) (nfft / 2));
        int n1 = nint(f1 / df);
        int n2 = nint(f2 / df);
        if (n1 == 0) {
            throw new IllegalArgumentException("Input data not long enough to evaluate minimum frequency.");
        }

        FKCartesian evaluator = new FKCartesian(smax, ns, xnorth, xeast);

        int ns2 = ns * ns;
        float[] fk;
        float[] fkaccum = new float[ns2];
        float[] Zr = new float[nc];
        float[] Zi = new float[nc];
        for (int n = n1; n <= n2; n++) {
            float omega = 2.0f * (float) Math.PI * n * df;
            evaluator.initialize(omega);
            for (int i = 0; i < nc; i++) {
                float[] x = DFTs.get(i);
                Zr[i] = x[n];
                Zi[i] = x[nfft - n];
            }
            fk = evaluator.evaluate(Zr, Zi);
            for (int i = 0; i < ns2; i++) {
                fkaccum[i] += fk[i];
            }
            FkObservable.getInstance().notifyObserversCurrentProgress(n - n1, n2 - n1);
        }

        return fkaccum;
    }

    /**
     * @param x input float
     * @return output int that is nearest to x
     */
    private static int nint(float x) {
        if (x >= 0.0f) {
            return ((int) (x + 0.5f));
        } else {
            return ((int) (x - 0.5f));
        }
    }

}
