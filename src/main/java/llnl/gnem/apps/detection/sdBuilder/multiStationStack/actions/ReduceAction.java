package llnl.gnem.apps.detection.sdBuilder.multiStationStack.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.MultiStationStackFrame;
import llnl.gnem.core.gui.util.Utility;

public class ReduceAction extends AbstractAction {

    private static ReduceAction ourInstance;
    private static final long serialVersionUID = -8113268630797390063L;

    public static ReduceAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new ReduceAction(owner);
        }
        return ourInstance;
    }

    private ReduceAction(Object owner) {
        super("Reduce", Utility.getIcon(owner, "miscIcons/pagedown32.gif"));
        putValue(SHORT_DESCRIPTION, "Decrease Magnification");
        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MultiStationStackFrame.getInstance().reduceTraces();
        MultiStationStackFrame.getInstance().returnFocusToPlot();
    }
}