package llnl.gnem.core.waveform.responseProcessing.spi;

import org.apache.commons.math3.complex.Complex;

public class TransferData {

    final float nmScale;
    final Complex[] workingData;
    
    public TransferData(float nmScale, Complex[] workingData) {
        this.nmScale = nmScale;
        this.workingData = workingData;
    }
    
    public float getNmScale() {
        return nmScale;
    }

    public Complex[] getWorkingData() {
        return workingData;
    }

    
    
}
