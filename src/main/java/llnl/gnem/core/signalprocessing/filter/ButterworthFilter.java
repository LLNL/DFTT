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
