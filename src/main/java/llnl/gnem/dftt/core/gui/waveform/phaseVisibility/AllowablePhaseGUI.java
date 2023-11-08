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
package llnl.gnem.dftt.core.gui.waveform.phaseVisibility;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import llnl.gnem.dftt.core.dataAccess.dataObjects.PhaseType;
import llnl.gnem.dftt.core.dataAccess.dataObjects.SeismicPhase;
import llnl.gnem.dftt.core.gui.util.Utility;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentViewer;

/**
 *
 * @author dodge1
 */
public class AllowablePhaseGUI extends JPanel {

    private static final long serialVersionUID = 1863877308528848612L;

    private JButton saveButton;
    private PhaseDataTableModel dataModel;
    private DefaultListModel phaseTypeListModel;
    private JList phaseTypeList;
    Map<PhaseType, Collection<SelectablePhase>> phaseTypeMap;

    public AllowablePhaseGUI() {
        this.setLayout(new BorderLayout());
        phaseTypeMap = new HashMap<>();
        BaseAvailablePhaseManager apm = BaseAvailablePhaseManager.getInstance();
        populateMap(apm.getAvailablePhases());
        JScrollPane phaseTypePane = setUpPhaseTypeSelector();
        JScrollPane phaseDataPane = setUpPhaseDataTable();
        for (PhaseType type : phaseTypeMap.keySet()) {
            phaseTypeListModel.addElement(type);
        }
        phaseTypeList.setSelectedIndex(0);
        PhaseType aType = (PhaseType) phaseTypeListModel.get(0);
        Collection<SelectablePhase> phases = phaseTypeMap.get(aType);
        dataModel.setPhaseData(phases);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, phaseTypePane, phaseDataPane);
        add(splitPane, BorderLayout.CENTER);

        setUpSaveButtonPanel();

        phaseTypeList.addListSelectionListener(new PhaseTypeListSelectionListener());
    }

    private void populateMap(Collection<SeismicPhase> phases) {
        phaseTypeMap.clear();
        BasePreferredPhaseManager bpm = BasePreferredPhaseManager.getInstance();
        for (SeismicPhase phase : phases) {
            boolean preferred = bpm.isPreferred(phase);
            PhaseType type = phase.getType();
            Collection<SelectablePhase> tmp = phaseTypeMap.get(type);
            if (tmp == null) {
                tmp = new ArrayList<>();
                phaseTypeMap.put(type, tmp);
            }
            tmp.add(new SelectablePhase(preferred, phase));
        }
    }

    private JScrollPane setUpPhaseTypeSelector() {
        phaseTypeListModel = new DefaultListModel();
        phaseTypeList = new JList(phaseTypeListModel);
        phaseTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane phaseTypePane = new JScrollPane(phaseTypeList);
        phaseTypePane.setMinimumSize(new Dimension(100, 200));
        return phaseTypePane;
    }

    private void setUpSaveButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new LineBorder(Color.black));
        add(buttonPanel, BorderLayout.SOUTH);

        saveButton = new JButton("Save to preferences",Utility.getIcon(this, "miscIcons/Save24.gif"));
        saveButton.setToolTipText("Save the current set of selected phases to user preferences.");
        buttonPanel.add(saveButton);
        saveButton.addActionListener(new SavePrefsActionListener());
    }

    private JScrollPane setUpPhaseDataTable() {
        dataModel = new PhaseDataTableModel();
        JTable phaseDataTable = new JTable(dataModel);
        TableColumn col = phaseDataTable.getColumnModel().getColumn(0);
        col.setPreferredWidth(50);
        col.setMaxWidth(50);
        col = phaseDataTable.getColumnModel().getColumn(1);
        col.setPreferredWidth(50);
        col.setMaxWidth(50);
        JScrollPane phaseDataPane = new JScrollPane(phaseDataTable);
        phaseDataPane.setMinimumSize(new Dimension(300, 200));
        return phaseDataPane;
    }

    private  class SavePrefsActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            BasePreferredPhaseManager.getInstance().savePreferences();
        }
    }

    private class PhaseTypeListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {

            PhaseType aType = (PhaseType) phaseTypeListModel.get(phaseTypeList.getSelectedIndex());
            Collection<SelectablePhase> phases = phaseTypeMap.get(aType);
            dataModel.setPhaseData(phases);
        }
    }

}
