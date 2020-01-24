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
 * User: Doug Date: Feb 8, 2012 Time: 11:23:35 AM
 */
public class ApplyFilterAction extends AbstractAction {

    private FilterGuiContainer owner;

    public ApplyFilterAction(FilterGuiContainer owner) {
        super("Filter traces", Utility.getIcon(owner, "miscIcons/applyFilter16.gif"));
        putValue(SHORT_DESCRIPTION, "Apply current filter to traces in memory");
        putValue(MNEMONIC_KEY, KeyEvent.VK_F);
        this.owner = owner;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        owner.getGui().applySelectedFilter();
    }
}
