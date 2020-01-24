package llnl.gnem.core.gui.waveform.factory.commands;

import llnl.gnem.core.util.Command;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.waveform.components.ComponentSet;
import llnl.gnem.core.gui.waveform.DisplayArrival;

/**
 * User: Doug
 * Date: Sep 12, 2009
 * Time: 5:06:47 PM
 */
public class DeleteArrivalCommand implements Command {
    private final ComponentSet set;
    private BaseSingleComponent component;
    private final DisplayArrival arrival;

    public DeleteArrivalCommand(ComponentSet set, DisplayArrival arrival) {
        this.set = set;
        this.arrival = arrival;
    }

    @Override
    public boolean execute() {
        component = set.deleteArrival(arrival);
        return true;
    }

    @Override
    public boolean unexecute() {
        if (component != null) {
            set.addArrival(component, arrival);
            return true;
        }

        return false;
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