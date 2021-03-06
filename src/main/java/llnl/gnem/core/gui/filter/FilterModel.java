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
package llnl.gnem.core.gui.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.UserObjectPreferences;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 * User: Doug Date: Feb 7, 2012 Time: 7:42:56 PM
 */
public class FilterModel {

    private StoredFilter currentFilter;
    private final ArrayList<StoredFilter> allStoredFilters;
    private final ArrayList<StoredFilter> myStoredFilters;
    private final Collection<FilterView> views;
    private final static String NODE_NAME = "USER_STORED_FILTERS";

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
                return true;
            }
        }
        return false;
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
        try {
            ArrayList<StoredFilter> tmp = (ArrayList<StoredFilter>) UserObjectPreferences.getInstance().retrieveObjectFromPrefs(NODE_NAME, ArrayList.class);
            if (tmp != null && !tmp.isEmpty()) {
                myStoredFilters.addAll(tmp);
            }
            currentFilter = (StoredFilter) UserObjectPreferences.getInstance().retrieveObjectFromPrefs("PREF_FILTERID", StoredFilter.class);
        } catch (IOException | ClassNotFoundException | BackingStoreException ex) {

        }
        if (currentFilter == null) {
            currentFilter = new StoredFilter();
        }
        views = new ArrayList<>();
    }

    public void savePreferences() {
        try {
            UserObjectPreferences.getInstance().saveObjectToPrefs(NODE_NAME, myStoredFilters);
            UserObjectPreferences.getInstance().saveObjectToPrefs("PREF_FILTERID", currentFilter);
        } catch (IOException | BackingStoreException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed saving preferred Filters preferences!", ex);
        }
    }

    public void updateModel(Collection<StoredFilter> storedFilters) {
        allStoredFilters.clear();
        allStoredFilters.addAll(storedFilters);
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

    public ArrayList<StoredFilter> getAllStoredFilters() {
        return myStoredFilters;
    }

    public void addNewFilter(StoredFilter filter) {
        if (filter != null) {
            allStoredFilters.add(filter);
            myStoredFilters.add(filter);
            currentFilter = filter;
            savePreferences();
            notifyViewsModelChanged();
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
    }
}
