/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.factory.commands;

import llnl.gnem.core.util.Command;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.gui.waveform.DisplayArrival;

/**
 * User: Doug Date: Sep 12, 2009 Time: 4:39:36 PM
 */
public class ChangeDeltimCommand implements Command {

    private final BaseSingleComponent component;
    private final double delta;
    private final DisplayArrival arrival;

    public ChangeDeltimCommand(final BaseSingleComponent channelData,
            final double delta,
            final DisplayArrival arrival) {
        this.component = channelData;
        this.delta = delta;
        this.arrival = arrival;
    }

    @Override
    public boolean execute() {
        arrival.incrementModificationCount();
        arrival.setDeltim(arrival.getDeltim() + delta);
        return true;
    }

    @Override
    public boolean unexecute() {
        arrival.decrementModificationCount();
        arrival.setDeltim(arrival.getDeltim() - delta);
        return true;
    }

    @Override
    public boolean isAllowable() {
        return true;
    }

    @Override
    public boolean isReversible() {
        return true;
    }

    @Override
    public boolean isRunInNewThread() {
        return false;
    }
}