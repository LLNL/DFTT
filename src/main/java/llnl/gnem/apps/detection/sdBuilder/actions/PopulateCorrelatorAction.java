package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.apps.detection.sdBuilder.SegmentRetrievalWorker;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.source.SourceData;
import llnl.gnem.apps.detection.util.FrameworkRun;
import llnl.gnem.apps.detection.util.SourceDataHolder;
import llnl.gnem.core.gui.util.ExceptionDialog;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class PopulateCorrelatorAction extends AbstractAction {

    private static PopulateCorrelatorAction ourInstance;
    private static final long serialVersionUID = 6142946342608559329L;
    private int runid;
    private int detectorid;
    private FrameworkRun runInfo;

    public static PopulateCorrelatorAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new PopulateCorrelatorAction(owner);
        }
        return ourInstance;
    }
    

    private PopulateCorrelatorAction(Object owner) {
        super("Display", Utility.getIcon(owner, "miscIcons/showAll.gif"));
        putValue(SHORT_DESCRIPTION, "Display detection segments");
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
    }
    
    public void setRunid( int runid )
    {
        this.runid = runid;
    }
    
    public void setDetectorid( int detectorid )
    {
        this.detectorid = detectorid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (runInfo != null && runid > 0 && detectorid > 0) {
            try {
                SourceData source = SourceDataHolder.getInstance().getSourceData(runid);
               
                double duration = ParameterModel.getInstance().getTraceLength();
               
                CorrelatedTracesModel.getInstance().setDetectorid(detectorid);
                 SegmentRetrievalWorker worker = new SegmentRetrievalWorker(runid,
                        detectorid, source, ParameterModel.getInstance().getPrepickSeconds(), duration, runInfo);
                worker.execute();
            } catch (Exception ex) {
                ExceptionDialog.displayError(ex);
            }
        }

    }

    public void setRunInfo(FrameworkRun runInfo) {
        this.runInfo = runInfo;
    }
}