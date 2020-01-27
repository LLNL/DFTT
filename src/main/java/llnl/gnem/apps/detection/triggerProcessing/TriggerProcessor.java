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
package llnl.gnem.apps.detection.triggerProcessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import llnl.gnem.apps.detection.classify.ClassifierManager;
import llnl.gnem.apps.detection.classify.DetectorClassifier;
import llnl.gnem.apps.detection.classify.LabeledFeature;
import llnl.gnem.apps.detection.classify.TriggerClassification;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;

import llnl.gnem.apps.detection.core.framework.FKScreen;
import llnl.gnem.apps.detection.core.framework.FKScreenResults;
import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;
import llnl.gnem.apps.detection.core.framework.detectors.array.FKScreenConfiguration;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.seriesMathHelpers.SampleStatistics;
import llnl.gnem.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author dodge
 */
public class TriggerProcessor {

    public static EvaluatedTrigger processSingleTrigger(int streamid,
            StreamSegment stream,
            StreamSegment rawStream,
            TriggerData triggerDataIn,
            double lagSeconds,
            FKScreenParams fkScreenParams,
            double snrThreshold,
            double minDuration,
            boolean forceFixedTemplateLength,
            double fixedTemplateLength,
            Detector detector,
            FKScreenConfiguration fkConfig) {

        TriggerData td = triggerDataIn;

        DetectorInfo di = td.getDetectorInfo();
        Collection<WaveformSegment> rawSegments = rawStream.getWaveforms(di.getStaChanList());
        ArrayList<WaveformSegment> segments = stream.getWaveforms(di.getStaChanList());
        double relativeAmplitude = -999.0;
        if (di.getDetectorType() == DetectorType.SUBSPACE) {
            DetectorSpecification spec = di.getSpecification();
            if (spec instanceof SubspaceSpecification) {
                SubspaceSpecification sspec = (SubspaceSpecification) spec;
                lagSeconds = Math.min(lagSeconds, sspec.getWindowDurationSeconds());
                relativeAmplitude = calculateRelativeAmplitude(detector, segments, td, lagSeconds);
            }

        }
        FeatureCollection featureCollection = produceFeatureCollection(segments, td, lagSeconds);
        Double medianDuration = featureCollection.getMedianValue(SeismogramFeatures.FeatureType.SIGNAL_LENGTH);

        LabeledFeature feature = produceTriggerFeatures(rawSegments, td, lagSeconds, featureCollection);
        TriggerClassification classification = TriggerClassification.GOOD;
        try {
            if (detector.getDetectorType().isSpawning()) {
                DetectorClassifier classifier = ClassifierManager.getInstance().getClassifier(streamid);
                if (classifier.isClassifierUsable()) {
                    classification = classifier.classifyTrigger(feature);
                    if (classification != TriggerClassification.GOOD) {
                        String msg = String.format("Trigger at(%s) failed Classifier screen. Classified Type is %s", td.getTriggerTime().toString(), classification.toString());
                        ApplicationLogger.getInstance().log(Level.FINE, msg);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(TriggerProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        double snr = feature.getSnr();
        boolean snrIsOK = (snr >= snrThreshold && snr < 50000) || isExemptFromScreen(td);

        if (!snrIsOK && classification == TriggerClassification.GOOD) {
            String msg = String.format("Trigger at(%s) failed SNR screen. Median SNR was %8.2f", td.getTriggerTime().toString(), snr);
            ApplicationLogger.getInstance().log(Level.FINE, msg);
        }
        boolean durationOK = medianDuration != null && medianDuration >= minDuration || isExemptFromScreen(td);
        if (!durationOK && classification == TriggerClassification.GOOD) {
            String msg = String.format("Trigger at(%s) failed Duration screen. Median duration was %4.1f s", td.getTriggerTime().toString(), medianDuration);
            ApplicationLogger.getInstance().log(Level.FINE, msg);
        }

        FKScreenResults fkResults = null;
 //       FKScreenConfiguration fkConfig = di.createFKScreen(fkScreenParams);

        boolean velocityOK = true;
        FKStatus fkStatus = FKStatus.NOT_PERFORMED;
        if (fkConfig != null && fkConfig.isComputeFKOnTriggers()) {
            try {
                fkConfig.buildArrays(segments, new TimeT(td.getTriggerTime().epochAsDouble()));
                fkResults = FKScreen.computeFKScreenResults((float) fkConfig.getsMax(),
                        fkConfig.getdNorth(),
                        fkConfig.getdEast(),
                        fkConfig.getWaveforms(),
                        (float) fkConfig.getDelta(),
                        fkConfig.getFreqLimits(),
                        fkConfig.getSlownessVector(),
                        (float) fkConfig.getSlowTol(),
                        (float) fkConfig.getMinFKQual());
                fkStatus = FKStatus.IGNORED;
                if (!fkResults.isPassed() && fkConfig.isScreenTrigger(td.getDetectorInfo().getDetectorType())) {
                    ApplicationLogger.getInstance().log(Level.FINE,
                            String.format("Failed: Velocity = %f, Azimuth = %f, Quality = %f",
                                    fkResults.getVelocity(), fkResults.getAzimuth(), fkResults.getQuality()));
                }
                if (fkConfig.isScreenTrigger(td.getDetectorInfo().getDetectorType())) {
                    fkStatus = fkResults.isPassed() ? FKStatus.PASSED : FKStatus.FAILED;
                }
                if (fkConfig.isRequireMinimumVelocity() || fkConfig.isRequireMaximumVelocity()) {
                    double velocity = fkResults.getVelocity();
                    if (fkConfig.isRequireMinimumVelocity() && velocity < fkConfig.getMinAllowableVelocity()) {
                        velocityOK = false;
                        ApplicationLogger.getInstance().log(Level.FINE, String.format("Failed: Velocity = %f", fkResults.getVelocity()));
                    }
                    if (fkConfig.isRequireMaximumVelocity() && velocity > fkConfig.getMaxAllowableVelocity()) {
                        velocityOK = false;
                        ApplicationLogger.getInstance().log(Level.FINE, String.format("Failed: Velocity = %f", fkResults.getVelocity()));
                    }
                }
            } catch (Exception ex) {
                ApplicationLogger.getInstance().log(Level.FINE, "Failed computing FK!");
            }

        }
        boolean usableTrigger
                = classification == TriggerClassification.GOOD
                && velocityOK
                && durationOK
                && snrIsOK
                && !(fkStatus == FKStatus.FAILED);
        return new EvaluatedTrigger(td, usableTrigger, fkStatus, feature, medianDuration,
                fkResults, snrIsOK, durationOK, velocityOK, forceFixedTemplateLength,
                fixedTemplateLength, relativeAmplitude);
    }

    private static LabeledFeature produceTriggerFeatures(Collection<WaveformSegment> rawSegments, TriggerData td, double lagSeconds, FeatureCollection featureCollection) {
        PairT<Double, Double> kAndS = getKurtosisAndSkewness(rawSegments, td, lagSeconds);
        Double medianSnr = featureCollection.getMedianValue(SeismogramFeatures.FeatureType.SNR);
        Double medianTB = featureCollection.getMedianValue(SeismogramFeatures.FeatureType.TBP);
        Double medianFreqSigma = featureCollection.getMedianValue(SeismogramFeatures.FeatureType.FREQ_SIGMA);
        Double medianFreqCentroid = featureCollection.getMedianValue(SeismogramFeatures.FeatureType.FREQ_CENTROID);
        Double rawKurtosis = kAndS.getFirst();
        Double rawSkewness = kAndS.getSecond();
        Double medianTemporalSkewness = featureCollection.getMedianValue(SeismogramFeatures.FeatureType.TEMPORAL_SKEWNESS);
        LabeledFeature feature = new LabeledFeature(null,
                medianSnr,
                featureCollection.getMedianValue(SeismogramFeatures.FeatureType.AMPLITUDE),
                featureCollection.getMedianValue(SeismogramFeatures.FeatureType.TIME_CENTROID),
                featureCollection.getMedianValue(SeismogramFeatures.FeatureType.TIME_SIGMA),
                medianTemporalSkewness,
                featureCollection.getMedianValue(SeismogramFeatures.FeatureType.TEMPORAL_SKEWNESS),
                medianFreqSigma,
                medianTB,
                kAndS.getSecond(),
                kAndS.getFirst(),
                rawSkewness,
                rawKurtosis,
                medianFreqCentroid);
        return feature;
    }

    private static double calculateRelativeAmplitude(Detector detector, ArrayList<WaveformSegment> segments, TriggerData td, double lagSeconds) {
        SubspaceDetector ssd = (SubspaceDetector) detector;
        SubspaceTemplate template = ssd.getTemplate();
        ArrayList<float[][]> rep = template.getRepresentation();
        float[][] dim0 = rep.get(0);
        if (dim0.length != segments.size()) {
            ApplicationLogger.getInstance().log(Level.FINE, "Could not measure relative amplitude because of template-data mismatch!");
            return -999.0;
        }
        double relativeAmplitude = 0;
        float[] channelRelAmp = new float[segments.size()];
        for (int j = 0; j < segments.size(); ++j) {
            WaveformSegment seg = segments.get(j);
            float[] singleChannelTemplate = dim0[j];
            double pickTime = td.getTriggerTime().epochAsDouble();
            Epoch window = new Epoch(pickTime, pickTime + lagSeconds);
            TimeSeries copy = seg.crop(window);
            float[] data = copy.getData();
            int N = Math.min(singleChannelTemplate.length, data.length);
            float[] x = new float[N];
            float[] y = new float[N];
            System.arraycopy(singleChannelTemplate, 0, x, 0, N);
            System.arraycopy(data, 0, y, 0, N);
            normalizeX(x);
            double mx = getMaxCorrelation(x, y);
            channelRelAmp[j] = (float) mx;
        }
        relativeAmplitude = SeriesMath.getMedian(channelRelAmp);
        return relativeAmplitude;
    }

    private static double getMaxCorrelation(float[] x, float[] y) {
        double[] cc = SeriesMath.crosscorrelate(x, y);
        double mx = -Float.MAX_VALUE;
        for (int m = 0; m < cc.length; ++m) {
            if (cc[m] > mx) {
                mx = cc[m];
            }
        }
        return mx;
    }

    private static void normalizeX(float[] x) {
        double sum = 0;
        int N = x.length;
        for (int k = 0; k < N; ++k) {
            sum += x[k] * x[k];
        }
        sum = Math.sqrt(sum);
        for (int k = 0; k < N; ++k) {
            x[k] /= sum;
        }
    }

    private static PairT<Double, Double> getKurtosisAndSkewness(Collection<WaveformSegment> segments, TriggerData td, double lagSeconds) {
        double meanKurtosis = 0;
        double meanSkewness = 0;
        double pickEpochTime = td.getTriggerTime().epochAsDouble();
        int n = 0;
        for (WaveformSegment ws : segments) {
            Epoch window = new Epoch(pickEpochTime, pickEpochTime + lagSeconds);
            TimeSeries copy = ws.crop(window);
            SampleStatistics ss = new SampleStatistics(copy.getData());
            meanKurtosis += ss.getKurtosis();
            meanSkewness += ss.getSkewness();
            ++n;
        }
        meanKurtosis /= n;
        meanSkewness /= n;
        return new PairT<>(meanKurtosis, meanSkewness);
    }

    private static FeatureCollection produceFeatureCollection(Collection<WaveformSegment> segments, TriggerData td, double lagSeconds) {
        List<PairT<StreamKey, SeismogramFeatures>> pairs = segments.parallelStream().map(s -> toPair(s, td, lagSeconds)).collect(Collectors.toList());
        return new FeatureCollection(pairs);
    }

    private static PairT<StreamKey, SeismogramFeatures> toPair(WaveformSegment ws, TriggerData td, double lagSeconds) {
        return new PairT<>(ws.getStreamKey(), new SeismogramFeatures(ws, td.getTriggerTime().epochAsDouble(), lagSeconds));
    }

    private static boolean isExemptFromScreen(TriggerData td) {
        return td.getDetectorInfo().getDetectorType() == DetectorType.SUBSPACE;
    }
}
