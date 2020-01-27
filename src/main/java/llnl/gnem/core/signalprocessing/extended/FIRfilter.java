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
package llnl.gnem.core.signalprocessing.extended;

/**
 * @author Paik6
 */
public interface FIRfilter {
    /**
     * Re-initializes this FIRfilter.
     */
    public abstract void init();

    /**
     * Used by master ComplexFIRfilters to filter the multiplexed data from the
     * preprocessing. The main difference from this filter function and the
     * other filter function is the calculation of the dataDFT. The filter function
     * requires another ComplexSequence to hold the new values.
     *
     * @param returnSeq   - the ComplexSequence to hold the new filtered values
     * @param dataSegment - the ComplexSequence representing the multiplexed data
     */
    public abstract void filter(ComplexSequence returnSeq, ComplexSequence dataSegment);

    /**
     * Used by slave ComplexFIRfilters to filter the multiplexed data from the
     * preprocessing. This function does not take in multiplexed data: rather,
     * it copies the dataDFT from its master.
     *
     * @param returnSeq - the ComplexSequence to hold the new filtered values
     */
    public abstract void filter(ComplexSequence returnSeq);
}