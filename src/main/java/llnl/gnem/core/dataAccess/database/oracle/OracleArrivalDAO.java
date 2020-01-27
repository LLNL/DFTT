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
package llnl.gnem.core.dataAccess.database.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.interfaces.ArrivalDAO;
import llnl.gnem.core.traveltime.Ak135.TraveltimeCalculatorProducer;
import llnl.gnem.core.traveltime.SinglePhaseTraveltimeCalculator;
import llnl.gnem.core.util.Geometry.EModel;

/**
 *
 * @author dodge1
 */
public class OracleArrivalDAO implements ArrivalDAO {

    @Override
    public NominalArrival getNominalArrival(long eventId, long stationId, long streamId, String phase) throws DataAccessException {
        try {
            return getNominalArrivalP(eventId, stationId, streamId, phase);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private NominalArrival getNominalArrivalP(long eventId, long stationId, long streamId, String phase) throws Exception {
        NominalArrival best = getExistingArrival(eventId, stationId, streamId, phase);
        if (best == null) {
            return getTheoreticalArrival(eventId, stationId, phase);
        } else {
            return best;
        }
    }

    private NominalArrival getTheoreticalArrival(double otime,
            double evla,
            double evlo,
            double stla,
            double stlo,
            Double depth,
            String phase) throws Exception {

        double delta = EModel.getDeltaWGS84(stla, stlo, evla, evlo);
        TraveltimeCalculatorProducer pdl = TraveltimeCalculatorProducer.getInstance();
        SinglePhaseTraveltimeCalculator model = pdl.getSinglePhaseTraveltimeCalculator(phase);
        double aDepth = 0.0;
        if (depth != null && depth > 0) {
            aDepth = depth;
        }
        double tt = model.getTT1D(delta, aDepth);

        if (!Double.isNaN(tt)) {
            return new NominalArrival(phase, otime + tt);
        } else {
            return null;
        }

    }

    private NominalArrival getExistingArrival(long eventId, long stationId, long streamId, String phase) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        ResultSet rs = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            String sql = String.format(" select c.time,c.auth,c.arrival_id\n"
                    + "  from %s a, %s b, %s c\n"
                    + " where a.station_id = ?\n"
                    + "   and a.group_id = b.group_id\n"
                    + "   and b.event_id = ?\n"
                    + "   and b.iphase = ? and b.primary_id = c.arrival_id"
                    + "", TableNames.ADSL_STATION_GROUP_MEMBER_TABLE,
                    TableNames.ARRIVAL_GROUP_TABLE,
                    TableNames.ARRIVAL_TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, stationId);
            stmt.setLong(2, eventId);
            stmt.setString(3, phase);
            rs = stmt.executeQuery();

            while (rs.next()) {
                double time = rs.getDouble(1);
                String auth = rs.getString(2);
                long arrivalId = rs.getLong(3);
                return new NominalArrival(phase, time, auth, arrivalId);
            }
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                DAOFactory.getInstance().getConnections().checkIn(conn);
            }
        }
    }

    private NominalArrival getTheoreticalArrival(long eventId, long stationId, String phase) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        ResultSet rs = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            String sql = String.format("select a.time,a.lat evla,a.lon evlo,"
                    + "b.lat stla, b.lon stlo, a.depth "
                    + "from %s a, "
                    + "%s b where "
                    + "event_id = ? a"
                    + "nd prime = 'y'\n"
                    + "and b.STATION_ID = ? "
                    + "and a.time between begin_time and end_time",
                    TableNames.ORIGIN_SOLUTION_TABLE, TableNames.STATION_EPOCH_TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, eventId);
            stmt.setLong(2, stationId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int jdx = 1;
                double time = rs.getDouble(jdx++);
                double evla = rs.getDouble(jdx++);
                double evlo = rs.getDouble(jdx++);
                double stla = rs.getDouble(jdx++);
                double stlo = rs.getDouble(jdx++);
                Double depth = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    depth = null;
                }
                return getTheoreticalArrival(time,
                        evla,
                        evlo,
                        stla,
                        stlo,
                        depth,
                        phase);
            }
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                DAOFactory.getInstance().getConnections().checkIn(conn);
            }
        }
    }

}
