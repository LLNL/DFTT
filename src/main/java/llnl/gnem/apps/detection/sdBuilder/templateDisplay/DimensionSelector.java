/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
