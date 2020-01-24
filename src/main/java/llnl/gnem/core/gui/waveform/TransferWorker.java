package llnl.gnem.core.gui.waveform;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.DifferentiateAction;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.IntegrateAction;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.waveform.components.ComponentSet;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataUnits;
import llnl.gnem.core.waveform.responseProcessing.FreqLimits;
import llnl.gnem.core.waveform.responseProcessing.ResponseType;
import llnl.gnem.core.waveform.responseProcessing.TransferFunctionService;
import llnl.gnem.core.waveform.responseProcessing.TransferParams;
import llnl.gnem.core.waveform.responseProcessing.TransferStatus;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class TransferWorker extends SwingWorker<Void, ComponentSet<? extends BaseSingleComponent>> {

    private final ResponseType toType;
    private final Collection<? extends ComponentSet<? extends BaseSingleComponent>> sets;
    private static final double NANOMETER_TO_METER = 1.0e-9;
    private final ThreeComponentModel model;

    public TransferWorker(ResponseType toType, ThreeComponentModel model) {
        this.model = model;
        sets = model.getComponentSets();
        int count = computeComponentCount();
        this.toType = toType;
        ProgressDialog.getInstance().setTitle("Removing Instrument Responses...");
        ProgressDialog.getInstance().setProgressStringPainted(true);
        ProgressDialog.getInstance().setProgressBarIndeterminate(false);
        ProgressDialog.getInstance().setMinMax(0, count);
        ProgressDialog.getInstance().setValue(0);
        ProgressDialog.getInstance().setVisible(true);
    }

    private int computeComponentCount() {
        int count = 0;
        for (ComponentSet componentSet : sets) {
            count += componentSet.getComponentCount();
        }
        return count;
    }

    @Override
    protected Void doInBackground() throws Exception {
        int count = 0;

        for (ComponentSet<? extends BaseSingleComponent> componentSet : sets) {
            for (BaseSingleComponent component : componentSet.getTransferableComponents()) {
                if (component != null) {
                    ProgressDialog.getInstance().setText(String.format("Processing %s...", component.getIdentifier()));
                    transferOneComponent( component);
                    ProgressDialog.getInstance().setValue(++count);
                }
            }
            publish(componentSet);
        }

        return null;
    }

    private void transferOneComponent(BaseSingleComponent component) {
        component.setTransferStatus(TransferStatus.UNTRANSFERRED);
        ProgressDialog.getInstance().setText(String.format("Processing %s...", component.getIdentifier()));
        CssSeismogram backup = component.getTraceData().getBackupSeismogram();
        FreqLimits limits = TransferParams.getInstance().getFreqLimits(backup.getNyquistFreq(), backup.getSegmentLength());
        try {
            boolean result = TransferFunctionService.getInstance().transfer(backup, toType);
            component.setTransferStatus(result ? TransferStatus.TRANSFERRED : TransferStatus.TRANSFER_FAILED);
            if( component.getDataUnits() != WaveformDataUnits.Pa){
                backup.MultiplyScalar(NANOMETER_TO_METER);
            }
            
            if (result) {
                component.setDataUnits(WaveformDataUnits.m);
                component.setDataType(WaveformDataType.displacement);
                component.getTraceData().unApplyFilter();
            }
        } catch (Exception e) {
            component.setTransferStatus(TransferStatus.TRANSFER_FAILED);
            ApplicationLogger.getInstance().log(Level.WARNING, String.format("Transfer failed for component: %s", component), e);
        }
    }

    @Override
    protected void process(List<ComponentSet<? extends BaseSingleComponent>> sets) {
        for (ComponentSet set : sets) {
            model.updateViewsForTransferredComponentSet(set);
        }
    }

    @Override
    public void done() {
        ProgressDialog.getInstance().setVisible(false);
        model.updateViewAction(IntegrateAction.class, model.canIntegrate());
        model.updateViewAction(DifferentiateAction.class, model.canDifferentiate());
        WaveformViewerFactoryHolder.getInstance().getThreeComponentModel().finishRetrieval();
        try {
            get();
        } catch (Exception e) {
            ExceptionDialog.displayError(e);
        }
    }
}