package llnl.gnem.core.gui.waveform.factory.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.core.util.Command;
import llnl.gnem.core.waveform.components.ComponentSet;
import llnl.gnem.core.gui.waveform.ThreeComponentModel;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.DifferentiateAction;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.IntegrateAction;

/**
 * Created by dodge1 Date: Mar 24, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DifferentiateCommand implements Command {
    private final Map<ComponentSet, ComponentSet> backupCollection;
    private final ThreeComponentModel model;

    public DifferentiateCommand(ThreeComponentModel model) {
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
        model.differentiate();
        model.updateViewAction(IntegrateAction.class, model.canIntegrate());
        model.updateViewAction(DifferentiateAction.class, model.canDifferentiate());
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
        model.updateViewAction(IntegrateAction.class, model.canIntegrate());
        model.updateViewAction(DifferentiateAction.class, model.canDifferentiate());
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