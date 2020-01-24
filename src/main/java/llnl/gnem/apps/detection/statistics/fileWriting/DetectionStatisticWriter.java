package llnl.gnem.apps.detection.statistics.fileWriting;


import llnl.gnem.apps.detection.core.dataObjects.DetectorType;

import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.util.TimeT;

/**
 * Created by dodge1
 * Date: Oct 4, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class DetectionStatisticWriter {

    private final boolean isWriteStatistics;
    private final File detStatDirectory;
    private final Map<String, StatFileWriter> detectorWriterMap;

    public void initializeWriter(String streamName, String detectorName, double sampleInterval, DetectorType type, int detectorid) throws FileSystemException {
        if (isWriteStatistics && !detectorWriterMap.containsKey(detectorName)) {
            StatFileWriter writer = FileWriterFactory.createFileWriter(detStatDirectory, streamName, detectorName, sampleInterval, type, detectorid);
            if (writer != null) {
                writer.initialize();
                detectorWriterMap.put(detectorName, writer);
            }
        }
    }

    public void appendData(String detectorName, float[] detectionStatistic, TimeT segmentStartTime, double timeCorrection) throws IOException {
        if (isWriteStatistics) {
            StatFileWriter writer = detectorWriterMap.get(detectorName);
            if (writer != null)
                writer.appendData(detectionStatistic, segmentStartTime, timeCorrection);
        }
    }

    private static class DetectionStatisticWriterHolder {
        private static final DetectionStatisticWriter instance = new DetectionStatisticWriter();
    }

    public static DetectionStatisticWriter getInstance() {
        return DetectionStatisticWriterHolder.instance;
    }

    private DetectionStatisticWriter() {
        detectorWriterMap = new ConcurrentHashMap<>();
        isWriteStatistics = ProcessingPrescription.getInstance().isWriteDetectionStatistics();
        detStatDirectory = ProcessingPrescription.getInstance().getDetectionStatisticDirectory();
    }
}
