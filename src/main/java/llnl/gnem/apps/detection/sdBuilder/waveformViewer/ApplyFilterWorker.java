/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.waveformViewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class ApplyFilterWorker extends SwingWorker<Void, Void> {

    private final ArrayList<CorrelationComponent> components;
    private final StoredFilter filter;
    private final Collection<SeismogramViewer> viewers;

    public ApplyFilterWorker(Collection<CorrelationComponent> components, StoredFilter filter, Collection<SeismogramViewer> viewerList) {
        this.components = new ArrayList<>(components);
        this.filter = filter;
        viewers = new ArrayList<>(viewerList);
        ProgressDialog.getInstance().setTitle("Filtering detection segments");
        ProgressDialog.getInstance().setText("...");
        ProgressDialog.getInstance().setProgressStringPainted(true);
        ProgressDialog.getInstance().setProgressBarIndeterminate(false);
        ProgressDialog.getInstance().setReferenceFrame(ClusterBuilderFrame.getInstance());
        ProgressDialog.getInstance().setValue(0);
        ProgressDialog.getInstance().setVisible(true);
    }

    @Override
    protected Void doInBackground() throws Exception {
        int processed = 0;
        int max = components.size();
        ProgressDialog.getInstance().setMinMax(0, max);
        for (CorrelationComponent comp : components) {
            comp.applyFilter(filter);
            ProgressDialog.getInstance().setValue(++processed);
        }

        return null;
    }

    @Override
    public void done() {
        ProgressDialog.getInstance().setVisible(false);
        try {
            get();
            for (SeismogramViewer viewer : viewers) {
                viewer.updateForChangedTrace();
            }
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error applying filters.", e);
            }
        }
    }
}
