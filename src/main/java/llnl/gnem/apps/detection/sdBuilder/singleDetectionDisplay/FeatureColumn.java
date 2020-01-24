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
