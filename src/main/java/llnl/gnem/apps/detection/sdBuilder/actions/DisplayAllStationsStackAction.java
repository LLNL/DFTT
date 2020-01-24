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
