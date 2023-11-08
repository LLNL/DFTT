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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import llnl.gnem.dftt.core.gui.plotting.ZoomType;
import llnl.gnem.dftt.core.gui.util.MultiSplitPane;
import llnl.gnem.dftt.core.util.CommandManager;
import llnl.gnem.dftt.core.waveform.components.ComponentSet;
import llnl.gnem.dftt.core.waveform.components.ComponentType;

/**
 * Created by dodge1 Date: Apr 2, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ComponentSetPlotHolder extends JPanel {

    protected final ThreeComponentViewer parent;
    private final Set<ComponentType> enabledTypes;
    private final ArrayList<ComponentSetPlot> enabledPlots;
    private final Map<ComponentSet, ComponentSetPlot> componentSetToPlotMap;
    private final CommandManager commandManager;

    public ComponentSetPlotHolder(ThreeComponentViewer parent) {
        super(new BorderLayout());
        this.parent = parent;

        enabledTypes = new HashSet<>();
        enabledPlots = new ArrayList<>();
        componentSetToPlotMap = new HashMap<>();
        commandManager = new CommandManager();
        commandManager.registerRedoAction(parent.getRedoAction());
        commandManager.registerUndoAction(parent.getUndoAction());
    }

    public ComponentSetPlotHolder(ThreeComponentViewer parent, ComponentSet set) {
        this(parent);
        addComponentSet(set);
    }

    public ComponentSetPlotHolder(ThreeComponentViewer parent, Set<ComponentSet> sets) {
        this(parent);
        updateComponentSets(sets);
    }
    
    public void setZoomType( ZoomType zoomType)
    {
        for( ComponentSetPlot plot : enabledPlots ){
            plot.setZoomType(zoomType);
        }
        for( ComponentSetPlot plot : componentSetToPlotMap.values()){
            plot.setZoomType(zoomType);
        }
    }

    public ComponentSetPlot addComponentSet(ComponentSet set) {
        List<JComponent> selectedPlots = new ArrayList<>();
        selectedPlots.addAll(enabledPlots);
        ComponentSetPlot plot = enablePlot(set);
        selectedPlots.add(plot);
        displayPlots(selectedPlots);
        return plot;
    }

    public void updateComponentSets(Set<ComponentSet> selectedSets) {
        enabledTypes.clear();
        enabledPlots.clear();

        ArrayList<ComponentSet> selection = new ArrayList<>();
        selection.addAll(selectedSets);
        Collections.sort(selection);

        ArrayList<JComponent> plots = new ArrayList<>();
        for (ComponentSet set : selection) {
            plots.add(enablePlot(set));
        }
        displayPlots(plots);
        parent.syncTraces(this);
        repaint();
    }

    public void setSelected() {
        commandManager.updateActions();
    }

    public void enableTypes(Set<ComponentType> types) {
        for (ComponentType type : types) {
            enabledTypes.add(type);
        }
    }

    public void updateForChangedComponentSets() {
        removeSelectedComponents();
        enabledPlots.clear();
    }

    public void updateButtonStates() {
        parent.updateButtonStates();
    }

    public void clearComponentDisplays() {
        removeSelectedComponents();
        enabledPlots.clear();
        componentSetToPlotMap.clear();
    }

    public ThreeComponentModel getDataModel() {
        return parent.getDataModel();
    }

    public ThreeComponentViewer getPlotContainer() {
        return parent;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ComponentSetPlot getPlot(ComponentSet set) {
        return componentSetToPlotMap.get(set);
    }

    public ArrayList<ComponentSetPlot> getPlots() {
        // Outside users can access the plots and manipulate them, but not change which plots are
        // displayed
        return (ArrayList<ComponentSetPlot>) enabledPlots.clone();
    }

    public boolean hasType(ComponentType type) {
        return enabledTypes.contains(type);
    }

    public boolean pairedWith(ComponentType pair) {
        if (enabledTypes.size() == 1) {
            for (ComponentType type : enabledTypes) {
                return type.pairedWith(pair);
            }
        }
        return false;
    }

    public Collection<ComponentType> getTypes() {
        return enabledTypes;
    }

    public ComponentType getHighestType() {
        if (enabledTypes.isEmpty()) {
            return null;
        }

        List<ComponentType> types = new ArrayList<>();
        types.addAll(enabledTypes);
        Collections.sort(types);
        return types.get(0);
    }

    public String getTitle() {
        List<ComponentType> types = new ArrayList<>(enabledTypes);
        Collections.sort(types);
        String title = "";
        String delim = "";
        for (ComponentType type : types) {
            title += delim + type.toString();
            delim = ", ";
        }
        return title;
    }

    @Override
    public String toString() {
        List<ComponentType> types = new ArrayList<>(enabledTypes);
        Collections.sort(types);
        String title = "<html>";
        String delim = "";
        for (ComponentType type : types) {
            String typeString = type.toString();
            title += delim + typeString;
            delim = ", ";
        }
        title += "</html>";
        return title;
    }

    @Override
    public int getHeight() {
        int height = super.getHeight();
        if (height <= 0) {
            ComponentSetPlotHolder current = parent.getCurrentHolder();
            if (current != null && current != this) {
                height = parent.getCurrentHolder().getHeight();
            }
        }
        return height;
    }

    private ComponentSetPlot enablePlot(ComponentSet set) {
        enabledTypes.add(set.getType());
        ComponentSetPlot plot = componentSetToPlotMap.containsKey(set)
                ? componentSetToPlotMap.get(set) : createPlot(set);
        enabledPlots.add(plot);
        plot.setZoomType(parent.getZoomType());
        return plot;
    }

    private ComponentSetPlot createPlot(ComponentSet set) {
        ComponentSetPlot plot = set.plotFor(this);
        plot.plot();
        componentSetToPlotMap.put(set, plot);
        return plot;
    }

    private void removeSelectedComponents() {
        java.awt.Component[] components = getComponents();
        for (java.awt.Component component : components) {
            remove(component);
        }
        repaint();
    }

    private void displayPlots(List<JComponent> plots) {
        removeSelectedComponents();
        add(MultiSplitPane.createMultiSplitPane(plots, getHeight()), BorderLayout.CENTER);
    }
}
