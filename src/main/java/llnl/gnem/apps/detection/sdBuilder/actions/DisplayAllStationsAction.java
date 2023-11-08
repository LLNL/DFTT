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
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.AllStationsFrame;
import llnl.gnem.apps.detection.sdBuilder.allStations.GetStationTimeDataWorker;
import llnl.gnem.apps.detection.sdBuilder.allStations.SeismogramModel;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;

import llnl.gnem.dftt.core.correlation.CorrelationComponent;
import llnl.gnem.dftt.core.gui.util.Utility;
import llnl.gnem.dftt.core.util.Epoch;

public class DisplayAllStationsAction extends AbstractAction {

    private static DisplayAllStationsAction ourInstance;
    private static final long serialVersionUID = 7149317947204406136L;
    private CorrelationComponent selectedComponent;

    public static DisplayAllStationsAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new DisplayAllStationsAction(owner);
        }
        return ourInstance;
    }

    private DisplayAllStationsAction(Object owner) {
        super("Display", Utility.getIcon(owner, "miscIcons/theorStack.gif"));
        putValue(SHORT_DESCRIPTION, "Display All stations for selected time");
        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SeismogramModel.getInstance().clear();
        if (selectedComponent != null) {
            long detectionid = selectedComponent.getEvent().getEvid();
            double duration = ParameterModel.getInstance().getTraceLength();
            double prepickSeconds = ParameterModel.getInstance().getPrepickSeconds();
            double detectionTime = selectedComponent.getNominalPick().getTime();
            Epoch epoch = new Epoch(detectionTime - prepickSeconds, detectionTime - prepickSeconds + duration);

            new GetStationTimeDataWorker(detectionid, epoch).execute();
            AllStationsFrame.getInstance().setVisible(true);
        }
    }

    public void setComponent(CorrelationComponent cc) {
        selectedComponent = cc;
    }
}
