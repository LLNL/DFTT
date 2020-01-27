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
