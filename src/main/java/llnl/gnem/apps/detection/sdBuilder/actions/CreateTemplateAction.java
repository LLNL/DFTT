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
package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.dftt.core.gui.util.ExceptionDialog;
import llnl.gnem.dftt.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class CreateTemplateAction extends AbstractAction {

    private static CreateTemplateAction ourInstance;
    private static final long serialVersionUID = 8266219106056871649L;

    public static CreateTemplateAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new CreateTemplateAction(owner);
        }
        return ourInstance;
    }

    private CreateTemplateAction(Object owner) {
        super("Template", Utility.getIcon(owner, "miscIcons/threeSeis32.gif"));
        putValue(SHORT_DESCRIPTION, "Create template from current traces.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_T);
        setEnabled(!ParameterModel.getInstance().isRequireCorrelation());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ParameterModel.getInstance().isRequireWindowPositionConfirmation()) {
            Object[] options = {"Continue", "Cancel"};
            int n = JOptionPane.showOptionDialog(ClusterBuilderFrame.getInstance(),
                    "Please make sure that the selection window defines the time interval of the template signal.\n If it is set correctly choose 'Continue'.",
                    "Verify Time Interval Selection",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == JOptionPane.OK_OPTION) {
                setEnabled(false);
                try {
                    CorrelatedTracesModel.getInstance().writeNewDetector();
                } catch (Exception ex) {
                    ExceptionDialog.displayError(ex);
                }
            }
        } else {
            try {
                CorrelatedTracesModel.getInstance().writeNewDetector();
            } catch (Exception ex) {
                ExceptionDialog.displayError(ex);
            }
        }
    }
}
