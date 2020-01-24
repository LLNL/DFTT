package llnl.gnem.core.gui.map.simple;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import llnl.gnem.core.gui.map.internal.RenderItemManager;
import llnl.gnem.core.gui.map.location.LocationInfo;
import llnl.gnem.core.gui.map.location.LocationModel;
import llnl.gnem.core.gui.map.location.LocationView;
import llnl.gnem.core.gui.map.simple.SimpleMap.PlotItem;
import llnl.gnem.core.gui.map.simple.SimpleMap.PlotObserverListener;
import llnl.gnem.core.gui.map.simple.SimpleMap.SimpleLayer;
import llnl.gnem.core.gui.plotting.HorizAlignment;
import llnl.gnem.core.gui.plotting.VertAlignment;
import llnl.gnem.core.gui.plotting.plotobject.DataText;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.plotobject.Symbol;

/**
 *
 * @author addair1
 */
public abstract class PlotManager<T extends LocationInfo> extends RenderItemManager<PlotItem<T>> implements LocationView<T>, PlotObserverListener {

    private final LocationModel<T> model;
    private final SimpleMap map;
    List<PlotItem<T>> viewItems = new ArrayList<PlotItem<T>>();
    SimpleLayer layer;
    PlotItem<T> selected;
    public HashMap<T, PlotItem<T>> infoToPlotItem = new HashMap<T, PlotItem<T>>();

    public PlotManager(String name, String selectedName, LocationModel<T> model, SimpleMap map) {
        this.model = model;
        this.map = map;
        layer = map.createLayer(name);
    }

    public SimpleMap.SimpleLayer getLayer() {
        return layer;
    }

    public void clear() {
        reset();
        clearViewItems();
        map.plot.repaint();
    }

    @Override
    public void clearLocations() {
        clear();
    }

    @Override
    public void updateCurrentLocation(T currentLocation) {
        updateSelected(currentLocation, false, false);
    }

    @Override
    public void updateLocations(Collection<T> locations) {
    }

    @Override
    public void locationWasRemoved(T location) {
        //TODO: implement refresh
        
    }

    @Override
    protected void clearViewItems() {
        layer.clear();
        viewItems.clear();
    }

    protected PlotObject createScreenLabel(final String value, final double x, final double y) {
        return new DataText(x, y, value, "Arial", 14.0f, Color.BLACK, HorizAlignment.LEFT, VertAlignment.CENTER);
    }

    @Override
    protected void refreshView() {
        map.repaint();
    }

    @Override
    public void viewItemAdded(PlotItem<T> item) {
        viewItems.add(item);
        layer.add(item.getSymbol());
    }

    @Override
    public void addLocations(Collection<T> locations) {
        for (T location : locations) {
            addLocation(location);
        }
    }

    @Override
    protected void addItem(PlotItem<T> item) {
        super.addItem(item);
        infoToPlotItem.put(item.getInfo(), item);
    }

    public void addLocation(T location) {
        addItem(createPlotItem(location));
    }

    public abstract PlotItem createPlotItem(T info);

    public abstract Color getColor(T info);

    protected void updateSelected(T info, boolean autoCenter, boolean zoom) {
        if (selected != null) {
            // Reset symbol to original non-selected color
            selected.getSymbol().setFillColor(getColor(selected.getInfo()));
        }
        if (info == null) {
            map.repaint();
            return;
        }
        PlotItem plotItem = infoToPlotItem.get(info);
        selected = plotItem;
        // Set symbol to selected color
        if (plotItem != null) {
            plotItem.getSymbol().setFillColor(getSelectedColor(selected.getInfo()));
        }
        if (autoCenter) {
            map.centerView(info, zoom);
        }
        map.repaint();
    }

    protected Color getSelectedColor(T info) {
        return Color.GREEN;
    }

    protected PlotItem<T> getItem(T measurable) {
        return infoToPlotItem.get(measurable);
    }

    protected Set<T> getInfos() {
        return infoToPlotItem.keySet();
    }

    protected Collection<PlotItem<T>> getViewItems() {
        return viewItems;
    }

    public PlotItem<T> findSymbol(Symbol symbol) {
        for (PlotItem item : viewItems) {
            if (item.getSymbol().equals(symbol)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void handleSymbolSelection(Symbol symbol, MouseEvent me) {
        PlotItem<T> eItem = findSymbol(symbol);
        if (eItem != null) {
            boolean popup = me.getButton() == MouseEvent.BUTTON3 && me.getClickCount() == 1;

            if (popup) {
                handlePopup(eItem.getInfo(), me);
            } else {
                handleSelection(eItem.getInfo(), me);
            }
        }
    }

    protected void handleSelection(T info, MouseEvent me) {
        model.setCurrent(info);
    }

    protected void handlePopup(T info, MouseEvent me) {
        map.getPopupMenu().show(me.getComponent(), me.getX(), me.getY() + 10);
    }
}
