package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.GetConfigurationsWorker;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.TemplateModel;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class RefreshConfigurationsAction extends AbstractAction {

    private static RefreshConfigurationsAction ourInstance;

    public static RefreshConfigurationsAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new RefreshConfigurationsAction(owner);
        }
        return ourInstance;
    }

    private RefreshConfigurationsAction(Object owner) {
        super("Refresh", Utility.getIcon(owner, "miscIcons/Refresh24.gif"));
        putValue(SHORT_DESCRIPTION, "Reload all configuration information.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ClusterBuilderFrame.getInstance().clearTree();
        CorrelatedTracesModel.getInstance().clear();
        TemplateModel.getInstance().clear();
        new GetConfigurationsWorker().execute();
    }

}
