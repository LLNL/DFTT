package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ProjectionRow {

    private final DetectorProjection data;

    public ProjectionRow(DetectorProjection data) {
        this.data = data;
    }

    public DetectorProjection getData() {
        return data;
    }

    public static int getColumnCount() {
        return ProjectionColumn.values().length;
    }

    public static Class getColumnClass(int col) {
        ProjectionColumn column = ProjectionColumn.values()[col];
        switch (column) {
            case DETECTORID:
                return Integer.class;
            case PROJECTION:
                return Double.class;
            case SHIFT:
                return Integer.class;
            default:
                throw new IllegalStateException("Unrecognized Enum Value " + column);
        }
    }
    
    public static ProjectionColumn getColumn(int col){
        return ProjectionColumn.values()[col];
    }

    public static String getColumnName(int col) {
        ProjectionColumn column = ProjectionColumn.values()[col];

        return column.toString();
    }

    public boolean isEditable(int col) {
        ProjectionColumn column = ProjectionColumn.values()[col];
        return column.isEditable();
    }

    public Object getValue(int col) {
        ProjectionColumn column = ProjectionColumn.values()[col];
        switch (column) {
            case DETECTORID:
                return data.getDetectorid();
            case PROJECTION:
                return data.getProjection();
            case SHIFT:
                return data.getShift();
            default:
                throw new IllegalStateException("Unrecognized Enum Value " + column);
        }
    }

    public void setValue(Object value, int col) {
    }

}
