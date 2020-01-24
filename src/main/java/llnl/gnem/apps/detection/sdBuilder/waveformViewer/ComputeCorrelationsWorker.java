package llnl.gnem.apps.detection.sdBuilder.waveformViewer;

import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.sdBuilder.actions.CreateTemplateAction;
import llnl.gnem.apps.detection.sdBuilder.actions.OutputClustersAction;
import llnl.gnem.apps.detection.sdBuilder.configuration.DetectorCreationEnabler;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.core.correlation.*;
import llnl.gnem.core.correlation.clustering.AdHocClusterer;
import llnl.gnem.core.correlation.clustering.ClusterResult;
import llnl.gnem.core.correlation.util.*;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.PairT;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ComputeCorrelationsWorker extends SwingWorker<Void, Void> {
    
    private ClusterResult clusterResult;
    private final boolean fixShiftsToZero;
    private final ProgressDialog progress;
    private boolean singleSeisSubmitted;
    
    public ComputeCorrelationsWorker(boolean fixShiftsToZero) {
        this.fixShiftsToZero = fixShiftsToZero;
        Collection<CorrelationComponent> matches = CorrelatedTracesModel.getInstance().getMatchingTraces();
        for (CorrelationComponent cc : matches) {
            if (!cc.containsWindow()) {
                CorrelatedTracesModel.getInstance().removeComponent(cc);
            }
        }
        progress = ProgressDialog.getInstance();
        progress.setTitle("Performing Correlation and Clustering");
        progress.setVisible(true);
        progress.setProgressBarIndeterminate(true);
    }
    
    @Override
    protected Void doInBackground() throws Exception {
        double windowStart = -ParameterModel.getInstance().getWindowStart();        
        
        double windowEnd = ParameterModel.getInstance().getCorrelationWindowLength() - windowStart;
        progress.setText("Accumulating data...");
        Collection<CorrelationComponent> matches = CorrelatedTracesModel.getInstance().getMatchingTraces();
        singleSeisSubmitted = matches.size() == 1;
        ArrayList<StationEventChannelData> secdCollection = new ArrayList<>();
        for (CorrelationComponent cc : matches) {
            secdCollection.add(new StationEventChannelData(cc));
        }
        
        if (!secdCollection.isEmpty()) {
            PairT<Double, Double> windowOffsets = new PairT<>(windowStart, windowEnd);
            ChannelDataCollection cdc = new ChannelDataCollection(secdCollection, windowOffsets);
            if (cdc.size() >= 2) {
                RealSequenceCorrelator correlator = new RealSequenceCorrelator();
                correlator.configureExecutorService();
                CorrelationResults result = correlator.optimizeShifts(cdc, ShiftType.LEAST_SQUARES, progress);
                correlator.shutCompServicedown();
                progress.setText("Clustering correlations...");
                progress.setProgressBarIndeterminate(true);
                AdHocClusterer ahc = new AdHocClusterer();
                clusterResult = ahc.cluster(result, matches, ParameterModel.getInstance().getCorrelationThreshold(), fixShiftsToZero);
                progress.setText("Done clustering.");
                if (clusterResult.isEmpty()) {
                    result.getCorrelations().print(10, 10);
                }
            }
        }
        return null;
        
    }
    
    @Override
    public void done() {
        try {
            progress.setVisible(false);
            get();
            if (clusterResult != null) {
                CorrelatedTracesModel.getInstance().updateFromClusterResult(clusterResult);
                OutputClustersAction.getInstance(this).setEnabled(true);
                DetectorCreationEnabler.getInstance().setWaveformsAvailable(true);
            }
            else if( singleSeisSubmitted ){
                CreateTemplateAction.getInstance(this).setEnabled(true);
                DetectorCreationEnabler.getInstance().hasBeenCorrelated(true);
                DetectorCreationEnabler.getInstance().setWaveformsAvailable(true);
            }
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ExceptionDialog.displayError(e);
                ApplicationLogger.getInstance().log(Level.WARNING, "Error computing correlations.", e);
            }
        }
    }
}
