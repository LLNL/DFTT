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
package llnl.gnem.dftt.core.gui.map.simple;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Collection;
import llnl.gnem.dftt.core.gui.map.LayerNames;
import llnl.gnem.dftt.core.seismicData.AbstractEventInfo;
import llnl.gnem.dftt.core.gui.map.events.EventModel;
import llnl.gnem.dftt.core.gui.map.simple.SimpleMap.PlotItem;
import llnl.gnem.dftt.core.gui.plotting.plotobject.Star5;
import llnl.gnem.dftt.core.gui.plotting.plotobject.Symbol;

/**
 *
 * @author addair1
 */
public class EventManager<E extends AbstractEventInfo> extends PlotManager<E> {
    private final EventModel<E> eventModel;
    private final SimpleMap map;
    private boolean popupSelectedEvent;

    public EventManager(EventModel<E> eventModel, SimpleMap map) {
        super(LayerNames.EVENT_LAYER_NAME, LayerNames.SELECTED_EVENT_LAYER_NAME, eventModel, map);
        this.map = map;
        this.eventModel = eventModel;
        popupSelectedEvent = false;
    }

    @Override
    public void updateCurrentLocation(E currentLocation) {
        setCurrent(currentLocation, true);
    }

    public void setCurrent(E currentEvent, boolean zoom) {
        updateSelected(currentEvent, !popupSelectedEvent, zoom);
    }

    @Override
    public Color getColor(E info) {
        boolean active = eventModel != null && eventModel.getCurrent() != null && eventModel.getCurrent().equals(info);
        if (active) {
            return Color.GREEN;
        }
        return Color.RED;
    }

    @Override
    public PlotItem<E> createPlotItem(E event) {
        Symbol symbol = new Star5(event.getLat(), event.getLon(), 6.0);
        symbol.setFillColor(getColor(event));
        return new PlotItem<E>(event, symbol);
    }

    @Override
    public void handleSymbolSelection(Symbol symbol, MouseEvent me) {
        PlotItem<E> eItem = findSymbol(symbol);
        if (eItem != null) {
            popupSelectedEvent = me.getButton() == MouseEvent.BUTTON3 && me.getClickCount() == 1;
            handleSelection(eItem.getInfo(), me);
            if (popupSelectedEvent) {
                handlePopup(eItem.getInfo(), me);
            }
            popupSelectedEvent = false;
        }
    }
    
    @Override
    protected void handleSelection(E info, MouseEvent me) {
        eventModel.setCurrent(info);
    }
    
    @Override
    protected void handlePopup(E info, MouseEvent me) {
        map.getPopupMenu().show(me.getComponent(), me.getX(), me.getY() + 10);
    }
}
