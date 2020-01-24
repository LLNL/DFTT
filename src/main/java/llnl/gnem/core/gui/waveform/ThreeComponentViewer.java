package llnl.gnem.core.gui.waveform;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import llnl.gnem.core.gui.plotting.ZoomType;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.ZoomInStateChange;
import llnl.gnem.core.gui.util.ClosableTabComponent;
import llnl.gnem.core.gui.util.DnDTabbedPane;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.StatefulAction;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.gui.waveform.factory.actions.SaveAction;
import llnl.gnem.core.gui.waveform.factory.commands.ApplyTaperCommand;
import llnl.gnem.core.gui.waveform.factory.commands.DifferentiateCommand;
import llnl.gnem.core.gui.waveform.factory.commands.IntegrateCommand;
import llnl.gnem.core.gui.waveform.factory.commands.RemoveTrendCommand;
import llnl.gnem.core.gui.waveform.factory.commands.RotateToGcpCommand;
import llnl.gnem.core.gui.waveform.factory.commands.TransferCommand;
import llnl.gnem.core.gui.waveform.filterBank.FilterBankModel;
import llnl.gnem.core.gui.waveform.plotPrefs.PlotPresentationPrefs;
import llnl.gnem.core.util.ButtonAction;
import llnl.gnem.core.util.Command;
import llnl.gnem.core.util.CommandManager;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.waveform.components.ComponentSet;
import llnl.gnem.core.waveform.components.ComponentType;

/**
 * Created by dodge1 Date: Mar 19, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public abstract class ThreeComponentViewer extends WaveformViewer<ThreeComponentModel> implements Base3CWaveformDataView {

    private final Collection<SingleComponentOperationAction> actions;
    private final ComponentSetFilter compSetFilter;
    private final StationNavigationModel navigationModel;
    private final JTabbedPane tabbedPane;
    private final ArrayList<ComponentSetPlotHolder> tabs;
    private final Set<ComponentType> viewedTypes;
    private ZoomType zoomType;

    public ThreeComponentViewer(WaveformViewerContainer owner, ThreeComponentModel dataModel, StationNavigationModel navigationModel, PlotPresentationPrefs prefs) {
        super(owner, "Single-Station", dataModel);

        this.navigationModel = navigationModel;
        zoomType = ZoomType.ZOOM_ALL;
        compSetFilter = new ComponentSetFilter(this);
        compSetFilter.setVisible(false);
        add(compSetFilter, BorderLayout.EAST);

        tabbedPane = new DnDTabbedPane() {

            @Override
            public void remove(int index) {
                ComponentSetPlotHolder tab = holderAt(index);
                super.remove(index);
                tabs.remove(tab);
            }

            @Override
            public void setSelectedIndex(int index) {
                super.setSelectedIndex(index);
                holderAt(index).setSelected();
            }
        };
        tabbedPane.addChangeListener(compSetFilter);
        replaceMouseListener(tabbedPane);
        add(tabbedPane, BorderLayout.CENTER);

        actions = new ArrayList<>();
        tabs = new ArrayList<>();
        viewedTypes = new HashSet<>();

        addToolbarAction(new AutoScaleAction());
        ZoomTypeState zoomTypeState = new ZoomTypeState();
        addToolbarAction(zoomTypeState);
        addToolbarAction(new SynchronizeTraceScalesAction(prefs));

        addToolbarAction(new TransferAction());
        addToolbarAction(new RotateToGcpAction());
        addToolbarAction(new IntegrateAction());
        addToolbarAction(new DifferentiateAction());

        getButtonAction(RotateToGcpAction.class).setEnabled(false);
        getButtonAction(TransferAction.class).setEnabled(false);
        getButtonAction(IntegrateAction.class).setEnabled(false);
        getButtonAction(DifferentiateAction.class).setEnabled(false);

        for (ButtonAction action : navigationModel.getActions()) {
            addToolbarAction(action);
        }
        addToolbarAction(SaveAction.getInstance(owner));

        addToolbarAction(new ApplyTaperAction());
        addToolbarAction(new RemoveTrendAction());

        addTraceMenuItem(new OpenFilterBankAction());
    }

    public void setupTabTraversalKeys() {
        setupTabTraversalKeys(tabbedPane);
    }

    private static void setupTabTraversalKeys(JTabbedPane tabbedPane) {
        KeyStroke ctrlTab = KeyStroke.getKeyStroke("ctrl TAB");
        KeyStroke ctrlShiftTab = KeyStroke.getKeyStroke("ctrl shift TAB");

// Remove ctrl-tab from normal focus traversal
        Set<AWTKeyStroke> forwardKeys = new HashSet<>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forwardKeys.remove(ctrlTab);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

// Remove ctrl-shift-tab from normal focus traversal
        Set<AWTKeyStroke> backwardKeys = new HashSet<>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backwardKeys.remove(ctrlShiftTab);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

// Add keys to the tab's input map
        InputMap inputMap = tabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(ctrlTab, "navigateNext");
        inputMap.put(ctrlShiftTab, "navigatePrevious");
    }

    @Override
    public void addComponentSet(ComponentSet set) {
        compSetFilter.addComponentSet(set);

        ComponentType type = set.getType();
        boolean newType = !viewedTypes.contains(type);
        viewedTypes.add(type);

        for (ComponentSetPlotHolder currentTab : tabs) {
            if (currentTab.hasType(type) || (newType && currentTab.pairedWith(type))) {
                ComponentSetPlot plot = currentTab.addComponentSet(set);
                newType = false;

                int index = tabbedPane.indexOfComponent(currentTab);
                if (index == -1) {
                    insertTab(currentTab, set);
                } else {
                    tabbedPane.setTitleAt(index, currentTab.getTitle());
                    addPlot(plot);
                }
            }
        }

        if (newType) {
            ComponentSetPlotHolder tab = new ComponentSetPlotHolder(this, set);
            insertTab(tab, set);
        }

        validate();
    }

    @Override
    public void addToolbar(WaveformViewerToolbar toolbar) {
        super.addToolbar(toolbar);

        // TODO notify toolbar of tab changes (for undo/redo operations, etc.)
        JButton tabManagerButton = new JButton();
        tabManagerButton.addActionListener(new TabManagerListener(tabManagerButton));
        toolbar.addRighthandComponent(tabManagerButton);
    }

    public void addTraceMenuItem(SingleComponentOperationAction action) {
        actions.add(action);
    }

    public void autoScaleTraces() {
        for (ComponentSetPlot plot : getPlots()) {
            plot.scaleAllTraces(true);
            plot.repaint();
        }
        synchronizeTraceScales();
    }

    @Override
    public void clearComponentDisplays() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            ComponentSetPlotHolder holder = holderAt(i);
            for (ComponentSetPlot plot : holder.getPlots()) {
                plot.clear();
            }
        }
        compSetFilter.clear();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            ComponentSetPlotHolder holder = holderAt(i);
            holder.getCommandManager().clear();
            holder.clearComponentDisplays();
        }
        tabbedPane.removeAll();
        navigationModel.clearEvents();

        tabs.clear();
        viewedTypes.clear();

        repaint();
    }

    @Override
    public void componentSetsCompleted() {
        // TODO redundant call, already done in updateComponentSets()
        for (ComponentSetPlotHolder holder : getHolders()) {
            syncTraces(holder);
        }
        updateZoomTypes();
    }

    public void createTab(Set<ComponentSet> selectedSets, Set<ComponentType> inactiveTypes) {
        ComponentSetPlotHolder tab = new ComponentSetPlotHolder(this, selectedSets);
        tab.enableTypes(inactiveTypes);
        tabbedPane.add(tab.getTitle(), tab);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new ClosableTabComponent(tabbedPane));
        tabs.add(tab);
        for (ComponentSet set : selectedSets) {
            addPlot(tab.getPlot(set));
        }
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        tabbedPane.repaint();
    }

    public Collection<ComponentSetPlot> getAllPlots() {
        Collection<ComponentSetPlot> plots = new ArrayList<>();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            plots.addAll(holderAt(i).getPlots());
        }
        return plots;
    }

    public ComponentSetPlotHolder getCurrentHolder() {
        return (ComponentSetPlotHolder) tabbedPane.getSelectedComponent();
    }

    public Collection<ComponentSetPlotHolder> getHolders() {
        Collection<ComponentSetPlotHolder> holders = new ArrayList<>();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            holders.add(holderAt(i));
        }
        return holders;
    }

    @Override
    public Collection<ComponentSetPlot> getPlots() {
        ComponentSetPlotHolder holder = getCurrentHolder();
        if (holder != null) {
            return holder.getPlots();
        } else {
            return new ArrayList<>();
        }
    }

    public ZoomType getZoomType() {
        return zoomType;
    }

    public void setZoomType(ZoomType zoomType) {
        this.zoomType = zoomType;
    }

    public boolean hasSelectedHolder() {
        return getCurrentHolder() != null;
    }

    public ComponentSetPlotHolder holderAt(int i) {
        return (ComponentSetPlotHolder) tabbedPane.getComponentAt(i);
    }

    @Override
    public void magnifyTraces() {
        super.magnifyTraces();
        synchronizeTraceScales();
    }

    @Override
    public void pushCommand(Command command) {
        for (ComponentSetPlotHolder holder : getHolders()) {
            holder.getCommandManager().pushCommand(command);
        }
    }

    @Override
    public void saveCompleted() {
        getButtonAction(SaveAction.class).setEnabled(false);
        for (ComponentSetPlotHolder holder : getHolders()) {
            holder.getCommandManager().clear();
        }
    }

    @Override
    public void saveStarted() {
        getButtonAction(SaveAction.class).setEnabled(false);
    }

    public void setComponent(ComponentSetPlot plot, BaseSingleComponent component) {
        for (ComponentSetPlot aplot : getPlots()) {
            if (plot != aplot) {
                aplot.unselectAll();
            }
        }
    }

    public void syncTraces(ComponentSetPlotHolder holder) {
        synchronizeTraceScales(holder);
    }

    public void synchronizeTraceScales() {
        synchronizeTraceScales(getCurrentHolder());
    }

    public void synchronizeTraceScales(ComponentSetPlotHolder holder) {
        if (!((ButtonAction) getActionMap().get(SynchronizeTraceScalesAction.class)).isSelected()) {
            return;
        }

        double maxY = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        for (ComponentSetPlot plot : holder.getPlots()) {
            for (JSubplot subplot : plot.getSubplotManager().getSubplots()) {
                double myMax = subplot.getYaxis().getMax();
                double myMin = subplot.getYaxis().getMin();
                if (myMax > maxY) {
                    maxY = myMax;
                }
                if (myMin < minY) {
                    minY = myMin;
                }
            }
        }
        for (ComponentSetPlot plot : holder.getPlots()) {
            for (JSubplot subplot : plot.getSubplotManager().getSubplots()) {
                subplot.setYlimits(minY, maxY);
            }
            plot.repaint();
        }
    }

    @Override
    public void unzoomAllTraces() {
        super.unzoomAllTraces();
        synchronizeTraceScales();
    }

    public void unzoomTraces() {
        for (ComponentSetPlot plot : getPlots()) {
            plot.zoomOut();
            plot.repaint();
        }
        synchronizeTraceScales();
    }

    public void updateButtonStates() {
        getActionMap().get(MagnifyAction.class).setEnabled(true);
        getActionMap().get(ReduceAction.class).setEnabled(true);

        navigationModel.updateCurrentEvent();
        getDataModel().updateViewActions();
    }

    public void updateComponentSets(Set<ComponentSet> selectedSets, Set<ComponentType> inactiveTypes) {
        int tabIndex = tabbedPane.getSelectedIndex();
        if (tabIndex != -1) {
            ComponentSetPlotHolder selectedTab = holderAt(tabIndex);
            selectedTab.updateComponentSets(selectedSets);
            selectedTab.enableTypes(inactiveTypes);
            tabbedPane.setTitleAt(tabIndex, selectedTab.getTitle());
        } else {
            createTab(selectedSets, inactiveTypes);
        }
        updateZoomTypes();
    }

    @Override
    public void updateForChangedComponentSets() {
        Collection<? extends ComponentSet> componentSets = getDataModel().getComponentSets();
        compSetFilter.clear();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            holderAt(i).updateForChangedComponentSets();
        }
        for (ComponentSet set : componentSets) {
            addComponentSet(set);
        }
        repaint();
    }

    @Override
    public void updateForChangedData() {
        for (ComponentSetPlot plot : getAllPlots()) {
            plot.updateForChangedData();
        }
        synchronizeTraceScales();
    }

    @Override
    public void updateForComponentRotation(Collection<ComponentSet> rotatable) {
        for (ComponentSetPlot plot : getAllPlots()) {
            plot.updateForComponentRotation(rotatable);
        }
        synchronizeTraceScales();
    }

    @Override
    public void updateForFilterOperation() {
        for (ComponentSetPlot plot : getAllPlots()) {
            plot.updateForFilterOperation();
        }
        synchronizeTraceScales();
    }

    @Override
    public void updateForTransferredComponentSet(ComponentSet set) {
        for (ComponentSetPlot plot : getAllPlots()) {
            plot.updateForTransferredComponentSet();
        }
        synchronizeTraceScales();
    }

    @Override
    public void updateForUndoneTransferOperation() {
        for (ComponentSetPlot plot : getAllPlots()) {
            plot.updateForUndoneTransferOperation();
        }
        synchronizeTraceScales();
    }

    public void updateZoomTypes() {
        for (ComponentSetPlot plot : getPlots()) {
            plot.setZoomType(zoomType);
        }
        synchronizeTraceScales();
    }

    public void zoomTraces(Rectangle zoomBounds) {
        for (ComponentSetPlot plot : getPlots()) {
            plot.zoomToBox(zoomBounds);
            plot.repaint();
        }
        synchronizeTraceScales();
    }

    public void zoomToNewXLimits(ZoomInStateChange zisc) {
//        JMultiAxisPlot initiator = zisc.getInitiator();
//        for (ComponentSetPlot plot : getPlots()) {
//            if (plot == initiator) {
//                if (plot.getSubplotManager().getNumVisibleSubplots() == 1) {
//                    plot.zoomToBox(zisc.getZoomBounds());
//                } else {
//                    plot.zoomToNewLimits(zisc.getZoomLimits());
//                }
//            } else {
//                plot.zoomToNewXLimits(zisc.getRealWorldXMin(), zisc.getRealWorldXMax());
//            }
//            plot.repaint();
//        }
//        synchronizeTraceScales();
    }

    private MouseListener findUIMouseListener(JComponent tabbedPane) {
        MouseListener[] listeners = tabbedPane.getMouseListeners();
        for (MouseListener l : listeners) {
            if (l.getClass().getName().contains("$Handler")) {
                return l;
            }
        }
        return null;
    }

    private void replaceMouseListener(JTabbedPane tabbedPane) {
        MouseListener handler = findUIMouseListener(tabbedPane);
        if (handler != null) {
            tabbedPane.removeMouseListener(handler);
            tabbedPane.addMouseListener(new MouseListenerWrapper(handler));
        }
    }

    protected void addComponentSetToFilter(ComponentSet componentSet) {
        if (this.compSetFilter == null) {
            return;
        }
        compSetFilter.addComponentSet(componentSet);
    }

    protected void addPlot(ComponentSetPlot plot) {
        for (SingleComponentOperationAction action : actions) {
            plot.addTraceMenuItem(action);
        }
        getDataModel().getPickManager().addSinglePlot(plot);

        // TODO we really only want to do this at the end of the plot adding process, but since
        // there's no method call for that at present, we'll make do with the performance hit
        compSetFilter.updateSelection(tabbedPane);
        updateZoomTypes();
    }

    @Override
    protected CommandManager getCommandManager() {
        return getCurrentHolder().getCommandManager();
    }

    protected void setDisplayedTabs(Collection<ComponentSetPlotHolder> tabsToShow) {
        tabs.clear();
        viewedTypes.clear();
        tabbedPane.removeAll();

        for (ComponentSetPlotHolder tab : tabsToShow) {
            final ArrayList<ComponentSetPlot> plots = tab.getPlots();
            if (plots.size() > 0) {
                insertTab(tab, plots.iterator().next().getSet());
            }
        }

        compSetFilter.updateSelection(tabbedPane);
    }

    protected ArrayList<ComponentSetPlotHolder> getTabs() {
        // give callers access to the tabs, but no modification rights to actual collection
        return new ArrayList<>(tabs);
    }

    protected void insertTab(ComponentSetPlotHolder tab, ComponentSet set) {
        int i = 0;
        while (i < tabbedPane.getTabCount() && tab.getHighestType().compareTo(holderAt(i).getHighestType()) >= 0) {
            i++;
        }
        tabbedPane.insertTab(tab.getTitle(), null, tab, null, i);
        tabbedPane.setTabComponentAt(i, new ClosableTabComponent(tabbedPane));
        tabbedPane.setSelectedIndex(0);

        addPlot(tab.getPlot(set));
        tabs.add(tab);
    }

    protected boolean isTabCurrentlyDisplayed(ComponentSetPlotHolder tab) {
        if (tab == null) {
            return false;
        }

        return tabbedPane.indexOfComponent(tab) != -1;
    }

    /**
     * @return the tabbedPane
     */
    protected JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public static class MouseListenerWrapper implements MouseListener {

        private final MouseListener delegate;

        public MouseListenerWrapper(MouseListener delegate) {
            this.delegate = delegate;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            delegate.mouseClicked(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                return;
            }
            delegate.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            delegate.mouseReleased(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            delegate.mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            delegate.mouseExited(e);
        }
    }

    public class ApplyTaperAction extends ButtonAction {

        private ApplyTaperAction() {
            super("Apply taper", Utility.getIcon(ThreeComponentViewer.this, "miscIcons/applyTaper32.gif"));
            putValue(SHORT_DESCRIPTION, "Apply taper to trace.");
            //putValue(MNEMONIC_KEY, KeyEvent.VK_Y);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                ApplyTaperCommand cmd = new ApplyTaperCommand(getDataModel());
                invoke(cmd);
            } catch (Exception e1) {
                ExceptionDialog.displayError(e1);
            }
        }

    }

    public class AutoScaleAction extends ButtonAction {

        public AutoScaleAction() {
            super("Auto-Scale", Utility.getIcon(ThreeComponentViewer.this, "miscIcons/fitInWindow32.gif"));
            putValue(SHORT_DESCRIPTION, "Apply default scale factors to all traces.");
            //putValue(MNEMONIC_KEY, KeyEvent.VK_S);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            autoScaleTraces();
        }
    }

    @SuppressWarnings({"NonThreadSafeLazyInitialization"})
    public class DifferentiateAction extends ButtonAction {

        private DifferentiateAction() {
            super("", Utility.getIcon(ThreeComponentViewer.this, "miscIcons/differentiate32.gif"));
            putValue(NAME, "Differentiate");
            putValue(SHORT_DESCRIPTION, "Differentiate data");
            //putValue(MNEMONIC_KEY, KeyEvent.VK_D);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Command cmd = new DifferentiateCommand(getDataModel());
            invoke(cmd);
        }
    }

    @SuppressWarnings({"NonThreadSafeLazyInitialization"})
    public class IntegrateAction extends ButtonAction {

        private IntegrateAction() {
            super("", Utility.getIcon(ThreeComponentViewer.this, "miscIcons/integrate32.gif"));
            putValue(NAME, "Integrate");
            putValue(SHORT_DESCRIPTION, "Integrate data");
            //putValue(MNEMONIC_KEY, KeyEvent.VK_I);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Command cmd = new IntegrateCommand(getDataModel());
            invoke(cmd);
        }

    }

    public class OpenFilterBankAction extends SingleComponentOperationAction {

        private OpenFilterBankAction() {
            super("FilterBank", Utility.getIcon(ThreeComponentViewer.this, "miscIcons/filterBank32.gif"));
            putValue(SHORT_DESCRIPTION, "Display Filter Bank.");
            //putValue(MNEMONIC_KEY, KeyEvent.VK_F);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (this.component != null) {
                    FilterBankModel.getInstance().setComponent(component);
                    FilterBankModel.getInstance().setActive(true);
                }
            } catch (Exception e1) {
                ExceptionDialog.displayError(e1);
            }
        }
    }

    public class RemoveTrendAction extends ButtonAction {

        private RemoveTrendAction() {
            super("Remove Trend", Utility.getIcon(ThreeComponentViewer.this, "miscIcons/rtrend32.gif"));
            putValue(SHORT_DESCRIPTION, "Remove trend from trace.");
            //putValue(MNEMONIC_KEY, KeyEvent.VK_T);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Command cmd = new RemoveTrendCommand(getDataModel());
                invoke(cmd);
            } catch (Exception e1) {
                ExceptionDialog.displayError(e1);
            }
        }
    }

    @SuppressWarnings({"NonThreadSafeLazyInitialization"})
    public class RotateToGcpAction extends ButtonAction {

        private RotateToGcpAction() {
            super("", Utility.getIcon(ThreeComponentViewer.this, "miscIcons/rotate32.gif"));
            putValue(NAME, "Rotate");
            putValue(SHORT_DESCRIPTION, "Rotate horizontal components to GCP");
            //putValue(MNEMONIC_KEY, KeyEvent.VK_R);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Command cmd = new RotateToGcpCommand(getDataModel());
            invoke(cmd);
        }
    }

    @SuppressWarnings({"NonThreadSafeLazyInitialization"})
    public class SynchronizeTraceScalesAction extends StatefulAction<SyncScaleState> {

        private PlotPresentationPrefs prefs;

        public SynchronizeTraceScalesAction(PlotPresentationPrefs prefs) {
            super(SyncScaleState.OFF, "Syncronize Scales", ThreeComponentViewer.this);
            //putValue(MNEMONIC_KEY, KeyEvent.VK_T);
            setPlotPrefs(prefs);
        }

        public final void setPlotPrefs(PlotPresentationPrefs prefs) {
            this.prefs = prefs;
            AbstractButton button = getButton();
            button.setVisible(true);
            if (prefs.isPlotTracesAtSameScale()) {
                button.doClick();
            }
        }

        @Override
        protected void handleStateChange(SyncScaleState state) {
            prefs.setPlotTracesAtSameScale(getButton().isSelected());
            synchronizeTraceScales();
        }

        @Override
        protected SyncScaleState getNext() {
            return getCurrentState().getNext();
        }

        @Override
        protected void createButton() {
            AbstractButton button = new JToggleButton(this);
            button.setVisible(false);
            setButton(button);
        }
    }

    @SuppressWarnings({"NonThreadSafeLazyInitialization"})
    public class TransferAction extends ButtonAction {

        private TransferAction() {
            super("", Utility.getIcon(ThreeComponentViewer.this, "miscIcons/removeInstrument32.gif"));
            putValue(NAME, "Transfer");
            putValue(SHORT_DESCRIPTION, "Remove instrument response from all components");
            //putValue(MNEMONIC_KEY, KeyEvent.VK_T);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Command cmd = new TransferCommand(getDataModel());
            invoke(cmd);
        }
    }

    public class ZoomTypeState extends ButtonAction {

        private final Preferences prefs;
        private String description;

        public ZoomTypeState() {
            super("Set Zoom-Type", Utility.getIcon(ThreeComponentViewer.this, "miscIcons/boxzoom32.gif"));

            prefs = Preferences.userNodeForPackage(this.getClass());

            String type = prefs.get("ZOOM_TYPE", ZoomType.ZOOM_BOX.toString());
            ImageIcon icon;
            if (type.equals(ZoomType.ZOOM_BOX.toString())) {
                setZoomType(ZoomType.ZOOM_BOX);
                description = "Click to change to Zoom-All";
                icon = Utility.getIcon(ThreeComponentViewer.this, "miscIcons/boxzoom32.gif");
            } else {
                setZoomType(ZoomType.ZOOM_ALL);
                description = "Click to change to Box-Zoom";
                icon = Utility.getIcon(ThreeComponentViewer.this, "miscIcons/zoomall32.gif");
            }
            putValue(SHORT_DESCRIPTION, description);
            putValue("SMALL_ICON", icon);
            getButton().setIcon(icon);
            getButton().setToolTipText(description);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source instanceof JButton) {
                JButton button = (JButton) source;
                String type = prefs.get("ZOOM_TYPE", ZoomType.ZOOM_BOX.toString());
                ImageIcon icon;
                if (type.equals(ZoomType.ZOOM_BOX.toString())) {
                    setZoomType(ZoomType.ZOOM_ALL);
                    description = "Click to change to Box-Zoom";
                    icon = Utility.getIcon(this, "miscIcons/zoomall32.gif");
                } else {
                    setZoomType(ZoomType.ZOOM_BOX);
                    description = "Click to change to Zoom-All";
                    icon = Utility.getIcon(this, "miscIcons/boxzoom32.gif");
                }
                putValue(SHORT_DESCRIPTION, description);
                putValue("SMALL_ICON", icon);
                button.setIcon(icon);
                button.setToolTipText(description);
                prefs.put("ZOOM_TYPE", zoomType.toString());
                updateZoomTypes();
            }
        }
    }

    class TabManagerListener implements ActionListener {

        private static final String showString = "Show Band Manager";
        private static final String hideString = "Hide Band Manager";
        private final JButton parent;

        public TabManagerListener(JButton parent) {
            this.parent = parent;
            setText();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            compSetFilter.setVisible(!compSetFilter.isVisible());
            setText();
        }

        private void setText() {
            String actionString = compSetFilter.isVisible() ? hideString : showString;
            parent.setText(actionString);
            parent.setActionCommand(actionString);
        }
    }
}
