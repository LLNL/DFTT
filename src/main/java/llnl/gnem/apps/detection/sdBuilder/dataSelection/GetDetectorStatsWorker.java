package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import llnl.gnem.apps.detection.database.DetectorDAO;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class GetDetectorStatsWorker extends SwingWorker<Void, Void> {

    private final Collection<DetectorStats> result;
    private final int runid;
    private final DefaultMutableTreeNode targetNode;

    public GetDetectorStatsWorker(int runid, DefaultMutableTreeNode node)
    {
        result = new ArrayList<>();
        this.runid = runid;
        targetNode = node;
    }

    @Override
    protected Void doInBackground() throws Exception {
       
        result.addAll(DetectorDAO.getInstance().getDetectorStats(runid, ParameterModel.getInstance().isSuppressBadDetectors()));
        return null;

    }

    @Override
    public void done() {
        try {
            get();
            ConfigDataModel.getInstance().setDetectorStats(result, targetNode);
            
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error retrieving detector stats collection.", e);
            }
        }
    }
}