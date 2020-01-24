package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import llnl.gnem.apps.detection.classify.ClassifyAllInstancesWorker;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ClassifyAllDetectionsAction extends AbstractAction {

    private static ClassifyAllDetectionsAction ourInstance;
    private int runid;

    public static ClassifyAllDetectionsAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new ClassifyAllDetectionsAction(owner);
        }
        return ourInstance;
    }

    private ClassifyAllDetectionsAction(Object owner) {
        super("Classify Detections", Utility.getIcon(owner, "miscIcons/discrim32.gif"));
        putValue(SHORT_DESCRIPTION, "Classify all Detections");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
    }

    public void setRunid(int runid) {
        this.runid = runid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (runid > 0 ) {
           new ClassifyAllInstancesWorker(runid).execute();
        }

    }

}
