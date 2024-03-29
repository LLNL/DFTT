/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.apps.detection.sdBuilder.waveformViewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.SwingWorker;

import llnl.gnem.apps.detection.sdBuilder.actions.CreateTemplateAction;
import llnl.gnem.apps.detection.sdBuilder.configuration.DetectorCreationEnabler;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.dftt.core.correlation.ChannelDataCollection;
import llnl.gnem.dftt.core.correlation.CorrelationComponent;
import llnl.gnem.dftt.core.correlation.CorrelationResults;
import llnl.gnem.dftt.core.correlation.RealSequenceCorrelator;
import llnl.gnem.dftt.core.correlation.StationEventChannelData;
import llnl.gnem.dftt.core.correlation.clustering.AdHocClusterer;
import llnl.gnem.dftt.core.correlation.clustering.ClusterResult;
import llnl.gnem.dftt.core.correlation.clustering.ClusterType;
import llnl.gnem.dftt.core.correlation.util.ShiftType;
import llnl.gnem.dftt.core.gui.util.ExceptionDialog;
import llnl.gnem.dftt.core.gui.util.ProgressDialog;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.PairT;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ComputeCorrelationsWorker extends SwingWorker<Void, Void> {

    private ClusterResult clusterResult;
    private final boolean fixShiftsToZero;
    private final ProgressDialog progress;
    private boolean singleSeisSubmitted;
    private final Double postCorrWindowStart;
    private final Double postCorrWindowEnd;
    private final Boolean turnWindowOff;

    public ComputeCorrelationsWorker(boolean fixShiftsToZero, Double postCorrWindowStart, Double postCorrWindowEnd, Boolean turnWindowOff) {
        this.fixShiftsToZero = fixShiftsToZero;
        this.postCorrWindowStart = postCorrWindowStart;
        this.postCorrWindowEnd = postCorrWindowEnd;
        this.turnWindowOff = turnWindowOff;
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
                CorrelationResults result = correlator.optimizeShifts(cdc, ShiftType.LEAST_SQUARES, progress);
                progress.setText("Clustering correlations...");
                progress.setProgressBarIndeterminate(false);
                int numGroups = ParameterModel.getInstance().getDesiredClusterCount();
                int dataSize = result.size();
                if (numGroups > dataSize) {
                    numGroups = dataSize;
                }
                ClusterType clusterType = ParameterModel.getInstance().getClusterType();
                AdHocClusterer ahc = new AdHocClusterer(clusterType, numGroups);
                clusterResult = ahc.cluster(result, matches, ParameterModel.getInstance().getCorrelationThreshold(), fixShiftsToZero, progress);
                progress.setText("Done clustering.");
                if (clusterResult.isEmpty()) {
                    System.out.println(result.getCorrelations().get(10, 10));
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
                DetectorCreationEnabler.getInstance().setWaveformsAvailable(true);
                if (postCorrWindowStart != null && postCorrWindowEnd != null && postCorrWindowEnd > postCorrWindowStart) {
                    ClusterBuilderFrame.getInstance().zoomToNewXLimits(postCorrWindowStart, postCorrWindowEnd);
                    ParameterModel.getInstance().setAutoZoomLimits(postCorrWindowStart, postCorrWindowEnd);
                    ParameterModel.getInstance().setAutoZoomEnabled(true);
                    ClusterBuilderFrame.getInstance().autoScaleTraces();
                }
                if (turnWindowOff != null && turnWindowOff) {
                    ClusterBuilderFrame.getInstance().setCorrelationWindowVisible(false);
                    ParameterModel.getInstance().setShowCorrelationWindow(false);
                }
            } else if (singleSeisSubmitted) {
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
