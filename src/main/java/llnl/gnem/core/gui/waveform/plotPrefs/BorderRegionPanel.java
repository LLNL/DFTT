/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.plotPrefs;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import llnl.gnem.core.gui.plotting.JFontChooser;
import llnl.gnem.core.gui.util.SpringUtilities;

/**
 *
 * @author dyer1
 */
public class BorderRegionPanel extends JPanel implements ActionListener {

    private final DrawingRegionPrefs prefs;
    private DrawingRegionPanel drawingPanel;
    private final JButton fontSetBtn;
    private final JButton fontColorBtn;
    private final JLabel exampleLabel;
    private Font font;

    /**
     * Set the dialog controls for drawing border area preferences.
     * @param prefs
     */
    public BorderRegionPanel(DrawingRegionPrefs prefs) {
        super(new SpringLayout());
        this.prefs = prefs;

        // Font
        font = prefs.getFont();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        fontSetBtn = new JButton("Set Title Font");
        fontSetBtn.addActionListener(this);
        topPanel.add(fontSetBtn);

        fontColorBtn = new JButton("Set Title Color");
        fontColorBtn.setForeground(prefs.getFontColor());
        fontColorBtn.addActionListener(this);
        topPanel.add(fontColorBtn);

        add( topPanel );

        JPanel secondPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exampleLabel = new JLabel("Example Title");
        exampleLabel.setFont(font);
        exampleLabel.setForeground(fontColorBtn.getForeground());
        secondPanel.add(exampleLabel);
        add(secondPanel);

        drawingPanel = new DrawingRegionPanel(prefs, true);
        add( drawingPanel );


        this.setBorder(BorderFactory.createLineBorder(Color.blue));

        // Set the display grid
        SpringUtilities.makeCompactGrid(this, // test
                3, 1, //rows, cols
                6, 5, //initX, initY
                6, 6);       //xPad, yPad
    }

    @Override
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
            Color newColor = JColorChooser.showDialog(this, "Choose Title Color", fontColorBtn.getForeground());
            if (newColor != null) {
                fontColorBtn.setForeground(newColor);
                exampleLabel.setForeground(fontColorBtn.getForeground());
            }
        }
    }

    void updatePrefsFromControls() {
        drawingPanel.updatePrefsFromControls();
        prefs.setFont(font);
        prefs.setFontColor(fontColorBtn.getForeground());
    }
}
