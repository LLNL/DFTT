package llnl.gnem.apps.detection.sdBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.database.ClustersDAO;

import llnl.gnem.apps.detection.sdBuilder.actions.OutputClustersAction;
import llnl.gnem.core.correlation.clustering.GroupData;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.util.ApplicationLogger;


public class WriteClustersWorker extends SwingWorker<Void, Void> {

    private final int detectorid;
    private final int runid;
    private final ArrayList<GroupData> groups;

    public WriteClustersWorker(Collection<GroupData> groups, int detectorid, int runid) {

        this.groups = new ArrayList<>(groups);
        this.detectorid = detectorid;
        this.runid = runid;
    }

    @Override
    protected Void doInBackground() throws Exception {
        ClustersDAO.getInstance().writeClusters(groups, detectorid, runid);
        return null;

    }

    @Override
    public void done() {
        try {
            OutputClustersAction.getInstance(this).setEnabled(false);
            get();
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ExceptionDialog.displayError(e);
                ApplicationLogger.getInstance().log(Level.WARNING, "Error writing clusters.", e);
            }
        }
    }
}
