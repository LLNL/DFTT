package llnl.gnem.apps.detection.sdBuilder.actions;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ComputeCorrelationsWorker;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1
 * Date: Mar 22, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class ComputeCorrelationsAction extends AbstractAction {
    private static ComputeCorrelationsAction ourInstance;
    private static final long serialVersionUID = 6163681618566924860L;

    public static ComputeCorrelationsAction getInstance(Object owner)
    {
        if( ourInstance == null )
            ourInstance = new ComputeCorrelationsAction(owner);
        return ourInstance;
    }

    private ComputeCorrelationsAction(Object owner)
    {
        super( "Compute", Utility.getIcon(owner,"miscIcons/process32.gif") );
        putValue(SHORT_DESCRIPTION, "Compute Correlations");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C );
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        ComputeCorrelationsWorker worker = new ComputeCorrelationsWorker(ParameterModel.getInstance().isFixShiftsToZero());
        worker.execute();
    }

}