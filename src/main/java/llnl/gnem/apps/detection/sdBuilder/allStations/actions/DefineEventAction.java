package llnl.gnem.apps.detection.sdBuilder.allStations.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.AllStationsFrame;
import llnl.gnem.core.gui.util.Utility;

public class DefineEventAction extends AbstractAction {

    private static DefineEventAction ourInstance;
    private static final long serialVersionUID = 2317571098161737336L;

    public static DefineEventAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new DefineEventAction(owner);
        }
        return ourInstance;
    }

    private DefineEventAction(Object owner) {
        super("Define", Utility.getIcon(owner, "miscIcons/add2db32.gif"));
        putValue(SHORT_DESCRIPTION, "Define event time window");
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AllStationsFrame.getInstance().defineEventWindow();
        AllStationsFrame.getInstance().returnFocusToPlot();
    }
}