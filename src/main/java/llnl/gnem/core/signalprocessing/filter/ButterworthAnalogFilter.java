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

