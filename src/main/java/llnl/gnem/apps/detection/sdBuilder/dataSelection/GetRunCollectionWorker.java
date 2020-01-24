package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import llnl.gnem.apps.detection.database.FrameworkRunDAO;
import llnl.gnem.apps.detection.util.FrameworkRun;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class GetRunCollectionWorker extends SwingWorker<Void, Void> {

    private final Collection<FrameworkRun> result;
    private final int configid;
    private final DefaultMutableTreeNode targetNode;

    public GetRunCollectionWorker(int configid, DefaultMutableTreeNode node)
    {
        result = new ArrayList<>();
        this.configid = configid;
        targetNode = node;
    }

    @Override
    protected Void doInBackground() throws Exception {
       
        result.addAll(FrameworkRunDAO.getInstance().getConfigRunCollection(configid));
        return null;

    }

    @Override
    public void done() {
        try {
            get();
            ConfigDataModel.getInstance().setRunCollection(result, targetNode);
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error retrieving configuration run collection.", e);
            }
        }
    }
}