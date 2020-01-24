package llnl.gnem.apps.detection.sdBuilder.histogramDisplay;

import llnl.gnem.apps.detection.sdBuilder.actions.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class ExportHistogramPlotAction extends AbstractAction {

    private static ExportHistogramPlotAction ourInstance;
    private static final long serialVersionUID = 3200996091887341127L;

    public static ExportHistogramPlotAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new ExportHistogramPlotAction(owner);
        }
        return ourInstance;
    }

    private ExportHistogramPlotAction(Object owner) {
        super("Export", Utility.getIcon(owner, "miscIcons/export32.gif"));
        putValue(SHORT_DESCRIPTION, "Export Plot");
        putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HistogramDisplayFrame.getInstance().exportPlot();
    }
}