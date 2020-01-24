/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.selectionCriteria;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.dataAccess.selectionCriteria.position.CircleRestriction;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.dataAccess.selectionCriteria.position.PositionRestrictionSpec;

/**
 *
 * @author dodge1
 */
public class EventSelectionCriteria {

    private final boolean useEvidList;
    private final PositionRestrictionSpec positionRestriction;
    private final EtypeRestriction etypes;
    private final TimeRestriction timeRange;
    private final MagnitudeRestriction magRange;
    private final DepthRestriction depthRange;
    private final Collection<Long> evidList;

    /**
     * @return the evidList
     */
    public Collection<Long> getEvidList() {
        return new ArrayList<>(evidList);
    }

    /**
     * @return the useEvidList
     */
    public boolean isUseEvidList() {
        return useEvidList;
    }

    /**
     * @return the positionRestriction
     */
    public PositionRestrictionSpec getPositionRestriction() {
        return positionRestriction;
    }

    /**
     * @return the etypes
     */
    public EtypeRestriction getEtypes() {
        return etypes;
    }

    /**
     * @return the timeRange
     */
    public TimeRestriction getTimeRange() {
        return timeRange;
    }

    /**
     * @return the magRange
     */
    public MagnitudeRestriction getMagRange() {
        return magRange;
    }

    /**
     * @return the depthRange
     */
    public DepthRestriction getDepthRange() {
        return depthRange;
    }

    public EventSelectionCriteria(PositionRestrictionSpec positionRestriction,
            TimeRestriction timeRange,
            MagnitudeRestriction magRange,
            DepthRestriction depthRange,
            Collection<String> etypes,
            boolean restrictByEtypes,
            Collection<Long> evidList,
            boolean useEvidList) {
        this.positionRestriction = positionRestriction;
        this.timeRange = timeRange;
        this.magRange = magRange;
        this.depthRange = depthRange;
        this.etypes = new EtypeRestriction(etypes, restrictByEtypes);
        this.evidList = new ArrayList<>(evidList);
        this.useEvidList = useEvidList;
    }

    public EventSelectionCriteria() {
        positionRestriction = new CircleRestriction(32.0, 54.0, 10.0);
        TimeT now = new TimeT();
        double epochTime = now.getEpochTime();
        double earlier = epochTime - TimeT.AVG_DAYS_PER_YEAR * TimeT.SECPERDAY;
        timeRange = new TimeRestriction(new Epoch(earlier, epochTime));
        magRange = new MagnitudeRestriction(5.0, 9.0);
        depthRange = new DepthRestriction(0.0, 700.0);
        etypes = new EtypeRestriction();
        evidList = new ArrayList<>();
        useEvidList = false;
    }
}
