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
package llnl.gnem.dftt.core.gui.waveform.phaseVisibility;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.table.AbstractTableModel;
import llnl.gnem.dftt.core.dataAccess.dataObjects.SeismicPhase;

/**
 * Created by: dodge1 Date: Nov 17, 2004 COPYRIGHT NOTICE GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class PhaseDataTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 644276111008453387L;

    private final String[] columnNames = {"Phase", "Used", "Description"};
    private final ArrayList<SelectablePhase> phaseData;

    public PhaseDataTableModel() {
        phaseData = new ArrayList<>();
    }

    /**
     * Gets the number of columns in this model.
     *
     * @return The number of columns.
     */
    @Override
    public final int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Gets the number of rows in this model.
     *
     * @return The number of rows in this model.
     */
    @Override
    public final int getRowCount() {
        return phaseData.size();
    }

    @Override
    public final Object getValueAt(int row, int column) {
        SelectablePhase data = phaseData.get(row);
        SeismicPhase phase = data.getPhase();
        switch (column) {
            case 0:
                return phase.getName();
            case 1:
                return data.isSelected();
            default:
                return phase.getDescription();
        }
    }

    /**
     * Sets the value of the specified cell. In this table model, only the
     * Boolean column can have its value changed.
     *
     * @param value The new Boolean value for this row.
     * @param row The row to be changed.
     * @param col Not used.
     */
    @Override
    public final void setValueAt(Object value, int row, int col) {
        SelectablePhase data = phaseData.get(row);
        SeismicPhase phase = data.getPhase();
        if (1 == col) {
            Boolean selected = (Boolean) value;
            data.setSelected(selected);
            fireTableCellUpdated(row, col);
            if (selected) {
                BasePreferredPhaseManager.getInstance().addPreferredPhase(phase);
            } else {
                BasePreferredPhaseManager.getInstance().removePreferredPhase(phase);
            }
        }
    }

    public final void setUsed(String phase, boolean used) {
        for (int j = 0; j < phaseData.size(); ++j) {
            SelectablePhase data = phaseData.get(j);
            if (data.getPhase().getName().equals(phase)) {
                Boolean selected = used;
                data.setSelected(selected);
                fireTableCellUpdated(j, 1);
            }
        }
    }

    /**
     * Gets the name of the specified column.
     *
     * @param col The column whose name is to be retrieved.
     * @return The column name.
     */
    @Override
    public final String getColumnName(int col) {
        return columnNames[col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    @Override
    public Class getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }

    /**
     * Controls which cells can be edited by user. First two contain
     * information-only Strings so should not be editable.
     *
     * @param row The requested row
     * @param col The requested column
     * @return true if the cell is editable
     */
    @Override
    public final boolean isCellEditable(int row, int col) {
        return col == 1;
    }

    public void clear() {
        phaseData.clear();
        fireTableDataChanged();
    }

    public void add(SelectablePhase data) {
        phaseData.add(data);
        fireTableDataChanged();
    }

    void setPhaseData(Collection<SelectablePhase> phases) {
        phaseData.clear();
        for (SelectablePhase aPhase : phases) {
            phaseData.add(aPhase);
        }
        fireTableDataChanged();
    }
}
