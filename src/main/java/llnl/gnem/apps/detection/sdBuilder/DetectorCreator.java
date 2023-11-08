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

import java.awt.HeadlessException;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.swing.JOptionPane;
import llnl.gnem.apps.detection.classify.TriggerClassification;
import llnl.gnem.apps.detection.core.dataObjects.ArrayCorrelationParams;

import llnl.gnem.apps.detection.core.dataObjects.SubspaceParameters;

import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationDetector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;


import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.TemplateDisplayFrame;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.TemplateModel;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.dftt.core.correlation.CorrelationComponent;
import llnl.gnem.dftt.core.correlation.CorrelationTraceData;
import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.sdBuilder.configuration.DetectorCreationOption;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.ClassifyDetectionWorker;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DeleteDetectorWorker;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.ReplaceTemplateWorker;
import llnl.gnem.dftt.core.gui.util.ProgressDialog;

/**
 *
 * @author dodge1
 */
public class DetectorCreator {

    private static final String BUILDER_CREATE_STRING = "Created by Builder";

    public static void writeNewDetector() throws Exception {

        double latestStart = -Double.MAX_VALUE;
        double earliestEnd = Double.MAX_VALUE;

        ProgressDialog.getInstance().setText("Determining window bounds...");

        CorrelatedTracesModel ctModel = CorrelatedTracesModel.getInstance();
        for (CorrelationComponent cc : ctModel.getMatchingTraces()) {
            CorrelationTraceData td = (CorrelationTraceData) cc.getCorrelationTraceData();
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
        int streamid = DetectionDAOFactory.getInstance().getStreamDAO().getStreamidForDetector(detectorid);
        double maxTemplateLengthSeconds = 200;

        double windowStart = ParameterModel.getInstance().getWindowStart();
        boolean triggerOnlyOnCorrelators = false;
        ProgressDialog.getInstance().setText("Creating Stream Processor...");

        ConcreteStreamProcessor processor = DetectionDAOFactory.getInstance().getStreamProcessorDAO().createStreamProcessor(streamid, maxTemplateLengthSeconds, triggerOnlyOnCorrelators);
        double traceLength = ParameterModel.getInstance().getTraceLength();
        double tmpBlockSize = 2 * (traceLength);
        processor = processor.changeBlockSize(tmpBlockSize);

        ProgressDialog.getInstance().setText("Getting Component Map...");
        Map<CorrelationComponent, DetectionWaveforms> components = ctModel.getComponentMap();
        ProgressDialog.getInstance().setText("Building WaveformSegment collection...");
        for (CorrelationComponent cc : components.keySet()) {
            DetectionWaveforms dw = components.get(cc);
            Collection<WaveformSegment> segments = new ArrayList<>();
            int minLength = Integer.MAX_VALUE;
            int maxLength = 0;
            for (CorrelationComponent cc2 : dw.getSegments()) {
                CorrelationTraceData td = (CorrelationTraceData) cc2.getCorrelationTraceData();
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
                SubspaceDetector detector = DetectionDAOFactory.getInstance().getSubspaceDetectorDAO().createAndSaveSubspaceDetector(processor,
                        downSampledSegments,
                        offsetSecondsToWindowStart,
                        correlationWindowLength,
                        params,
                        mod.isFixSubspaceDimension(),
                        mod.isCapSubspaceDimension(),
                        mod.getSubspaceDimension(), 
                        BUILDER_CREATE_STRING,
                        ProgressDialog.getInstance());
                if (mod.isDisplayNewTemplates()) {
                    TemplateDisplayFrame.getInstance().setVisible(true);
                    TemplateModel.getInstance().setDetectorInfo("Newly created in Builder");
                    TemplateModel.getInstance().setTemplate(detector.getTemplate(), detector.getdetectorid());
                }
                ProgressDialog.getInstance().setVisible(false);
                int result = getCreationAction(mod, detector);
                //               int result = JOptionPane.YES_OPTION;
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
                                windowLength, BUILDER_CREATE_STRING).execute();
                        if (mod.isDisplayNewTemplates()) {
                            TemplateModel.getInstance().setDetectorInfo("Created by Builder");
                            TemplateModel.getInstance().setTemplate(detector.getTemplate(), oldDetectorid);
                        }

                        //                       TemplateModel.getInstance().clear();
                        break;
                    // Add to database. Already done so just update classification if one exists.
                    case JOptionPane.NO_OPTION:
                        TriggerClassification tc = CorrelatedTracesModel.getInstance().getCommonTriggerClassification();
                        String status = tc.getStatus();
                        new ClassifyDetectionWorker(detector.getdetectorid(), status).execute();
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
                ArrayCorrelationDetector detector = DetectionDAOFactory.getInstance().getArrayCorrelationDetectorDAO().createAndSaveArrayCorrelationDetector(processor,
                        downSampledSegments,
                        offsetSecondsToWindowStart,
                        correlationWindowLength,
                        params, BUILDER_CREATE_STRING);
                JOptionPane.showMessageDialog(ClusterBuilderFrame.getInstance(), "Created: " + detector.toString());
                break;
            }

        }

    }

    private static int getCreationAction(ParameterModel mod, SubspaceDetector detector) throws HeadlessException {
        DetectorCreationOption dco = mod.getDetectorCreationOption();
        switch (dco) {
            case REPLACE:
                return JOptionPane.YES_OPTION;
            case ADD:
                return JOptionPane.NO_OPTION;
            case DELETE:
                return JOptionPane.CANCEL_OPTION;
            case PROMPT:
            default: {
        Object[] options = {"Replace Current",
            "Add",
            "Discard"};
        return JOptionPane.showOptionDialog(ClusterBuilderFrame.getInstance(),
                "Created: " + detector.toString(), "New Detector",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
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
