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

import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import java.awt.*;

/**
 * ColorTableCellRenderer.java
 * From Core Java 2 Chapter 6
 *
 * User: Eric Matzel
 * Date: Sep 14, 2007
 */

/**
 * This Class is used to allow Color Objects which are included in Table GUI objects to be rendered as the color itself.
 * e.g. the cell would be drawn as a red pane instead of the identifier "java.awt.Color.Red"
 *
 * Usage: table.setDefaultRenderer(Color.class, new ColorTableCellRenderer());
 */
public class ColorTableCellRenderer implements TableCellRenderer
{
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        panel.setBackground((Color) value);
        return panel;
    }

    // The following panel is returned for all cells, with the background color set to the Color value of the cell

    private JPanel panel = new JPanel();
}