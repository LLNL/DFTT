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

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public enum FeatureColumn {
    SNR(false, -1),
    AMPLITUDE(false, -1),
    TIME_CENTROID(false, -1),
    TIME_SIGMA(false, -1),
    TEMPORAL_SKEWNESS(false, -1),
    TEMPORAL_KURTOSIS(false, -1),
    FREQ_SIGMA(false, -1),
    TBP(false, -1),
    SKEWNESS(false, -1),
    KURTOSIS(false, -1),
    RAW_SKEWNESS(false, -1),
    RAW_KURTOSIS(false, -1),
    FREQ_CENTROID(false,-1);

    private final boolean editable;
    private final int columnWidth;

    FeatureColumn(boolean editable, int columnWidth) {
        this.editable = editable;
        this.columnWidth = columnWidth;
    }

    public boolean isEditable() {
        return editable;
    }

    public static FeatureColumn getColumn(int col) {
        for (FeatureColumn column : FeatureColumn.values()) {
            if (column.ordinal() == col) {
                return column;
            }
        }
        return null;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

}
