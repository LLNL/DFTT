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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


/**
 * A class that encapsulates a JLabel and an associated JTextField. Because both the JLabel
 * and the JTextField are in the same container, layout managers deal with them as a unit.
 */
public class JLabeledTextField extends JPanel {

    private static final long serialVersionUID = -2356924812404069348L;

    /**
     * Constructor for the JLabeledTextField
     *
     * @param labelText The text displayed by the label
     * @param text      The text displayed by the JTextField
     * @param minWidth  The minimum width of this control
     * @param prefWidth The preferred width of this control
     * @param maxWidth  The maximum width of this control
     * @param height    The height of this control
     */
    public JLabeledTextField( final String labelText, final Number text, final int minWidth,
                              final int prefWidth, final int maxWidth, final int height )
    {
        field = new JFormattedTextField( text );
        setMaximumSize( new Dimension( maxWidth, height ) );
        setPreferredSize( new Dimension( prefWidth, height ) );
        setMinimumSize( new Dimension( minWidth, height ) );
        label = new JLabel( labelText, JLabel.TRAILING );
        label.setLabelFor( field );
        add( label );
        add( field );
    }
    
    public void addActionListener( ActionListener listener)
    {
        field.addActionListener(listener);
    }

    public void setColumns( int columns )
    {
        field.setColumns( columns );
    }

    public JLabel getLabel()
    {
        return label;
    }

    public JTextField getTextField()
    {
        return field;
    }

    public String getText()
    {
        return field.getText();
    }

    public void setText( final String text )
    {
        field.setText( text );
    }


    protected JFormattedTextField field;
    protected JLabel label;

}
