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

/**
 *
 * @author dodge1
 */
public class MiscParams {

    private final double referencePeriod = 20;
    private final double referenceDistance = 1.0;
    private final double taperPercent = 5.0;
    private final double tfactor = 2;
    private final double highpassFrac = 0.85;
    private final double highCutFrac = 0.9;
    private final double lowCutFrac = 0.8;

    /**
     * @return the referencePeriod
     */
    public double getReferencePeriod() {
        return referencePeriod;
    }

    /**
     * @return the taperPercent
     */
    public double getTaperPercent() {
        return taperPercent;
    }

    /**
     * @return the tfactor
     */
    public double getTfactor() {
        return tfactor;
    }

    /**
     * @return the highpassFrac
     */
    public double getHighpassFrac() {
        return highpassFrac;
    }

    /**
     * @return the highCutFrac
     */
    public double getHighCutFrac() {
        return highCutFrac;
    }

    /**
     * @return the lowCutFrac
     */
    public double getLowCutFrac() {
        return lowCutFrac;
    }

    public double getReferenceDistance() {
        return referenceDistance;
    }

    private static class MiscParamsHolder {

        private static final MiscParams instance = new MiscParams();
    }

    public static MiscParams getInstance() {
        return MiscParamsHolder.instance;
    }

    private MiscParams() {
    }
}
