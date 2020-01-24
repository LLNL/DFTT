/*
 * FIRfilter.java
 *
 * Created on August 22, 2002, 7:10 PM
 * Modified:  December 30, 2003
 *   modification to use double precision arithmetic in the convolution calculation  12/30/03
 * Last modified: July 2, 2004
 * 	 modification to eliminate the slave RealFIRfilters.
 */

package llnl.gnem.core.signalprocessing.extended;

/**
 * The RealFIRfilter represents an FIRfilter that takes in a Sequence
 * representing multiplexed data and convolves it with another Sequence
 * representing the template derived from the subspace designer.
 * <p></p>
 * All FIRfilters multiply the FFT of the data with the FFT of the
 * template, then alias the data by a factor equal to the number of data
 * channels. Then the inverse FFT is taken and an overlap add function is
 * performed. The remaining Sequence is then outputted.
 * <p></p>
 * Much of these calculations require double precision. After the double
 * precision is not necessary, the DoubleSequences are reduced to
 * Sequences.
 *
 * @author Dave Harris, adapted by Tim Paik
 */
public class RealFIRfilter {

    protected FFTdp fft;
    protected RealDoubleSequence kernel;          // Sequence containing multiplexed convolution kernel
    protected RealDoubleSequence shiftRegister;
    protected int numChannels;
    protected int dataLength;
    protected int kernelLength;
    protected FIRfilter master;
    protected RealDoubleSequence dataDFT;

    /**
     * Creates a new master FIRfilter.
     *
     * @param _kernel      - The template from the subspace designer.
     * @param _numChannels - The number of data channels from a data stream.
     * @param _dataLength  - The length of the ComplexSequences that this
     *                     ComplexFIRfilter can take in as data.
     */
    public RealFIRfilter(RealSequence _kernel, int _numChannels, int _dataLength) {
        master = null;
        kernel = new RealDoubleSequence(_kernel);
        numChannels = _numChannels;
        dataLength = _dataLength;
        kernelLength = kernel.length() / numChannels;

        // calculate fft size
        int M = 1;
        int m = 0;
        int R = numChannels;
        int resultLength = dataLength + kernelLength - 1;
        while (M < resultLength) {
            M *= 2;
            m++;
        }
        while ((R / 2) * 2 == R) {
            R /= 2;
            m++;
        }
        fft = new FFTdp(m, R);

        // transform kernel
        kernel.reverse();
        kernel.pad_to(fft.fftsize());
        kernel.cshift(1 - numChannels);
        kernel.dftRX(fft);

        // create shift register for overlap add implementation
        shiftRegister = new RealDoubleSequence(fft.fftsize() / numChannels);

        // initialize dataDFT to null
        dataDFT = null;
    }


    /**
     * Re-initializes this RealFIRfilter.
     */
    public void init() {
        shiftRegister.setConstant(0.0);
    }

    /**
     * Used by RealFIRfilters to filter the multiplexed data from the
     * preprocessing.
     *
     * @param dataSegment - the ComplexSequence representing the multiplexed data
     * @return the Sequence representing the filtered data.
     */
    public RealSequence filter(RealSequence dataSegment) {
        RealDoubleSequence tmp;

        dataDFT = new RealDoubleSequence(dataSegment);
        dataDFT.pad_to(fft.fftsize());
        dataDFT.dftRX(fft);

        tmp = RealDoubleSequence.dftprod(dataDFT, kernel, 1.0);
        tmp.dftAlias(numChannels);
        tmp.idft();
        shiftRegister.zshift(-dataLength);
        double[] s = shiftRegister.getArray();
        double[] t = tmp.getArray();
        for (int i = 0; i < dataLength + kernelLength; i++) s[i] += t[i];         // overlap add
        float[] retval = new float[dataLength];
        for (int i = 0; i < dataLength; i++) retval[i] = (float) s[i];

        return new RealSequence(retval);
    }
}
