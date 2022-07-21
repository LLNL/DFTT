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
package llnl.gnem.apps.detection.sdBuilder.configuration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;

/**
 *
 * @author dodge1
 */
public class ParamPanel extends JPanel {

    private static final long serialVersionUID = 1904024300665553940L;

    private final GeneralParamsPanel generalParams;
    private final CommonDetectorParamsPanel commonDetectorParams;
    private final SubspaceParamPane subspaceParams;
    private final ArrayCorrelationParamsPanel arrayCorrelationParams;
    private final PickingParamPane pickingParams;
    private final FKParamsPanel fkParams;
    private final WindowSelectionParamPane refineWindowParams;

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
            String originTableName,
            int minDetectionCount,
            double prePickSeconds,
            double minDetectionStat,
            double maxDetectionStat,
            boolean suppressBadDetectors,
            boolean requireCorrelation,
            boolean fixSubspaceDimension,
            boolean capSubspaceDimension,
            int subspaceDimension,
            boolean autoApplyFilter,
            boolean requireWindowPositionConfirmation,
            boolean retrieveByBlocks,
            int blockSize,
            boolean displayNewTemplates,
            DetectorCreationOption detectorCreationOption,
            boolean displayPredictedPicks,
            boolean displayEventIDLabels,
            boolean displayDetectionLabels,
            double maxSlowness,
            int numSlowness,
            double minFrequency,
            double maxFrequency,
            double rWFloorFactor,
            boolean rWRefineWindow,
            double rWSnrThreshold,
            double rWAnalysisWindowLength,
            double rWMinimumWindowLength) {

        super(new BorderLayout());
        generalParams = new GeneralParamsPanel(clusterThreshold, detectorType, traceLength,
                fixShiftsToZero, originTableName, minDetectionCount,
                prePickSeconds, minDetectionStat, maxDetectionStat, suppressBadDetectors, requireCorrelation, autoApplyFilter, retrieveByBlocks, blockSize);
        commonDetectorParams = new CommonDetectorParamsPanel(detectionThreshold, blackoutSeconds);
        subspaceParams = new SubspaceParamPane(energyCapture, fixSubspaceDimension, capSubspaceDimension,
                subspaceDimension, requireWindowPositionConfirmation, displayNewTemplates, detectorCreationOption);
        arrayCorrelationParams = new ArrayCorrelationParamsPanel(energyCapture, staDuration, ltaDuration, gapDuration);

        pickingParams = new PickingParamPane(displayPredictedPicks, displayEventIDLabels, displayDetectionLabels);

        fkParams = new FKParamsPanel(maxSlowness, numSlowness, minFrequency, maxFrequency);
        
       refineWindowParams =  new WindowSelectionParamPane( rWFloorFactor, rWRefineWindow, rWSnrThreshold, rWAnalysisWindowLength, rWMinimumWindowLength);

        add(generalParams, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(800, 300));

        tabbedPane.addTab("Common Detector Params", null, commonDetectorParams, "Parameters that apply to all detectors");
        tabbedPane.addTab("Subspace Params", null, subspaceParams, "Parameters that apply to subspace detectors");

        tabbedPane.addTab("ArrayCorrelation Params", null, arrayCorrelationParams, "Parameters that apply to array correlation detectors");
        tabbedPane.addTab("Picking Params", null, pickingParams, "Parameters that apply to picking");
        tabbedPane.addTab("FK Params", null, fkParams, "Parameters controlling FK analysis");
        tabbedPane.addTab("Refine Window Params", null, refineWindowParams, "Parameters controlling window refinement");

        add(tabbedPane, BorderLayout.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.blue));

    }

    public String getOriginTableName() {
        return generalParams.getOriginTableName();
    }

    public double getClusterThreshold() {
        return generalParams.getClusterThreshold();
    }

    public double getMinDetStatThreshold() {
        return generalParams.getMinDetStatThreshold();
    }

    public double getMaxDetStatThreshold() {
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

    public boolean isFixSubspaceDimension() {
        DetectorType type = getDetectorType();
        if (type == DetectorType.SUBSPACE) {
            return subspaceParams.isFixSubspaceDimension();
        } else {
            return false;
        }
    }

    public boolean isCapSubspaceDimension() {
        DetectorType type = getDetectorType();
        if (type == DetectorType.SUBSPACE) {
            return subspaceParams.isCapSubspaceDimension();
        } else {
            return false;
        }
    }

    public boolean isDisplayNewTemplates() {
        DetectorType type = getDetectorType();
        if (type == DetectorType.SUBSPACE) {
            return subspaceParams.isDisplayNewTemplates();
        } else {
            return false;
        }
    }

    public DetectorCreationOption getDetectorCreationOption() {
        return subspaceParams.getDetectorCreationOption();
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

    public boolean isAutoApplyFilter() {
        return generalParams.isAutoApplyFilter();
    }

    public double getPrePickSeconds() {
        return generalParams.getWindowStart();
    }

    public boolean isRequireWindowPositionConfirmation() {
        return subspaceParams.isRequireWindowPositionConfirmation();
    }

    public boolean isRetrieveByBlocks() {
        return generalParams.isRetrieveByBlocks();
    }

    public int getBlockSize() {
        return generalParams.getBlockSize();
    }

    public boolean isDisplayPredictedPicks() {
        return pickingParams.isDisplayPredictedPicks();
    }

    public boolean isDisplayEventIDLabels() {
        return pickingParams.isDisplayEventIDLabels();
    }

    public boolean isDisplayDetectionLabels() {
        return pickingParams.isDisplayDetectionLabels();
    }

    public double getMaxSlowness() {
        return fkParams.getMaxSlowness();
    }

    public int getNumSlowness() {
        return fkParams.getNumSlowness();
    }

    public double getMinFrequency() {
        return fkParams.getMinFrequency();
    }

    public double getMaxFrequency() {
        return fkParams.getMaxFrequency();
    }

 
    public double getRWFloorFactor() {
        return refineWindowParams.getFloorFactor();
    }

    public double getRWSNRThreshold() {
        return refineWindowParams.getSNRThreshold();
    }

    public boolean isRefineWindow() {
        return refineWindowParams.isRefineWindow();
    }

    public double getRWAnalysisWindowLength() {
        return refineWindowParams.getAnalysisWindowLength();
    }

    public double getRWMinimumWindowLength() {
        return refineWindowParams.getMinimumWindowLength();
    }
   
    
}
