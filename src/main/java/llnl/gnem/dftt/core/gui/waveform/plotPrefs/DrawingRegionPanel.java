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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import llnl.gnem.dftt.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge1
 */
public class DrawingRegionPanel extends JPanel implements ActionListener {

    private final DrawingRegionPrefs prefs;
    private final JCheckBox fillRegionChk;
    private final JCheckBox drawBoxChk;
    private final JButton lineColorBtn;
    private final JButton fillColorBtn;
    private final SpinnerModel model;

    public DrawingRegionPanel(DrawingRegionPrefs prefs) {
        this(prefs, false);
        this.setBorder(BorderFactory.createLineBorder(Color.blue));
    }
    
    /**
     * Set the dialog controls for drawing region preferences.
     * @param prefs
     */
    public DrawingRegionPanel(DrawingRegionPrefs prefs, boolean noColorBorder) {
        super(new SpringLayout());
        this.prefs = prefs;

        JPanel fillPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Check box. Turn "Fill Region" on and off.
        fillRegionChk = new JCheckBox("Fill Region", prefs.isFillRegion());
        fillPanel.add(fillRegionChk);

        // JButton. Set "Fill Color"
        fillColorBtn = new JButton("Fill Color");
        fillColorBtn.setBackground(prefs.getBackgroundColor());
        fillColorBtn.setOpaque(true);
        fillColorBtn.addActionListener(this);
        fillPanel.add(fillColorBtn);
        add( fillPanel );



        JPanel outLinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Check box. Turn "Draw Box" on and off.
        drawBoxChk = new JCheckBox("Draw Outline", prefs.isDrawBox());
        outLinePanel.add(drawBoxChk);

        // JButton. Set "Outline Color"
        lineColorBtn = new JButton("Outline Color");
        lineColorBtn.setForeground(prefs.getLineColor());
        lineColorBtn.addActionListener(this);
        outLinePanel.add( lineColorBtn);
        add( outLinePanel );



        // Label and JSpinner. Set line width.
        JPanel lineWidthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Line Width", JLabel.LEADING);
        lineWidthPanel.add(label);
        model = new SpinnerNumberModel(prefs.getLineWidth(),1,5,1);
        JSpinner lineWidthSpinner = new JSpinner(model);
        lineWidthPanel.add(lineWidthSpinner);
        add( lineWidthPanel );



        // Set some empty space.
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(30,300));
        add(spacer);

        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(30, 300));
        add(spacer);

        // Set the display grid
        SpringUtilities.makeCompactGrid(this, // test
                5, 1, //rows, cols
                6, 5, //initX, initY
                6, 6);       //xPad, yPad

//        this.setBorder(BorderFactory.createLineBorder(Color.blue));

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if( ae.getSource() == fillColorBtn){
            Color newColor = JColorChooser.showDialog(this, "Choose Fill Color", fillColorBtn.getBackground());
            if( newColor != null ){
                fillColorBtn.setBackground(newColor);
                fillColorBtn.setOpaque(true);
            }
        }
        else if( ae.getSource() == lineColorBtn){
            Color newColor = JColorChooser.showDialog(this, "Choose Fill Color", lineColorBtn.getForeground());
            if (newColor != null) {
                lineColorBtn.setForeground(newColor);
            }
        }
    }

    void updatePrefsFromControls() {
        prefs.setLineColor(lineColorBtn.getForeground());
        prefs.setBackgroundColor(fillColorBtn.getBackground());
        prefs.setFillRegion(fillRegionChk.isSelected());
        prefs.setDrawBox(drawBoxChk.isSelected());
        prefs.setLineWidth((Integer)model.getValue());
    }
}
