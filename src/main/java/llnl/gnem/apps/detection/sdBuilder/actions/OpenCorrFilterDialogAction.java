/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.gui.waveform.factory.FilterComponentFactoryHolder;

/**
 * Created by IntelliJ IDEA.
 * User: Doug
 * Date: Feb 2, 2012
 * Time: 9:25:09 PM
 */
public class OpenCorrFilterDialogAction extends AbstractAction {

    private static OpenCorrFilterDialogAction ourInstance = null;
    private static final long serialVersionUID = 7506223831209067435L;

    public static OpenCorrFilterDialogAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new OpenCorrFilterDialogAction(owner);
        }
        return ourInstance;
    }

    private OpenCorrFilterDialogAction(Object owner) {
        super("Filter", Utility.getIcon(owner, "miscIcons/ShowFilter32.gif"));
        putValue(SHORT_DESCRIPTION, "Open Correlation Trace Filter Dialog.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_F);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FilterComponentFactoryHolder.getInstance().getSSFilterGuiContainer().setVisible(true);
    }
}
