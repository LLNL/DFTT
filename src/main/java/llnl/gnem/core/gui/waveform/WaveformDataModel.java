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
