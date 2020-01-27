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
package llnl.gnem.core.gui.waveform.recsec;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.gui.waveform.WaveformPlot;
import llnl.gnem.core.util.CommandManager;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.gui.waveform.WaveformViewer;
import llnl.gnem.core.gui.waveform.WaveformViewerContainer;

/**
 * Created by dodge1 Date: Mar 19, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public abstract class MultiStationViewer extends WaveformViewer<MultiStationWaveformDataModel> implements BaseMultiChannelWaveformView {

    private static final long serialVersionUID = 1120987567040721478L;
    private final MultiStationPlot plot;
    private final CommandManager commandManager;

    public MultiStationViewer(WaveformViewerContainer owner, MultiStationPlot plot) {
        super(owner, "Multi-Station", plot.getDataModel());
        this.plot = plot;
        plot.setOwner(this);
        add(plot, BorderLayout.CENTER);

        commandManager = new CommandManager();
        commandManager.registerRedoAction(getRedoAction());
        commandManager.registerUndoAction(getUndoAction());
    }

    @Override
    public Collection<? extends WaveformPlot> getPlots() {
        Collection<MultiStationPlot> plots = new ArrayList<>();
        plots.add(plot);
        return plots;
    }

    @Override
    protected CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public void updateForChangedOrigin() {
        plot.updateForChangedOrigin();
    }

    @Override
    public void updateForAddedOrigin() {
        // Adding an origin causes the origin to change, so the updateForChangedOrigin method is called.
    }

    @Override
    public void updateForChangedPreferredOrigin() {
        // TODO
    }

    @Override
    public void clear() {
        plot.clear();
    }

    @Override
    public void updateForNewEvent() {
        plot.updateForNewEvent();
    }

    @Override
    public void updateForChangedChannel() {
        plot.updateForChangedChannel();
    }

    @Override
    public void updateForChangedWaveform(BaseSingleComponent channelData) {
        plot.updateForChangedWaveform(channelData);
    }

    @Override
    public void updateForChangedWaveform() {
        plot.updateForChangedWaveform();
    }
}
