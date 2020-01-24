//                                                   AnalogFilter.java
//  copyright 2001  Regents of the University of California
//  Author:  Dave Harris
//  Creation date:  November 30, 1999
//  Last modified:  January 10, 2001
//  Dependencies:


/*************************************************
 *                                                *
 *              Analog filter class               *
 *                                                *
 *************************************************/
package llnl.gnem.core.signalprocessing.filter;

import java.io.PrintStream;
import java.util.Vector;

import org.apache.commons.math3.complex.Complex;

class AnalogFilter {
    protected Vector<AnalogSecOrdSect> sections;

// default constructor

    public AnalogFilter()
    {
        sections = new Vector<AnalogSecOrdSect>();
    }

// copy constructor

    public AnalogFilter( AnalogFilter F )
    {
        sections = new Vector<AnalogSecOrdSect>( F.sections.size() );
        for ( int i = 0; i < sections.size(); i++ ) {
            sections.addElement( new AnalogSecOrdSect( F.get( i ) ) );
        }
    }

// Insertion operator

    public void print( PrintStream ps )
    {
        ps.println( "Analog Filter: " );
        ps.print( "  Number of second order sections: " + nSections() );
        for ( int i = 0; i < nSections(); i++ ) {
            ps.println( "    Section " + i + ": " );
            ( get( i ) ).print( ps );
        }
    }


// accessors:


    public int nSections()
    {
        return sections.size();
    }

    AnalogSecOrdSect get( int i )
    {
        return sections.elementAt( i );
    }

// mutators

    public void addSection( AnalogSecOrdSect S )
    {
        sections.addElement( S );
    }


// evaluation:


    Complex evaluateAt( Complex s )
    {
        Complex H = new Complex( 1.0, 0.0 );
        for ( int i = 0; i < sections.size(); i++ ) {
            H = H.multiply( get( i ).evaluateAt( s ) );
        }
        return H;
    }

// transformations:

    AnalogFilter LPtoLP( double fh )
    {
        AnalogFilter result = new AnalogFilter();
        for ( int i = 0; i < sections.size(); i++ ) {
            result.addSection( get( i ).LPtoLP( fh ) );
        }
        return result;
    }

    AnalogFilter LPtoHP( double fl )
    {
        AnalogFilter result = new AnalogFilter();
        for ( int i = 0; i < sections.size(); i++ ) {
            result.addSection( get( i ).LPtoHP( fl ) );
        }
        return result;
    }

    AnalogFilter LPtoBP( double fl, double fh )
    {
        AnalogFilter result = new AnalogFilter();
        Vector<AnalogSecOrdSect> V;
        for ( int i = 0; i < sections.size(); i++ ) {
            V = get( i ).LPtoBP( fl, fh );
            for ( int j = 0; j < V.size(); j++ ) {
                result.addSection( V.elementAt( j ) );
            }
        }
        return result;
    }

    AnalogFilter LPtoBR( double fl, double fh )
    {
        AnalogFilter result = new AnalogFilter();
        AnalogSecOrdSect S;
        Vector<AnalogSecOrdSect> V;
        for ( int i = 0; i < sections.size(); i++ ) {
            S = get( i ).LPtoHP( 1. / ( 2.0 * Math.PI ) );
            V = S.LPtoBP( fl, fh );
            for ( int j = 0; j < V.size(); j++ ) {
                result.addSection( V.elementAt( j ) );
            }
        }
        return result;
    }


// class functions:

    static double warp( double f, double ts )
    {
        return Math.tan( Math.PI * f * ts ) / ( Math.PI * ts );
    }
}


