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
package llnl.gnem.core.waveform.continuous.oracleImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.continuous.StationSelectionMode;
import llnl.gnem.core.waveform.continuous.segments.SegmentInfoDAO;
import llnl.gnem.core.waveform.continuous.segments.ChannelSegmentCatalog;
import llnl.gnem.core.waveform.continuous.segments.Segment;

/**
 *
 * @author dodge
 */
public class OracleStaChanSegmentInfoDAO implements SegmentInfoDAO {

    private final String wfdiscTable;
    private final String siteTable;

    public OracleStaChanSegmentInfoDAO(String wfdiscTable, String siteTable) {
        this.wfdiscTable = wfdiscTable;
        this.siteTable = siteTable;
    }

    @Override
    public ArrayList<ChannelSegmentCatalog> getChannelSegments(StreamKey channel, StationSelectionMode mode) throws Exception {
        ArrayList<ChannelSegmentCatalog> result = new ArrayList<>();

        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();

            if (mode == StationSelectionMode.SINGLE_STATION) {
                result.add(getSingleCatalog(channel, conn));
            } else {
                Collection<StreamKey> streams = getArrayStreams(channel, conn);
                for (StreamKey aKey : streams) {
                    result.add(getSingleCatalog(aKey, conn));
                }
            }
            return result;
        } finally {
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    private Collection<StreamKey> getArrayStreams(StreamKey channel, Connection conn) throws SQLException {
        Collection<StreamKey> result = new ArrayList<>();
        String sql = String.format("select /*+ parallel(12) ordered use_nl(b) */ distinct b.sta from %s a, %s b where refsta = ? and a.sta = b.sta and jdate "
                + "between ondate and offdate and net = ? and chan = ? and locid = ? order by chan", siteTable, wfdiscTable);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, channel.getSta());
            stmt.setString(2, channel.getNet());
            stmt.setString(3, channel.getChan());
            stmt.setString(4, channel.getLocationCode());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idx = 1;
                    String sta = rs.getString(idx++);
                    result.add(new StreamKey(channel.getNet(), sta, channel.getChan(), channel.getLocationCode()));
                }
            }
            return result;
        }
    }

    private ChannelSegmentCatalog getSingleCatalog(StreamKey channel, Connection conn) throws SQLException {
        ChannelSegmentCatalog catalog = new ChannelSegmentCatalog(channel);
        String netClause = "net = ? and ";
        boolean useNet = true;
        if (channel.getNet().equals("*") || channel.getNet().equals("?") || channel.getNet().isEmpty()) {
            netClause = "";
            useNet = false;
        }

        String locidClause = " and locid = ?";
        boolean useLocid = true;
        if (channel.getLocationCode().equals("*") || channel.getLocationCode().equals("?") || channel.getLocationCode().isEmpty()) {
            locidClause = "";
            useLocid = false;
        }

        String sql = String.format("select wfid, time,endtime, samprate from %s where %s sta = ? and chan = ? %s order by time", wfdiscTable, netClause, locidClause);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int jdx = 1;
            if (useNet) {
                stmt.setString(jdx++, channel.getNet());
            }
            stmt.setString(jdx++, channel.getSta());
            stmt.setString(jdx++, channel.getChan());
            if (useLocid) {
                stmt.setString(jdx++, channel.getLocationCode());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idx = 1;
                    int wfid = rs.getInt(idx++);
                    double time = rs.getDouble(idx++);
                    double endtime = rs.getDouble(idx++);
                    double rate = rs.getDouble(idx++);
                    Segment segment = new Segment(wfid, time, endtime, rate);
                    catalog.addSegment(segment);
                }

                return catalog;
            }
        }

    }

    @Override
    public Collection<String> getAvailableNetworks(StationSelectionMode mode) throws Exception {
        Collection<String> result = new ArrayList<>();
        Connection conn = null;
        String sql = String.format("select /*+ parallel(12) */ distinct net from %s order by net", wfdiscTable);
        if (mode == StationSelectionMode.ARRAY_REFSTA) {
            sql = String.format("select  /*+ parallel(12) */ distinct net from %s a, %s b where a.sta = b.sta and b.sta != b.refsta order by net", wfdiscTable, siteTable);
        }
        try {
            conn = ConnectionManager.getInstance().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getString(1));
                    }
                    return result;
                }
            }
        } finally {
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    @Override
    public Collection<String> getAvailableStations(String net, StationSelectionMode mode) throws Exception {
        Collection<String> result = new ArrayList<>();
        Connection conn = null;
        String sql = String.format("select  /*+ parallel(12) */ distinct sta from %s where net = ? order by sta", wfdiscTable);
        if (mode == StationSelectionMode.ARRAY_REFSTA) {
            sql = String.format("select  /*+ parallel(12) */ distinct refsta from %s a, %s b where net = ?  and a.sta = b.sta and jdate between ondate and offdate order by refsta", wfdiscTable, siteTable);
        }
        try {
            conn = ConnectionManager.getInstance().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, net);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getString(1));
                    }
                    return result;
                }
            }
        } finally {
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    @Override
    public Collection<String> getAvailableChannels(String net, String sta, StationSelectionMode mode) throws Exception {
        Collection<String> result = new ArrayList<>();
        Connection conn = null;
        String sql = String.format("select  /*+ parallel(12) */ distinct chan from %s where net = ? and sta = ? order by chan", wfdiscTable);
        String arg1 = net;
        String arg2 = sta;
        if (mode == StationSelectionMode.ARRAY_REFSTA) {
            sql = String.format("select  /*+ parallel(12) */ distinct chan from %s a, %s b where refsta = ? and a.sta = b.sta and jdate "
                    + "between ondate and offdate and net = ? order by chan", siteTable, wfdiscTable);
            arg1 = sta;
            arg2 = net;
        }
        try {
            conn = ConnectionManager.getInstance().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, arg1);
                stmt.setString(2, arg2);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getString(1));
                    }
                    return result;
                }
            }
        } finally {
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    @Override
    public Collection<String> getAvailableLocids(String net, String sta, String chan, StationSelectionMode mode) throws Exception {
        Collection<String> result = new ArrayList<>();
        Connection conn = null;
        String sql = String.format("select  /*+ parallel(12) */ distinct locid from %s where net = ? and sta = ? and chan = ? order by locid", wfdiscTable);
        String arg1 = net;
        String arg2 = sta;
        if (mode == StationSelectionMode.ARRAY_REFSTA) {
            sql = String.format("select  /*+ parallel(12) */ distinct locid from %s a, %s b where refsta = ? and a.sta = b.sta and jdate "
                    + "between ondate and offdate and net = ? and chan = ? order by chan", siteTable, wfdiscTable);
            arg1 = sta;
            arg2 = net;
        }
        try {
            conn = ConnectionManager.getInstance().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, arg1);
                stmt.setString(2, arg2);
                stmt.setString(3, chan);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getString(1));
                    }
                    return result;
                }
            }
        } finally {
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    @Override
    public TimeT getLastSampleTime(String net, String sta, String chan) throws Exception {
        Connection conn = null;
        String sql = String.format("select  /*+ parallel(12) */ max(endtime) from %s where net = ? and sta = ? and chan = ?", wfdiscTable);
        try {
            conn = ConnectionManager.getInstance().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, net);
                stmt.setString(2, sta);
                stmt.setString(3, chan);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        return new TimeT(rs.getDouble(1));
                    }
                    TimeT tmp = new TimeT();
                    double minusOneYear = tmp.getEpochTime() - TimeT.AVG_DAYS_PER_YEAR * TimeT.SECPERDAY;
                    return new TimeT(minusOneYear);
                }
            }
        } finally {
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

}
