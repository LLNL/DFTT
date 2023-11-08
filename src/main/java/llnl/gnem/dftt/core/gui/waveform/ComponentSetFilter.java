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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import llnl.gnem.dftt.core.waveform.components.ComponentSet;
import llnl.gnem.dftt.core.waveform.components.ComponentType;

/**
 *
 * @author addair1
 */
public class ComponentSetFilter extends JPanel implements ListSelectionListener, ChangeListener {
    private static final String filterString = "Display";

    private final ThreeComponentViewer parent;

    private final JList list;
    private final DefaultListModel listModel;

    private final JButton filterButton;

    private final Map<ComponentType, Collection<ComponentSet>> componentMap;
    private final Collection<ComponentType> allTypes;
    private boolean showUnused;

    public ComponentSetFilter(ThreeComponentViewer parent) {
        super(new BorderLayout());

        this.parent = parent;
        componentMap = new HashMap<>();
        allTypes = ComponentType.orderedList();
        showUnused = false;

        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setCellRenderer(new ComponentTypeCellRenderer());
        list.addListSelectionListener(this);
        list.addMouseListener(new PopupListener());
        list.setVisibleRowCount(10);
        JScrollPane activeListScrollPane = new JScrollPane(list);

        filterButton = new JButton(filterString);
        filterButton.setActionCommand(filterString);
        filterButton.addActionListener(new FilterListener());
        filterButton.setEnabled(false);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.add(filterButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(activeListScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }

    public void addComponentSet(ComponentSet componentSet) {
        ComponentType type = componentSet.getType();
        if (showUnused)
            listModel.removeElement(type);

        insert(type);

        if (!componentMap.containsKey(type)) {
            componentMap.put(type, new ArrayList<ComponentSet>());
        }
        componentMap.get(type).add(componentSet);

        list.setSelectedIndex(0);
    }

    public void clear() {
        listModel.clear();
        if (showUnused) {
            for (ComponentType type : allTypes) listModel.addElement(type);
        }
        componentMap.clear();
    }

    public void updateSelection(JTabbedPane controller) {
        ComponentSetPlotHolder holder = (ComponentSetPlotHolder) controller.getSelectedComponent();
        updateSelection(holder);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting())
            filterButton.setEnabled(list.getSelectedIndex() != -1);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
        updateSelection(tabbedPane);
    }

    private void updateSelection(ComponentSetPlotHolder holder) {
        if (listModel.getSize() > 0) {
            list.clearSelection();

            if (holder != null) {
                ArrayList<Integer> indices = new ArrayList<>();
                for (ComponentType type : holder.getTypes()) {
                    int index = listModel.indexOf(type);
                    if (index == -1) index = insert(type);
                    indices.add(index);
                }

                int[] selection = new int[indices.size()];
                Iterator<Integer> iterator = indices.iterator();
                for (int i = 0; i < selection.length; i++) {
                    selection[i] = iterator.next();
                }
                list.setSelectedIndices(selection);
            }
        }
    }

    private Set<ComponentSet> getSelectedSets() {
        Object[] selectedValues = list.getSelectedValues();
        Set<ComponentSet> selectedSets = new HashSet<>();
        for (int i = 0; i < selectedValues.length; i++) {
            ComponentType type = (ComponentType) selectedValues[i];
            if (componentMap.containsKey(type))
                selectedSets.addAll(componentMap.get(type));
        }
        return selectedSets;
    }

    private Set<ComponentType> getInactiveTypes() {
        Object[] selectedValues = list.getSelectedValues();
        Set<ComponentType> selectedTypes = new HashSet<>();
        for (int i = 0; i < selectedValues.length; i++) {
            ComponentType type = (ComponentType) selectedValues[i];
            if (!componentMap.containsKey(type))
                selectedTypes.add(type);
        }
        return selectedTypes;
    }

    private void filter() {
        parent.updateComponentSets(getSelectedSets(), getInactiveTypes());
    }

    private void filterNewTab() {
        parent.createTab(getSelectedSets(), getInactiveTypes());
    }

    private void toggleUnusedTypes() {
        showUnused = !showUnused;
        if (showUnused) {
            for (ComponentType type : allTypes) {
                if (!componentMap.containsKey(type)) {
                    insert(type);
                }
            }
        } else {
            for (int i = listModel.size() - 1; i >= 0; i--) {
                ComponentType type = (ComponentType) listModel.get(i);
                if (parent.getCurrentHolder() == null ||
                        (!componentMap.containsKey(type) && !parent.getCurrentHolder().hasType(type)))
                    listModel.remove(i);
            }
        }
    }

    private int insert(ComponentType type) {
        boolean active = componentMap.containsKey(type);
        int i = 0;
        boolean lowerPriority = true;
        while (i < listModel.size() && lowerPriority) {
            ComponentType other = (ComponentType) listModel.get(i);

            if (type.equals(other)) {
                return i;
            }

            boolean otherActive = componentMap.containsKey(other);
            if (active == otherActive) {
                int comparison = type.compareTo(other);
                if (comparison == 0) {
                    return i;
                } else if (comparison > 0) {
                    i++;
                } else {
                    lowerPriority = false;
                }
            } else if (otherActive) {
                i++;
            } else {
                lowerPriority = false;
            }
        }

        listModel.add(i, type);
        if (list.isSelectedIndex(i)) list.removeSelectionInterval(i, i);
        return i;
    }


    private class FilterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            filter();
        }
    }


    private class PopupListener implements MouseListener, ActionListener {
        private final JPopupMenu popup;

        private final JMenuItem openCurrentTab;
        private final JMenuItem openNewTab;
        private final JMenuItem toggleUnused;

        public PopupListener() {
            popup = new JPopupMenu();

            openCurrentTab = new JMenuItem("Display in Current Tab");
            openNewTab = new JMenuItem("Display in New Tab");
            toggleUnused = new JMenuItem(getToggleText());

            openCurrentTab.addActionListener(this);
            openNewTab.addActionListener(this);
            toggleUnused.addActionListener(this);

            popup.add(openCurrentTab);
            popup.add(openNewTab);
            popup.add(toggleUnused);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == openCurrentTab) {
                filter();
            } else if (source == openNewTab) {
                filterNewTab();
            } else if (source == toggleUnused) {
                toggleUnusedTypes();
                toggleUnused.setText(getToggleText());
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        private String getToggleText() {
            String text = (showUnused) ? "Hide" : "Show";
            text += " Unused Component Types";
            return text;
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }


    private class ComponentTypeCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            ComponentType type = (ComponentType) value;
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (componentMap.containsKey(type)) {
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            } else {
                c.setFont(c.getFont().deriveFont(Font.PLAIN));
            }

            c.setForeground(type.getForeground());

            return c;
        }
    }
}
