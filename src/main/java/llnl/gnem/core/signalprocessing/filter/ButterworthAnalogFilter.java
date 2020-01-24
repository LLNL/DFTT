//                                          ButterworthAnalogFilter
//  copyright 2001  Regents of the University of California
//  Author:  Dave Harris
//  Creation date:  November 30, 1999
//  Last modified:  January 10, 2001
//  Dependencies:
package llnl.gnem.core.signalprocessing.filter;

import llnl.gnem.core.signalprocessing.filter.AnalogFilter;
import llnl.gnem.core.signalprocessing.filter.AnalogSecOrdSect;

import java.util.Vector;

public class ButterworthAnalogFilter extends AnalogFilter {
// constructors:

    public ButterworthAnalogFilter( int order )
    {
        int half = order / 2;
        sections = new Vector<AnalogSecOrdSect>();
        double angle;
        if( half * 2 != order ) {
            sections.addElement( new AnalogSecOrdSect( 1.0, 0.0, 0.0, 1.0, 1.0, 0.0 ) );
        }
        for ( int i = 0; i < half; i++ ) {
            angle = Math.PI * ( .5 + ( (double) 2 * i + 1 ) / ( (double) 2 * order ) );
            sections.addElement( new AnalogSecOrdSect( 1.0, 0.0, 0.0, 1.0, -2.0 * Math.cos( angle ), 1.0 ) );
        }
    }
}

