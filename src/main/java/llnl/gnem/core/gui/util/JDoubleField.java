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

/**
 * Created by: dodge1
 * Date: Dec 7, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class JDoubleField extends JFormattedTextField {
    private double minAllowableValue = Double.NEGATIVE_INFINITY;
    private double maxAllowableValue = Double.POSITIVE_INFINITY;


    public JDoubleField()
    {
        setValue( 0.0 );
    }

    public JDoubleField( double value )
    {
        setValue( value );
    }

    public JDoubleField( double value, double minValue, double maxValue )
    {
        minAllowableValue = minValue;
        maxAllowableValue = maxValue;
        setValue( value );
    }

    /**
     * Gets the double value stored in this object
     *
     * @return the double value
     */
    public double getDoubleValue()
    {
        try {
            commitEdit();
            Object o = getValue();
            //noinspection IfStatementWithTooManyBranches
            if( o instanceof Double )
                return (Double) o;
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
        catch( Exception e ) {
            e.printStackTrace();
        }
        return 0.0;
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

        super.setValue(value );
    }


}
