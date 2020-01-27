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

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.table.AbstractTableModel;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ProjectionTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 3655284253530487901L;

    private final ArrayList<ProjectionRow> rows;

    public ProjectionTableModel() {
        rows = new ArrayList<>();
    }

    @Override
    public int getColumnCount() {
        return ProjectionRow.getColumnCount();
    }

    @Override
    public Object getValueAt(int row, int col) {
        ProjectionRow arow = rows.get(row);
        return arow.getValue(col);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    public void clear() {
        rows.clear();
        fireTableDataChanged();
    }

    @Override
    public Class getColumnClass(int col) {
        return ProjectionRow.getColumnClass(col);
    }

    @Override
    public String getColumnName(int col) {
        return ProjectionRow.getColumnName(col);
    }

    public ProjectionColumn getProjectionColumn(int col) {
        return ProjectionRow.getColumn(col);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        ProjectionRow arow = rows.get(row);
        return arow.isEditable(col);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        ProjectionRow arow = rows.get(row);
        arow.setValue(value, col);
        fireTableCellUpdated(row, col);
    }

    DetectorProjection getData(int row) {
        return rows.get(row).getData();
    }

    public void setData(Collection<DetectorProjection> values) {
        rows.clear();
        for (DetectorProjection dp : values) {
            ProjectionRow row = new ProjectionRow(dp);
            rows.add(row);
        }
        fireTableDataChanged();
    }

    public void addData(DetectorProjection data) {
        ProjectionRow row = new ProjectionRow(data);
        rows.add(row);
        fireTableDataChanged();
    }
}
