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
package llnl.gnem.apps.detection.statistics.fileWriting;


import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;

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
