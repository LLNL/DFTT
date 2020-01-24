package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import llnl.gnem.apps.detection.database.TableNames;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParamPanel;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class OpenParamsDlgAction extends AbstractAction {
    
    private static OpenParamsDlgAction ourInstance;
    private static final long serialVersionUID = -4175581073357966469L;
    
    public static OpenParamsDlgAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new OpenParamsDlgAction(owner);
        }
        return ourInstance;
    }
    
    private OpenParamsDlgAction(Object owner) {
        super("Params", Utility.getIcon(owner, "miscIcons/editDlg32.gif"));
        putValue(SHORT_DESCRIPTION, "Open Params Dialog");
        putValue(MNEMONIC_KEY, KeyEvent.VK_P);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        ParameterModel mod = ParameterModel.getInstance();
        ParamPanel panel = new ParamPanel(mod.getCorrelationThreshold(),
                mod.getDetectionThreshold(),
                mod.getEnergyCapture(),
                mod.getBlackoutSeconds(),
                mod.getDetectorType(),
                mod.getFFTSize(),
                mod.getStaDuration(),
                mod.getLtaDuration(),
                mod.getGapDuration(),
                mod.isNormalizeStatistics(),
                mod.isPrewhitenStatistics(),
                mod.isEnableSpawning(),
                mod.getTraceLength(),
                mod.isFixShiftsToZero(),
                TableNames.getInstance().getSiteTableName(),
                TableNames.getInstance().getOriginTableName(),
                mod.getMinDetectionCountForRetrieval(),
                mod.getPrepickSeconds(),
                mod.getMinDetStatThreshold(),
                mod.getMaxDetStatThreshold(),
                mod.isSuppressBadDetectors(),
                mod.isRequireCorrelation(),
                mod.isFixSubspaceDimension(),
                mod.getSubspaceDimension(),
                mod.isAutoApplyFilter(),
                mod.isRequireWindowPositionConfirmation(), 
                mod.isRetrieveByBlocks(), 
                mod.getBlockSize());
        
        Object[] options2 = {"Accept Changes", "Cancel"};
        int answer = JOptionPane.showOptionDialog(ClusterBuilderFrame.getInstance(),
                panel,
                String.format("Set Parameters"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, //do not use a custom Icon
                options2, //the titles of buttons
                options2[0]); //default button title

        if (answer == JOptionPane.YES_OPTION) {
            
            mod.setCorrelationThreshold(panel.getClusterThreshold());
            mod.setDetectionThreshold(panel.getDetectionThreshold());
            mod.setEnergyCapture(panel.getEnergyCapture());
            mod.setBlackoutSeconds(panel.getBlackoutSeconds());
            mod.setDetectorType(panel.getDetectorType());
            mod.setStaDuration(panel.getStaDuration());
            mod.setLtaDuration(panel.getLtaDuration());
            mod.setGapDuration(panel.getGapDuration());
            mod.setTraceLength(panel.getTraceLength());
            mod.setFixShiftsToZero(panel.isFixShiftsToZero());
            mod.setMinDetectionCountForRetrieval(panel.getMinDetectionCount());
            TableNames.getInstance().setSiteTableName(panel.getSiteTableName());
            TableNames.getInstance().setOriginTableName(panel.getOriginTableName());
            mod.setPrepickSeconds(panel.getPrePickSeconds());
            mod.setMinDetStatThreshold(panel.getMinDetStatThreshold());
            mod.setMaxDetStatThreshold(panel.getMaxDetStatThreshold());
            mod.setSuppressBadDetectors(panel.isSuppressBadDetectors());
            mod.setRequireCorrelation(panel.isRequireCorrelation());
            mod.setFixSubspaceDimension(panel.isFixSubspaceDimension());
            mod.setSubspaceDimension(panel.getSubspaceDimension());
            mod.setAutoApplyFilter(panel.isAutoApplyFilter());
            mod.setRequireWindowPositionConfirmation(panel.isRequireWindowPositionConfirmation());
            mod.setRetrieveByBlocks(panel.isRetrieveByBlocks());
            mod.setBlockSize(panel.getBlockSize());
            
        }
        
    }
}
