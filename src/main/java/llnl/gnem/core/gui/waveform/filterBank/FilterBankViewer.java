package llnl.gnem.core.gui.waveform.filterBank;

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
public abstract class FilterBankViewer extends WaveformViewer<FilterBankModel> implements BaseFilterBankView {
    private final FilterBankPlot plot;
    private final CommandManager commandManager;

    public FilterBankViewer(WaveformViewerContainer owner, FilterBankModel dataModel) {
        super(owner, "Filter Bank", dataModel);
        this.plot = new FilterBankPlot(this);
        add(plot, BorderLayout.CENTER);

        commandManager = new CommandManager();
        commandManager.registerRedoAction(getRedoAction());
        commandManager.registerUndoAction(getUndoAction());
    }
        
    @Override
    public Collection<? extends WaveformPlot> getPlots() {
        Collection<FilterBankPlot> plots = new ArrayList<FilterBankPlot>();
        plots.add(plot);
        return plots;
    }

    @Override
    protected CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public void clear() {
        plot.clear();
    }

    @Override
    public void setData(Collection<SeisFilterData> data, BaseSingleComponent aComp) {
        plot.setData(data, aComp);
    }
}
