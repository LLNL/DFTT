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
