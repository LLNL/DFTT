/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
