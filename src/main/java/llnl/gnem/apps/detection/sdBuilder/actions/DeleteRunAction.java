package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DeleteDetectorWorker;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DeleteRunWorker;
import llnl.gnem.core.gui.util.ExceptionDialog;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DeleteRunAction extends AbstractAction {

    private static DeleteRunAction ourInstance;
    private int runid;

    public static DeleteRunAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new DeleteRunAction(owner);
        }
        return ourInstance;
    }
    private DefaultMutableTreeNode node;

    private DeleteRunAction(Object owner) {
        super("Delete", Utility.getIcon(owner, "miscIcons/remove32.gif"));
        putValue(SHORT_DESCRIPTION, "Delete Selected Run From Database");
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
    }

    public void setRunid(int runid) {
        this.runid = runid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (runid > 0 && node != null) {
            Object[] options = {"Continue", "Cancel"};
            int n = JOptionPane.showOptionDialog(ClusterBuilderFrame.getInstance(),
                    "Really delete results of runid: " + runid + "?",
                    "Delete  Run",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == JOptionPane.OK_OPTION) {
                try {
                    new DeleteRunWorker(runid, node).execute();
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
