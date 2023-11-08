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
package llnl.gnem.dftt.core.gui.filter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import llnl.gnem.dftt.core.gui.filter.actions.RemoveFilterAction;
import llnl.gnem.dftt.core.waveform.filter.FilterClient;
import llnl.gnem.dftt.core.util.Passband;
import llnl.gnem.dftt.core.gui.util.SpringUtilities;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;

/**
 * User: Doug Date: Feb 8, 2012 Time: 10:17:22 AM
 */
@SuppressWarnings({"TypeMayBeWeakened", "MagicNumber"})
public class FilterGui extends JPanel implements FilterView,
        ListSelectionListener {

    private static final long serialVersionUID = 948724504295345429L;

    private JFormattedTextField highCornerEdit;
    private JList filterList;
    private JFormattedTextField lowCutEdit;
    private JCheckBox zeroPhaseCheck;
    private SpinnerModel spinnerModel;
    private DefaultListModel listModel;
    private SpinnerModel passbandSpinnerModel;
    private boolean skipModelUpdate;

    private final Collection<FilterClient> clients;
    private final JFrame owningContainer;
    private final FilterToolbarControl toolbarControl;

    public FilterGui(JFrame owningContainer) {
        super(new SpringLayout());
        JPanel lhs = new JPanel(new SpringLayout());
        JPanel rhs = new JPanel(new SpringLayout());
        this.add(lhs);
        this.add(rhs);
        int numRows = 1;
        int numColumns = 2;
        SpringUtilities.makeCompactGrid(this, numRows, numColumns, 0, 0, 0, 0);
        setUpProjectFilterPanel(lhs);
        setupRightHandPanel(rhs);
        clients = new ArrayList<>();
        skipModelUpdate = false;
        this.owningContainer = owningContainer;
        toolbarControl = new FilterToolbarControl(this);
    }

    public void addClient(FilterClient client) {
        clients.add(client);
    }

    private void setUpProjectFilterPanel(JPanel owner) {
        JPanel panel = new JPanel(new SpringLayout());
        JPanel spacer = new JPanel();
        JScrollPane jScrollPane1 = new JScrollPane();
        listModel = new DefaultListModel();
        filterList = new JList(listModel);

        filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        filterList.addListSelectionListener(this);

        filterList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {          // Double-click
                    applySelectedFilter();
                }
            }
        });

        jScrollPane1.getViewport().setView(filterList);
        setLayout(new GridLayout(1, 2, 10, 5));
        setBorder(new BevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel.setLayout(new BorderLayout());
        panel.setBorder(new javax.swing.border.TitledBorder("Available Filter List"));
        filterList.setBorder(new javax.swing.border.BevelBorder(BevelBorder.LOWERED));
        jScrollPane1.setViewportView(filterList);
        panel.add(jScrollPane1, BorderLayout.CENTER);

        owner.add(panel);
        owner.add(spacer);
        final int numRows = 2;
        final int numColumns = 1;
        SpringUtilities.makeCompactGrid(owner, numRows, numColumns, 0, 0, 0, 0);

    }

    private void setUpFilterOrderPanel(JPanel panel) {
        JLabel label = new JLabel();
        JPanel orderPanel = new JPanel();
        Integer[] values = new Integer[7];
        values[0] = 2;
        values[1] = 3;
        values[2] = 4;
        values[3] = 5;
        values[4] = 6;
        values[5] = 7;
        values[6] = 8;
        spinnerModel = new SpinnerListModel(values);
        JSpinner orderSpinner = new JSpinner(spinnerModel);
        orderSpinner.setPreferredSize(new Dimension(60, 21));
        orderPanel.add(orderSpinner);
        JFormattedTextField tf = ((JSpinner.DefaultEditor) orderSpinner.getEditor()).getTextField();
        tf.setEditable(false);
        tf.setBackground(Color.white);
        label.setText("Filter Order");
        orderPanel.add(label);
        panel.add(orderPanel);
    }

    private void setUpLowCutEditPanel(JPanel panel) {
        JPanel lcPanel = new JPanel();
        lowCutEdit = new JFormattedTextField(0.0);
        JLabel label = new JLabel();
        lowCutEdit.setMinimumSize(new Dimension(30, 21));
        lowCutEdit.setPreferredSize(new Dimension(60, 21));
        lcPanel.add(lowCutEdit);
        label.setText("Low Corner");
        lcPanel.add(label);
        panel.add(lcPanel);
    }

    private void setUpPassbandSpinner(JPanel panel) {
        JPanel pbPanel = new JPanel();
        JLabel label = new JLabel();
        Passband[] pBvalues = Passband.getAvailablePassBands();
        passbandSpinnerModel = new SpinnerListModel(pBvalues);
        JSpinner passBandSpinner = new JSpinner(passbandSpinnerModel);
        passBandSpinner.setPreferredSize(new Dimension(60, 26));

        JFormattedTextField tf = ((JSpinner.DefaultEditor) passBandSpinner.getEditor()).getTextField();
        tf.setEditable(false);
        tf.setBackground(Color.white);
        pbPanel.add(passBandSpinner);
        label.setText("Pass Band");
        pbPanel.add(label);
        panel.add(pbPanel);
    }

    private void setUpHighCornerPanel(JPanel panel) {
        JPanel hcPanel = new JPanel();
        highCornerEdit = new JFormattedTextField(1000.0);
        JLabel label = new JLabel();
        highCornerEdit.setPreferredSize(new Dimension(60, 21));
        hcPanel.add(highCornerEdit);
        label.setText("High Corner");
        hcPanel.add(label);
        panel.add(hcPanel);
    }

    private JPanel setUpFilterParametersPanel() {
        JPanel panel = new JPanel(new SpringLayout());
        panel.setBorder(new javax.swing.border.TitledBorder("Filter Parameters"));
        panel.setPreferredSize(new Dimension(150, 400));
        setUpFilterOrderPanel(panel);
        setUpLowCutEditPanel(panel);
        setUpHighCornerPanel(panel);
        setUpPassbandSpinner(panel);
        setUpZeroPhaseCheck(panel);
        final int numRows = 5;
        final int numColumns = 1;
        SpringUtilities.makeCompactGrid(panel, numRows, numColumns, 10, 10, 2, 2);
        return panel;
    }

    private void setUpZeroPhaseCheck(JPanel panel) {
        JPanel zpPanel = new JPanel();
        zeroPhaseCheck = new JCheckBox();
        zeroPhaseCheck.setText("Zero-Phase");
        zpPanel.add(zeroPhaseCheck);
        panel.add(zpPanel);
    }

    private void setupRightHandPanel(JPanel rhs) {
        rhs.add(setUpFilterParametersPanel());
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 100));
        rhs.add(spacer);
        int numRows = 2;
        int numColumns = 1;
        SpringUtilities.makeCompactGrid(rhs, numRows, numColumns, 0, 0, 0, 0);
    }

    @Override
    public void update() {
        FilterModel model = FilterModel.getInstance();
        ArrayList<StoredFilter> filters = model.getMyStoredFilters();
        StoredFilter aFilter = model.getCurrentFilter();
        if (aFilter != null) {
            spinnerModel.setValue(aFilter.getOrder());
            lowCutEdit.setValue(aFilter.getLowpass());
            highCornerEdit.setValue(aFilter.getHighpass());
            passbandSpinnerModel.setValue(aFilter.getPassband());
            zeroPhaseCheck.setSelected(!aFilter.isCausal());
        }
        filterList.removeAll();
        listModel.removeAllElements();
        for (StoredFilter filter : filters) {
            listModel.addElement(filter);
        }

        for (int j = 0; j < listModel.getSize(); ++j) {
            StoredFilter filter = (StoredFilter) listModel.elementAt(j);
            if (aFilter != null && filter.equals(aFilter)) {
                skipModelUpdate = true;
                filterList.setSelectedIndex(j);
                break;
            }
        }
        skipModelUpdate = false;
        int index = filterList.getSelectedIndex();
        if (index < 0) {
            index = 0;
        }
        filterList.ensureIndexIsVisible(index);

        toolbarControl.setFilters(filters);
        StoredFilter selected = (StoredFilter) filterList.getSelectedValue();
        if (selected != null) {
            toolbarControl.setSelectedFilter(selected);
        }
        //      FilterModel.getInstance().setCurrentFilter(selected);
    }

    public StoredFilter getUserDefinedFilter() {

        return new StoredFilter(-1,
                (Passband) passbandSpinnerModel.getValue(),
                !zeroPhaseCheck.isSelected(),
                (Integer) spinnerModel.getValue(),
                (Double) lowCutEdit.getValue(),
                (Double) highCornerEdit.getValue(),
                "-",
                "iir",
                "-", false);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (skipModelUpdate) {
            return;
        }
        if (!e.getValueIsAdjusting()) {

            if (filterList.getSelectedIndex() == -1) {
                RemoveFilterAction.getInstance(this).setSelectedFilter(null);
            } else {

                StoredFilter filter = (StoredFilter) filterList.getSelectedValue();
                FilterModel.getInstance().changeSelectedFilter(filter);
                toolbarControl.setSelectedFilter(filter);
                RemoveFilterAction.getInstance(this).setSelectedFilter(filter);
            }
        }
    }

    public void applySelectedFilter() {
        StoredFilter filter = FilterModel.getInstance().getCurrentFilter();
        for (FilterClient client : clients) {
            client.applyFilter(filter);
        }
    }

    public void unapplyFilter() {
        for (FilterClient client : clients) {
            client.unApplyFilter();
        }
    }

    public void removeSelectedFilter() {
        StoredFilter filter = (StoredFilter) filterList.getSelectedValue();
        new FilterRemovalWorker(filter).execute();
        toolbarControl.removeFilter(filter);
        RemoveFilterAction.getInstance(this).setSelectedFilter(null);
    }

    public JFrame getOwningContainer() {
        return owningContainer;
    }

    FilterToolbarControl getFilterToolbarControl() {
        return toolbarControl;
    }

    public void setSelectedFilter(StoredFilter filter) {
        FilterModel.getInstance().changeSelectedFilter(filter);
    }
}
