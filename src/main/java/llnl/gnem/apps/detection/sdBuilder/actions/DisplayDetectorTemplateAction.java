package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.TemplateDisplayFrame;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.TemplateRetrievalWorker;
import llnl.gnem.core.gui.util.ExceptionDialog;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DisplayDetectorTemplateAction extends AbstractAction {

    private static DisplayDetectorTemplateAction ourInstance;
    private static final long serialVersionUID = 6377649089648812896L;
    private int detectorid;

    public static DisplayDetectorTemplateAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new DisplayDetectorTemplateAction(owner);
        }
        return ourInstance;
    }

    private DisplayDetectorTemplateAction(Object owner) {
        super("Template", Utility.getIcon(owner, "miscIcons/threeSeis32.gif"));
        putValue(SHORT_DESCRIPTION, "Display Template for selected detector");
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
    }

    public void setDetectorid(int detectorid) {
        this.detectorid = detectorid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (detectorid > 0) {

            try {
                TemplateDisplayFrame.getInstance().setVisible(true);
                new TemplateRetrievalWorker(detectorid).execute();
            } catch (Exception ex) {
                ExceptionDialog.displayError(ex);
            }
        }

    }

}
