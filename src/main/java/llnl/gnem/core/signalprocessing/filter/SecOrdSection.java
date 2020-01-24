//                                              SecOrdSection.java
//
//  copyright 2001  Regents of the University of California
//  Author:  Dave Harris
//  Creation date:  November 30, 1999
//  Last modified:  January 10, 2001

/************************************************************************
 *               SecOrdSection  - Second Order Section Class             *
 ************************************************************************/
package llnl.gnem.core.signalprocessing.filter;

import java.io.PrintStream;

import org.apache.commons.math3.complex.Complex;

public class SecOrdSection {
    private double b0, b1, b2, a1, a2;      // coefficients
    private double s1, s2;                  // internal states

// Constructor:

    public SecOrdSection( double B0, double B1, double B2, double A1, double A2 )
    {
        b0 = B0;
        b1 = B1;
        b2 = B2;
        a1 = A1;
        a2 = A2;
        s1 = 0.0;
        s2 = 0.0;
    }

    public SecOrdSection( AnalogSecOrdSect A, double T )
    {
//  constructs a digital second order section from an analog second order
//    section using a bilinear transformation:
//                   -1
//          2  (1 - z  )
//      s = -  --------
//          T        -1
//             (1 + z  )

//  the analog section has the form:
//
//    ( b0 + b1*s + b2*s^2 ) / ( a0 + a1*s + a2*s^2 )
//
        int order = Math.max( A.nZeroes(), A.nPoles() );
        double a0;
        switch (order) {
            case 0:
                b0 = A.b0 / A.a0;
                b1 = 0.0;
                b2 = 0.0;
                a1 = 0.0;
                a2 = 0.0;
                break;
            case 1:
                b2 = 0.0;
                b1 = A.b0 - A.b1 * 2.0 / T;
                b0 = A.b0 + A.b1 * 2.0 / T;
                a1 = A.a0 - A.a1 * 2.0 / T;
                a0 = A.a0 + A.a1 * 2.0 / T;
                b1 /= a0;
                b0 /= a0;
                a1 /= a0;
                a2 = 0.0;
                break;
            case 2:
                double t2 = A.b2 * 2.0 / T * 2.0 / T;
                double t1 = A.b1 * 2.0 / T;
                b2 = t2 - t1 + A.b0;
                b1 = 2.0 * ( A.b0 - t2 );
                b0 = t2 + t1 + A.b0;
                t2 = A.a2 * 2.0 / T * 2.0 / T;
                t1 = A.a1 * 2.0 / T;
                a2 = t2 - t1 + A.a0;
                a1 = 2.0 * ( A.a0 - t2 );
                a0 = t2 + t1 + A.a0;
                b2 /= a0;
                b1 /= a0;
                b0 /= a0;
                a2 /= a0;
                a1 /= a0;
                break;
        }
        s1 = 0.0;
        s2 = 0.0;
    }


//    copy constructor:


    public SecOrdSection( SecOrdSection S )
    {
        b0 = S.b0;
        b1 = S.b1;
        b2 = S.b2;
        a1 = S.a1;
        a2 = S.a2;
        s1 = S.s1;
        s2 = S.s2;
    }

// Arithmetic functions:

    public void filter( float[] signal )
    {
        double s0;
        for ( int i = 0; i < signal.length; i++ ) {
            s0 = ( (double) signal[i] ) - a1 * s1 - a2 * s2;
            signal[i] = (float) ( b0 * s0 + b1 * s1 + b2 * s2 );
            s2 = s1;
            s1 = s0;
        }
    }

    public float filter( float seqValue )
    {
        double s0;
        float retval;
        s0 = ( (double) seqValue ) - a1 * s1 - a2 * s2;
        retval = (float) ( b0 * s0 + b1 * s1 + b2 * s2 );
        s2 = s1;
        s1 = s0;
        return retval;
    }

// Modifiers:

    public void clearstates()
    {
        s1 = 0.0;
        s2 = 0.0;
    }


// accessors:

    public Complex evaluateAt( double Omega )
    {
        Complex EjOmega =  new Complex( 0.0, -Omega ).exp();
        return ( ( EjOmega.multiply( ( EjOmega.multiply( b2 ) ) ).add( b1 ) ).add( b0 ) ).divide( ( EjOmega.multiply( ( EjOmega.multiply( a2 ) ) ).add( a1 ) ).add( 1.0 ) );
    }

    public void print( PrintStream ps )
    {
        ps.println( "  Coefficients: " );
        ps.println( "    b0: " + b0 );
        ps.println( "    b1: " + b1 );
        ps.println( "    b2: " + b2 );
        ps.println( "    a1: " + a1 );
        ps.println( "    a2: " + a2 );
        ps.println( "  States: " );
        ps.println( "    s1: " + s1 );
        ps.println( "    s2: " + s2 );
    }
}



