package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.database.FeatureDAO;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ClassifyDetectionWorker extends SwingWorker<Void, Void> {

    private final int detectorid;
    private final String status;

    public ClassifyDetectionWorker(int detectorid, String status) {
        this.detectorid = detectorid;
        this.status = status;
    }

    @Override
    protected Void doInBackground() throws Exception {
        FeatureDAO.getInstance().writeDetectorTrainingDataRow(detectorid, status);
        return null;

    }

    @Override
    public void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error classifying detections.", e);
            }
        }
    }
}
