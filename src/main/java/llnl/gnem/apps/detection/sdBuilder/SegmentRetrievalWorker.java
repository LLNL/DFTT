package llnl.gnem.apps.detection.sdBuilder;

import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataUnits;
import llnl.gnem.core.waveform.components.RotationStatus;
import llnl.gnem.core.waveform.components.ComponentIdentifier;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.classify.TriggerClassification;
import llnl.gnem.apps.detection.core.dataObjects.DetectionObjects;
import llnl.gnem.apps.detection.core.framework.detectors.EmpiricalTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;

import llnl.gnem.apps.detection.database.DbOps;
import llnl.gnem.apps.detection.database.DetectionDAO;
import llnl.gnem.apps.detection.database.PredictedPhasePickDAO;
import llnl.gnem.apps.detection.database.SubspaceTemplateDAO;
import llnl.gnem.apps.detection.database.TableNames;
import llnl.gnem.apps.detection.database.TriggerDAO;
import llnl.gnem.apps.detection.sdBuilder.actions.AdvanceAction;
import llnl.gnem.apps.detection.sdBuilder.actions.OutputClustersAction;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.histogramDisplay.HistogramModel;
import llnl.gnem.apps.detection.sdBuilder.picking.PredictedPhasePick;
import llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay.SingleDetectionModel;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.TemplateModel;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.ProjectionModel;
import llnl.gnem.apps.detection.source.SourceData;
import llnl.gnem.apps.detection.util.FrameworkRun;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.CorrelationEventInfo;
import llnl.gnem.core.correlation.CorrelationTraceData;
import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.database.dao.CssSiteDAO;
import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.responseProcessing.TransferStatus;

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
        source.setStaChanArrays();

        Connection conn = null;
        if (duration <= 0) {
            duration = preTrigSeconds + 1.5 * DbOps.getInstance().getMeanDuration(runid, detectorid);
        }
        try {
            conn = ConnectionManager.getInstance().checkOut();
            DetectionObjects detObjs = DetectionDAO.getInstance().getDetectionObjects(runid, detectorid, retrieveByBlocks, blockSize, lastRetrievedDetectionId);
            lastRetrievedDetectionId = detObjs.getMaxDetectionId();
            rowsRetrieved = detObjs.getRowsRetrieved();
            triggerClassificationMap.putAll(detObjs.getTriggerClassificationMap());
            
            detectionPhasePicks.addAll(DetectionDAOFactory.getInstance().getPickDAO().getDetectionPhasePicks(runid, detectorid));
            predictedPhasePicks.addAll(PredictedPhasePickDAO.getInstance().getPredictedPicks(runid, detectorid));
            if (detObjs.getU().size() > 0) {
                ProgressDialog.getInstance().setProgressBarIndeterminate(false);
                ProgressDialog.getInstance().setMinMax(0, detObjs.getDetTimes().size());
                ProgressDialog.getInstance().setValue(0);
                int processed = 0;
                source.setCommonSampleRate(detObjs.getU());
                for (PairT<Integer, Double> pair : detObjs.getDetTimes()) {

                    int detectionid = pair.getFirst();
                    ProgressDialog.getInstance().setText("Processing detectionid: " + detectionid);
                    double time = pair.getSecond();
                    Collection<WaveformSegment> results = source.retrieveDataBlock(new TimeT(time - preTrigSeconds), duration, false);
                    boolean segmentsAreSameLength = segmentLengthsAgree(results);
                    if (segmentsAreSameLength) {
                        Collection<CorrelationComponent> components = buildCorrelationComponentCollection(detectionid, time, results, 1, conn);
                        if (components.size() == results.size()) {
                            Epoch epoch = new Epoch(time - preTrigSeconds, time - preTrigSeconds + duration);
                            Collection<PhasePick> picks = getNonDetectionPhasePicks(epoch, detectionid);
                                retrieved.add(new DetectionWaveforms(detectionid, components, picks));
                            ++successCount;
                        } else {
                            ++failedCount;
                        }
                    } else {
                        ProgressDialog.getInstance().setText(String.format("Skipping detectionid: %d because not all segements are same length.", detectionid));
                        ++failedCount;
                    }
                    ProgressDialog.getInstance().setValue(++processed);
                }
                correlationWindowLength = TriggerDAO.getInstance().getAverageSignalDuration(runid, detectorid, conn);
                try {
                    EmpiricalTemplate etemplate = SubspaceTemplateDAO.getInstance().getEmpiricalTemplate(conn, detectorid);
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
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    private Collection<PhasePick> getNonDetectionPhasePicks(Epoch epoch, int detectionid) throws DataAccessException {
        Collection<PhasePick> picks = DetectionDAOFactory.getInstance().getPickDAO().getPicks(configid, epoch);
        Iterator<PhasePick> it = picks.iterator();
        while(it.hasNext()){
            PhasePick pick = it.next();
            if(pick.getDetectionid() != null && pick.getDetectionid() == detectionid){
                it.remove();
            }
        }
        return picks;
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
            OutputClustersAction.getInstance(this).setEnabled(false);
            if (failedCount > 0) {
                String msg = String.format("%d detections were retrieved.\n%d were rejected because of missing data.", successCount, failedCount);
                JOptionPane.showMessageDialog(ClusterBuilderFrame.getInstance(), msg);

            }
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(SegmentRetrievalWorker.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private StationInfo getStationInfo(String sta, double start, Connection conn) throws SQLException {

        TimeT time = new TimeT(start);
        return CssSiteDAO.getInstance().getSiteRow(sta, time.getJdate(), TableNames.getInstance().getSiteTableName(), conn);

    }

    private Collection<CorrelationComponent> buildCorrelationComponentCollection(int detectionid, double time,
            Collection<WaveformSegment> results, int wfid,
            Connection conn) throws SQLException {
        Collection<CorrelationComponent> components = new ArrayList<>();
        NominalArrival arrival = new NominalArrival("DET", time);
        for (WaveformSegment ws : results) {
            CssSeismogram seis = new CssSeismogram(wfid++, ws.getSta(), ws.getChan(), ws.getData(), ws.getSamprate(), new TimeT(ws.getTimeAsDouble()), 1.0, -1.0);
            if (runInfo.isRawRateFixed() && runInfo.getFixedRawSampleRate() != seis.getSamprate()) {
                seis.resample(runInfo.getFixedRawSampleRate());
            }
            CorrelationTraceData ctd = new CorrelationTraceData(seis, WaveformDataType.counts,
                    WaveformDataUnits.unknown, arrival);
            StationInfo si = getStationInfo(ws.getSta(), ws.getTimeAsDouble(), conn);
            ComponentIdentifier identifier = DbOps.getInstance().getComponentIdentifier(ws.getChan());
            CorrelationEventInfo info = new CorrelationEventInfo(detectionid);
            if (identifier != null) {
                CorrelationComponent component = new CorrelationComponent(si,
                        identifier,
                        ctd,
                        TransferStatus.UNTRANSFERRED,
                        RotationStatus.UNROTATED,
                        info, null);
                components.add(component);
            }
        }
        return components;
    }
}
