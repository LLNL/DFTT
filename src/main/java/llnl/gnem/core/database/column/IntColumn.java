package llnl.gnem.core.database.column;

import llnl.gnem.core.database.column.Column;
import llnl.gnem.core.util.Variant;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * The base class for all database Columns that hold an integer value..
 *
 * @author Doug Dodge
 */
public class IntColumn extends Column {
    /**
     * Default Constructor for the IntColumn object
     *
     * @param columnName       Name of the Column in the database (Must match the class
     *                         name)
     * @param defaultValue     The default (NA) value for this Column
     * @param precision        The number of digits of precision to store
     * @param constraintString A Constraint other than 'NOT NULL'
     */
    public IntColumn( String columnName, int defaultValue, int precision, String constraintString )
    {
        super( columnName, new Variant( defaultValue ), getSpecifierString( columnName, defaultValue, precision, constraintString ) );
        this.setPrecision(precision);
    }

    /**
     * Constructor for the IntColumn object that also sets the value of the IntColumn.
     *
     * @param value            The column value.
     * @param columnName       Name of the Column in the database (Must match the class
     *                         name)
     * @param defaultValue     The default (NA) value for this Column
     * @param precision        The number of digits of precision to store
     * @param constraintString A Constraint other than 'NOT NULL'
     */
    public IntColumn( String columnName, int defaultValue, int precision, String constraintString, int value )
    {
        super( columnName, new Variant( defaultValue ), getSpecifierString( columnName, defaultValue, precision, constraintString ) );
        this.setPrecision(precision);
        thisValue = Validate( new Variant( value ) );
    }

    /**
     * Returns a formatted fixed-width String representation of the IntColumn Column
     * value suitable for use in writing a column-delimited listing of a table's contents.
     *
     * @return The formatted IntColumn Column value as a String.
     */
    public String ValueString()
    {
        boolean alignRight = true;
        return Column.FormatStringForOutput( thisValue.toString(), precision, alignRight );
    }

    /**
     * Return a String representation of the Column value suitable for embedding into
     * an INSERT statement. For numeric values, this will simply be the value converted
     * into a string.
     *
     * @return The formatted String
     */
    public String DbInsertString()
    {
        return thisValue.toString();
    }

    public int getColumnWidth()
    {
        return precision;
    }

    private int precision;

    private static String getSpecifierString( String columnName, int defaultValue, int precision, String constraintString )
    {
        int Precision = precision >= 8 ? precision : 8;
        StringBuffer s = new StringBuffer( columnName.toUpperCase() + " NUMBER(" + Precision + ") NOT NULL" );
        if( constraintString.length() > 0 )
            s.append(" ").append(constraintString);
        return s.toString();
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }
}





