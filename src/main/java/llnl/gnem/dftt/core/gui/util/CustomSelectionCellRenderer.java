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
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Feb 9, 2006
 */
@SuppressWarnings({"MagicNumber"})
public class CustomSelectionCellRenderer extends DefaultTableCellRenderer {

    NumberFormat intFormatter;
    NumberFormat doubleFormatter;
    Color selectedColor;
    Color customSelectedColor;
    Color bothSelected;

    public CustomSelectionCellRenderer()
    {
        super();
        intFormatter = NumberFormat.getIntegerInstance();
        intFormatter.setGroupingUsed( false );
        doubleFormatter = NumberFormat.getNumberInstance();
        doubleFormatter.setMaximumFractionDigits( 4 );
        selectedColor = new Color( 200, 200, 255 );
        customSelectedColor = new Color( 255, 200, 200 );
        bothSelected = new Color( 255, 200, 255 );
    }


    public Component getTableCellRendererComponent( JTable table, Object value,
                                                    boolean isSelected,
                                                    boolean hasFocus,
                                                    int row, int column )
    {

        Component comp =
                super.getTableCellRendererComponent( table, value, isSelected, hasFocus,
                                                     row, column );

        JLabel label = (JLabel) comp;

        label.setBackground( Color.white );
        if( isSelected ){
            label.setBackground( selectedColor );
        }

        if( table instanceof CustomRowSelectionTable ){
            CustomRowSelectionTable crst = (CustomRowSelectionTable) table;
            if( row == crst.getCustomSelectedRow() ){
                label.setBackground( isSelected ? bothSelected : customSelectedColor );
            }
        }
        label.setHorizontalAlignment( JLabel.LEFT );
        if( value instanceof Double ){
            Double val = (Double) value;
            String tmp = doubleFormatter.format( val );
            label.setText( tmp );
            label.setHorizontalAlignment( JLabel.RIGHT );

        }
        else if( value instanceof Integer ){
            Integer val = (Integer) value;
            String tmp = intFormatter.format( val );
            label.setText( tmp );
            label.setHorizontalAlignment( JLabel.RIGHT );
        }
        else
            label.setText( value.toString() );
        return label;
    }
}
