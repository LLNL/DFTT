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

public class AnalogSecOrdSect {
    public double a0, b0, a1, b1, a2, b2;

    public AnalogSecOrdSect()         // default constructor
    {
        a0 = b0 = 1.0;
        a1 = b1 = a2 = b2 = 0.0;
    }

    // copy constructor
    public AnalogSecOrdSect( AnalogSecOrdSect aSect )
    {
        a0 = aSect.a0;
        a1 = aSect.a1;
        a2 = aSect.a2;
        b0 = aSect.b0;
        b1 = aSect.b1;
        b2 = aSect.b2;
    }

// constructor:

    public AnalogSecOrdSect( double B0, double B1, double B2, double A0, double A1, double A2 )
    {
        b0 = B0;
        b1 = B1;
        b2 = B2;
        a0 = A0;
        a1 = A1;
        a2 = A2;
    }

// transformation operators:

// LPtoLP transformation is just a scaling of the frequency axis

    public AnalogSecOrdSect LPtoLP( double fh )
    {
        AnalogSecOrdSect S = new AnalogSecOrdSect( this );
        S.scalef( fh );
        return S;
    }

// LPtoHP transformation replaces s with 1/s and scales the frequency axis

    public AnalogSecOrdSect LPtoHP( double fl )
    {
        AnalogSecOrdSect aSect = new AnalogSecOrdSect( this );
        if( a2 != 0.0 | b2 != 0.0 ) {
            aSect.a0 = a2;
            aSect.a1 = a1;
            aSect.a2 = a0;
            aSect.b0 = b2;
            aSect.b1 = b1;
            aSect.b2 = b0;
        }
        else if( a1 != 0.0 | b1 != 0.0 ) {
            aSect.a0 = a1;
            aSect.a1 = a0;
            aSect.a2 = 0.0;
            aSect.b0 = b1;
            aSect.b1 = b0;
            aSect.b2 = 0.0;
        }
        aSect.scalef( fl );
        return aSect;
    }

    public Vector<AnalogSecOrdSect> LPtoBP( double fl, double fh )

//  uses transformation     s -> (s^2 + a) / sb
    {
        double a = 4.0 * Math.PI * Math.PI * fl * fh;
        double b = 2.0 * Math.PI * ( fh - fl );
        double[] A2 = new double[2];
        double[] A1 = new double[2];
        double[] A0 = new double[2];
        double[] B2 = new double[2];
        double[] B1 = new double[2];
        double[] B0 = new double[2];
        Complex p, r1, r2, t1, t2;
        double t;
        double G = 1.0;
        int nSects = Math.max( nPoles(), nZeroes() );
        nSects = Math.max( nSects, 1 );
        switch (nZeroes()) {
            case 2:
                G = b2;
                p = new Complex( b1 * b1 - 4.0 * b2 * b0, 0.0 ).sqrt().add(-b1).divide( 2.0 * b2 );
                t1 = p.divide( 2.0 ).multiply(b);
                t2 = t1.multiply( t1 ).subtract( a ).sqrt() ;
                r1 = t1.add( t2 );
                r2 = t1.subtract( t2 );
                B2[0] = 1.0;
                B1[0] = -2.0 * r1.getReal();
                B0[0] = ( r1.multiply( r1.conjugate() ) ).getReal();
                B2[1] = 1.0;
                B1[1] = -2.0 * r2.getReal();
                B0[1] = ( r2.multiply( r2.conjugate() ) ).getReal();
                break;
            case 1:
                G = b1;
                t = -b * ( b0 / b1 ) / 2.0;
                r1 = new Complex( t * t - a ).sqrt().add(t);
                B2[0] = 1.0;
                B1[0] = -2.0 * r1.getReal();
                B0[0] = ( r1.multiply( r1.conjugate() ) ).getReal();
                B2[1] = 0.0;
                B1[1] = 0.0;
                B0[1] = 1.0;
                break;
            case 0:
                G = b0;
                B2[0] = 0.0;
                B1[0] = 0.0;
                B0[0] = 1.0;
                B2[1] = 0.0;
                B1[1] = 0.0;
                B0[1] = 1.0;
                break;
        }
        switch (nPoles()) {
            case 2:
                G /= a2;
                p = new Complex( a1 * a1 - 4.0 * a2 * a0, 0.0 ).sqrt().add(-a1).divide( 2.0 * a2 );
                t1 = p.divide( 2.0 ).multiply(b);
                t2 = t1.multiply(t1).subtract(a).sqrt();
                r1 = t1.add( t2 );
                r2 = t1.subtract( t2 );
                A2[0] = 1.0;
                A1[0] = -2.0 * r1.getReal();
                A0[0] = ( r1.multiply( r1.conjugate() ) ).getReal();
                A2[1] = 1.0;
                A1[1] = -2.0 * r2.getReal();
                A0[1] = ( r2.multiply( r2.conjugate() ) ).getReal();
                break;
            case 1:
                G /= a1;
                t = -b * ( a0 / a1 ) / 2.0;
                r1 = new Complex( t * t - a ).sqrt().add(t);
                A2[0] = 1.0;
                A1[0] = -2.0 * r1.getReal();
                A0[0] = ( r1.multiply( r1.conjugate() ) ).getReal();
                A2[1] = 0.0;
                A1[1] = 0.0;
                A0[1] = 1.0;
                break;
            case 0:
                G /= a0;
                A2[0] = 0.0;
                A1[0] = 0.0;
                A0[0] = 1.0;
                A2[1] = 0.0;
                A1[1] = 0.0;
                A0[1] = 1.0;
                break;
        }
        switch (nSects) {
            case 2:
                switch (nPoles() - nZeroes()) {
                    case 2:
                        B2[0] = b * B1[0];
                        B1[0] = b * B0[0];
                        B0[0] = 0.0;
                        B2[1] = b * B1[1];
                        B1[1] = b * B0[1];
                        B0[1] = 0.0;
                        break;
                    case 1:
                        B2[1] = b * B1[1];
                        B1[1] = b * B0[1];
                        B0[1] = 0.0;
                        break;
                    case -1:
                        A2[1] = b * A1[1];
                        A1[1] = b * A0[1];
                        A0[1] = 0.0;
                        break;
                    case -2:
                        A2[0] = b * A1[0];
                        A1[0] = b * A0[0];
                        A0[0] = 0.0;
                        A2[1] = b * A1[1];
                        A1[1] = b * A0[1];
                        A0[1] = 0.0;
                        break;
                }
                break;
            case 1:
                switch (nPoles() - nZeroes()) {
                    case 1:
                        B2[0] = b * B1[0];
                        B1[0] = b * B0[0];
                        B0[0] = 0.0;
                        break;
                    case -1:
                        A2[0] = b * A1[0];
                        A1[0] = b * A0[0];
                        A0[0] = 0.0;
                        break;
                }
                break;
        }
        Vector<AnalogSecOrdSect> V = new Vector<AnalogSecOrdSect>( 2 );
        V.addElement( ( new AnalogSecOrdSect( B0[0], B1[0], B2[0], A0[0], A1[0], A2[0] ) ).times( G ) );
        if( nSects > 1 ) {
            V.addElement( new AnalogSecOrdSect( B0[1], B1[1], B2[1], A0[1], A1[1], A2[1] ) );
        }
        return V;
    }

    public void scalef( double f )
    {
        double scale = 2.0 * Math.PI * f;
        b1 /= scale;
        a1 /= scale;
        b2 /= ( scale * scale );
        a2 /= ( scale * scale );
    }

    public AnalogSecOrdSect times( double f )
    {
        AnalogSecOrdSect result = new AnalogSecOrdSect( this );
        result.b0 *= f;
        result.b1 *= f;
        result.b2 *= f;
        return result;
    }


// accessors:


    public int nPoles()
    {
        if( a2 != 0.0 )
            return 2;
        else if( a1 != 0.0 )
            return 1;
        else
            return 0;
    }

    public int nZeroes()
    {
        if( b2 != 0.0 )
            return 2;
        else if( b1 != 0.0 )
            return 1;
        else
            return 0;
    }


// evaluation:

    public Complex evaluateAt( Complex s )
    {
        return ( ( s.multiply( ( s.multiply( b2 ) ).add( b1 ) ) ).add( b0 ) ).divide( ( ( s.multiply( ( s.multiply( a2 ) ).add( a1 ) ) ).add( a0 ) ) );
    }


// Print method

    public void print( PrintStream ps )
    {
        int count = 0;
        ps.print( "( " );
        if( b2 != 0 ) {
            ps.print( b2 + " * s^2 " );
            count++;
        }
        if( b1 != 0 ) {
            if( count > 0 ) ps.print( " + " );
            ps.print( b1 + " * s" );
            count++;
        }
        if( b0 != 0 ) {
            if( count > 0 ) ps.print( " + " );
            ps.print( b0 );
        }
        ps.print( " ) / (" );
        count = 0;
        if( a2 != 0 ) {
            ps.print( a2 + " * s^2 " );
            count++;
        }
        if( a1 != 0 ) {
            if( count > 0 ) ps.print( " + " );
            ps.print( a1 + " * s" );
            count++;
        }
        if( a0 != 0 ) {
            if( count > 0 ) ps.print( " + " );
            ps.print( a0 );
        }
        ps.println( " )" );
    }
}

