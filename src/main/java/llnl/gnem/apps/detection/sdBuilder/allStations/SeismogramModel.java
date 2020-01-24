/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.allStations;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.dataobjects.EventInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.OriginInfo;
import llnl.gnem.core.waveform.BaseTraceData;
import llnl.gnem.core.waveform.filter.FilterClient;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class SeismogramModel implements FilterClient{

    private final Collection<SeismogramView> views;
    private final Collection<EventSeismogramData> data;
    private final Collection<EventSeismogramData> displayableData;
    private OriginInfo currentOrigin;
    private final ArrayList<EventInfo> events;

    private SeismogramModel() {
        views = new ArrayList<>();
        data = new ArrayList<>();
        events = new ArrayList<>();
        displayableData = new ArrayList<>();
    }

    public void clear() {
        data.clear();
        displayableData.clear();
        events.clear();
        notifyViewsDataChanged();
    }

    public static SeismogramModel getInstance() {
        return SeismogramModelHolder.INSTANCE;
    }

    void addView(SeismogramView seismogramView) {
        views.add(seismogramView);
    }

    void setSeismograms(Collection<EventSeismogramData> results,
            Collection<EventInfo> events) {
        clear();
        data.addAll(results);
        this.events.addAll(events);
        AllStationsPickModel.getInstance().setEventSeismogramData(results);
        displayableData.addAll(data);
        notifyViewsDataChanged();
    }

    private void notifyViewsDataChanged() {
        for (SeismogramView view : views) {
            view.dataHaveChanged();
        }
    }

    public ArrayList<EventInfo> getEvents() {
        return events;
    }

    public Collection<EventSeismogramData> getData() {
        return new ArrayList<>(displayableData);
    }

    void setCurrentOrigin(OriginInfo origin) {
        this.currentOrigin = origin;
    }

    public void hideTrace(BaseTraceData selectedTrace) {
        for (EventSeismogramData esd : displayableData) {
            if (esd.getTraceData().equals(selectedTrace)) {
                displayableData.remove(esd);
                break;
            }
        }
        notifyViewsDataChanged();
    }

    @Override
    public void applyFilter(StoredFilter filter) {
        for(EventSeismogramData esd : data){
            esd.getTraceData().applyFilter(filter);
        }
        notifyViewsDataChanged();
    }

    @Override
    public void unApplyFilter() {
        for(EventSeismogramData esd : data){
            esd.getTraceData().unApplyFilter();
        }
        notifyViewsDataChanged();
    }

    private static class SeismogramModelHolder {

        private static final SeismogramModel INSTANCE = new SeismogramModel();
    }

    public OriginInfo getCurrentOrigin() {
        return currentOrigin;
    }

}
