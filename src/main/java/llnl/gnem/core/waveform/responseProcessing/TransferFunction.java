package llnl.gnem.core.waveform.responseProcessing;

public interface TransferFunction {

   
    /**
     * @return amplitude array values
     */
    public double[] getAmplitude() ;

    /**
     * @return phase for each amplitude value
     */
    public double[] getPhase();

    /**
     * @return frequencies
     */
    public double[] getFrequencies();
}
