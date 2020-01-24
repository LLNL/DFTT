package llnl.gnem.apps.detection.core.framework.detectors.subspace;

import java.io.Serializable;
import llnl.gnem.apps.detection.core.signalProcessing.RFFTdp;
import llnl.gnem.core.signalprocessing.Sequence;

/**
 * Author: Dave Harris Lawrence Livermore National Laboratory Created: April 5,
 * 2009 Time: 10:10:00 AM Last Modified: November 30, 2011 by DBH/DSP LLC for
 * NORSAR BAA11
 */
public class SingleChannelCorrelator implements Serializable {

    private static final long serialVersionUID = 6980429739673996767L;

    private final double[] kernel;
    private final double[] shiftRegister;
    private final int kernelLength;
    private final RFFTdp fft;

    private final int dataBlockLength;

    private final double[] product;

    // slave constructor
    public SingleChannelCorrelator(float[] kernel, int dataBlockLength, RFFTdp fft) {

        this.dataBlockLength = dataBlockLength;
        kernelLength = kernel.length;

        // use the fft resource passed from MultichannelCorrelator
        this.fft = fft;
        int N = fft.fftsize();

        // pad and transform kernel
        this.kernel = new double[N];
        for (int i = 0; i < kernelLength; i++) {
            this.kernel[i] = kernel[kernelLength - 1 - i];   // TODO:  Check
        }

        fft.dft(this.kernel);

        // allocate shift register and workspace
        shiftRegister = new double[N];
        product = new double[N];
    }

    public void initialize() {
        Sequence.zero(shiftRegister);
    }

    // filtering operation
    public void filter(double[] dataDFT, double[] result) {

        fft.dftproduct(dataDFT, kernel, product, 1.0);
        fft.idft(product);

        Sequence.zshift(shiftRegister, -dataBlockLength);

        for (int i = 0; i < dataBlockLength + kernelLength; i++) {            // overlap add
            shiftRegister[i] += product[i];
        }
        System.arraycopy(shiftRegister, 0, result, 0, dataBlockLength);
    }

    public void flush(double[] result, int offset) {
        Sequence.zshift(shiftRegister, -dataBlockLength);
        System.arraycopy(shiftRegister, 0, result, offset, kernelLength);
    }

}
