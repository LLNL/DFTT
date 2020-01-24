package llnl.gnem.core.util;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 *
 * @author addair1
 */
public abstract class ButtonAction extends AbstractAction {

    private static final long serialVersionUID = -3674318330014671158L;
    private AbstractButton button;

    public ButtonAction(String name, Icon icon) {
        super(name, icon);
    }

    public void updateState() {
        setEnabled(true);
    }

    public AbstractButton getButton() {
        if (button == null)
            createButton();
        return button;
    }

    public boolean isSelected() {
        return button.isSelected();
    }

    public boolean hasMnemonic() {
        return getValue(MNEMONIC_KEY) != null;
    }

    public int getMnemonic() {
        return (Integer) getValue(MNEMONIC_KEY);
    }

    protected final void setButton(AbstractButton button) {
        this.button = button;
    }

    protected void createButton() {
        button = new JButton(this);
    }
}
