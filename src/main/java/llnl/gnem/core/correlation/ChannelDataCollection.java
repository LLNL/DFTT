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
package llnl.gnem.core.correlation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.BandInfo;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.qc.QCFeatures;

/**
 *
 * @author dodge1
 */
public class ChannelDataCollection {

    private final List<StationEventChannelData> data;
 
    private final double commonRate;
    private final double prePickSeconds;
    private final double postPickSeconds;
    private final PhaseArrivalWindow window;
    private final double delta;

    public ChannelDataCollection(List<StationEventChannelData> data, double inputPrepickSeconds) {
        if( data == null || data.isEmpty()){
            throw new IllegalStateException( "Attempt to construct ChannelDataCollection with null or empty input data!");
        }
        this.data = new ArrayList<>(data);
       
        window = data.iterator().next().getWindow();
        delta = data.iterator().next().getDelta();
        commonRate = maybeResampleData();
        validateSampleRate();
        prePickSeconds = maybeAdjustPrepickSeconds(inputPrepickSeconds);
        if (prePickSeconds != inputPrepickSeconds) {
            ApplicationLogger.getInstance().log(Level.FINEST, String.format("pre-pick seconds adjusted from %s to %s", 
                    inputPrepickSeconds, prePickSeconds));
        }
        postPickSeconds = getBestWindowLength();
    }

    public ChannelDataCollection(ArrayList<StationEventChannelData> data, PairT<Double, Double> windowOffsets) {
        if (data == null || data.isEmpty()) {
            throw new IllegalStateException("Attempt to construct ChannelDataCollection with null or empty input data!");
        }
        this.data = new ArrayList<>(data);
        window = data.iterator().next().getWindow();
        delta = data.iterator().next().getDelta();
        commonRate = maybeResampleData();
        validateSampleRate();
        
        
        double inputPrepickSeconds = windowOffsets.getFirst();
        prePickSeconds = maybeAdjustPrepickSeconds(inputPrepickSeconds);
        if (prePickSeconds != inputPrepickSeconds) {
            ApplicationLogger.getInstance().log(Level.FINEST, String.format("pre-pick seconds adjusted from %s to %s",
                    inputPrepickSeconds, prePickSeconds));
        }
        
        
        postPickSeconds = windowOffsets.getSecond();
    }

    private double maybeResampleData() {
        ArrayList<Double> rates = new ArrayList<>();
        for (StationEventChannelData secd : this.data) {
            rates.add(secd.getSampleRate());
        }
      
        double minRate = SeriesMath.getMin(rates);
        double maxRate = SeriesMath.getMax(rates);
        double aRate = (minRate + maxRate) / 2;
        if ((maxRate - minRate) / maxRate > 0.001) {
            double medianRate = SeriesMath.getMedian(rates);
            double newRate = medianRate;
            if (newRate >= 1) {
                newRate = Math.round(newRate);
                aRate = newRate;
            }
            for (StationEventChannelData secd : this.data) {
                secd.interpolateTo(newRate);
            }
        }
        return aRate;
    }

    private void validateSampleRate() {
        double rate = -999;
        for (StationEventChannelData secd : getData()) {
            double thisRate = secd.getSampleRate();
            if (rate < 0) {
                rate = thisRate;
            } else {
                double fractionalDifference = Math.abs((rate - thisRate) / rate);
                if (fractionalDifference > 0.001) {
                    throw new IllegalStateException("SampleRate differ among channels!");
                }
            }
        }
    }
    
    private double maybeAdjustPrepickSeconds(double prePickSeconds){
        double result = prePickSeconds;
        for( StationEventChannelData secd : this.getData()){
            double rate = secd.getSampleRate();
            CssSeismogram seis = secd.getSeismogram();
            double pickTime = secd.getNominalPickTime();
            double startTime = seis.getTimeAsDouble();
            if( pickTime - result < startTime){
                result = pickTime - startTime - 1/rate;
            }
            
        }
        return result;
    }
    
   

    public int getCommonWindowLengthSamples() {
        double rate = getData().get(0).getSampleRate();
        double duration = prePickSeconds + postPickSeconds;
        return (int) Math.round(duration * rate);
    }

    public int size() {
        return data.size();
    }

    /**
     * @return the data
     */
    public List<StationEventChannelData> getData() {
        return data;
    }

    StationEventChannelData getChannelData(int j) {
        return data.get(j);
    }

    public void outputResults() {
        for (StationEventChannelData secd : data) {
            secd.outputResults();
        }
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public String getSta() {
        return data.get(0).getSta();
    }

    public String getPhase() {
        return data.get(0).getPhase();
    }

    public double getCommonRate() {
        return commonRate;
    }

    private double getBestWindowLength() {
        double minAllowableLength = window.getMinimumWindowLength(delta);
       
        double bestWindowLength = window.getWindowLength(delta);
        Iterator<StationEventChannelData> it = data.iterator();
        while( it.hasNext() ){
            StationEventChannelData secd = it.next();
            double thisLength = secd.getLength();
            if( thisLength < minAllowableLength ){
                it.remove();
            }
            else if( thisLength <bestWindowLength ){
                bestWindowLength = thisLength;
            }
        }
        if( data.size() < 2){
            return -1;
        }
        bestWindowLength -= 1/commonRate; 
        
        if( window.isWholeSeismogramWindow()){
            ArrayList<Double> tmp = new ArrayList<>();
            for( StationEventChannelData secd : data ){
                QCFeatures features = secd.getFeatures();
                double t0 = features.getTimeCentroid(false);
                double sigma = features.getTimeCentroidSigma();
                double aLength = t0 + 2 * sigma;
                tmp.add(aLength);
            }
            double medianLength = SeriesMath.getMedian(tmp);
            if( medianLength < bestWindowLength && medianLength >= minAllowableLength){
                bestWindowLength = medianLength;
            }
        }
        
        return bestWindowLength;
    }

    public double getPrepickSeconds() {
        return prePickSeconds;
    }

    public double getPostPickSeconds() {
        return postPickSeconds;
    }
    
    public double getCommonWindowLengthSeconds()
    {
        return prePickSeconds + postPickSeconds;
    }

    public BandInfo getBand() {
        return data.iterator().next().getBandInfo();
    }

    public PhaseArrivalWindow getWindow() {
        return window;
    }
}
