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
package llnl.gnem.apps.detection.sdBuilder.arrayDisplay;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.ArrayDisplayModel.FKInputData;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.fkDisplay.SingleEventFKFrame;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.fkDisplay.SingleEventFKModel;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.core.signalprocessing.arrayProcessing.FKProducer;
import llnl.gnem.core.signalprocessing.arrayProcessing.FKResult;

/**
 *
 * @author dodge1
 */
public class MakeFKMeasurementWorker extends SwingWorker<Void, Void> {

    private FKResult result;

    public MakeFKMeasurementWorker() {
    }

    @Override
    protected Void doInBackground() throws Exception {

        float smax = (float) ParameterModel.getInstance().getFKMaxSlowness();
        int numSlowness = ParameterModel.getInstance().getFKNumSlowness();
        double minFrequency = ParameterModel.getInstance().getFKMinFrequency();
        double maxFrequency = ParameterModel.getInstance().getFKMaxFrequency();
        FKInputData data = ArrayDisplayModel.getInstance().getFKInputData();
       result = new FKProducer().produce(smax, numSlowness, data.getXnorth(), data.getXeast(), data.getWaveforms(), data.getDelta(), (float) minFrequency, (float) maxFrequency);

        return null;
    }

    @Override
    public void done() {

        try {
            get();
            SingleEventFKFrame.getInstance().setVisible(true);
            SingleEventFKModel.getInstance().addFKResult(result);

        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(MakeFKMeasurementWorker.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

}
