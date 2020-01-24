package llnl.gnem.core.gui.waveform.factory.commands;

import llnl.gnem.core.util.Command;

/**
 *
 * @author addair1
 */
public abstract class ReversibleCommand implements Command {
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
