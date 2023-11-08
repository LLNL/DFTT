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

import llnl.gnem.dftt.core.util.HumanTimeFormatType;
import llnl.gnem.dftt.core.util.TimeFormat;
import llnl.gnem.dftt.core.util.TimeT;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by: dodge1
 * Date: Jul 2, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */

/**
 * A class that allows display and editing of date/time values using several fixed
 * formats for display/editing. The display consists of a TextField in which the display/editing
 * occurs placed above a JComboBox that is used to control the format in
 * which date/time values are entered. Formats currently available are:
 * yyyyDDD, yyyy/MM/dd, yyyy/MM/dd-HH:mm:ss.SSS, yyyy/DDD-HH:mm:ss.SSS.
 * Changing the format automatically re-formats the displayed date/time to match the new format.
 * Validation of user-supplied times occurs when the JTextField loses focus.
 */
public class JDateField extends JPanel implements ActionListener, FocusListener {

    private String[] items;
    private final JComboBox chooser;
    private final JFormattedTextField textField;
    private final JFormattedTextField epochField;
    private TimeFormat timeFormat;

    private final int fieldWidth = 200;
    private final int fieldHeight = 20;
    private final Dimension controlSize = new Dimension(fieldWidth, fieldHeight);

    public JDateField()
    {

        super( new SpringLayout() );
        fillChooserArray();


        SimpleDateFormat dateFormat = new SimpleDateFormat( items[0] );
        dateFormat.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
        textField = new JFormattedTextField( dateFormat );
        textField.setColumns( 13 );
        textField.setValue( new Date() );
        add( textField );
        textField.setMinimumSize( controlSize );
        textField.setMaximumSize( controlSize );
        textField.setPreferredSize( controlSize );
        textField.putClientProperty( "terminateEditOnFocusLost", Boolean.TRUE );

        chooser = new JComboBox( items );
        chooser.addActionListener( this );
        add( chooser );
        chooser.setMinimumSize( controlSize );
        chooser.setMaximumSize( controlSize );
        chooser.setPreferredSize( controlSize );

        final int numRows = 2;
        final int numCols = 1;
        final int padding = 0;
        SpringUtilities.makeCompactGrid( this, numRows, numCols, padding, padding, padding, padding );


        TimeT tmp = getTime();
        epochField = new JFormattedTextField( tmp.getEpochTime());
        epochField.setColumns( 13 );
        epochField.addFocusListener( this );
        timeFormat = TimeFormat.HUMAN;
    }

    private void fillChooserArray()
    {
        int numItems = HumanTimeFormatType.values().length;
        items = new String[numItems + 1];
        for ( HumanTimeFormatType type : HumanTimeFormatType.values() )
            items[type.ordinal()] = type.toString();
        items[numItems] = "Epoch";
    }

    @Override
    public void setEnabled( boolean enabled )
    {
        textField.setEnabled( enabled );
        chooser.setEnabled( enabled );
        epochField.setEnabled( enabled );
    }

    public void setTimeFormat( TimeFormat newFormat )
    {
        if( newFormat == timeFormat )
            return;

        if( newFormat == TimeFormat.EPOCH ){
            epochField.setValue( getTime().getEpochTime());
            remove( textField );
            add( epochField, 0 );
            if( chooser.getSelectedIndex() != 4 )
                chooser.setSelectedIndex( 4 );
        }
        else{
            setTime( new TimeT( ( (Double) epochField.getValue() )) );
            remove( epochField );
            add( textField, 0 );
            final int numRows = 2;
            final int numCols = 1;
            final int padding = 0;
            SpringUtilities.makeCompactGrid( this, numRows, numCols, padding, padding, padding, padding );
        }
        timeFormat = newFormat;
        validate();
    }

    @Override
    public void addKeyListener( KeyListener keyListener )
    {
        textField.addKeyListener( keyListener );
        epochField.addKeyListener( keyListener );
    }

    public void commitEdit() throws ParseException
    {
        if( timeFormat == TimeFormat.EPOCH ){
            epochField.commitEdit();
            setTime( new TimeT( ( (Double) epochField.getValue() )) );
        }
        else
            textField.commitEdit();
    }


    @Override
    public void actionPerformed( ActionEvent e )
    {
        int index = chooser.getSelectedIndex();
        if( index < 4 ){  // Change to or within human formats...
            SimpleDateFormat dateFormat = new SimpleDateFormat( items[index] );
            dateFormat.setTimeZone( TimeZone.getTimeZone( "GMT" ) );

            DateFormatter fmt = (DateFormatter) textField.getFormatter();
            fmt.setFormat( dateFormat );
            if( timeFormat == TimeFormat.HUMAN ){   // switching among human-formats...
                textField.setValue( textField.getValue() );
            }
            else if( timeFormat == TimeFormat.EPOCH ){  // switching from epoch to human...
                setTimeFormat( TimeFormat.HUMAN );
            }
        }
        else{
            setTimeFormat( TimeFormat.EPOCH );
        }
    }

    public void setHumanTimeFormatType( HumanTimeFormatType type )
    {
        if( timeFormat == TimeFormat.HUMAN )
            chooser.setSelectedIndex( type.ordinal() );
    }

    /**
     * Gets the current date/time as a TimeT object.
     *
     * @return A TimeT object containing the currently set Time.
     */
    public final TimeT getTime()
    {
        if( timeFormat == TimeFormat.EPOCH ){
            return new TimeT( (Double) epochField.getValue() );
        }
        else{
            Object temp = textField.getValue();
            if( temp != null ){
                try {
                    Date date = (Date) temp;
                    return new TimeT( date );
                }
                catch( Exception e ) {
                    return null;
                }
            }
            return null;
        }
    }


    /**
     * Sets the time stored in the JDateField and updates the display.
     *
     * @param time The new time to assign to the control.
     */
    public void setTime( final TimeT time )
    {
        Date date = time.getDate();
        textField.setValue( date );
        epochField.setValue( time.getEpochTime());
    }

    @Override
    public void focusGained( FocusEvent e )
    {

    }

    @Override
    public void focusLost( FocusEvent e )
    {
        try {
            epochField.commitEdit();
        }
        catch( ParseException ex ) {
        }
        Double temp = (Double) epochField.getValue();
        TimeT tmp = new TimeT( temp);
        textField.setValue( tmp.getDate() );
    }

    public void setChooserVisibility( boolean visible )
    {
        chooser.setVisible( visible );
        if( !visible ){
            chooser.setMinimumSize( new Dimension( 0, 0 ) );
            chooser.setMaximumSize( new Dimension( 0, 0 ) );
            chooser.setPreferredSize( new Dimension( 0, 0 ) );
        }
        else{
            chooser.setMinimumSize( controlSize );
            chooser.setMaximumSize( controlSize );
            chooser.setPreferredSize( controlSize );
        }
    }

}
