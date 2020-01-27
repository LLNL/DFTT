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
package llnl.gnem.core.gui.map.simple;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Collection;
import llnl.gnem.core.gui.map.LayerNames;
import llnl.gnem.core.gui.map.simple.SimpleMap.PlotItem;
import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.gui.map.stations.StationModel;
import llnl.gnem.core.gui.plotting.plotobject.Symbol;

/**
 *
 * @author addair1
 * @param <S>
 */
public class StationManager<S extends StationInfo> extends PlotManager<S> {
    private final StationModel<S> stationModel;
    private final SimpleMap map;

    public StationManager(StationModel<S> stationModel, SimpleMap map) {
        super(LayerNames.STATION_LAYER_NAME, LayerNames.SELECTED_STATION_LAYER_NAME, stationModel, map);
        this.map = map;
        this.stationModel = stationModel;
    }

    @Override
    protected void clearViewItems() {
        super.clearViewItems();
    }

    @Override
    public void addLocations(Collection<S> stations) {
        for (S info : stations) {
            addLocation(info);
        }
        refreshView();
    }

    @Override
    public void updateCurrentLocation(S currentLocation) {
        setCurrent(currentLocation);
    }

    @Override
    public void refreshView() {
        map.plot.repaint();
    }

    public void setCurrent(S currentStation) {
        updateSelected(currentStation, stationModel.getAutoCenterStationUponSelection(), false);
    }

    @Override
    public Color getColor(S info) {
        Color color = map.iconManager.getStationSymbolColor(info, isSelected(info));
        return color;
    }

    public void updateAllStations() {
        clearLocations();
        if (stationModel != null) {
            Collection<S> stations = stationModel.getAllLocations();
            for (S info : stations) {
                addItem(createPlotItem(info));
            }
        }
        refreshView();
    }

    @Override
    public PlotItem createPlotItem(S info) {
        Color color = getColor(info);
        // Symbols consist of a geometric shape centered on user-supplied
        // coordinates and optional text centered under the shape.
        Symbol symbol = map.iconManager.getStationSymbol(info);
        symbol.setFillColor(color);
        return new PlotItem<S>(info, symbol);
    }

    public void recreateStationIcons() {
        if (map.iconManager == null) {
            return;
        }
        for (PlotItem<S> item : infoToPlotItem.values()) {
            // Only need to re-create those that are 'visible' in the layer
            if (layer.contains(item.getSymbol())) {
                // Remove old icon from plot
                layer.remove(item.getSymbol());
                // Create newest icon
                S station = item.getInfo();
                item.setSymbol(map.iconManager.getStationSymbol(station));
                item.getSymbol().setFillColor(getColor(station));
                item.getSymbol().setText(item.getInfo().toString());
                item.getSymbol().setTextVisible(false);
                // Add new icon to plot if it was there originally
                layer.add(item.getSymbol());
            }
        }
        map.plot.repaint();
    }

    protected boolean isSelected(StationInfo info) {
        return stationModel != null && stationModel.getCurrent() != null && stationModel.getCurrent() == info;
    }

    @Override
    public void handleSymbolSelection(Symbol symbol, MouseEvent me) {
        SimpleMap.PlotItem sItem = findSymbol(symbol);
        if (sItem != null) {
            stationModel.setCurrent((S) sItem.getInfo());
        }
    }
}
