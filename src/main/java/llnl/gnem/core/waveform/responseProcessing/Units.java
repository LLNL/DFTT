/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.responseProcessing;

/**
 *
 * @author dodge1
 */
public enum Units {

    Default(0, "def"), Displacement(1, "dis"), Velocity(2, "vel"), Acceleration(3, "acc");
    private final int evrespCode;
    private final String shortString;

    Units(int code, String shortString) {
        evrespCode = code;
        this.shortString = shortString;
    }

    public int getEvrespCode() {
        return evrespCode;
    }

    public String getShortString() {
        return shortString;
    }
}
