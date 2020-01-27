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
package llnl.gnem.core.waveform.continuous.segments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class BaseSegmentCatalogModel implements SegmentCatalogModel {

    private static final double DEFAULT_WINDOW_DURATION = 86400.0;
    private static final double MIN_WINDOW_DURATION = 7200.0;
    private static final double MAX_WINDOW_DURATION = DEFAULT_WINDOW_DURATION * 10;

    private final Collection<SegmentCatalogView> views;
    private final Collection<ChannelSegmentCatalog> catalogs;
    private double selectionWindowTime;
    private double windowDuration;

    public BaseSegmentCatalogModel() {
        views = new ArrayList<>();
        catalogs = new ArrayList<>();
        windowDuration = DEFAULT_WINDOW_DURATION;
    }

    @Override
    public void addView(SegmentCatalogView view) {
        views.add(view);
    }

    @Override
    public void addChannelSegmentCatalog(ChannelSegmentCatalog catalog) {
        catalogs.add(catalog);
        notifyViewsCatalogAdded(catalog);
    }

    @Override
    public ChannelSegmentCatalog getChannelSegmentCatalog(StreamKey key) {
        for (ChannelSegmentCatalog catalog : catalogs) {
            if (catalog.getName().equals(key)) {
                return catalog;
            }
        }
        return null;
    }

    @Override
    public List<StreamKey> getCatalogList() {
        ArrayList<StreamKey> result = new ArrayList<>();
        for (ChannelSegmentCatalog catalog : catalogs) {
            result.add(catalog.getName());
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public void clear() {
        catalogs.clear();
        notifyViewsCatalogsCleared();
    }

    @Override
    public void setCatalogs(Collection<ChannelSegmentCatalog> catalogs) {
        this.catalogs.clear();
        this.catalogs.addAll(catalogs);
        notifyViewsCatalogsLoaded();
    }

    private void notifyViewsCatalogAdded(ChannelSegmentCatalog catalog) {
        for (SegmentCatalogView view : views) {
            view.catalogWasLoaded(catalog.getName());
        }
    }

    private void notifyViewsCatalogsCleared() {
        for (SegmentCatalogView view : views) {
            view.catalogsCleared();
        }
    }

    private void notifyViewsCatalogsLoaded() {
        for (SegmentCatalogView view : views) {
            view.catalogsWereLoaded();
        }
    }

    @Override
    public void setSelectionWindowTime(double windowTime) {
        selectionWindowTime = windowTime;
        notifyViewsSelectionWindowMoved();
    }

    @Override
    public double getSelectionWindowDuration() {
        return windowDuration;
    }

    /**
     * @param windowDuration the windowDuration to set
     */
    @Override
    public void setWindowDuration(double windowDuration) {
        this.windowDuration = windowDuration;
        notifyViewsWindowDurationChanged();
    }

    /**
     * @return the selectionWindowTime
     */
    @Override
    public double getSelectionWindowTime() {
        return selectionWindowTime;
    }

    @Override
    public double validateDurationChange(double delta) {
        double tmp = windowDuration + delta;
        if (tmp < MIN_WINDOW_DURATION) {
            return MIN_WINDOW_DURATION;
        } else if (tmp > MAX_WINDOW_DURATION) {
            return MAX_WINDOW_DURATION;
        } else {
            return tmp;
        }
    }

    private void notifyViewsWindowDurationChanged() {
        for (SegmentCatalogView view : views) {
            view.windowDurationWasChanged();
        }
    }

    private void notifyViewsSelectionWindowMoved() {
        for (SegmentCatalogView view : views) {
            view.selectionWindowWasMoved();
        }
    }

}
