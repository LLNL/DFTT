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
import java.text.DecimalFormat;

public class JLabeledIntField extends JLabeledTextField {
    private int minAllowableValue;
    private int maxAllowableValue;
    private JRangeControl rangeControl = null;

    /**
     * Constructor for the JLabeledIntField that does not set any limits on
     * the acceptable values.
     *
     * @param labelText The text of the label accompanying the JTextfield where values are entered.
     * @param value     The initial value to assign to this component
     * @param columns   The width of the JTextField in which the number is displayed
     * @param minWidth  The minimum width of this control
     * @param prefWidth The preferred width of this control
     * @param maxWidth  The maximum width of this control
     * @param height    The height of this control
     */
    public JLabeledIntField( final String labelText, final int value,
                             final int columns, final int minWidth, final int prefWidth,
                             final int maxWidth, final int height )
    {
        super( labelText, value, minWidth, prefWidth, maxWidth, height );
        maxAllowableValue = Integer.MAX_VALUE;
        minAllowableValue = -maxAllowableValue;
        initialize( value, columns );

    }

    /**
     * Constructor that sets an allowable range for this object
     *
     * @param labelText The text of the label accompanying the JTextfield where values are entered.
     * @param value     The initial value to assign to this component
     * @param minValue  The minimum value for this component
     * @param maxValue  The maximum value for this component.
     * @param columns   The width of the JTextField in which the number is displayed
     * @param minWidth  The minimum width of this control
     * @param prefWidth The preferred width of this control
     * @param maxWidth  The maximum width of this control
     * @param height    The height of this control
     */
    public JLabeledIntField( final String labelText, final int value,
                             final int minValue, final int maxValue, final int columns, final int minWidth, final int prefWidth,
                             final int maxWidth, final int height )
    {
        super( labelText, value, minWidth, prefWidth, maxWidth, height );
        maxAllowableValue = minValue;
        minAllowableValue = maxValue;

        if( value < minValue || value > maxValue )
            throw new IllegalArgumentException( "Value is outside of supplied range!" );

        initialize( value, columns );

    }

    public void setRangeControlOwner( JRangeControl control )
    {
        rangeControl = control;
    }

    private void initialize( final int value, final int columns )
    {
        this.remove( field );
        StringBuffer mask = new StringBuffer( "0" );
        DecimalFormat form = new DecimalFormat( mask.toString() );
        form.setParseIntegerOnly( true );
        field = new JFormattedTextField( form );

        field.setValue( new Integer( value ) );
        field.setColumns( columns );
        field.setInputVerifier( new ValueVerifyier() );
        add( field );
    }


    public void setText( final String text )
    {
        // Do nothing. Users should not be able to directly set the text of this control.
    }


    /**
     * Assign a new value to this object programatically
     *
     * @param value The value to assign
     */
    public void setValue( final int value )
    {
        if( value < minAllowableValue || value > maxAllowableValue )
            throw new IllegalArgumentException( "Supplied value is out of range!" );

        field.setValue( new Integer( value ) );
    }

    /**
     * Gets the int value stored in this object
     *
     * @return the int value
     */
    public int getValue()
    {
        try {
            field.commitEdit();
            Object o = field.getValue();
            if( o instanceof Double )
                return ( (Integer) o ).intValue();
            else if( o instanceof Long ){
                Long l = (Long) o;
                return l.intValue();
            }
            else if( o instanceof String ){
                return Integer.parseInt( (String) o );
            }
            else
                return 0;
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return 0;
    }

    class ValueVerifyier extends InputVerifier {
        public boolean verify( JComponent input )
        {
            JFormattedTextField tf = (JFormattedTextField) input;
            String text = tf.getText();
            try {
                Object oldValue = tf.getValue();
                int newValue = Integer.parseInt( text );
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
            catch( Exception e ) {
                tf.setValue( tf.getValue() );
                Toolkit.getDefaultToolkit().beep();
                return false;
            }
        }
    }
}
