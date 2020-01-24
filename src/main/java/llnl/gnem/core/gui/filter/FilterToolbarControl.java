/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.filter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SHORT_DESCRIPTION;

import llnl.gnem.core.util.Passband;
import llnl.gnem.core.gui.util.SpringUtilities;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.waveform.filter.StoredFilter;

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
    		super (-5, Passband.BAND_PASS, false,
    	    		0, 0, 0, "None", "None", "None",false);
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
        filterCombo.setPreferredSize(new Dimension(250,20));
        filterCombo.setMaximumSize(new Dimension(250, 20));
        add(filterCombo);
        filterCombo.addActionListener(new MyActionListener());
        externalTrigger = false;

        addButton(new JButton( new ApplyFilterAction()));
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
            }
            else {
                owner.setSelectedFilter(filter);
                owner.applySelectedFilter();
            }
        }
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

    void setSelectedFilter(StoredFilter selected) {
        externalTrigger = true;
        filterCombo.setSelectedItem(selected);
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
