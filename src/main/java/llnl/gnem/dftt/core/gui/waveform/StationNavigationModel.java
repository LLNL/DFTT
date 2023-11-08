/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.dftt.core.gui.waveform;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.dftt.core.gui.map.stations.StationInfo;
import llnl.gnem.dftt.core.util.ButtonAction;
import llnl.gnem.dftt.core.gui.util.Utility;

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
