/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder;

import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.swing.JOptionPane;
import llnl.gnem.apps.detection.core.dataObjects.ArrayCorrelationParams;

import llnl.gnem.apps.detection.core.dataObjects.SubspaceParameters;

import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationDetector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.database.ArrayCorrelationDetectorDAO;
import llnl.gnem.apps.detection.database.ConcreteStreamProcessorDAO;
import llnl.gnem.apps.detection.database.StreamDAO;
import llnl.gnem.apps.detection.database.SubspaceDetectorDAO;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.TemplateDisplayFrame;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.TemplateModel;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.CorrelationTraceData;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DeleteDetectorWorker;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.ReplaceTemplateWorker;

/**
 *
 * @author dodge1
 */
public class DetectorCreator {

    public static void writeNewDetector() throws Exception {

        double latestStart = -Double.MAX_VALUE;
        double earliestEnd = Double.MAX_VALUE;

        CorrelatedTracesModel ctModel = CorrelatedTracesModel.getInstance();
        for (CorrelationComponent cc : ctModel.getMatchingTraces()) {
            CorrelationTraceData td = (CorrelationTraceData) cc.getTraceData();
            float[] plotData = td.getPlotData();
            double traceStart = td.getTime().getEpochTime();
            double nominalPickTime = td.getNominalPick().getTime();
            double ccShift = cc.getShift();
            double start = traceStart - nominalPickTime + ccShift;
            double end = start + (plotData.length - 1) * td.getDelta();

            if (start > latestStart) {
                latestStart = start;
            }
            if (end < earliestEnd) {
                earliestEnd = end;
            }
        }

        Collection<StreamSegment> downSampledSegments = new ArrayList<>();

        int detectorid = ctModel.getCurrentDetectorid();
        int streamid = StreamDAO.getInstance().getStreamidForDetector(detectorid);
        double maxTemplateLengthSeconds = 200;

        double windowStart = ParameterModel.getInstance().getWindowStart();
        boolean triggerOnlyOnCorrelators = false;

        ConcreteStreamProcessor processor = ConcreteStreamProcessorDAO.getInstance().createStreamProcessor(streamid, maxTemplateLengthSeconds, triggerOnlyOnCorrelators);
        double traceLength = ParameterModel.getInstance().getTraceLength();
        double tmpBlockSize = 2 * (traceLength);
        processor = processor.changeBlockSize(tmpBlockSize);

        Map<CorrelationComponent, DetectionWaveforms> components = ctModel.getComponentMap();
        for (CorrelationComponent cc : components.keySet()) {
            DetectionWaveforms dw = components.get(cc);
            Collection<WaveformSegment> segments = new ArrayList<>();
            int minLength = Integer.MAX_VALUE;
            int maxLength = 0;
            for (CorrelationComponent cc2 : dw.getSegments()) {
                CorrelationTraceData td = (CorrelationTraceData) cc2.getTraceData();
                CssSeismogram tmp = new CssSeismogram(td.getBackupSeismogram());

                float[] theData = td.getBackupSeismogram().getData();
                double delta = td.getDelta();
                double traceStart = td.getTime().getEpochTime();
                double nominalPickTime = td.getNominalPick().getTime();
                double ccShift = cc.getShift();
                double start = traceStart - nominalPickTime + ccShift;
                double end = start + (theData.length - 1) * td.getDelta();
                int idx0 = 0;
                if (start < latestStart) {
                    idx0 = (int) Math.round((latestStart - start) / delta);
                }
                int idx1 = theData.length - 1;
                if (end > earliestEnd) {
                    idx1 = (int) Math.round((earliestEnd - start) / delta);
                }
                tmp.cut(idx0, idx1);
                int blockLength = idx1 - idx0 + 1;
                if (blockLength < minLength) {
                    minLength = blockLength;
                }
                if (blockLength > maxLength) {
                    maxLength = blockLength;
                }
                WaveformSegment ws = new WaveformSegment(tmp, new ArrayList<>());
                segments.add(ws);
            }
            if (maxLength > minLength) {
                segments = adjustSegmentLengths(segments, minLength);
            }
            StreamSegment segment = new StreamSegment(segments);
            StreamSegment downSampled = processor.downSampleBlock(segment);
            downSampledSegments.add(downSampled);

        }

        boolean produceTriggers = true;
        double offsetSecondsToWindowStart = Math.max(windowStart - latestStart, 0.0);

        ParameterModel mod = ParameterModel.getInstance();
        double correlationWindowLength = mod.getCorrelationWindowLength();
        switch (mod.getDetectorType()) {

            case SUBSPACE: {

                SubspaceParameters params = new SubspaceParameters(mod.getDetectionThreshold(), mod.getEnergyCapture(), mod.getBlackoutSeconds());
                SubspaceDetector detector = SubspaceDetectorDAO.getInstance().createAndSaveSubspaceDetector(processor,
                        downSampledSegments,
                        offsetSecondsToWindowStart,
                        correlationWindowLength,
                        params,
                        produceTriggers,
                        mod.isFixSubspaceDimension(),
                        mod.getSubspaceDimension());
                TemplateDisplayFrame.getInstance().setVisible(true);
                TemplateModel.getInstance().setDetectorInfo("Newly created in Builder");
                TemplateModel.getInstance().setTemplate(detector.getTemplate(), detector.getdetectorid());
                Object[] options = {"Replace Current",
                    "Add",
                    "Discard"};
                int result = JOptionPane.showOptionDialog(ClusterBuilderFrame.getInstance(),
                        "Created: " + detector.toString(), "New Detector",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

            switch (result) {
                case JOptionPane.YES_OPTION:
                    //Switch templates, update times, reload
                    double currentWindowStart = ParameterModel.getInstance().getWindowStart();
                    //Detections are always loaded so that the template start is at relative time 0.
                    // Therefore, currentWindowStart is the required shift for all triggers.
                    int newDetectorid = detector.getdetectorid();
                    int oldDetectorid = CorrelatedTracesModel.getInstance().getCurrentDetectorid();
                    double windowLength = ParameterModel.getInstance().getCorrelationWindowLength();
                    new ReplaceTemplateWorker(oldDetectorid,
                            newDetectorid,
                            currentWindowStart,
                            windowLength).execute();
                    TemplateModel.getInstance().clear();
                    break;
            // Add to database. Already done so nothing more to do.
                case JOptionPane.NO_OPTION:
                    break;
                case JOptionPane.CANCEL_OPTION:
                    // Discard new detector
                    new DeleteDetectorWorker(detector.getdetectorid(), null).execute();
                    TemplateModel.getInstance().clear();
                    break;
                default:
                    break;
            }
                break;
            }
            case ARRAY_CORRELATION: {
                ArrayCorrelationParams params = new ArrayCorrelationParams(mod.getDetectionThreshold(), mod.getEnergyCapture(), mod.getBlackoutSeconds(),
                        (float) mod.getStaDuration(), (float) mod.getLtaDuration(), (float) mod.getGapDuration());
                ArrayCorrelationDetector detector = ArrayCorrelationDetectorDAO.getInstance().createAndSaveArrayCorrelationDetector(processor,
                        downSampledSegments,
                        offsetSecondsToWindowStart,
                        correlationWindowLength,
                        params,
                        produceTriggers);
                JOptionPane.showMessageDialog(ClusterBuilderFrame.getInstance(), "Created: " + detector.toString());
                break;
            }

        }

    }

    private static Collection<WaveformSegment> adjustSegmentLengths(Collection<WaveformSegment> segments, int minLength) {
        Collection<WaveformSegment> result = new ArrayList<>();
        for (WaveformSegment seg : segments) {
            float[] data = seg.getData();
            if (data.length == minLength) {
                result.add(seg);
            } else {
                float[] v = new float[minLength];
                System.arraycopy(data, 0, v, 0, minLength);
                seg.setData(v);
                result.add(seg);
            }
        }
        return result;
    }
}
