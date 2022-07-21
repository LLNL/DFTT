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
package llnl.gnem.apps.detection.sdBuilder.allStations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.apps.detection.dataAccess.dataobjects.EventInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.OriginInfo;
import llnl.gnem.core.waveform.BaseTraceData;
import llnl.gnem.core.waveform.filter.FilterClient;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class SeismogramModel implements FilterClient {

    private final Collection<SeismogramView> views;
    private final Map<String, Collection<EventSeismogramData>> data;
    private final Map<String, Collection<EventSeismogramData>> displayableData;
    private OriginInfo currentOrigin;
    private final ArrayList<EventInfo> events;

    private SeismogramModel() {
        views = new ArrayList<>();
        data = new HashMap<>();
        events = new ArrayList<>();
        displayableData = new HashMap<>();
    }

    public void clear() {
        data.clear();
        displayableData.clear();
        events.clear();
        notifyViewsDataChanged();
        AllStationChannelCombo.getInstance().removeAllItems();
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
        populateMap(results, data);
        this.events.addAll(events);
        AllStationsPickModel.getInstance().setEventSeismogramData(results);
        populateMap(results, displayableData);
        populateChannelCombo();
        notifyViewsDataChanged();
    }

    private void populateMap(Collection<EventSeismogramData> source, Map<String, Collection<EventSeismogramData>> target) {
        for (EventSeismogramData esd : source) {
            String chan = esd.getTraceData().getChan();
            Collection<EventSeismogramData> tmp = target.get(chan);
            if (tmp == null) {
                tmp = new ArrayList<>();
                target.put(chan, tmp);
            }
            tmp.add(esd);
        }
    }

    private void populateChannelCombo() {
        Collection<String> channels = data.keySet();
        AllStationChannelCombo.getInstance().enableActionListener(false);
        AllStationChannelCombo.getInstance().removeAllItems();
        for (String chan : channels) {
            AllStationChannelCombo.getInstance().addItem(chan);
        }
        if (!channels.isEmpty()) {
            AllStationChannelCombo.getInstance().setSelectedIndex(0);
        }
        AllStationChannelCombo.getInstance().revalidate();
        AllStationChannelCombo.getInstance().enableActionListener(true);
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

        String key = (String) AllStationChannelCombo.getInstance().getSelectedItem();
        if (key != null) {
            Collection<EventSeismogramData> tmp = displayableData.get(key);
            if (tmp != null) {
                return new ArrayList<>(tmp);
            }
        }
        return new ArrayList<>();
    }

    void setCurrentOrigin(OriginInfo origin) {
        this.currentOrigin = origin;
    }

    public void hideTrace(BaseTraceData selectedTrace) {
        String key = selectedTrace.getChan();
        Collection<EventSeismogramData> tmp = displayableData.get(key);
        for (EventSeismogramData esd : tmp) {
            if (esd.getTraceData().equals(selectedTrace)) {
                tmp.remove(esd);
                break;
            }
        }
        notifyViewsDataChanged();
    }

    @Override
    public void applyFilter(StoredFilter filter) {
        for (Collection<EventSeismogramData> tmp : data.values()) {
            for (EventSeismogramData esd : tmp) {
                esd.getTraceData().applyFilter(filter);
            }
        }
        notifyViewsDataChanged();
    }

    @Override
    public void unApplyFilter() {
        for (Collection<EventSeismogramData> tmp : data.values()) {
            for (EventSeismogramData esd : tmp) {
                esd.getTraceData().unApplyFilter();
            }
        }
        notifyViewsDataChanged();
    }

    void channelWasChanged() {
        notifyViewsDataChanged();
    }

    private static class SeismogramModelHolder {

        private static final SeismogramModel INSTANCE = new SeismogramModel();
    }

    public OriginInfo getCurrentOrigin() {
        return currentOrigin;
    }

}
