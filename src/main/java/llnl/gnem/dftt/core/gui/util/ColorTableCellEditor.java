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
package llnl.gnem.dftt.core.gui.util;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;
import java.util.EventObject;

/**
 * ColorTableCellEditor.java
 * From Core Java 2 Chapter 6
 *
 * User: Eric Matzel
 * Date: Sep 14, 2007
 *
 * This editor pops up a color dialog to edit a cell value
 *
 * usage: table.setDefaultEditor(Color.class, new ColorTableCellEditor());
 */
public class ColorTableCellEditor extends AbstractCellEditor implements TableCellEditor
{
    public ColorTableCellEditor()
    {
        panel = new JPanel();

        // prepare color dialog

        colorChooser = new JColorChooser();
        colorDialog = JColorChooser.createDialog(null, "set color", false, colorChooser,
                new ActionListener() // OK Button Listener
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        stopCellEditing();
                    }
                },
                new ActionListener() // Cancel Button Listener
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        cancelCellEditing();
                    }
                });

        colorDialog.addWindowListener(
                new WindowAdapter()
                {
                    public void windowClosing(WindowEvent event)
                    {
                        cancelCellEditing();
                    }
                });
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        // This is where  we get  the  current color value. We store it in the dialog in case the user starts editing.
        colorChooser.setColor((Color) value);
        return panel;
    }

    public boolean shouldSelectCell(EventObject anEvent)
    {
        // start editing
        colorDialog.setVisible(true);

        // tell caller it is ok to select this cell
        return true;
    }

    public void cancelCellEditing()
    {
        // editing is canceled -- hide dialog
        colorDialog.setVisible(false);
        super.cancelCellEditing();
    }

    public boolean stopCellEditing()
    {
        // editing  is complete -- hide dialog
        colorDialog.setVisible(false);
        super.stopCellEditing();

        // tell caller it is ok to use color values
        return true;
    }

    public Object getCellEditorValue()
    {
        return colorChooser.getColor();
    }

    private JColorChooser colorChooser;
    private JDialog colorDialog;
    private JPanel panel;
}