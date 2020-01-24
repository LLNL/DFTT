package llnl.gnem.core.gui.map.location;

import java.awt.Rectangle;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;


public abstract class LocationTableHolder<T extends LocationInfo> implements LocationView<T> {

    private final JScrollPane scrollPane;
    protected final SortTable table;
    protected final LocationTableModel<T> tableModel;
    private JPopupMenu popupMenu;

    public LocationTableHolder(LocationModel<T> model) {
        this.tableModel = new LocationTableModel<>(model);

        table = new SortTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.addHighlighter(HighlighterFactory.createSimpleStriping());
        table.setRolloverEnabled(true);
        table.setColumnControlVisible(true);
        scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        table.getSelectionModel().addListSelectionListener(new TableSelectionListener(model));
    }

    public JComponent getDisplayable() {
        return scrollPane;
    }

    public boolean isNextAvailable() {
        ListSelectionModel lsm = table.getSelectionModel();
        if (lsm.isSelectionEmpty()) {
            return false;
        } else {
            int idx = table.getSelectedRow();
            return idx < table.getRowCount() - 1;

        }
    }

    public boolean isPreviousAvailable() {
        ListSelectionModel lsm = table.getSelectionModel();
        if (lsm.isSelectionEmpty()) {
            return false;
        } else {
            int idx = table.getSelectedRow();
            return idx > 0;

        }
    }

    public T getNext() {
        ListSelectionModel lsm = table.getSelectionModel();
        if (lsm.isSelectionEmpty()) {
            return null;
        } else {
            int idx = table.getSelectedRow() + 1;
            int modelIndex = table.getRowSorter().convertRowIndexToModel(idx);
            return tableModel.getData(modelIndex);
        }
    }

    public T getPrevious() {
        ListSelectionModel lsm = table.getSelectionModel();
        if (lsm.isSelectionEmpty()) {
            return null;
        } else {
            int idx = table.getSelectedRow() - 1;
            int modelIndex = table.getRowSorter().convertRowIndexToModel(idx);
            return tableModel.getData(modelIndex);
        }
    }

    public T getCurrent() {
        ListSelectionModel lsm = table.getSelectionModel();
        if (lsm.isSelectionEmpty()) {
            return null;
        } else {
            int idx = table.getSelectedRow();
            int modelIndex = table.getRowSorter().convertRowIndexToModel(idx);
            return tableModel.getData(modelIndex);
        }
    }

    @Override
    public void clearLocations() {
        tableModel.clear();
    }

    @Override
    public void addLocations(Collection<T> locations) {
        tableModel.addData(locations);
    }

    @Override
    public void updateCurrentLocation(T location) {
        if (location != null) {
            int modelIndex = tableModel.findLocation(location);
            if (modelIndex >= 0) {
                int viewIndex = table.getRowSorter().convertRowIndexToView(modelIndex);
                table.getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
                table.scrollRectToVisible(new Rectangle(0, table.getRowHeight() * (viewIndex), 0, table.getRowHeight()));
            }
        }
    }

    @Override
    public void updateLocations(Collection<T> locations) {
    }

    @Override
    public void retrievalIsCompleted() {
    }

    @Override
    public void locationWasRemoved(T location) {
        tableModel.removeLocation(location);
    }

    protected final void setPopup(JPopupMenu menu) {
        popupMenu = menu;
    }

    protected final JPopupMenu getPopup() {
        return popupMenu;
    }

    protected final SortTable getTable() {
        return table;
    }

    protected class SortTable extends JXTable {

        private static final long serialVersionUID = 1L;
        boolean sortInProgress = false;

        public SortTable(LocationTableModel model) {
            super(model);
        }

        @Override
        public void toggleSortOrder(int column) {
            sortInProgress = true;
            super.toggleSortOrder(column);
            sortInProgress = false;
        }

        @Override
        public void toggleSortOrder(Object obj) {
            sortInProgress = true;
            super.toggleSortOrder(obj);
            sortInProgress = false;
        }
    }

    private class TableSelectionListener implements ListSelectionListener {

        private final LocationModel<T> model;

        public TableSelectionListener(LocationModel<T> model) {
            this.model = model;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            // Ignore extra messages.
            if (e.getValueIsAdjusting()) {
                return;
            }

            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (!lsm.isSelectionEmpty()) {
                if (!table.sortInProgress) {
                    final int idx = table.getSelectedRow();
                    if (idx >= 0) {
                        int modelIndex = table.getRowSorter().convertRowIndexToModel(idx);
                        final T selected = tableModel.getData(modelIndex);
                        if (selected != null) {
                            model.setCurrent(selected);
                        }
                    }
                } else {
                    // Re-sort - make sure to update the selected row to current location
                    updateCurrentLocation(model.getCurrent());
                }
            }
        }
    }
}
