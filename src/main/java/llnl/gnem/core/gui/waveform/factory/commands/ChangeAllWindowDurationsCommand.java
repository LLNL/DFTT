/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
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