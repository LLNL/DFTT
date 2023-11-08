/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.dftt.core.gui.waveform.plotPrefs;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import llnl.gnem.dftt.core.gui.util.ExceptionDialog;
import llnl.gnem.dftt.core.gui.util.Utility;

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
