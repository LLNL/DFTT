package llnl.gnem.apps.detection.sdBuilder.arrayDisplay.actions;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import llnl.gnem.apps.detection.core.signalProcessing.FKMeasurement;
import llnl.gnem.apps.detection.database.DetectionDAO;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ComputeCorrelationsWorker;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.ArrayDisplayModel;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1
 * Date: Mar 22, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class MakeFKMeasurementAction extends AbstractAction {
    private static MakeFKMeasurementAction ourInstance;

    public static MakeFKMeasurementAction getInstance(Object owner)
    {
        if( ourInstance == null )
            ourInstance = new MakeFKMeasurementAction(owner);
        return ourInstance;
    }

    private MakeFKMeasurementAction(Object owner)
    {
        super( "FK", Utility.getIcon(owner,"miscIcons/fkStack.gif") );
        putValue(SHORT_DESCRIPTION, "Compute FK statistics");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C );
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try {
            FKMeasurement meas = ArrayDisplayModel.getInstance().computeFKStatistic();
            System.out.println(meas);
            int detectionid = ArrayDisplayModel.getInstance().getDetectionID();
            double time = ArrayDisplayModel.getInstance().getWindowStart();
            double duration = ArrayDisplayModel.getInstance().getWindowDuration();
            DetectionDAO.getInstance().writeDetectionFKStats(detectionid, time, duration, meas);
        } catch (SQLException ex) {
            Logger.getLogger(MakeFKMeasurementAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}