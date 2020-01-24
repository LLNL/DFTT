/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class FrameworkRun {

    private final int runid;
    private final Date runDate;
    private final String wfdisc;
    private final int configid;
    private final byte[] configFileText;
    private final String commandLine;
    private final Date endDate;
    private final double fixedRawSampleRate;

    public FrameworkRun(int runid,
            Date runDate,
            String wfdisc,
            int configid,
            byte[] configFileText,
            String commandLine,
            Date endDate,
            double fixedRawSampleRate) {
        this.runid = runid;
        this.runDate = runDate;
        this.wfdisc = wfdisc;
        this.configid = configid;
        this.configFileText = configFileText.clone();
        this.commandLine = commandLine;
        this.endDate = endDate;
        this.fixedRawSampleRate = fixedRawSampleRate;
    }

    @Override
    public String toString() {
        return String.format("Runid %d on %s", runid, runDate.toString());
    }

    /**
     * @return the runid
     */
    public int getRunid() {
        return runid;
    }

    /**
     * @return the runDate
     */
    public Date getRunDate() {
        return runDate;
    }

    /**
     * @return the wfdisc
     */
    public String getWfdisc() {
        return wfdisc;
    }

    /**
     * @return the configid
     */
    public int getConfigid() {
        return configid;
    }

    /**
     * @return the configFileText
     * @throws java.io.IOException
     */
    public String getConfigFileText() throws IOException {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            is = new ByteArrayInputStream(configFileText);
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * @return the commandLine
     */
    public String getCommandLine() {

        return commandLine;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @return the fixedRawSampleRate
     */
    public double getFixedRawSampleRate() {
        return fixedRawSampleRate;
    }

    public boolean isRawRateFixed() {
        return fixedRawSampleRate > 0;
    }
}
