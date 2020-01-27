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
