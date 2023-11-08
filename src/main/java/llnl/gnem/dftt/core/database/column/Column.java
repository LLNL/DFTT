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
package llnl.gnem.dftt.core.database.column;

import llnl.gnem.dftt.core.util.Variant;

import java.text.NumberFormat;

/*
*  COPYRIGHT NOTICE
*  GnemUtils Version 1.0
*  Copyright (C) 2002 Lawrence Livermore National Laboratory.
*/

/**
 * Column is a class to represent a generic database column. It provides all the
 * methods for accessing and modifying column data. Specialization of Column only
 * requires that the subclass have a constructor that defines data elements specific
 * to the specialized class.
 *
 * @author Doug Dodge
 */
public abstract class Column implements Cloneable {
    /**
     * Constructor called by subclass constructors to pass the instance-specific data
     * to this object.
     *
     * @param theName      The name of the specialized column
     * @param theDefault   The default value of the column
     * @param theSpecifier The SQL column specifier required when creating a table
     *                     with this column
     */
    protected Column( String theName, Variant theDefault, String theSpecifier )
    {
        name = theName;
        defaultValue = new Variant( theDefault );
        thisValue = new Variant( theDefault );
        specifier = theSpecifier;
    }

    /**
     * Constructor for the Column object
     *
     * @param theName    Description of the Parameter
     * @param theDefault Description of the Parameter
     */
    protected Column( String theName, Variant theDefault )
    {
        name = theName;
        defaultValue = new Variant( theDefault );
        thisValue = new Variant( theDefault );
        specifier = null;
    }

    /**
     * Constructor for the Column object
     *
     * @param theName    Description of the Parameter
     * @param theDefault Description of the Parameter
     * @param theValue   Description of the Parameter
     */
    protected Column( String theName, Variant theDefault, Variant theValue )
    {
        name = theName;
        defaultValue = new Variant( theDefault );
        thisValue = new Variant( theValue );
        specifier = null;
    }

    /**
     * Sets the specifier attribute of the Column object
     *
     * @param theSpecifier The new specifier value
     */
    protected void setSpecifier( String theSpecifier )
    {
        specifier = theSpecifier;
    }

    /**
     * Make a deep copy of this Column object
     *
     * @return A deep copy of this object.
     */
    public Object clone() throws CloneNotSupportedException {
        Column c = null;
        try {
            c = (Column) super.clone();
        }
        catch( CloneNotSupportedException e ) {
            e.printStackTrace( System.err );
        }
        assert c != null;
        c.thisValue = new Variant( thisValue );
        return c;
    }

    /**
     * Gets the name attribute of the Column object
     *
     * @return The name value
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the default attribute of the Column object
     *
     * @return The default value
     */
    public Variant getDefault()
    {
        return defaultValue;
    }

    /**
     * Gets the columnSpecifier attribute of the Column object. This is the SQL that
     * defines this column during table creation.
     *
     * @return The columnSpecifier value
     */
    public String getColumnSpecifier()
    {
        return specifier;
    }

    /**
     * Sets the value of the Column object
     *
     * @param value The new Column value
     */
    public void setValue( Variant value )
    {
        thisValue = Validate( new Variant( value ) );
    }

    /**
     * Gets the value of the Column object
     *
     * @return The value of This Column
     */
    public Variant getValue()
    {
        return new Variant( thisValue );
    }

    /**
     * Returns true if this Column is stored in the database as an integer
     *
     * @return true if this is an int type Column.
     */
    public boolean getIsIntType()
    {
        return thisValue.IsInteger();
    }

    /**
     * Returns true if this Column is stored in the database as a float or double
     *
     * @return true if this is a float type Column.
     */
    public boolean getIsFloatType()
    {
        return thisValue.IsFloatType();
    }

    /**
     * Returns true if this Column is stored in the database as text
     *
     * @return true if this is a text Column.
     */
    public boolean getIsText()
    {
        return thisValue.IsText();
    }

    /**
     * Returns true i fthis Column is stored in the Database as a Date
     *
     * @return true if this is a Date Column
     */
    public boolean getIsDateType()
    {
        return thisValue.IsDate();
    }

    /**
     * Given a String representation of a new Column value, parses the String
     * and updates the Column's value with the parsed result. The String representations
     * are assumed to be appropriate for the column type. Does not do anything for Date
     * Columns.
     *
     * @param stringValue
     */
    public void setValueFromString( String stringValue )
    {
        if( getIsText() ){
            setValue( new Variant( stringValue ) );
        }
        else if( getIsIntType() ){
            setValue( new Variant( Integer.parseInt( stringValue ) ) );
        }
        else if( getIsFloatType() ){
            setValue( new Variant( Double.parseDouble( stringValue ) ) );
        }
    }

    /**
     * Method to ensure that values input to the setValue method are in range for the
     * Column. Derived classes can override this method to do any kind of checking
     * / modification that they need to. In the base class implementation, the input
     * value is passed through unchanged.
     *
     * @param value The input value to be checked and possibly modified.
     * @return The validated data, ready to be used to set the value in this Column.
     */
    public Variant Validate( Variant value )
    {
        return value;
    }

    /**
     * Get a String representation of the Column.
     *
     * @return A String representation of this column
     */
    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder( "Name = " + name + ", Value = " + thisValue );
        s.append(", Default = ").append(defaultValue).append(", Specifier = ").append(specifier);
        return s.toString();
    }

    /**
     * Returns a formatted String representation of the Column value suitable for use
     * in writing a comma-delimited listing of a table's contents.
     *
     * @return The formatted Column value as a String.
     */
    public abstract String ValueString();

    /**
     * Return a String representation of the Column value suitable for embedding into
     * an INSERT statement. For numeric values, this will simply be the value converted
     * into a string. For String values, this will be a quoted String no longer than
     * the max width allowed for the database column.
     *
     * @return The formatted String
     */
    public abstract String DbInsertString();

    public abstract int getColumnWidth();

    /**
     * Compare two Column objects for equality.
     *
     * @param o The object to be compared for equality.
     * @return true if the contents of the two objects are identical.
     */
    public boolean equals( Object o )
    {
        if( o == this )
            return true;
        if( o instanceof Column ){
            // No need to check for null because instanceof handles that check

            Column tmp = (Column) o;
            return thisValue.equals( tmp.thisValue ) && name.equals( tmp.name ) && defaultValue.equals( tmp.defaultValue ) && specifier.equals( tmp.specifier );
        }
        else
            return false;
    }

    /**
     * Returns an int hash code based on the contents of the Column.
     *
     * @return An int hashCode value.
     */
    public int hashCode()
    {
        return thisValue.hashCode() ^ name.hashCode() ^ defaultValue.hashCode() ^ specifier.hashCode();
    }

    /**
     * Convert a float to a String with a specified number of decimal places.
     *
     * @param v             The input value to be converted
     * @param decimalPlaces The number of decimal places to output. The number will
     *                      be rounded to this value.
     * @return A String representation of the floating point value with
     *         the specified number of decimal places.
     */
    protected static String floatToString( double v, int decimalPlaces )
    {
        NumberFormat form = NumberFormat.getInstance();
        form.setMinimumFractionDigits( decimalPlaces );
        form.setMaximumFractionDigits( decimalPlaces );
        form.setGroupingUsed( false );
        return form.format( v );
    }

    /**
     * Formats a String to fill a fixed width and aligns it to either the left or the
     * right. The String is truncated if necessary
     *
     * @param s          The input String to be formatted
     * @param width      The width of the output String
     * @param alignRight true if the output String is to be right-aligned.
     * @return The formatted String.
     */
    protected static String FormatStringForOutput( String s, int width, boolean alignRight )
    {
        StringBuffer sb = new StringBuffer();
        for ( int j = 0; j < width; ++j )
            sb.append( " " );
        s = s.trim();
        if( s.length() > width )
            s = s.substring( 0, width );
        int L = s.length();
        if( alignRight ){
            int first = width - L;
            int last = first + L;
            sb.replace( first, last, s );
        }
        else{
            int first = 0;
            sb.replace( first, L, s );
        }
        return sb.toString();
    }

    /**
     * Given an input String, return a copy enclosed in single-quotes and with any
     * embedded single-quotes converted to a pair of single-quotes. The returned String
     * may be truncated to a maximum length of 'width' not counting single-quotes.
     *
     * @param s     The input String
     * @param width Max width of the output string ignoring embedded single-quotes.
     * @return The quoted String
     */
    public static String makeQuotedString( String s, int width )
    {
        // First make it fit into the field

        if( s.length() > width )
            s = s.substring( 0, width );

        // Now replace any embedded single-quotes with escaped ('') single quotes

        int pos = 0;
        StringBuffer s2 = new StringBuffer( s );
        while( ( pos = s2.indexOf( "'", pos ) ) >= 0 ){
            s2.insert( pos, "'" );
            pos += 2;
        }

        // Now surround with single-quotes.

        return "'" + s2 + "'";
    }

    public boolean isNull()
    {
        return defaultValue.isNullData();
    }

    public void setNull()
    {
        defaultValue.setNullData(true);
    }

    /**
     * The value of this Column stored as a variant.
     */
    protected Variant thisValue;
    /**
     * The name of this Column
     */
    protected final String name;
    /**
     * The default value of this column
     */
    protected final Variant defaultValue;
    /**
     * The SQL create string used when creating a table.
     */
    protected String specifier;


}





