/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
