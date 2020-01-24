package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.gui.util.Utility;

public class ResizeWindowAction extends AbstractAction {

    private static ResizeWindowAction ourInstance;
    private static final long serialVersionUID = -714291761242000400L;

    public static ResizeWindowAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new ResizeWindowAction(owner);
        }
        return ourInstance;
    }

    private ResizeWindowAction(Object owner) {
        super("Resize", Utility.getIcon(owner, "miscIcons/resize.png"));
        putValue(SHORT_DESCRIPTION, "Auto-resize the template window");
        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      
           CorrelatedTracesModel.getInstance().autoResizeWindow();
      
    }
}