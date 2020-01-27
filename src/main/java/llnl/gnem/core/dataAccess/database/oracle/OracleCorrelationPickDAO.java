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

import Jama.Matrix;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.clustering.GroupData;
import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.interfaces.CorrelationPickDAO;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class OracleCorrelationPickDAO implements CorrelationPickDAO {

    @Override
    public void writeCorrelationPickSet(Collection<CorrelationComponent> componentGroup,
            long streamId, double groupThreshold, double preRefSeconds,
            double postRefSeconds, Integer filterid, double analystShift,
            String phase, double uncertainty, GroupData gd) throws DataAccessException {
        try {
            writeCorrelationPickSetP(componentGroup,
                    streamId,
                    groupThreshold,
                    preRefSeconds,
                    postRefSeconds,
                    filterid,
                    analystShift,
                    phase,
                    uncertainty,
                    gd);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private void writeCorrelationPickSetP(Collection<CorrelationComponent> componentGroup,
            long streamId,
            double groupThreshold,
            double preRefSeconds,
            double postRefSeconds,
            Integer filterid,
            double analystShift,
            String phase,
            double uncertainty,
            GroupData gd) throws SQLException {
        Connection conn = null;
        try {

            conn = DAOFactory.getInstance().getConnections().checkOut();

            writePickGroupRow(streamId,
                    groupThreshold,
                    preRefSeconds,
                    postRefSeconds,
                    filterid,
                    analystShift,
                    conn);

            ArrayList<Long> arrivalIdValues = new ArrayList<>();
            int colIndex = 0;

            for (CorrelationComponent comp : componentGroup) {
                NominalArrival refArrival = comp.getNominalPick();
                double lsShift = comp.getShift();

                long eventId = comp.getEvent().getEvid();
                long stationId = comp.getStationId();
                long waveformId = comp.getWfid();
                double snr = comp.getSeismogram().getSnr(refArrival.getTime(), 10, 10);
                TimeT pickTime = new TimeT(refArrival.getTime() - lsShift + analystShift);
                long arrivalId = writePrimeArrival(eventId, stationId,
                        streamId, pickTime.getEpochTime(), phase, waveformId, filterid,
                        uncertainty, snr, conn);
                writeCorrelationPickRow(arrivalId, colIndex, refArrival.getTime(),
                        refArrival.getArid(), lsShift, waveformId, conn);
                arrivalIdValues.add(arrivalId);
                ++colIndex;
            }
            Matrix correlations = gd.getCorrelations();
            Matrix shifts = gd.getShifts();

            for (int j = 0; j < arrivalIdValues.size() - 1; ++j) {
                long arrivalId1 = arrivalIdValues.get(j);
                for (int k = j + 1; k < arrivalIdValues.size(); ++k) {
                    long arrivalId2 = arrivalIdValues.get(k);
                    double correlation = correlations.get(j, k);
                    double shift = shifts.get(j, k);
                    writeCorrMatrixElement(arrivalId1, arrivalId2, correlation, shift, conn);
                }
            }
            conn.commit();
        } finally {
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    void writePickGroupRow(long streamId,
            double groupThreshold,
            double preRefSeconds,
            double postRefSeconds,
            Integer filterid,
            double analystShift,
            Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = String.format("insert into %s values (%s.nextval, ?,?,?,?,?,?,lower(user), sysdate)",
                    TableNames.PICK_GROUP_TABLE, SequenceNames.GROUP_ID_SEQUENCE);
            stmt = conn.prepareStatement(sql);
            int jdx = 1;
            stmt.setLong(jdx++, streamId);
            stmt.setDouble(jdx++, groupThreshold);
            stmt.setDouble(jdx++, preRefSeconds);
            stmt.setDouble(jdx++, postRefSeconds);
            if (filterid != null && filterid > 0) {
                stmt.setInt(jdx++, filterid);
            } else {
                stmt.setNull(jdx++, Types.INTEGER);
            }
            stmt.setDouble(jdx++, analystShift);

            stmt.execute();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private long writePrimeArrival(long eventId, long stationId,
            long streamId, double time, String phase, long waveformId, Integer filterId,
            double uncertainty, double snr, Connection conn) throws SQLException {
        long arrivalId = OracleDBUtil.getNextId(conn, SequenceNames.ARRIVAL_ID_SEQUENCE);
        String sql = String.format("insert into %s values (?,?,?,?,?,?,"
                + "lower(user), sysdate)", TableNames.ARRIVAL_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int jdx = 1;
            stmt.setLong(jdx++, arrivalId);
            stmt.setLong(jdx++, eventId);
            stmt.setLong(jdx++, stationId);
            stmt.setLong(jdx++, streamId);
            stmt.setDouble(jdx++, time);
            stmt.setString(jdx++, phase);
            stmt.execute();
            writeRewiewedArrivalRow(arrivalId, conn);
            writeArrivalWaveformAssoc(waveformId, arrivalId, conn);
            if (filterId != null) {
                writeAppliedFilterRow(filterId, arrivalId, conn);
            }
            writeArrivalCharacteristicRow(arrivalId, uncertainty, snr, conn);
            updateStationGroup(arrivalId, conn);
            return arrivalId;
        }
    }

    private void updateStationGroup(long arrivalId, Connection conn) throws SQLException {

        String sql = String.format("merge into %s tt\n"
                + "using (select a.arrival_id, c.cnt + 1 cnt, c.rowid crowid\n"
                + "         from %s a, %s b, %s c\n"
                + "        where arrival_id = ?\n"
                + "          and a.station_id = b.station_id\n"
                + "          and b.group_id = c.group_id\n"
                + "          and a.event_id = c.event_id\n"
                + "          and a.iphase = c.iphase) st\n"
                + "on (tt.rowid = st.crowid)\n"
                + "when matched then\n"
                + "  update set tt.primary_id = st.arrival_id, tt.cnt = st.cnt",
                TableNames.ARRIVAL_GROUP_TABLE,
                TableNames.ARRIVAL_TABLE,
                TableNames.ADSL_STATION_GROUP_MEMBER_TABLE,
                TableNames.ARRIVAL_GROUP_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, arrivalId);
            stmt.execute();
        }
    }

    private void writeArrivalWaveformAssoc(long waveformId, long arrivalId, Connection conn) throws SQLException {
        String sql = String.format("insert into %s values(?,?)",
                TableNames.ARRIVAL_WAVEFORM_ASSOC_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, arrivalId);
            stmt.setLong(2, waveformId);
            stmt.execute();
        }
    }

    private void writeAppliedFilterRow(int filterId, long arrivalId, Connection conn) throws SQLException {
        String sql = String.format("insert into %s values(?,?, lower(user),sysdate)",
                TableNames.APPLIED_FILTER_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, arrivalId);
            stmt.setInt(2, filterId);
            stmt.execute();
        }
    }

    private void writeArrivalCharacteristicRow(long arrivalId, double uncertainty, double snr, Connection conn) throws SQLException {
        String sql = String.format("insert into %s values(?,?, null,?,null)",
                TableNames.ARRIVAL_CHARACTERISTIC_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, arrivalId);
            stmt.setDouble(2, uncertainty);
            stmt.setDouble(3, snr);
            stmt.execute();
        }
    }

    public void writeCorrMatrixElement(long arrivalId1, long arrivalId2,
            double correlation, double shift, Connection conn) throws SQLException {
        String sql = String.format("insert into %s values (?,?,?,?)",
                TableNames.CORR_MATRIX_ELEMENT_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int jdx = 1;
            stmt.setLong(jdx++, arrivalId1);
            stmt.setLong(jdx++, arrivalId2);
            stmt.setDouble(jdx++, correlation);
            stmt.setDouble(jdx++, shift);
            stmt.execute();
        }

    }

    private void writeCorrelationPickRow(long arrivalId, int colIndex, double refTime,
            Long arid, double lsShift, long waveformId, Connection conn) throws SQLException {
        String sql = String.format("insert into %s values (%s.currval,?,?,?,?,?,?,lower(user),sysdate)",
                TableNames.CORRELATION_PICK_TABLE, SequenceNames.GROUP_ID_SEQUENCE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int jdx = 1;
            stmt.setLong(jdx++, arrivalId);
            stmt.setInt(jdx++, colIndex);
            stmt.setDouble(jdx++, refTime);
            if (arid != null) {
                stmt.setLong(jdx++, arid);
            } else {
                stmt.setNull(jdx++, Types.INTEGER);
            }
            stmt.setDouble(jdx++, lsShift);
            stmt.setLong(jdx++, waveformId);
            stmt.execute();
        }

    }

    private void writeRewiewedArrivalRow(long arrivalId, Connection conn) throws SQLException {
        String sql = String.format("insert into %s values (?,'y',lower(user))",
                TableNames.REVIEWED_ARRIVAL_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, arrivalId);
            stmt.execute();
        }
    }

}
