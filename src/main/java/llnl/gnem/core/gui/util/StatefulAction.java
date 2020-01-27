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
package llnl.gnem.core.gui.util;

import java.awt.event.ActionEvent;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import llnl.gnem.core.gui.util.StatefulAction.ActionState;
import llnl.gnem.core.util.ButtonAction;

/**
 *
 * @author addair1
 * @param <T>
 */
public abstract class StatefulAction<T extends ActionState> extends ButtonAction {
    private T currentState;

    public StatefulAction(T firstState, String name, Object owner) {
        super(name, Utility.getIcon(owner, firstState.getIconPath()));

        putValue(SHORT_DESCRIPTION, firstState.getDescription());
        currentState = firstState;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setState(getNext());
    }

    protected T getCurrentState() {
        return currentState;
    }

    protected void handleStateChange(T state) {
    }

    private void setState(T state) {
        currentState = state;

        ImageIcon icon = Utility.getIcon(this, currentState.getIconPath());

        AbstractButton button = getButton();
        putValue(SHORT_DESCRIPTION, currentState.getDescription());
        putValue("SMALL_ICON", icon);
        button.setIcon(icon);
        button.setToolTipText(currentState.getDescription());

        handleStateChange(currentState);
    }

    protected abstract T getNext();

    public interface ActionState {
        public String getDescription();

        public String getIconPath();
    }
}
