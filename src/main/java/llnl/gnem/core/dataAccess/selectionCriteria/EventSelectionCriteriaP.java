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
package llnl.gnem.core.dataAccess.selectionCriteria;

import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.selectionCriteria.position.PositionRestriction;

/**
 *
 * @author dodge1
 */
public class EventSelectionCriteriaP {

    private final PositionRestriction positionRestriction;
    private final EtypeRestriction etypes;
    private final TimeRestriction timeRange;
    private final MagnitudeRestriction magRange;
    private final DepthRestriction depthRange;

    public EventSelectionCriteriaP(EventSelectionCriteria esc) {
        this.positionRestriction = esc.getPositionRestriction().getImplementation();
        this.timeRange = esc.getTimeRange();
        this.magRange = esc.getMagRange();
        this.depthRange = esc.getDepthRange();
        this.etypes = esc.getEtypes();
    }

    /**
     * @return the positionRestriction
     */
    public PositionRestriction getPositionRestriction() {
        return positionRestriction;
    }

    public String getSQl() {
        String sql = String.format("select event_id,\n"
                + " origin_id,\n"
                + " lat,\n"
                + " lon,\n"
                + " depth,\n"
                + " time,\n"
                + " etype,\n"
                + " magtype,\n"
                + " magnitude, has_waveforms from %s where 1=1 ",
                TableNames.QUICK_ORIGIN_LOOKUP_TABLE);
        sql = sql + positionRestriction.getSQLClause();
        sql = sql + timeRange.getSQLClause();
        sql = sql + magRange.getSQLClause();
        sql = sql + depthRange.getSQLClause();
        sql = sql + etypes.getSQLClause();

        return sql;
    }

}
