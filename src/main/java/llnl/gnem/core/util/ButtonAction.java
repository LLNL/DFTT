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
package llnl.gnem.core.util;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 *
 * @author addair1
 */
public abstract class ButtonAction extends AbstractAction {

    private static final long serialVersionUID = -3674318330014671158L;
    private AbstractButton button;

    public ButtonAction(String name, Icon icon) {
        super(name, icon);
    }

    public void updateState() {
        setEnabled(true);
    }

    public AbstractButton getButton() {
        if (button == null)
            createButton();
        return button;
    }

    public boolean isSelected() {
        return button.isSelected();
    }

    public boolean hasMnemonic() {
        return getValue(MNEMONIC_KEY) != null;
    }

    public int getMnemonic() {
        return (Integer) getValue(MNEMONIC_KEY);
    }

    protected final void setButton(AbstractButton button) {
        this.button = button;
    }

    protected void createButton() {
        button = new JButton(this);
    }
}
