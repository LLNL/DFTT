/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.continuous.oracleImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.database.dao.OracleWaveformDAO;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.continuous.ContinuousSeismogram;
import llnl.gnem.core.waveform.continuous.ContinuousSeismogramDAO;

/**
 *
 * @author dodge1
 */
public class OracleContinuousSeismogramDAO implements ContinuousSeismogramDAO {

    private final String wfdiscTableName;

    public OracleContinuousSeismogramDAO(String wfdiscTableName) {
        this.wfdiscTableName = wfdiscTableName;
    }

    @Override
    public ContinuousSeismogram getSeismogram(StreamKey name, Epoch epoch) throws Exception {
        Collection<CssSeismogram> segments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            String netClause = "net = ? and ";
            boolean useNet = true;
            if (name.getNet().equals("*") || name.getNet().equals("?") || name.getNet().isEmpty()) {
                netClause = "";
                useNet = false;
            }

            String locidClause = "locid = ? and ";
            boolean useLocid = true;
            if (name.getLocationCode().equals("*") || name.getLocationCode().equals("?") || name.getLocationCode().isEmpty()) {
                locidClause = "";
                useLocid = false;
            }
            String sql = String.format("select /*+ index( a CW_N_S_C_L_T_IDX) */ wfid, net, locid from %s a where %s sta = ? and chan = ? and %s endtime > ? and time < ? order by time", wfdiscTableName, netClause, locidClause);
            stmt = conn.prepareStatement(sql);
            int jdx = 1;
            if (useNet) {
                stmt.setString(jdx++, name.getNet());
            }
            stmt.setString(jdx++, name.getSta());
            stmt.setString(jdx++, name.getChan());
            if (useLocid) {
                stmt.setString(jdx++, name.getLocationCode());
            }
            stmt.setDouble(jdx++, epoch.getTime().getEpochTime());
            stmt.setDouble(jdx++, epoch.getEnd());
            rs = stmt.executeQuery();
            while (rs.next()) {
                int wfid = rs.getInt(1);
                String aNet = rs.getString(2);
                String aLocid = rs.getString(3);
                CssSeismogram seis = OracleWaveformDAO.getInstance().getSingleSeismogram(wfid, wfdiscTableName, conn);
                if (seis.getEndtime().lt(epoch.getTime())) {
                    continue;
                }
                if (seis.getTime().gt(epoch.getEndtime())) {
                    continue;
                }
                if (seis.getTime().lt(epoch.getTime())) {
                    seis.cutBefore(epoch.getTime());
                }
                if (seis.getEndtime().gt(epoch.getEndtime())) {
                    seis.cutAfter(epoch.getEndtime());
                }
                CssSeismogram fseis = new CssSeismogram(seis.getWaveformID(), aNet, name.getSta(),
                        name.getChan(), aLocid, seis.getData(), seis.getSamprate(), seis.getTime(), seis.getCalib(), seis.getCalper());
                segments.add(fseis);
            }
            return segments.isEmpty() ? null : new ContinuousSeismogram(segments);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

}
