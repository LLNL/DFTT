
package llnl.gnem.core.gui.waveform.phaseVisibility;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;


public class OpenPreferredPhaseDialogAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public OpenPreferredPhaseDialogAction() {
        super("Preferred Phase Preferences");
        putValue(SHORT_DESCRIPTION, "Open the preferred phase selection dialog.");
        //putValue(MNEMONIC_KEY, KeyEvent.VK_S);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	PreferredPhaseDialogFrame.getInstance().setVisible(true);
    }
}