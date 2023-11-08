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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import llnl.gnem.dftt.core.gui.util.SpringUtilities;
import llnl.gnem.dftt.core.gui.util.Utility;
import llnl.gnem.dftt.core.util.Passband;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class FilterToolbarControl extends JPanel {

    private static final long serialVersionUID = 1705739090296975754L;

    private final JComboBox filterCombo;
    private final FilterGui owner;
    private boolean externalTrigger;

    private static class NoneStoredFilter extends StoredFilter {

        private static final long serialVersionUID = -7050884455944323076L;

        public NoneStoredFilter() {
            super(-5, Passband.BAND_PASS, false,
                    0, 0, 0, "None", "None", "None", false);
        }

        @Override
        public String toString() {
            return "None";
        }
    }

    private static final StoredFilter NONE_FILTER = new NoneStoredFilter();

    public FilterToolbarControl(FilterGui owner) {
        super(new SpringLayout());
        this.owner = owner;
        filterCombo = new JComboBox();
        filterCombo.setPreferredSize(new Dimension(250, 20));
        filterCombo.setMaximumSize(new Dimension(250, 20));
        add(filterCombo);
        filterCombo.addActionListener(new MyActionListener());
        externalTrigger = false;

        addButton(new JButton(new ApplyFilterAction()));
        OpenFilterDialogAction action = new OpenFilterDialogAction();

        addButton(new JButton(action));

        final int numRows = 1;
        final int numColumns = 3;
        SpringUtilities.makeCompactGrid(this, numRows, numColumns, 1, 1, 1, 1);
        setBorder(BorderFactory.createLineBorder(Color.blue));
        setPreferredSize(new Dimension(310, 25));
        setMaximumSize(new Dimension(310, 25));
        setMinimumSize(new Dimension(310, 25));
    }

    class MyActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            applyCurrentFilter();
        }
    }

    public void applyCurrentFilter() {
        if (!externalTrigger) {
            StoredFilter filter = (StoredFilter) filterCombo.getSelectedItem();
            if (filter.getFilterid() == NONE_FILTER.getFilterid()) {
                owner.unapplyFilter();
            } else {
                owner.setSelectedFilter(filter);
                owner.applySelectedFilter();
            }
        }
    }

    public StoredFilter getCurrentFilter() {
        StoredFilter filter = (StoredFilter) filterCombo.getSelectedItem();
        if (filter.getFilterid() == NONE_FILTER.getFilterid()) {
            return null;
        } else {
            return filter;
        }
    }

    public void unApplyFilter() {
        owner.unapplyFilter();
    }

    private void addButton(JButton button) {
        button.setText("");
        button.setPreferredSize(new Dimension(20, 20));
        add(button);
    }

    void setFilters(ArrayList<StoredFilter> filters) {
        externalTrigger = true;
        filterCombo.removeAllItems();
        filterCombo.addItem(NONE_FILTER);
        for (StoredFilter filter : filters) {
            filterCombo.addItem(filter);
        }
        externalTrigger = false;
    }

    public void setSelectedFilter(StoredFilter selected) {
        externalTrigger = true;
        boolean filterSet = false;
        for (int j = 0; j < filterCombo.getModel().getSize(); ++j) {
            Object obj = filterCombo.getItemAt(j);
            if (obj instanceof StoredFilter) {
                StoredFilter sf = (StoredFilter) obj;
                if (sf.getFilterid() == selected.getFilterid()) {
                    filterCombo.setSelectedIndex(j);
                    filterSet = true;
                    break;
                } else if (selected.isFunctionallyEquivalent(sf)) {
                    filterCombo.setSelectedIndex(j);
                    filterSet = true;
                    break;
                }
            }
        }
        if (!filterSet) {
            for (StoredFilter filter : FilterModel.getInstance().getAllStoredFilters()) {
                if (filter.isFunctionallyEquivalent(selected)) {
                    filterCombo.addItem(filter);
                    filterCombo.setSelectedItem(filter);
                    break;
                }
            }
        }

        externalTrigger = false;
    }

    void removeFilter(StoredFilter filter) {
        externalTrigger = true;
        filterCombo.removeItem(filter);
        externalTrigger = false;
    }

    class OpenFilterDialogAction extends AbstractAction {

        private static final long serialVersionUID = -8475164904631425980L;

        private OpenFilterDialogAction() {
            super("Filter", Utility.getIcon(owner, "miscIcons/ShowFilter16.gif"));
            putValue(SHORT_DESCRIPTION, "Open Filter Dialog.");
            putValue(MNEMONIC_KEY, KeyEvent.VK_F);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            owner.getOwningContainer().setVisible(true);
        }
    }

    class ApplyFilterAction extends AbstractAction {

        private static final long serialVersionUID = 4533980142015506737L;

        private ApplyFilterAction() {
            super("Apply", Utility.getIcon(owner, "miscIcons/applyFilter16.gif"));
            putValue(SHORT_DESCRIPTION, "Apply Current Filter.");
            putValue(MNEMONIC_KEY, KeyEvent.VK_A);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            applyCurrentFilter();
        }
    }
}
