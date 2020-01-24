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
 * Time: 11:25:45 AM
  */
public class UnapplyFilterAction extends AbstractAction {
   private FilterGuiContainer owner;

    public UnapplyFilterAction(FilterGuiContainer owner)
    {
        super( "Un-Filter trace", Utility.getIcon(owner,"miscIcons/undo16.gif") );
        putValue(SHORT_DESCRIPTION, "Un-Apply current filter");
        putValue(MNEMONIC_KEY, KeyEvent.VK_U );
        this.owner = owner;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        owner.getGui().unapplyFilter();
    }

}
