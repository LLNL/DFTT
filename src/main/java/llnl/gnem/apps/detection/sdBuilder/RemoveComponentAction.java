package llnl.gnem.apps.detection.sdBuilder;

import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.gui.util.Utility;


/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class RemoveComponentAction extends AbstractAction {

    private static RemoveComponentAction ourInstance;
    private static final long serialVersionUID = -3431373010944463378L;
    private CorrelationComponent selectedComponent;

    public static RemoveComponentAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new RemoveComponentAction(owner);
        }
        return ourInstance;
    }

    private RemoveComponentAction(Object owner) {
        super("Hide Trace", Utility.getIcon(owner, "miscIcons/remove32.gif"));
        putValue(SHORT_DESCRIPTION, "Hide Selected Trace (does not remove from model)");
        putValue(MNEMONIC_KEY, KeyEvent.VK_H);
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