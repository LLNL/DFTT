package llnl.gnem.core.database.column;

import llnl.gnem.core.database.column.Column;
import llnl.gnem.core.util.Variant;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A Column for floats or doubles
 *
 * @author Doug Dodge
 */
public class FloatColumn extends Column {
    /**
     * Constructor for a FloatColumn that sets all fields except for the value of the
     * Column
     *
     * @param name                 Name of the Column
     * @param nullValue            The null value for this Column
     * @param minValue             The minimum value for this column ( not used unless
     *                             constrainMin is true ).
     * @param maxValue             The maximum allowable value for this Column ( Not
     *                             used unless constrainMax is true ).
     * @param precision            The number of significant digits to store
     * @param decimalPlaces        The number of places to the right of the decimal
     *                             point to store.
     * @param displayDecimalPlaces The number of decimal places to display
     * @param displayFieldWidth    The width of the field to use when outputting this
     *                             value as a String.
     * @param constrainMin         If true, place a mimimum value constraint on the
     *                             Column.
     * @param constrainMax         If true, place a maximum value constraint on the
     *                             Column.
     */
    protected FloatColumn( String name, double nullValue, double minValue, double maxValue, int precision, int decimalPlaces, int displayDecimalPlaces, int displayFieldWidth, boolean constrainMin, boolean constrainMax )
    {
        super( name, new Variant( nullValue ) );
        setConstraints( name, nullValue, minValue, maxValue, precision, decimalPlaces, displayDecimalPlaces, displayFieldWidth, constrainMin, constrainMax );
    }

    /**
     * Constructor for the FloatColumn object that also sets the value of the column.
     *
     * @param name                 Name of the Column
     * @param value                The value to place in this Column.
     * @param nullValue            The null value for this Column
     * @param minValue             The minimum value for this column ( not used unless constrainMin is true ).
     * @param maxValue             The maximum allowable value for this Column ( Not used unless constrainMax is true ).
     * @param precision            The number of significant digits to store
     * @param decimalPlaces        The number of places to the right of the decimal point to store.
     * @param displayDecimalPlaces The number of decimal places to display
     * @param displayFieldWidth    The width of the field to use when outputting this value as a String.
     * @param constrainMin         If true, place a mimimum value constraint on the Column.
     * @param constrainMax         If true, place a maximum value constraint on the Column.
     */
    protected FloatColumn( String name, double value, double nullValue, double minValue, double maxValue, int precision, int decimalPlaces, int displayDecimalPlaces, int displayFieldWidth, boolean constrainMin, boolean constrainMax )
    {
        super( name, new Variant( nullValue ), new Variant( value ) );
        setConstraints( name, nullValue, minValue, maxValue, precision, decimalPlaces, displayDecimalPlaces, displayFieldWidth, constrainMin, constrainMax );
        thisValue = Validate( thisValue );
    }

    /**
     * Method to ensure that values input to the setValue method are in range for the
     * Column. Input data must be of float type and must be in the range defined for
     * this Column
     *
     * @param value The input value to be checked and possibly modified.
     * @return The validated data, ready to be used to set the value in this Column.
     */
    public Variant Validate( Variant value )
    {
        if( !value.IsFloatType() )
            throw new IllegalArgumentException( "Input value is not of float type." );
        double v = value.floatValue();
        if( v < minValue )
            value = new Variant( minValue );
        else if( v > maxValue )
            value = new Variant( maxValue );
        return value;
    }

    /**
     * Returns a formatted fixed-width String representation of the FloatColumn Column
     * value suitable for use in writing a column-delimited listing of a table's contents.
     *
     * @return The formatted FloatColumn Column value as a String.
     */
    public String ValueString()
    {
        boolean alignRight = true;
        return Column.FormatStringForOutput( floatToString( thisValue.doubleValue(), displayDecimalPlaces ), displayFieldWidth, alignRight );
    }

    /**
     * Return a String representation of the Column value suitable for embedding into
     * an INSERT statement. For float values, this will simply be the value converted
     * into a string.
     *
     * @return The formatted String
     */
    public String DbInsertString()
    {
        return floatToString( thisValue.doubleValue(), decimalPlaces );
    }

    private void setConstraints( String name, double nullValue, double minValue, double maxValue, int precision, int decimalPlaces, int displayDecimalPlaces, int displayFieldWidth, boolean constrainMin, boolean constrainMax )
    {
        setSpecifier( makeSpecifier( name, minValue, maxValue, precision, decimalPlaces, constrainMin, constrainMax ) );
        this.decimalPlaces = decimalPlaces;
        this.displayDecimalPlaces = displayDecimalPlaces;
        this.displayFieldWidth = displayFieldWidth;
        this.nullValue = nullValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    private String makeCheckConstraint( String name, double minValue, double maxValue, boolean constrainMin, boolean constrainMax )
    {
        StringBuffer s = new StringBuffer();
        if( constrainMin || constrainMax ) {
            s.append( " CHECK (" + name.toUpperCase() + " " );
            if( constrainMin && constrainMax )
                s.append( "BETWEEN " + minValue + " AND " + maxValue + ")" );
            else if( constrainMin )
                s.append( ">= " + minValue + ")" );
            else if( constrainMax )
                s.append( "<= " + maxValue + ")" );
        }
        return s.toString();
    }

    private String makeSpecifier( String name, double minValue, double maxValue, int precision, int decimalPlaces, boolean constrainMin, boolean constrainMax )
    {
        StringBuffer s = new StringBuffer( name.toUpperCase() + " NUMBER(" );
        s.append( precision );
        s.append( "," );
        s.append( decimalPlaces );
        s.append( ") NOT NULL" );
        s.append( makeCheckConstraint( name, minValue, maxValue, constrainMin, constrainMax ) );
        return s.toString();
    }

    public int getColumnWidth()
    {
        return displayFieldWidth;
    }

    private int decimalPlaces;
    private int displayDecimalPlaces;
    private int displayFieldWidth;
    private double nullValue;
    private double minValue;
    private double maxValue;
}


