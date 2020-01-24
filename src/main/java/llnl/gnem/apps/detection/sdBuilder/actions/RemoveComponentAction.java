package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.gui.util.Utility;

public class RemoveComponentAction extends AbstractAction {

    private static RemoveComponentAction ourInstance;
    private static final long serialVersionUID = 7164583606380075251L;
    private CorrelationComponent selectedComponent;

    public static RemoveComponentAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new RemoveComponentAction(owner);
        }
        return ourInstance;
    }

    private RemoveComponentAction(Object owner) {
        super("Remove", Utility.getIcon(owner, "miscIcons/remove32.gif"));
        putValue(SHORT_DESCRIPTION, "Remove selected detection from memory");
        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( selectedComponent != null ){
            CorrelatedTracesModel.getInstance().removeComponent(selectedComponent);
        }
    }

    public void setComponent(CorrelationComponent cc) {
        selectedComponent = cc;
    }
}