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
package llnl.gnem.core.database.column;

import llnl.gnem.core.database.column.Column;
import llnl.gnem.core.util.Variant;

/*

 *  COPYRIGHT NOTICE

 *  GnemUtils Version 1.0

 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.

 */

/**
 * A Generic String Column that encapsulates operations common to all String Columns.
 *
 * @author Doug Dodge
 */
public class StringColumn extends Column {
    /**
     * Default Constructor for the StringColumn object
     *
     * @param columnName       The name of the column (Must match the class name)
     * @param columnDefault    The default (NA) value for this Column
     * @param columnWidth      The number of characters wide
     * @param columnConstraint Any Column constraint other than 'NOT NULL'. If no constraint
     *                         is to applied this should be an empty String.
     */
    public StringColumn( String columnName, String columnDefault, int columnWidth, String columnConstraint )
    {
        super( columnName, new Variant( columnDefault ), makeDescriptor( columnName, columnWidth, columnConstraint ) );
        this.columnWidth = columnWidth;
    }

    /**
     * Constructor for the StringColumn object that also sets the value of the Column.
     *
     * @param value            The value to assign to this Column.
     * @param columnName       The name of the column (Must match the class name)
     * @param columnDefault    The default (NA) value for this Column
     * @param columnWidth      The number of characters wide
     * @param columnConstraint Any Column constraint other than 'NOT NULL'. If no constraint
     *                         is to applied this should be an empty String.
     */
    public StringColumn( String columnName, String columnDefault, int columnWidth, String columnConstraint, String value )
    {
        super( columnName, new Variant( columnDefault ), makeDescriptor( columnName, columnWidth, columnConstraint ) );
        this.columnWidth = columnWidth;
        if( value.length() > columnWidth ) {
            StringBuffer msg = new StringBuffer( "Attempt to instantiate new " + columnName + " column " );
            msg.append( "with a String that exceeds the allowable column width. String is: " );
            msg.append( value );
            throw new IllegalArgumentException( msg.toString() );
        }
        thisValue = new Variant( value );
    }

    /**
     * Returns a formatted fixed-width String representation of the StringColumn Column
     * value suitable for use in writing a column-delimited listing of a table's contents.
     *
     * @return The formatted StringColumn Column value as a String.
     */
    public String ValueString()
    {
        boolean alignRight = false;
        return Column.FormatStringForOutput( thisValue.toString(), columnWidth, alignRight );
    }

    /**
     * Return a String representation of the Column value suitable for embedding into
     * an INSERT statement. For numeric values, this will simply be the value converted
     * into a string. For String values, this will be a quoted String no longer than
     * the max width allowed for the database column.
     *
     * @return The formatted String
     */
    public String DbInsertString()
    {
        return Column.makeQuotedString( thisValue.toString(), columnWidth );
    }

    /**
     * Returns the maximum length of a String that can be inserted in this column.
     *
     * @return The maximum String length.
     */
    public int getColumnWidth()
    {
        return columnWidth;
    }

    private int columnWidth;

    private static String makeDescriptor( String columnName, int columnWidth, String columnConstraint )
    {
        StringBuffer s = new StringBuffer( columnName.toUpperCase() + " VARCHAR2(" + columnWidth + ") NOT NULL" );
        if( columnConstraint.length() > 0 )
            s.append( " " + columnConstraint );
        return s.toString();
    }
}





