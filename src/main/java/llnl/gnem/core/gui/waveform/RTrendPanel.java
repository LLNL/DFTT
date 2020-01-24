/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import llnl.gnem.core.gui.waveform.ThreeComponentViewer.RemoveTrendAction;
import llnl.gnem.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge1
 */
public class RTrendPanel extends JPanel {
    private final ThreeComponentViewer owner;
    private final JToggleButton button;

    public RTrendPanel(ThreeComponentViewer owner) {
        super(new SpringLayout());
        this.owner = owner;
        addButton(owner.getButtonAction(RemoveTrendAction.class).getButton());
        button = new JToggleButton();
        button.setBackground(new Color(255, 200, 200));
        button.addActionListener(new TrendRemovalActionListener());
        button.setPreferredSize(new Dimension(10, 36));
        button.setMaximumSize(new Dimension(10, 36));
        add(button);
        SpringUtilities.makeCompactGrid(this,
                1, 2, //rows, cols
                0, 0, //initX, initY
                0, 0);       //xPad, yPad
        setPreferredSize(new Dimension(50, 36));
        setMaximumSize(new Dimension(50, 36));
        setMinimumSize(new Dimension(50, 36));
    }

    private void addButton(AbstractButton button) {
        if (button.getIcon() != null) {
            button.setText(""); //an icon-only button
        }
        button.setPreferredSize(new Dimension(38, 36));
        button.setMaximumSize(new Dimension(38, 36));
        add(button);
    }

    private void setTooltip() {
        String text = button.isSelected() ? "Click to disable automatic trend removal on input" : "Click to enable automatic trend removal on input";
        button.setToolTipText(text);
    }

    private class TrendRemovalActionListener implements ActionListener {

        public TrendRemovalActionListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            owner.getDataModel().setRemoveTrendOnInput(button.isSelected());
            setTooltip();
        }
    }

    public void updateState(ThreeComponentModel dataModel) {
        button.setSelected(dataModel.isRemoveTrendOnInput());
        setTooltip();
    }
}
