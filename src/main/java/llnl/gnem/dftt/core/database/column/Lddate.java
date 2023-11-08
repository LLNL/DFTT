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

import llnl.gnem.dftt.core.database.column.Column;
import llnl.gnem.dftt.core.util.Variant;

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


