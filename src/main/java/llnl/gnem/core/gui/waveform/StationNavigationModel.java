/**
 * Created by dodge1
 * Date: Mar 19, 2012
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
package llnl.gnem.core.gui.waveform;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.util.ButtonAction;
import llnl.gnem.core.gui.util.Utility;

public abstract class StationNavigationModel  {
    private final NextAction next;
    private final PreviousAction previous;

    public StationNavigationModel() {
        next = new NextAction();
        previous = new PreviousAction();
    }

    public void clearEvents() {
        next.setEnabled(false);
        previous.setEnabled(false);
    }

    public void updateCurrentEvent() {
        next.setEnabled(isNextAvailable());
        previous.setEnabled(isPreviousAvailable());
    }

    public Collection<ButtonAction> getActions() {
        Collection<ButtonAction> actions = new ArrayList<>();
        actions.add(previous);
        actions.add(next);
        return actions;
    }

    public class NextAction extends ButtonAction {
        private NextAction() {
            super("", Utility.getIcon(StationNavigationModel.this, "miscIcons/next32.gif"));
            putValue(NAME, "Next Station (Forward)");
            putValue(SHORT_DESCRIPTION, "Move forward to next station");
            putValue(MNEMONIC_KEY, KeyEvent.VK_F);
            setEnabled(isNextAvailable());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setEnabled(false);
            StationInfo stationInfo = getNext();
            setSelectedStation(stationInfo);
        }
    }

    public class PreviousAction extends ButtonAction {
        private PreviousAction() {
            super("", Utility.getIcon(StationNavigationModel.this, "miscIcons/previous32.gif"));
            putValue(NAME, "Previous Station (Back)");
            putValue(SHORT_DESCRIPTION, "Move back to previous station");
            putValue(MNEMONIC_KEY, KeyEvent.VK_B);
            setEnabled(isPreviousAvailable());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setEnabled(false);
            StationInfo stationInfo = getPrevious();
            setSelectedStation(stationInfo);
        }
    }

    public abstract boolean isNextAvailable();

    public abstract boolean isPreviousAvailable();

    public abstract StationInfo getNext();

    public abstract StationInfo getPrevious();

    public abstract void setSelectedStation(StationInfo StationInfo);
}
