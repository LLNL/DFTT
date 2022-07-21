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
public class WindowSelectionParamPane extends JPanel {

    private final JFormattedTextField floorFactorField;
    private final JCheckBox refineWindowChk;
    private final JFormattedTextField SNRThresholdField;
    private final JFormattedTextField analysisWindowLengthField;
    private final JFormattedTextField minimumWindowLengthField;

    public WindowSelectionParamPane(double floorFactor,
            boolean refineWindow,
            double snrThreshold,
            double analysisWindowLength,
            double minimumWindowLength) {

        super(new SpringLayout());
        JLabel label = new JLabel("Floor Factor", JLabel.TRAILING);
        add(label);
        floorFactorField = new JFormattedTextField(floorFactor);
        floorFactorField.setColumns(10);
        add(floorFactorField);
        label.setLabelFor(floorFactorField);

        label = new JLabel("Analysis Window Length (s)", JLabel.TRAILING);
        add(label);
        analysisWindowLengthField = new JFormattedTextField(analysisWindowLength);
        analysisWindowLengthField.setColumns(10);
        add(analysisWindowLengthField);
        label.setLabelFor(analysisWindowLengthField);

        label = new JLabel("SNR Threshold", JLabel.TRAILING);
        add(label);
        SNRThresholdField = new JFormattedTextField(snrThreshold);
        SNRThresholdField.setColumns(10);
        add(SNRThresholdField);
        label.setLabelFor(SNRThresholdField);

        label = new JLabel("Minimum Window Length (s)", JLabel.TRAILING);
        add(label);
        minimumWindowLengthField = new JFormattedTextField(minimumWindowLength);
        minimumWindowLengthField.setColumns(10);
        add(minimumWindowLengthField);
        label.setLabelFor(minimumWindowLengthField);

        label = new JLabel("Refine Window (2nd-pass)", JLabel.TRAILING);
        add(label);
        refineWindowChk = new JCheckBox("", refineWindow);
        label.setLabelFor(refineWindowChk);
        refineWindowChk.setSelected(refineWindow);
        add(refineWindowChk);

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 500));
        add(spacer);
        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 500));
        add(spacer);
        this.setBorder(BorderFactory.createLineBorder(Color.blue));

        SpringUtilities.makeCompactGrid(this,
                6, 2,
                5, 5, //initX, initY
                5, 5);
    }

    public double getFloorFactor() {
        return (Double) floorFactorField.getValue();
    }

    public double getSNRThreshold() {
        return (Double) SNRThresholdField.getValue();
    }

    public boolean isRefineWindow() {
        return refineWindowChk.isSelected();
    }

    public double getAnalysisWindowLength() {
        return (Double) analysisWindowLengthField.getValue();
    }

    public double getMinimumWindowLength() {
        return (Double) minimumWindowLengthField.getValue();
    }

}
