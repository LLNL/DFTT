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
