/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.statistics;

/**
 *
 * @author dodge1
 */
public class HistogramData {
    private final float[] bins;
    private final float[] values;
    public HistogramData(float[] bins, float[] values) {
        this.bins = bins.clone();
        this.values = values.clone();
    }

    /**
     * @return the bins
     */
    public float[] getBins() {
        return bins;
    }

    /**
     * @return the values
     */
    public float[] getValues() {
        return values;
    }
    
}
