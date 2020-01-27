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
