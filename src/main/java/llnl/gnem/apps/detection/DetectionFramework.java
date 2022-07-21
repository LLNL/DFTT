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
package llnl.gnem.apps.detection;



import llnl.gnem.apps.detection.core.framework.StreamProcessor;

import llnl.gnem.apps.detection.source.SourceData;
import llnl.gnem.apps.detection.streams.StreamServer;
import llnl.gnem.apps.detection.tasks.ComputationService;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.apps.detection.util.RunStatsReporter;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;


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
         if (!ProcessingPrescription.getInstance().isCreateConfiguration() && ProcessingPrescription.getInstance().isReplaceBulletinDetector()) {
             int streamid = ProcessingPrescription.getInstance().getTargetBulletinDetectorStreamid();
             DetectionDAOFactory.getInstance().getBulletinDetectorDAO().maybeReplaceBulletinDetector(streamid);
         }
        
        processors.addAll(DetectionDAOFactory.getInstance().getStreamProcessorDAO().constructProcessors(configName, sampleRate,streamTriggeringMap));
        for (StreamProcessor processor : processors) {
            server.addStreamProcessor(processor);
            if (ProcessingPrescription.getInstance().isCreateConfiguration()) {
                int streamid = processor.getStreamId();
                String streamName = processor.getStreamName();
                DetectionDAOFactory.getInstance().getStreamDAO().writeStreamParamsIntoConfiguration(streamid, streamName, sampleRate);
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
            DetectionDAOFactory.getInstance().getSubspaceDetectorDAO().writeHistograms(detectors);
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


    public void close() throws IOException, DataAccessException, InterruptedException {
        ComputationService service = ComputationService.getInstance();
        service.shutdown();
    }

}
