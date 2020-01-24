package llnl.gnem.core.gui.waveform;

import java.util.logging.Level;
import llnl.gnem.core.seismicData.AbstractEventInfo;
import llnl.gnem.core.gui.map.events.EventModel;
import llnl.gnem.core.gui.plotting.JPlotContainer;
import llnl.gnem.core.gui.plotting.PickCreationInfo;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Command;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.gui.waveform.factory.commands.CreateArrivalCommand;
import llnl.gnem.core.gui.waveform.phaseVisibility.UsablePhaseManager;

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
