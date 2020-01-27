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
