/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.gui.filter.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.gui.waveform.factory.FilterComponentFactoryHolder;

/**
 * User: Doug
 * Date: Feb 8, 2012
 * Time: 11:21:35 AM
 */
public class RemoveFilterAction extends AbstractAction {
    private static RemoveFilterAction ourInstance = null;

    public synchronized static RemoveFilterAction getInstance(Object owner)
    {
        if( ourInstance == null )
            ourInstance = new RemoveFilterAction(owner);
        return ourInstance;
    }

    private RemoveFilterAction(Object owner)
    {
        super( "Remove Selected", Utility.getIcon(owner,"miscIcons/ignore16.gif") );
        putValue(SHORT_DESCRIPTION, "Remove selected filter from list.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_R );
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try {
            FilterComponentFactoryHolder.getInstance().getSSFilterGuiContainer().getGui().removeSelectedFilter();
        }
        catch (Exception e1) {
            ExceptionDialog.displayError(e1);
        }
    }

}