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
