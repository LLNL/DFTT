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
package llnl.gnem.dftt.core.gui.waveform.factory.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import llnl.gnem.dftt.core.seismicData.AbstractEventInfo;
import llnl.gnem.dftt.core.gui.map.events.EventModel;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentModel;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentViewer.RotateToGcpAction;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.Command;
import llnl.gnem.dftt.core.waveform.components.ComponentSet;

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
