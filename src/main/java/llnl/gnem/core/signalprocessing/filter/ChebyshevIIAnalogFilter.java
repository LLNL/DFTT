//                                                                                     ChebyshevIIAnalogFilter.java
//
// Copyright (c) 2004  Regents of the University of California
//
//   Author:  Dave Harris
//   Created:  March 23, 2004
//   Last Modified:  March 23, 2004

package llnl.gnem.core.signalprocessing.filter;

import java.util.Vector;

public class ChebyshevIIAnalogFilter extends AnalogFilter {

// constructors:

    public ChebyshevIIAnalogFilter( int order, double a, double omegaR )
    {

        int half = order / 2;
        sections = new Vector<AnalogSecOrdSect>();

        double angle;

        //  intermediate design parameters

        double gamma = ( a + Math.sqrt( a * a - 1.0 ) );
        gamma = Math.log( gamma ) / ( (double) order );
        gamma = Math.exp( gamma );
        double s = 0.5 * ( gamma - 1.0 / gamma );
        double c = 0.5 * ( gamma + 1.0 / gamma );

        AnalogSecOrdSect section;

        if( half * 2 != order ) {
            section = new AnalogSecOrdSect( 1.0, 0.0, 0.0,
                                            omegaR / s, 1.0, 0.0 );
            section = section.times( omegaR / s );
            sections.addElement( section );
        }

        for ( int i = 0; i < half; i++ ) {
            angle = Math.PI * ( ( (double) 2 * i - 1 ) / ( (double) 2 * order ) );
            double alpha = -s * Math.sin( angle );
            double beta = c * Math.cos( angle );
            double denom = alpha * alpha + beta * beta;
            double sigma = omegaR * alpha / denom;
            double omegaP = -omegaR * beta / denom;
            double omegaZ = omegaR / Math.cos( angle );
            double b0 = omegaZ * omegaZ;
            double a0 = sigma * sigma + omegaP * omegaP;

            section = new AnalogSecOrdSect( b0, 0.0, 1.0,
                                            a0, -2.0 * sigma, 1.0 );
            section = section.times( a0 / b0 );
            sections.addElement( section );
        }
    }

}



