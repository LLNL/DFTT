/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay;

import java.awt.Cursor;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.core.dataObjects.TriggerDataFeatures;
import llnl.gnem.apps.detection.database.DetectionDAO;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class TriggerStatisticsRetrievalWorker extends SwingWorker<Void, Void> {

    private final int detectionid;
    private TriggerDataFeatures result;
    
    public TriggerStatisticsRetrievalWorker(int detectionid) {
        this.detectionid = detectionid;
        ProgressDialog.getInstance().setReferenceFrame(SingleDetectionDisplayFrame.getInstance());
        ProgressDialog.getInstance().setProgressBarIndeterminate(true);
        ProgressDialog.getInstance().setText("retrieving statistics");
        ProgressDialog.getInstance().setVisible(true);
    }
    
    @Override
    protected Void doInBackground() throws Exception {
        result = DetectionDAO.getInstance().getTriggerDataFeatures(detectionid);
        return null;
    }
    
    @Override
    public void done() {
        try {
            ProgressDialog.getInstance().setVisible(false);
            get();
            SingleDetectionModel.getInstance().setTriggerStatistics(result);
        } catch (InterruptedException | ExecutionException ex) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Failed retrieving trigger statistics for detectionid : " + detectionid, ex);
        }
    }
    
}
