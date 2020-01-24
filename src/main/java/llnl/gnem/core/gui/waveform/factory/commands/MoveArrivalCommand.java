package llnl.gnem.core.gui.waveform.factory.commands;

import llnl.gnem.core.util.Command;
import llnl.gnem.core.gui.waveform.DisplayArrival;

public class MoveArrivalCommand implements Command {
    private final DisplayArrival arrival;

    private final DisplayArrival source;
    private final DisplayArrival destination;

    public MoveArrivalCommand(final double deltaT, final DisplayArrival arrival) {
        this.arrival = arrival;

        source = new DisplayArrival(arrival);
        destination = new DisplayArrival(arrival, deltaT);
    }

    @Override
    public boolean execute() {
        arrival.incrementModificationCount();
        arrival.updateFrom(destination);
        return true;
    }

    @Override
    public boolean unexecute() {
        arrival.decrementModificationCount();
        arrival.updateFrom(source);
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