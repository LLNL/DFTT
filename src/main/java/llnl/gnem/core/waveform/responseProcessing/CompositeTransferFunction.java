/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.responseProcessing;

import org.apache.commons.math3.complex.Complex;

/**
 *
 * @author dodge1
 */
public class CompositeTransferFunction {

    private final double[] xre;
    private final double[] xim;
    private final double dataMultiplier;
    private final float nmScale;
    private final double[] frequencies;

    public CompositeTransferFunction(double[] xre, double[] xim, double multiplier, float scale, double[] frequencies) {
        this.xre = xre.clone();
        this.xim = xim.clone();
        dataMultiplier = multiplier;
        this.nmScale = scale;
        this.frequencies = frequencies.clone();
    }

    public Complex[] getValues() {
        Complex[] result = new Complex[xre.length];
        for (int j = 0; j < xre.length; ++j) {
            result[j] = new Complex(xre[j], xim[j]);
        }
        return result;
    }

    /**
     * @return the xre
     */
    public double[] getXre() {
        return xre.clone();
    }

    /**
     * @return the xim
     */
    public double[] getXim() {
        return xim.clone();
    }

    /**
     * @return the dataMultiplier
     */
    public double getDataMultiplier() {
        return dataMultiplier;
    }

    /**
     * @return the nmScale
     */
    public float getNmScale() {
        return nmScale;
    }

    public double[] getFrequencies() {
        return frequencies.clone();
    }
}
