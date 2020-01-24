package llnl.gnem.apps.detection.sdBuilder.allStations.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.SeismogramModel;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.waveform.BaseTraceData;

public class HideTraceAction extends AbstractAction {

    private static HideTraceAction ourInstance;
    private static final long serialVersionUID = 7164583606380075251L;
    private BaseTraceData selectedTrace;

    public static HideTraceAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new HideTraceAction(owner);
        }
        return ourInstance;
    }

    private HideTraceAction(Object owner) {
        super("Hide", Utility.getIcon(owner, "miscIcons/remove32.gif"));
        putValue(SHORT_DESCRIPTION, "Hide selected trace");
        putValue(MNEMONIC_KEY, KeyEvent.VK_H);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( selectedTrace != null ){
            SeismogramModel.getInstance().hideTrace(selectedTrace);
        }
    }

    public void setTrace(BaseTraceData cc) {
        selectedTrace = cc;
    }
}