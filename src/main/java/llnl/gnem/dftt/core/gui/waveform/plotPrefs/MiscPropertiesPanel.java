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
package llnl.gnem.dftt.core.gui.waveform.plotPrefs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import llnl.gnem.dftt.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge1
 */
public class MiscPropertiesPanel extends JPanel {

    private final PlotPresentationPrefs prefs;
    private final SpinnerNumberModel maxSymbolsModel;
    private final JCheckBox plotLineSymbolsChk;
    private final JCheckBox limitPlotSymbolsChk;
    private final JButton colorBtn;

    MiscPropertiesPanel(PlotPresentationPrefs prefs) {
        super(new SpringLayout());
        this.prefs = prefs;

        // title - font, size, color
        // llnl.gnem.dftt.core.gui.plotting.StripedDrawingRegion
        JLabel label = new JLabel("Max Line Symbols ", JLabel.TRAILING);
        add(label);
        maxSymbolsModel = new SpinnerNumberModel(prefs.getMaxSymbolsToPlot(), 10, 1000, 10);
        JSpinner spinner = new JSpinner(maxSymbolsModel);
        label.setLabelFor(spinner);
        add(spinner);

        plotLineSymbolsChk = new JCheckBox("Plot Line Symbols");
        plotLineSymbolsChk.setSelected(prefs.isPlotLineSymbols());
        add(plotLineSymbolsChk);

        limitPlotSymbolsChk = new JCheckBox("Limit Extent of Plot Line Symbols");
        limitPlotSymbolsChk.setSelected(prefs.isLimitPlottedSymbols());
        add(limitPlotSymbolsChk);

        label = new JLabel("Line Color ", JLabel.TRAILING);
        add(label);
        colorBtn = new JButton("Color");
        colorBtn.setForeground(prefs.getTraceColor());
        colorBtn.addActionListener(new ColorBtnListener());
        label.setLabelFor(colorBtn);
        add(colorBtn);

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(20, 200));
        add(spacer);
        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(20, 200));
        add(spacer);
        this.setBorder(BorderFactory.createLineBorder(Color.blue));

        SpringUtilities.makeCompactGrid(this, // test
                4, 2, //rows, cols
                6, 5, //initX, initY
                6, 6);       //xPad, yPad
    }

    void updatePrefsFromControls() {
        prefs.setMaxSymbolsToPlot((Integer) maxSymbolsModel.getValue());
        prefs.setPlotLineSymbols(plotLineSymbolsChk.isSelected());
        prefs.setLimitPlottedSymbols(limitPlotSymbolsChk.isSelected());
        prefs.setTraceColor(colorBtn.getForeground());
    }

    private class ColorBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            Color newColor = JColorChooser.showDialog(MiscPropertiesPanel.this, "Choose Color", colorBtn.getForeground());
            if (newColor != null) {
                colorBtn.setForeground(newColor);
            }
        }
    }

}
