/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.filter.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.core.gui.filter.FilterAdditionWorker;
import llnl.gnem.core.gui.filter.FilterGui;
import llnl.gnem.core.gui.filter.FilterModel;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 * User: Doug
 * Date: Feb 8, 2009
 * Time: 11:19:02 AM
 */
public class AddCurrentAction extends AbstractAction {

    private static AddCurrentAction ourInstance = null;
    private static final long serialVersionUID = -758004814166881039L;
    private FilterGui associatedGui = null;

    public synchronized static AddCurrentAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new AddCurrentAction(owner);
        }
        return ourInstance;
    }

    private AddCurrentAction(Object owner) {
        super("Add Current", Utility.getIcon(owner, "miscIcons/add-16.gif"));
        putValue(SHORT_DESCRIPTION, "Add current specifications to the filter list.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        StoredFilter filter = associatedGui.getUserDefinedFilter();
        if(!FilterModel.getInstance().addFilterFromPool(filter)){ // We did not have this filter in the allFilters pool
            new FilterAdditionWorker(filter).execute(); // So add it to database
        }
        
    }

    public void setFilterGui(FilterGui gui) {
        associatedGui = gui;
    }
}