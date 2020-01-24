package llnl.gnem.apps.detection.sdBuilder.allStations.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.AllStationsFrame;
import llnl.gnem.core.gui.util.Utility;

public class PrintAction extends AbstractAction {

    private static PrintAction ourInstance;
    private static final long serialVersionUID = 2317571098161737336L;

    public static PrintAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new PrintAction(owner);
        }
        return ourInstance;
    }

    private PrintAction(Object owner) {
        super("Print", Utility.getIcon(owner, "miscIcons/print32.gif"));
        putValue(SHORT_DESCRIPTION, "Print Plot");
        putValue(MNEMONIC_KEY, KeyEvent.VK_P);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AllStationsFrame.getInstance().printPlot();
        AllStationsFrame.getInstance().returnFocusToPlot();
    }
}