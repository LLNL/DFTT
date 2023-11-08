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
package llnl.gnem.dftt.core.gui.waveform;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentViewer.ApplyTaperAction;
import llnl.gnem.dftt.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge1
 */
public class TaperPanel extends JPanel {
    private final ThreeComponentViewer owner;
    private final JToggleButton button;

    public TaperPanel(ThreeComponentViewer owner) {
        super(new SpringLayout());
        this.owner = owner;
        addButton(owner.getButtonAction(ApplyTaperAction.class).getButton());
        button = new JToggleButton();
        button.setBackground(new Color(255,200,200));
        button.addActionListener(new TaperActionListener());
        button.setPreferredSize(new Dimension(10, 36));
        button.setMaximumSize(new Dimension(10, 36));
        add(button);
        SpringUtilities.makeCompactGrid(this,
                1, 2, //rows, cols
                0, 0, //initX, initY
                0, 0);       //xPad, yPad
        setPreferredSize(new Dimension(50,36));
        setMaximumSize(new Dimension(50,36));
        setMinimumSize(new Dimension(50,36));
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
        String text = button.isSelected() ? "Click to disable automatic taper on input" : "Click to enable automatic taper on input";
        button.setToolTipText(text);
    }

    private class TaperActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            owner.getDataModel().setTaperOnInput(button.isSelected());
            setTooltip();
        }
    }

    public void updateState(ThreeComponentModel dataModel) {
        button.setSelected(dataModel.isTaperOnInput());
        setTooltip();
    }
}
