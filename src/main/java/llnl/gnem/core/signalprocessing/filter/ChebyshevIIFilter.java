//                                                                            ChebyshevIIFilter.java
//  copyright 2004  Regents of the University of California
//  Author:  Dave Harris
//  Creation date:  March 24, 2004
//  Last modified:  March 24, 2004


package llnl.gnem.core.signalprocessing.filter;


import llnl.gnem.core.util.Passband;


public class ChebyshevIIFilter extends IIRFilter {

    public ChebyshevIIFilter( int order,
                              double atten,
                              double omegaR,
                              Passband passband,
                              double cutoff1,
                              double cutoff2,
                              double T )
    {


        super( new ChebyshevIIAnalogFilter( order, atten, omegaR ),
               passband,
               cutoff1,
               cutoff2,
               T );
    }
}
