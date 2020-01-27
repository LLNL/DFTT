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
package llnl.gnem.core.database.row;

import llnl.gnem.core.util.Variant;
import llnl.gnem.core.util.Passband;

import java.util.Vector;
import java.sql.*;

import java.text.NumberFormat;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A class that manages access to a single row retrieved from the STORED_FILTER
 * table.
 *
 * @author Doug Dodge
 */
public class StoredFilterRow extends ColumnSet {
    /**
     * Constructor for the StoredFilterRow object
     */
    public StoredFilterRow()
    {
        super( getMyColumnNames() );
        setValue( "Type", new Variant( "BP" ) );
        setValue( "Causal", new Variant( "y" ) );
        setValue( "FilterOrder", new Variant( 2 ) );
        setValue( "Lowpass", new Variant( 1.0 ) );
        setValue( "Highpass", new Variant( 10.0 ) );
        setValue( "Descrip", new Variant( "Default filter" ) );
        setValue( "ImpulseResponse", new Variant( "iir" ) );
    }

    /**
     * Constructor for the StoredFilterRow object
     *
     * @param Filterid        Unique identifier for this row in the database table
     * @param passband        Filter passband, e.g. bandpass, lowpass, highpass, bandreject
     * @param Causal          Causality of the filter
     * @param FilterOrder     Order of the filter polynomial
     * @param Lowpass         Lowpass corner of the filter
     * @param Highpass        highpass corner of the filter
     * @param Descrip         Description of the filter
     * @param ImpulseResponse Impulse response type of the filter
     * @param Auth            Creator of this row.
     * @param Lddate          Date this row was loaded into the database.
     */
    public StoredFilterRow( int Filterid, Passband passband, boolean Causal, int FilterOrder, double Lowpass, double Highpass, String Descrip, String ImpulseResponse, String Auth, Date Lddate )
    {
        super( getMyColumnNames() );
        setValue( "Filterid", new Variant( Filterid ) );
        setValue( "Type", new Variant( passband.toString() ) );
        setValue( "Causal", new Variant( Causal ? "y" : "n" ) );
        setValue( "FilterOrder", new Variant( FilterOrder ) );
        setValue( "Lowpass", new Variant( Lowpass ) );
        setValue( "Highpass", new Variant( Highpass ) );
        setValue( "Descrip", new Variant( Descrip ) );
        setValue( "ImpulseResponse", new Variant( ImpulseResponse ) );
        setValue( "Auth", new Variant( Auth ) );
        setValue( "Lddate", new Variant( Lddate ) );
    }

    public StoredFilterRow( StoredFilterRow from )
    {
        super( getMyColumnNames() );
        setValue( "Filterid", new Variant( from.getFilterid() ) );
        setValue( "Type", new Variant( from.getPassband().toString() ) );
        setValue( "Causal", new Variant( from.getCausal() ? "y" : "n" ) );
        setValue( "FilterOrder", new Variant( from.getFilterOrder() ) );
        setValue( "Lowpass", new Variant( from.getLowpass() ) );
        setValue( "Highpass", new Variant( from.getHighpass() ) );
        setValue( "Descrip", new Variant( from.getDescrip() ) );
        setValue( "ImpulseResponse", new Variant( from.getImpulseResponse() ) );
        setValue( "Auth", new Variant( from.getAuth() ) );
        setValue( "Lddate", new Variant( from.getValue( "Lddate" ).dateValue() ) );
    }

    /**
     * Gets the filterid attribute of the StoredFilterRow object
     *
     * @return The filterid value
     */
    public int getFilterid()
    {
        return getValue( "Filterid" ).intValue();
    }

    public void setFilterid( int v )
    {
        setValue( "Filterid", new Variant( v ) );
    }

    /**
     * Gets the passband attribute of the StoredFilterRow object
     *
     * @return The passband value
     */
    public Passband getPassband()
    {
        String type = getValue( "Type" ).toString();
        return Passband.getPassbandFromString( type );
    }

    public String getType()
    {
        return getValue( "Type" ).toString();
    }

    public void setPassband( Passband pb )
    {
        setValue( "Type", new Variant( pb.toString() ) );
    }

    /**
     * Gets the causal attribute of the StoredFilterRow object
     *
     * @return The causal value
     */
    public boolean getCausal()
    {
        String c = getValue( "Causal" ).toString();
        return c.equals( "y" );
    }

    public void setCausal( boolean v )
    {
        setValue( "Causal", new Variant( v ? "y" : "n" ) );
    }

    /**
     * Gets the filterOrder attribute of the StoredFilterRow object
     *
     * @return The filterOrder value
     */
    public int getFilterOrder()
    {
        return getValue( "FilterOrder" ).intValue();
    }

    public void setFilterOrder( int v )
    {
        setValue( "FilterOrder", new Variant( v ) );
    }

    /**
     * Gets the lowpass attribute of the StoredFilterRow object
     *
     * @return The lowpass value
     */
    public double getLowpass()
    {
        return getValue( "Lowpass" ).doubleValue();
    }

    public void setLowpass( double v )
    {
        setValue( "Lowpass", new Variant( v ) );
    }

    /**
     * Gets the highpass attribute of the StoredFilterRow object
     *
     * @return The highpass value
     */
    public double getHighpass()
    {
        return getValue( "Highpass" ).doubleValue();
    }

    public void setHighpass( double v )
    {
        setValue( "Highpass", new Variant( v ) );
    }

    /**
     * Gets the descrip attribute of the StoredFilterRow object
     *
     * @return The descrip value
     */
    public String getDescrip()
    {
        return getValue( "Descrip" ).toString();
    }

    /**
     * Gets the impulseResponse attribute of the StoredFilterRow object
     *
     * @return The impulseResponse value
     */
    public String getImpulseResponse()
    {
        return getValue( "ImpulseResponse" ).toString();
    }

    /**
     * Gets the auth attribute of the StoredFilterRow object
     *
     * @return The auth value
     */
    public String getAuth()
    {
        return getValue( "Auth" ).toString();
    }

    public void setAuth( String auth )
    {
        setValue( "Auth", new Variant( auth ) );
    }

    public void setLddate( Date lddate )
    {
        setValue( "Lddate", new Variant( lddate ) );
    }

    /**
     * Gets the Column Names of a StoredFilterRow class
     *
     * @return A Vector of Strings containing the Column Names of a StoredFilterRow.
     */
    public static Vector<String> getMyColumnNames()
    {
        Vector<String> result = new Vector<String>();
        for ( int j = 0; j < names.length; ++j )
            result.add( names[j] );
        return result;
    }

    /**
     * Gets a Vector of Strings containing the StoredFilterRow primary key Columns.
     *
     * @return The primary key Columns Vector.
     */
    public static Vector<String> getPkColumns()
    {
        Vector<String> pkColumns = new Vector<String>();
        pkColumns.add( "Filterid" );
        return pkColumns;
    }

    public String toString()
    {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits( 3 );
        StringBuffer result = new StringBuffer( getValue( "Type" ).toString() + "_LC_" );
        result.append( f.format( getLowpass() ) + "_HC_" );
        result.append( f.format( getHighpass() ) );
        String tmp = getCausal() ? "_Causal_Order_" : "_Acausal_Order_";
        result.append( tmp );
        result.append( getFilterOrder() );
        return result.toString();
    }

    private final static String[] names = {"Filterid", "Type", "Causal", "FilterOrder", "Lowpass", "Highpass", "Descrip", "ImpulseResponse", "Auth", "Lddate"};
}





