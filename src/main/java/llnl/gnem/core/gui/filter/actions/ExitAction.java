/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.gui.filter.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.core.gui.filter.FilterGuiContainer;
import llnl.gnem.core.gui.util.Utility;

/**
 * User: Doug
 * Date: Feb 8, 2012
 * Time: 11:27:23 AM
 */
public class ExitAction extends AbstractAction {
   private FilterGuiContainer owner;

    public ExitAction(FilterGuiContainer owner) {
        super("Exit", Utility.getIcon(owner, "miscIcons/Exit16.gif"));
        putValue(SHORT_DESCRIPTION, "Click to close filter dialog.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_X);
        this.owner = owner;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        owner.setVisible(false);
    }

}
