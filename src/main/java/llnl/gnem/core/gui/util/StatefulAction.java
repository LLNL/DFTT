package llnl.gnem.core.gui.util;

import java.awt.event.ActionEvent;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import llnl.gnem.core.gui.util.StatefulAction.ActionState;
import llnl.gnem.core.util.ButtonAction;

/**
 *
 * @author addair1
 * @param <T>
 */
public abstract class StatefulAction<T extends ActionState> extends ButtonAction {
    private T currentState;

    public StatefulAction(T firstState, String name, Object owner) {
        super(name, Utility.getIcon(owner, firstState.getIconPath()));

        putValue(SHORT_DESCRIPTION, firstState.getDescription());
        currentState = firstState;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setState(getNext());
    }

    protected T getCurrentState() {
        return currentState;
    }

    protected void handleStateChange(T state) {
    }

    private void setState(T state) {
        currentState = state;

        ImageIcon icon = Utility.getIcon(this, currentState.getIconPath());

        AbstractButton button = getButton();
        putValue(SHORT_DESCRIPTION, currentState.getDescription());
        putValue("SMALL_ICON", icon);
        button.setIcon(icon);
        button.setToolTipText(currentState.getDescription());

        handleStateChange(currentState);
    }

    protected abstract T getNext();

    public interface ActionState {
        public String getDescription();

        public String getIconPath();
    }
}
