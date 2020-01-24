package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import llnl.gnem.apps.detection.sdBuilder.picking.DetectionPhasePickModel;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DeleteAllPicksAction extends AbstractAction {

    private static DeleteAllPicksAction ourInstance;
    private static final long serialVersionUID = -9143217544910179470L;

    public static DeleteAllPicksAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new DeleteAllPicksAction(owner);
        }
        return ourInstance;
    }

    private DeleteAllPicksAction(Object owner) {
        super("Delete", Utility.getIcon(owner, "miscIcons/remove32.gif"));
        putValue(SHORT_DESCRIPTION, "Delete all picks for current detector and runid.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DetectionPhasePickModel.getInstance().clear();
    }

}
