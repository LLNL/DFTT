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
package llnl.gnem.dftt.core.signalprocessing.filter;

import java.io.PrintStream;
import java.util.Vector;

import org.apache.commons.math3.complex.Complex;

import llnl.gnem.dftt.core.util.Passband;

public class IIRFilter {
    protected Vector<SecOrdSection> sections;

//  Constructor and Destructor:

    public IIRFilter()
    {
        sections = new Vector<SecOrdSection>();
    }


//  Copy constructor and assignment


    public IIRFilter( IIRFilter F )
    {
        sections = new Vector<SecOrdSection>( F.sections.size() );
        for ( int i = 0; i < F.sections.size(); i++ ) {
            sections.addElement( F.get( i ) );
        }
    }


//  Constructors:


    /**
     * Constructor for the IIRFilter object
     *
     * @param baseFilter Description of the Parameter
     * @param cutoff1    The first cutoff frequency. For lowpass and highpass filters,
     *                   this is the only cutoff values that matters and the other should be set
     *                   to 0.0. For bandpass and bandrejhect filters this is the low frequency corner
     *                   of the filter.
     * @param cutoff2    For bandpass and bandreject filters, this is the high frequency
     *                   corner. For other filters, this value should be 0.0.
     * @param T          The sample interval in seconds of the data to be filtered
     * @param passband   The passband of the filter, e.g. LOW_PASS, HIGH_PASS, etc.
     */
    public IIRFilter( AnalogFilter baseFilter, Passband passband, double cutoff1, double cutoff2, double T )
    {
        AnalogFilter prototype = null;
        if( passband == Passband.LOW_PASS )
            prototype = baseFilter.LPtoLP( AnalogFilter.warp( cutoff1 * T / 2.0, 2.0 ) );
        else if( passband == Passband.HIGH_PASS )
            prototype = baseFilter.LPtoHP( AnalogFilter.warp( cutoff1 * T / 2.0, 2.0 ) );
        else if( passband == Passband.BAND_PASS )
            prototype = baseFilter.LPtoBP( AnalogFilter.warp( cutoff1 * T / 2.0, 2.0 ), AnalogFilter.warp( cutoff2 * T / 2.0, 2.0 ) );
        else if( passband == Passband.BAND_REJECT )
            prototype = baseFilter.LPtoBR( AnalogFilter.warp( cutoff1 * T / 2.0, 2.0 ), AnalogFilter.warp( cutoff2 * T / 2.0, 2.0 ) );
        sections = new Vector<SecOrdSection>();
        for ( int i = 0; i < prototype.nSections(); i++ )
            sections.addElement( new SecOrdSection( prototype.get( i ), 2.0 ) );
    }


//  Mutators

    public void addSection( SecOrdSection S )
    {
        sections.addElement( S );
    }


//  Initializing sections:


    public void initialize()
    {
        for ( int i = 0; i < sections.size(); i++ ) {
            get( i ).clearstates();
        }
    }


//  filtering:


    public float filter( float s )
    {                   // single step
        for ( int i = 0; i < sections.size(); i++ ) {
            s = get( i ).filter( s );
        }
        return s;
    }

    public void filter( float[] signal )
    {
        for ( int i = 0; i < sections.size(); i++ ) {
            get( i ).filter( signal );
        }
    }


//  accessors:


    public int nSections()
    {
        return sections.size();
    }

    public SecOrdSection get( int i )
    {
        return sections.elementAt( i );
    }

    public Complex evaluateAt( double Omega )
    {
        Complex result = new Complex( 1.0, 0.0 );
        for ( int i = 0; i < sections.size(); i++ ) {
            result = result.multiply(get( i ).evaluateAt( Omega ));
        }
        return result;
    }


//  utilities:


    public void print( PrintStream ps )
    {
        ps.println( "Digital filter: " );
        for ( int i = 0; i < sections.size(); i++ ) {
            ps.println( "\n  section " + ( i + 1 ) + ":" );
            get( i ).print( ps );
        }
    }
}








