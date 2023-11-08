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
package llnl.gnem.dftt.core.gui.waveform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentViewer.ApplyTaperAction;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentViewer.DifferentiateAction;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentViewer.IntegrateAction;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentViewer.RemoveTrendAction;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentViewer.RotateToGcpAction;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentViewer.TransferAction;
import llnl.gnem.dftt.core.gui.waveform.factory.actions.OpenFilterDialogAction;
import llnl.gnem.dftt.core.gui.waveform.factory.actions.SaveAction;
import llnl.gnem.dftt.core.gui.waveform.factory.commands.ApplyTaperCommand;
import llnl.gnem.dftt.core.gui.waveform.factory.commands.RemoveTrendCommand;
import llnl.gnem.dftt.core.gui.waveform.filterBank.FilterBankModel;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.ButtonAction;
import llnl.gnem.dftt.core.util.Command;
import llnl.gnem.dftt.core.util.CommandManager;
import llnl.gnem.dftt.core.waveform.components.ComponentSet;
import llnl.gnem.dftt.core.waveform.filter.FilterClient;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;

public class ThreeComponentModel<T extends ComponentSet, S extends Base3CWaveformDataView> extends WaveformDataModel<S> implements FilterClient {

    private final Collection<T> componentSets;

    private BasePickingStateManager pickManager;
    private final Preferences prefs;
    private boolean removeTrendOnInput;
    private final Collection<T> singleChannelComponentSets;
    private boolean taperOnInput;

    public ThreeComponentModel(BasePickingStateManager pickManager) {
        this.pickManager = pickManager;
        componentSets = new ArrayList<>();
        singleChannelComponentSets = new ArrayList<>();
        prefs = Preferences.userNodeForPackage(this.getClass());

        taperOnInput = prefs.getBoolean("APPLY_TAPER_ON_INPUT", false);
        removeTrendOnInput = prefs.getBoolean("REMOVE_TREND_ON_INPUT", false);
    }

    public void addComponentSet(T componentSet) {
        if (componentSet.getComponentCount() > 1) {
            componentSets.add(componentSet);
            notifyViewsComponentSetAdded(componentSet);
            if (componentSet.canRotateComponents()) {
                for (Base3CWaveformDataView view : getViews()) {
                    view.getButtonAction(RotateToGcpAction.class).setEnabled(componentSet.canRotateComponents());
                }
            }
        } else {
            singleChannelComponentSets.add(componentSet);
            notifyViewsComponentSetAdded(componentSet);
        }
    }

    public void addComponentSets(Collection<? extends T> components) {
        for (T set : components) {
            addComponentSet(set);
        }
    }

    @Override
    public void applyFilter(StoredFilter filter) {
        for (T set : componentSets) {
            set.applyFilter(filter);
        }

        for (T set : singleChannelComponentSets) {
            set.applyFilter(filter);
        }
        notifyViewsTracesFiltered();
    }

    public boolean canApplyTaper() {
        return !componentSets.isEmpty() || !singleChannelComponentSets.isEmpty();
    }

    public boolean canDifferentiate() {
        for (T set : componentSets) {
            if (set.canDifferentiate()) {
                return true;
            }
        }
        for (T set : singleChannelComponentSets) {
            if (set.canDifferentiate()) {
                return true;
            }
        }
        return false;
    }

    public boolean canIntegrate() {
        for (T set : componentSets) {
            if (set.canIntegrate()) {
                return true;
            }
        }
        for (T set : singleChannelComponentSets) {
            if (set.canIntegrate()) {
                return true;
            }
        }
        return false;
    }

    public boolean canRemoveInstrumentResponse() {
        for (T set : componentSets) {
            if (set.canRemoveInstrumentResponse()) {
                return true;
            }
        }
        for (T set : singleChannelComponentSets) {
            if (set.canRemoveInstrumentResponse()) {
                return true;
            }
        }
        return false;
    }

    public boolean canRemoveTrend() {
        return !componentSets.isEmpty() || !singleChannelComponentSets.isEmpty();
    }

    public boolean canRotateComponents() {
        for (T set : componentSets) {
            if (set.canRotateComponents()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void clear() {
        componentSets.clear();
        singleChannelComponentSets.clear();
        FilterBankModel.getInstance().clear();
        notifyViewsModelCleared();

        for (S view : getViews()) {
            view.getButtonAction(RotateToGcpAction.class).setEnabled(false);
            view.getButtonAction(TransferAction.class).setEnabled(false);
            view.getButtonAction(IntegrateAction.class).setEnabled(false);
            view.getButtonAction(DifferentiateAction.class).setEnabled(false);
        }
    }

    public void differentiate() {
        for (T set : componentSets) {
            set.differentiate();
        }
        for (T set : singleChannelComponentSets) {
            set.differentiate();
        }
    }

    public void doAfterSave() {
        notifyViewsAfterSave();
    }

    public void doBeforeRetrieval() {
        notifyViewsDisabled();
    }

    public void doBeforeSave() {
        notifyViewsBeforeSave();
    }

    public void finishRetrieval() {
        notifyViewsEnabled();
    }

    public Collection<? extends T> getAllComponentSets() {
        Collection<T> result = new ArrayList<>();
        result.addAll(componentSets);
        result.addAll(singleChannelComponentSets);
        return result;
    }

    public Collection<T> getComponentSets() {
        return new ArrayList<>(componentSets);
    }

    public BasePickingStateManager getPickManager() {
        return pickManager;
    }

    public void setPickingStateManager(BasePickingStateManager ppsm) {
        pickManager = ppsm;
    }

    public void integrate() {
        for (T set : componentSets) {
            set.integrate();
        }
        for (T set : singleChannelComponentSets) {
            set.integrate();
        }
    }

    public boolean isRemoveTrendOnInput() {
        return removeTrendOnInput;
    }

    public void setRemoveTrendOnInput(boolean removeTrendOnInput) {
        this.removeTrendOnInput = removeTrendOnInput;
        prefs.putBoolean("REMOVE_TREND_ON_INPUT", removeTrendOnInput);

    }

    public boolean isTaperOnInput() {
        return taperOnInput;
    }

    public void setTaperOnInput(boolean taperOnInput) {
        this.taperOnInput = taperOnInput;
        prefs.putBoolean("APPLY_TAPER_ON_INPUT", taperOnInput);
    }

    public void notifyViewsDataWasChanged() {
        for (Base3CWaveformDataView view : getViews()) {
            view.updateForChangedData();
        }
    }

    public void notifyViewsRotationWasUndone(Collection<ComponentSet> rotatable) {
        for (Base3CWaveformDataView view : getViews()) {
            view.updateForComponentRotation(rotatable);
        }
    }

    public void notifyViewsTransferWasUndone() {
        for (Base3CWaveformDataView view : getViews()) {
            view.updateForUndoneTransferOperation();
        }
    }

    public void removeTrend() {
        for (T set : componentSets) {
            set.removeTrend();
        }
        for (T set : singleChannelComponentSets) {
            set.removeTrend();
        }
    }

    @Override
    public void retrievalIsComplete() {
        for (T set : singleChannelComponentSets) {
            componentSets.add(set);
            notifyViewsComponentSetAdded(set);
        }
        notifyViewsComponentSetsCompleted();

        for (Base3CWaveformDataView view : getViews()) {
            view.getButtonAction(TransferAction.class).setEnabled(canRemoveInstrumentResponse());
            view.getButtonAction(IntegrateAction.class).setEnabled(canIntegrate());
            view.getButtonAction(DifferentiateAction.class).setEnabled(canDifferentiate());
        }
        singleChannelComponentSets.clear();
        if (removeTrendOnInput) {
            try {
                Command cmd = new RemoveTrendCommand(this);
                CommandManager.runCommand(cmd);
                notifyViewsCommandInvoked(cmd);
            } catch (Exception ex) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Failed to create RemoveTrendCommand!", ex);
            }
        }
        if (taperOnInput) {
            try {
                Command cmd = new ApplyTaperCommand(this);
                CommandManager.runCommand(cmd);
                notifyViewsCommandInvoked(cmd);
            } catch (Exception ex) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Failed to create ApplyTaperCommand!", ex);
            }
        }

        SaveAction.getInstance(this).setEnabled(false);
    }

    public void taper(double taperPercent) {
        for (T set : componentSets) {
            set.applyTaper(taperPercent);
        }
        for (T set : singleChannelComponentSets) {
            set.applyTaper(taperPercent);
        }
    }

    @Override
    public void unApplyFilter() {
        for (T set : componentSets) {
            set.unApplyFilter();
        }

        for (T set : singleChannelComponentSets) {
            set.unApplyFilter();
        }
        notifyViewsTracesFiltered();
    }

    public void updateViewAction(Class<? extends ButtonAction> className, boolean enabled) {
        for (Base3CWaveformDataView view : getViews()) {
            view.getButtonAction(className).setEnabled(enabled);
        }
    }

    public void updateViewActions() {
        for (Base3CWaveformDataView view : getViews()) {
            view.getButtonAction(ApplyTaperAction.class).setEnabled(canApplyTaper());
            view.getButtonAction(RemoveTrendAction.class).setEnabled(canRemoveTrend());
            view.getButtonAction(RotateToGcpAction.class).setEnabled(canRotateComponents());
            view.getButtonAction(TransferAction.class).setEnabled(canRemoveInstrumentResponse());
            view.getButtonAction(IntegrateAction.class).setEnabled(canIntegrate());
            view.getButtonAction(DifferentiateAction.class).setEnabled(canDifferentiate());
        }

        SaveAction.getInstance(this).setEnabled(isAnyDirtyData());
        OpenFilterDialogAction.getInstance(this).setEnabled(true);
    }

    public void updateViewsForTransferredComponentSet(T set) {
        for (Base3CWaveformDataView view : getViews()) {
            view.updateForTransferredComponentSet(set);
        }
    }

    private void notifyViewsCommandInvoked(Command command) {
        for (Base3CWaveformDataView view : getViews()) {
            view.pushCommand(command);
        }
    }

    private void notifyViewsComponentSetAdded(T set) {
        for (Base3CWaveformDataView view : getViews()) {
            view.addComponentSet(set);
        }
    }

    private void notifyViewsComponentSetsCompleted() {
        for (Base3CWaveformDataView view : getViews()) {
            view.componentSetsCompleted();
        }
    }

    public void notifyViewsComponentsRotated(Collection<ComponentSet> rotatable) {
        for (Base3CWaveformDataView view : getViews()) {
            view.updateForComponentRotation(rotatable);
        }
    }

    private void notifyViewsModelCleared() {
        for (Base3CWaveformDataView view : getViews()) {
            view.clearComponentDisplays();
        }
        if (pickManager != null) {
            pickManager.removePlots();
        }
    }

    private void notifyViewsTracesFiltered() {
        for (Base3CWaveformDataView view : getViews()) {
            view.updateForFilterOperation();
        }
    }

    protected Collection<T> getSingleComponentSets() {
        return singleChannelComponentSets;
    }

    protected void notifyViewsAfterSave() {
        for (Base3CWaveformDataView view : getViews()) {
            view.saveCompleted();
        }
    }

    protected void notifyViewsBeforeSave() {
        for (Base3CWaveformDataView view : getViews()) {
            view.saveStarted();
        }
    }

    protected void notifyViewsDisabled() {
        for (Base3CWaveformDataView view : getViews()) {
            view.setUsable(false);
        }
    }

    protected void notifyViewsEnabled() {
        for (Base3CWaveformDataView view : getViews()) {
            view.setUsable(true);
        }
    }

    boolean isAnyDirtyData() {
        for (T set : componentSets) {
            if (set.isSaveRequired()) {
                return true;
            }
        }
        return false;
    }
}
