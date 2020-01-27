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
package llnl.gnem.apps.detection.core.dataObjects;

import com.oregondsp.util.DirectoryListing;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Level;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.StreamKey;


/*
 * This class holds additional parameters needed to create the empirically-derived detectors:  SubspaceDetector and 
 * MatchedFieldDetector.  The principal addition is machinery to specify event data in flat files.
 */
public abstract class AbstractEmpiricalSpecification extends AbstractSpecification implements EmpiricalDetectorSpecification, Serializable {

    protected String eventDataPath;
    protected String eventDirectoryPattern;
    protected String eventFilePattern;
    protected double offsetSecondsToWindowStart;
    protected double windowDurationSeconds;
    protected String[] eventDirectoryList;
    static final long serialVersionUID = 3426077835743588816L;

    /*
     * Constructor to support instantiation of empirical detectors from flat file data
     */
    public AbstractEmpiricalSpecification(InputStream stream) throws IOException {

        super(stream);

        String tmp = parameterList.getProperty("eventDataPath");
        File tmpFile = new File(tmp);
        if (!tmpFile.isAbsolute()) {
            String curDir = System.getProperty("user.dir");
            tmpFile = new File(curDir, tmp);
        }
        eventDataPath = tmpFile.getAbsolutePath();
        eventDirectoryPattern = parameterList.getProperty("eventDirectoryPattern");
        eventFilePattern = parameterList.getProperty("eventFilePattern");
        offsetSecondsToWindowStart = Double.parseDouble(parameterList.getProperty("templateStart", "0.0"));
        windowDurationSeconds = Double.parseDouble(parameterList.getProperty("templateDuration", "60.0"));

        // get event list
        if (eventDataPath == null) {
            throw new IllegalStateException("Empirical templates constructed from files require eventDataPath to be specified!");
        }
        if (eventDirectoryPattern == null) {
            throw new IllegalStateException("Empirical templates constructed from files require eventDirectoryPattern to be specified!");
        }
        if (eventFilePattern == null) {
            throw new IllegalStateException("Empirical templates constructed from files require eventFilePattern to be specified!");
        }

        DirectoryListing D = new DirectoryListing(eventDataPath, eventDirectoryPattern);

        eventDirectoryList = new String[D.nSubdirectories()];
        if (eventDirectoryList.length < 1) {
            throw new IllegalStateException("Failed to find any event directories using pattern: " + eventDirectoryPattern);
        }
        for (int i = 0; i < D.nSubdirectories(); i++) {
            eventDirectoryList[i] = eventDataPath + File.separator + D.subDirectory(i);
        }

        ApplicationLogger.getInstance().log(Level.FINE,
                String.format("Identified %d template events in directory (%s) using pattern (%s)",
                        eventDirectoryList.length,
                        eventDataPath,
                        eventDirectoryPattern));

    }

    /*
     * Constructor to support instantiation of detectors stored in the database.
     */
    public AbstractEmpiricalSpecification(float threshold,
            float blackoutPeriod,
            Collection< StreamKey> staChanList,
            double offsetSecondsToWindowStart,
            double windowDurationSeconds) {

        super(threshold, blackoutPeriod, staChanList);

        this.offsetSecondsToWindowStart = offsetSecondsToWindowStart;
        this.windowDurationSeconds = windowDurationSeconds;

    }

    @Override
    public String[] getEventDirectoryList() {
        return eventDirectoryList;
    }

    public String getEventFilePattern() {
        return eventFilePattern;
    }

    @Override
    public double getOffsetSecondsToWindowStart() {
        return offsetSecondsToWindowStart;
    }

    @Override
    public double getWindowDurationSeconds() {
        return windowDurationSeconds;
    }

    public static void printSpecificationTemplate(PrintStream ps) {

        AbstractSpecification.printSpecificationTemplate(ps);

        ps.println("eventDataPath         = <path>");
        ps.println("eventDirectoryPattern = <regex>");
        ps.println("eventFilePattern      = <regex>");
        ps.println("templateStart         = <offset from file start (sec)>");
        ps.println("templateDuration      = <window duration (sec)>");

    }

    @Override
    public void printSpecification(PrintStream ps) {

        super.printSpecification(ps);

        ps.println();
        ps.println("eventDataPath         = " + eventDataPath);
        ps.println("eventDirectoryPattern = " + eventDirectoryPattern);
        if (eventDirectoryList != null) {
            ps.println("event directories: ");
            for (String eventDirectoryList1 : eventDirectoryList) {
                ps.println("    " + eventDirectoryList1);
            }
        }
        ps.println("eventFilePattern      = " + eventFilePattern);
        ps.println("templateStart         = " + offsetSecondsToWindowStart);
        ps.println("templateDuration      = " + windowDurationSeconds);

    }
    
    
    @Override
    public boolean isArraySpecification() {
        return false;
    }

}
