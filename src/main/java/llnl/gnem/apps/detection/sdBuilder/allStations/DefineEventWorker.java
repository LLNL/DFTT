package llnl.gnem.apps.detection.sdBuilder.allStations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DefineEventWorker extends SwingWorker<Void, Void> {

    private final double minTime;
    private final double maxTime;
    private final ArrayList<PhasePick> picks;
    private final ArrayList<Integer> picksToRemove;

    public DefineEventWorker(double minTime, double maxTime, Collection<PhasePick> picks,
            Collection<Integer> picksToRemove) {
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.picks = new ArrayList<>(picks);
        this.picksToRemove = new ArrayList<>(picksToRemove);
        ProgressDialog.getInstance().setTitle("Defining Event...");
        ProgressDialog.getInstance().setText("...");
        ProgressDialog.getInstance().setProgressStringPainted(true);
        ProgressDialog.getInstance().setProgressBarIndeterminate(true);
        ProgressDialog.getInstance().setReferenceFrame(AllStationsFrame.getInstance());
        ProgressDialog.getInstance().setVisible(true);
    }

    @Override
    protected Void doInBackground() throws Exception {

        DetectionDAOFactory.getInstance().getPickDAO().saveDetectionPhasePicks(picks, picksToRemove);
        DetectionDAOFactory.getInstance().getEventDAO().defineNewEvent(minTime, maxTime);
        return null;

    }

    @Override
    public void done() {
        ProgressDialog.getInstance().setVisible(false);
        try {
            get();
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error defining event.", e);
            }
        }
    }
}
