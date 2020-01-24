package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import llnl.gnem.apps.detection.core.dataObjects.Detection;
import llnl.gnem.apps.detection.database.DetectionDAO;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DeleteDetectionWorker extends SwingWorker<Void, Void> {

    private final Detection detection;
    private final DefaultMutableTreeNode node;

    public DeleteDetectionWorker(Detection detection, DefaultMutableTreeNode node)
    {
        this.detection = detection;
        this.node = node;
    }

    @Override
    protected Void doInBackground() throws Exception {
       
       DetectionDAO.getInstance().deleteDetection(detection);
        return null;

    }

    @Override
    public void done() {
        try {
            get();
            ConfigDataModel.getInstance().removeNode(node);
        } catch (Exception e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error removing detector.", e);
            }
        }
    }
}