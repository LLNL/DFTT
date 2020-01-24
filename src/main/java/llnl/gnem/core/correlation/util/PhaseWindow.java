/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.correlation.util;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.correlation.PhaseArrivalWindow;
import llnl.gnem.core.seismicData.BasePhaseWindow;
import llnl.gnem.core.util.BandInfo;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class PhaseWindow extends BasePhaseWindow {

    public static void removeRedundantPhases(ArrayList<PhaseArrivalWindow> tmpWindows) {
        if (hasPhase("Pg", tmpWindows)) {
            removePhase("P", tmpWindows);
        }

        if (hasPhase("Pn", tmpWindows)) {
            removePhase("P", tmpWindows);
        }

        if (hasPhase("Lg", tmpWindows)) {
            removePhase("S", tmpWindows);
        }

        if (hasPhase("Sn", tmpWindows)) {
            removePhase("S", tmpWindows);
        }
    }

    private static boolean hasPhase(String target, ArrayList<PhaseArrivalWindow> windows) {
        for (PhaseArrivalWindow window : windows) {
            if (window.getPhase().equals(target)) {
                return true;
            }
        }
        return false;
    }

    private static void removePhase(String target, ArrayList<PhaseArrivalWindow> windows) {
        for (PhaseArrivalWindow window : windows) {
            if (window.getPhase().equals(target)) {
                windows.remove(window);
                return;
            }
        }
    }
    private final int windowid;
    private final double minDelta;
    private final double maxDelta;
    private final double minDepth;
    private final double maxDepth;
    private final Collection<BandInfo> bands;

    public PhaseWindow(int windowid,
            String phase,
            double nominalWindowLength,
            double preWinSeconds,
            double minDelta,
            double maxDelta,
            double minDepth,
            double maxDepth,
            Collection<BandInfo> bands) {
        super(phase, nominalWindowLength, preWinSeconds);
        this.windowid = windowid;
        this.minDelta = minDelta;
        this.maxDelta = maxDelta;
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
        this.bands = new ArrayList<>(bands);
    }

    /**
     * @return the windowid
     */
    public int getWindowid() {
        return windowid;
    }

    /**
     * @return the bands
     */
    public Collection<BandInfo> getBands() {
        return new ArrayList<>(bands);
    }

    public String getCalculatorPhaseName() {
        return getPhase().equalsIgnoreCase("Whole") ? "P" : getPhase();
    }

    /**
     * @return the minDelta
     */
    public double getMinDelta() {
        return minDelta;
    }

    /**
     * @return the maxDelta
     */
    public double getMaxDelta() {
        return maxDelta;
    }

    /**
     * @return the minDepth
     */
    public double getMinDepth() {
        return minDepth;
    }

    /**
     * @return the maxDepth
     */
    public double getMaxDepth() {
        return maxDepth;
    }

    public boolean isAllowable(double delta, double depth) {
        if (delta < minDelta || delta > maxDelta) {
            return false;
        }
        if (depth < minDepth || depth > maxDepth) {
            return false;
        }
        return true;
    }
}
