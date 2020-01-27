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

import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.core.io.SAC.SACFileWriter;

import llnl.gnem.core.util.TimeT;

import java.io.File;
import java.io.IOException;

/**
 * Created by dodge1 Date: Oct 13, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class SACFileStatsWriter extends BaseFileWriter {

    private final double sampleInterval;
    private SACFileWriter writer;
    private final DetectorType detectorType;
    private final int detectorid;

    public SACFileStatsWriter(File file, double sampleInterval, DetectorType type, int detectorid) {
        super(file);
        this.sampleInterval = sampleInterval;
        this.detectorType = type;
        this.detectorid = detectorid;
    }

    @Override
    public void appendData(float[] detectionStatistic, TimeT segmentStartTime, double timeCorrection) throws IOException {

        if (writer == null) {
            writer = new SACFileWriter(file);
            setHeaderVariables(segmentStartTime);
            writeShiftedBuffer(timeCorrection, detectionStatistic);
        } else {
            writer.reOpen();
            writer.writeFloatArray(detectionStatistic);
            writer.close();
        }
    }

    private void writeShiftedBuffer(double timeCorrection, float[] detectionStatistic) throws IOException {
        int delaySamples = (int) Math.round(timeCorrection / sampleInterval);
        float[] tmp = new float[detectionStatistic.length - delaySamples];
        System.arraycopy(detectionStatistic, delaySamples, tmp, 0, detectionStatistic.length - delaySamples);
        writer.writeFloatArray(tmp);

        writer.close();
    }

    private void setHeaderVariables(TimeT segmentStartTime) {
        writer.header.delta = (float) sampleInterval;
        writer.header.b = 0.0f;
        writer.header.kstnm = detectorType.getShortName();
        String chan = String.format("%d", detectorid);
        int len = chan.length();
        if (len > 8) {
            len = 8;
        }
        writer.header.kcmpnm = chan.substring(0, len);
        writer.setTime(new TimeT(segmentStartTime.getEpochTime()));
    }
}
