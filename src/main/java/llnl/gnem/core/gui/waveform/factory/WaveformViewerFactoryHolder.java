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
public class WaveformViewerFactoryHolder implements WaveformViewerComponentFactory {

    private WaveformViewerComponentFactory theFactory;

    private WaveformViewerFactoryHolder() {
    }

    public static WaveformViewerFactoryHolder getInstance() {
        return SSWaveformFactoryHolderHolder.instance;
    }

    public void setFactory(WaveformViewerComponentFactory aFactory) {
        theFactory = aFactory;
    }

    @Override
    public MainForm getMainForm() {
        return theFactory.getMainForm();
    }

    @Override
    public AvailablePhaseManager getAvailablePhaseManager() {
        return theFactory.getAvailablePhaseManager();
    }

    @Override
    public StationNavigationModel getStationNavigationModel() {
        return theFactory.getStationNavigationModel();
    }

    @Override
    public ComponentDataSaver getComponentDataSaver() {
        return theFactory.getComponentDataSaver();
    }

    @Override
    public CompSetPlotToolbar getCompSetPlotToolbar() {
        return theFactory.getCompSetPlotToolbar();
    }

    @Override
    public MultiStationWaveformDataModel getMSWaveformDataModel() {
        return theFactory.getMSWaveformDataModel();
    }

    @Override
    public MultiStationPlot getMultiStationWaveformView() {
        return theFactory.getMultiStationWaveformView();
    }

    @Override
    public RsToolbar getRsToolbar() {
        return theFactory.getRsToolbar();
    }

    @Override
    public FilterBankToolbar getFilterbankToolbar() {
        return theFactory.getFilterbankToolbar();
    }

    @Override
    public ThreeComponentModel getThreeComponentModel() {
        return theFactory.getThreeComponentModel();
    }

    @Override
    public PreferredPhaseManager getPreferredPhaseManager() {
        return theFactory.getPreferredPhaseManager();
    }

    @Override
    public UsablePhaseManager getUsablePhaseManager() {
        return theFactory.getUsablePhaseManager();
    }

    private static class SSWaveformFactoryHolderHolder {

        private static final WaveformViewerFactoryHolder instance = new WaveformViewerFactoryHolder();
    }
}
