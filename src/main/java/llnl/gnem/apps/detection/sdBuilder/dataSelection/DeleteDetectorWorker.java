package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import llnl.gnem.apps.detection.database.DetectorDAO;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DeleteDetectorWorker extends SwingWorker<Void, Void> {

    private final int detectorid;
    private final DefaultMutableTreeNode node;

    public DeleteDetectorWorker(int detectorid, DefaultMutableTreeNode node) {
        this.detectorid = detectorid;
        this.node = node;
    }

    @Override
    protected Void doInBackground() throws Exception {

        DetectorDAO.getInstance().deleteDetector(detectorid);
        return null;

    }

    @Override
    public void done() {
        try {
            get();
            if (node != null) {
                ConfigDataModel.getInstance().removeNode(node);
                CorrelatedTracesModel.getInstance().clear();
            }
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error removing detector.", e);
            }
        }
    }
}
