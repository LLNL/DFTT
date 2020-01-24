package llnl.gnem.core.gui.waveform.factory.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.core.util.Command;
import llnl.gnem.core.waveform.components.ComponentSet;
import llnl.gnem.core.gui.waveform.TransferWorker;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.gui.waveform.ThreeComponentModel;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.DifferentiateAction;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.IntegrateAction;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.TransferAction;
import llnl.gnem.core.waveform.responseProcessing.ResponseType;

/**
 * Created by dodge1 Date: Mar 24, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class TransferCommand implements Command {

    private final Map<ComponentSet, ComponentSet> backupCollection;
    private final ThreeComponentModel model;

    public TransferCommand(ThreeComponentModel dataModel) {
        model = dataModel;
        backupCollection = new HashMap<ComponentSet, ComponentSet>();
        Collection<? extends ComponentSet> sets = model.getComponentSets();
        for (ComponentSet set : sets) {
            ComponentSet backup = new ComponentSet(set);
            backupCollection.put(set, backup);
        }
    }

    @Override
    public boolean execute() {
        WaveformViewerFactoryHolder.getInstance().getThreeComponentModel().doBeforeRetrieval();
        model.updateViewAction(TransferAction.class, false);
        TransferWorker worker = new TransferWorker(ResponseType.DIS, model);
        worker.execute();
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
        model.notifyViewsTransferWasUndone();
        model.updateViewAction(TransferAction.class, true);
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