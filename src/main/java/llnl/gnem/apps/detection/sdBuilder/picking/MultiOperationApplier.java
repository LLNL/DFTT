/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2023 Lawrence Livermore National Laboratory (LLNL)
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
package llnl.gnem.apps.detection.sdBuilder.picking;

import java.util.ArrayList;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ComputeCorrelationsWorker;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.dftt.core.correlation.CorrelationComponent;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;

public class MultiOperationApplier {

    private boolean inProgress = false;

    private MultiOperationApplier() {
    }

    public static MultiOperationApplier getInstance() {
        return MultiOperationApplierHolder.INSTANCE;
    }

    private static class MultiOperationApplierHolder {

        private static final MultiOperationApplier INSTANCE = new MultiOperationApplier();
    }

    public void performOperations(double xValue) {
        if (inProgress) {
            return;
        }
        double windowLength = ParameterModel.getInstance().getSNRWinLength();
        double start = xValue - windowLength / 2;
        double padding = windowLength/4;
        ParameterModel.getInstance().setWindowStart(start);
        ParameterModel.getInstance().setCorrelationWindowLength(windowLength);
        // Get width of theSNR calc window
        // Get reference to the correlation window
        // Set correlation window start to x-position - 1/2 SNR window width
        //Set correlation Window width = width of SNR window.
        ClusterBuilderFrame.getInstance().setCorrelationWindowStart(start);
        ClusterBuilderFrame.getInstance().setCorrelationWindowLength(windowLength);
        // Filter all data
         StoredFilter filter = ClusterBuilderFrame.getInstance().getCurrentFilter();
        if(filter != null){
            ArrayList<CorrelationComponent>  compList = CorrelatedTracesModel.getInstance().getAllComponents();
            for(CorrelationComponent cc :compList){
                cc.applyFilter(filter);
            }
        }
        
        
        
        
        //Correlate data
        ComputeCorrelationsWorker worker = new ComputeCorrelationsWorker(ParameterModel.getInstance().isFixShiftsToZero(), start-padding, start + windowLength+padding, true);
        worker.execute();

        inProgress = false;
    }
}
