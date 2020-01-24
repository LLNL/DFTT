package llnl.gnem.apps.detection.statistics.fileWriting;





import java.io.IOException;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.util.TimeT;

/**
 * Created by dodge1
 * Date: Oct 5, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public interface StatFileWriter {
    void initialize() throws FileSystemException;
    void appendData(float[] detectionStatistic, TimeT segmentStartTime, double timeCorrection) throws IOException;
}
