package llnl.gnem.core.gui.util;

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
