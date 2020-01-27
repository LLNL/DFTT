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
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DeleteDetectorWorker;
import llnl.gnem.core.gui.util.ExceptionDialog;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DeleteDetectorAction extends AbstractAction {

    private static DeleteDetectorAction ourInstance;
    private static final long serialVersionUID = -4682836352953719024L;
    private int detectorid;
    private boolean promptForDelete = true;

    public void setPromptForDelete(boolean promptForDelete) {
        this.promptForDelete = promptForDelete;
    }

    public static DeleteDetectorAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new DeleteDetectorAction(owner);
        }
        return ourInstance;
    }
    private DefaultMutableTreeNode node;

    private DeleteDetectorAction(Object owner) {
        super("Delete", Utility.getIcon(owner, "miscIcons/remove32.gif"));
        putValue(SHORT_DESCRIPTION, "Delete Selected Run From Database");
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
    }

    public void setDetectorid(int detectorid) {
        this.detectorid = detectorid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (detectorid > 0 && node != null) {
            if (promptForDelete) {
                Object[] options = {"Continue", "Cancel"};
                int n = JOptionPane.showOptionDialog(ClusterBuilderFrame.getInstance(),
                        "Really delete detector: " + detectorid + "?",
                        "Delete  Detector",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (n == JOptionPane.OK_OPTION) {
                    try {
                        new DeleteDetectorWorker(detectorid, node).execute();
                    } catch (Exception ex) {
                        ExceptionDialog.displayError(ex);
                    }

                }
            } else {
                try {
                    new DeleteDetectorWorker(detectorid, node).execute();
                } catch (Exception ex) {
                    ExceptionDialog.displayError(ex);
                }
            }
        }

    }

    public void setNode(DefaultMutableTreeNode node) {
        this.node = node;
    }

}
