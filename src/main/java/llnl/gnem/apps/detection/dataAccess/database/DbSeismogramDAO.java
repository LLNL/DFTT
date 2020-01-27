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
package llnl.gnem.apps.detection.dataAccess.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.OriginInfo;
import llnl.gnem.apps.detection.dataAccess.interfaces.OriginDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.SeismogramDAO;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.database.Connections;
import llnl.gnem.core.database.dao.OracleWaveformDAO;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.seismogram.TimeSeries;

public abstract class DbSeismogramDAO implements SeismogramDAO {

    @Override
    public CssSeismogram getSeismogram(StreamKey stachan, TimeT start, TimeT end) throws DataAccessException {
        try {
            return getSeismogramP(stachan, start, end);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private CssSeismogram getSeismogramP(StreamKey stachan, TimeT start, TimeT end) throws Exception {
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            List<Integer> wfids = getWfids(stachan, start, end, conn);
            TimeSeries series = null;
            for (int wfid : wfids) {
                CssSeismogram block = OracleWaveformDAO.getInstance().getSingleSeismogram(wfid, TableNames.getContinuousWfdiscTable(), conn);
                if (series == null && block != null) {
                    series = block;
                } else {
                    series = TimeSeries.unionOf(series, block);
                }
            }
            if (series != null) {
                series.trimTo(new Epoch(start, end));
                Long wfid = series.getWaveformID();
                if (wfid == null) {
                    wfid = -1L;
                }
                return new CssSeismogram(wfid, stachan, series.getData(), series.getSamprate(), series.getTime(), 1.0, 1.0);
            }

        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return null;
    }

    private List<Integer> getWfids(StreamKey stachan, TimeT start, TimeT end, Connection conn) throws Exception {
        List<Integer> results = new ArrayList<>();
        String sql = String.format("select wfid from %s where sta=? and chan=?  and endtime >= ? and time <= ?", TableNames.getContinuousWfdiscTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int i = 1;
            stmt.setString(i++, stachan.getSta());
            stmt.setString(i++, stachan.getChan());
            stmt.setDouble(i++, start.getEpochTime());
            stmt.setDouble(i++, end.getEpochTime());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int wfid = rs.getInt(1);
                    results.add(wfid);
                }
                return results;
            }
        }
    }

}
