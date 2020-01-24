/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.plotPrefs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;
import llnl.gnem.core.gui.plotting.JFontChooser;
import llnl.gnem.core.gui.plotting.TickDir;
import llnl.gnem.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge1
 */
public class TickPrefsPanel extends JPanel implements ActionListener {
    private final TickPrefs prefs;

    private Font font;
    private final JButton fontSetBtn;
    private final JButton fontColorBtn;
    private final JLabel exampleLabel;
    private final JRadioButton dirIn;
    private final JRadioButton dirOut;
    private ButtonGroup buttonGroup;
    private final JCheckBox visibility;


    public TickPrefsPanel(TickPrefs prefs)
    {
        super( new SpringLayout());
        this.prefs = prefs;

        JPanel tickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Check box. Turn visibility on and off.
        visibility = new JCheckBox("Ticks Are Visible", prefs.isVisible());
        tickPanel.add(visibility);

        // Tick direction
        JLabel tickDirLabel = new JLabel("Tick direction:", JLabel.LEFT);
        tickPanel.add(tickDirLabel);

        buttonGroup = new ButtonGroup();

        dirIn = new JRadioButton("In");
        dirIn.setActionCommand("In");
        tickPanel.add(dirIn);
        buttonGroup.add(dirIn);

        dirOut = new JRadioButton("Out");
        dirOut.setActionCommand("Out");
        tickPanel.add(dirOut);
        buttonGroup.add(dirOut);

        TickDir dir = prefs.getDirection();
        if (dir == TickDir.IN) {
            dirIn.setSelected(true);
        }
        else {
            dirOut.setSelected(true);
        }


        add(tickPanel);



        // Font
        font = prefs.getFont();

        JPanel fontButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        fontSetBtn = new JButton("Set Font");
        fontSetBtn.addActionListener(this);
        fontButtonPanel.add(fontSetBtn);

        fontColorBtn = new JButton("Set Color");
        fontColorBtn.setForeground(prefs.getFontColor());
        fontColorBtn.addActionListener(this);
        fontButtonPanel.add(fontColorBtn);

        add(fontButtonPanel);


        // Example label
        exampleLabel = new JLabel("Example tick label:  1.2345", JLabel.LEFT);
        exampleLabel.setFont(font);
        exampleLabel.setForeground(fontColorBtn.getForeground());
        add(exampleLabel);


         // Set some empty space.
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(30,300));
        add(spacer);

      // Set the display grid
        SpringUtilities.makeCompactGrid(this,
                4, 1, //rows, cols
                6, 5, //initX, initY
                3, 3);  //xPad, yPad
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
            Color newColor = JColorChooser.showDialog(this, "Choose Color", fontColorBtn.getForeground());
            if (newColor != null) {
                fontColorBtn.setForeground(newColor);
                exampleLabel.setForeground(fontColorBtn.getForeground());
            }
        }
    }

    /**
     *
     * @return TickDir tick direction
     */
     public TickDir getTickDir()
    {
        TickDir dir;
        if (dirIn.isSelected()) {
            dir = TickDir.IN;
        }
        else {
            dir = TickDir.OUT;
        }
        return dir;
    }

   /**
    * Update the preferences using the values found in the dialog console.
    */
   void updatePrefsFromControls() {
        prefs.setFont(font);
        prefs.setFontColor(fontColorBtn.getForeground());
        prefs.setDirection(getTickDir());
        prefs.setVisible(visibility.isSelected());
    }

}
