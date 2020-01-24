package llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.table.AbstractTableModel;
import llnl.gnem.apps.detection.core.dataObjects.TriggerDataFeatures;

/**
 * Created by dodge1
 * Date: Feb 12, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class FeatureTableModel extends AbstractTableModel {

    private static final long serialVersionUID = -2202131186684209449L;


    private final ArrayList<FeatureRow> rows;

    public FeatureTableModel() {
        rows = new ArrayList<>();
    }

    @Override
    public int getColumnCount() {
        return FeatureRow.getColumnCount();
    }

    @Override
    public Object getValueAt(int row, int col) {
        FeatureRow arow = rows.get(row);
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
        return FeatureRow.getColumnClass(col);
    }

    @Override
    public String getColumnName(int col) {
        return FeatureRow.getColumnName(col);
    }
    
    public FeatureColumn getFeatureColumn(int col){
        return FeatureRow.getColumn(col);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        FeatureRow arow = rows.get(row);
        return arow.isEditable(col);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        FeatureRow arow = rows.get(row);
        arow.setValue(value, col);
        fireTableCellUpdated(row, col);
    }


    TriggerDataFeatures getData(int row) {
        return rows.get(row).getData();
    }


    public void addData(TriggerDataFeatures data) {
        FeatureRow row = new FeatureRow(data);
        rows.add(row);
        fireTableDataChanged();
    }
}