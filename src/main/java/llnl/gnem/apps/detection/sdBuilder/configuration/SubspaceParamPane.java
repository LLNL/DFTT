/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.configuration;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import llnl.gnem.core.gui.util.SpringUtilities;

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

    public SubspaceParamPane(double energyCapture, 
            boolean fixSubspaceDimension, 
            int subspaceDimension,
            boolean requireWindowPositionConfirmation) {

        super(new SpringLayout());

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

        
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 500));
        add(spacer);
        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 500));
        add(spacer);
        this.setBorder(BorderFactory.createLineBorder(Color.blue));

        SpringUtilities.makeCompactGrid(this,
                5, 2,
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
    
    public boolean isRequireWindowPositionConfirmation()
    {
        return requireWindowPositionConfirmationChk.isSelected();
    }
}