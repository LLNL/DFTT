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
package llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay;

import llnl.gnem.apps.detection.core.dataObjects.TriggerDataFeatures;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class FeatureRow {

    private final TriggerDataFeatures data;

    public FeatureRow(TriggerDataFeatures data) {
        this.data = data;
    }

    public TriggerDataFeatures getData() {
        return data;
    }

    public static int getColumnCount() {
        return FeatureColumn.values().length;
    }

    public static Class getColumnClass(int col) {
        FeatureColumn column = FeatureColumn.values()[col];
        switch (column) {
            case SNR:
                return Double.class;
            case AMPLITUDE:
                return Double.class;
            case TIME_CENTROID:
                return Double.class;
            case TIME_SIGMA:
                return Double.class;
            case TEMPORAL_SKEWNESS:
                return Double.class;
            case TEMPORAL_KURTOSIS:
                return Double.class;
            case FREQ_SIGMA:
                return Double.class;
            case TBP:
                return Double.class;
            case SKEWNESS:
                return Double.class;
            case KURTOSIS:
                return Double.class;
            case RAW_SKEWNESS:
                return Double.class;
            case RAW_KURTOSIS:
                return Double.class;
            case FREQ_CENTROID:
                return Double.class;
            default:
                throw new IllegalStateException("Unrecognized Enum Value " + column);
        }
    }
    
    public static FeatureColumn getColumn(int col){
        return FeatureColumn.values()[col];
    }

    public static String getColumnName(int col) {
        FeatureColumn column = FeatureColumn.values()[col];

        return column.toString();
    }

    public boolean isEditable(int col) {
        FeatureColumn column = FeatureColumn.values()[col];
        return column.isEditable();
    }

    public Object getValue(int col) {
        FeatureColumn column = FeatureColumn.values()[col];
        switch (column) {
            case SNR:
                return data.getSnr();
            case AMPLITUDE:
                return data.getAmplitude();
            case TIME_CENTROID:
                return data.getTimeCentroid();
            case TIME_SIGMA:
                return data.getTimeSigma();
            case TEMPORAL_SKEWNESS:
                return data.getTemporalSkewness();
            case TEMPORAL_KURTOSIS:
                return data.getTemporalKurtosis();
            case FREQ_SIGMA:
                return data.getFrequencySigma();
            case TBP:
                return data.getTbp();
            case SKEWNESS:
                return data.getSkewness();
            case KURTOSIS:
                return data.getKurtosis();
            case RAW_SKEWNESS:
                return data.getRawSignalSkewness();
            case RAW_KURTOSIS:
                return data.getRawSignalKurtosis();
            case FREQ_CENTROID:
                return data.getFreqCentroid();
            default:
                throw new IllegalStateException("Unrecognized Enum Value " + column);
        }
    }

    public void setValue(Object value, int col) {
    }

}
