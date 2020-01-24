/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.waveformViewer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.apps.detection.classify.TriggerClassification;
import llnl.gnem.apps.detection.core.dataObjects.Detection;
import llnl.gnem.apps.detection.database.StreamDAO;
import llnl.gnem.apps.detection.sdBuilder.ChannelCombo;
import llnl.gnem.apps.detection.sdBuilder.DetectionWaveforms;
import llnl.gnem.apps.detection.sdBuilder.DetectorCreator;
import llnl.gnem.apps.detection.sdBuilder.actions.CreateSacfilesAction;
import llnl.gnem.apps.detection.sdBuilder.actions.NextCorrelationAction;
import llnl.gnem.apps.detection.sdBuilder.actions.PreviousCorrelationAction;
import llnl.gnem.apps.detection.sdBuilder.actions.RevertAction;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.ArrayDisplayFrame;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.ArrayDisplayModel;
import llnl.gnem.apps.detection.sdBuilder.configuration.DetectorCreationEnabler;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.sdBuilder.picking.SavePicksWorker;
import llnl.gnem.apps.detection.sdBuilder.allStations.SeismogramModel;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.MultiStationStackModel;
import llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay.SingleDetectionDisplayFrame;
import llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay.SingleDetectionModel;
import llnl.gnem.apps.detection.sdBuilder.picking.DetectionPhasePickModel;
import llnl.gnem.apps.detection.sdBuilder.picking.PredictedPhasePick;
import llnl.gnem.apps.detection.sdBuilder.picking.PredictedPhasePickModel;

import llnl.gnem.apps.detection.sdBuilder.stackViewer.SingleComponentStack;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.clustering.ClusterResult;
import llnl.gnem.core.correlation.clustering.GroupData;
import llnl.gnem.core.gui.plotting.MouseMode;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.waveform.filter.FilterClient;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class CorrelatedTracesModel implements FilterClient {

    private static final double TAPER_PERCENT = 5.0;
    private int detectorid;
    private final Collection<SeismogramViewer> viewers;
    private final Collection<DetectionWaveforms> myWaveforms;
    private final Map<CorrelationComponent, DetectionWaveforms> components;
    private final Map<CorrelationComponent, DetectionWaveforms> removedComponents;
    private int elementIndex = 0;
    private GroupData current;
    private final ArrayList<GroupData> allGroups;
    private int currentIndex;
    private int runid;
    private final Map<Integer, TriggerClassification> triggerClassificationMap;
    private int lastDetectionId;
    private int totalRowsRetrieved;
    private int totalDetectionCount;
    private int configid;

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
        return new ArrayList<>(components.keySet());
    }

    public Map<CorrelationComponent, DetectionWaveforms> getComponentMap() {
        return new HashMap<>(components);
    }

    public Collection<GroupData> getGroups() {
        return new ArrayList<>(allGroups);
    }

    private CorrelatedTracesModel() {
        myWaveforms = new ArrayList<>();
        components = new TreeMap<>();
        removedComponents = new TreeMap<>();
        allGroups = new ArrayList<>();
        triggerClassificationMap = new HashMap<>();
        viewers = new ArrayList<>();
        DetectorCreationEnabler.getInstance().setWaveformsAvailable(false);
        detectorid = -1;
        lastDetectionId = 0;
        totalRowsRetrieved = 0;
        configid = -1;
    }

    public void revert() {
        allGroups.clear();
        setTraces(new ArrayList<>(), new HashMap<>(), new ArrayList<>(), new ArrayList<>());

        // DetectorCreationEnabler.getInstance().hasBeenCorrelated(false);
        for (CorrelationComponent cc : components.keySet()) {
            cc.setCorrelation(0);
            cc.setShift(0);
            cc.setStd(0);
        }

        clearAllViewers();
        dataWereLoaded(false);
        DetectorCreationEnabler.getInstance().setWaveformsAvailable(!myWaveforms.isEmpty());
        disableActions();
    }

    public static CorrelatedTracesModel getInstance() {
        return CorrelatedTracesModelHolder.INSTANCE;
    }

    public void setDetectorid(int detectorid) {
        if (this.detectorid > 0 && this.detectorid != detectorid){
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

        myWaveforms.addAll(retrieved);
        this.triggerClassificationMap.clear();
        this.triggerClassificationMap.putAll(triggerClassificationMap);
        ParameterModel.getInstance().setWindowStart(0.0);
        populateChannelCombo();
        DetectionPhasePickModel.getInstance().addExistingPicks(retrieved, detectionPicks);
        PredictedPhasePickModel.getInstance().addExistingPicks(retrieved, predictedPicks);

        displayTraces();
        if (ParameterModel.getInstance().isAutoApplyFilter()) {
            try {
                StoredFilter filter = StreamDAO.getInstance().getStreamFilter(detectorid);
                applyFilter(filter);
            } catch (SQLException ex) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Failed to retrieve stored filter from database!");
                Logger.getLogger(CorrelatedTracesModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        DetectorCreationEnabler.getInstance().setWaveformsAvailable(!retrieved.isEmpty());
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
        elementIndex = 0;

        ChannelCombo.getInstance().setSelectedIndex(elementIndex);
        ChannelCombo.getInstance().revalidate();
        ChannelCombo.getInstance().enableActionListener(true);
    }

    private void displayTraces() {
        components.clear();
        resampleData();
        conditionData();
        for (DetectionWaveforms dw : myWaveforms) {
            CorrelationComponent cc = dw.getSegments().get(elementIndex);
            components.put(cc, dw);
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

    public Collection<CorrelationComponent> getMatchingTraces() {
        return new ArrayList<>(components.keySet());
    }

    public void clear() {
        myWaveforms.clear();
        components.clear();
        removedComponents.clear();
        DetectionPhasePickModel.getInstance().clear();
        PredictedPhasePickModel.getInstance().clear();
        clearAllViewers();

        allGroups.clear();
        ParameterModel.getInstance().setWindowStart(0.0);
        NextCorrelationAction.getInstance(this).setEnabled(false);
        PreviousCorrelationAction.getInstance(this).setEnabled(false);
        CreateSacfilesAction.getInstance(this).setEnabled(false);
        DetectorCreationEnabler.getInstance().setWaveformsAvailable(false);
        SeismogramModel.getInstance().clear();
        MultiStationStackModel.getInstance().clear();
    }

    public void removeComponent(CorrelationComponent selectedComponent) {
        DetectionWaveforms dw = components.get(selectedComponent);
        removedComponents.put(selectedComponent, dw);
        components.remove(selectedComponent);
        dataWereLoaded(false);
    }

    public void detectionWasDeleted(int detectionid) {
        Collection<CorrelationComponent> deleteThese = new ArrayList<>();
        components.keySet().stream().filter((cc) -> (cc.getEvent().getEvid() == detectionid)).forEachOrdered((cc) -> {
            deleteThese.add(cc);
        });
        deleteThese.forEach((cc) -> {
            components.remove(cc);
        });
        dataWereLoaded(false);

    }
    
    public void detectionsWereDeleted(Collection<CorrelationComponent> deleteThese){
        deleteThese.forEach((cc) -> {
            components.remove(cc);
        });
        dataWereLoaded(false);

    }

    void setViewer(SeismogramViewer viewer) {
        viewers.add(viewer);
    }

    public void previousGroup() {
        if (currentIndex > 0) {
            --currentIndex;
            NextCorrelationAction.getInstance(this).setEnabled(currentIndex < allGroups.size() - 1);
            PreviousCorrelationAction.getInstance(this).setEnabled(currentIndex > 0);

            resetForNewGroup();
        }
    }

    public void nextGroup() {
        if (currentIndex < allGroups.size() - 1) {
            ++currentIndex;
            NextCorrelationAction.getInstance(this).setEnabled(currentIndex < allGroups.size() - 1);
            PreviousCorrelationAction.getInstance(this).setEnabled(currentIndex > 0);

            resetForNewGroup();
        }
    }

    void updateFromClusterResult(ClusterResult cr) {
        allGroups.clear();
        allGroups.addAll(cr.getGroups());
        NextCorrelationAction.getInstance(this).setEnabled(false);
        PreviousCorrelationAction.getInstance(this).setEnabled(false);
        if (allGroups.isEmpty()) {
            updateForFailedCorrelation();
            return;
        }

        currentIndex = 0;
        NextCorrelationAction.getInstance(this).setEnabled(currentIndex < allGroups.size() - 1);
        PreviousCorrelationAction.getInstance(this).setEnabled(currentIndex > 0);

        DetectorCreationEnabler.getInstance().hasBeenCorrelated(true);
        RevertAction.getInstance(this).setEnabled(true);
        resetForNewGroup();
    }

    private void resetForNewGroup() {
        current = allGroups.get(currentIndex);
        removedComponents.putAll(components);
        components.clear();
        Collection<CorrelationComponent> tmp = current.getAssociatedInfo();
        for (CorrelationComponent cc : tmp) {
            DetectionWaveforms dw = removedComponents.get(cc);
            components.put(cc, dw);
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
        ArrayList<CorrelationComponent> allComponents = getAllComponents();
        for (CorrelationComponent comp : allComponents) {
            comp.unApplyFilter();
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

        RevertAction.getInstance(this).setEnabled(false);
    }

    public void writeNewDetector() throws Exception {
        setMouseMode(MouseMode.SELECT_REGION);
        DetectorCreator.writeNewDetector();
        setMouseMode(MouseMode.SELECT_ZOOM);
        ClusterBuilderFrame.getInstance().returnFocusToTree();
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

    private Collection<StreamKey> getDataChannels() {
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
        DetectionWaveforms dw = components.get(selectedComponent);
        ArrayDisplayFrame.getInstance().setVisible(true);
        ArrayDisplayModel.getInstance().setMatchingTraces(dw.getSegments());
    }

    public void setSelectedDetection(Detection det) {
        for (CorrelationComponent cc : components.keySet()) {
            if (cc.getEvent().getEvid() == det.getDetectionid()) {
                maybeHighlightAllTraces(cc);
            }
        }
    }

    public void setSelectedDetection(int detectionid) {
        for (CorrelationComponent cc : components.keySet()) {
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

    public void autoResizeWindow() {
        Collection<CorrelationComponent> data = getMatchingTraces();
        if (data.isEmpty()) {
            return;
        }
        new DetectionWindowAdjuster().autoResizeWindow(data);
    }

    private void clearAllViewers() {
        for (SeismogramViewer viewer : viewers) {
            viewer.clear();
        }
    }

    public void adjustAllWindows(double windowStart, double winLen) {
        for (SeismogramViewer viewer : viewers) {
            viewer.adjustWindow(windowStart, winLen);
        }
    }

    private void maybeHighlightAllTraces(CorrelationComponent cc) {
        for (SeismogramViewer viewer : viewers) {
            viewer.maybeHighlightTrace(cc);
        }
    }

    private void setMouseMode(MouseMode mouseMode) {
        for (SeismogramViewer viewer : viewers) {
            viewer.setMouseMode(mouseMode);
        }
    }

    private void updateForChangedTrace() {
        viewers.forEach((viewer) -> {
            viewer.updateForChangedTrace();
        });
    }

    private void loadClusterResult() {
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
            StreamKey key = cc.getTraceData().getStreamKey();
            SingleComponentStack stack = keyStackMap.get(key);
            if (stack == null) {
                stack = new SingleComponentStack(key);
                keyStackMap.put(key, stack);
            }
            stack.addTrace(cc);
        }
        return keyStackMap;
    }

    private ArrayList<CorrelationComponent> getAllComponents() {
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
        for( Collection<PhasePick> cdpp : pickMap.values()){
            picks.addAll(cdpp);
        }
        new SavePicksWorker(picks, deletedPicks, ClusterBuilderFrame.getInstance()).execute();
    }

    public void incrementRowsRetrieved(int value) {
        if (totalRowsRetrieved+value == this.totalDetectionCount){
            lastDetectionId = 0;
            totalRowsRetrieved = 0;
       
        } 
        else{
         totalRowsRetrieved+=value;   
        }
        
    }

    public void setTotalDetectionCount(int detectionCount) {
        totalDetectionCount = detectionCount;
    }

    public void setConfigid(int configid) {
        this.configid = configid;
    }

    private static class CorrelatedTracesModelHolder {

        private static final CorrelatedTracesModel INSTANCE = new CorrelatedTracesModel();
    }
}
