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
package llnl.gnem.dftt.core.correlation;

import llnl.gnem.dftt.core.correlation.util.NominalArrival;
import llnl.gnem.dftt.core.util.SeriesMath;
import llnl.gnem.dftt.core.waveform.BaseTraceData;
import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.dftt.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.dftt.core.waveform.responseProcessing.WaveformDataUnits;

/**
 *
 * @author dodge1
 */
public class CorrelationTraceData extends BaseTraceData {

    private NominalArrival referenceArrival;

    public CorrelationTraceData(CssSeismogram seismogram, WaveformDataType dataType,
            WaveformDataUnits dataUnits, NominalArrival arrival) {
        super(seismogram, dataType, dataUnits);
        referenceArrival = arrival;
    }

    public CorrelationTraceData(CorrelationTraceData other) {
        super(other);
        this.referenceArrival = other.referenceArrival;
    }

    public NominalArrival getNominalPick() {
        return referenceArrival;
    }
    
    public void replaceNominalPick(NominalArrival arrival){
        referenceArrival = new NominalArrival(arrival);
    }

    @Override
    public CorrelationTraceData newCopy() {
        return new CorrelationTraceData(this);
    }

    public float[] getScaledData(double rmin, double rmax) {
        float[] result = this.getBackupSeismogram().getData();
        scaleArray(rmax, rmin, result);

        return result;
    }

    private void scaleArray(double rmax, double rmin, float[] result) {
        double rrange = rmax - rmin;
        double max = SeriesMath.getMax(result);
        double min = SeriesMath.getMin(result);
        double range = max - min;
        range /= rrange;
        for (int j = 0; j < result.length; ++j) {
            result[j] /= range;
        }
    }

    public void addSeismogram(CssSeismogram seis) {
        this.getBackupSeismogram().addInPlace(seis);
    }

    public void scaleTo(double min, double max) {
        this.getBackupSeismogram().scaleTo(min,max);
    }
}
