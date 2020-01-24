/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.seismicData;

/**
 *
 * @author maganazook1
 */
public class BasePhaseWindow {

    public String getPhase() {
        return phase;
    }

    public double getNominalWindowLength() {
        return nominalWindowLength;
    }

    public double getPreWinSeconds() {
        return preWinSeconds;
    }

    public BasePhaseWindow(String phase, double nominalWindowLength, double preWinSeconds) {
        this.phase = phase;
        this.nominalWindowLength = nominalWindowLength;
        this.preWinSeconds = preWinSeconds;
    }
    private final String phase;
    private final double nominalWindowLength;
    private final double preWinSeconds;

}
