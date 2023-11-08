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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.dftt.core.util.Command;
import llnl.gnem.dftt.core.waveform.components.ComponentSet;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentModel;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentViewer;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentViewer.DifferentiateAction;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentViewer.IntegrateAction;

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