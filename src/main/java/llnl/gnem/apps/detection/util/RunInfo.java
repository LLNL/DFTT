/**
 * Created by dodge1
 * Date: Oct 4, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
package llnl.gnem.apps.detection.util;

import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import llnl.gnem.apps.detection.database.FrameworkRunDAO;
import llnl.gnem.core.util.ApplicationLogger;

public class RunInfo {

    private static final RunInfo INSTANCE = new RunInfo();
    private int runid = -1;

    public static RunInfo getInstance() {
        return INSTANCE;
    }

    public void initialize(Integer runidToResume, String wfdiscTable, String commandLineArgs, double fixedRawSampleRate) throws SQLException, IOException {
        if (runid > 0) {
            ApplicationLogger.getInstance().log(Level.FINE, "RunInfo already initialized with runid = " + runid);
            return;
        }

        String configName = ProcessingPrescription.getInstance().getConfigName();
        ApplicationLogger.getInstance().log(Level.FINE, "Obtaining runid for configuration: " + configName);
        if (runidToResume != null){
            ApplicationLogger.getInstance().log(Level.FINE, "Retrieving run information for configuration using runid = " + runidToResume);
        }
        if (runidToResume != null && runidToResume > 0 && FrameworkRunDAO.getInstance().isConsistentRunid(runidToResume, configName)) {
            ApplicationLogger.getInstance().log(Level.FINE, "Resuming previous run: " + runidToResume);
            runid = runidToResume;
        } else {
            ApplicationLogger.getInstance().log(Level.FINE, "Creating new run entry for configuration: " + configName+"...");
            runid = FrameworkRunDAO.getInstance().createFrameworkRunEntry(wfdiscTable, configName, commandLineArgs, fixedRawSampleRate);
            ApplicationLogger.getInstance().log(Level.FINE, "Created new run entry for runid: " + runid);
        }

    }

    private RunInfo() {
    }

    public int getRunid() {
        return runid;
    }

    public void logEndTime() throws SQLException {
        FrameworkRunDAO.getInstance().logEndTime(runid);
    }
}
