package llnl.gnem.apps.detection.sdBuilder.picking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class SavePicksWorker extends SwingWorker<Void, Void> {
    private final ArrayList<PhasePick> picks;
    private final ArrayList<Integer> picksToRemove;

    public SavePicksWorker(Collection<PhasePick> picks,
            Collection<Integer> picksToRemove,
            JFrame referenceFrame) {
        this.picks = new ArrayList<>(picks);
        this.picksToRemove = new ArrayList<>(picksToRemove);
        ProgressDialog.getInstance().setTitle("Saving Picks...");
        ProgressDialog.getInstance().setText("...");
        ProgressDialog.getInstance().setProgressStringPainted(true);
        ProgressDialog.getInstance().setProgressBarIndeterminate(true);
        ProgressDialog.getInstance().setReferenceFrame(referenceFrame);
        ProgressDialog.getInstance().setVisible(true);
    }

    @Override
    protected Void doInBackground() throws Exception {
        
        DetectionDAOFactory.getInstance().getPickDAO().saveDetectionPhasePicks(picks,picksToRemove);
        return null;

    }

    @Override
    public void done() {
        ProgressDialog.getInstance().setVisible(false);
        try {
            get();
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error saving picks.", e);
            }
        }
    }
}