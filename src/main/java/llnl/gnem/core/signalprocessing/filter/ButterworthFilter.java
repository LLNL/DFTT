//                                               ButterworthFilter.java
//  copyright 2001  Regents of the University of California
//  Author:  Dave Harris
//  Creation date:  November 30, 1999
//  Last modified:  January 10, 2001
package llnl.gnem.core.signalprocessing.filter;

import llnl.gnem.core.util.Passband;

public class ButterworthFilter extends IIRFilter {
    /**
     * The Constructor for the ButterworthFilter that fully specifies the filter.
     *
     * @param order    The order of the filter
     * @param cutoff1  The first cutoff frequency. For lowpass and highpass filters,
     *                 this is the only cutoff values that matters and the other should be set
     *                 to 0.0. For bandpass and bandrejhect filters this is the low frequency corner
     *                 of the filter.
     * @param cutoff2  For bandpass and bandreject filters, this is the high frequency
     *                 corner. For other filters, this value should be 0.0.
     * @param T        The sample interval in seconds.
     * @param passband The passband of the filter, e.g. LOW_PASS, HIGH_PASS, etc.
     */
    public ButterworthFilter( int order, Passband passband, double cutoff1, double cutoff2, double T )
    {
        super( new ButterworthAnalogFilter( order ), passband, cutoff1, cutoff2, T );
    }
}
