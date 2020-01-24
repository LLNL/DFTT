package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public enum ProjectionColumn {
    DETECTORID(false, -1),
    PROJECTION(false, -1),
    SHIFT(false, -1);

    private final boolean editable;
    private final int columnWidth;

    ProjectionColumn(boolean editable, int columnWidth) {
        this.editable = editable;
        this.columnWidth = columnWidth;
    }

    public boolean isEditable() {
        return editable;
    }

    public static ProjectionColumn getColumn(int col) {
        for (ProjectionColumn column : ProjectionColumn.values()) {
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
