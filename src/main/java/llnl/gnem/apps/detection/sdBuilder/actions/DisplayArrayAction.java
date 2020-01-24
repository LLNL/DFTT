package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.gui.util.Utility;

public class DisplayArrayAction extends AbstractAction {

    private static DisplayArrayAction ourInstance;
    private CorrelationComponent selectedComponent;

    public static DisplayArrayAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new DisplayArrayAction(owner);
        }
        return ourInstance;
    }

    private DisplayArrayAction(Object owner) {
        super("Display", Utility.getIcon(owner, "miscIcons/viewStack.gif"));
        putValue(SHORT_DESCRIPTION, "Display All elements for selected detection");
        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( selectedComponent != null ){
           CorrelatedTracesModel.getInstance().displayArrayElements(selectedComponent);
        }
    }

    public void setComponent(CorrelationComponent cc) {
        selectedComponent = cc;
    }
}