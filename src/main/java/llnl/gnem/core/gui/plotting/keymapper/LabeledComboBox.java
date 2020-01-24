package llnl.gnem.core.gui.plotting.keymapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

/**
 * Created by: dodge1
 * Date: Dec 22, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 * Will not inspect for: MagicNumber
 */
public class LabeledComboBox implements ActionListener {
    public JLabel getLabel()
    {
        return label;
    }

    public JComboBox getCombo()
    {
        return combo;
    }

    private JLabel label;
    private JComboBox combo;
    private KeyMapperModel model;
    private DefaultComboBoxModel comboModel;

    public LabeledComboBox( KeyMap map, KeyMapperModel model )
    {
        this.model = model;
        Vector<CodeWrapper> codes = new Vector<CodeWrapper>();
        label = new JLabel( map.getDescription() );
        codes.add( new CodeWrapper( map.getKeyCode() ) );
        Vector<Integer> availableCodes = model.getAvailableCodes();
        for (Integer tmp : availableCodes) {
            codes.add(new CodeWrapper(tmp));
        }
        comboModel = new DefaultComboBoxModel( codes );
        combo = new JComboBox( comboModel );
        combo.setPreferredSize( new Dimension( 100, 25 ) );
        combo.setMaximumSize( new Dimension( 100, 25 ) );
        combo.addActionListener( this );
    }

    public void update( Vector<Integer> availableCodes )
    {
        CodeWrapper cw = (CodeWrapper) combo.getSelectedItem();
        comboModel.removeAllElements();
        comboModel.addElement( cw );
        for (Integer tmp : availableCodes) {
            comboModel.addElement(new CodeWrapper(tmp));
        }
    }


    public void actionPerformed( ActionEvent e )
    {
        JComboBox cb = (JComboBox) e.getSource();
        if( cb == combo ) {
            CodeWrapper cw = (CodeWrapper) combo.getSelectedItem();
            if( cw != null )
                model.setKeyCode( label.getText(), cw.code );
        }
    }


    class CodeWrapper {
        public int code;

        public CodeWrapper( int code )
        {
            this.code = code;
        }


        public String toString()
        {
            return KeyEvent.getKeyText( code );
        }
    }
}
