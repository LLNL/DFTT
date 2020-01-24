/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.plotPrefs;

import java.awt.Color;
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
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import llnl.gnem.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge1
 */
public class AxisPrefsPanel extends JPanel implements ActionListener {
    private final AxisPrefs prefs;
    private final LabelPrefsPanel labelPrefs;
    private final TickPrefsPanel tickPrefs;
    private final JCheckBox visibility;
    private final JButton colorBtn;
    private final SpinnerModel model;

    public AxisPrefsPanel( AxisPrefs prefs )
    {
        super( new SpringLayout());
        this.prefs = prefs;

        JPanel topPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Check box. Turn visibility on and off.
        visibility = new JCheckBox("Axis Is Visible", prefs.isVisible());
        topPane.add(visibility);

        // Color
        colorBtn = new JButton("Set Axis Color");
        colorBtn.setForeground(prefs.getColor());
        colorBtn.addActionListener(this);
        topPane.add(colorBtn);

        // Pen width
        JLabel label = new JLabel("Line Width", JLabel.LEADING);
        topPane.add(label);
        model = new SpinnerNumberModel(prefs.getPenWidth(),1,5,1);
        JSpinner lineWidthSpinner = new JSpinner(model);
        topPane.add(lineWidthSpinner);

        add(topPane);



        labelPrefs = new LabelPrefsPanel(prefs.getLabelPrefs());
        tickPrefs = new TickPrefsPanel(prefs.getTickPrefs());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Labels", null, labelPrefs);
        tabbedPane.addTab("Ticks", null, tickPrefs);

        add(tabbedPane);



        SpringUtilities.makeCompactGrid(this, // test
                2, 1, //rows, cols
                6, 5, //initX, initY
                6, 6);       //xPad, yPad
        this.setBorder(BorderFactory.createLineBorder(Color.blue));
    }

    @Override
    /**
     * Do something if certain actions happened.
     */
    public void actionPerformed(ActionEvent ae) {
        if( ae.getSource() == colorBtn){
            Color newColor = JColorChooser.showDialog(this, "Choose Color", colorBtn.getForeground());
            if (newColor != null) {
                colorBtn.setForeground(newColor);
            }
        }
    }

    void updatePrefsFromControls() {
        labelPrefs.updatePrefsFromControls();
        tickPrefs.updatePrefsFromControls();
        prefs.setVisible(visibility.isSelected());
        prefs.setColor(colorBtn.getForeground());
        prefs.setPenWidth((Integer)model.getValue());
        System.out.println("Also update remaining axis prefs here...");
    }

}
