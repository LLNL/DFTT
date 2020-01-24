package llnl.gnem.core.database.column;

import llnl.gnem.core.database.column.Column;
import llnl.gnem.core.util.Variant;

import java.sql.Date;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * The date when some set of values becomes effective. For example. In a SITE table,
 * the Lddate would be the date that a particular configuration of a SITE became
 * effective. These dates are represented as an integer whose first 4 digits are
 * the year and whose last 3 digits are the day of the year, e.g. 1999063 would
 * be day 63 of the year 1999.
 *
 * @author Doug Dodge
 */
public class Lddate extends Column {
    /**
     * Default Constructor for the Lddate object
     */
    public Lddate()
    {
        super( "Lddate", new Variant( new Date( 0L ) ), "LDDATE DATE NOT NULL" );
    }

    /**
     * Constructor for the Lddate object that also sets the value of the Lddate.
     *
     * @param value The ondate value.
     */
    public Lddate( Date value )
    {
        super( "Lddate", new Variant( new Date( 0L ) ), "LDDATE DATE NOT NULL" );
        thisValue = new Variant( value );
    }

    /**
     * Returns a formatted fixed-width String representation of the Lddate Column value
     * suitable for use in writing a column-delimited listing of a table's contents.
     *
     * @return The formatted Lddate Column value as a String.
     */
    public String ValueString()
    {
        boolean alignRight = false;
        return Column.FormatStringForOutput( thisValue.toString(), 17, alignRight );
    }


    public int getColumnWidth()
    {
        return 17;
    }


    /**
     * Return a String representation of the Column value suitable for embedding into
     * an INSERT statement.
     *
     * @return The formatted String
     */
    public String DbInsertString()
    {
        StringBuffer s = new StringBuffer( "TO_DATE('" );
        s.append( thisValue.toString() );
        s.append( "', 'DD-MON-YYYY' )" );
        return s.toString();
    }
}


