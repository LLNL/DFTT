/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.configuration;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import llnl.gnem.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge1
 */
public class ArrayCorrelationParamsPanel extends JPanel {

    private final JFormattedTextField energyCaptureField;

    private final JFormattedTextField staDurationField;
    private final JFormattedTextField ltaDurationField;
    private final JFormattedTextField gapDurationField;

    public ArrayCorrelationParamsPanel(
            double energyCapture,
            double staDuration,
            double ltaDuration,
            double gapDuration) {

        super(new SpringLayout());

        JLabel label = new JLabel("Energy Capture", JLabel.TRAILING);
        add(label);
        energyCaptureField = new JFormattedTextField(energyCapture);
        energyCaptureField.setColumns(10);
        add(energyCaptureField);
        label.setLabelFor(energyCaptureField);

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

    public double getStaDuration() {
        return (Double) staDurationField.getValue();
    }

    public double getLtaDuration() {
        return (Double) ltaDurationField.getValue();
    }

    public double getGapDuration() {
        return (Double) gapDurationField.getValue();
    }

}
