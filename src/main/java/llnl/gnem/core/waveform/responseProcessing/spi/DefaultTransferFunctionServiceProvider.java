/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.responseProcessing.spi;

import java.io.IOException;
import java.util.EnumSet;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.apache.commons.math3.complex.Complex;

import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.FileInputArrayLoader;
import llnl.gnem.core.waveform.responseProcessing.CompositeTransferFunction;
import llnl.gnem.core.waveform.responseProcessing.FreqLimits;
import llnl.gnem.core.waveform.responseProcessing.ResponseMetaData;
import llnl.gnem.core.waveform.responseProcessing.ResponseType;
import llnl.gnem.core.waveform.responseProcessing.TransferFunctionUtils;
import llnl.gnem.core.waveform.responseProcessing.TransferParams;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class DefaultTransferFunctionServiceProvider implements TransferFunctionServiceProvider {

    public DefaultTransferFunctionServiceProvider() {
    }

    @Override
    public EnumSet<ResponseType> supportedSourceResponseTypes() {

        return EnumSet.of(ResponseType.ACC, ResponseType.SACPZF, ResponseType.VEL, ResponseType.DIS);
    }

    @Override
    public TransferData preprocessWaveformData(CssSeismogram seis, ResponseType toType, ResponseMetaData metadata)
                    throws IOException {

        /*
         * - Set up scratch space for TRANSFER subroutine. It needs four DOUBLE PRECISION arrays,two for the ffts that
         * must be sized to the next power of two above the maximum number of dat(nfft) and two that are half that size
         * plus 1(nfreq.)
         */
        int nfft = TransferFunctionUtils.next2(seis.getNsamp());
        int nfreq = nfft / 2 + 1;

        double[] xre = new double[nfreq];
        double[] xim = new double[nfreq];

        double delta = 1.0 / seis.getSamprate();
        double delfrq = 1.0 / (nfft * delta);

        /*
         * EXECUTION PHASE:
         */

        /*
         * - Deconvolve seismometer.
         */
        ResponseType fromType = metadata.getRsptype();
        float nmScale = getNmScale(fromType, toType);

        dseis(nfreq, delfrq, seis.getTime().getEpochTime(), xre, xim, fromType, metadata.getFilename(), seis.getSta(),
                        seis.getChan());

        /**
         * @param seis - passed in
         * @param toType - passed in
         * @param metadata - passed in
         * @param limits - passed in - not used until now!
         * 
         * @param nfreq - created above, used in dseis as a counter (in both calls)
         * 
         * @param sre - created above, but not used until now
         * @param sim - created above, but not used until now
         * 
         * @param xre - created above and changed in dseis (could be part of Complex?)
         * @param xim - created above and changed in dseis (could be part of Complex?)
         * 
         * @param delfrq - created above, used in getran as a constant (think we can recreate)
         * 
         * @param frequencies - created above and initilized; only used if !inverse (so far this appears never to happen)
         * @param nmScale - created above and initilized; only used if !inverse (so far this appears never to happen) needs both types to be calculated
         * @param inverse - passed in, not used until now!
         * @return
         * @throws IOException
         */
        
        // return:
        // nmScale
        // xre xim
        
        //stop passing in above:
        // limits
        Complex[] data = new Complex[nfreq];
        for(int i=0; i < nfreq; i++) {
            data[i] = new Complex(xre[i], xim[i]);
        }
        
        TransferData transferData = new TransferData(nmScale, data);
        return transferData;
       // return build(seis, toType, metadata, limits, inverse, transferData);
    }

  
    /**
     * @param seis
     * @param toType
     * @param metadata
     * @param limits
     * @param xre
     * @param xim
     * @param nmScale
     * @param inverse
     * @return
     * @throws IOException
     */
    @Override
    public CompositeTransferFunction buildTransferFunction(CssSeismogram seis, ResponseType toType, ResponseMetaData metadata,
                    FreqLimits limits, boolean inverse, TransferData transferData) throws IOException {
      
        int nfft = TransferFunctionUtils.next2(seis.getNsamp());
        int nfreq = nfft / 2 + 1;
        double[] sre = new double[nfft];
        double[] sim = new double[nfft];
        
        double delta = 1.0 / seis.getSamprate();
        double delfrq = 1.0 / (nfft * delta);
        double[] frequencies = new double[nfreq];
        for (int i = 0; i < nfreq; i++) {
            double freq = i * delfrq;
            frequencies[i] = freq;
        }
        
        double[] xre = new double[nfreq];
        double[] xim = new double[nfreq];
        Complex[] data = transferData.getWorkingData();
        for(int i=0; i < nfreq; i++) {
            xre[i] = data[i].getReal();
            xim[i] = data[i].getImaginary();
        }
        
        float nmScale = transferData.getNmScale();
        
        if (!inverse) {
            return new CompositeTransferFunction(xre, xim, 1.0, nmScale, frequencies);
        }
        
        /*
         * Attempt to determine whether data have been scaled or not and if transfer function is normalized or not.
         * Based on that information, the data may need to be scaled. This function must be called immediately after
         * getting the deconvolution transfer function before the real and imaginary arrays have been modified.
         */
        double multiplier = getNormalizationFactor(seis.getCalib(), seis.getCalper(), metadata);

        if (multiplier != 1) {
            normalizeTransferFunction(xre, xim, frequencies, seis.getCalper(), Math.signum(multiplier));
        }
        /*
         * compute 1 / Transfer function applying waterlevel of FLT_MIN
         */
        for (int i = 0; i < nfreq; ++i) {
            double denr = (Math.pow(xre[i], 2) + Math.pow(xim[i], 2));
            if (denr <= Float.MIN_NORMAL) {
                sre[i] = 0.0;
                sim[i] = 0.0;
            } else {
                denr = 1.0e0 / denr;
                sre[i] = xre[i] * denr;
                sim[i] = -xim[i] * denr;
            }
        }

        /*
         * - Determine seismometer transfer function in the 'TO' direction.
         */
        dseis(nfreq, delfrq, seis.getTime().getEpochTime(), xre, xim, toType, "none", "none", "none");

        /*
         * Multiply the two transfer functions together to get a composite transfer function...
         */
        for (int i = 0; i < nfreq; i++) {
            double temp = xre[i] * sre[i] - xim[i] * sim[i];
            xim[i] = xre[i] * sim[i] + xim[i] * sre[i];
            xre[i] = temp;
        }

        /*
         * Apply the taper to the composite transfer function...
         */
        for (int i = 0; i < nfreq; i++) {
            double freq = i * delfrq;
            double fac = delfrq * taper(freq, limits.getLowpass(), limits.getLowcut())
                            * taper(freq, limits.getHighpass(), limits.getHighcut());
            xre[i] *= fac;
            xim[i] *= fac;
        }
        return new CompositeTransferFunction(xre, xim, multiplier, nmScale, frequencies);
    }

    protected void dseis(int nfreq, double delfrq, double epoch, double[] xre, double[] xim, ResponseType type, String kf,
                    String inSta, String inChan) throws IOException {
        /*
         * Branching routine to apply the individual instrument responses.
         */
        for (int idx = 0; idx < nfreq; idx++) {
            xre[idx] = 1.0e0;
            xim[idx] = 0.0e0;
        }
        String sta = removeRegexEscape(inSta);
        switch (type) {
        case ACC:
            acc(delfrq, xre, xim);
            break;
        case SACPZF:
            polezero(delfrq, xre, xim, kf);
            break;
        case VEL:
            vel(delfrq, xre, xim);
            break;
        case DIS:
            break;

        default:
            throw new IllegalStateException("Unhandled type: " + type);
        }

    }

    private void acc(double delfrq, double[] xre, double[] xim) {
        int i, npole, nzero;
        Complex pole[] = new Complex[2];
        Complex zero[] = new Complex[2];

        /*
         * .....Acceleration Spectral Operator.....
         *
         *
         * .....Set poles and zeros.....
         *
         */
        float const_ = 1.0f;
        npole = 0;
        nzero = 2;

        for (i = 0; i < nzero; i++) {
            zero[i] = new Complex(0.0, 0.0);
        }

        /*
         * .....Compute transfer function.....
         *
         */
        getran(delfrq, const_, nzero, zero, npole, pole, xre, xim);
    }

    private void polezero(double delfrq, double xre[], double xim[], String poleZeroFile) throws IOException {
        final int MPOLES = 100;
        final int MZEROS = 100;

        double constant;
        Complex[] poles = new Complex[MPOLES];
        Complex[] zeros = new Complex[MZEROS];

        /*
         * - Set default values for constant, poles, and zeros.
         */
        constant = 1.0;
        for (int i = 0; i < MZEROS; i++) {
            zeros[i] = new Complex(0.0, 0.0);
        }
        for (int i = 0; i < MPOLES; i++) {
            poles[i] = new Complex(0.0, 0.0);
        }

        String[] lines = FileInputArrayLoader.fillStrings(poleZeroFile);

        // Get constant
        for (String line : lines) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            if (tokenizer.hasMoreTokens()) {
                if (tokenizer.nextToken().equalsIgnoreCase("CONSTANT")) {
                    constant = Double.parseDouble(tokenizer.nextToken());
                }
            }
        }

        // Get zeros
        int nzeros = 0;
        int zerosLeft = 0;
        for (String line : lines) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            if (tokenizer.hasMoreTokens()) {
                String first = tokenizer.nextToken();
                if (first.equalsIgnoreCase("ZEROS")) {
                    nzeros = Integer.parseInt(tokenizer.nextToken());
                    zerosLeft = nzeros;
                } else if (zerosLeft > 0) {
                    if (first.contains("POLES") || first.contains("CONSTANT")) {
                        break;
                    }

                    double real = Double.parseDouble(first);
                    double imaginary = Double.parseDouble(tokenizer.nextToken());
                    poles[nzeros - zerosLeft] = new Complex(real, imaginary);
                    zerosLeft--;
                }
            }
        }

        // Get poles
        int npoles = 0;
        int polesLeft = 0;
        for (String line : lines) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            if (tokenizer.hasMoreTokens()) {
                String first = tokenizer.nextToken();
                if (first.equalsIgnoreCase("POLES")) {
                    npoles = Integer.parseInt(tokenizer.nextToken());
                    polesLeft = npoles;
                } else if (polesLeft > 0) {
                    if (first.contains("ZEROS") || first.contains("CONSTANT")) {
                        break;
                    }

                    double real = Double.parseDouble(first);
                    double imaginary = Double.parseDouble(tokenizer.nextToken());
                    poles[npoles - polesLeft] = new Complex(real, imaginary);
                    polesLeft--;
                }
            }
        }

        getran(delfrq, constant, nzeros, zeros, npoles, poles, xre, xim);
    }

    private void vel(double delfrq, double[] xre, double[] xim) {
        int npole, nzero;
        Complex[] poles = new Complex[1];
        Complex[] zeros = new Complex[1];

        /*
         * .....VEL - velocity spectral operator.....
         *
         */
        /*
         * .....Set poles and zeros.....
         *
         */
        float const_ = 1.0f;
        nzero = 1;

        zeros[0] = new Complex(0.0, 0.0);

        npole = 0;

        /*
         * .....Compute transfer function.....
         *
         */
        getran(delfrq, const_, nzero, zeros, npole, poles, xre, xim);
    }

    private void getran(double delfrq, double const_, int nzero, Complex[] zero, int npole, Complex[] pole, double[] xre,
                    double[] xim) {
        int idx, jdx;
        double delomg, fac, omega, ti, ti0, tid, tin, tr, tr0, trd, trn;
        final double twopi = Math.PI * 2;
        /*
         * .....Subroutine to compute the transfer function.....
         *
         */

        int nfreq = xre.length;
        delomg = twopi * delfrq;
        for (jdx = 0; jdx < nfreq; jdx++) {
            omega = delomg * (jdx);
            trn = 1.0e0;
            tin = 0.0e0;
            if (nzero != 0) {
                for (idx = 0; idx < nzero; idx++) {
                    tr = -zero[idx].getReal();
                    ti = omega - zero[idx].getImaginary();
                    tr0 = trn * tr - tin * ti;
                    ti0 = trn * ti + tin * tr;
                    trn = tr0;
                    tin = ti0;
                }
            }
            trd = 1.0e0;
            tid = 0.0e0;
            if (npole != 0) {
                for (idx = 0; idx < npole; idx++) {
                    tr = -pole[idx].getReal();
                    ti = omega - pole[idx].getImaginary();
                    tr0 = trd * tr - tid * ti;
                    ti0 = trd * ti + tid * tr;
                    trd = tr0;
                    tid = ti0;
                }
            }

            fac = (const_) / (Math.pow(trd, 2) + Math.pow(tid, 2));
            xre[jdx] = fac * (trn * trd + tin * tid);
            xim[jdx] = fac * (trd * tin - trn * tid);
        }
    }

    private double taper(double freq, double fqh, double fql) {
        final double twopi = Math.PI * 2;

        /*
         * SUBROUTINE TO TAPER SPECTRA BY A COSINE
         *
         * CALLING ARGUMENTS:
         *
         * FREQ - FREQUENCY IN QUESTION FQH - FREQUENCY AT WHICH THERE IS A TRANSITION BETWEEN UNITY AND THE TAPER FQL -
         * FREQUENCY AT WHICH THERE IS A TRANSITION BETWEEN ZERO AND THE TAPER NOTE: IF FQL>FQH LO-PASS IF FQH>FQL
         * HI-PASS
         *
         */
        double dblepi = 0.5e0 * twopi;

        double taper_v = 0.0;
        if (fql > fqh) {
            /*
             * LO-PASS CASE
             *
             */
            if (freq < fqh) {
                taper_v = 1.0e0;
            }
            if (freq >= fqh && freq <= fql) {
                taper_v = 0.5e0 * (1.0e0 + Math.cos(dblepi * (freq - fqh) / (fql - fqh)));
            }
            if (freq > fql) {
                taper_v = 0.0e0;
            }
            return taper_v;
        }
        if (fqh > fql) {
            /*
             * HI-PASS CASE
             *
             */
            if (freq < fql) {
                taper_v = 0.0e0;
            }
            if (freq >= fql && freq <= fqh) {
                taper_v = 0.5e0 * (1.0e0 - Math.cos(dblepi * (freq - fql) / (fqh - fql)));
            }
            if (freq > fqh) {
                taper_v = 1.0e0;
            }
            return taper_v;
        }

        return taper_v;
    }

    private float getNmScale(final ResponseType fromType, final ResponseType toType) {
        float nmScale = 1.0f;
        if (fromType == ResponseType.EVRESP) {
            nmScale *= 1e09;
        }
        if (toType == ResponseType.EVRESP) {
            nmScale /= 1e09;
        }
        return nmScale;
    }

    private String removeRegexEscape(String tmpSta) {
        char oldChar = '\\';
        StringBuilder sb = new StringBuilder();
        for (char c : tmpSta.toCharArray()) {
            if (c != oldChar) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private void normalizeTransferFunction(double[] xre, double[] xim, double[] frequencies, Double calper, double sign) {
        double calFreq = calper != null ?  1 / calper : 1;
        int idx = -1;
        double minDiff = Double.MAX_VALUE;
        for (int j = 0; j < frequencies.length; ++j) {
            double freqDiff = Math.abs(calFreq - frequencies[j]);
            if (freqDiff < minDiff) {
                idx = j;
                minDiff = freqDiff;
            }
        }

        if (idx < 0) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Response normalization failed!");
            return;
        }
        double c = xre[idx];
        double d = xim[idx];
        double denom = Math.sqrt(c * c + d * d) * sign;
        for (int j = 0; j < xre.length; ++j) {
            xre[j] /= denom;
            xim[j] /= denom;
        }
    }

    @Override
    public FreqLimits determineFreqLimitsFromTransferFunction(CssSeismogram seis, ResponseMetaData rmd) throws IOException {
        int nfreqs = 10000;
        double nyquist = seis.getSamprate() / 2;
        double delfrq = nyquist / (nfreqs - 1);
        double val = 0;
        double[] freqs = new double[nfreqs];
        for (int i = 0; i < nfreqs; i++) {
            freqs[i] = val;
            val += delfrq;
        }
        double[] xre = new double[nfreqs];
        double[] xim = new double[nfreqs];
        dseis(nfreqs, delfrq, -1.0, xre, xim, rmd.getRsptype(), rmd.getFilename(), "-", "-");
        double[] amplitude = new double[nfreqs];
        for (int j = 0; j < nfreqs; ++j) {
            double tmp = xre[j] * xre[j] + xim[j] * xim[j];
            amplitude[j] = Math.sqrt(tmp);
        }
        return TransferParams.getInstance().produceFromTransferFunction(freqs, amplitude);
    }

    private enum NormalizationStatus {

        Normalized, UnNormalized, Unknown
    }

    private double getNormalizationFactor(Double wcalib, Double wcalper, ResponseMetaData metadata) {

        NormalizationStatus normStatus;

        double dataMultiplier = 1.0;
        double effectiveCalib;

        wcalper = CompareCalperValues(wcalper, metadata.getNominalCalper(), metadata.getSensorCalper());

        /*
         * Now determine whether response has been normalized or not...
         */
        if (wcalper <= 0 || wcalib == null) {
            normStatus = NormalizationStatus.Unknown;

            String msg = String.format(
                            "Calper is not available. Cannot tell if response is normalized, so transfer function will be used without scaling.");
            ApplicationLogger.getInstance().log(Level.FINEST, msg);
        } else if (wcalib != 0 && wcalib != 1) {
            normStatus = NormalizationStatus.Normalized;
        } else {
            normStatus = NormalizationStatus.UnNormalized;
        }

        effectiveCalib = getEffectiveCalib(wcalib, metadata.getNominalCalib(), metadata.getSensorCalratio(),
                        metadata.getRsptype());

        if (normStatus == NormalizationStatus.Normalized) {
            dataMultiplier = effectiveCalib;
        }

        String msg = String.format("Waveform multiplied by %f after deconvolution.", dataMultiplier);
        ApplicationLogger.getInstance().log(Level.FINEST, msg);

        return dataMultiplier;
    }

    private double CompareCalperValues(Double WfdiscCalper, Double ncalper, Double SensorCalper) {
        double tol = 0.001;
        double undef = -999.0;

        if (isUnset(WfdiscCalper) && isUnset(ncalper) && isUnset(SensorCalper)) {
            return undef;
        }

        if (isUnset(WfdiscCalper) && !isUnset(ncalper)) {

            String msg = String.format("Wfdisc Calper not set. Setting Wfdisc Calper to %f to match Nominal Calper.", ncalper);
            ApplicationLogger.getInstance().log(Level.FINEST, msg);

            WfdiscCalper = ncalper;
        }

        if (isUnset(WfdiscCalper) && !isUnset(SensorCalper)) {
            String msg = String.format("Wfdisc Calper not set. Setting Wfdisc Calper to %f to match Sensor Calper.\n",
                            SensorCalper);

            ApplicationLogger.getInstance().log(Level.FINEST, msg);
            WfdiscCalper = SensorCalper;
        }

        if (isUnset(ncalper) && !isUnset(WfdiscCalper)) {

            String msg = String.format("Nominal Calper not set. Setting Nominal " + "Calper to %f to match Wfdisc Calper.",
                            ncalper);
            ApplicationLogger.getInstance().log(Level.FINEST, msg);
            ncalper = WfdiscCalper;
        }

        if (isUnset(SensorCalper) && !isUnset(WfdiscCalper)) {
            String msg = String.format("Sensor Calper not set. Setting Sensor " + "Calper to %f to match Wfdisc Calper.",
                            WfdiscCalper);
            ApplicationLogger.getInstance().log(Level.FINEST, msg);
            SensorCalper = WfdiscCalper;
        }

        /*
         * Now all calper values should be different from undef. If they differ, then the problem cannot be resolved.
         */
        if (Math.abs(WfdiscCalper - ncalper) > tol) {
            String msg = String.format("Wfdisc Calper differs from Instrument Calper. "
                            + "Values are: Wfdisc Calper = %f, Instrument Calper = %f, " + "Un-resolvable problem.", WfdiscCalper,
                            ncalper);
            ApplicationLogger.getInstance().log(Level.FINEST, msg);
            return undef;
        }
        if (Math.abs(WfdiscCalper - SensorCalper) > tol) {
            String msg = String.format("Wfdisc Calper differs from Sensor Calper. "
                            + "Values are: Wfdisc Calper = %f, Sensor Calper = %f, " + "Un-resolvable problem.", WfdiscCalper,
                            SensorCalper);
            ApplicationLogger.getInstance().log(Level.FINEST, msg);
            return undef;
        }
        if (Math.abs(ncalper - SensorCalper) > tol) {
            String msg = String.format(
                            "Nominal Calper differs from Sensor Calper. "
                                            + "Values are: Nominal Calper = %f, Sensor Calper = %f, " + "Un-resolvable problem.",
                            ncalper, SensorCalper);
            ApplicationLogger.getInstance().log(Level.FINEST, msg);
            return undef;
        }

        /*
         * They are all the same, so return any one of them...
         */
        return ncalper;
    }

    private double getEffectiveCalib(Double calib, Double ncalib, Double calratio, ResponseType fromType) {
        double EffectiveCalib = 1.0;
        if (fromType.isNDCType()) {
            if (isMeaningfulValue(calib)) {
                EffectiveCalib *= calib;
            }
        }

        return EffectiveCalib;
    }

    private boolean isMeaningfulValue(Double value) {
        double tol = 0.001;
        return value != null && Math.abs(value + 999) > tol;
    }

    private boolean isUnset(Double value) {
        return value == null || Math.abs(value + 999.0) < 0.001;
    }

}
