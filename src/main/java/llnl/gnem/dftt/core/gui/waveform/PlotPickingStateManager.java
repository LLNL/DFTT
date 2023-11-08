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

import java.util.logging.Level;
import llnl.gnem.dftt.core.seismicData.AbstractEventInfo;
import llnl.gnem.dftt.core.gui.map.events.EventModel;
import llnl.gnem.dftt.core.gui.plotting.JPlotContainer;
import llnl.gnem.dftt.core.gui.plotting.PickCreationInfo;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.Command;
import llnl.gnem.dftt.core.waveform.components.BaseSingleComponent;
import llnl.gnem.dftt.core.gui.waveform.factory.commands.CreateArrivalCommand;
import llnl.gnem.dftt.core.gui.waveform.phaseVisibility.UsablePhaseManager;

/**
 *
 * @author dodge1
 */
public class PlotPickingStateManager extends BasePickingStateManager {

    public PlotPickingStateManager(UsablePhaseManager apMgr, String user) {
        super(new JMultiAxisPlot(), apMgr, user);
    }

    @Override
    protected void maybeCreatePick(String phase, PickCreationInfo pci) {
        JSubplot subplot = pci.getOwningPlot();
        JPlotContainer plot = subplot.getOwner();
        if (plot instanceof ComponentSetPlot) {
            ComponentSetPlot csp = (ComponentSetPlot) plot;
            if (csp.getSet().canCreatePicks()) {
                phase = csp.getSet().maybeRemapPhase(phase);
                DisplayArrival existing = csp.getExistingPick(phase);
                if (existing != null) {
                } else {
                    BaseSingleComponent component = csp.getSingleComponent(subplot);
                    if (component != null && component.isPickAllowable()) {
                        EventModel<? extends AbstractEventInfo> eventModel = csp.getDataModel().getEventModel();
                        double refTime = eventModel.getCurrent().getTime().getEpochTime();
                        ApplicationLogger.getInstance().log(Level.FINE, String.format("PlotPickingStateManager::maybeCreatePick-->Creating arrival command for component(%s)  with relTime = %f...",
                                component, pci.getCoordinate().getWorldC1()));
                        Command cmd = new CreateArrivalCommand(csp.getSet(), component, pci.getCoordinate().getWorldC1(), refTime, phase, getUser());
                        csp.invoke(cmd);
                    }
                }
            }
        }
    }
}
