/**
 * Created by: dodge1
 * Date: Dec 21, 2004
 *
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
package llnl.gnem.core.gui.plotting.keymapper;


import llnl.gnem.core.gui.util.SpringUtilities;

import javax.swing.*;
import java.util.Vector;


public class KeyMapperGui extends JPanel {
    private Vector<LabeledComboBox> components;

    public KeyMapperGui()
    {
        super( new SpringLayout() );
        components = new Vector<LabeledComboBox>();
    }

    public void addLabeledCombo( LabeledComboBox lcb )
    {
        JLabel label = lcb.getLabel();
        add( label );
        JComboBox box = lcb.getCombo();
        label.setLabelFor( box );
        add( box );
        components.add( lcb );
    }

    public void layoutControls()
    {
        SpringUtilities.makeCompactGrid( this,
                                         components.size(), 2, //rows, cols
                                         6, 6, //initX, initY
                                         6, 6 );       //xPad, yPad
    }


    public void update( Vector<Integer> codes )
    {
        for (LabeledComboBox lcb : components) {
            lcb.update(codes);
        }
    }

}
