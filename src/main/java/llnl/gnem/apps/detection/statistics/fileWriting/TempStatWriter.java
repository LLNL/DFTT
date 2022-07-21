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

import java.io.File;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;

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
