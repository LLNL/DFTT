package llnl.gnem.apps.detection.core.signalProcessing;

import java.util.Arrays;

/**
 * Author:  Dave Harris
 * Lawrence Livermore National Laboratory
 * Created: Feb 19, 2008
 * Time: 3:20:05 PM
 * Last Modified: Feb 19, 2008
 */

public class OverlapAdd {

    private float[] kernel;
    private float[] shiftRegister;
    private int kernelLength;
    private int N;
    private int log2N;
    private RFFT fft;

    private int dataBlockLength;
    private float[] dataDFT;

    private float[] product;

    private OverlapAdd master;

    // master constructor

    public OverlapAdd(float[] kernel, int dataBlockSize)
    {

        master = null;

        dataBlockLength = dataBlockSize;
        kernelLength = kernel.length;

        // calculate fft size

        log2N = 0;
        N = 1;
        while (N < kernelLength + dataBlockLength - 1) {
            log2N++;
            N *= 2;
        }

        // instantiate fft

        fft = new RFFT(log2N);

        // pad, reverse and transform kernel

        this.kernel = Sequence.pad(kernel, N);
//    Sequence.reverse( this.kernel );
        fft.dft(this.kernel);

        // allocate shift register and workspace

        shiftRegister = new float[N];
        product = new float[N];
    }

    // slave constructor

    public OverlapAdd(float[] kernel, OverlapAdd master)
    {

        this.master = master;

        dataBlockLength = master.dataBlockLength;
        kernelLength = kernel.length;

        // use the master fft resource

        log2N = master.log2N;
        N = master.N;
        fft = master.fft;

        // pad, reverse and transform kernel

        this.kernel = Sequence.pad(kernel, N);
//    Sequence.reverse( this.kernel );
        fft.dft(this.kernel);

        // allocate shift register and share workspace

        shiftRegister = new float[N];
        product = master.product;
    }

    //

    public void initialize()
    {
        initialize(0.0f);
    }


    public void initialize(float value)
    {
        Arrays.fill(shiftRegister, value);
    }

    // master filtering operation

    public void filter(float[] dataBlock, float[] result, int offset)
    {

        dataDFT = Sequence.pad(dataBlock, N);
        fft.dft(dataDFT);

        fft.dftproduct(dataDFT, kernel, product, 1.0f);
        fft.idft(product);

        Sequence.zshift(shiftRegister, -dataBlockLength);

        for (int i = 0; i < dataBlockLength + kernelLength; i++) shiftRegister[i] += product[i];         // overlap add
        System.arraycopy(shiftRegister, 0, result, offset, dataBlockLength);

    }

    // slave filtering operation

    public void filter(float[] result, int offset)
    {

        fft.dftproduct(master.dataDFT, kernel, product, 1.0f);
        fft.idft(product);

        Sequence.zshift(shiftRegister, -dataBlockLength);

        for (int i = 0; i < dataBlockLength + kernelLength; i++) shiftRegister[i] += product[i];         // overlap add
        System.arraycopy(shiftRegister, 0, result, offset, dataBlockLength);

    }

}
