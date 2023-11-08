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
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import llnl.gnem.dftt.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge
 */
public class SubspaceParamPane extends JPanel {

    private static final long serialVersionUID = -5549814037152365518L;

    private final JFormattedTextField energyCaptureField;
    private final JCheckBox fixSubspaceDimensionChk;
    private final JFormattedTextField subspaceDimensionField;
    private final JCheckBox requireWindowPositionConfirmationChk;
    private final JCheckBox capSubspaceDimensionChk;
    private final JCheckBox displayNewTemplatesChk;
    private final JComboBox detectorCreationOptionCombo;

    public SubspaceParamPane(double energyCapture,
            boolean fixSubspaceDimension,
            boolean capSubspaceDimension,
            int subspaceDimension,
            boolean requireWindowPositionConfirmation,
            boolean displayNewTemplates,
            DetectorCreationOption detectorCreationOption) {

        super(new SpringLayout());

        if (fixSubspaceDimension) {
            capSubspaceDimension = false;
        }
        JLabel label = new JLabel("Energy Capture", JLabel.TRAILING);
        add(label);
        energyCaptureField = new JFormattedTextField(energyCapture);
        energyCaptureField.setColumns(10);
        add(energyCaptureField);
        label.setLabelFor(energyCaptureField);

        label = new JLabel("Fix Subspace Dimension", JLabel.TRAILING);
        add(label);
        fixSubspaceDimensionChk = new JCheckBox("", fixSubspaceDimension);
        label.setLabelFor(fixSubspaceDimensionChk);
        fixSubspaceDimensionChk.setSelected(fixSubspaceDimension);
        add(fixSubspaceDimensionChk);
        fixSubspaceDimensionChk.addItemListener(new FixCheckListener());

        label = new JLabel("Cap Subspace Dimension", JLabel.TRAILING);
        add(label);
        capSubspaceDimensionChk = new JCheckBox("", capSubspaceDimension);
        label.setLabelFor(capSubspaceDimensionChk);
        capSubspaceDimensionChk.setSelected(capSubspaceDimension);
        add(capSubspaceDimensionChk);
        capSubspaceDimensionChk.addItemListener(new CapCheckListener());

        label = new JLabel("Subspace Dimension", JLabel.TRAILING);
        add(label);
        subspaceDimensionField = new JFormattedTextField(subspaceDimension);
        subspaceDimensionField.setColumns(10);
        add(subspaceDimensionField);
        label.setLabelFor(subspaceDimensionField);

        label = new JLabel("Require Window Confirmation", JLabel.TRAILING);
        add(label);
        requireWindowPositionConfirmationChk = new JCheckBox("", requireWindowPositionConfirmation);
        label.setLabelFor(requireWindowPositionConfirmationChk);
        requireWindowPositionConfirmationChk.setSelected(requireWindowPositionConfirmation);
        add(requireWindowPositionConfirmationChk);

        label = new JLabel("Display newly-created templates", JLabel.TRAILING);
        add(label);
        displayNewTemplatesChk = new JCheckBox("", displayNewTemplates);
        label.setLabelFor(displayNewTemplatesChk);
        displayNewTemplatesChk.setSelected(displayNewTemplates);
        add(displayNewTemplatesChk);
        
        

        label = new JLabel("Detector Creation Option", JLabel.TRAILING);
        add(label);

        DetectorCreationOption[] types = DetectorCreationOption.values();
        detectorCreationOptionCombo = new JComboBox(types);
        add(detectorCreationOptionCombo);
        label.setLabelFor(detectorCreationOptionCombo);
        detectorCreationOptionCombo.setSelectedItem(detectorCreationOption);
        
        

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 500));
        add(spacer);
        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 500));
        add(spacer);
        this.setBorder(BorderFactory.createLineBorder(Color.blue));

        SpringUtilities.makeCompactGrid(this,
                8, 2,
                5, 5, //initX, initY
                5, 5);
    }

    public double getEnergyCapture() {
        return (Double) energyCaptureField.getValue();
    }

    public int getSubspaceDimension() {
        return (Integer) subspaceDimensionField.getValue();
    }

    public boolean isFixSubspaceDimension() {
        return fixSubspaceDimensionChk.isSelected();
    }

    public boolean isCapSubspaceDimension() {
        return capSubspaceDimensionChk.isSelected();
    }

    public boolean isRequireWindowPositionConfirmation() {
        return requireWindowPositionConfirmationChk.isSelected();
    }
    
    public boolean isDisplayNewTemplates()
    {
        return displayNewTemplatesChk.isSelected();
    }
    
    public DetectorCreationOption getDetectorCreationOption()
    {
        return (DetectorCreationOption) detectorCreationOptionCombo.getSelectedItem();
    }

    private class CapCheckListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                fixSubspaceDimensionChk.setSelected(false);
            }
        }
    }

    private class FixCheckListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                capSubspaceDimensionChk.setSelected(false);
            }
        }
    }
}
