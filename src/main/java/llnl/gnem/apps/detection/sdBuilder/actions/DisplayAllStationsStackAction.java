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
package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.BuildMultiStationStackWorker;
import llnl.gnem.apps.detection.sdBuilder.allStations.GetStationTimeDataWorker;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.MultiStationStackFrame;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.MultiStationStackModel;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.util.Epoch;

public class DisplayAllStationsStackAction extends AbstractAction {

    private static DisplayAllStationsStackAction ourInstance;

    public static DisplayAllStationsStackAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new DisplayAllStationsStackAction(owner);
        }
        return ourInstance;
    }

    private DisplayAllStationsStackAction(Object owner) {
        super("Stack", Utility.getIcon(owner, "miscIcons/theorStack.gif"));
        putValue(SHORT_DESCRIPTION, "Display All station stack for current detections");
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MultiStationStackModel.getInstance().clear();
        MultiStationStackFrame.getInstance().setVisible(true);
        Collection<CorrelationComponent> currentData = CorrelatedTracesModel.getInstance().getMatchingTraces();
        if (currentData.size() > 0) {
            Collection<Epoch> epochs = new ArrayList<>();
            long detectionid = -1;
            for (CorrelationComponent cc : currentData) {
                if (detectionid < 1) {
                    // Need one detectionid to get the station group
                    detectionid = cc.getEvent().getEvid();
                }
                double duration = ParameterModel.getInstance().getTraceLength();
                double prepickSeconds = ParameterModel.getInstance().getPrepickSeconds();
                double detectionTime = cc.getNominalPick().getTime();
                epochs.add(new Epoch(detectionTime - prepickSeconds - 20, detectionTime - prepickSeconds + duration + 20));
            }
            new BuildMultiStationStackWorker(detectionid, epochs).execute();
        }

    }

}
