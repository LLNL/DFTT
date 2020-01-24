/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.recsec;

import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.gui.waveform.DisplayArrival;
import llnl.gnem.core.gui.waveform.PlotPickDataBridge;

/**
 *
 * @author dodge1
 */
public class MSPlotPickDataBridge extends PlotPickDataBridge {

    private double reductionTime;

    public MSPlotPickDataBridge(DisplayArrival arrival, BaseSingleComponent component, double reductionTime) {
        super(arrival, component);
        this.reductionTime = reductionTime;
    }

    double getReductionTime() {
        return reductionTime;
    }

    void setReductionTime(double reductionTime) {
        this.reductionTime = reductionTime;
    }
}
