/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.responseProcessing;

/**
 *
 * @author dodge1
 */
public interface InstrumentResponse {
    TransferFunction computeTransferFunction(double[] frequencies, Units units);
}
