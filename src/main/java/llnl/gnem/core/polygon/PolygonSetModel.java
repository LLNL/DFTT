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
