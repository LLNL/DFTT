package llnl.gnem.core.waveform.responseProcessing.spi;

import java.io.IOException;
import java.util.EnumSet;

import llnl.gnem.core.waveform.responseProcessing.CompositeTransferFunction;
import llnl.gnem.core.waveform.responseProcessing.FreqLimits;
import llnl.gnem.core.waveform.responseProcessing.ResponseMetaData;
import llnl.gnem.core.waveform.responseProcessing.ResponseType;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

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
