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
package llnl.gnem.dftt.core.dataAccess.selectionCriteria;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.dftt.core.dataAccess.selectionCriteria.position.CircleRestriction;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.dftt.core.dataAccess.selectionCriteria.position.PositionRestrictionSpec;

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
