/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.oracle.waveformUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.dataObjects.continuous.ChannelSegmentCatalog;
import llnl.gnem.core.dataAccess.dataObjects.continuous.ContinuousSeismogram;
import llnl.gnem.core.dataAccess.dataObjects.continuous.Segment;
import llnl.gnem.core.dataAccess.dataObjects.continuous.StationSelectionMode;
import llnl.gnem.core.dataAccess.dataObjects.continuous.StreamSupport;
import llnl.gnem.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class CssUtil {

    public static ArrayList<ChannelSegmentCatalog> getChannelSegments(StreamKey channel, StationSelectionMode mode) throws Exception {
        ArrayList<ChannelSegmentCatalog> result = new ArrayList<>();

        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();

            if (mode == StationSelectionMode.SINGLE_STATION) {
                result.addAll(getSingleCatalog(channel, conn));
            } else {
                Collection<StreamKey> streams = getArrayStreams(channel, conn);
                for (StreamKey aKey : streams) {
                    result.addAll(getSingleCatalog(aKey, conn));
                }
            }
            return result;
        } finally {
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    public static Collection<ChannelSegmentCatalog> getSingleCatalog(StreamKey channel, Connection conn) throws SQLException {
        Map<Long, ChannelSegmentCatalog> rateCatalogMap = new HashMap<>();
        String tableName = DAOFactory.getInstance().getSeismogramSourceInfo().getTableName();

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

        String sql = String.format("select wfid, time,endtime, samprate from %s where %s sta = ? and chan = ? %s order by time", tableName, netClause, locidClause);
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
                    Long lrate = Math.round(rate);
                    ChannelSegmentCatalog csc = rateCatalogMap.get(lrate);
                    if (csc == null) {
                        csc = new ChannelSegmentCatalog(channel);
                        rateCatalogMap.put(lrate, csc);
                    }
                    csc.addSegment(segment);
                }

                return rateCatalogMap.values();
            }
        }

    }

    public static Collection<StreamKey> getArrayStreams(StreamKey channel, Connection conn) throws SQLException {
        String wfdiscTable = DAOFactory.getInstance().getSeismogramSourceInfo().getTableName();
        String siteTable = "llnl.site";
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

    public static Collection<String> getAgencies() {
        return new ArrayList<>(); // No agency concept is supported in CSS schema
    }

    public static Collection<String> getAvailableNetworks(StationSelectionMode mode) throws SQLException {
        String wfdiscTable = DAOFactory.getInstance().getSeismogramSourceInfo().getTableName();
        String siteTable = "llnl.site";
        Collection<String> result = new ArrayList<>();
        Connection conn = null;
        String sql = String.format("select /*+ parallel(12) */ distinct net from %s order by net", wfdiscTable);
        if (mode == StationSelectionMode.ARRAY_REFSTA) {
            sql = String.format("select  /*+ parallel(12) */ distinct net from %s a, %s b where a.sta = b.sta and b.sta != b.refsta order by net", wfdiscTable, siteTable);
        }
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getString(1));
                    }
                    return result;
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    public static Collection<Integer> getNetworkStartDates(String net, StationSelectionMode mode) {
        return new ArrayList<>(); // network start date not used for Css continuous data.
    }

    public static Collection<String> getAvailableStations(String net, StationSelectionMode mode) throws SQLException {
        String wfdiscTable = DAOFactory.getInstance().getSeismogramSourceInfo().getTableName();
        String siteTable = "llnl.site";
        Collection<String> result = new ArrayList<>();
        Connection conn = null;
        String sql = String.format("select  /*+ parallel(12) */ distinct sta from %s where net = ? order by sta", wfdiscTable);
        if (mode == StationSelectionMode.ARRAY_REFSTA) {
            sql = String.format("select  /*+ parallel(12) */ distinct refsta from %s a, %s b where net = ?  and a.sta = b.sta and jdate between ondate and offdate order by refsta", wfdiscTable, siteTable);
        }
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
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
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    public static Collection<String> getAvailableChannels(String net, String sta, StationSelectionMode mode) throws SQLException {
        String wfdiscTable = DAOFactory.getInstance().getSeismogramSourceInfo().getTableName();
        String siteTable = "llnl.site";
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
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
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
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    public static Collection<String> getAvailableLocids(String net, String sta, String chan, StationSelectionMode mode) throws SQLException {
        String wfdiscTable = DAOFactory.getInstance().getSeismogramSourceInfo().getTableName();
        String siteTable = "llnl.site";
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
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
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
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    public static TimeT getLastSampleTime(String net, String sta, String chan) throws SQLException {
        String wfdiscTable = DAOFactory.getInstance().getSeismogramSourceInfo().getTableName();

        Connection conn = null;
        String sql = String.format("select  /*+ parallel(12) */ max(endtime) from %s where net = ? and sta = ? and chan = ?", wfdiscTable);
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, net);
                stmt.setString(2, sta);
                stmt.setString(3, chan);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        return new TimeT(rs.getDouble(1));
                    }
                    TimeT tmp = new TimeT();
                    //    double minusOneYear = tmp.getEpochTime() - TimeT.AVG_DAYS_PER_YEAR * TimeT.SECPERDAY;
                    return tmp;//new TimeT(minusOneYear);
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    public static ContinuousSeismogram getContinuousSeismogramFromCssExtTable(StreamKey name, Epoch epoch) throws Exception {
        Collection<CssSeismogram> segments = new ArrayList<>();
        Connection conn = null;
        String tableName = DAOFactory.getInstance().getSeismogramSourceInfo().getTableName();
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();

            String netClause = "net = ? and ";
            boolean useNet = true;
            if (name.getNet() == null || name.getNet().equals("*") || name.getNet().equals("?") || name.getNet().isEmpty()) {
                netClause = "";
                useNet = false;
            }

            String locidClause = "locid = ? and ";
            boolean useLocid = true;
            if (name.getLocationCode() == null || name.getLocationCode().equals("*") || name.getLocationCode().equals("?") || name.getLocationCode().isEmpty()) {
                locidClause = "";
                useLocid = false;
            }
            String sql = String.format("select wfid, net, locid from %s a where %s sta = ? and chan = ? and %s endtime > ? and time < ? order by time",
                    tableName, netClause, locidClause);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int wfid = rs.getInt(1);
                        String aNet = rs.getString(2);
                        String aLocid = rs.getString(3);
                        CssSeismogram seis = WaveformUtil.getSingleSeismogramFromCssTable(wfid, tableName, conn);
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
                }
            }
        } finally {
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    public static StreamSupport getStreamSupportFromCssExtTable(StreamKey name, int minJdate, int maxJdate) throws SQLException {
        Connection conn = null;
        String tableName = DAOFactory.getInstance().getSeismogramSourceInfo().getTableName();
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();

            String netClause = "net = ? and ";
            boolean useNet = true;
            if (name.getNet() == null || name.getNet().equals("*") || name.getNet().equals("?") || name.getNet().isEmpty()) {
                netClause = "";
                useNet = false;
            }

            String locidClause = "locid = ? and ";
            boolean useLocid = true;
            if (name.getLocationCode() == null || name.getLocationCode().equals("*") || name.getLocationCode().equals("?") || name.getLocationCode().isEmpty()) {
                locidClause = "";
                useLocid = false;
            }
            String sql = String.format("select /*= parallel */\n"
                    + " min(time) min_time, max(endtime) max_time, median(samprate) samprate\n"
                    + "  from %s\n"
                    + " where %s sta = ?\n"
                    + "   and chan = ? and %s \n"
                    + "   time between jdate2epoch(?) - 7200 and jdate2epoch(?)\n"
                    + "   and endtime between jdate2epoch(?) and jdate2epoch(?) + 7200",
                    tableName, netClause, locidClause);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int jdx = 1;
                if (useNet) {
                    stmt.setString(jdx++, name.getNet());
                }
                stmt.setString(jdx++, name.getSta());
                stmt.setString(jdx++, name.getChan());
                if (useLocid) {
                    stmt.setString(jdx++, name.getLocationCode());
                }
                stmt.setInt(jdx++, minJdate);
                stmt.setInt(jdx++, maxJdate);
                stmt.setInt(jdx++, minJdate);
                stmt.setInt(jdx++, maxJdate);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        double minTime = rs.getDouble(1);
                        double maxTime = rs.getDouble(2);
                        double sampRate = rs.getDouble(3);
                        return new StreamSupport(new Epoch(minTime, maxTime), sampRate);
                    }
                }
            }
        } finally {
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return null;
    }

    public static Collection<ChannelSegmentCatalog> getChannelSegments(StreamKey key, Epoch epoch) throws SQLException {
        Map<StreamRateKey, ChannelSegmentCatalog> rateCatalogMap = new HashMap<>();
        String tableName = DAOFactory.getInstance().getSeismogramSourceInfo().getTableName();

        boolean useNet = key.getNet() != null && !key.getNet().isEmpty();
        String netClause = useNet ? "net = ? and " : "";

        boolean useChan = key.getChan() != null && !key.getChan().isEmpty();
        String chanClause = useChan ? "chan = ? and " : "";

        boolean useLocid = key.getLocationCode() != null && !key.getLocationCode().isEmpty();
        String locidClause = useLocid ? " and locid = ?" : "";

        String sql = String.format("select net,chan,locid,wfid, time,endtime, samprate from %s where %s sta = ? and %s %s endtime >= ? and time <= ? order by time",
                tableName, netClause, chanClause, locidClause);
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int jdx = 1;
                if (useNet) {
                    stmt.setString(jdx++, key.getNet());
                }
                stmt.setString(jdx++, key.getSta());
                if (useChan) {
                    stmt.setString(jdx++, key.getChan());
                }
                if (useLocid) {
                    stmt.setString(jdx++, key.getLocationCode());
                }
                stmt.setDouble(jdx++, epoch.getStart());
                stmt.setDouble(jdx++, epoch.getEnd());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int idx = 1;
                        String anet = OracleDBUtil.getStringFromCursor(rs, idx++);
                        String achan = OracleDBUtil.getStringFromCursor(rs, idx++);
                        String alocid = OracleDBUtil.getStringFromCursor(rs, idx++);
                        StreamKey akey = new StreamKey(anet, key.getSta(), achan, alocid);
                        int wfid = rs.getInt(idx++);
                        double time = rs.getDouble(idx++);
                        double endtime = rs.getDouble(idx++);
                        double rate = rs.getDouble(idx++);
                        Segment segment = new Segment(wfid, time, endtime, rate);
                        Long lrate = Math.round(rate);
                        StreamRateKey mkey = new StreamRateKey(akey, lrate);
                        ChannelSegmentCatalog csc = rateCatalogMap.get(mkey);
                        if (csc == null) {
                            csc = new ChannelSegmentCatalog(akey);
                            rateCatalogMap.put(mkey, csc);
                        }
                        csc.addSegment(segment);
                    }

                    return rateCatalogMap.values();
                }
            }
        } finally {
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }
}
