package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

import llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay.*;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ProjectionTableHolder implements ProjectionView {

    private final JScrollPane scrollPane;
    private final JTable table;
    private final ProjectionTableModel model;

    public ProjectionTableHolder() {
        model = new ProjectionTableModel();
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(25);
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 50));
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {
                    int idx = table.getSelectedRow();
                    int jdx = table.getRowSorter().convertRowIndexToModel(idx);
                    DetectorProjection dp = model.getData(jdx);
                    new SecondTemplateRetrievalWorker(dp).execute();
                } else {
                }

            }
        });
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    @Override
    public void updateForNewProjection() {
        Collection<DetectorProjection> values = ProjectionModel.getInstance().getProjectionCollection().getProjections();
        model.setData(values);
    }

    @Override
    public void clear() {
        model.clear();
    }

}
