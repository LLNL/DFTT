package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import llnl.gnem.apps.detection.sdBuilder.histogramDisplay.HistogramDisplayFrame;
import llnl.gnem.apps.detection.sdBuilder.histogramDisplay.HistogramRetrievalWorker;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.TemplateDisplayFrame;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.TemplateRetrievalWorker;
import llnl.gnem.core.gui.util.ExceptionDialog;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DisplayDetectorHistogramAction extends AbstractAction {

    private static DisplayDetectorHistogramAction ourInstance;
    private static final long serialVersionUID = -8674025015655530819L;
    private int detectorid;

    public static DisplayDetectorHistogramAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new DisplayDetectorHistogramAction(owner);
        }
        return ourInstance;
    }
    private int runid = -1;

    private DisplayDetectorHistogramAction(Object owner) {
        super("Histogram", Utility.getIcon(owner, "miscIcons/hist.png"));
        putValue(SHORT_DESCRIPTION, "Display histogram for selected subspace detector");
        putValue(MNEMONIC_KEY, KeyEvent.VK_H);
    }

    public void setDetectorid(int detectorid) {
        this.detectorid = detectorid;
    }

    public void setRunid(int runid) {
        this.runid = runid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (detectorid > 0) {

            try {
                HistogramDisplayFrame.getInstance().setVisible(true);
                new HistogramRetrievalWorker(detectorid, runid).execute();
            } catch (Exception ex) {
                ExceptionDialog.displayError(ex);
            }
        }

    }

}
