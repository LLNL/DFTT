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



