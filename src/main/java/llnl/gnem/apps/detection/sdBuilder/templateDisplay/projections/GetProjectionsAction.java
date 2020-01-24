package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class GetProjectionsAction extends AbstractAction {

    private static GetProjectionsAction ourInstance;
    private static final long serialVersionUID = 5120574418825972506L;
   

    public static GetProjectionsAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new GetProjectionsAction(owner);
        }
        return ourInstance;
    }
    private int detectorid;
    private int runid;

    private GetProjectionsAction(Object owner) {
        super("Projections", Utility.getIcon(owner, "miscIcons/projector32.gif"));
        putValue(SHORT_DESCRIPTION, "Get projections on other templates used in current run.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_P);
        setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new ComputeProjectionsWorker(detectorid,runid ).execute();
    }

    public void setDetectorid(int detectorid) {
        this.detectorid = detectorid;
    }
    
    public void setRunid( int runid ){
        this.runid = runid;
    }

}
