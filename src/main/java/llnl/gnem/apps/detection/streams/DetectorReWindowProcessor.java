/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.streams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.SubspaceParameters;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.core.windowRevision.WindowSelector;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Detection;
import llnl.gnem.apps.detection.source.SourceData;
import llnl.gnem.apps.detection.util.SourceDataHolder;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.apps.detection.util.initialization.StreamInfo;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.signalprocessing.statistics.SignalPairStats;
import llnl.gnem.dftt.core.signalprocessing.statistics.TimeBandwidthComponents;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.Passband;
import llnl.gnem.dftt.core.util.SeriesMath;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.waveform.merge.NamedIntWaveform;
import llnl.gnem.dftt.core.waveform.seismogram.BasicSeismogram;
import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author dodge1
 */
public class DetectorReWindowProcessor {

    private final ConcreteStreamProcessor processor;

    public DetectorReWindowProcessor(ConcreteStreamProcessor processor) {
        this.processor = processor;
    }

    public SubspaceDetector rewindowOneDetector(int detectorid, Collection<Detection> detections) throws DataAccessException {
        Detector detector = processor.getDetector(detectorid);
        if (!(detector instanceof SubspaceDetector)) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Expecting re-window candidate to be a subspace detector!");
            return null;
        }
        SubspaceDetector originalDetector = (SubspaceDetector) detector;
        ApplicationLogger.getInstance().log(Level.INFO, String.format("Re-windowing detector %d with %d detections...", detectorid, detections.size()));
        StreamInfo info = StreamsConfig.getInstance().getInfo(processor.getStreamName());
        Map<Integer, ArrayList<BasicSeismogram>> data = extractDetectionSegments(detections);
        Double rate = null;
        int npts = Integer.MAX_VALUE;
        Integer numberOfChanels = null;
        ArrayList< float[][]> X = new ArrayList<>();
        for (int detectionid : data.keySet()) {
            ArrayList<BasicSeismogram> seismograms = data.get(detectionid);
            if (numberOfChanels == null) {
                numberOfChanels = seismograms.size();
            }
            ArrayList<float[]> waveforms = new ArrayList<>();
            for (BasicSeismogram seis : seismograms) {
                if (seis.getNsamp() < npts) {
                    npts = seis.getNsamp();
                }
                if (rate == null) {
                    rate = seis.getSamprate();
                }
                seis.RemoveMean();
                seis.Taper(2.0);
                double f1 = info.getPassBandLowFrequency();
                double f2 = info.getPassBandHighFrequency();
                int order = info.getPreprocessorFilterOrder();
                seis.filter(order, Passband.BAND_PASS, f1, f2, false);
                int decimationRate = processor.getParams().getDecimationRate();
                if (decimationRate > 1) {
                    seis.decimate(decimationRate);
                }
                waveforms.add(seis.getData());
            }
            float[][] tmp = new float[waveforms.size()][];
            for (int j = 0; j < waveforms.size(); ++j) {
                tmp[j] = waveforms.get(j);
            }
            X.add(tmp);
        }
        double floorFactor = 2.0;
        int decrate = 1;
        boolean refine = true;
        float SNRThreshold = (float) 2.5;
        double windowLengthSeconds = ProcessingPrescription.getInstance().getRewindowSlidingWindowLengthSeconds();
        double minLengthSeconds = ProcessingPrescription.getInstance().getReWindowMinWindowLengthSeconds();
        double preTriggerSeconds = ProcessingPrescription.getInstance().getRewindowPreTriggerSeconds();
        int nch = X.get(0).length;

        int analysisWindowLength = (int) (windowLengthSeconds * rate);
        int minimumWindowLength = (int) (minLengthSeconds * rate);
        int minDimensionForRefinement = 5;

        WindowSelector selector = new WindowSelector(X, nch, npts, analysisWindowLength, decrate, minimumWindowLength, refine, SNRThreshold, (float) floorFactor, minDimensionForRefinement);
        OnsetData onset = getSTALTAEnergyCaptureOnset(selector, rate, preTriggerSeconds, analysisWindowLength);
        if(onset == null)return null;

        SubspaceParameters params = new SubspaceParameters(originalDetector.getSpecification().getThreshold(),
                info.getEnergyCaptureThreshold(), originalDetector.getSpecification().getBlackoutPeriod());
        double offsetSecondsToWindowStart = preTriggerSeconds + onset.getWindowStart();
        double correlationWindowLength = onset.getWindowEnd() - onset.getWindowStart();
        String creationString = "Rewindowed In Framework";
        boolean fixSubspaceDimension = true;
        boolean capSubspaceDimension = true;
        int subspaceDimension = 1;
        String message = String.format("Analysis of detections results in start time adjustment of %f and duration of %f.", onset.getWindowStart(), correlationWindowLength);
        ApplicationLogger.getInstance().log(Level.INFO, message);

        Collection<StreamSegment> downSampledSegments = produceStreamSegmentCollection(data);
        SubspaceDetector newDetector = DetectionDAOFactory.getInstance().getSubspaceDetectorDAO().createAndSaveSubspaceDetector(processor,
                downSampledSegments,
                offsetSecondsToWindowStart,
                correlationWindowLength,
                params,
                fixSubspaceDimension,
                capSubspaceDimension,
                subspaceDimension,
                creationString,
                null);
        
        createNewTriggers(detections, newDetector.getdetectorid(), onset.getWindowStart(), correlationWindowLength);
        message = String.format("Created new detector %d.", newDetector.getdetectorid());
        ApplicationLogger.getInstance().log(Level.INFO, message);
        return newDetector;
    }

    private Collection<StreamSegment> produceStreamSegmentCollection(Map<Integer, ArrayList<BasicSeismogram>> data) {
        Collection<StreamSegment> downSampledSegments = new ArrayList<>();
        for (ArrayList<BasicSeismogram> seismograms : data.values()) {
            Collection<WaveformSegment> tmp = new ArrayList<>();
            for (BasicSeismogram bs : seismograms) {
                WaveformSegment ws = new WaveformSegment(bs);
                tmp.add(ws);
            }
            StreamSegment segment = new StreamSegment(tmp);
            downSampledSegments.add(segment);
        }
        return downSampledSegments;
    }

    private Map<Integer, ArrayList<BasicSeismogram>> extractDetectionSegments(Collection<Detection> detections) {
        Map<Integer, ArrayList<BasicSeismogram>> result = new HashMap<>();
        try {
            double preTriggerSeconds = ProcessingPrescription.getInstance().getRewindowPreTriggerSeconds();
            double analysisWindowLength = ProcessingPrescription.getInstance().getRewindowAnalysisWindowLengthSeconds();
            SourceData sd = SourceDataHolder.getInstance().getSourceData();
            for (Detection detection : detections) {
                int detectionid = detection.getDetectionid();
                double triggerTime = detection.getTriggerTime().epochAsDouble();

                Collection<NamedIntWaveform> segments = sd.getNamedWaveformCollection(triggerTime - preTriggerSeconds, triggerTime - preTriggerSeconds + analysisWindowLength);
                // Returned segments should be in proper order, but make sure by synchronizing with key list from the streamProcessor... 
                Map<StreamKey, NamedIntWaveform> keyWaveMap = new HashMap<>();
                for (NamedIntWaveform niw : segments) {
                    keyWaveMap.put(niw.getKey(), niw);
                }
                ArrayList<StreamKey> keys = processor.getChannels();

                ArrayList<BasicSeismogram> seismograms = new ArrayList<>();
                for (StreamKey key : keys) {
                    seismograms.add(new CssSeismogram(keyWaveMap.get(key)));
                }
                result.put(detectionid, seismograms);
            }

        } catch (DataAccessException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed to create segment collection for detections!", ex);
        }

        return result;
    }

    private void createNewTriggers(Collection<Detection> detections, int detectorid, double windowCorrection, double correlationWindowLength) throws DataAccessException {
        String substitutionReason = "RE-WINDOWED";
        for(Detection detection : detections){
            int existingTriggerid = detection.getTriggerid();
             DetectionDAOFactory.getInstance().getTriggerDAO().createSubstituteTrigger(existingTriggerid,substitutionReason,detectorid, windowCorrection, correlationWindowLength);
        }
    }

    private static class OnsetData {

        private final double windowStart;
        private final double windowEnd;
        private final double snr;

        public OnsetData(double windowStart, double snr, double windowEnd) {
            this.windowStart = windowStart;
            this.windowEnd = windowEnd;
            this.snr = snr;
        }

        public double getWindowStart() {
            return windowStart;
        }

        public double getSnr() {
            return snr;
        }

        public double getWindowEnd() {
            return windowEnd;
        }

        @Override
        public String toString() {
            return "OnsetData{" + "windowStart=" + windowStart + ", windowEnd=" + windowEnd + ", snr=" + snr + '}';
        }

    }

    private OnsetData getSTALTAEnergyCaptureOnset(WindowSelector selector, double rate, double preTrigSeconds, int analysisWindowLength) {

        double minLengthSeconds = ProcessingPrescription.getInstance().getReWindowMinWindowLengthSeconds();
        double maxLengthSeconds = ProcessingPrescription.getInstance().getReWindowMaxWindowLengthSeconds();
        // Get the energy capture trace
        float[] ecap = getEnergyCapture(selector);
        double ltaSeconds = preTrigSeconds / 2;
        double staSeconds = ltaSeconds / 20;
        int ltaWinLen = (int) Math.round(ltaSeconds * rate);
        int staWinLen = (int) Math.round(staSeconds * rate);

        //Create two "rolling-statistics" objects
        DescriptiveStatistics ltaStat = new DescriptiveStatistics(ltaWinLen);
        DescriptiveStatistics staStat = new DescriptiveStatistics(staWinLen);

        int staStartIndex = ltaWinLen;
        for (int j = 0; j < ecap.length - staWinLen; ++j) {
            // short-term window index leads by staWinLen samples.
            int k = j + staWinLen;
            ltaStat.addValue(ecap[j]);
            if (j >= staStartIndex) {
                staStat.addValue(ecap[k]);
            }
            if (j >= ltaWinLen) {
                double sta = staStat.getMean();
                double lta = ltaStat.getMean();
                double ltStd = ltaStat.getStandardDeviation();
                if (lta > 0) {
                    // Compute SNR for reporting purposes
                    double snr = staStat.getVariance() / ltaStat.getVariance();

                    //Trigger if STA > LTA + 5 times the standard deviation
                    if (sta > lta + 5 * ltStd) {
                        int triggerIndex = j - staWinLen + analysisWindowLength;
                        double windowStart = -preTrigSeconds + triggerIndex / rate + staSeconds / 2;
                        float[] tmp = SeriesMath.Add(ecap, -lta);
                        TimeBandwidthComponents tbc = SignalPairStats.computeTimeBandwidthProduct(tmp, 1 / rate);
                        double windowLength = tbc.getTimeCentroid() + tbc.getTimeSigma() - preTrigSeconds;
                        windowLength = Math.max(Math.min(windowLength, maxLengthSeconds), minLengthSeconds);
                        return new OnsetData(windowStart, snr, windowStart + windowLength);
                    }
                }
            }
        }
        return null;
    }

    private float[] getEnergyCapture(WindowSelector selector) throws IllegalStateException {
        float[] ecap = selector.getEnergyCapture2();
        if (ecap == null) {
            ecap = selector.getEnergyCapture1();
        }
        if (ecap == null) {
            throw new IllegalStateException("Energy Capture is not available!");
        }
        return ecap;
    }
}
