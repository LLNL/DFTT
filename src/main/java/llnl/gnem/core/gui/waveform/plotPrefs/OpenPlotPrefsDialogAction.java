/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.plotPrefs;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.Utility;

/**
 *
 * @author dodge1
 */
public class OpenPlotPrefsDialogAction extends AbstractAction {

    private static final long serialVersionUID = 1678458357981619450L;

    public OpenPlotPrefsDialogAction(Object owner) {
        super("PlotPrefs", Utility.getIcon(owner, "miscIcons/test.gif"));
        putValue(SHORT_DESCRIPTION, "Open Plot Preferences Dialog.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        PlotPresentationPrefs prefs = PlotPreferenceModel.getInstance().getPrefs();
        PlotPrefsPanel ppp = new PlotPrefsPanel(prefs);
        Object[] options = {"Apply", "Defaults", "Cancel"};
        int answer = JOptionPane.showOptionDialog(null,
                ppp,
                String.format("Set Plot Properties"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, //do not use a custom Icon
                options, //the titles of buttons
                options[0]); //default button title
        if (answer == JOptionPane.YES_OPTION) {
            ppp.updatePrefsFromControls();
            try {
                PlotPreferenceModel.getInstance().setPrefs(prefs);
            } catch (Exception ex) {
                ExceptionDialog.displayError(ex);
            }
        } else if (answer == JOptionPane.NO_OPTION) {
            try {
                PlotPreferenceModel.getInstance().setPrefs(new PlotPresentationPrefs());
            } catch (Exception ex) {
                ExceptionDialog.displayError(ex);
            }
        }
    }
}
