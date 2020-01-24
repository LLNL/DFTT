package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import llnl.gnem.apps.detection.core.dataObjects.Detection;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DeleteDetectionWorker;
import llnl.gnem.core.gui.util.ExceptionDialog;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DeleteDetectionAction extends AbstractAction {

    private static DeleteDetectionAction ourInstance;
    private static final long serialVersionUID = 2706902177125347105L;
    private Detection detection;
   
   

    public static DeleteDetectionAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new DeleteDetectionAction(owner);
        }
        return ourInstance;
    }
    private DefaultMutableTreeNode node;
    

    private DeleteDetectionAction(Object owner) {
        super("Delete", Utility.getIcon(owner, "miscIcons/remove32.gif"));
        putValue(SHORT_DESCRIPTION, "Delete Selected Detection From Database");
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
    }
    
    public void setDetection( Detection detection )
    {
        this.detection = detection;
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        if (detection != null && node != null) {
       
            try {
                new DeleteDetectionWorker(detection,node).execute();
            } catch (Exception ex) {
                ExceptionDialog.displayError(ex);
            }
        }

    }

    public void setNode(DefaultMutableTreeNode node) {
        this.node = node;
    }

}