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
package llnl.gnem.apps.detection.sdBuilder.templateDisplay;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author dodge1
 */
public class DimensionSelector extends JPanel implements ActionListener {

    private static final long serialVersionUID = -4794974492644919435L;

    private final JComboBox combo;
    private TemplateView view;
    private final DefaultComboBoxModel model;

    public DimensionSelector() {
        super(new FlowLayout());
        JLabel label = new JLabel("Dimension");
        add(label);
        Integer[] dimensions = {0};
        model = new DefaultComboBoxModel(dimensions);
        combo = new JComboBox(model);
        combo.setSelectedIndex(0);
        add(combo);
        combo.addActionListener(this);
    }

    void updateDimensions(int numDimensions) {
        combo.removeActionListener(this);
        model.removeAllElements();

        for (int j = 0; j < numDimensions; ++j) {
            combo.addItem(j);
        }
        combo.setSelectedItem(0);
        combo.addActionListener(this);
    }

    void setView(TemplateView view) {
        this.view = view;
    }

    void reset() {
        combo.setSelectedItem(0);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        int dimension = (Integer) combo.getSelectedItem();
        if (view != null) {
            view.setDimension(dimension);
        }
    }
}
