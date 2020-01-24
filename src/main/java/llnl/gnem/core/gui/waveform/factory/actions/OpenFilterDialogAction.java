/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.factory.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.gui.waveform.factory.FilterComponentFactoryHolder;

/**
 * User: Doug
 * Date: Feb 2, 2012
 * Time: 9:25:09 PM
 */
public class OpenFilterDialogAction extends AbstractAction {

    private static OpenFilterDialogAction ourInstance = null;

    public static OpenFilterDialogAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new OpenFilterDialogAction(owner);
        }
        return ourInstance;
    }

    private OpenFilterDialogAction(Object owner) {
        super("Filter", Utility.getIcon(owner, "miscIcons/ShowFilter32.gif"));
        putValue(SHORT_DESCRIPTION, "Open Filter Dialog.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_F);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            FilterComponentFactoryHolder.getInstance().getSSFilterGuiContainer().setVisible(true);
        } catch (Exception e1) {
            ExceptionDialog.displayError(e1);
        }
    }
}
