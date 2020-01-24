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
public class CommonDetectorParamsPanel extends JPanel {

    private final JFormattedTextField detectionThresholdField;

    private final JFormattedTextField blackoutField;

    public CommonDetectorParamsPanel(
            double detectionThreshold,
            double blackoutSeconds) {

        super(new SpringLayout());

        JLabel label = new JLabel("Detector Threshold", JLabel.TRAILING);
        add(label);
        detectionThresholdField = new JFormattedTextField(detectionThreshold);
        detectionThresholdField.setColumns(10);
        add(detectionThresholdField);
        label.setLabelFor(detectionThresholdField);

        label = new JLabel("Blackout Seconds", JLabel.TRAILING);
        add(label);
        blackoutField = new JFormattedTextField(blackoutSeconds);
        blackoutField.setColumns(10);
        add(blackoutField);
        label.setLabelFor(blackoutField);
        
        JPanel spacer = new JPanel();
       spacer.setPreferredSize(new Dimension(100, 500));
        add(spacer);
        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 500));
        add(spacer);
        this.setBorder(BorderFactory.createLineBorder(Color.blue));

        SpringUtilities.makeCompactGrid(this,
                3, 2,
                5, 5, //initX, initY
                5, 5);

    }

    public double getDetectionThreshold() {
        return (Double) detectionThresholdField.getValue();
    }

    public double getBlackoutSeconds() {
        return (Double) blackoutField.getValue();
    }

}
