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
