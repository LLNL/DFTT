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
package llnl.gnem.core.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PolygonSetModel {

    private final ArrayList<PolygonSetView> views;
    private final Map<PolygonSetType, List<PolygonSet>> polygonSets;
    private PolygonSet selectedPolygonSet = null;
    private static PolygonSetModel instance;
    private PolygonSetType selectedPolygonSetType = PolygonSetType.country;

    public static PolygonSetModel getInstance() {
        if (instance == null) {
            instance = new PolygonSetModel();
        }
        return instance;
    }

    private PolygonSetModel() {
        polygonSets = new EnumMap<>(PolygonSetType.class);
        views = new ArrayList<>();
    }

    public void addAllSets(List<PolygonSet> allPoly) {
        polygonSets.clear();
        for (PolygonSet poly : allPoly) {
            PolygonSetType type = poly.getType();
            List<PolygonSet> list = polygonSets.get(type);
            if (list == null) {
                list = new ArrayList<>();
                polygonSets.put(type, list);
            }
            list.add(poly);
        }
        notifyViewsPolygonsUpdated();
    }

    public void initialize() {
        new PolygonSetRetrievalWorker(this).execute();
    }

    public void addView(PolygonSetView view) {
        views.add(view);
    }

    /**
     * @return the selectedPolygonSet
     */
    public PolygonSet getSelectedPolygonSet() {
        return selectedPolygonSet;
    }

    /**
     * @param selectedPolygonSet the selectedPolygonSet to set
     */
    public void setSelectedPolygonSet(PolygonSet selectedPolygonSet) {
        this.selectedPolygonSet = selectedPolygonSet;
        notifyViewsSelectedSetChanged();
    }

    public List<PolygonSet> getPolygonSets(PolygonSetType type) {
        return polygonSets.get(type);
    }

    public Collection<PolygonSetType> getPolygonSetTypes() {
        return polygonSets.keySet();
    }

    private void notifyViewsPolygonsUpdated() {
        for (PolygonSetView view : views) {
            view.updateForChangedPolygons();
        }
    }

    private void notifyViewsSelectedSetChanged() {
        for (PolygonSetView view : views) {
            view.updateForChangedSelectedSet();
        }
    }

    public void setSelectedPolygonSetType(PolygonSetType pst) {
        selectedPolygonSetType = pst;
        notifyViewsSelectedPolygonSetTypeChanged();
    }

    private void notifyViewsSelectedPolygonSetTypeChanged() {
        for (PolygonSetView view : views) {
            view.updateForChangedSelectedPolygonSetType();
        }
    }

    public PolygonSetType getSelectedPolygonSetType() {
        return selectedPolygonSetType;
    }
}
