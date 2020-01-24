package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.ArrayList;
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
public class DeleteDetectionsWorker extends SwingWorker<Void, Void> {

    private final ArrayList<Integer> detectionIdValues;

    public DeleteDetectionsWorker(ArrayList<Integer> detectionIdValues)
    {
        this.detectionIdValues = new ArrayList<>(detectionIdValues);
    }

    @Override
    protected Void doInBackground() throws Exception {
       
       DetectionDAO.getInstance().deleteDetections(detectionIdValues);
        return null;

    }

    @Override
    public void done() {
        try {
            get();
         } catch (Exception e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error removing detector.", e);
            }
        }
    }
}