package llnl.gnem.apps.detection.sdBuilder.multiStationStack.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.MultiStationStackFrame;
import llnl.gnem.core.gui.util.Utility;

public class ExportAction extends AbstractAction {

    private static ExportAction ourInstance;
    private static final long serialVersionUID = -7911251716177779072L;

    public static ExportAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new ExportAction(owner);
        }
        return ourInstance;
    }

    private ExportAction(Object owner) {
        super("Export", Utility.getIcon(owner, "miscIcons/export32.gif"));
        putValue(SHORT_DESCRIPTION, "Export Plot");
        putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MultiStationStackFrame.getInstance().exportPlot();
        MultiStationStackFrame.getInstance().returnFocusToPlot();
    }
}