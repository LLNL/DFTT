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
package llnl.gnem.dftt.core.gui.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.UserObjectPreferences;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;

/**
 * User: Doug Date: Feb 7, 2012 Time: 7:42:56 PM
 */
public class FilterModel {

    private StoredFilter currentFilter;
    private final ArrayList<StoredFilter> allStoredFilters;
    private final ArrayList<StoredFilter> myStoredFilters;
    private final Collection<FilterView> views;
    private final Collection<FilterModelObserver> changeObservers;

    private void maybeSetDefaultFilters() {
        if (myStoredFilters.isEmpty()) {
            for (StoredFilter sf : allStoredFilters) {
                if (sf.isDefaultFilter()) {
                    myStoredFilters.add(sf);
                }
            }
            savePreferences();
        }
    }

    public boolean addFilterFromPool(StoredFilter filter) {
        for (StoredFilter afilter : allStoredFilters) {
            if (afilter.isFunctionallyEquivalent(filter)) {
                myStoredFilters.add(afilter);
                currentFilter = afilter;
                savePreferences();
                notifyViewsModelChanged();
                notifyChangeObserversFilterAdded(afilter);
                return true;
            }
        }
        return false;
    }

    public void replaceUserFilters(Collection<StoredFilter> filters) {
        myStoredFilters.clear();
        myStoredFilters.addAll(filters);
        notifyViewsModelChanged();
    }

    private void notifyChangeObserversFilterRemoved(StoredFilter filter) {
        for (FilterModelObserver fmo : changeObservers) {
            fmo.filterWasRemoved(filter);
        }
    }

    private void notifyChangeObserversFilterAdded(StoredFilter filter) {
        for (FilterModelObserver fmo : changeObservers) {
            fmo.filterWasAdded(filter);
        }
    }

    void setCurrentFilter(StoredFilter selected) {
        currentFilter = selected;
        savePreferences();
    }

    private static class FilterModelHolder {

        private static final FilterModel INSTANCE = new FilterModel();
    }

    public static FilterModel getInstance() {
        return FilterModelHolder.INSTANCE;
    }

    private FilterModel() {
        allStoredFilters = new ArrayList<>();
        myStoredFilters = new ArrayList<>();

        currentFilter = getCurrentFromStorage();

        views = new ArrayList<>();
        changeObservers = new ArrayList<>();
    }

    public void addChangeObserver(FilterModelObserver observer) {
        changeObservers.add(observer);
    }

    public void savePreferences() {
        try {
            StoredFilter target = currentFilter;
            if (target == null) {
                target = new StoredFilter();
            }
            UserObjectPreferences.getInstance().saveObjectToPrefs("PREF_FILTERID", target);
        } catch (IOException | BackingStoreException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed saving preferred Filters preferences!", ex);
        }
    }

    public void updateModel(Collection<StoredFilter> allFilters, Collection<StoredFilter> myFilters) {
        allStoredFilters.clear();
        myStoredFilters.clear();
        allStoredFilters.addAll(allFilters);
        myStoredFilters.addAll(myFilters);
        maybeSetDefaultFilters();
        currentFilter = getCurrentFromStorage();
        notifyViewsModelChanged();
    }

    private StoredFilter getCurrentFromStorage() {
        StoredFilter result = new StoredFilter();
        try {
            result = (StoredFilter) UserObjectPreferences.getInstance().retrieveObjectFromPrefs("PREF_FILTERID", StoredFilter.class);
        } catch (IOException | ClassNotFoundException | BackingStoreException ex) {
            Logger.getLogger(FilterModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (result != null && result.isNoFilter()) {
            result = null;
        }
        return result;
    }

    public void updateModel() {
        notifyViewsModelChanged();
    }

    public void addView(FilterView view) {
        views.add(view);
        view.update();
    }

    void notifyViewsModelChanged() {
        for (FilterView view : views) {
            view.update();
        }
    }

    public StoredFilter getCurrentFilter() {
        return currentFilter;
    }

    public ArrayList<StoredFilter> getMyStoredFilters() {
        return new ArrayList<>(myStoredFilters);
    }

    public ArrayList<StoredFilter> getAllStoredFilters() {
        return new ArrayList<>(allStoredFilters);
    }

    public void addNewFilter(StoredFilter filter) {
        if (filter != null) {
            allStoredFilters.add(filter);
            myStoredFilters.add(filter);
            currentFilter = filter;
            savePreferences();
            notifyViewsModelChanged();
            notifyChangeObserversFilterAdded(filter);
        }
    }

    public void changeSelectedFilter(StoredFilter filter) {
        currentFilter = filter;
        savePreferences();
        notifyViewsModelChanged();
    }

    public void removeFilter(StoredFilter filter) {
        myStoredFilters.remove(filter);
        if (myStoredFilters.isEmpty()) {
            currentFilter = new StoredFilter();
        } else if (currentFilter == filter) {
            currentFilter = myStoredFilters.get(0);
            savePreferences();
        }
        notifyViewsModelChanged();
        notifyChangeObserversFilterRemoved(filter);
    }
}
