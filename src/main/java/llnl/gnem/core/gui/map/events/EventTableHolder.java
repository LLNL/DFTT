package llnl.gnem.core.gui.map.events;

import llnl.gnem.core.seismicData.AbstractEventInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import llnl.gnem.core.gui.map.location.LocationTableHolder;

/**
 *
 * @author addair1
 */
public abstract class EventTableHolder<T extends AbstractEventInfo> extends LocationTableHolder<T> {
    public EventTableHolder(EventModel<T> eventModel) {
        super(eventModel);
        table.addMouseListener(new PopupListener());
    }

    private class PopupListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            handlePopup(e);
        }
    }

    protected abstract void handlePopup(MouseEvent e);
}
