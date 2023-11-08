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
package llnl.gnem.apps.detection.sdBuilder.waveformViewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import llnl.gnem.apps.detection.classify.TriggerClassification;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Detection;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;

import llnl.gnem.apps.detection.sdBuilder.ChannelCombo;
import llnl.gnem.apps.detection.sdBuilder.DetectionWaveforms;
import llnl.gnem.apps.detection.sdBuilder.actions.CreateSacfilesAction;
import llnl.gnem.apps.detection.sdBuilder.actions.NextCorrelationAction;
import llnl.gnem.apps.detection.sdBuilder.actions.PreviousCorrelationAction;
import llnl.gnem.apps.detection.sdBuilder.actions.RevertAction;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.ArrayDisplayFrame;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.ArrayDisplayModel;
import llnl.gnem.apps.detection.sdBuilder.configuration.DetectorCreationEnabler;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.sdBuilder.DetectorCreationWorker;
import llnl.gnem.apps.detection.sdBuilder.actions.BuildStackBeamAction;
import llnl.gnem.apps.detection.sdBuilder.actions.CreateTemplateAction;
import llnl.gnem.apps.detection.sdBuilder.picking.DeleteAllPicksAction;
import llnl.gnem.apps.detection.sdBuilder.actions.DeleteDisplayedDetectionsAction;
import llnl.gnem.apps.detection.sdBuilder.actions.FirstCorrelationAction;
import llnl.gnem.apps.detection.sdBuilder.actions.LastCorrelationAction;
import llnl.gnem.apps.detection.sdBuilder.actions.ShowSNRWindowsAction;
import llnl.gnem.apps.detection.sdBuilder.actions.SortBySNRAction;
import llnl.gnem.apps.detection.sdBuilder.picking.SavePicksWorker;
import llnl.gnem.apps.detection.sdBuilder.allStations.SeismogramModel;
import llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay.SingleDetectionDisplayFrame;
import llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay.SingleDetectionModel;
import llnl.gnem.apps.detection.sdBuilder.picking.DetectionPhasePickModel;
import llnl.gnem.apps.detection.sdBuilder.picking.PredictedPhasePick;
import llnl.gnem.apps.detection.sdBuilder.picking.PredictedPhasePickModel;

import llnl.gnem.apps.detection.sdBuilder.stackViewer.SingleComponentStack;
import llnl.gnem.apps.detection.sdBuilder.stackViewer.StackModel;
import llnl.gnem.dftt.core.correlation.CorrelationComponent;
import llnl.gnem.dftt.core.correlation.clustering.ClusterResult;
import llnl.gnem.dftt.core.correlation.clustering.GroupData;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.gui.plotting.MouseMode;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.waveform.filter.FilterClient;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;
import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class CorrelatedTracesModel implements FilterClient {
    
    private static final double TAPER_PERCENT = 5.0;
    private int detectorid;
    private final Collection<SeismogramViewer> viewers;
    // Each DetectionWaveform has all channels for a single detection.
    private final Collection<DetectionWaveforms> myWaveforms;
    private final Map<CorrelationComponent, DetectionWaveforms> visibleComponents;
    private final Map<CorrelationComponent, DetectionWaveforms> hiddenComponents;
    private int elementIndex = 0;
    private GroupData currentGroup;
    private final ArrayList<GroupData> allGroups;
    private int currentGroupIndex;
    private int runid;
    private final Map<Integer, TriggerClassification> triggerClassificationMap;
    private int lastDetectionId;
    private int totalRowsRetrieved;
    private int totalDetectionCount;
    private int configid;
    
    public GroupData getCurrent() {
        return currentGroup;
    }
    
    public int getConfigid() {
        return configid;
    }
    
    public static double getSeismogramTaperPercent() {
        return TAPER_PERCENT;
    }
    
    public boolean isCanAdvance() {
        return totalRowsRetrieved < totalDetectionCount && ParameterModel.getInstance().isRetrieveByBlocks();
    }
    
    public Collection<CorrelationComponent> getComponents() {
        return new ArrayList<>(visibleComponents.keySet());
    }
    
    public Map<CorrelationComponent, DetectionWaveforms> getComponentMap() {
        return new HashMap<>(visibleComponents);
    }
    
    public Collection<GroupData> getGroups() {
        return new ArrayList<>(allGroups);
    }
    
    private CorrelatedTracesModel() {
        myWaveforms = new ArrayList<>();
        visibleComponents = new TreeMap<>();
        hiddenComponents = new TreeMap<>();
        allGroups = new ArrayList<>();
        triggerClassificationMap = new HashMap<>();
        viewers = new ArrayList<>();
        DetectorCreationEnabler.getInstance().setWaveformsAvailable(false);
        detectorid = -1;
        lastDetectionId = 0;
        totalRowsRetrieved = 0;
        configid = -1;
        currentGroup = null;
    }

    /*
    This method is called by called when the user wishes to undo the clusters and return to the original view of the data.
     */
    public void revert() {
        allGroups.clear();
        currentGroup = null;
        setTraces(new ArrayList<>(), new HashMap<>(), new ArrayList<>(), new ArrayList<>());
        
        for (CorrelationComponent cc : visibleComponents.keySet()) {
            cc.setCorrelation(0);
            cc.setShift(0);
            cc.setStd(0);
        }
        ParameterModel.getInstance().setAutoZoomEnabled(false);
        clearAllViewers();
        dataWereLoaded(false);
        DetectorCreationEnabler.getInstance().setWaveformsAvailable(!myWaveforms.isEmpty());
        disableActions();
    }
    
    public static CorrelatedTracesModel getInstance() {
        return CorrelatedTracesModelHolder.INSTANCE;
    }
    
    public void setDetectorid(int detectorid) {
        if (this.detectorid > 0 && this.detectorid != detectorid) {
            lastDetectionId = 0;
            totalRowsRetrieved = 0;
        }
        this.detectorid = detectorid;
        
    }
    
    public void setLastDetectionId(int lastDetectionId) {
        this.lastDetectionId = lastDetectionId;
    }
    
    public int getTotalRowsRetrieved() {
        return totalRowsRetrieved;
    }
    
    public int getLastDetectionId() {
        return lastDetectionId;
    }
    
    public void setTraces(Collection<DetectionWaveforms> retrieved,
            Map<Integer, TriggerClassification> triggerClassificationMap,
            Collection<PhasePick> detectionPicks,
            Collection<PredictedPhasePick> predictedPicks) {
        currentGroup = null;
        myWaveforms.addAll(retrieved);
        triggerClassificationMap.clear();
        triggerClassificationMap.putAll(triggerClassificationMap);
        ParameterModel.getInstance().setWindowStart(0.0);
        DetectionPhasePickModel.getInstance().addExistingPicks(retrieved, detectionPicks);
        PredictedPhasePickModel.getInstance().addExistingPicks(retrieved, predictedPicks);
        populateChannelCombo();
        ParameterModel.getInstance().setAutoZoomEnabled(false);
        displayTraces();
        if (ParameterModel.getInstance().isAutoApplyFilter()) {
            try {
                StoredFilter filter = DetectionDAOFactory.getInstance().getStreamDAO().getStreamFilter(detectorid);
                applyFilter(filter);
                ClusterBuilderFrame.getInstance().setSelectedFilter(filter);
            } catch (DataAccessException ex) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Failed to retrieve stored filter from database!");
            }
        }
        CreateTemplateAction.getInstance(this).setEnabled(true);
        ShowSNRWindowsAction.getInstance(this).setEnabled(true);
        SortBySNRAction.getInstance(this).setEnabled(true);
        DeleteAllPicksAction.getInstance(this).setEnabled(true);
        DeleteDisplayedDetectionsAction.getInstance(this).setEnabled(true);
        
        DetectorCreationEnabler.getInstance().setWaveformsAvailable(!retrieved.isEmpty());
        setFKActions();
    }
    
    private void setFKActions() {
        for (DetectionWaveforms dw : myWaveforms) {
            ArrayList<CorrelationComponent> acc = dw.getSegments();
            for (CorrelationComponent cc : acc) {
                Double dnorth = cc.getDnorth();
                BuildStackBeamAction.getInstance(this).setEnabled(dnorth != null);
                return;
            }
        }
    }
    
    private void populateChannelCombo() {
        Collection<StreamKey> channels = getDataChannels();
        ChannelCombo.getInstance().enableActionListener(false);
        ChannelCombo.getInstance().removeAllItems();
        for (StreamKey chan : channels) {
            ChannelCombo.getInstance().addItem(chan);
        }
        if (channels.isEmpty()) {
            ChannelCombo.getInstance().addItem("NONE");
        }
        elementIndex = getBestDisplayableChannel();
        
        ChannelCombo.getInstance().setSelectedIndex(elementIndex);
        ChannelCombo.getInstance().revalidate();
        ChannelCombo.getInstance().enableActionListener(true);
    }
    
    private void displayTraces() {
        visibleComponents.clear();
        resampleData();
        conditionData();
        for (DetectionWaveforms dw : myWaveforms) {
            CorrelationComponent cc = dw.getSegments().get(elementIndex);
            visibleComponents.put(cc, dw);
        }
        
        dataWereLoaded(false);
        DetectorCreationEnabler.getInstance().hasBeenCorrelated(false);
        disableActions();
        CreateSacfilesAction.getInstance(this).setEnabled(true);
    }
    
    public TriggerClassification getTriggerClassification(int triggerid) {
        TriggerClassification result = triggerClassificationMap.get(triggerid);
        return result != null ? result : TriggerClassification.UNSET;
    }
    
    public TriggerClassification getCommonTriggerClassification() {
        if (triggerClassificationMap.isEmpty()) {
            return TriggerClassification.UNSET;
        } else {
            return triggerClassificationMap.values().iterator().next();
        }
    }
    
    public Collection<CorrelationComponent> getMatchingTraces() {
        return new ArrayList<>(visibleComponents.keySet());
    }
    
    public void clear() {
        myWaveforms.clear();
        visibleComponents.clear();
        hiddenComponents.clear();
        DetectionPhasePickModel.getInstance().clear();
        PredictedPhasePickModel.getInstance().clear();
        clearAllViewers();
        
        allGroups.clear();
        ParameterModel.getInstance().setWindowStart(0.0);
        NextCorrelationAction.getInstance(this).setEnabled(false);
        PreviousCorrelationAction.getInstance(this).setEnabled(false);
        LastCorrelationAction.getInstance(this).setEnabled(false);
        FirstCorrelationAction.getInstance(this).setEnabled(false);
        
        CreateTemplateAction.getInstance(this).setEnabled(false);
        ShowSNRWindowsAction.getInstance(this).setEnabled(false);
        SortBySNRAction.getInstance(this).setEnabled(false);
        DeleteAllPicksAction.getInstance(this).setEnabled(false);
        DeleteDisplayedDetectionsAction.getInstance(this).setEnabled(false);
         
        CreateSacfilesAction.getInstance(this).setEnabled(false);
        
        DetectorCreationEnabler.getInstance().setWaveformsAvailable(false);
        SeismogramModel.getInstance().clear();
     }

    //Called when user asks to hide detection without deletion from DB
    public void removeComponent(CorrelationComponent selectedComponent) {
        DetectionWaveforms dw = visibleComponents.remove(selectedComponent);
        if (dw != null) {
            myWaveforms.remove(dw);
        }
        dw = hiddenComponents.remove(selectedComponent);
        if (dw != null) {
            myWaveforms.remove(dw);
        }
        maybeDeleteFromCurrentGroup(selectedComponent);
        dataWereLoaded(false);
    }
    
    public void removeMultipleComponents(Collection<CorrelationComponent> ccs) {
        for (CorrelationComponent cc : ccs) {
            DetectionWaveforms dw = visibleComponents.remove(cc);
            if (dw != null) {
                myWaveforms.remove(dw);
            }
            dw = hiddenComponents.remove(cc);
            if (dw != null) {
                myWaveforms.remove(dw);
            }
            maybeDeleteFromCurrentGroup(cc);
        }
        dataWereLoaded(false);
    }
    
    public void detectionWasDeleted(int detectionid) {
        
        Collection<CorrelationComponent> deleteThese = new ArrayList<>();
        visibleComponents.keySet().stream().filter((cc) -> (cc.getEvent().getEvid() == detectionid)).forEachOrdered((cc) -> {
            deleteThese.add(cc);
        });
        detectionsWereDeleted(deleteThese);
    }
    
    public void detectionsWereDeleted(Collection<CorrelationComponent> deleteThese) {
        
        for (CorrelationComponent cc : deleteThese) {
            DetectionWaveforms dw = visibleComponents.get(cc);
            if (dw != null) {
                myWaveforms.remove(dw);
            }
            visibleComponents.remove(cc);
            hiddenComponents.remove(cc);
            totalDetectionCount--;
            maybeDeleteFromCurrentGroup(cc);
        }
        
        dataWereLoaded(false);
        
    }
    
    void setViewer(SeismogramViewer viewer) {
        viewers.add(viewer);
    }
    
    public void previousGroup() {
        if (currentGroupIndex > 0) {
            --currentGroupIndex;
            NextCorrelationAction.getInstance(this).setEnabled(currentGroupIndex < allGroups.size() - 1);
            PreviousCorrelationAction.getInstance(this).setEnabled(currentGroupIndex > 0);
            LastCorrelationAction.getInstance(this).setEnabled(currentGroupIndex < allGroups.size() - 1);
            FirstCorrelationAction.getInstance(this).setEnabled(currentGroupIndex > 0);
            resetForNewGroup();
        }
    }
    
    public void nextGroup() {
        if (currentGroupIndex < allGroups.size() - 1) {
            ++currentGroupIndex;
            NextCorrelationAction.getInstance(this).setEnabled(currentGroupIndex < allGroups.size() - 1);
            PreviousCorrelationAction.getInstance(this).setEnabled(currentGroupIndex > 0);
            LastCorrelationAction.getInstance(this).setEnabled(currentGroupIndex < allGroups.size() - 1);
            FirstCorrelationAction.getInstance(this).setEnabled(currentGroupIndex > 0);
            resetForNewGroup();
        }
    }
    
    void updateFromClusterResult(ClusterResult cr) {
        allGroups.clear();
        allGroups.addAll(cr.getGroups());
        NextCorrelationAction.getInstance(this).setEnabled(false);
        PreviousCorrelationAction.getInstance(this).setEnabled(false);
        LastCorrelationAction.getInstance(this).setEnabled(false);
        FirstCorrelationAction.getInstance(this).setEnabled(false);
        if (allGroups.isEmpty()) {
            updateForFailedCorrelation();
            return;
        }
        
        currentGroupIndex = 0;
        NextCorrelationAction.getInstance(this).setEnabled(currentGroupIndex < allGroups.size() - 1);
        PreviousCorrelationAction.getInstance(this).setEnabled(currentGroupIndex > 0);
        LastCorrelationAction.getInstance(this).setEnabled(currentGroupIndex < allGroups.size() - 1);
        FirstCorrelationAction.getInstance(this).setEnabled(currentGroupIndex > 0);
        
        DetectorCreationEnabler.getInstance().hasBeenCorrelated(true);
        RevertAction.getInstance(this).setEnabled(true);
        resetForNewGroup();
    }
    
    private void resetForNewGroup() {
        currentGroup = allGroups.get(currentGroupIndex);
        hiddenComponents.putAll(visibleComponents);
        visibleComponents.clear();
        Collection<CorrelationComponent> tmp = currentGroup.getAssociatedInfo();
        for (CorrelationComponent cc : tmp) {
            DetectionWaveforms dw = hiddenComponents.get(cc);
            visibleComponents.put(cc, dw);
        }
        
        loadClusterResult();
        
        DetectorCreationEnabler.getInstance().hasBeenCorrelated(true);
    }
    
    @Override
    public void applyFilter(StoredFilter filter) {
        ArrayList<CorrelationComponent> allComponents = getAllComponents();
        new ApplyFilterWorker(allComponents, filter, viewers).execute();
    }
    
    @Override
    public void unApplyFilter() {
        for (DetectionWaveforms dw : myWaveforms) {
            for (CorrelationComponent cc : dw.getSegments()) {
                cc.unApplyFilter();
            }
        }
        updateForChangedTrace();
    }
    
    private void resampleData() {
        double maxRate = -Double.MAX_VALUE;
        for (DetectionWaveforms dw : myWaveforms) {
            for (CorrelationComponent comp : dw.getSegments()) {
                double thisRate = comp.getSeismogram().getSamprate();
                if (thisRate > maxRate) {
                    maxRate = thisRate;
                }
            }
        }
        double newRate = Math.round(maxRate * 1000) / 1000.0;
        for (DetectionWaveforms dw : myWaveforms) {
            for (CorrelationComponent comp : dw.getSegments()) {
                comp.resample(newRate);
            }
        }
    }
    
    private void conditionData() {
        for (DetectionWaveforms dw : myWaveforms) {
            for (CorrelationComponent comp : dw.getSegments()) {
                comp.removeTrend();
                comp.applyTaper(TAPER_PERCENT);
            }
        }
    }
    
    private void disableActions() {
        NextCorrelationAction.getInstance(this).setEnabled(false);
        PreviousCorrelationAction.getInstance(this).setEnabled(false);
        LastCorrelationAction.getInstance(this).setEnabled(false);
        FirstCorrelationAction.getInstance(this).setEnabled(false);
        
        RevertAction.getInstance(this).setEnabled(false);
    }
    
    public void writeNewDetector() throws Exception {
        setMouseMode(MouseMode.SELECT_REGION);
        new DetectorCreationWorker().execute();
    }
    
    public int getCurrentDetectorid() {
        return detectorid;
    }

    /**
     * @return the runid
     */
    public int getRunid() {
        return runid;
    }

    /**
     * @param runid the runid to set
     */
    public void setRunid(int runid) {
        this.runid = runid;
    }
    
    public Map<Long, ArrayList<CssSeismogram>> getDetectionSeismogramListMap() {
        Map<Long, ArrayList<CssSeismogram>> result = new HashMap<>();
        for (DetectionWaveforms dw : myWaveforms) {
            ArrayList<CorrelationComponent> comps = dw.getSegments();
            for (CorrelationComponent comp : comps) {
                long detectionid = comp.getEvent().getEvid();
                CssSeismogram seis = comp.getSeismogram();
                ArrayList<CssSeismogram> seismograms = result.get(detectionid);
                if (seismograms == null) {
                    seismograms = new ArrayList<>();
                    result.put(detectionid, seismograms);
                }
                seismograms.add(seis);
            }
        }
        return result;
    }
    
    public Collection<StreamKey> getDataChannels() {
        Collection<StreamKey> result = new ArrayList<>();
        if (myWaveforms.isEmpty()) {
            return result;
        }
        
        for (DetectionWaveforms dw : myWaveforms) {
            ArrayList<CorrelationComponent> segments = dw.getSegments();
            for (CorrelationComponent cc : segments) {
                StreamKey key = cc.getSeismogram().getStreamKey();
                result.add(key);
            }
            return result;
        }
        return result;
    }
    
    public void setSelectedChannel(StreamKey key) {
        for (DetectionWaveforms dw : myWaveforms) {
            ArrayList<CorrelationComponent> segments = dw.getSegments();
            int idx = 0;
            for (CorrelationComponent cc : segments) {
                StreamKey aKey = cc.getSeismogram().getStreamKey();
                if (aKey.equals(key)) {
                    elementIndex = idx;
                    displayTraces();
                    return;
                }
                ++idx;
            }
        }
    }
    
    public void displayArrayElements(CorrelationComponent selectedComponent) {
        DetectionWaveforms dw = visibleComponents.get(selectedComponent);
        ArrayDisplayFrame.getInstance().setVisible(true);
        ArrayDisplayModel.getInstance().setMatchingTraces(dw.getSegments());
    }
    
    public void setSelectedDetection(Detection det) {
        for (CorrelationComponent cc : visibleComponents.keySet()) {
            if (cc.getEvent().getEvid() == det.getDetectionid()) {
                maybeHighlightAllTraces(cc);
            }
        }
    }
    
    public void setSelectedDetection(int detectionid) {
        for (CorrelationComponent cc : visibleComponents.keySet()) {
            if (cc.getEvent().getEvid() == detectionid) {
                SingleDetectionDisplayFrame.getInstance().setVisible(true);
                SingleDetectionModel.getInstance().setData(cc, runid);
                return;
            }
        }
    }
    
    public void shiftWindowStart(WindowAdjustmentDirection direction) {
        double oldWindowStart = ParameterModel.getInstance().getWindowStart();
        double windowDuration = ParameterModel.getInstance().getCorrelationWindowLength();
        double adjustmentFraction = 0.02;
        double adjustmentAmount = windowDuration * adjustmentFraction;
        if (direction == WindowAdjustmentDirection.LEFT) {
            double newWindowStart = oldWindowStart - adjustmentAmount;
            ParameterModel.getInstance().setWindowStart(newWindowStart);
            adjustAllWindows(newWindowStart, windowDuration);
        } else if (direction == WindowAdjustmentDirection.RIGHT) {
            double newWindowStart = oldWindowStart + adjustmentAmount;
            ParameterModel.getInstance().setWindowStart(newWindowStart);
            adjustAllWindows(newWindowStart, windowDuration);
            
        }
    }
    
    public void changeWindowSize(WindowAdjustmentDirection direction) {
        double windowStart = ParameterModel.getInstance().getWindowStart();
        double windowDuration = ParameterModel.getInstance().getCorrelationWindowLength();
        double adjustmentFraction = 0.02;
        double adjustmentAmount = windowDuration * adjustmentFraction;
        if (direction == WindowAdjustmentDirection.LEFT) {
            double newWindowLength = windowDuration - adjustmentAmount;
            if (newWindowLength < 1) {
                return;
            }
            ParameterModel.getInstance().setCorrelationWindowLength(newWindowLength);
            adjustAllWindows(windowStart, newWindowLength);
        } else if (direction == WindowAdjustmentDirection.RIGHT) {
            double newWindowLength = windowDuration + adjustmentAmount;
            ParameterModel.getInstance().setCorrelationWindowLength(newWindowLength);
            adjustAllWindows(windowStart, newWindowLength);
        }
    }
    
    private void clearAllViewers() {
        for (SeismogramViewer viewer : viewers) {
            viewer.clear();
        }
        StackModel.getInstance().clear();
    }
    
    public void adjustAllWindows(double windowStart, double winLen) {
        for (SeismogramViewer viewer : viewers) {
            viewer.adjustWindow(windowStart, winLen);
        }
        ClusterBuilderFrame.getInstance().setCorrelationWindowStart(windowStart);
        ClusterBuilderFrame.getInstance().setCorrelationWindowLength(winLen);
    }
    
    private void maybeHighlightAllTraces(CorrelationComponent cc) {
        for (SeismogramViewer viewer : viewers) {
            viewer.maybeHighlightTrace(cc);
        }
    }
    
    public void setMouseMode(MouseMode mouseMode) {
        for (SeismogramViewer viewer : viewers) {
            viewer.setMouseMode(mouseMode);
        }
    }
    
    public void updateForChangedTrace() {
        StackModel.getInstance().setCurrentStack();
        viewers.forEach((viewer) -> {
            viewer.updateForChangedTrace();
        });
    }
    
    private void loadClusterResult() {
        StackModel.getInstance().setCurrentStack();
        viewers.forEach((viewer) -> {
            viewer.loadClusterResult();
        });
    }
    
    private void updateForFailedCorrelation() {
        viewers.forEach((viewer) -> {
            viewer.updateForFailedCorrelation();
        });
    }
    
    private void dataWereLoaded(boolean b) {
        StackModel.getInstance().setCurrentStack();
        viewers.forEach((viewer) -> {
            viewer.dataWereLoaded(b);
        });
    }
    
    public Map<StreamKey, SingleComponentStack> getKeyStackMap() {
        Collection<CorrelationComponent> data = getMatchingTraces();
        Set<Integer> detectionIdValues = new HashSet<>();
        for (CorrelationComponent cc : data) {
            detectionIdValues.add((int) cc.getEvent().getEvid());
        }
        
        ArrayList<CorrelationComponent> result = new ArrayList<>();
        for (DetectionWaveforms dw : myWaveforms) {
            for (CorrelationComponent comp : dw.getSegments()) {
                if (detectionIdValues.contains((int) comp.getEvent().getEvid())) {
                    result.add(comp);
                }
            }
        }
        
        Map<StreamKey, SingleComponentStack> keyStackMap = new HashMap<>();
        
        for (CorrelationComponent cc : result) {
            StreamKey key = cc.getCorrelationTraceData().getStreamKey();
            SingleComponentStack stack = keyStackMap.get(key);
            if (stack == null) {
                stack = new SingleComponentStack(key);
                keyStackMap.put(key, stack);
            }
            stack.addTrace(cc);
        }
        return keyStackMap;
    }
    
    public ArrayList<CorrelationComponent> getAllComponents() {
        ArrayList<CorrelationComponent> result = new ArrayList<>();
        for (DetectionWaveforms dw : myWaveforms) {
            for (CorrelationComponent comp : dw.getSegments()) {
                result.add(comp);
            }
        }
        return result;
    }
    
    public void notifyViewsPicksChanged() {
        viewers.forEach((viewer) -> {
            viewer.displayAllPicks();
        });
    }
    
    public void notifyViewsPicksRemoved() {
        viewers.forEach((viewer) -> {
            viewer.clearAllPicks();
        });
    }
    
    public void savePicks() {
        Map<CorrelationComponent, Collection<PhasePick>> pickMap = DetectionPhasePickModel.getInstance().getAllPicks();
        Collection<Integer> deletedPicks = DetectionPhasePickModel.getInstance().getDeletedPicks();
        ArrayList<PhasePick> picks = new ArrayList<>();
        for (Collection<PhasePick> cdpp : pickMap.values()) {
            picks.addAll(cdpp);
        }
        new SavePicksWorker(picks, deletedPicks, ClusterBuilderFrame.getInstance()).execute();
    }
    
    public void incrementRowsRetrieved(int value) {
        if (totalRowsRetrieved + value == this.totalDetectionCount) {
            lastDetectionId = 0;
            totalRowsRetrieved = 0;
            
        } else {
            totalRowsRetrieved += value;
        }
        
    }
    
    public void setTotalDetectionCount(int detectionCount) {
        totalDetectionCount = detectionCount;
    }
    
    public void setConfigid(int configid) {
        this.configid = configid;
    }
    
    private int getBestDisplayableChannel() {
        
        String chan = DetectionPhasePickModel.getInstance().getBestChanForPicks();
        if (chan != null) {
            for (int j = 0; j < ChannelCombo.getInstance().getItemCount(); ++j) {
                StreamKey sk = (StreamKey) ChannelCombo.getInstance().getItemAt(j);
                if (sk.getChan().equals(chan)) {
                    return j;
                }
            }
        } else {
            for (int j = 0; j < ChannelCombo.getInstance().getItemCount(); ++j) {
                Object obj = ChannelCombo.getInstance().getItemAt(j);
                if (obj instanceof StreamKey) {
                    StreamKey sk = (StreamKey) obj;
                    if (sk.getChan().endsWith("Z")) {
                        return j;
                    }
                }
            }
        }
        
        return 0;
    }
    
    public void lastGroup() {
        currentGroupIndex = allGroups.size() - 1;
        NextCorrelationAction.getInstance(this).setEnabled(currentGroupIndex < allGroups.size() - 1);
        PreviousCorrelationAction.getInstance(this).setEnabled(currentGroupIndex > 0);
        LastCorrelationAction.getInstance(this).setEnabled(false);
        FirstCorrelationAction.getInstance(this).setEnabled(currentGroupIndex > 0);
        resetForNewGroup();
    }
    
    public void firstGroup() {
        currentGroupIndex = 0;
        NextCorrelationAction.getInstance(this).setEnabled(currentGroupIndex < allGroups.size() - 1);
        PreviousCorrelationAction.getInstance(this).setEnabled(currentGroupIndex > 0);
        LastCorrelationAction.getInstance(this).setEnabled(currentGroupIndex < allGroups.size() - 1);
        FirstCorrelationAction.getInstance(this).setEnabled(currentGroupIndex > 0);
        resetForNewGroup();
    }
    
    private void maybeDeleteFromCurrentGroup(CorrelationComponent cc) {
        if (!allGroups.isEmpty() && currentGroupIndex >= 0) {
            GroupData gd = allGroups.get(currentGroupIndex);
            if (gd != null) {
                gd.maybeRemoveCorrelation(cc);
            }
        }
    }
    
    private static class CorrelatedTracesModelHolder {
        
        private static final CorrelatedTracesModel INSTANCE = new CorrelatedTracesModel();
    }
    
    public void resetTriggerClassification(TriggerClassification tc, int aDetectorid) {
        if (aDetectorid != detectorid) {
            return;
        }
        for (int key : triggerClassificationMap.keySet()) {
            triggerClassificationMap.replace(key, tc);
        }
    }
    
}
