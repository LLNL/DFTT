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

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.dftt.core.correlation.clustering.ClusterType;
import llnl.gnem.dftt.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge1
 */
public class GeneralParamsPanel extends JPanel {

    private static final long serialVersionUID = 4510467557517001233L;

    private final JFormattedTextField clusterThresholdField;

    private final JFormattedTextField traceLengthField;
    private final JComboBox detectorTypeCombo;

    private final JCheckBox fixShiftsChk;
    private final JCheckBox suppressBadDetectorsChk;
    private final JCheckBox requireCorrelationChk;

    private final JTextField originTableField;
    private final JFormattedTextField minDetectionCountField;
    private final JFormattedTextField prePickSecondsField;
    private final JFormattedTextField minDetStatField;
    private final JFormattedTextField maxDetStatField;
    private final JCheckBox autoApplyFilterChk;
    private final JCheckBox retrieveByBlocksChk;
    private final JFormattedTextField blockSizeField;

    private final JComboBox clusterTypeCombo;
    private final JFormattedTextField numGroupsField;

    public GeneralParamsPanel(double clusterThreshold,
            DetectorType detectorType,
            double traceLength,
            boolean fixShiftsToZero,
            String originTableName,
            int minDetectionCount,
            double windowStart,
            double minDetectionStat,
            double maxDetectionStat,
            boolean suppressBadDetectors,
            boolean requireCorrelation,
            boolean autoApplyFilter,
            boolean retrieveByBlocks,
            int blockSize,
            ClusterType clusterType,
            int numGroups) {

        super(new SpringLayout());

        int row = 0;
        JLabel label = new JLabel("ORIGIN Table Name", JLabel.TRAILING);
        add(label);
        originTableField = new JTextField(originTableName);
        label.setLabelFor(originTableField);
        add(originTableField);
        ++row;

        label = new JLabel("Pre-Detection Seconds", JLabel.TRAILING);
        add(label);
        prePickSecondsField = new JFormattedTextField(windowStart);
        prePickSecondsField.setColumns(10);
        add(prePickSecondsField);
        label.setLabelFor(prePickSecondsField);
        ++row;

        label = new JLabel("Retrieved Trace Length", JLabel.TRAILING);
        add(label);
        traceLengthField = new JFormattedTextField(traceLength);
        traceLengthField.setColumns(10);
        add(traceLengthField);
        label.setLabelFor(traceLengthField);
        ++row;

        label = new JLabel("Min Detection STAT", JLabel.TRAILING);
        add(label);
        minDetStatField = new JFormattedTextField(minDetectionStat);
        minDetStatField.setColumns(10);
        add(minDetStatField);
        label.setLabelFor(minDetStatField);
        ++row;

        label = new JLabel("Max Detection STAT", JLabel.TRAILING);
        add(label);
        maxDetStatField = new JFormattedTextField(maxDetectionStat);
        maxDetStatField.setColumns(10);
        add(maxDetStatField);
        label.setLabelFor(maxDetStatField);
        ++row;

        label = new JLabel("Force Zero Shifts", JLabel.TRAILING);
        add(label);
        fixShiftsChk = new JCheckBox("", fixShiftsToZero);
        label.setLabelFor(fixShiftsChk);
        fixShiftsChk.setSelected(fixShiftsToZero);
        add(fixShiftsChk);
        ++row;

        label = new JLabel("Cluster Threshold", JLabel.TRAILING);
        add(label);
        clusterThresholdField = new JFormattedTextField(clusterThreshold);
        clusterThresholdField.setColumns(10);
        add(clusterThresholdField);
        label.setLabelFor(clusterThresholdField);
        ++row;

        label = new JLabel("Detector Type", JLabel.TRAILING);
        add(label);
        DetectorType[] types = {DetectorType.SUBSPACE, DetectorType.ARRAY_CORRELATION,};
        detectorTypeCombo = new JComboBox(types);
        add(detectorTypeCombo);
        label.setLabelFor(detectorTypeCombo);
        detectorTypeCombo.setSelectedItem(detectorType);
        ++row;

        label = new JLabel("Cluster Type", JLabel.TRAILING);
        add(label);
        ClusterType[] ctypes = ClusterType.values();
        clusterTypeCombo = new JComboBox(ctypes);
        add(clusterTypeCombo);
        label.setLabelFor(clusterTypeCombo);
        clusterTypeCombo.setSelectedItem(clusterType);
        ++row;

        label = new JLabel("Cluster Size", JLabel.TRAILING);
        add(label);
        numGroupsField = new JFormattedTextField(numGroups);
        numGroupsField.setColumns(10);
        add(numGroupsField);
        label.setLabelFor(numGroupsField);
        ++row;

        label = new JLabel("Min DetectionCount", JLabel.TRAILING);
        add(label);
        minDetectionCountField = new JFormattedTextField(minDetectionCount);
        minDetectionCountField.setColumns(10);
        add(minDetectionCountField);
        label.setLabelFor(minDetectionCountField);
        ++row;

        label = new JLabel("Suppress Bad Detectors", JLabel.TRAILING);
        add(label);
        suppressBadDetectorsChk = new JCheckBox("", suppressBadDetectors);
        label.setLabelFor(suppressBadDetectorsChk);
        suppressBadDetectorsChk.setSelected(suppressBadDetectors);
        add(suppressBadDetectorsChk);
        ++row;

        label = new JLabel("Require Correlation for Rebuild", JLabel.TRAILING);
        add(label);
        requireCorrelationChk = new JCheckBox("", requireCorrelation);
        label.setLabelFor(requireCorrelationChk);
        requireCorrelationChk.setSelected(requireCorrelation);
        add(requireCorrelationChk);
        ++row;

        label = new JLabel("Auto-Apply Filter", JLabel.TRAILING);
        add(label);
        autoApplyFilterChk = new JCheckBox("", autoApplyFilter);
        label.setLabelFor(autoApplyFilterChk);
        autoApplyFilterChk.setSelected(autoApplyFilter);
        add(autoApplyFilterChk);
        ++row;

        label = new JLabel("Retrieve by Blocks", JLabel.TRAILING);
        add(label);
        retrieveByBlocksChk = new JCheckBox("", retrieveByBlocks);
        label.setLabelFor(retrieveByBlocksChk);
        retrieveByBlocksChk.setSelected(retrieveByBlocks);
        add(retrieveByBlocksChk);
        ++row;

        label = new JLabel("Block Size", JLabel.TRAILING);
        add(label);
        blockSizeField = new JFormattedTextField(blockSize);
        blockSizeField.setColumns(10);
        add(blockSizeField);
        label.setLabelFor(blockSizeField);
        ++row;

        this.setBorder(BorderFactory.createLineBorder(Color.blue));

        SpringUtilities.makeCompactGrid(this,
                row, 2,
                5, 5, //initX, initY
                5, 5);

    }

    public String getOriginTableName() {
        return originTableField.getText();
    }

    public double getClusterThreshold() {
        return (Double) clusterThresholdField.getValue();
    }

    public double getMinDetStatThreshold() {
        return (Double) minDetStatField.getValue();
    }

    public double getMaxDetStatThreshold() {
        return (Double) maxDetStatField.getValue();
    }

    public DetectorType getDetectorType() {
        return (DetectorType) detectorTypeCombo.getSelectedItem();
    }

    public double getTraceLength() {
        return (Double) traceLengthField.getValue();
    }

    public boolean isFixShiftsToZero() {
        return fixShiftsChk.isSelected();
    }

    public int getMinDetectionCount() {
        return (Integer) minDetectionCountField.getValue();
    }

    public boolean isSuppressBadDetectors() {
        return suppressBadDetectorsChk.isSelected();
    }

    public boolean isRequireCorrelation() {
        return requireCorrelationChk.isSelected();
    }

    public double getWindowStart() {
        return (Double) this.prePickSecondsField.getValue();
    }

    public boolean isAutoApplyFilter() {
        return autoApplyFilterChk.isSelected();
    }

    public boolean isRetrieveByBlocks() {
        return retrieveByBlocksChk.isSelected();
    }

    public int getBlockSize() {
        return (Integer) blockSizeField.getValue();
    }
    
    
    public ClusterType getClusterType() {
        return (ClusterType) clusterTypeCombo.getSelectedItem();
    }

    public int getNumberOfGroups()
    {
        return (Integer) numGroupsField.getValue();
    }

}
