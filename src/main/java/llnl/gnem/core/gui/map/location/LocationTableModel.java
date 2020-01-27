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
package llnl.gnem.core.gui.map.location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author addair1
 */
public class LocationTableModel<T extends LocationInfo> extends AbstractTableModel {
    private final List<T> rows;
    private final List<LocationColumn<T>> columns;

    public LocationTableModel(LocationModel<T> model) {
        rows = new ArrayList<T>();
        columns = model.getColumns();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int r, int c) {
        T row = rows.get(r);
        LocationColumn<T> column = columns.get(c);
        return column.getValue(row);
    }

    public void clear() {
        rows.clear();
        fireTableDataChanged();
    }

    @Override
    public Class getColumnClass(int col) {
        return columns.get(col).getClassName();
    }

    @Override
    public String getColumnName(int col) {
        return columns.get(col).toString();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        // Currently, individual cells do not determine editability, the columns do
        return columns.get(col).isEditable();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        T arow = rows.get(row);
        //arow.setValue(value, col);
        fireTableCellUpdated(row, col);
    }

    public void addData(Collection<T> data) {
        for (T sd : data) {
            rows.add(sd);
        }
        fireTableDataChanged();
    }

    public void addData(T data) {
        rows.add(data);
        fireTableDataChanged();
    }

    public T getData(int row) {
        return rows.get(row);
    }
    
    public void removeLocation(T data)
    {
        rows.remove(data);
        fireTableDataChanged();
    }

    public int findLocation(T info) {
        for( int j = 0; j < rows.size(); ++j){
            T aRow = rows.get(j);
            if (aRow == info)
                return j;
        }
        return -1;
    }

    public void updateSingleRow(int modelIndex) {
        this.fireTableRowsUpdated(modelIndex, modelIndex);
    }
}
