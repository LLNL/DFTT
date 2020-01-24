package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import llnl.gnem.apps.detection.database.FrameworkRunDAO;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DeleteRunWorker extends SwingWorker<Void, Void> {

    private final int runid;
    private final DefaultMutableTreeNode node;

    public DeleteRunWorker(int runid, DefaultMutableTreeNode node)
    {
        this.runid = runid;
        this.node = node;
    }

    @Override
    protected Void doInBackground() throws Exception {
       
       FrameworkRunDAO.getInstance().deleteFrameworkRun(runid);
        return null;

    }

    @Override
    public void done() {
        try {
            get();
            ConfigDataModel.getInstance().removeNode(node);
        } catch (Exception e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error removing framework run.", e);
            }
        }
    }
}