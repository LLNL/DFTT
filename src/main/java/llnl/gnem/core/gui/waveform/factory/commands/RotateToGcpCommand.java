package llnl.gnem.core.gui.waveform.factory.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import llnl.gnem.core.seismicData.AbstractEventInfo;
import llnl.gnem.core.gui.map.events.EventModel;
import llnl.gnem.core.gui.waveform.ThreeComponentModel;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.RotateToGcpAction;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Command;
import llnl.gnem.core.waveform.components.ComponentSet;

/**
 * Created by dodge1 Date: Mar 24, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class RotateToGcpCommand implements Command {

    private final Map<ComponentSet, ComponentSet> backupCollection;
    private final ThreeComponentModel model;
    private final Collection<ComponentSet> rotatable;

    public RotateToGcpCommand(ThreeComponentModel model) {
        this.model = model;
        backupCollection = new HashMap<>();
        rotatable = new ArrayList<>();
        Collection<? extends ComponentSet> sets = model.getComponentSets();
        for (ComponentSet set : sets) {
            if (set.canRotateComponents()) {
                rotatable.add(set);
                ComponentSet backup = new ComponentSet(set);
                backupCollection.put(set, backup);
            }
        }
    }

    @Override
    public boolean execute() {
        EventModel<? extends AbstractEventInfo> eventModel = model.getEventModel();
        AbstractEventInfo info = eventModel.getCurrent();
        for (ComponentSet set : rotatable) {
            if (set.canRotateComponents()) {
                try {
                    set.rotateToGCP(info);
                } catch (Exception e) {
                    ApplicationLogger.getInstance().log(Level.WARNING, String.format("Could not rotate components for %s", set), e);

                }
            }
        }
        model.notifyViewsComponentsRotated(rotatable);
                
        model.updateViewAction(RotateToGcpAction.class, false);
        return true;
    }

    @Override
    public boolean unexecute() {
        Collection<? extends ComponentSet> sets = model.getComponentSets();

        boolean someComponentCanBeRotated = false;
        for (ComponentSet set : rotatable) {
            ComponentSet backup = backupCollection.get(set);
            if (backup != null) {
                set.replaceContentsFromBackup(backup);
            }
            if (set.canRotateComponents()) {
                someComponentCanBeRotated = true;
            }
        }
        model.notifyViewsRotationWasUndone(rotatable);
        model.updateViewAction(RotateToGcpAction.class, someComponentCanBeRotated);
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
