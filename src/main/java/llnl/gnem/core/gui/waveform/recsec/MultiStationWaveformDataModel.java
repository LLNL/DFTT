package llnl.gnem.core.gui.waveform.recsec;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.gui.map.location.SelectionChangeListener;
import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.seismicData.AbstractEventInfo;
import llnl.gnem.core.gui.map.origins.OriginInfo;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.waveform.filter.FilterClient;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.gui.waveform.DisplayArrival;
import llnl.gnem.core.gui.waveform.PlotPickingStateManager;
import llnl.gnem.core.gui.waveform.WaveformDataModel;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.Geometry.EModel;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 * @param <T>
 */
public abstract class MultiStationWaveformDataModel<T extends AbstractEventInfo> extends WaveformDataModel<BaseMultiChannelWaveformView> implements FilterClient, SelectionChangeListener<T> {

    private final PlotPickingStateManager pickManager;
    private final Collection<BaseSingleComponent> components;
    private BaseSingleComponent selectedComponent;

    protected MultiStationWaveformDataModel(PlotPickingStateManager pickManager) {
        this.pickManager = pickManager;
        components = new ArrayList<>();
        selectedComponent = null;
    }

    public PlotPickingStateManager getPickManager() {
        return pickManager;
    }

    @Override
    public void applyFilter(StoredFilter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unApplyFilter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public abstract OriginInfo getCurrentOriginInfo();

    Collection<BaseSingleComponent> getWaveformChannels() {
        Collection<BaseSingleComponent> result = new ArrayList<>();
        OriginInfo origin = getCurrentOriginInfo();
        double maxTime = origin.getTime() + 2000;
        double maxDelta = 90.0;
        double decimatedSampleRate = 4.0;
        for (BaseSingleComponent bsc : components) {
            StationInfo si = bsc.getStationInfo();
            double thisDelta = EModel.getDelta(si.getLat(), si.getLon(), origin.getLat(), origin.getLon());
            if(thisDelta > maxDelta)continue;
            BaseSingleComponent bsc2 = new BaseSingleComponent(bsc);
            Epoch existing = bsc2.getEpoch();
            if (existing.getStart() >= maxTime) {
                continue;
            }
            if (existing.getEnd() > maxTime) {
                bsc2.trimTo(new Epoch(existing.getStart(), maxTime));
            }
            bsc2.resample(decimatedSampleRate);
            result.add(bsc2);
        }
        return result;
    }

    public BaseSingleComponent getCurrentChannel() {
        return selectedComponent;
    }

    void setCurrentChannel(BaseSingleComponent cd) {
        WaveformViewerFactoryHolder.getInstance().getStationNavigationModel().setSelectedStation(cd.getStationInfo());
        selectedComponent = cd;
    }

    BaseSingleComponent getChannelForExistingArrival(DisplayArrival arrival) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    double getMaxDelta(OriginInfo origin) {
        double result = 0;
        for (BaseSingleComponent bsc : components) {
            double delta = bsc.getStationInfo().delta(origin);
            if (delta > result) {
                result = delta;
            }
        }
        return result;
    }

    public void removeArrival(BaseSingleComponent channelData, DisplayArrival arrival) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void reinstatePick(BaseSingleComponent channelData, DisplayArrival arrival) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public DisplayArrival createPick(BaseSingleComponent channelData, double time, String phase) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void movePick(BaseSingleComponent channelData, DisplayArrival arrival, double deltaT) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void changePickDeltim(BaseSingleComponent channelData, DisplayArrival arrival, double delta) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void savePicks() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setComponents(Collection<BaseSingleComponent> newComponents) {
        clear();
        components.addAll(newComponents);
        for (BaseMultiChannelWaveformView view : getViews()) {
            view.updateForNewEvent();
        }
    }

    @Override
    public void clear() {
        components.clear();
        for (BaseMultiChannelWaveformView view : getViews()) {
            view.clear();
        }
    }

    public int getNumTraces() {
        return components.size();
    }

    public void changeSelectedStation(StationInfo stationInfo) {
        selectedComponent = null;
        for (BaseSingleComponent bsc : components) {
            if (bsc.getStationInfo().equals(stationInfo)) {
                selectedComponent = bsc;
                break;
            }
        }
        for (BaseMultiChannelWaveformView view : getViews()) {
            view.updateForChangedChannel();
        }
    }

    @Override
    public void selectionChanged(T eventInfo) {
        clear();
    }
}
