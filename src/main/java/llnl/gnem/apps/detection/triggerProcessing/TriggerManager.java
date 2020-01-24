package llnl.gnem.apps.detection.triggerProcessing;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.dataObjects.Detection;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.Trigger;
import llnl.gnem.apps.detection.database.DetectionDAO;
import llnl.gnem.apps.detection.database.TriggerDAO;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Epoch;

/**
 * Created by dodge1 Date: Sep 27, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class TriggerManager {

    public Collection<Detection> processTriggersIntoDetections(Map<DetectorType, List<Trigger>> triggerMap) throws SQLException {
        Collection<Detection> result = new ArrayList<>();
        removeOverlappedSpawningDetections(triggerMap);
        for (int priority : DetectorType.getRanks()) {
            DetectorType type = DetectorType.getByPriority(priority);
            Collection<Trigger> triggers = triggerMap.get(type);
            triggerMap.remove(type); // leave only triggers from lower-ranked detectors in map.
            if (triggers != null && !triggers.isEmpty()) {
                result.addAll(processThisRank(triggers, triggerMap));
            }
        }
        return result;
    }

    private static Collection<Detection> processThisRank(Collection<Trigger> triggers, Map<DetectorType, List<Trigger>> triggerMap) throws SQLException {
        Collection<Detection> result = new ArrayList<>();
        while (!triggers.isEmpty()) {
            Trigger highestStatTrigger = getHighestStatTrigger(triggers);
            triggers.remove(highestStatTrigger);
            Detection detection = convertToDetection(highestStatTrigger);
            result.add(detection);
            removeCoincidentTriggers(highestStatTrigger, triggers);

            for (DetectorType type : triggerMap.keySet()) {
                Collection<Trigger> lowerRankedTriggers = triggerMap.get(type);
                removeCoincidentTriggers(highestStatTrigger, lowerRankedTriggers);
            }
        }
        return result;
    }

    public static Collection<Trigger> getCancellationTriggers(Collection<Trigger> triggers) throws SQLException {
        Collection<Trigger> result = new ArrayList<>();
        while (!triggers.isEmpty()) {
            Trigger highestStatTrigger = getHighestStatTrigger(triggers);
            result.add(highestStatTrigger);
            triggers.remove(highestStatTrigger);
            removeCoincidentTriggers(highestStatTrigger, triggers);
        }
        return result;
    }

    private static Detection convertToDetection(Trigger trigger) throws SQLException {
        return DetectionDAO.getInstance().detectionFromTrigger(trigger);
    }

    private static Trigger getHighestStatTrigger(Collection<Trigger> triggers) {
        double maxStat = -Double.MAX_VALUE;
        Trigger best = null;
        for (Trigger trigger : triggers) {
            double statistic = trigger.getMaxDetStat();
            if (statistic > maxStat) {
                maxStat = statistic;
                best = trigger;
            }
        }
        return best;
    }

    private static void removeCoincidentTriggers(Trigger highestStatTrigger, Collection<Trigger> triggers) throws SQLException {

        Iterator<Trigger> it = triggers.iterator();
        while (it.hasNext()) {
            Trigger trigger = it.next();

            if (isCoincident(highestStatTrigger, trigger)) {
                if (trigger.getTriggerid() != highestStatTrigger.getTriggerid()) {
                    it.remove();
                    TriggerDAO.getInstance().markAsCoincident(trigger);
                    String msg = String.format("Trigger (%s) was rejected as coincident in TriggerManager:removeCoincidentTriggers.", trigger.toString());
                    ApplicationLogger.getInstance().log(Level.FINE, String.format(msg));
                }
            }
        }
    }

    private static boolean isCoincident(Trigger trigger1, Trigger trigger2) {
        Epoch epoch1 = trigger1.getEpoch();
        double t1 = epoch1.getStart();
        Epoch epoch2 = trigger2.getEpoch();
        double t2 = epoch2.getStart();
        if (trigger1.getDetectorType() == DetectorType.SUBSPACE) {
            return Math.abs(t1 - t2) < 2.5;
        } else {
            return epoch1.intersects(epoch2);
        }

    }

    private void removeOverlappedSpawningDetections(Map<DetectorType, List<Trigger>> triggerMap) throws SQLException {
        for (DetectorType type : DetectorType.getSpawningDetectorTypes()) {
            Collection<Trigger> triggers = triggerMap.get(type);
            if (triggers != null && !triggers.isEmpty()) {
                removeOverlappedForType(triggers);
                if (triggers.isEmpty()) {
                    triggerMap.remove(type);
                }
            }
        }
    }

    private void removeOverlappedForType(Collection<Trigger> triggers) throws SQLException {
        ArrayList<Trigger> tmp = new ArrayList<>(triggers);
        triggers.clear();
        while (collectionHasOverlaps(tmp)) {
            Trigger first = tmp.get(0);
            tmp.remove(first);

            // overlapped contains everything overlapped with first including first.
            ArrayList<Trigger> overlapped = findOverlapWith(first, tmp);
            tmp.removeAll(overlapped);
            if (overlapped.size() == 1) { // no overlap
                triggers.add(overlapped.get(0));
            } else {
                Trigger survivor = chooseSurvivor(overlapped);//Remove all but best and update database accordingly.
                tmp.add(survivor); // Put it back in because if not the first it may overlap another element.
            }
        }
        triggers.addAll(tmp);
    }

    private boolean collectionHasOverlaps(ArrayList<Trigger> triggers) {
        for (int j = 0; j < triggers.size() - 1; ++j) {
            Epoch epoch1 = triggers.get(j).getEpoch();
            for (int k = j + 1; k < triggers.size(); ++k) {
                Epoch epoch2 = triggers.get(k).getEpoch();
                if (epoch1.intersects(epoch2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private ArrayList<Trigger> findOverlapWith(Trigger target, ArrayList<Trigger> triggers) {
        Epoch e = target.getEpoch();
        ArrayList<Trigger> overlapped = new ArrayList<>();
        overlapped.add(target);
        for (Trigger trigger : triggers) {
            if (e.intersects(trigger.getEpoch())) {
                overlapped.add(trigger);
            }
        }
        return overlapped;
    }

    private Trigger chooseSurvivor(ArrayList<Trigger> overlapped) throws SQLException {
        int index = -1;
        double bestDetStat = 0;
        for (int j = 0; j < overlapped.size(); ++j) {
            Trigger trigger = overlapped.get(j);
            if (trigger.getMaxDetStat() > bestDetStat) {
                index = j;
                bestDetStat = trigger.getMaxDetStat();
            }
        }
        Trigger survivor = overlapped.get(index);
        overlapped.remove(survivor);
        for (Trigger trigger : overlapped) {
            TriggerDAO.getInstance().markAsCoincident(trigger);
            String msg = String.format("Trigger (%s) was rejected as coincident in TriggerManager:chooseSurvivor.",
                    trigger.toString());
            ApplicationLogger.getInstance().log(Level.FINE, String.format(msg));

        }
        overlapped.clear();
        return survivor;
    }

    private static class TriggerManagerHolder {

        private static final TriggerManager instance = new TriggerManager();
    }

    public static TriggerManager getInstance() {
        return TriggerManagerHolder.instance;
    }

    private TriggerManager() {
    }
}
