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
package llnl.gnem.core.gui.filter;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JToolBar;
import llnl.gnem.core.gui.filter.actions.*;

/**
 *
 * @author dodge1
 */
public class FilterGuiToolbar extends JToolBar {
    public FilterGuiToolbar(FilterGuiContainer owner) {
        super();

        JButton button = new JButton(AddCurrentAction.getInstance(this));
        addButton(button);

        button = new JButton(RemoveFilterAction.getInstance(this));
        addButton(button);
        this.addSeparator();


        button = new JButton(new ApplyFilterAction(owner));
        addButton(button);

        button = new JButton(new UnapplyFilterAction(owner));
        addButton(button);

        this.addSeparator();
        button = new JButton(new ExitAction(owner));
        addButton(button);
    }

    private void addButton(JButton button) {
        if (button.getIcon() != null) {
            button.setText(""); //an icon-only button
        }
        button.setPreferredSize(new Dimension(22, 22));
        button.setMaximumSize(new Dimension(22, 22));
        add(button);
    }

    public void setFilterGui(FilterGui gui) {
        AddCurrentAction.getInstance(this).setFilterGui(gui);
    }
}
