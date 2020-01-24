package llnl.gnem.core.gui.waveform;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.gui.map.events.EventModel;
import llnl.gnem.core.gui.map.stations.StationModel;

public abstract class WaveformDataModel<T extends WaveformView> {
    protected final Collection<T> views;
    private EventModel eventModel;
    private StationModel stationModel;

    public WaveformDataModel() {
        views = new ArrayList<>();
        eventModel = null;
    }

    public void retrievalIsComplete() {
    }

    public EventModel getEventModel() {
        if (eventModel == null) {
            throw new IllegalStateException("No event data model found for waveform data model.");
        }
        return eventModel;
    }

    public void setEventModel(EventModel eventModel) {
        this.eventModel = eventModel;
    }

    public StationModel getStationModel() {
        if (stationModel == null) {
            throw new IllegalStateException("No station data model found for waveform data model.");
        }
        return stationModel;
    }

    public void setStationModel(StationModel stationModel) {
        this.stationModel = stationModel;
    }

    public void addView(T view) {
        views.remove(view);
        views.add(view);
    }

    public void setActive(boolean active) {
        for (T view : views) {
            view.setActive(active);
        }
    }

    protected Collection<T> getViews() {
        return views;
    }

    public abstract void clear();
}
