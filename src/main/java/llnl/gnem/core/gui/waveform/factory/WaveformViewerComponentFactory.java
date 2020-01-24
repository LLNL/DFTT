package llnl.gnem.core.gui.waveform.factory;

import llnl.gnem.core.gui.waveform.StationNavigationModel;
import llnl.gnem.core.gui.waveform.factory.actions.ComponentDataSaver;
import llnl.gnem.core.gui.waveform.CompSetPlotToolbar;
import llnl.gnem.core.gui.waveform.ThreeComponentModel;
import llnl.gnem.core.gui.waveform.filterBank.FilterBankToolbar;
import llnl.gnem.core.gui.waveform.phaseVisibility.PreferredPhaseManager;
import llnl.gnem.core.gui.waveform.phaseVisibility.UsablePhaseManager;
import llnl.gnem.core.gui.waveform.recsec.MultiStationPlot;
import llnl.gnem.core.gui.waveform.recsec.MultiStationWaveformDataModel;
import llnl.gnem.core.gui.waveform.recsec.RsToolbar;
import llnl.gnem.core.gui.waveform.phaseVisibility.AvailablePhaseManager;

/**
 *
 * @author dodge1
 */
public interface WaveformViewerComponentFactory {
    MainForm getMainForm();

    AvailablePhaseManager getAvailablePhaseManager();

    PreferredPhaseManager getPreferredPhaseManager();

    UsablePhaseManager getUsablePhaseManager();

    StationNavigationModel getStationNavigationModel();

    ComponentDataSaver getComponentDataSaver();

    CompSetPlotToolbar getCompSetPlotToolbar();

    MultiStationWaveformDataModel getMSWaveformDataModel();

    MultiStationPlot getMultiStationWaveformView();

    RsToolbar getRsToolbar();

    FilterBankToolbar getFilterbankToolbar();

    ThreeComponentModel getThreeComponentModel();
}
