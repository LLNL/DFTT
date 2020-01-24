/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.responseProcessing;

/**
 *
 * @author dodge1
 */
public enum WaveformDataUnits {

    cm("cm"), cmpersec("cm/s"), cmpersec2("cm/s^2"), m("m"), mpersec("m/s"), mpersec2("m/s^2"), Pa("Pa"), V("V"),
    PaS("Pa-s"), degC("degC"), unknown("unknown");

    public static WaveformDataUnits changeForDifferentiation(WaveformDataUnits dataUnits) {
        switch (dataUnits) {
            case cm:
                return cmpersec;
            case cmpersec:
                return cmpersec2;
            case m:
                return mpersec;
            case mpersec:
                return mpersec2;
            default:
                throw new IllegalStateException("Do not know how to change units: " + dataUnits + " for differentiation!");
        }
    }

    public static WaveformDataUnits changeForIntegration(WaveformDataUnits dataUnits) {
        switch (dataUnits) {
            case cmpersec2:
                return cmpersec;
            case cmpersec:
                return cm;
            case mpersec2:
                return mpersec;
            case mpersec:
                return m;
            default:
                throw new IllegalStateException("Do not know how to change units: " + dataUnits + " for integration!");
        }
    }
    private final String displayString;

    WaveformDataUnits(String string) {
        displayString = string;
    }

    @Override
    public String toString() {
        return displayString;
    }

    public static WaveformDataUnits getWaveformDataUnitsFromString(String string) {
        if (string.equals("cm")) {
            return cm;
        } else if (string.equals("cm/s")) {
            return cmpersec;
        } else if (string.equals("cm/s^2")) {
            return cmpersec2;
        } else if (string.equals("m")) {
            return m;
        } else if (string.equals("m/s")) {
            return mpersec;
        } else if (string.equals("m/s^2")) {
            return mpersec2;
        } else if (string.equals("Pa")) {
            return Pa;
        } else if (string.equals("Pa-s")) {
            return PaS;
        } else if (string.equals("V")) {
            return V;
        } else if (string.equals("degC")) {
            return degC;
        } else {
            throw new IllegalArgumentException("Unrecognized string: " + string);

        }
    }
}
