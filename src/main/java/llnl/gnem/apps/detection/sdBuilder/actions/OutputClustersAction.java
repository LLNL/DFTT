package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.apps.detection.sdBuilder.WriteClustersWorker;
import llnl.gnem.core.correlation.clustering.GroupData;
import llnl.gnem.core.gui.util.Utility;


public class OutputClustersAction extends AbstractAction {

    private static OutputClustersAction ourInstance;
   

    public static OutputClustersAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new OutputClustersAction(owner);
        }
        return ourInstance;
    }

    private OutputClustersAction(Object owner) {
        super("Template", Utility.getIcon(owner, "miscIcons/add2db32.gif"));
        putValue(SHORT_DESCRIPTION, "Write clusters to database.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_T);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Collection<GroupData>groups = CorrelatedTracesModel.getInstance().getGroups();
        int detectorid = CorrelatedTracesModel.getInstance().getCurrentDetectorid();
        int runid = CorrelatedTracesModel.getInstance().getRunid();
        new WriteClustersWorker(groups,  detectorid,  runid).execute();

    }

}
