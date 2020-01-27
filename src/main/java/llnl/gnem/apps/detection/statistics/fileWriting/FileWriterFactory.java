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
