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
package llnl.gnem.apps.detection.sdBuilder;

import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataUnits;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.ConfigurationInfo;
import llnl.gnem.apps.detection.classify.TriggerClassification;
import llnl.gnem.apps.detection.core.dataObjects.DetectionObjects;
import llnl.gnem.apps.detection.core.framework.detectors.EmpiricalTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayConfiguration;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayElementInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;

import llnl.gnem.apps.detection.sdBuilder.actions.AdvanceAction;

import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.histogramDisplay.HistogramModel;
import llnl.gnem.apps.detection.sdBuilder.picking.PredictedPhasePick;
import llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay.SingleDetectionModel;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.TemplateModel;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.ProjectionModel;

import llnl.gnem.apps.detection.source.SourceData;
import llnl.gnem.apps.detection.util.ArrayInfoModel;
import llnl.gnem.apps.detection.util.FrameworkRun;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.CorrelationEventInfo;
import llnl.gnem.core.correlation.CorrelationTraceData;
import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.seismicData.EventInfo;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class SegmentRetrievalWorker extends SwingWorker<Void, Void> {

    private final SourceData source;
    private final double preTrigSeconds;
    private double duration;
    private final int runid;
    private final int detectorid;
    private final Collection<DetectionWaveforms> retrieved;
    private double correlationWindowLength = 100;
    private int successCount = 0;
    private int failedCount = 0;
    private final FrameworkRun runInfo;
    private final Map<Integer, TriggerClassification> triggerClassificationMap;
    private final Collection<PhasePick> detectionPhasePicks;
    private final Collection<PredictedPhasePick> predictedPhasePicks;
    private final boolean retrieveByBlocks;
    private final int blockSize;
    private int lastRetrievedDetectionId;
    private int rowsRetrieved;
    private final int configid;

    public SegmentRetrievalWorker(int runid, int detectorid, SourceData source, double preTrigSeconds, double duration, FrameworkRun runInfo) {
        this.runid = runid;
        this.detectorid = detectorid;
        this.source = source;
        this.preTrigSeconds = preTrigSeconds;
        this.duration = duration;
        this.runInfo = runInfo;
        retrieved = new ArrayList<>();
        triggerClassificationMap = new HashMap<>();
        detectionPhasePicks = new ArrayList<>();
        predictedPhasePicks = new ArrayList<>();
        retrieveByBlocks = ParameterModel.getInstance().isRetrieveByBlocks();
        blockSize = ParameterModel.getInstance().getBlockSize();
        lastRetrievedDetectionId = CorrelatedTracesModel.getInstance().getLastDetectionId();
        configid = runInfo.getConfigid();
        ProgressDialog.getInstance().setTitle("Retrieving detection segments");
        ProgressDialog.getInstance().setText("...");
        ProgressDialog.getInstance().setProgressStringPainted(true);
        ProgressDialog.getInstance().setProgressBarIndeterminate(true);
        ProgressDialog.getInstance().setReferenceFrame(ClusterBuilderFrame.getInstance());
        ProgressDialog.getInstance().setVisible(true);
    }

    @Override
    protected Void doInBackground() throws Exception {

        ConfigurationInfo.getInstance().setCurrentConfigurationData(runInfo.getConfigid());

        if (duration <= 0) {
            duration = preTrigSeconds + 1.5 * DetectionDAOFactory.getInstance().getTriggerDAO().getMeanDuration(runid, detectorid);
        }

        DetectionObjects detObjs = DetectionDAOFactory.getInstance().getDetectionDAO().getDetectionObjects(runid, detectorid, retrieveByBlocks, blockSize, lastRetrievedDetectionId);
        lastRetrievedDetectionId = detObjs.getMaxDetectionId();
        rowsRetrieved = detObjs.getRowsRetrieved();
        triggerClassificationMap.putAll(detObjs.getTriggerClassificationMap());

        detectionPhasePicks.addAll(DetectionDAOFactory.getInstance().getPickDAO().getDetectionPhasePicks(runid, detectorid));
        predictedPhasePicks.addAll(DetectionDAOFactory.getInstance().getPredictedPhasePickDAO().getPredictedPicks(runid, detectorid));
        Collection<PhasePick> allPicks = new ArrayList<>();//DetectionDAOFactory.getInstance().getPickDAO().getAllPicks(configid);
        if (detObjs.getU().size() > 0) {
            ProgressDialog.getInstance().setProgressBarIndeterminate(false);
            ProgressDialog.getInstance().setMinMax(0, detObjs.getDetTimes().size());
            ProgressDialog.getInstance().setValue(0);
            AtomicInteger successes = new AtomicInteger(0);
            AtomicInteger failures = new AtomicInteger(0);
            AtomicInteger processedCount = new AtomicInteger(0);
            List<DetectionWaveforms> results = detObjs.getDetTimes().parallelStream().map(t -> getDetectionWaveforms(t, allPicks, successes, failures, processedCount)).filter(Objects::nonNull).collect(Collectors.toList());
            successCount = successes.get();
            failedCount = failures.get();
            retrieved.addAll(results);
            correlationWindowLength = DetectionDAOFactory.getInstance().getTriggerDAO().getAverageSignalDuration(runid, detectorid);
            try {
                EmpiricalTemplate etemplate = DetectionDAOFactory.getInstance().getSubspaceTemplateDAO().getEmpiricalTemplate(detectorid);
                float[][] data = null;

                SubspaceTemplate template = (SubspaceTemplate) etemplate;
                data = template.getRepresentation().get(0);
                double rate = etemplate.getProcessingParameters().samplingRate / etemplate.getProcessingParameters().decrate;
                correlationWindowLength = (int) (data[0].length / rate);
            } catch (Exception ex) {
                //

            }
        }

        return null;

    }

    private DetectionWaveforms getDetectionWaveforms(PairT<Integer, Double> pair, Collection<PhasePick> allPicks, AtomicInteger successes, AtomicInteger failures, AtomicInteger processedCount) {
        int detectionid = pair.getFirst();
        ProgressDialog.getInstance().setText("Processing detectionid: " + detectionid);
        double time = pair.getSecond();
        Collection<WaveformSegment> results = null;
        try {
            results = source.retrieveDataBlock(new TimeT(time - preTrigSeconds), duration, false);
        } catch (Exception ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed retrieving data block", ex);
            failures.incrementAndGet();
            ProgressDialog.getInstance().setValue(processedCount.incrementAndGet());
            return null;
        }
        boolean segmentsAreSameLength = segmentLengthsAgree(results);
        if (segmentsAreSameLength) {
            Collection<CorrelationComponent> components = buildCorrelationComponentCollection(detectionid, time, results, 1);
            if (components.size() == results.size()) {
                Epoch epoch = new Epoch(time - preTrigSeconds, time - preTrigSeconds + duration);
                try {
                    Collection<PhasePick> picks = getNonDetectionPhasePicks(epoch, detectionid, allPicks);
                    successes.incrementAndGet();
                    ProgressDialog.getInstance().setValue(processedCount.incrementAndGet());
                    return new DetectionWaveforms(detectionid, components, picks);
                } catch (DataAccessException ex) {
                    failures.incrementAndGet();
                    ProgressDialog.getInstance().setValue(processedCount.incrementAndGet());
                    return null;
                }
            } else {
                failures.incrementAndGet();
                ProgressDialog.getInstance().setValue(processedCount.incrementAndGet());
                return null;
            }
        } else {
            ProgressDialog.getInstance().setText(String.format("Skipping detectionid: %d because not all segements are same length.", detectionid));
            failures.incrementAndGet();
            ProgressDialog.getInstance().setValue(processedCount.incrementAndGet());
            return null;
        }

    }

    private Collection<PhasePick> getNonDetectionPhasePicks(Epoch epoch, int detectionid, Collection<PhasePick> allPicks) throws DataAccessException {
        Collection<PhasePick> result = new ArrayList<>();
        Iterator<PhasePick> it = allPicks.iterator();
        while (it.hasNext()) {
            PhasePick pick = it.next();
            if (!epoch.ContainsTime(new TimeT(pick.getTime()))) {
                continue;
            } else if (pick.getDetectionid() != null && pick.getDetectionid() == detectionid) {
                continue;
            } else {
                result.add(pick);
            }
        }
        return result;
    }

    private boolean segmentLengthsAgree(Collection<WaveformSegment> results) {
        int length = -1;
        for (WaveformSegment seg : results) {
            int aLength = seg.getNsamp();
            if (length < 0) {
                length = aLength;
            } else if (length != aLength) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void done() {
        ProgressDialog.getInstance().setVisible(false);
        try {
            get();
            CorrelatedTracesModel.getInstance().clear();
            if (TemplateModel.getInstance().getDetectorid() != detectorid) {
                TemplateModel.getInstance().clear();
            }
            if (SingleDetectionModel.getInstance().getDetectorid() != detectorid) {
                SingleDetectionModel.getInstance().clear();
            }
            if (HistogramModel.getInstance().getDetectorid() != detectorid) {
                HistogramModel.getInstance().clear();
            }
            if (ProjectionModel.getInstance().getDetectorid() != detectorid) {
                ProjectionModel.getInstance().clear();
            }
            CorrelatedTracesModel.getInstance().setLastDetectionId(lastRetrievedDetectionId);
            CorrelatedTracesModel.getInstance().incrementRowsRetrieved(rowsRetrieved);
            AdvanceAction.getInstance(this).setEnabled(CorrelatedTracesModel.getInstance().isCanAdvance());

            ParameterModel.getInstance().setCorrelationWindowLength(correlationWindowLength);
            CorrelatedTracesModel.getInstance().setTraces(retrieved, triggerClassificationMap,
                    detectionPhasePicks, predictedPhasePicks);
            CorrelatedTracesModel.getInstance().setRunid(runid);
            CorrelatedTracesModel.getInstance().setConfigid(configid);
            if (failedCount > 0) {
                String msg = String.format("%d detections were retrieved.\n%d were rejected because of missing data.", successCount, failedCount);
                JOptionPane.showMessageDialog(ClusterBuilderFrame.getInstance(), msg);

            }
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(SegmentRetrievalWorker.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        ClusterBuilderFrame.getInstance().returnFocusToTree();

    }

    private Collection<CorrelationComponent> buildCorrelationComponentCollection(int detectionid, double time,
            Collection<WaveformSegment> results, int wfid) {
        Map<StreamKey, ArrayElementInfo> keyElementMap = new HashMap<>();
        Collection<CorrelationComponent> components = new ArrayList<>();
        Collection<StreamKey> channels = getChannelKeys(results);
        ArrayConfiguration config = ArrayInfoModel.getInstance().getGeometry(channels);
        if (config != null) {
            keyElementMap.putAll(config.getElements(channels, new TimeT(time).getJdate()));
        }
        NominalArrival arrival = new NominalArrival("DET", time);
        for (WaveformSegment ws : results) {
            ArrayElementInfo aei = keyElementMap.get(ws.getStreamKey());
            CssSeismogram seis = new CssSeismogram(wfid++, ws.getStreamKey(), ws.getData(), ws.getSamprate(), new TimeT(ws.getTimeAsDouble()), 1.0, -1.0);
            if (runInfo.isRawRateFixed() && runInfo.getFixedRawSampleRate() != seis.getSamprate()) {
                seis.resample(runInfo.getFixedRawSampleRate());
            }

            CorrelationTraceData ctd = new CorrelationTraceData(seis, WaveformDataType.counts,
                    WaveformDataUnits.unknown, arrival);
            CorrelationEventInfo info = new CorrelationEventInfo(detectionid);
            CorrelationComponent component;
            if (aei != null) {
                component = new CorrelationComponent(ctd, info, aei.getArrayName(), aei.getDnorth(), aei.getDeast());
            } else {
                component = new CorrelationComponent(ctd, info);
            }
            components.add(component);
        }
        return components;
    }

    private Collection<StreamKey> getChannelKeys(Collection<WaveformSegment> waveforms) {
        Collection<StreamKey> results = new ArrayList<>();
        for (WaveformSegment ws : waveforms) {
            results.add(ws.getStreamKey());
        }
        return results;
    }
}
