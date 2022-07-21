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
package llnl.gnem.apps.detection.sdBuilder.stackViewer;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.CorrelationTraceData;
import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.BaseTraceData;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataUnits;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author dodge1
 */
public class SingleComponentStack {

    private final StreamKey key;
    private final ArrayList<CorrelationComponent> data;

    public SingleComponentStack(StreamKey key) {
        this.key = key;
        data = new ArrayList<>();
    }

    public void addTrace(CorrelationComponent ctd) {
        data.add(ctd);
    }
    
    public Collection<CorrelationComponent> getStackData()
    {
        return new ArrayList<>(data);
    }
    
    public Double getDNorth()
    {
        Double result = null;
        for(CorrelationComponent cc : data){
            Double tmp = cc.getDnorth();
            if(result == null)result = tmp;
            
        }
        return result;
    }
    
    
    Double getDeast() {
        Double result = null;
        for(CorrelationComponent cc : data){
            Double tmp = cc.getDeast();
            if(result == null)result = tmp;
            
        }
        return result;
    }

    public BaseTraceData produceWeightedStack() {
        double earliest = Double.MAX_VALUE;
        double latest = -earliest;
        double maxLengthSeconds = 0;
        int maxNsamps = 0;
        for (CorrelationComponent cc : data) {
            CorrelationTraceData td = (CorrelationTraceData) cc.getCorrelationTraceData();
            double traceStart = td.getTime().getEpochTime();
            double nominalPickTime = td.getNominalPick().getTime();
            double ccShift = cc.getShift();
            double start = traceStart - nominalPickTime + ccShift;
            double traceEnd = td.getEpoch().getEnd() - nominalPickTime + ccShift;

            if (start < earliest) {
                earliest = start;
            }
            if (traceEnd > latest) {
                latest = traceEnd;
            }
            maxLengthSeconds = latest - earliest;
            maxNsamps = (int) (maxLengthSeconds * td.getSampleRate() + 1);
        }
        float[] result = new float[maxNsamps];
        double samprate = -1;
        for (CorrelationComponent cc : data) {
            CorrelationTraceData td = (CorrelationTraceData) cc.getCorrelationTraceData();
            CssSeismogram filtered = td.getSeismogram();
            NominalArrival na = cc.getNominalPick();
            double medianNoiseVariance = Math.sqrt(getNoiseVariance(filtered, na));
            float[] plotData = td.getPlotData();
            int pickIndex = (int) Math.round((na.getTime() - filtered.getTimeAsDouble()) * filtered.getSamprate());
            double snrWindowLengthSeconds = 5.0;
            int snrWindowLengthSamples = (int) Math.round(snrWindowLengthSeconds * filtered.getSamprate());
            int startIndex = pickIndex - snrWindowLengthSamples;
            if (startIndex < 0) {
                startIndex = 0;
            }
            int endIndex = pickIndex + snrWindowLengthSamples;
            if (endIndex > plotData.length - 1) {
                endIndex = plotData.length - 1;
            }
            double snr = SeriesMath.getSnr(plotData, td.getSampleRate(), pickIndex, startIndex, endIndex);
            if (snr <= 0 || medianNoiseVariance == 0) {
                continue;
            }
            double traceStart = td.getTime().getEpochTime();
            double nominalPickTime = td.getNominalPick().getTime();
            double ccShift = cc.getShift();
            double start = traceStart - nominalPickTime + ccShift;
            double offset = start - earliest;
            samprate = td.getSampleRate();
            int intOffset = (int) Math.round(offset * samprate);
            SeriesMath.MultiplyScalar(plotData, 1.0 / medianNoiseVariance * Math.sqrt(snr));
            for (int j = 0; j < plotData.length; ++j) {
                int idx = intOffset + j;
                if (idx >= 0 && idx < result.length) {
                    result[idx] = result[idx] + plotData[j];
                }
            }
        }

        double pToP = SeriesMath.getPeakToPeakAmplitude(result);
        SeriesMath.MultiplyScalar(result, 1.0 / pToP);
        double startTime = -ParameterModel.getInstance().getPrepickSeconds();
        CssSeismogram tmpResult = new CssSeismogram(1L, key, result, samprate, new TimeT(startTime), 1.0, 1.0);
        return new BaseTraceData(tmpResult, WaveformDataType.unknown, WaveformDataUnits.unknown);
    }

    
    
    public BaseTraceData produceStack() {
        double earliest = Double.MAX_VALUE;
        double latest = -earliest;
        double maxLengthSeconds = 0;
        int maxNsamps = 0;
        for (CorrelationComponent cc : data) {
            CorrelationTraceData td = (CorrelationTraceData) cc.getCorrelationTraceData();
            double traceStart = td.getTime().getEpochTime();
            double nominalPickTime = td.getNominalPick().getTime();
            double ccShift = cc.getShift();
            double start = traceStart - nominalPickTime + ccShift;
            double traceEnd = td.getEpoch().getEnd() - nominalPickTime + ccShift;

            if (start < earliest) {
                earliest = start;
            }
            if (traceEnd > latest) {
                latest = traceEnd;
            }
            maxLengthSeconds = latest - earliest;
            maxNsamps = (int) (maxLengthSeconds * td.getSampleRate() + 1);
        }
        float[] result = new float[maxNsamps];
        double samprate = -1;
        for (CorrelationComponent cc : data) {
            CorrelationTraceData td = (CorrelationTraceData) cc.getCorrelationTraceData();
            CssSeismogram filtered = td.getSeismogram();
            float[] plotData = filtered.getData();
            double p2p= SeriesMath.getPeakToPeakAmplitude(plotData);
            double scale = p2p >0 ? 1/p2p : 1;
            SeriesMath.MultiplyScalar(plotData, scale);

            double traceStart = td.getTime().getEpochTime();
            double nominalPickTime = td.getNominalPick().getTime();
            double ccShift = cc.getShift();
            double start = traceStart - nominalPickTime + ccShift;
            double offset = start - earliest;
            samprate = td.getSampleRate();
            int intOffset = (int) Math.round(offset * samprate);
            for (int j = 0; j < plotData.length; ++j) {
                int idx = intOffset + j;
                if (idx >= 0 && idx < result.length) {
                    result[idx] = result[idx] + plotData[j];
                }
            }
        }

        double pToP = SeriesMath.getPeakToPeakAmplitude(result);
        SeriesMath.MultiplyScalar(result, 1.0 / pToP);
        double startTime = -ParameterModel.getInstance().getPrepickSeconds();
        CssSeismogram tmpResult = new CssSeismogram(1L, key, result, samprate, new TimeT(startTime), 1.0, 1.0);
        return new BaseTraceData(tmpResult, WaveformDataType.unknown, WaveformDataUnits.unknown);
    }

    public double getDelta() {
        for (CorrelationComponent cc : data) {
            return cc.getCorrelationTraceData().getDelta();
        }

        return 0.001;
    }

    private double getNoiseVariance(CssSeismogram filtered, NominalArrival na) {
        int minSamplesPerWindow = 10;
        double arrivalTime = na.getTime();
        double seismogramBeginTime = filtered.getTimeAsDouble();
        double offsetToWindow = arrivalTime - seismogramBeginTime;
        double taperPercent = CorrelatedTracesModel.getSeismogramTaperPercent();
        double startingOffset = filtered.getLengthInSeconds() * (taperPercent / 100.0);
        double noiseWindowLength = offsetToWindow - startingOffset;
        double desiredSampleWindowLengthSeconds = 1.0;
        double sampleFrequency = filtered.getSamprate();
        if (sampleFrequency * desiredSampleWindowLengthSeconds < minSamplesPerWindow) {
            desiredSampleWindowLengthSeconds = minSamplesPerWindow / sampleFrequency;
        }
        int numWindows = (int) (noiseWindowLength / desiredSampleWindowLengthSeconds);
        if (numWindows < 1) {
            return 1.0;
        } else {
            double[] values = new double[numWindows];
            for (int j = 0; j < numWindows; ++j) {
                Epoch epoch = new Epoch(seismogramBeginTime + startingOffset, seismogramBeginTime + startingOffset + desiredSampleWindowLengthSeconds);
                TimeSeries ts = filtered.crop(epoch);
                double variance = ts.getVariance();
                values[j] = variance;
            }
            return SeriesMath.getMean(values);
        }
    }

}
