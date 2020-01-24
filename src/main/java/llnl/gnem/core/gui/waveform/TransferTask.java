/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataUnits;
import llnl.gnem.core.waveform.responseProcessing.ResponseType;
import llnl.gnem.core.waveform.responseProcessing.TransferFunctionService;
import llnl.gnem.core.waveform.responseProcessing.TransferStatus;

/**
 *
 * @author dodge1
 */
public class TransferTask implements Callable<BaseSingleComponent> {

    private final BaseSingleComponent component;
    private final ResponseType toType;
    private static final double NANOMETER_TO_METER = 1.0e-9;

    public TransferTask(BaseSingleComponent component, ResponseType toType) {
        this.component = component;
        this.toType = toType;
    }

    @Override
    public BaseSingleComponent call() throws Exception {
        component.setTransferStatus(TransferStatus.UNTRANSFERRED);
        CssSeismogram backup = component.getTraceData().getBackupSeismogram();

        boolean result = TransferFunctionService.getInstance().transfer(backup, toType);
        component.setTransferStatus(result ? TransferStatus.TRANSFERRED : TransferStatus.TRANSFER_FAILED);
        backup.MultiplyScalar(NANOMETER_TO_METER);
        if (result) {
            component.setDataUnits(WaveformDataUnits.m);
            component.setDataType(WaveformDataType.displacement);
            component.getTraceData().unApplyFilter();
        }
        return component;
    }
}
