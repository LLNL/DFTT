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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import llnl.gnem.dftt.core.gui.plotting.JFontChooser;
import llnl.gnem.dftt.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge1
 */
public class LabelPrefsPanel extends JPanel implements ActionListener {
    private final LabelPrefs prefs;
    private Font font;
    private final JButton fontSetBtn;
    private final JButton fontColorBtn;
    private final JLabel exampleLabel;
    private final SpinnerModel model;


    public LabelPrefsPanel(LabelPrefs prefs)
    {
        super(new SpringLayout());
        this.prefs = prefs;

        // Font
        font = prefs.getFont();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        fontSetBtn = new JButton("Set Font");
        fontSetBtn.addActionListener(this);
        topPanel.add(fontSetBtn);

        fontColorBtn = new JButton("Set Font Color");
        fontColorBtn.setForeground(prefs.getFontColor());
        fontColorBtn.addActionListener(this);
        topPanel.add(fontColorBtn);


        // Label and JSpinner. Set offset.
        JPanel offsetPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Label Offset", JLabel.LEADING);
        offsetPanel.add(label);
        model = new SpinnerNumberModel((int)prefs.getOffset(),1,20,1);
        JSpinner offsetSpinner = new JSpinner(model);
        offsetPanel.add(offsetSpinner);
        topPanel.add( offsetPanel );

        add(topPanel);


        JPanel secondPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exampleLabel = new JLabel("Example axis label.");
        exampleLabel.setFont(font);
        exampleLabel.setForeground(fontColorBtn.getForeground());
        secondPanel.add(exampleLabel);
        add(secondPanel);


        // Set some empty space.
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(30,300));
        add(spacer);

        // Set the display grid
        SpringUtilities.makeCompactGrid(this,
                3, 1, //rows, cols
                1, 1, //initX, initY
                1, 1);  //xPad, yPad
    }

    @Override
    /**
     * Do something if certain actions happened.
     */
    public void actionPerformed(ActionEvent ae) {
        if( ae.getSource() == fontSetBtn){
            JFontChooser fontChooser = new JFontChooser(font);
            int result = fontChooser.showDialog(this);
            if (result == JFontChooser.OK_OPTION) {
                font = fontChooser.getSelectedFont();
                exampleLabel.setFont(font);

            }
        }
        else if( ae.getSource() == fontColorBtn){
            Color newColor = JColorChooser.showDialog(this, "Choose Font Color", fontColorBtn.getForeground());
            if (newColor != null) {
                fontColorBtn.setForeground(newColor);
                exampleLabel.setForeground(fontColorBtn.getForeground());
            }
        }
    }


   /**
    * Update the preferences using the values found in the dialog console.
    */
    void updatePrefsFromControls() {
        prefs.setFont(font);
        prefs.setFontColor(fontColorBtn.getForeground());
        prefs.setOffset((Integer)model.getValue());
    }

}
