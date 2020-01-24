/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.triggerProcessing;

import llnl.gnem.apps.detection.core.signalProcessing.FKMeasurement;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class WindowStats {

    private final FKMeasurement measurement;
    private final double snr;
    private final double signalEnergy;
    private final TimeT windowStart;
    private final double windowDuration;

    public WindowStats(FKMeasurement measurement, 
            double snr, 
            double postEnergy,
            TimeT windowStart, 
            double windowDuration) {
        this.measurement = measurement;
        this.snr = snr;
        this.signalEnergy = postEnergy;
        this.windowStart = windowStart;
        this.windowDuration = windowDuration;
    }

    public FKMeasurement getMeasurement() {
        return measurement;
    }

    public double getPostEnergy() {
        return signalEnergy;
    }

    public double getSNR() {
        return snr;
    }
    
    public double getAzimuthDeviation( WindowStats other)
    {
        return measurement.getAzimuth() - other.getMeasurement().getAzimuth();
    }

    @Override
    public String toString() {
        return String.format("%s, energy = %9.1f, SNR = %4.2f", measurement.toString(), signalEnergy, snr);
    }

    public TimeT getWindowStart() {
        return windowStart;
    }

    public double getWindowDuration() {
        return windowDuration;
    }
}
