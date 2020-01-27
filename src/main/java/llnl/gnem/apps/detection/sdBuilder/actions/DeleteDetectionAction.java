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