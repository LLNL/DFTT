package llnl.gnem.apps.detection.statistics.fileWriting;

import llnl.gnem.apps.detection.core.dataObjects.DetectorType;

import llnl.gnem.apps.detection.util.RunInfo;


import java.io.File;
import llnl.gnem.core.util.FileSystemException;

/**
 * Created by dodge1
 * Date: Oct 5, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class FileWriterFactory {
    public static StatFileWriter createFileWriter(File detStatDirectory, String streamName, String detectorName, double sampleInterval, DetectorType type, int detectorid) throws FileSystemException {

        int runid = RunInfo.getInstance().getRunid();
        File runidDetstatDirectory = createDirectoryForCurrentRun(detStatDirectory, runid);
                String name = String.format("%s_%s.detstat.sac", streamName, detectorName);
                File statsFile = new File(runidDetstatDirectory, name);
                return new SACFileStatsWriter(statsFile, sampleInterval, type, detectorid);
    }

    private static File createDirectoryForCurrentRun(File detStatDirectory, int runid) throws FileSystemException {
        File runidDetstatDirectory = new File(detStatDirectory, String.format("%05d", runid));
        if (!runidDetstatDirectory.exists()) {
            if (!runidDetstatDirectory.mkdirs()) {
                throw new FileSystemException(String.format("Failed to create directory: %s!", runidDetstatDirectory.getAbsolutePath()));
            }
        }
        return runidDetstatDirectory;
    }
}
