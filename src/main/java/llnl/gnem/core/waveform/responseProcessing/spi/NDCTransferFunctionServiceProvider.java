/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.responseProcessing.spi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import llnl.gnem.core.util.MathFunctions.Complex;
import llnl.gnem.core.waveform.responseProcessing.ResponseType;

/**
 *
 * @author dodge1
 */
public class NDCTransferFunctionServiceProvider extends DefaultTransferFunctionServiceProvider {

    public NDCTransferFunctionServiceProvider() {
        super();
    }

    @Override
    public EnumSet<ResponseType> supportedSourceResponseTypes() {

        return EnumSet.of(ResponseType.PAZ, ResponseType.FAP, ResponseType.PAZFIR);
    }


    @Override
    protected void dseis(int nfreq, double delfrq, double epoch, double[] xre, double[] xim, ResponseType type, String kf,
            String inSta, String inChan) throws IOException {
        /*
         * Branching routine to apply the individual instrument responses.
         */
        for (int idx = 0; idx < nfreq; idx++) {
            xre[idx] = 1.0e0;
            xim[idx] = 0.0e0;
        }
        switch (type) {

            case PAZ:
            case FAP:
            case PAZFIR:
                // FIXME insert service loader result here?
                transfer(kf, delfrq, nfreq, xre, xim);
                break;
            default:
                throw new IllegalStateException("Unhandled type: " + type);
        }

    }

    private static final double TWOPI = 2 * Math.PI;

    public static void transfer(String filename, double dt, int nfr, double[] xre, double[] xim) throws FileNotFoundException {

        double start_fr = 0.0;
        double end_fr = dt * (nfr - 1);

        Polar[] cascade = new Polar[nfr];
        for (int i = 0; i < cascade.length; i++) {
            cascade[i] = new Polar(0.0, 1.0);
        }

        Scanner sc = new Scanner(new File(filename));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            if (line.charAt(0) != '#') {
                if (line.contains("theoretical") || line.contains("measured")) {
                    Polar[] result = new Polar[0];
                    if (line.contains("paz")) {
                        double normFactor = readNormFactor(sc);
                        DComplex[] poles = readDComplex(sc);
                        DComplex[] zeros = readDComplex(sc);

                        result = doPaz(nfr, start_fr, end_fr, poles, zeros);
                        for (int j = 0; j < nfr; j++) {
                            result[j].a *= normFactor;
                        }
                    } else if (line.contains("fap")) {
                        Fap[] faps = readFap(sc);
                        result = doFap(nfr, start_fr, end_fr, faps);
                    } else if (line.contains("fir")) {
                        Fir fir = readFir(sc);
                        result = doFir(nfr, start_fr, end_fr, fir);
                    }

                    /*
                     * Cascade individual group responses
                     */
                    for (int j = 0; j < result.length; j++) {
                        cascade[j].a *= result[j].a;
                        cascade[j].p += result[j].p;
                    }
                }
            }
        }

        for (int j = 0; j < nfr; j++) {
            Complex c = tocmplx(cascade[j]);
            xre[j] = c.real();
            xim[j] = c.imag();
        }
    }

    private static double readNormFactor(Scanner sc) {
        double normFactor = sc.nextDouble();
        sc.nextLine();
        return normFactor;
    }

    private static DComplex[] readDComplex(Scanner sc) {
        String line = sc.nextLine();

        int linesLeft = getFirstInt(line);
        DComplex[] results = new DComplex[linesLeft];
        while (linesLeft > 0) {
            StringTokenizer tokenizer = new StringTokenizer(sc.nextLine());

            double zReal = Double.parseDouble(tokenizer.nextToken());
            double zImag = Double.parseDouble(tokenizer.nextToken());
            double eReal = Double.parseDouble(tokenizer.nextToken());
            double eImag = Double.parseDouble(tokenizer.nextToken());

            DComplex dc = new DComplex(new Complex(zReal, zImag), new Complex(eReal, eImag));
            results[results.length - linesLeft] = dc;

            linesLeft--;
        }

        return results;
    }

    private static Fap[] readFap(Scanner sc) {
        String line = sc.nextLine();

        int linesLeft = getFirstInt(line);
        Fap[] results = new Fap[linesLeft];
        while (linesLeft > 0) {
            StringTokenizer tokenizer = new StringTokenizer(sc.nextLine());

            double f = Double.parseDouble(tokenizer.nextToken());
            double a = Double.parseDouble(tokenizer.nextToken());
            double p = Double.parseDouble(tokenizer.nextToken());
            double ae = Double.parseDouble(tokenizer.nextToken());
            double pe = Double.parseDouble(tokenizer.nextToken());

            p *= TWOPI / 360.0;

            Fap fap = new Fap(f, a, p, ae, pe);
            results[results.length - linesLeft] = fap;

            linesLeft--;
        }

        return results;
    }

    private static Fir readFir(Scanner sc) {
        double isr = sc.nextDouble();
        Fir fir = new Fir(isr);
        sc.nextLine();

        String line = sc.nextLine();
        int linesLeft = getFirstInt(line);
        fir.setNumerator(linesLeft);
        while (linesLeft > 0) {
            StringTokenizer tokenizer = new StringTokenizer(sc.nextLine());

            double nu = Double.parseDouble(tokenizer.nextToken());
            double nue = Double.parseDouble(tokenizer.nextToken());

            int index = fir.nnc - linesLeft;
            fir.nu[index] = nu;
            fir.nue[index] = nue;

            linesLeft--;
        }

        line = sc.nextLine();
        linesLeft = Integer.parseInt(line.trim());
        fir.setDenominator(linesLeft);
        while (linesLeft > 0) {
            StringTokenizer tokenizer = new StringTokenizer(sc.nextLine());

            double de = Double.parseDouble(tokenizer.nextToken());
            double dee = Double.parseDouble(tokenizer.nextToken());

            int index = fir.ndc - linesLeft;
            fir.de[index] = de;
            fir.dee[index] = dee;

            linesLeft--;
        }

        return fir;
    }

    /*
     * Compute response using poles and zeros
     */
    private static Polar[] doPaz(int nfr, double start_fr, double end_fr, DComplex[] poles, DComplex[] zeros) {
        double amp, phase, delta_f;

        if (nfr == 1) /*
                       * avoid 0 in denominator
         */ {
            delta_f = 1.0;
            /*
                            * value irrelevant, loop ends first
             */
        } else {
            /*
             * constant linear spacing
             */
            delta_f = (end_fr - start_fr) / (nfr - 1.0);
        }

        Polar[] result = new Polar[nfr];
        double delta = 1.0;
        for (int j = 0; j < nfr; j++, delta *= delta_f) {
            double omega = TWOPI * (start_fr + j * delta_f);
            result[j] = new Polar(0.0, 1.0);

            for (int i = 0; i < zeros.length; i++) {
                Polar polar = topolar(-zeros[i].z.real(), (omega - zeros[i].z.imag()));
                result[j].a *= polar.a;
                result[j].p += polar.p;
            }

            for (int i = 0; i < poles.length; i++) {
                Polar polar = topolar(-poles[i].z.real(), (omega - poles[i].z.imag()));
                if (polar.a != 0.0) {
                    result[j].a /= polar.a;
                }
                result[j].p -= polar.p;
            }
        }

        return result;
    }

    private static Polar[] doFap(int nfr, double start_fr, double end_fr, Fap[] faps) {
        int order;
        double freq;
        /*
                      * frequency or log(f) if log_flag
         */
        double ftmp1;
        double ftmp2;
        double delta_f;

        /*
         * Lagrange interpolation works better for log function
         */
        double[] f = new double[faps.length];
        double[] a = new double[faps.length];
        for (int i = 0; i < faps.length; i++) {
            if (faps[i].f <= 1.0e-20) {
                faps[i].f = 1.0e-20;
                /*
                                      * To avoid log(0)
                 */
            }
            if (faps[i].a <= 1.0e-20) {
                faps[i].a = 1.0e-20;
                /*
                                      * To avoid log(0)
                 */
            }

            f[i] = Math.log10(faps[i].f);
            a[i] = Math.log10(faps[i].a);
        }

        if (nfr == 1) {
            /*
             * avoid 0 in denominator
             */
            delta_f = 1.0;
            /*
                            * value irrelevant, loop ends first
             */
        } else {
            delta_f = (end_fr - start_fr) / (nfr - 1.0);
        }

        Polar[] result = new Polar[nfr];
        for (int j = 0; j < nfr; j++) {
            /*
             * constant delta in linear f space
             */
 /*
             * lagrange interpolation works better for log func.
             */

            ftmp2 = start_fr + j * delta_f;
            if (ftmp2 <= 1.0e-20) {
                ftmp2 = 1.0e-20;
                /*
                                  * To avoid log(0)
                 */
            }

            freq = Math.log10(ftmp2);
            result[j] = new Polar(0.0, 1.0);
            int i;
            for (i = 0; i < faps.length; i++) {
                if (freq < f[i]) {
                    break;
                }
            }

            i -= 2;
            order = 4;
            if (i < 0) {
                i = 0;
                order = 2;
            } else if (i > faps.length - 4) {
                i = faps.length - 2;
                order = 2;
            }

            double[] p = new double[faps.length];
            for (int k = 0; k < faps.length; k++) {
                p[k] = faps[k].p;
            }

            ftmp1 = lagrange(a, f, i, order, freq);
            result[j].a = ftmp1;
            ftmp1 = lagrange(p, f, i, order, freq);
            result[j].p = ftmp1;
            result[j].a = Math.pow(10.0, result[j].a);
            /*
             * DBG(fprintf (stderr, "%lf %lf %lf\n", freq, result[i].a, result[i].p));
             */
        }

        /*
         * DBG(fprintf (stderr, "%s\n","end"));
         */
        return result;
    }

    private static Polar[] doFir(int nfr, double start_fr, double end_fr, Fir firs) {
        int flag = 1;
        /*
                       * 1= forward transform, -1 reverse
         */

 /*
         * Initialize for error handling, one return at bailout
         */

 /*
         * frequency amplitude phase
         */
        int n = 513;
        Fap[] faps = new Fap[n];
        double[] xr = new double[n * 2];

        for (int i = 0; i < n; i++) {
            faps[i] = new Fap(0.0, 0.0, 0.0, 0.0, 0.0);
        }

        /*
         * Set up data in a large array
         */
        for (int i = 0; i < firs.nnc; i++) {
            xr[i] = firs.nu[i];
        }
        for (int i = firs.nnc; i < faps.length * 2; i++) {
            xr[i] = 0.0;
        }

        /*
         * Set up and call fft for numerator coefficients
         */
        double df = 1.0;

        /*
         * Find greatest power of 2 that is less than twice faps.n
         */
        int nexp;
        for (nexp = 1; Math.pow(2.0, nexp) < 2 * faps.length; nexp++) {
        }
        nexp--;

        /*
         * note df no longer passed since no scaling is to be done, ganz 6/11
         */
        odfftr(nexp, xr, flag);

        /*
         * Compute frequency, amplitude, and phase for numerator (fir)
         */
        df = firs.isr / ((faps.length - 1) * 2);
        for (int i = 0; i < faps.length; i++) {
            faps[i].a = Math.hypot(xr[i * 2], xr[i * 2 + 1]);
            if (xr[i * 2] == 0 && xr[i * 2 + 1] == 0) {
                faps[i].p = 0;
            } else {
                faps[i].p = Math.atan2(xr[i * 2], xr[i * 2 + 1]);
            }
            faps[i].f = i * df;
        }

        if (firs.ndc > 0) {
            /*
             * Set up data in a large array
             */
            for (int i = 0; i < firs.ndc; i++) {
                xr[i] = firs.de[i];
            }
            for (int i = firs.ndc; i < faps.length * 2; i++) {
                xr[i] = 0.0;
            }

            /*
             * Set up and call fft for numerator coefficients
             */
            df = 1.0;
            /*
             * df for scaling, no longer passed since no scaling was ever done
             */
            odfftr(nexp, xr, flag);

            /*
             * Compute frequency, amplitude, and phase for denom. (iir)
             */
            for (int i = 0; i < faps.length; i++) {
                faps[i].a /= Math.hypot((1.0 - xr[i * 2]), xr[i * 2 + 1]);
                if ((1.0 - xr[i * 2]) == 0 && xr[i * 2 + 1] == 0) {
                    faps[i].p -= 0;
                } else {
                    faps[i].p -= Math.atan2((1.0 - xr[i * 2]), xr[i * 2 + 1]);
                }
            }
        }

        /*
         * Make the phase a smooth function, i.e. get rid of wraps \/\
         */
 /*
         * Find the index with the derivative closest to zero
         */
        // TODO this is a bug in the C version of the code, change the type of min
        // back to double after testing is complete
        // double min = TWOPI;
        int min = (int) TWOPI;
        int minindex = 0;
        double[] deriv = new double[faps.length];
        for (int i = 0; i < faps.length - 1; i++) {
            deriv[i] = faps[i + 1].p - faps[i].p;
            if (Math.abs(deriv[i]) < min) {
                min = (int) Math.abs(deriv[i]);
                minindex = i;
            }
        }

        /*
         * Correct phases with high indicies
         */
        for (int i = minindex; i < faps.length - 2; i++) {
            if (sign(deriv[i]) != sign(deriv[i + 1])) {
                for (int j = i + 2; j < faps.length; j++) {
                    faps[j].p = faps[j].p + sign(deriv[i]) * TWOPI;
                }
                deriv[i + 1] = faps[i + 2].p - faps[i + 1].p;
            }
        }

        /*
         * Correct phases with low indicies
         */
        for (int i = minindex; i > 0; i--) {
            if (sign(deriv[i]) != sign(deriv[i - 1])) {
                for (int j = i - 1; j >= 0; j--) {
                    faps[j].p = faps[j].p + sign(deriv[i]) * TWOPI;
                }
                deriv[i - 1] = faps[i].p - faps[i - 1].p;
            }
        }

        /*
         * Call fap to interpolate for proper values
         */
        return doFap(nfr, start_fr, end_fr, faps);
    }

    private static Polar topolar(double real, double imag) {
        double amp = Math.sqrt(real * real + imag * imag);
        double phase;
        if (imag == 0.0 && real == 0.0) {
            phase = 0.0;
        } else {
            phase = Math.atan2(imag, real);
        }

        return new Polar(phase, amp);
    }

    private static double lagrange(double[] f, double[] xi, int offset, int n, double x) {
        /*
         * evenly spaced , in order
         */
        int i, k;
        double prod;

        double fx = 0.0;
        for (k = 0; k < n; k++) {
            prod = 1.0;
            for (i = 0; i < n; i++) {
                if (i != k) {
                    prod = prod * (x - xi[i + offset]) / (xi[k + offset] - xi[i + offset]);
                }
            }
            fx = fx + prod * f[k + offset];
        }
        return fx;
    }

    private static void odfftr(int nexp, double[] xr, int flag) {
        /*
         * complex c;
         */
        double c;
        int i, j, k, m, p, n1;
        int Ls, ks, ms, jm, dk;
        double wr, wi, tr, ti;

        double[] xi;
        int n2;
        int npts, n, jj;

        /*
         * need to build imaginary array
         */
        npts = 1;
        n2 = 1;

        npts = 1;

        i = nexp;
        while (i-- > 0) {
            npts <<= 1;
        }

        n = npts;
        xi = new double[npts + 2];

        if (flag < 0) {
            for (i = 0; i <= npts / 2; i++) {
                k = i * 2;
                xi[i] = xr[k + 1];
                xr[i] = xr[k];
            }
            for (i = 0; i <= npts / 2; i++) {
                k = i * 2;
                xi[npts - i] = -xi[i];
                xr[npts - i] = xr[i];
            }

        } else {
            for (i = 0; i < npts; i++) {
                xi[i] = 0.0f;
            }

        }

        n1 = n / n2;
        /*
         * do bit reversal permutation
         */
        for (k = 0; k < n1; ++k) {
            /*
                                    * This is algorithms 1.5.1 and 1.5.2.
             */
            j = 0;
            m = k;
            p = 1;
            /*
             * p = 2^q, q used in the book
             */
            while (p < n1) {
                j = 2 * j + (m & 1);
                m >>= 1;
                p <<= 1;
            }

            assert (p == n1);
            /*
             * make sure n1 is a power of two
             */
            if (j > k) {
                for (i = 0; i < n2; ++i) {
                    /*
                     * swap k <-> j row
                     */
                    c = xr[k * n2 + i];
                    /*
                     * for all columns
                     */
                    xr[k * n2 + i] = xr[j * n2 + i];
                    xr[j * n2 + i] = c;

                    c = xi[k * n2 + i];
                    /*
                     * for all columns
                     */
                    xi[k * n2 + i] = xi[j * n2 + i];
                    xi[j * n2 + i] = c;

                }
            }
        }

        /*
         * This is (3.1.7), page 124
         */
        p = 1;
        while (p < n1) {
            Ls = p;
            p <<= 1;
            jm = 0;
            /*
             * jm is j*n2
             */
            dk = p * n2;
            for (j = 0; j < Ls; ++j) {
                wr = Math.cos(Math.PI * j / Ls);
                /*
                 * real and imaginary part
                 */
                wi = -flag * Math.sin(Math.PI * j / Ls);
                /*
                 * of the omega
                 */
                for (k = jm; k < n; k += dk) {
                    /*
                     * "butterfly"
                     */
                    ks = k + Ls * n2;
                    for (i = 0; i < n2; ++i) {
                        /*
                         * for each row
                         */
                        m = k + i;
                        ms = ks + i;
                        tr = wr * xr[ms] - wi * xi[ms];
                        ti = wr * xi[ms] + wi * xr[ms];
                        xr[ms] = xr[m] - tr;
                        xi[ms] = xi[m] - ti;
                        xr[m] += tr;
                        xi[m] += ti;
                    }
                }
                jm += n2;
            }
        }

        /*
         * now combine the real/imaginary parts back into xr
         */
        if (flag > 0) {
            for (i = npts / 2; i >= 0; i--) {
                k = i * 2;
                xr[k] = xr[i];
                xr[k + 1] = xi[i];
            }
        }
    }

    private static int sign(double a) {
        return a < 0.0 ? -1 : 1;
    }

    private static Complex tocmplx(Polar polar) {
        return new Complex(polar.a * Math.cos(polar.p), polar.a * Math.sin(polar.p));
    }

    private static int getFirstInt(String line) {
        Pattern intPattern = Pattern.compile("\\d+");
        Matcher matcher = intPattern.matcher(line);
        matcher.find();
        return Integer.parseInt(matcher.group());
    }

    private static class DComplex {

        public final Complex z;
        public final Complex e;

        public DComplex(Complex z, Complex e) {
            this.z = z;
            this.e = e;
        }
    }

    private static class Fap {

        public double f;
        public double a;
        public double p;
        public double ae;
        public double pe;

        public Fap(double f, double a, double p, double ae, double pe) {
            this.f = f;
            this.a = a;
            this.p = p;
            this.ae = ae;
            this.pe = pe;
        }
    }

    private static class Fir {

        public double isr;
        public int nnc;
        public int ndc;
        public double[] nu;
        public double[] nue;
        public double[] de;
        public double[] dee;

        public Fir(double isr) {
            this.isr = isr;
        }

        public void setNumerator(int nnc) {
            this.nnc = nnc;
            nu = new double[nnc];
            nue = new double[nnc];
        }

        public void setDenominator(int ndc) {
            this.ndc = ndc;
            de = new double[ndc];
            dee = new double[ndc];
        }
    }

    private static class Polar {

        public double p;
        public double a;

        public Polar(double p, double a) {
            this.p = p;
            this.a = a;
        }
    }

}
