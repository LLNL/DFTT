package llnl.gnem.apps.detection.sdBuilder.allStations.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.sdBuilder.allStations.AllStationsPickModel;
import llnl.gnem.core.gui.util.Utility;

public class RemoveSinglePickAction extends AbstractAction {

    private static RemoveSinglePickAction ourInstance;
    private static final long serialVersionUID = 6651312145249048229L;
    private PhasePick selectedPick;

    public static RemoveSinglePickAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new RemoveSinglePickAction(owner);
        }
        return ourInstance;
    }

    private RemoveSinglePickAction(Object owner) {
        super("Remove Pick", Utility.getIcon(owner, "miscIcons/remove32.gif"));
        putValue(SHORT_DESCRIPTION, "Remove selected pick from memory");
        putValue(MNEMONIC_KEY, KeyEvent.VK_P);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( selectedPick != null ){
            AllStationsPickModel.getInstance().deleteSinglePick(selectedPick);
        }
    }

    public void setSelectedPick(PhasePick dpp) {
        selectedPick = dpp;
    }
}