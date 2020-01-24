package llnl.gnem.core.gui.waveform.factory.commands;

import llnl.gnem.core.util.Command;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JWindowRegion;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.WindowDurationChangedState;

/**
 * Created by dodge1
 * Date: Mar 24, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class ChangeSingleWindowDurationCommand implements Command {
    private final WindowDurationChangedState wdcs;
    private final double deltaD;

    public ChangeSingleWindowDurationCommand(WindowDurationChangedState wdcs) {
        this.wdcs = wdcs;
        this.deltaD = wdcs.getDeltaD();
    }

    public boolean execute() {
        moveWindow(deltaD);
        return true;
    }

    private void moveWindow(double amount) {
        VPickLine vpl = wdcs.getWindowHandle().getAssociatedPick();
        JWindowRegion window = vpl.getWindow();
        double duration = window.getDuration();
        window.setDurationNoNotify(duration + amount);
        wdcs.getSubplot().getOwner().repaint();
    }

    public boolean unexecute() {
        moveWindow(-deltaD);
        return true;
    }

    public boolean isAllowable() {
        return true;
    }

    public boolean isReversible() {
        return true;
    }

    public boolean isRunInNewThread() {
        return false;
    }
}