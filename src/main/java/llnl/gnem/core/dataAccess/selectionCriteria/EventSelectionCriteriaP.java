/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
