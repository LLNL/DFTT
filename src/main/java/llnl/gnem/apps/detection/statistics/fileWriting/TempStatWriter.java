package llnl.gnem.apps.detection.statistics.fileWriting;

import java.io.File;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.util.TimeT;

/**
 * Created by dodge1 Date: Oct 4, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class TempStatWriter {

    private final Map<String, StatFileWriter> detectorWriterMap;

    private StatFileWriter initializeWriter(String name, double sampleInterval, DetectorType type, int detectorid) throws FileSystemException {
        File statsFile = new File(name);
        StatFileWriter writer = new SACFileStatsWriter(statsFile, sampleInterval, type, detectorid);
        writer.initialize();
        detectorWriterMap.put(name, writer);
        System.out.println("Writer map size = " + detectorWriterMap.size());
        return writer;
    }

    public void appendData(String fileName, float[] detectionStatistic, TimeT segmentStartTime, 
            double timeCorrection, double sampleInterval, DetectorType type, int detectorid) throws IOException {

        StatFileWriter writer = detectorWriterMap.get(fileName);
        if (writer != null) {
            writer.appendData(detectionStatistic, segmentStartTime, timeCorrection);
        } else {
            writer = initializeWriter(fileName, sampleInterval, type, detectorid);
            writer.appendData(detectionStatistic, segmentStartTime, timeCorrection);
        }

    }

    private static class DetectionStatisticWriterHolder {

        private static final TempStatWriter instance = new TempStatWriter();
    }

    public static TempStatWriter getInstance() {
        return DetectionStatisticWriterHolder.instance;
    }

    private TempStatWriter() {
        detectorWriterMap = new ConcurrentHashMap<>();
    }
}
