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
package llnl.gnem.dftt.core.gui.map.location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author addair1
 * @param <T>
 */
public class LocationModel<T extends LocationInfo> extends SelectionModel<T> {
    private final Collection<T> locations;
    private final List<LocationView<T>> views;
    private final List<LocationColumn<T>> columns;

    public LocationModel() {
        locations = Collections.synchronizedSet(new HashSet<T>());
        views = Collections.synchronizedList(new ArrayList<LocationView<T>>());
        columns = new ArrayList<>();
    }
    
    public boolean contains(T info) {
        return locations.contains(info);
    }

    public void remove(T info) {
        if (info != null) {
            if (info == getCurrent()) {
                assign(null);
            }
            locations.remove(info);
            notifyViewsLocationRemoved(info);
        }
    }

    public void clear() {
        locations.clear();
        assign(null);
        notifyViewsSelectionCleared();
    }

    public void addLocations(Collection<T> locations) {
        this.locations.addAll(locations);
        notifyViewsLocationsAdded(locations);
    }

    public void addView(LocationView<T> view) {
        views.remove(view);
        views.add(view);
        view.clearLocations();
        view.addLocations(getLocations());
    }

    public List<T> getAllLocations() {
        return new ArrayList<>(locations);
    }

    protected Collection<T> getLocations() {
        return locations;
    }

    protected Collection<LocationView<T>> getViews() {
        return new ArrayList<>(views);
    }

    @Override
    protected final void notifyViewsSelectionCleared() {
        for (LocationView view : getViews()) {
            view.clearLocations();
        }
        super.notifyViewsSelectionCleared();
    }
    
    @Override
    protected final void notifyViewsSelectionChanged() {
        for (LocationView view : getViews()) {
            view.updateCurrentLocation(getCurrent());
        }
        super.notifyViewsSelectionChanged();
    }

    protected final void notifyViewsLocationsAdded(Collection<T> addedLocations) {
        addedLocations = filterDisplay(addedLocations);
        for (LocationView view : getViews()) {
            view.addLocations(addedLocations);
        }
    }

    protected final void notifyViewsUpdatedLocations(Collection<T> locations) {
        locations = filterDisplay(locations);
        for (LocationView view : getViews()) {
            view.updateLocations(locations);
        }
    }

    public void retrievalIsCompleted() {
        for (LocationView view : getViews()) {
            view.retrievalIsCompleted();
        }
    }

    protected void addColumn(LocationColumn<T> column) {
        columns.add(column);
    }

    public List<LocationColumn<T>> getColumns() {
        if (columns.isEmpty()) {
            createColumns();
        }
        return columns;
    }

    private void notifyViewsLocationRemoved(T info) {
        for (LocationView view : getViews()) {
            view.locationWasRemoved(info);
        }
    }
    
    protected void createColumns() {
    }
    
    protected Collection<T> filterDisplay(Collection<T> locations) {
        return locations;
    }
}
