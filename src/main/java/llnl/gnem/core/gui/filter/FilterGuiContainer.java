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

import java.awt.BorderLayout;
import java.awt.Image;
import llnl.gnem.core.gui.util.PersistentPositionContainer;

/**
 *
 * @author dodge1
 */
public class FilterGuiContainer extends PersistentPositionContainer {

    private static final long serialVersionUID = -7001958459643230775L;

    private final FilterGui gui;

    public FilterGuiContainer(String path, String title, Image icon) {
        super(path, title, 480, 310);

        setIconImage(icon);
        gui = new FilterGui(this);
        FilterModel.getInstance().addView(gui);

        FilterGuiToolbar toolbar = new FilterGuiToolbar(this);
        toolbar.setFilterGui(gui);
        getContentPane().add(toolbar, BorderLayout.NORTH);
        getContentPane().add(gui, BorderLayout.CENTER);
        this.setSize(480, 310);
        this.setResizable(false);
        updateCaption();
    }

    public FilterGui getGui() {
        return gui;
    }


    public FilterToolbarControl getFilterToolbarControl() {
        return gui.getFilterToolbarControl();
    }


}
