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
package llnl.gnem.apps.detection.util;

import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import java.io.IOException;

import java.util.logging.Level;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;

import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.util.ApplicationLogger;

public class RunInfo {

    private static final RunInfo INSTANCE = new RunInfo();
    private int runid = -1;

    public static RunInfo getInstance() {
        return INSTANCE;
    }

    public void initialize(Integer runidToResume, String commandLineArgs) throws IOException, DataAccessException {
        if (runid > 0) {
            ApplicationLogger.getInstance().log(Level.FINE, "RunInfo already initialized with runid = " + runid);
            return;
        }

        String configName = ProcessingPrescription.getInstance().getConfigName();
        ApplicationLogger.getInstance().log(Level.FINE, "Obtaining runid for configuration: " + configName);
        if (runidToResume != null){
            ApplicationLogger.getInstance().log(Level.FINE, "Retrieving run information for configuration using runid = " + runidToResume);
        }
        if (runidToResume != null && runidToResume > 0 && DetectionDAOFactory.getInstance().getFrameworkRunDAO().isConsistentRunid(runidToResume, configName)) {
            ApplicationLogger.getInstance().log(Level.FINE, "Resuming previous run: " + runidToResume);
            runid = runidToResume;
        } else {
            ApplicationLogger.getInstance().log(Level.FINE, "Creating new run entry for configuration: " + configName+"...");
            runid = DetectionDAOFactory.getInstance().getFrameworkRunDAO().createFrameworkRunEntry(configName, commandLineArgs);
            ApplicationLogger.getInstance().log(Level.FINE, "Created new run entry for runid: " + runid);
        }

    }

    private RunInfo() {
    }

    public int getRunid() {
        return runid;
    }

    public void logEndTime() throws DataAccessException {
        DetectionDAOFactory.getInstance().getFrameworkRunDAO().logEndTime(runid);
    }
}
