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
import java.awt.*;
import java.text.DecimalFormat;
import java.text.ParseException;


/**
 * A class that allows display and editing of a labeled double value. User-typed Strings
 * are constrained to be valid fixed-point values (no scientific notation), and optionally
 * constrained to be within defined limits.
 */
public class JLabeledDoubleField extends JLabeledTextField {

    private static final long serialVersionUID = -8550263128444188349L;
    private final double minAllowableValue;
    private final double maxAllowableValue;
    private JRangeControl rangeControl = null;

    /**
     * Constructor for the JLabeledDoubleField that does not set any limits on
     * the acceptable values.
     *
     * @param labelText The text of the label accompanying the JTextfield where values are entered.
     * @param value     The initial value to assign to this component
     * @param precision The number of decimal places to present and store
     * @param columns   The width of the JTextField in which the number is displayed
     * @param minWidth  The minimum width of this control
     * @param prefWidth The preferred width of this control
     * @param maxWidth  The maximum width of this control
     * @param height    The height of this control
     */
    public JLabeledDoubleField( final String labelText, final double value, final int precision,
                                final int columns, final int minWidth, final int prefWidth,
                                final int maxWidth, final int height )
    {
        super( labelText, value, minWidth, prefWidth, maxWidth, height );
        maxAllowableValue = Double.MAX_VALUE;
        minAllowableValue = -maxAllowableValue;
        initialize( precision, value, columns );

    }

    /**
     * Constructor that sets an allowable range for this component
     *
     * @param labelText The text of the label accompanying the JTextfield where values are entered.
     * @param value     The initial value to assign to this component
     * @param precision The number of decimal places to present and store
     * @param minValue  The minimum value for this component
     * @param maxValue  The maximum value for this component.
     * @param columns   The width of the JTextField in which the number is displayed
     * @param minWidth  The minimum width of this control
     * @param prefWidth The preferred width of this control
     * @param maxWidth  The maximum width of this control
     * @param height    The height of this control
     */
    public JLabeledDoubleField( final String labelText, final double value, final int precision,
                                final double minValue, final double maxValue, final int columns, final int minWidth, final int prefWidth,
                                final int maxWidth, final int height )
    {
        super( labelText, value, minWidth, prefWidth, maxWidth, height );
        maxAllowableValue = maxValue;
        minAllowableValue = minValue;

        if( value < minValue || value > maxValue )
            throw new IllegalArgumentException( "Value is outside of supplied range!" );

        initialize( precision, value, columns );

    }


    public void setRangeControlOwner( JRangeControl control )
    {
        rangeControl = control;
    }


    private void initialize( final int precision, final double value, final int columns )
    {
        this.remove( field );
        StringBuilder mask = new StringBuilder( "###." );
        for ( int j = 0; j < precision; ++j )
            mask.append( "#" );
        field = new JFormattedTextField( new DecimalFormat( mask.toString() ) );

        field.setValue(value);
        field.setColumns( columns );
        field.setInputVerifier( new ValueVerifyier() );
        add( field );
    }


    @Override
    public void setText( final String text )
    {
        // Do nothing. Users should not be able to directly set the text of this control.
    }


    /**
     * Assign a new value to this control programatically
     *
     * @param value The value to assign
     */
    public void setValue( final double value )
    {
        if( value < minAllowableValue || value > maxAllowableValue ){
            StringBuffer msg = new StringBuffer( "Supplied value of " );
            msg.append( value );
            msg.append( " is not in the allowed range of (" );
            msg.append( minAllowableValue );
            msg.append( " to " );
            msg.append( maxAllowableValue );
            msg.append( ")!" );
            throw new IllegalArgumentException( msg.toString() );
        }

        field.setValue( new Double( value ) );
    }


    /**
     * Gets the double value stored in this object
     *
     * @return the double value
     */
    public double getValue()
    {
        try {
            field.commitEdit();
            Object o = field.getValue();
            if( o instanceof Double )
                return ( (Double) o );
            else if( o instanceof Long ){
                Long l = (Long) o;
                return l.doubleValue();
            }
            else if( o instanceof String ){
                return Double.parseDouble( (String) o );
            }
            else
                return 0;
        }
        catch( NumberFormatException | ParseException e ) {
            e.printStackTrace();
        }
        return 0.0;
    }

    class ValueVerifyier extends InputVerifier {
        @Override
        public boolean verify( JComponent input )
        {

            JFormattedTextField tf = (JFormattedTextField) input;
            String text = tf.getText();
            try {
                Object oldValue = tf.getValue();
                double newValue = Double.parseDouble( text );
                if( newValue < minAllowableValue || newValue > maxAllowableValue ){
                    Toolkit.getDefaultToolkit().beep();
                    tf.setValue( tf.getValue() );
                    return false;
                }
                else{
                    if( rangeControl != null ){
                        if( !rangeControl.ValidateChange() ){
                            Toolkit.getDefaultToolkit().beep();
                            tf.setValue( oldValue );
                            return false;
                        }
                        else
                            return true;
                    }
                    else
                        return true;
                }
            }
            catch( NumberFormatException e ) {
                tf.setValue( tf.getValue() );
                Toolkit.getDefaultToolkit().beep();
                return false;
            }
        }
    }
}
