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
import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import llnl.gnem.dftt.core.gui.util.SpringUtilities;

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
