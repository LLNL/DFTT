package llnl.gnem.apps.detection.streams;

import com.oregondsp.util.TimeStamp;
import llnl.gnem.apps.detection.core.dataObjects.Detection;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;

import llnl.gnem.apps.detection.core.dataObjects.Trigger;

import llnl.gnem.apps.detection.core.framework.StreamProcessor;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.array.FKScreenConfiguration;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import llnl.gnem.apps.detection.DetectionStatisticScanner;
import llnl.gnem.apps.detection.core.dataObjects.ArrayCorrelationParams;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;
import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.apps.detection.core.framework.DownSampler;
import llnl.gnem.apps.detection.core.framework.StreamTransformer;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.Projection;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.database.DetectionDAO;
import llnl.gnem.apps.detection.database.SubspaceDetectorDAO;
import llnl.gnem.apps.detection.database.TriggerDAO;
import llnl.gnem.apps.detection.statistics.fileWriting.DetectionStatisticWriter;
import llnl.gnem.apps.detection.tasks.CalculateDetectionStatisticTask;
import llnl.gnem.apps.detection.tasks.ComputationService;
import llnl.gnem.apps.detection.triggerProcessing.EvaluatedTrigger;
import llnl.gnem.apps.detection.triggerProcessing.TriggerData;
import llnl.gnem.apps.detection.triggerProcessing.TriggerManager;
import llnl.gnem.apps.detection.triggerProcessing.TriggerProcessor;
import llnl.gnem.apps.detection.util.DetectorSubstitution;
import llnl.gnem.apps.detection.util.StreamDataWriter;
import llnl.gnem.apps.detection.util.SubstitutionReason;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.apps.detection.sdBuilder.histogramDisplay.HistogramModel;
import llnl.gnem.apps.detection.util.SubspaceUpdateParams;

/**
 * Created by dodge1 Date: Oct 1, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ConcreteStreamProcessor implements StreamProcessor {

    protected final Set<StreamKey> processorChannels;
    protected final Map<StreamKey, WaveformSegment> accumulator;
    private final int streamid;
    private final Map<Integer, Detector> detectors;
    protected FKScreenConfiguration fkScreenConfiguration = null;
    private final PreprocessorParams params;
    private final String streamName;
    private final DetectionStatisticScanner statisticScanner;
    private final StreamSegmentCache streamSegmentCache;

    private final DownSampler downSampler;
    private final StreamTransformer streamTransformer;
    private final StreamDataWriter rawDataWriter;
    private final StreamDataWriter modifiedDataWriter;

    private long blocksProcessedCount = 0;
    Collection<Detector> mfDetectors;

    public ConcreteStreamProcessor(PreprocessorParams params,
            int streamid,
            Collection<StreamKey> channels,
            String streamName,
            double samplerate,
            int maxTemplateLength,
            boolean triggerOnlyOnCorrelators) {
        processorChannels = new HashSet<>();
        processorChannels.addAll(channels);
        accumulator = new TreeMap<>();
        this.streamid = streamid;
        detectors = new ConcurrentHashMap<>();
        this.params = params;
        this.streamName = streamName;
        statisticScanner = new DetectionStatisticScanner(triggerOnlyOnCorrelators, streamName);
        streamSegmentCache = new StreamSegmentCache();
        downSampler = new DownSampler(params, channels.size(), samplerate);
        streamTransformer = new StreamTransformer(params, channels.size(), maxTemplateLength);
        rawDataWriter = new StreamDataWriter(ProcessingPrescription.getInstance().isWriteRawTraces());
        rawDataWriter.initialize(ProcessingPrescription.getInstance().getRawTraceDirectory(),
                ProcessingPrescription.getInstance().getRawTraceChannels());
        modifiedDataWriter = new StreamDataWriter(ProcessingPrescription.getInstance().isWriteModifiedTraces());
        modifiedDataWriter.initialize(ProcessingPrescription.getInstance().getModifiedTraceDirectory(),
                ProcessingPrescription.getInstance().getModifiedTraceChannels());

        mfDetectors = new ArrayList<>();
    }

    public ConcreteStreamProcessor changeBlockSize(double blockSizeSeconds) {
        PreprocessorParams tmpParams = params.changeBlockSize(blockSizeSeconds);
        return new ConcreteStreamProcessor(tmpParams, streamid,
                this.getChannels(),
                streamName,
                params.getSampleRate(),
                streamTransformer.getMaxTemplateLength(),
                statisticScanner.isTriggerOnlyOnCorrelators());
    }

    public void addFKScreen(FKScreenConfiguration compositeScreen) {
        this.fkScreenConfiguration = compositeScreen;
    }

    @Override
    public String getStreamName() {
        return streamName;
    }

    @Override
    public int getFFTSize() {
        return streamTransformer.getFFTSize();
    }

    @Override
    public void processNewData() throws Exception {
        StreamSegment block = new StreamSegment(accumulator);

        accumulator.clear();
        maybeWriteStreamSegment(block);
        StreamSegment downSampled = downSampleBlock(block);
        // Apply in succession any algorithms that cause changes in the stream, e.g. signal cancellers
        for (StreamModifier modifier : StreamModifierManager.getInstance().getModifiers()) {
            modifier.put(downSampled);
            downSampled = modifier.take();
        }
        maybeWriteModifiedData(downSampled);

        TransformedStreamSegment transformed = this.transformBlock(downSampled);
        processBlock(transformed, block);
        ++blocksProcessedCount;
        if (blocksProcessedCount % StreamsConfig.getInstance().getStatsRefreshIntervalInBlocks(streamName) == 0) {
            ApplicationLogger.getInstance().log(Level.INFO, "Writing detection histogram data...");
            SubspaceDetectorDAO.getInstance().writeHistograms(getSubspaceDetectors());
            ApplicationLogger.getInstance().log(Level.INFO, "Done writing detection histogram data.");
            if (StreamsConfig.getInstance().isUseDynamicThresholds(streamName)) {
                ApplicationLogger.getInstance().log(Level.INFO, "Updating subspace detector thresholds...");
                updateSubspaceDetectorThresholds(getSubspaceDetectors());
                ApplicationLogger.getInstance().log(Level.INFO, "Done updating subspace detector thresholds.");
            }
        }

    }

    private void maybeWriteStreamSegment(StreamSegment block) {
        ApplicationLogger.getInstance().log(Level.FINE, String.format("Stream procesor %s is beginning pre-processing in thread %s.", toString(), Thread.currentThread()));
        try {
            rawDataWriter.maybeWriteStreamBlock(block);
        } catch (IOException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed writing raw data!", ex);
        }
    }

    private void processBlock(TransformedStreamSegment segment, StreamSegment rawBlock) {

        // Now push the fully modified segment into the cache. From there, the detectorList can access segments as they become available.
        streamSegmentCache.push(segment, rawBlock);
        ApplicationLogger.getInstance().log(Level.FINE, String.format("Computing detection statistics..."));
        computeAllDetectionStatistics(segment);
        //    updateHistograms();
        if (StreamsConfig.getInstance().isProduceTriggers(streamName)) {
            ApplicationLogger.getInstance().log(Level.FINE, String.format("Scanning for triggers..."));
            boolean useDynamicThreshold = StreamsConfig.getInstance().isUseDynamicThresholds(streamName);
            Collection<TriggerData> triggers = statisticScanner.scanForTriggers(useDynamicThreshold);
            if (!triggers.isEmpty() && streamSegmentCache.canProduceConcatenatedSegment()) {
                processAllTriggers(triggers);
            }
        }
    }

    private void maybeWriteModifiedData(StreamSegment segment) {
        try {
            modifiedDataWriter.maybeWriteStreamBlock(segment);
        } catch (IOException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed writing modified data!", ex);
        }
    }

    private void processAllTriggers(Collection<TriggerData> triggers) {
        double leadSeconds = 0;
        double lagSeconds = ProcessingPrescription.getInstance().getMaxTemplateLength();
        double snrThreshold = StreamsConfig.getInstance().getSnrThreshold(streamName);
        double minDuration = StreamsConfig.getInstance().getMinEventDuration(streamName);
        FKScreenParams fkScreenParams = StreamsConfig.getInstance().getFKScreenParams(streamName);
        StreamSegment processedStream = streamSegmentCache.getStream();
        StreamSegment rawStream = streamSegmentCache.getRawStream();
        Map<DetectorType, List<Trigger>> triggerMap = evaluateAndArchiveTriggers(triggers,
                leadSeconds,
                lagSeconds,
                processedStream,
                rawStream,
                fkScreenParams,
                snrThreshold,
                minDuration);
        processDetections(triggerMap, processedStream);
    }

    private void computeAllDetectionStatistics(TransformedStreamSegment segment) {
        ComputationService service = ComputationService.getInstance();
        int submitted = 0;
        for (Detector detector : detectors.values()) {
            CalculateDetectionStatisticTask task = new CalculateDetectionStatisticTask(detector, segment);
            service.getDetStatCompService().submit(task);
            ++submitted;
        }
        while (submitted > 0) {
            try {
                Future<DetectionStatistic> future = service.getDetStatCompService().take();
                DetectionStatistic statistic = future.get();
                maybeWriteStatistic(statistic);
                statisticScanner.addStatistic(statistic);

            } catch (InterruptedException ex) {
                Logger.getLogger(ConcreteStreamProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Failure occurred during detection statistic computation!", ex);
            }
            --submitted;
        }

    }

    private void processDetections(Map<DetectorType, List<Trigger>> triggerMap, StreamSegment compatibleStream) {
        try {
            if (!triggerMap.isEmpty()) {
                Collection<Detection> detections = TriggerManager.getInstance().processTriggersIntoDetections(triggerMap);
                detections.stream().forEach((detection) -> {
                    ApplicationLogger.getInstance().log(Level.INFO, detection.toString());
                });
                maybeSpawnNewdetectors(detections, compatibleStream);
            }
        } catch (Exception ex) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Failed creating detections!", ex);
        }
    }

    private Map<DetectorType, List<Trigger>> evaluateAndArchiveTriggers(Collection<TriggerData> triggers,
            double leadSeconds,
            double lagSeconds,
            StreamSegment processedStream,
            StreamSegment rawStream,
            FKScreenParams fkScreenParams,
            double snrThreshold,
            double minDuration) {

        boolean fixTemplateLengths = ProcessingPrescription.getInstance().isForceFixedTemplateLengths();
        double maxTemplateLength = ProcessingPrescription.getInstance().getMaxTemplateLength();
        List<Trigger> tmp = triggers.parallelStream()
                .filter(td -> td.isContained(processedStream, leadSeconds, lagSeconds))
                .map(td -> toEvaluatedTrigger(td,
                lagSeconds,
                processedStream,
                rawStream,
                fkScreenParams,
                snrThreshold,
                minDuration,
                fixTemplateLengths,
                maxTemplateLength))
                .map(et -> getDbTrigger(et))
                .filter(Objects::nonNull).collect(Collectors.toList());
        return tmp.isEmpty() ? new HashMap<>()
                : tmp.stream().collect(Collectors.groupingBy(Trigger::getDetectorType));
    }

    private Trigger getDbTrigger(EvaluatedTrigger evaluatedTrigger) {
        try {
            Trigger dbTrigger = TriggerDAO.getInstance().writeNewTrigger(evaluatedTrigger);
            return evaluatedTrigger.isUsable() ? dbTrigger : null;
        } catch (SQLException ex) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Failed inserting new Trigger!", ex);
            return null;
        }
    }

    private EvaluatedTrigger toEvaluatedTrigger(TriggerData td,
            double lagSeconds,
            StreamSegment processedStream,
            StreamSegment rawStream,
            FKScreenParams fkScreenParams,
            double snrThreshold,
            double minDuration,
            boolean fixTemplateLengths,
            double maxTemplateLength) {
        Detector detector = getDetector(td.getDetectorInfo().getDetectorid());
        String msg = String.format("Trigger at (%s) with statistic %f by detector %d", td.getTriggerTime(), td.getStatistic(), td.getDetectorInfo().getDetectorid());
        ApplicationLogger.getInstance().log(Level.FINE, msg);
        EvaluatedTrigger evaluatedTrigger = TriggerProcessor.processSingleTrigger(streamid, processedStream,
                rawStream, td, lagSeconds,
                fkScreenParams, snrThreshold, minDuration,
                fixTemplateLengths,
                maxTemplateLength,
                detector,
                fkScreenConfiguration);
        return evaluatedTrigger;
    }

    private void maybeWriteStatistic(DetectionStatistic statistic) {
        try {
            DetectionStatisticWriter.getInstance().appendData(statistic.getDetectorInfo().getDetectorName(), statistic.getStatistic(), statistic.getTime(), statistic.getDetectorInfo().getProcessingDelay());
        } catch (IOException ex) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Failed appending detection statistic!", ex);
        }
    }

    private void maybeSpawnNewdetectors(Collection<Detection> detections, StreamSegment compatibleStream) throws Exception {
        detections.parallelStream().forEach(d -> maybeSpawnOneDetector(d, compatibleStream));
    }

    private void maybeSpawnOneDetector(Detection detection, StreamSegment compatibleStream) {
        Detector detector = getDetector(detection);
        if (detector == null) {
            ApplicationLogger.getInstance().log(Level.WARNING, String.format("Failed to get detector for detection: %s", detection.toString()));
        } else {
            try {
                if (detector.getDetectorType().isSpawning()) {
                    if (StreamsConfig.getInstance().isSpawnCorrelationDetectors(streamName)) {
                        spawnNewCorrelator(detection, compatibleStream);
                    }
                } else if (detector instanceof SubspaceDetector && SubspaceUpdateParams.getInstance().isUpdateOnDetection()) {
                    updateDetector((SubspaceDetector) detector, detection, compatibleStream);
                }
            } catch (Exception ex) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Failed creating new detector!", ex);

            }
        }
    }

    public void addDetector(Detector detector) {
        detectors.put(detector.getdetectorid(), detector);
        maybeInitializeWriter(detector);
    }

    public void maybeInitializeWriter(Detector detector) {
        double sampleInterval = 1 / getParams().getPreprocessorParams().getDecimatedSampleRate();
        try {
            DetectionStatisticWriter.getInstance().initializeWriter(streamName, detector.getName(),
                    sampleInterval, detector.getDetectorType(), detector.getdetectorid());
        } catch (FileSystemException ex) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Failed initializing statistic writer!", ex);
        }
    }

    @Override
    public void addDetectors(Collection<? extends Detector> detectorCollection) {
        detectorCollection.stream().forEach((detector) -> {
            addDetector(detector);
        });
    }

    @Override
    public void maybeAddChannel(WaveformSegment channel) {

        StreamKey test = channel.getStreamKey();
        if (includesChannel(test)) {
            accumulator.put(test, channel);
        } else {
        }
    }

    private boolean includesChannel(StreamKey key) {
        return processorChannels.contains(key);
    }

    @Override
    public Collection<StreamKey> getChannels() {
        return new ArrayList<>(processorChannels);
    }

    @Override
    public int getStreamId() {
        return streamid;
    }

    @Override
    public Collection<SubspaceDetector> getSubspaceDetectors() {
        Collection<SubspaceDetector> result = new ArrayList<>();
        for (Detector detector : detectors.values()) {
            if (detector.getDetectorType() == DetectorType.SUBSPACE) {
                result.add((SubspaceDetector) detector);
            }
        }
        return result;
    }

    public Detector removeDetector(int detectorid) {
        Detector result = detectors.get(detectorid);
        detectors.remove(detectorid);

        return result;
    }

    @Override
    public Detector getDetector(Detection detection) {
        int detectorid = detection.getDetectorid();
        return detectors.get(detectorid);
    }

    public Detector getDetector(int detectorid) {
        return detectors.get(detectorid);
    }

    public PreprocessorParams getParams() {
        return params;
    }

    @Override
    public StreamSegment downSampleBlock(StreamSegment block) {
        return downSampler.process(block);
    }

    @Override
    public TransformedStreamSegment transformBlock(StreamSegment block) {
        return streamTransformer.transform(block);
    }

    private void spawnNewCorrelator(Detection detection, StreamSegment compatibleStream) throws Exception {
        try {
            DetectorSubstitution substitute = createAndAddSubspaceDetector(detection, compatibleStream);
            //Create a new trigger which is a clone of the one that  created this detector
            //but is otherwise appropriate for the new detector.
            //Then associate the detection with the new trigger.
            //The effect is that power detectorList never end up with detections.
            if (substitute != null) {
                DetectionDAO.getInstance().reassignDetection(detection, substitute);
            } else {
                ApplicationLogger.getInstance().log(Level.INFO, String.format(
                        "Detection (%s) could not be reassigned and remains with originating detector.",
                        detection.toString()));
            }
        } catch (Exception ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed creating new detector!", ex);
        }
    }

    private DetectorSubstitution createAndAddSubspaceDetector(Detection detection, StreamSegment segment) throws Exception {
        double templateLeadSeconds = 0;
        double templateLagSeconds = ProcessingPrescription.getInstance().getMaxTemplateLength();

        double signalDuration = detection.getSignalDuration();
        double duration = signalDuration == 0 ? templateLagSeconds : signalDuration;

        TimeStamp triggerTime = detection.getTriggerTime();
        SubspaceDetector detector = SubspaceDetectorDAO.getInstance().createDetectorFromStreamSegment(segment, triggerTime, templateLeadSeconds, duration, this);

        ApplicationLogger.getInstance().log(Level.INFO, "Created new detector: " + detector);
        this.addDetector(detector);
        Double shift = 0.0;
        return new DetectorSubstitution(detector, shift, 1.0, detection.getDetectorid(), SubstitutionReason.INITIAL_PATTERN_INSTANCE);
    }

    public void updateDetector(SubspaceDetector detector, Detection detection, StreamSegment segment) throws Exception {

        double updateThreshold = SubspaceUpdateParams.getInstance().getUpdateThreshold();
        if (detection.getMaxDetStat() < updateThreshold) {
            ApplicationLogger.getInstance().log(Level.FINE, String.format("Detector %d was not "
                    + "updated because detection statistic (%f) was less than update threshold of %f.",
                    detector.getdetectorid(), detection.getMaxDetStat(), updateThreshold));
            return;
        }

        int numPadSamples = 10;
        TimeStamp triggerTime = detection.getTriggerTime();
        SubspaceTemplate template = detector.getTemplate();

        int templateLength = template.getTemplateLength();

        TimeT segmentStart = segment.getStartTime();
        double sampleInterval = segment.getSampleInterval();
        int startIndex = (int) Math.round((triggerTime.epochAsDouble() - segmentStart.getEpochTime()) / sampleInterval) - numPadSamples;
        if (startIndex < 0) {
            throw new IllegalStateException("Requested template data starts before segment start!");
        }
        int endIndex = templateLength + startIndex - 1 + numPadSamples;
        if (endIndex >= segment.size()) {
            throw new IllegalStateException("Requested template data ends after segment end!");
        }

        int N = endIndex - startIndex + 1;

        int nch = segment.getNumChannels();
        float[][] preprocessedDataFromStream = createCompatibleRepresentation(template.getStaChanList(), N, segment, startIndex);

        ArrayList< float[]> registered = Projection.getRegisteredSegment(template, preprocessedDataFromStream);
        double lambda = SubspaceUpdateParams.getInstance().getLambda();
        double energyCapture = SubspaceUpdateParams.getInstance().getEnergyCapture();
        template.update(registered, energyCapture, lambda);
        SubspaceDetectorDAO.getInstance().updateTemplateInDB(detector.getdetectorid(), template);
    }

    private float[][] createCompatibleRepresentation(ArrayList< StreamKey> requestedChans, int N, StreamSegment segment, int startIndex) {

        int nch = requestedChans.size();
        float[][] preprocessedDataFromStream = new float[nch][N];

        int ich = 0;
        for (StreamKey key : requestedChans) {
            float[] streamData = segment.getChannelData(key);
            for (int i = 0; i < N; i++) {
                preprocessedDataFromStream[ich][i] = streamData[startIndex + i];
            }
            ich++;
        }

        return preprocessedDataFromStream;
    }

    public static ArrayCorrelationSpecification createArrayCorrelationSpecification(ConcreteStreamProcessor processor, ArrayCorrelationParams params, Collection<StreamSegment> eventSegments, double prepickSeconds, double correlationWindowLength) {

        ArrayList<StreamKey> streamChannels = new ArrayList<>(processor.getChannels());

        PreprocessorParams pparams = processor.getParams();

        ArrayCorrelationSpecification spec = new ArrayCorrelationSpecification(
                (float) params.getDetectionThreshold(),
                (float) params.getBlackoutSeconds(),
                prepickSeconds,
                correlationWindowLength,
                (float) params.getEnergyCapture(),
                params.getStaDuration(),
                params.getLtaDuration(),
                params.getStaLtaDelaySeconds(),
                streamChannels);

        return spec;
    }

    private void updateSubspaceDetectorThresholds(Collection<SubspaceDetector> subspaceDetectors) {
        subspaceDetectors.parallelStream().forEach(t -> HistogramModel.updateSingleDetectorThreshold(t));
    }

}
