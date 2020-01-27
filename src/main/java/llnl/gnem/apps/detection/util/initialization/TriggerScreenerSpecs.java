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
package llnl.gnem.apps.detection.util.initialization;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author dodge1
 */
public class TriggerScreenerSpecs {

    private double snrPreWindowLength;
    private double snrPostWindowLength;
    private double snrThreshold;
    private double minEventDuration;
    private double durationScreenDetriggerWindowLength;
    private double durationScreenDeTriggerThreshold;
    private double snrPreTriggerGuardBand;

    private TriggerScreenerSpecs() {
    }

    public static TriggerScreenerSpecs getInstance() {
        return TriggerScreenerSpecsHolder.instance;
    }

    private static class TriggerScreenerSpecsHolder {

        private static final TriggerScreenerSpecs instance = new TriggerScreenerSpecs();
    }

    public void initialize(String parfile) throws  FileNotFoundException, IOException {

        FileInputStream infile = null;
        Properties propertyList = new Properties();
        try {
            infile = new FileInputStream(parfile);
            propertyList.load(infile);
        } finally {
            if (infile != null) {
                infile.close();
            }
        }


        snrPreWindowLength = Double.parseDouble(propertyList.getProperty("SnrPreWindowLength", "5.0").trim());
        snrPostWindowLength = Double.parseDouble(propertyList.getProperty("SnrPostWindowLength", "5.0").trim());
        snrThreshold = Double.parseDouble(propertyList.getProperty("SnrThreshold", "2.0").trim());
        minEventDuration = Double.parseDouble(propertyList.getProperty("MinEventDuration", "50.0").trim());
        durationScreenDetriggerWindowLength = Double.parseDouble(propertyList.getProperty("DurationScreenDetriggerWindowLength", "30.0").trim());
        durationScreenDeTriggerThreshold = Double.parseDouble(propertyList.getProperty("DurationScreenDeTriggerThreshold", "3.0").trim());
        snrPreTriggerGuardBand = Double.parseDouble(propertyList.getProperty("SnrPreTriggerGuardBand", "8.0").trim());

    }

    public double getSnrPreTriggerGuardBand() {
        return snrPreTriggerGuardBand;
    }

    public double getSnrPreWindowLength() {
        return snrPreWindowLength;
    }

    public double getSnrPostWindowLength() {
        return snrPostWindowLength;
    }

    public double getSnrThreshold() {
        return snrThreshold;
    }

    public double getMinEventDuration() {
        return minEventDuration;
    }

    public double getDurationScreenDetriggerWindowLength() {
        return durationScreenDetriggerWindowLength;
    }

    public double getDurationScreenDeTriggerThreshold() {
        return durationScreenDeTriggerThreshold;
    }
}
