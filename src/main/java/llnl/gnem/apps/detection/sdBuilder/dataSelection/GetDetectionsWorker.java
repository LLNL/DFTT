package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import llnl.gnem.apps.detection.core.dataObjects.Detection;
import llnl.gnem.apps.detection.database.DetectionDAO;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class GetDetectionsWorker extends SwingWorker<Void, Void> {

    private final Collection<ClassifiedDetection> result;
    private final int runid;
    private final int detectorid;
    private final DefaultMutableTreeNode targetNode;

    public GetDetectionsWorker(int runid, int detectorid, DefaultMutableTreeNode node)
    {
        result = new ArrayList<>();
        this.runid = runid;
        this.detectorid = detectorid;
        targetNode = node;
    }

    @Override
    protected Void doInBackground() throws Exception {
       
        result.addAll(DetectionDAO.getInstance().getDetections(runid, detectorid));
        return null;

    }

    @Override
    public void done() {
        try {
            get();
            ConfigDataModel.getInstance().setDetections(result, targetNode);
            CorrelatedTracesModel.getInstance().setTotalDetectionCount(result.size());
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error retrieving detections collection.", e);
            }
        }
    }
}