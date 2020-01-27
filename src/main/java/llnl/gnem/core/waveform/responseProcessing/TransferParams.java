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
package llnl.gnem.core.waveform.responseProcessing;

import java.util.prefs.Preferences;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.SeriesMath;

/**
 * Created by dodge1 Date: Mar 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class TransferParams {

    private static class TransferParamsHolder {

        private static final TransferParams instance = new TransferParams();
    }

    public static TransferParams getInstance() {
        return TransferParamsHolder.instance;
    }

    private final Preferences prefs;

    private TransferParams() {
        prefs = Preferences.userNodeForPackage(this.getClass());
    }

    /**
     * Gets a FreqLimits object to be used when removing the instrument response
     * from a segment of waveform data in the current project.
     *
     * @param nyquist The Nyquist frequency of the data for which the result
     * FreqLimits object will be used.
     * @param windowLength The length of the data window to be transferred.
     * @return A FreqLimits object containing the appropriate frequency limits
     * for the deconvolution.
     */
    public FreqLimits getFreqLimits(double nyquist, double windowLength) {
        double highpass = MiscParams.getInstance().getHighpassFrac() * nyquist;
        double highcut = MiscParams.getInstance().getHighCutFrac() * nyquist;

        double minfreq = prefs.getDouble("MIN_LOWPASS_FREQ", 0.01);
        double tfactor = MiscParams.getInstance().getTfactor();
        double lowpass = Math.max(tfactor / windowLength, minfreq);
        double lowcut = MiscParams.getInstance().getLowCutFrac() * lowpass;
        return new FreqLimits(lowcut, lowpass, highpass, highcut);
    }

    public FreqLimits produceFromTransferFunction(double[] freqs, double[] amplitude) {
        double nyquist = freqs[freqs.length - 1];
        PairT<Integer, Double> max = SeriesMath.getMaxIndex(amplitude);
        double stopValue = max.getSecond() / 1000;
        double passValue = stopValue * 10;

        int highStopIndex = -1;
        int highPassIndex = -1;
        for (int j = max.getFirst(); j < amplitude.length; ++j) {
            double value = amplitude[j];
            if (value <= passValue && highPassIndex < 0) {
                highPassIndex = j;
            }
            if (value <= stopValue && highStopIndex < 0) {
                highStopIndex = j;
            }
        }
        double lowpass = 0.02;
        double lowcut = MiscParams.getInstance().getLowCutFrac() * lowpass;
        double highPass = highPassIndex > 0 ? freqs[highPassIndex] : nyquist * .95;
        double highStop = highStopIndex > 0 ? freqs[highStopIndex] : nyquist * .99;

        highPass = highPass > nyquist * .95 ? nyquist * .95 : highPass;
        highStop = highStop > nyquist * .99 ? nyquist * .99 : highStop;
        return new FreqLimits(lowcut, lowpass, highPass, highStop);
    }
}
