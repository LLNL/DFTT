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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import llnl.gnem.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge
 */
public class PickingParamPane extends JPanel {

    private static final long serialVersionUID = -5549814037152365518L;

    private final JCheckBox displayPredictedPicksChk;
    private final JCheckBox displayDetectionLabelsChk;
    private final JCheckBox displayEventIDLabelsChk;

    public PickingParamPane( boolean displayPredictedPicks,
             boolean displayEventIDLabels,
            boolean displayDetectionLabels) {

        super(new SpringLayout());
        JLabel label = new JLabel("Display Predicted Picks", JLabel.TRAILING);
        add(label);
        displayPredictedPicksChk = new JCheckBox("", displayPredictedPicks);
        label.setLabelFor(displayPredictedPicksChk);
        displayPredictedPicksChk.setSelected(displayPredictedPicks);
        add(displayPredictedPicksChk);

        label = new JLabel("Display Detection Labels", JLabel.TRAILING);
        add(label);
        displayDetectionLabelsChk = new JCheckBox("", displayDetectionLabels);
        label.setLabelFor(displayDetectionLabelsChk);
        displayDetectionLabelsChk.setSelected(displayDetectionLabels);
        add(displayDetectionLabelsChk);


        label = new JLabel("Display Event Windows", JLabel.TRAILING);
        add(label);
        displayEventIDLabelsChk = new JCheckBox("", displayEventIDLabels);
        label.setLabelFor(displayEventIDLabelsChk);
        displayEventIDLabelsChk.setSelected(displayEventIDLabels);
        add(displayEventIDLabelsChk);
        

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 500));
        add(spacer);
        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 500));
        add(spacer);
        this.setBorder(BorderFactory.createLineBorder(Color.blue));

        SpringUtilities.makeCompactGrid(this,
                4, 2,
                5, 5, //initX, initY
                5, 5);
    }
    public boolean isDisplayPredictedPicks() {
        return displayPredictedPicksChk.isSelected();
    }

    public boolean isDisplayDetectionLabels() {
        return displayDetectionLabelsChk.isSelected();
    }

    public boolean isDisplayEventIDLabels() {
        return displayEventIDLabelsChk.isSelected();
    }

}
