package llnl.gnem.core.gui.util;

import java.awt.event.ActionEvent;
import llnl.gnem.core.util.ButtonAction;
import llnl.gnem.core.util.Command;
import llnl.gnem.core.util.Invokable;

/**
 *
 * @author addair1
 */
public abstract class CommandAction extends ButtonAction {
    private final Invokable owner;

    public CommandAction(String name, String desc, String icon, int mnemonic, Invokable owner) {
        super("", Utility.getIcon(owner, icon));
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        this.owner = owner;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        invoke();
    }

    public void invoke() {
        if (isEnabled()) {
            owner.invoke(getCommand());
        }
    }

    public abstract Command getCommand();
}
