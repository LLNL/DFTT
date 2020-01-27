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
package llnl.gnem.apps.detection;

import com.oregondsp.signalProcessing.filter.iir.PassbandType;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import java.io.Serializable;
import llnl.gnem.apps.detection.core.framework.detectors.PersistentProcessingParameters;
import llnl.gnem.apps.detection.core.signalProcessing.IIRFilterType;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class FrameworkPreprocessorParams implements PreprocessorParams, Serializable {

    private final int filterOrder;
    private final int dataBlockSize;
    private final double passBandHighFrequency;
    private final double passBandLowFrequency;
    private final double sampleRate;
    private final int decimatedBlockSize;
    private final int decimationRate;

    static final long serialVersionUID = 8789564455499291021L;

    public FrameworkPreprocessorParams(int filterOrder, 
            int dataBlockSize, 
            double passBandHighFrequency, 
            double passBandLowFrequency, 
            double sampleRate, 
            int decimatedBlockSize, 
            int decimationRate) {
        this.filterOrder = filterOrder;
        this.dataBlockSize = dataBlockSize;
        this.passBandHighFrequency = passBandHighFrequency;
        this.passBandLowFrequency = passBandLowFrequency;
        this.sampleRate = sampleRate;
        this.decimatedBlockSize = decimatedBlockSize;
        this.decimationRate = decimationRate;
    }
    
    @Override
    public FrameworkPreprocessorParams changeBlockSize(double blockSizeSeconds)
    {
        int tmpDataBlockSize = (int)Math.round(blockSizeSeconds * sampleRate) + 1;
        int tmpDecimatedBlockSize = (int)Math.round(blockSizeSeconds * sampleRate / decimationRate) + 1;
        return new FrameworkPreprocessorParams(filterOrder,tmpDataBlockSize, passBandHighFrequency, passBandLowFrequency, sampleRate, tmpDecimatedBlockSize, decimationRate );
    }

    public FrameworkPreprocessorParams(String streamName, double sampleRate) {
        filterOrder = StreamsConfig.getInstance().getPreprocessorFilterOrder(streamName);
        dataBlockSize = StreamsConfig.getInstance().getUndecimatedBlockSize();
        passBandHighFrequency = StreamsConfig.getInstance().getPassBandHighFrequency(streamName);
        passBandLowFrequency = StreamsConfig.getInstance().getPassBandLowFrequency(streamName);
        decimatedBlockSize = StreamsConfig.getInstance().getDecimatedDataBlockSize(streamName);
        decimationRate = StreamsConfig.getInstance().getDecimationRate(streamName);
        this.sampleRate = sampleRate;
    }

    public PersistentProcessingParameters getProcessingParameters() {
        return new PersistentProcessingParameters(IIRFilterType.BUTTERWORTH,
                PassbandType.BANDPASS,
                getPassBandLowFrequency(),
                getPassBandHighFrequency(),
                filterOrder,
                0.001,
                getSampleRate(),
                decimationRate);
    }

    @Override
    public int getPreprocessorFilterOrder() {
        return filterOrder;
    }

    @Override
    public int getDataBlockSize() {
        return dataBlockSize;
    }

    @Override
    public double getPassBandHighFrequency() {
        return passBandHighFrequency;
    }

    @Override
    public double getPassBandLowFrequency() {
        return passBandLowFrequency;
    }

    /**
     * @return the sampleRate
     */
    @Override
    public double getSampleRate() {
        return sampleRate;
    }

    @Override
    public int getDecimatedDataBlockSize() {
        return decimatedBlockSize;
    }

    @Override
    public int getDecimationRate() {
        return decimationRate;
    }

    @Override
    public PersistentProcessingParameters getPreprocessorParams() {
        return getProcessingParameters();
    }

}
