package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.database.DetectorDAO;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ReplaceTemplateWorker extends SwingWorker<Void, Void> {

    private final int targetDetectorid;
           private final  int sourceDetectorid;
           private final  double templateOffset;
           private final  double templateDuration;

    public ReplaceTemplateWorker(int targetDetectorid,
            int sourceDetectorid,
            double templateOffset,
            double templateDuration) {
        this.sourceDetectorid = sourceDetectorid;
        this.targetDetectorid = targetDetectorid;
        this.templateOffset = templateOffset;
        this.templateDuration = templateDuration;
    }

    @Override
    protected Void doInBackground() throws Exception {

        DetectorDAO.getInstance().replaceSubspaceTemplate(targetDetectorid, sourceDetectorid, templateOffset, templateDuration);
        return null;

    }

    @Override
    public void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error swapping templates.", e);
            }
        }
    }
}
