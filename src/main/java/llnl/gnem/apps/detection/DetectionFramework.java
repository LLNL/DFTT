package llnl.gnem.apps.detection;

import llnl.gnem.apps.detection.database.StreamProcessorDAO;

import llnl.gnem.apps.detection.core.framework.StreamProcessor;
import llnl.gnem.apps.detection.database.StreamDAO;
import llnl.gnem.apps.detection.source.SourceData;
import llnl.gnem.apps.detection.streams.StreamServer;
import llnl.gnem.apps.detection.tasks.ComputationService;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.apps.detection.util.RunStatsReporter;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.database.SubspaceDetectorDAO;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Sep 27, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DetectionFramework {

    private final StreamServer server;
    private final Collection<StreamProcessor> processors;

    public DetectionFramework(SourceData source, double primaryBufferSize) {
        server = new StreamServer(source, primaryBufferSize);
        processors = new ArrayList<>();
    }

    public void initialize() throws Exception {
        server.initialize();

        Map<String, Boolean> streamTriggeringMap = buildStreamTriggerMap();
        double sampleRate = server.getCommonSampleRate();
        
        String configName = ProcessingPrescription.getInstance().getConfigName();
        processors.addAll(StreamProcessorDAO.getInstance().constructProcessors(configName, sampleRate,streamTriggeringMap));
        for (StreamProcessor processor : processors) {
            server.addStreamProcessor(processor);
            if (ProcessingPrescription.getInstance().isCreateConfiguration()) {
                int streamid = processor.getStreamId();
                String streamName = processor.getStreamName();
                StreamDAO.getInstance().writeStreamParamsIntoConfiguration(streamid, streamName, sampleRate);
            }
        }
    }

    private Map<String, Boolean> buildStreamTriggerMap() {
        Collection<String> streams = StreamsConfig.getInstance().getStreamNames();
        Map<String, Boolean> streamTriggeringMap = new HashMap<>();
        for( String stream : streams){
            boolean triggerOnlyOnCorrelators = StreamsConfig.getInstance().isTriggerOnlyOnCorrelators(stream);
            streamTriggeringMap.put(stream, triggerOnlyOnCorrelators);
        }
        return streamTriggeringMap;
    }

    public void run() throws Exception {
        long start = System.currentTimeMillis();
        while (server.hasMoreData()) {
           if (server.advance()) {
                processAllStreams();
            }
        }
        ApplicationLogger.getInstance().log(Level.INFO,"No more data available to DetectionFramework.");
        server.shutdown();
        
        ApplicationLogger.getInstance().log(Level.INFO, String.format("Writing histograms..."));
        for (StreamProcessor processor : processors) {
            Collection<SubspaceDetector> detectors = processor.getSubspaceDetectors();
            SubspaceDetectorDAO.getInstance().writeHistograms(detectors);
        }

        RunStatsReporter.reportAllDetections();
        RunStatsReporter.reportDetectionSummary();
        long end = System.currentTimeMillis();
        ApplicationLogger.getInstance().log(Level.INFO, String.format("Processed input in %7.2f seconds", (end - start) / 1000.0));
        RunInfo.getInstance().logEndTime();
        int runid = RunInfo.getInstance().getRunid();
        ApplicationLogger.getInstance().log(Level.INFO, String.format("This was Runid %d", runid));
    }

    private void processAllStreams() {
        for (StreamProcessor processor : processors) {
            try {
                processor.processNewData();
            } catch (Exception ex) {
                ApplicationLogger.getInstance().log(Level.SEVERE, "Failed processing block!", ex);
            } 
        }

    }


    public void close() throws IOException, SQLException, InterruptedException {
        server.close();
        ComputationService service = ComputationService.getInstance();
        service.shutdown();
    }

}
