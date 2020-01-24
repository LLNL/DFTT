/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.plotPrefs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import llnl.gnem.core.gui.plotting.PenStyle;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickTextPosition;
import llnl.gnem.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge1
 */
public class PickPrefsPanel extends JPanel {

    private final PickPrefs prefs;
    private final SpinnerModel fracHgtModel;
    private final SpinnerModel widthModel;
    private final SpinnerModel textSizeModel;
    private final JComboBox txtPosCombo;
    private final JComboBox penStyleCombo;
    private final JButton colorBtn;

    PickPrefsPanel(PickPrefs pickPrefs) {
        super(new SpringLayout());
        this.prefs = pickPrefs;
        JLabel label = new JLabel("Fractional Height", JLabel.TRAILING);
        add(label);
        fracHgtModel = new SpinnerNumberModel(prefs.getHeight(), 0.1, 1.0, 0.1);
        JSpinner spinner = new JSpinner(fracHgtModel);
        label.setLabelFor(spinner);
        add(spinner);

        label = new JLabel("Width", JLabel.TRAILING);
        add(label);
        widthModel = new SpinnerNumberModel(prefs.getWidth(), 1, 5, 1);
        spinner = new JSpinner(widthModel);
        label.setLabelFor(spinner);
        add(spinner);

        label = new JLabel("Text Size", JLabel.TRAILING);
        add(label);
        textSizeModel = new SpinnerNumberModel(prefs.getTextSize(), 6, 18, 1);
        spinner = new JSpinner(textSizeModel);
        label.setLabelFor(spinner);
        add(spinner);

        label = new JLabel("Text Position", JLabel.TRAILING);
        add(label);
        txtPosCombo = new JComboBox(PickTextPosition.values());
        label.setLabelFor(txtPosCombo);
        txtPosCombo.setSelectedItem(prefs.getTextPosition());
        add(txtPosCombo);

        label = new JLabel("Pen Style", JLabel.TRAILING);
        add(label);
        penStyleCombo = new JComboBox(PenStyle.values());
        label.setLabelFor(penStyleCombo);
        penStyleCombo.setSelectedItem(prefs.getPenStyle());

        add(penStyleCombo);

        label = new JLabel("Color", JLabel.TRAILING);
        add(label);
        colorBtn = new JButton("Color");
        colorBtn.setForeground(prefs.getColor());
        label.setLabelFor(colorBtn);
        add(colorBtn);
        colorBtn.addActionListener(new ColorBtnListener());


        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(30, 300));
        add(spacer);
        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(30, 300));
        add(spacer);
        this.setBorder(BorderFactory.createLineBorder(Color.blue));

        SpringUtilities.makeCompactGrid(this, // test
                7, 2, //rows, cols
                6, 5, //initX, initY
                6, 6);       //xPad, yPad
    }

    void updatePrefsFromControls() {
        prefs.setHeight((Double) fracHgtModel.getValue());
        prefs.setWidth((Integer) widthModel.getValue());
        prefs.setTextSize((Integer) textSizeModel.getValue());
        prefs.setTextPosition((PickTextPosition) txtPosCombo.getSelectedItem());
        prefs.setPenStyle((PenStyle) penStyleCombo.getSelectedItem());
        prefs.setColor(colorBtn.getForeground());
    }

    private class ColorBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            Color newColor = JColorChooser.showDialog(PickPrefsPanel.this, "Choose Fill Color", colorBtn.getForeground());
            if (newColor != null) {
                colorBtn.setForeground(newColor);
            }
        }
    }
}
