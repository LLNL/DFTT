package llnl.gnem.core.gui.waveform.phaseVisibility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;
import llnl.gnem.core.gui.waveform.PlotPickingStateManager;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class AvailablePhaseRetrievalWorker extends SwingWorker<Void, Void> {

    private final AvailablePhaseManager manager;
    private final Collection<SeismicPhase> phases;
    private final Collection<PlotPickingStateManager> pickManagers;

    public AvailablePhaseRetrievalWorker(AvailablePhaseManager manager, Collection<PlotPickingStateManager> pickManagers) {
        this.manager = manager;
        phases = new ArrayList<>();
        this.pickManagers = new ArrayList<>(pickManagers);
    }

    @Override
    protected Void doInBackground() throws Exception {

        phases.addAll(DAOFactory.getInstance().getSeismicPhaseDAO().getAK135Phases());
        return null;
    }

    @Override
    public void done() {
        try {
            get();
            manager.replacePhases(phases);
            for (PlotPickingStateManager ppsm : pickManagers) {
                ppsm.updateForChangeInAllowablePhases();
            }
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error retrieving phase data.", e);
            }
        }
    }
}
