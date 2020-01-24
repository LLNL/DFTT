/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.responseProcessing;

/**
 *
 * @author dodge1
 */
public class MiscParams {

    private final double referencePeriod = 20;
    private final double referenceDistance = 1.0;
    private final double taperPercent = 5.0;
    private final double tfactor = 2;
    private final double highpassFrac = 0.85;
    private final double highCutFrac = 0.9;
    private final double lowCutFrac = 0.8;

    /**
     * @return the referencePeriod
     */
    public double getReferencePeriod() {
        return referencePeriod;
    }

    /**
     * @return the taperPercent
     */
    public double getTaperPercent() {
        return taperPercent;
    }

    /**
     * @return the tfactor
     */
    public double getTfactor() {
        return tfactor;
    }

    /**
     * @return the highpassFrac
     */
    public double getHighpassFrac() {
        return highpassFrac;
    }

    /**
     * @return the highCutFrac
     */
    public double getHighCutFrac() {
        return highCutFrac;
    }

    /**
     * @return the lowCutFrac
     */
    public double getLowCutFrac() {
        return lowCutFrac;
    }

    public double getReferenceDistance() {
        return referenceDistance;
    }

    private static class MiscParamsHolder {

        private static final MiscParams instance = new MiscParams();
    }

    public static MiscParams getInstance() {
        return MiscParamsHolder.instance;
    }

    private MiscParams() {
    }
}
