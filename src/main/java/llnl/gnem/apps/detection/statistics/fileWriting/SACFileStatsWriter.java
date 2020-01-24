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
