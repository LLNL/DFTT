/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.util.seriesMathHelpers;

import java.util.LinkedList;
import java.util.Queue;
import llnl.gnem.core.util.SeriesMath;

/**
 * http://en.wikipedia.org/wiki/Standard_deviation#Rapid_calculation_methods
 * @author dodge1
 */
public class RollingStats {

    private final int n;
    private double mean;
    private double s1;
    private double s2;
    private final Queue<Float> values;

    public RollingStats(float[] data) {
        n = data.length;
        values = new LinkedList<>();
        for(float v : data){
            values.add(v);
        }
        mean = SeriesMath.getMean(data);
        s1 = SeriesMath.getSum(data);
        s2 = SeriesMath.getSumOfSquares(data);
    }

    public void replace(float oldDatum, float newDatum) {
        mean += (newDatum / n) - (oldDatum / n);
        s1 += newDatum - oldDatum;
        s2 += (newDatum * newDatum) - (oldDatum * oldDatum);
    }
    
    public void addLatest( float v){
        float oldest = values.remove();
        replace(oldest,v );
        values.add(v);
    }

    public double getMean() {
        return mean;
    }

    public double getStandardDeviation() {
        return Math.sqrt((n * s2 - s1 * s1) / (n * (n-1)));
    }
    
    @Override
    public String toString()
    {
        return String.format("Mean = %f, std = %f", getMean(), getStandardDeviation());
    }
}