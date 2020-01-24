package llnl.gnem.core.gui.map.simple;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Collection;
import llnl.gnem.core.gui.map.LayerNames;
import llnl.gnem.core.seismicData.AbstractEventInfo;
import llnl.gnem.core.gui.map.events.EventModel;
import llnl.gnem.core.gui.map.simple.SimpleMap.PlotItem;
import llnl.gnem.core.gui.plotting.plotobject.Star5;
import llnl.gnem.core.gui.plotting.plotobject.Symbol;

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
