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
package llnl.gnem.core.gui.waveform.factory;

import javax.swing.JTabbedPane;
import llnl.gnem.core.gui.map.EditableMap;
import llnl.gnem.core.gui.map.PrintableMap;
import llnl.gnem.core.gui.map.ViewPort;
import llnl.gnem.core.gui.map.origins.OriginViewingMap;
import llnl.gnem.core.gui.util.PersistentPositionContainer;

/**
 *
 * @author addair1
 */
public abstract class MainForm extends PersistentPositionContainer {
    public MainForm(String path, String title, int width, int height) {
        super(path, title, width, height);
    }

    public abstract JTabbedPane getTabbedPane();

    public abstract ViewPort getMapViewPort();

    public abstract PrintableMap getPrintableMap();

    public abstract EditableMap getEditableMap();

    public abstract OriginViewingMap getOriginViewingMap();
}
