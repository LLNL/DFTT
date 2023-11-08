/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.dftt.core.gui.waveform;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import llnl.gnem.dftt.core.gui.util.ProgressDialog;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.waveform.components.BaseSingleComponent;
import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.dftt.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.dftt.core.waveform.responseProcessing.WaveformDataUnits;
import llnl.gnem.dftt.core.waveform.responseProcessing.ResponseType;
import llnl.gnem.dftt.core.waveform.responseProcessing.TransferFunctionService;
import llnl.gnem.dftt.core.waveform.responseProcessing.TransferStatus;

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
