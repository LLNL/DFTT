package llnl.gnem.core.gui.waveform.factory.commands;

import llnl.gnem.core.util.Command;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JWindowRegion;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.WindowDurationChangedState;

import java.util.Collection;

/**
 * Created by dodge1
 * Date: Mar 24, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class ChangeAllWindowDurationsCommand implements Command {
    private final WindowDurationChangedState wdcs;
    private final double deltaD;
    private final Collection<VPickLine> picks;

    public ChangeAllWindowDurationsCommand(WindowDurationChangedState wdcs, Collection<VPickLine> picks) {
        this.wdcs = wdcs;
        this.deltaD = wdcs.getDeltaD();
        this.picks = picks;
    }

    public boolean execute() {
        moveWindows(deltaD);
        return true;
    }

    private void moveWindows(double amount) {
        for (VPickLine vpl : picks) {
            JWindowRegion window = vpl.getWindow();
            double duration = window.getDuration();
            window.setDurationNoNotify(duration + amount);
        }
        wdcs.getSubplot().getOwner().repaint();
    }

    public boolean unexecute() {
        moveWindows(-deltaD);
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