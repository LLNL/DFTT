/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.factory.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.core.util.Command;
import llnl.gnem.core.waveform.components.ComponentSet;
import llnl.gnem.core.gui.waveform.ThreeComponentModel;

/**
 * User: Doug Date: Feb 14, 2012 Time: 7:33:55 AM
 *
 */
public class RemoveTrendCommand implements Command {

    private final Map<ComponentSet, ComponentSet> backupCollection;
    private final ThreeComponentModel model;

    public RemoveTrendCommand(ThreeComponentModel model) throws Exception {
        this.model = model;
        backupCollection = new HashMap<ComponentSet, ComponentSet>();
        Collection<? extends ComponentSet> sets = model.getComponentSets();

        for (ComponentSet set : sets) {
            ComponentSet backup = new ComponentSet(set);
            backupCollection.put(set, backup);
        }
    }

    @Override
    public boolean execute() {
        model.removeTrend();
        model.notifyViewsDataWasChanged();
        return true;
    }

    @Override
    public boolean unexecute() {
        Collection<? extends ComponentSet> sets = model.getComponentSets();
        for (ComponentSet set : sets) {
            ComponentSet backup = backupCollection.get(set);
            if (backup != null) {
                set.replaceContentsFromBackup(backup);
            }
        }
        model.notifyViewsDataWasChanged();
        return true;
    }

    @Override
    public boolean isAllowable() {
        return true;
    }

    @Override
    public boolean isReversible() {
        return true;
    }

    @Override
    public boolean isRunInNewThread() {
        return false;
    }
}