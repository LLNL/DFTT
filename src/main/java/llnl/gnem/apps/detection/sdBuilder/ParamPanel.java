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
package llnl.gnem.apps.detection.sdBuilder;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge1
 */
public class ParamPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -6873753328381783612L;
    
    private final JFormattedTextField clusterThresholdField;
    private final JFormattedTextField detectionThresholdField;
    private final JFormattedTextField energyCaptureField;
    private final JFormattedTextField blackoutField;
    private final JFormattedTextField fftSizeField;
    private final JFormattedTextField staDurationField;
    private final JFormattedTextField ltaDurationField;
    private final JFormattedTextField gapDurationField;
    private final JFormattedTextField traceLengthField;
    private final JComboBox detectorTypeCombo;
    private final JCheckBox normalizeChk;
    private final JCheckBox prewhitenChk;
    private final JCheckBox spawningChk;
    private final JCheckBox fixShiftsChk;
   
    
    public ParamPanel(double clusterThreshold,
            double detectionThreshold,
            double energyCapture,
            double blackoutSeconds,
            DetectorType detectorType,
            int fftSize,
            int matchedFieldDesignFactor,
            int matchedFieldDimension,
            double staDuration,
            double ltaDuration,
            double gapDuration,
            boolean normalizeStatistics,
            boolean prewhitenStatistics,
            boolean enableSpawning,
            double traceLength,
            boolean fixShiftsToZero) {
        
        super(new SpringLayout());
        
        JLabel label = new JLabel("Retrieved Trace Length", JLabel.TRAILING);
        add(label);

        traceLengthField = new JFormattedTextField(traceLength);
        traceLengthField.setColumns(10);
        add(traceLengthField);
        label.setLabelFor(traceLengthField);
        
        label = new JLabel("Force Zero Shifts", JLabel.TRAILING);
        add(label);
        fixShiftsChk = new JCheckBox("", fixShiftsToZero);
        label.setLabelFor(fixShiftsChk);
        fixShiftsChk.setSelected(fixShiftsToZero);
        add(fixShiftsChk);
        
        
        label = new JLabel("Cluster Threshold", JLabel.TRAILING);
        add(label);
        
        clusterThresholdField = new JFormattedTextField(clusterThreshold);
        clusterThresholdField.setColumns(10);
        add(clusterThresholdField);
        label.setLabelFor(clusterThresholdField);
        
        label = new JLabel("Detector Threshold", JLabel.TRAILING);
        add(label);
        detectionThresholdField = new JFormattedTextField(detectionThreshold);
        detectionThresholdField.setColumns(10);
        add(detectionThresholdField);
        label.setLabelFor(detectionThresholdField);
        
        
        label = new JLabel("Energy Capture", JLabel.TRAILING);
        add(label);
        energyCaptureField = new JFormattedTextField(energyCapture);
        energyCaptureField.setColumns(10);
        add(energyCaptureField);
        label.setLabelFor(energyCaptureField);
        
        label = new JLabel("Blackout Seconds", JLabel.TRAILING);
        add(label);
        blackoutField = new JFormattedTextField(blackoutSeconds);
        blackoutField.setColumns(10);
        add(blackoutField);
        label.setLabelFor(blackoutField);
        
        label = new JLabel("Detector Type", JLabel.TRAILING);
        add(label);
        
        DetectorType[] types = {DetectorType.SUBSPACE};
        detectorTypeCombo = new JComboBox(types);
        add(detectorTypeCombo);
        label.setLabelFor(detectorTypeCombo);
        detectorTypeCombo.setSelectedItem(detectorType);
        detectorTypeCombo.addActionListener(this);
        
        label = new JLabel("FFT Size", JLabel.TRAILING);
        add(label);
        fftSizeField = new JFormattedTextField(fftSize);
        fftSizeField.setColumns(10);
        add(fftSizeField);
        label.setLabelFor(fftSizeField);
        
        
        label = new JLabel("STA Duration", JLabel.TRAILING);
        add(label);
        staDurationField = new JFormattedTextField(staDuration);
        staDurationField.setColumns(10);
        add(staDurationField);
        label.setLabelFor(staDurationField);
        
        label = new JLabel("LTA Duration", JLabel.TRAILING);
        add(label);
        ltaDurationField = new JFormattedTextField(ltaDuration);
        ltaDurationField.setColumns(10);
        add(ltaDurationField);
        label.setLabelFor(ltaDurationField);
        
        label = new JLabel("Gap Duration", JLabel.TRAILING);
        add(label);
        gapDurationField = new JFormattedTextField(gapDuration);
        gapDurationField.setColumns(10);
        add(gapDurationField);
        label.setLabelFor(gapDurationField);
        
        label = new JLabel("Normalize", JLabel.TRAILING);
        add(label);
        normalizeChk = new JCheckBox("",normalizeStatistics);
         label.setLabelFor(normalizeChk);
        add(normalizeChk);
        
        
        label = new JLabel("Pre-whiten", JLabel.TRAILING);
        add(label);
        prewhitenChk = new JCheckBox("", prewhitenStatistics);
        label.setLabelFor(prewhitenChk);
        add(prewhitenChk);
        
       
        label = new JLabel("Spawn Correlators", JLabel.TRAILING);
        add(label);
        spawningChk = new JCheckBox("", enableSpawning);
        label.setLabelFor(spawningChk);
        add(spawningChk); 
        this.setBorder(BorderFactory.createLineBorder(Color.blue));
        
        SpringUtilities.makeCompactGrid(this,
                16, 2,
                5, 5, //initX, initY
                5, 5);
        actionPerformed(null);
    }
    
    public double getClusterThreshold() {
        return (Double) clusterThresholdField.getValue();
    }
    
    public double getDetectionThreshold() {
        return (Double) detectionThresholdField.getValue();
    }
    
    public double getEnergyCapture() {
        return (Double) energyCaptureField.getValue();
    }
    
    public double getBlackoutSeconds() {
        return (Double) blackoutField.getValue();
    }
    
    public DetectorType getDetectorType() {
        return (DetectorType) detectorTypeCombo.getSelectedItem();
    }
    
    public int getFFTSize() {
        return (Integer) fftSizeField.getValue();
    }
    
    public double getStaDuration() {
        return (Double) staDurationField.getValue();
    }
    
    public double getLtaDuration() {
        return (Double) ltaDurationField.getValue();
    }
    
    public double getGapDuration() {
        return (Double) gapDurationField.getValue();
    }
    
    public boolean isNormalizeStatistics()
    {
        return normalizeChk.isSelected();
    }
    
    public boolean isPrewhitenStatistics()
    {
        return prewhitenChk.isSelected();
    }
    
    public boolean isSpawnOnTriggers()
    {
        return spawningChk.isSelected();
    }
    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        DetectorType type = getDetectorType();
    }

    public double getTraceLength() {
        return (Double) traceLengthField.getValue();
    }
    
    public boolean isFixShiftsToZero()
    {
        return fixShiftsChk.isSelected();
    }
}
