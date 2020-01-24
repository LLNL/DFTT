/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform;


import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickDataBridge;

/**
 *
 * @author dodge1
 */
public class PlotPickDataBridge extends PickDataBridge {

    private DisplayArrival arrival;
    private final BaseSingleComponent component;

    public PlotPickDataBridge(DisplayArrival arrival, BaseSingleComponent component) {
        super(arrival.getTime(), arrival.getDeltim(), 0);
        this.arrival = arrival;
        this.component = component;
    }

    @Override
    public String getInfoString() {
        return String.format("%s pick on %s by %s at %8.5f with deltim = %8.7f (Arid = %d)",
                arrival.getPhase(),
                component.getName(),
                arrival.getAuth(),
                arrival.getTime(),
                deltim,
                arrival.getArid());
    }
}
