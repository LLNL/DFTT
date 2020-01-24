/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform;

import llnl.gnem.core.gui.waveform.DisplayArrival;

/**
 *
 * @author addair1
 * @param <T>
 */
public interface ArrivalChangeListener<T extends DisplayArrival> {
    public void arrivalChanged(T arrival);
}
