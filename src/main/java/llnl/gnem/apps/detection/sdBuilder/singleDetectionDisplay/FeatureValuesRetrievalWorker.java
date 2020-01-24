/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.database.TriggerDAO;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class FeatureValuesRetrievalWorker extends SwingWorker<Void, Void> {

    private final int runid;
    private final String columnName;
    private final Collection<Double> result;

    public FeatureValuesRetrievalWorker(int runid, String columnName) {
        this.runid = runid;
        this.columnName = columnName;
        result = new ArrayList<>();
        ProgressDialog.getInstance().setReferenceFrame(SingleDetectionDisplayFrame.getInstance());
        ProgressDialog.getInstance().setProgressBarIndeterminate(true);
        ProgressDialog.getInstance().setProgressStringPainted(false);
        ProgressDialog.getInstance().setTitle("retrieving statistics");
        ProgressDialog.getInstance().setText(columnName);
        ProgressDialog.getInstance().setVisible(true);
    }

    @Override
    protected Void doInBackground() throws Exception {
        result.addAll(TriggerDAO.getInstance().getFeatureValues(runid, columnName));
        return null;
    }

    @Override
    public void done() {
        try {
            ProgressDialog.getInstance().setVisible(false);
            get();
            SingleDetectionModel.getInstance().setFeatureValues(columnName, result);
        } catch (InterruptedException | ExecutionException ex) {
            ApplicationLogger.getInstance().log(Level.WARNING, String.format("Failed retrieving %s feature values for runid %d ", columnName, runid), ex);
        }
    }

}
