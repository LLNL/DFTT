package llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay;

import java.awt.Dimension;
import java.util.Collection;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import llnl.gnem.apps.detection.core.dataObjects.TriggerDataFeatures;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class FeatureTableHolder implements SingleDetectionView {

    private final JScrollPane scrollPane;
    private final JTable table;
    private final FeatureTableModel model;

    public FeatureTableHolder() {
        model = new FeatureTableModel();
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(25);
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 50));
        table.getSelectionModel().addListSelectionListener(new TableSelectionListener());
        table.setCellSelectionEnabled(true);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void clearFeatures() {
        model.clear();
    }

    public void addFeatures(TriggerDataFeatures newFeatures) {
        model.clear();
        model.addData(newFeatures);
    }

    @Override
    public void traceWasAdded() {
        // Don't need to do anything with this
    }

    @Override
    public void clear() {
        clearFeatures();
    }

    @Override
    public void setTriggerStatistics(TriggerDataFeatures result) {
        addFeatures(result);
    }

    @Override
    public void setFeatureValues(String featureName, Collection<Double> result) {
        // not needed...
    }

    @Override
    public void detectionRetrieved() {
        // Not needed...
    }

    private class TableSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            //Ignore extra messages.
            if (e.getValueIsAdjusting()) {
                return;
            }

            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (!lsm.isSelectionEmpty()) {
                int idx = table.getSelectedRow();
                if (idx >= 0) {
                    int jdx = table.getSelectedColumn();
                    if( jdx >= 0){
                        String columnName = model.getColumnName(jdx);
                        SingleDetectionModel.getInstance().featureSelected(columnName);
                        table.clearSelection();
                    }
                }
            }

        }
    }

}
