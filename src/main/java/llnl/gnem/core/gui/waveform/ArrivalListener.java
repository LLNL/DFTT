/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform;

import llnl.gnem.core.gui.waveform.DisplayArrival;
import llnl.gnem.core.waveform.components.BaseSingleComponent;

/**
 *
 * @author addair1
 * @param <T>
 * @param <S>
 */
public interface ArrivalListener<T extends BaseSingleComponent, S extends DisplayArrival> {
    public void arrivalAdded(T component, S arrival);

    public void arrivalRemoved(S arrival);
}
