package llnl.gnem.core.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author addair1
 */
public class OracleContinuousDAO implements ContinuousWaveformDAO {
    private static final String table = "LLNL.CONTINUOUS_WFDISC";
    private Connection conn;

    public OracleContinuousDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public PairT<TimeT, TimeT> getBounds(StreamKey stachan) throws Exception {
        ResultSet rs = null;
        PreparedStatement stmt = null;

        String sql = String.format("select min(time), max(endtime) from %s where sta=? and chan=?", table);;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, stachan.getSta());
            stmt.setString(2, stachan.getChan());

            rs = stmt.executeQuery();
            if (rs.next()) {
                int i = 1;
                double start = rs.getDouble(i++);
                double end = rs.getDouble(i++);

                return new PairT<TimeT, TimeT>(new TimeT(start), new TimeT(end));
            } else {
                throw new IllegalArgumentException(String.format("No data found for StaChan: %s", stachan.toString()));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    @Override
    public TimeSeries getSeismogram(StreamKey stachan, TimeT start, TimeT end, int decimation) throws Exception {
        List<Integer> wfids = getWfids(stachan, start, end);

        TimeSeries series = null;
        System.out.println("wfids: " + wfids.size());
        for (int wfid : wfids) {
            TimeSeries block = OracleWaveformDAO.getInstance().getSingleSeismogram(wfid, table, conn);
            block.decimate(decimation);

            if (series == null) {
                series = block;
            } else {
                series = TimeSeries.unionOf(series, block);
            }
        }

        return series;
    }

    private List<Integer> getWfids(StreamKey stachan, TimeT start, TimeT end) throws Exception {
        ResultSet rs = null;
        PreparedStatement stmt = null;

        String sql = String.format("select wfid from %s where sta=? and chan=? and time >= ? and endtime <= ?", table);
        try {
            int i = 1;
            stmt = conn.prepareStatement(sql);
            stmt.setString(i++, stachan.getSta());
            stmt.setString(i++, stachan.getChan());
            stmt.setDouble(i++, start.getEpochTime());
            stmt.setDouble(i++, end.getEpochTime());

            rs = stmt.executeQuery();

            List<Integer> results = new ArrayList<Integer>();
            while (rs.next()) {
                int wfid = rs.getInt(1);
                results.add(wfid);
            }

            return results;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    @Override
    public double getSampleRate(StreamKey stachan) throws Exception {
        ResultSet rs = null;
        PreparedStatement stmt = null;

        String sql = String.format("select samprate, count(samprate) as freq from %s where sta=? and chan=? group by samprate order by freq desc", table);
        try {
            int i = 1;
            stmt = conn.prepareStatement(sql);
            stmt.setString(i++, stachan.getSta());
            stmt.setString(i++, stachan.getChan());

            rs = stmt.executeQuery();

            if (rs.next()) {
                double sampleRate = rs.getDouble(1);
                return sampleRate;
            } else {
                throw new IllegalArgumentException(String.format("No data found for StaChan: %s", stachan.toString()));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }
}
