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
package llnl.gnem.dftt.core.waveform.responseProcessing.spi;

import java.io.IOException;
import java.util.EnumSet;

import llnl.gnem.dftt.core.waveform.responseProcessing.CompositeTransferFunction;
import llnl.gnem.dftt.core.waveform.responseProcessing.FreqLimits;
import llnl.gnem.dftt.core.waveform.responseProcessing.ResponseMetaData;
import llnl.gnem.dftt.core.waveform.responseProcessing.ResponseType;
import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;

public interface TransferFunctionServiceProvider {

    /**
     * Provides the ResponseTypes supported by this Provider than can generate a CompositeTransferFunction.
     * 
     * @return Supported ResponseTypes
     */
    public EnumSet<ResponseType> supportedSourceResponseTypes();

    /**
     * @param seis
     * @param toType
     * @param metadata
     * @return
     * @throws IOException
     */
    public TransferData preprocessWaveformData(final CssSeismogram seis, final ResponseType toType,
                    final ResponseMetaData metadata) throws IOException;

    /**
     * The method that can generated a CompositeTransferFunction for the CssSeismogram.
     * 
     * @param seis
     * @param toType
     * @param metadata
     * @param limits
     * @param inverse
     * @param transferData
     * @parma transferData
     * @return
     * @throws IOException
     */
    public CompositeTransferFunction buildTransferFunction(final CssSeismogram seis, final ResponseType toType,
                    final ResponseMetaData metadata, final FreqLimits limits, final boolean inverse,
                    final TransferData transferData) throws IOException;

    public FreqLimits determineFreqLimitsFromTransferFunction(CssSeismogram seis, ResponseMetaData rmd) throws IOException;

}
