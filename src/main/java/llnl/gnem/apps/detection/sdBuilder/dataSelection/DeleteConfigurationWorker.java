package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import llnl.gnem.apps.detection.database.ConfigurationDAO;
import llnl.gnem.apps.detection.database.FrameworkRunDAO;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DeleteConfigurationWorker extends SwingWorker<Void, Void> {
    
    private final int configid;
    private final DefaultMutableTreeNode node;
    
    public DeleteConfigurationWorker(int configid, DefaultMutableTreeNode node) {
        this.configid = configid;
        this.node = node;
    }
    
    @Override
    protected Void doInBackground() throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            ConfigurationDAO.getInstance().removeConfiguration(conn, configid);
            return null;
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
        
    }
    
    @Override
    public void done() {
        try {
            get();
            ConfigDataModel.getInstance().removeNode(node);
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error removing configuration.", e);
            }
        }
    }
}
