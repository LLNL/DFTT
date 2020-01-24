package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.AllStationsFrame;
import llnl.gnem.apps.detection.sdBuilder.allStations.GetStationTimeDataWorker;
import llnl.gnem.apps.detection.sdBuilder.allStations.SeismogramModel;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.MultiStationStackModel;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.util.Epoch;

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
