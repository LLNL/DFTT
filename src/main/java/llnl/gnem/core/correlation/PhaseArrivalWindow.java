/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.correlation;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.correlation.util.PhaseWindow;
import llnl.gnem.core.util.BandInfo;
import llnl.gnem.core.util.Geometry.EModel;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.TimeT;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class PhaseArrivalWindow implements Comparable<PhaseArrivalWindow> {
    public static final double MAX_TRACE_LENGTH = 2500.0;

    public static PhaseArrivalWindow maybeTrimWindow1(PhaseArrivalWindow window1, PhaseArrivalWindow window2) {
        if (windowsAreDependent(window1, window2)) {
            if (window1.getEnd() <= window2.getTime()) {
                return window1;
            } else {
                return trimWindowToFit(window2, window1);
            }
        }
        else{
            return window1;
        }
    }

    private static PhaseArrivalWindow trimWindowToFit(PhaseArrivalWindow window2, PhaseArrivalWindow window1) {
        double requiredDuration = window2.getTime() - window1.getTime();
        if (requiredDuration > window1.getWindowLength() / 2) {
            PhaseArrivalWindow newWindow = window1.makeNewWindow(requiredDuration);
            if (newWindow.isUsable()) {
                return newWindow;
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }

    private static boolean windowsAreDependent(PhaseArrivalWindow window1, PhaseArrivalWindow window2) {
        String p1 = window1.getPhase();
        String p2 = window2.getPhase();
        return p1.equals("Pn") && p2.equals("Pg") ||
                p1.equals("P") && p2.equals("S") ||
                p1.equals("Sn") && p2.equals("Lg");
    }

    private final int windowid;
    private final String phase;
    private final double windowLength;
    private final double time;
    private final double preWinSeconds;
    private final double minDelta;
    private final double maxDelta;
    private final double minDepth;
    private final double maxDepth;
    private final Collection<BandInfo> bands;
    private static final double MIN_EVENT_LENGTH = 50.0;
    public static final int CUTOFF_GROUP_VELOCITY = 3;

    public PhaseArrivalWindow(PhaseWindow phaseWindow, double time, double windowLength) {
        this.windowid = phaseWindow.getWindowid();
        this.phase = phaseWindow.getPhase();
        this.windowLength = windowLength;
        preWinSeconds = phaseWindow.getPreWinSeconds();
        minDelta = phaseWindow.getMinDelta();
        maxDelta = phaseWindow.getMaxDelta();
        minDepth = phaseWindow.getMinDepth();
        maxDepth = phaseWindow.getMaxDepth();
        this.time = time;
        this.bands = subsetBands(phaseWindow.getBands(), windowLength);
    }

    @Override
    public String toString() {
        return String.format("(%d) %s window starting (%s) with duration %f and %d bands",
                windowid, phase,new TimeT(time).toString(),windowLength, bands.size());
    }

    public PhaseArrivalWindow makeNewWindow( double requiredDuration )
    {
        PhaseWindow tmp = new PhaseWindow(  windowid,phase,requiredDuration, preWinSeconds, minDelta, maxDelta, minDepth, maxDepth, bands);
        return new PhaseArrivalWindow(tmp,time,requiredDuration);
    }

    /**
     * @return the windowid
     */
    public int getWindowid() {
        return windowid;
    }

    /**
     * @return the phase
     */
    public String getPhase() {
        return phase;
    }

    /**
     * @return the nominalWindowLength
     */
    public double getWindowLength() {
        return windowLength;
    }
    public double getWindowLength(double delta) {
        double test = phase.equalsIgnoreCase("Whole")
                ? delta * EModel.getKilometersPerDegree() / CUTOFF_GROUP_VELOCITY : windowLength;
        double minAllowable = Math.min(MIN_EVENT_LENGTH, windowLength);
        double testTime = Math.max(test, minAllowable);
        return Math.min(testTime, MAX_TRACE_LENGTH - 10);
    }

    public double getMinimumWindowLength( double delta )
    {
        return getWindowLength(delta) / 2;
    }

    /**
     * @return the bands
     */
    public Collection<BandInfo> getBands() {
        return new ArrayList<BandInfo>(bands);
    }

    private static Collection<BandInfo> subsetBands(Collection<BandInfo> bands, double winLen) {
        Collection<BandInfo> result = new ArrayList<BandInfo>();
        for (BandInfo bi : bands) {
            double upper = bi.getHighpass();
            double cycles = winLen * upper;
            if (cycles >= 10) {
                result.add(bi);
            }
        }
        return result;
    }

    public boolean isUsable() {
        return !bands.isEmpty();
    }

    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }

    public boolean isWholeSeismogramWindow() {
        return phase.equalsIgnoreCase("Whole");
    }

    @Override
    public int compareTo(PhaseArrivalWindow aThat) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;


        if (this == aThat) {
            return EQUAL;
        }
        if (this.time == aThat.time) {
            return EQUAL;
        } else if (this.time < aThat.time) {
            return BEFORE;
        } else {
            return AFTER;
        }
    }

    public double getEnd() {
        return time + windowLength;
    }

    public Epoch getEpoch() {
        return new Epoch(time, getEnd());
    }

    public Epoch getMinimumAllowableEpoch(double delta) {
        return new Epoch(time, time + getMinimumWindowLength(delta));
    }

    public Epoch getDesiredEpoch(double delta) {
        return new Epoch( time, time + getWindowLength(delta));
    }
}
