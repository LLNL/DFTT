/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.configuration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;

/**
 *
 * @author dodge1
 */
public class ParamPanel extends JPanel {

    private final GeneralParamsPanel generalParams;
    private final CommonDetectorParamsPanel commonDetectorParams;
    private final SubspaceParamPane subspaceParams;
    private final ArrayCorrelationParamsPanel arrayCorrelationParams;

    public ParamPanel(double clusterThreshold,
            double detectionThreshold,
            double energyCapture,
            double blackoutSeconds,
            DetectorType detectorType,
            int fftSize,
            double staDuration,
            double ltaDuration,
            double gapDuration,
            boolean normalizeStatistics,
            boolean prewhitenStatistics,
            boolean enableSpawning,
            double traceLength,
            boolean fixShiftsToZero,
            String siteTableName,
            String originTableName,
            int minDetectionCount,
            double prePickSeconds,
            double minDetectionStat,
            double maxDetectionStat,
            boolean suppressBadDetectors,
            boolean requireCorrelation, 
            boolean fixSubspaceDimension, 
            int subspaceDimension,
            boolean autoApplyFilter,
            boolean requireWindowPositionConfirmation,
            boolean retrieveByBlocks,
            int blockSize) {

        super(new BorderLayout());
        generalParams = new GeneralParamsPanel(clusterThreshold, detectorType, traceLength, 
                fixShiftsToZero, siteTableName, originTableName, minDetectionCount, 
                prePickSeconds,minDetectionStat, maxDetectionStat, suppressBadDetectors, requireCorrelation, autoApplyFilter, retrieveByBlocks, blockSize);
        commonDetectorParams = new CommonDetectorParamsPanel(detectionThreshold, blackoutSeconds);
        subspaceParams = new SubspaceParamPane(energyCapture, fixSubspaceDimension, subspaceDimension,requireWindowPositionConfirmation);
        arrayCorrelationParams = new ArrayCorrelationParamsPanel(energyCapture, staDuration, ltaDuration, gapDuration);

        add(generalParams, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(800, 300));

        tabbedPane.addTab("Common Detector Params", null, commonDetectorParams, "Parameters that apply to all detectors");
        tabbedPane.addTab("Subspace Params", null, subspaceParams, "Parameters that apply to subspace detectors");

        tabbedPane.addTab("ArrayCorrelation Params", null, arrayCorrelationParams, "Parameters that apply to array correlation detectors");

        add(tabbedPane, BorderLayout.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.blue));

    }

    public String getSiteTableName() {
        return generalParams.getSiteTableName();
    }

    public String getOriginTableName() {
        return generalParams.getOriginTableName();
    }

    public double getClusterThreshold() {
        return generalParams.getClusterThreshold();
    }
    
    public double getMinDetStatThreshold()
    {
        return generalParams.getMinDetStatThreshold();
    }
    
    public double getMaxDetStatThreshold()
    {
        return generalParams.getMaxDetStatThreshold();
    }

    public double getDetectionThreshold() {
        return commonDetectorParams.getDetectionThreshold();
    }

    public double getEnergyCapture() {
        DetectorType type = getDetectorType();
        if (type == DetectorType.SUBSPACE) {
            return subspaceParams.getEnergyCapture();
        } else {
            return arrayCorrelationParams.getEnergyCapture();
        }
    }
    
    public int getSubspaceDimension() {
        DetectorType type = getDetectorType();
        if (type == DetectorType.SUBSPACE) {
            return subspaceParams.getSubspaceDimension();
        } else {
            return -1;
        }
    }
    
    public boolean isFixSubspaceDimension(){
        DetectorType type = getDetectorType();
        if (type == DetectorType.SUBSPACE) {
            return subspaceParams.isFixSubspaceDimension();
        } else {
            return false;
        }
    }

    public double getBlackoutSeconds() {
        return commonDetectorParams.getBlackoutSeconds();
    }

    public DetectorType getDetectorType() {
        return generalParams.getDetectorType();
    }

    public int getMinDetectionCount() {
        return generalParams.getMinDetectionCount();
    }

    public double getStaDuration() {
        return arrayCorrelationParams.getStaDuration();
    }

    public double getLtaDuration() {
        return arrayCorrelationParams.getLtaDuration();
    }

    public double getGapDuration() {
        return arrayCorrelationParams.getGapDuration();
    }

    public double getTraceLength() {
        return generalParams.getTraceLength();
    }

    public boolean isFixShiftsToZero() {
        return generalParams.isFixShiftsToZero();
    }
    
    public boolean isSuppressBadDetectors() {
        return generalParams.isSuppressBadDetectors();
    }
    
    public boolean isRequireCorrelation() {
        return generalParams.isRequireCorrelation();
    }
    
    public boolean isAutoApplyFilter()
    {
        return generalParams.isAutoApplyFilter();
    }
    
    public double getPrePickSeconds()
    {
        return generalParams.getWindowStart();
    }
    
    public boolean isRequireWindowPositionConfirmation()
    {
        return subspaceParams.isRequireWindowPositionConfirmation();
    }
    
    public boolean isRetrieveByBlocks() {
        return generalParams.isRetrieveByBlocks();
    }

    public int getBlockSize() {
        return generalParams.getBlockSize();
    }
}
