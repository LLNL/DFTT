package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DeleteConfigurationWorker;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DeleteDetectorWorker;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DeleteRunWorker;
import llnl.gnem.core.gui.util.ExceptionDialog;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DeleteConfigurationAction extends AbstractAction {

    private static DeleteConfigurationAction ourInstance;
    private int configid;

    public static DeleteConfigurationAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new DeleteConfigurationAction(owner);
        }
        return ourInstance;
    }
    private DefaultMutableTreeNode node;

    private DeleteConfigurationAction(Object owner) {
        super("Delete", Utility.getIcon(owner, "miscIcons/remove32.gif"));
        putValue(SHORT_DESCRIPTION, "Delete Selected Configuration From Database");
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
    }

    public void setConfigid(int configid) {
        this.configid = configid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (configid > 0 && node != null) {
            Object[] options = {"Continue", "Cancel"};
            int n = JOptionPane.showOptionDialog(ClusterBuilderFrame.getInstance(),
                    "Really delete configuration: " + configid + "?",
                    "Delete  Configuration",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == JOptionPane.OK_OPTION) {
                try {
                    new DeleteConfigurationWorker(configid, node).execute();
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
