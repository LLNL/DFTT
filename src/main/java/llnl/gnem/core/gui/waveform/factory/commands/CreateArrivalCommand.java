/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.factory.commands;

import java.util.logging.Level;
import llnl.gnem.core.util.Command;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.waveform.components.ComponentSet;
import llnl.gnem.core.gui.waveform.DisplayArrival;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class CreateArrivalCommand implements Command {
    protected final double relTime;
    protected final double refTime;
    protected final String phase;
    protected DisplayArrival arrival;
    protected final ComponentSet set;
    protected final BaseSingleComponent component;
    protected final String auth;

    public CreateArrivalCommand(ComponentSet set, BaseSingleComponent channelData,
            double relTime,
            double refTime,
            String phase,
            String auth) {
        this.set = set;
        this.component = channelData;
        this.relTime = relTime;
        this.refTime = refTime;
        this.phase = phase;
        this.auth = auth;
        arrival = null;
    }

    @Override
    public boolean execute() {
        if (arrival == null) {
            // Command has not been executed previously
            arrival = set.createPick(component, relTime, refTime, phase, auth);
        }
       ApplicationLogger.getInstance().log(Level.FINE, String.format("CreateArrivalCommand::execute-->Adding arrival (%s) to component(%s)...", arrival, component));

        set.addArrival(component, arrival);
        return true;
    }

    @Override
    public boolean unexecute() {
        set.deleteArrival(arrival);
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
