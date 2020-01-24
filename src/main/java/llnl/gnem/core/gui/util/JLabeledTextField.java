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
