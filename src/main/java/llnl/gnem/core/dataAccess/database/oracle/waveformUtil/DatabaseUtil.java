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
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.dataObjects.continuous.ChannelSegmentCatalog;
import llnl.gnem.core.dataAccess.dataObjects.continuous.ContinuousSeismogram;
import llnl.gnem.core.dataAccess.dataObjects.continuous.Segment;
import llnl.gnem.core.dataAccess.dataObjects.continuous.StationSelectionMode;
import llnl.gnem.core.dataAccess.dataObjects.continuous.StreamSupport;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StationKey;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class DatabaseUtil {





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

    private static Collection<ChannelSegmentCatalog> getSingleCatalog(StreamKey channel, Connection conn) throws SQLException {
        Map<Long, ChannelSegmentCatalog> rateCatalogMap = new HashMap<>();

        String sql = String.format("select waveform_id, begin_time, end_time, samprate\n"
                + "  from %s\n"
                + " where agency = ?\n"
                + "   and network_code = ? and net_start_date = ?\n"
                + "   and station_code = ?\n"
                + "   and chan = ? and location_code = ?\n"
                + " order by begin_time",
                TableNames.getContinuousWaveformViewName());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            int jdx = 1;
            stmt.setString(jdx++, channel.getAgency());
            stmt.setString(jdx++, channel.getNet());
            stmt.setInt(jdx++, channel.getNetJdate());
            stmt.setString(jdx++, channel.getSta());
            stmt.setString(jdx++, channel.getChan());
            stmt.setString(jdx++, channel.getLocationCode());
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

    private static Collection<StreamKey> getArrayStreams(StreamKey channel, Connection conn) throws SQLException {
        Collection<StreamKey> result = new ArrayList<>();
        String sql = String.format("select /*+ parallel */\n"
                + "distinct a.station_code\n"
                + "  from (select distinct station_code\n"
                + "          from %s\n"
                + "         where agency = ?\n"
                + "           and network_code = ? and net_start_date = ? \n"
                + "           and array_name = ?) a,\n"
                + "       %s b\n"
                + " where agency = ?\n"
                + "   and network_code = ? and net_start_date = ? \n"
                + "   and a.station_code = b.station_code\n"
                + "   and chan = ?\n"
                + "   and location_code = ?\n"
                + " order by a.station_code",
                TableNames.getArrayMemberViewName(),
                TableNames.getContinuousWaveformViewName());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int jdx = 1;
            stmt.setString(jdx++, channel.getAgency());
            stmt.setString(jdx++, channel.getNet());
            stmt.setInt(jdx++, channel.getNetJdate());
            stmt.setString(jdx++, channel.getSta());
            stmt.setString(jdx++, channel.getAgency());
            stmt.setString(jdx++, channel.getNet());
            stmt.setInt(jdx++, channel.getNetJdate());
            stmt.setString(jdx++, channel.getChan());
            stmt.setString(jdx++, channel.getLocationCode());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idx = 1;
                    String sta = rs.getString(idx++);
                    result.add(new StreamKey(channel.getAgency(), channel.getNet(), channel.getNetJdate(), sta, channel.getChan(), channel.getLocationCode()));
                }
            }
            return result;
        }
    }

    public static ContinuousSeismogram getContinuousSeismogramFromDB(StreamKey name, Epoch epoch) throws Exception {
        Collection<CssSeismogram> segments = new ArrayList<>();
        Connection conn = null;
        boolean hasAgency = name.getAgency() != null;
        boolean hasNetwork = name.getNet() != null;
        boolean hasNetJdate = name.getNetJdate() != null;
        boolean hasLocid = name.getLocationCode() != null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            String agencyClause = hasAgency ? " agency = ? and " : "";
            String networkClause = hasNetwork ? " network_code = ? and " : "";
            String netJdateClause = hasNetJdate ? " net_start_date = ? and " : "";
            String locidClause = hasLocid ? " location_code = ? and " : "";
            String sql = String.format("select waveform_id from %s a where "
                    + " %s "
                    + " %s "
                    + " %s "
                    + " station_code = ? and "
                    + " chan = ? and "
                    + " %s "
                    + " end_time >= ? and begin_time <= ? order by begin_time",
                    TableNames.getContinuousWaveformViewName(),
                    agencyClause,
                    networkClause,
                    netJdateClause,
                    locidClause);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int jdx = 1;
                if (hasAgency) {
                    stmt.setString(jdx++, name.getAgency());
                }
                if (hasNetwork) {
                    stmt.setString(jdx++, name.getNet());
                }
                if (hasNetJdate) {
                    stmt.setInt(jdx++, name.getNetJdate());
                }
                stmt.setString(jdx++, name.getSta());
                stmt.setString(jdx++, name.getChan());
                if (hasLocid) {
                    stmt.setString(jdx++, name.getLocationCode());
                }
                stmt.setDouble(jdx++, epoch.getTime().getEpochTime());
                stmt.setDouble(jdx++, epoch.getEnd());
                try (ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        int wfid = rs.getInt(1);
                        CssSeismogram seis = getCssSeismogramUsingWaveformID(wfid, TableNames.getContinuousWaveformViewName(), conn);
                        if (seis.getEndtime().lt(epoch.getTime())) {
                            continue;
                        }
                        if (seis.getTime().ge(epoch.getEndtime())) {
                            continue;
                        }
                        if (seis.getTime().lt(epoch.getTime())) {
                            seis.cutBefore(epoch.getTime());
                        }
                        if (seis.getEndtime().gt(epoch.getEndtime())) {
                            seis.cutAfter(epoch.getEndtime());
                        }
                        StreamKey sk = new StreamKey(name.getAgency(), name.getNet(), name.getNetJdate(), name.getSta(), name.getChan(), name.getLocationCode());

                        CssSeismogram fseis = new CssSeismogram(seis.getWaveformID(), sk, seis.getData(), seis.getSamprate(), seis.getTime(), seis.getCalib(), seis.getCalper());
                        segments.add(fseis);
                    }
                    return segments.isEmpty() ? null : new ContinuousSeismogram(segments);
                }
            }

        } finally {

            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private static CssSeismogram getCssSeismogramUsingWaveformID(long waveformId, String viewName, Connection conn) throws Exception {
        String sql = String.format(
                "select \n"
                + "a.agency,\n"
                + "a.network_code,\n"
                + "a.net_start_date,\n"
                + "a.station_code,\n"
                + "a.chan,\n"
                + "a.location_code,\n"
                + "a.begin_time,\n"
                + "a.nsamp,\n"
                + "a.samprate,\n"
                + "a.data_type,\n"
                + "a.calib,\n"
                + "a.calper,\n"
                + "a.foff,\n"
                + "a.dir,\n"
                + "a.dfile\n"
                + "FROM  %s a where waveform_id = ?",
                viewName);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, waveformId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (Thread.interrupted()) {
                        return null;
                    }
                    int jdx = 1;
                    String agency = rs.getString(jdx++);
                    String networkCode = rs.getString(jdx++);
                    int netStartDate = rs.getInt(jdx++);
                    String sta = rs.getString(jdx++);
                    StationKey staKey = new StationKey(agency, networkCode, netStartDate, sta);
                    String chan = rs.getString(jdx++);
                    String locationCode = rs.getString(jdx++);
                    StreamKey streamKey = new StreamKey(staKey, chan, locationCode);
                    double time = rs.getDouble(jdx++);
                    int nsamp = rs.getInt(jdx++);
                    double samprate = rs.getDouble(jdx++);
                    String datatype = rs.getString(jdx++);
                    Double calib = OracleDBUtil.getDoubleFromCursor(rs, jdx++);
                    Double calper = OracleDBUtil.getDoubleFromCursor(rs, jdx++);
                    int foff = rs.getInt(jdx++);
                    String dir = rs.getString(jdx++);
                    String dfile = rs.getString(jdx++);
                    float[] floatData = WaveformUtil.getSeismogramDataAsFloatArray(dir, dfile, foff, nsamp, datatype);
                    return new CssSeismogram(waveformId, streamKey, floatData, samprate, new TimeT(time), calib, calper);

                }
                return null;
            }
        }

    }

    public static TimeT getLastSampleTime(String agency, String netCode, int netStartDate, String stationCode, String chanCode) throws SQLException {
        String sql = String.format("select max(begin_time)\n"
                + "  from %s\n"
                + " where agency = ?\n"
                + "   and network_code = ? and net_start_date = ? \n"
                + "   and station_code = ?\n"
                + "   and chan = ?",
                TableNames.getContinuousWaveformViewName());
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int jdx = 1;
                stmt.setString(jdx++, agency);
                stmt.setString(jdx++, netCode);
                stmt.setInt(jdx++, netStartDate);
                stmt.setString(jdx++, stationCode);
                stmt.setString(jdx++, chanCode);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        double time = rs.getDouble(1);
                        return new TimeT(time);
                    }
                }
            }
        } finally {
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return null;
    }

    public static StreamSupport getStreamSupport(StreamKey name, int minJdate, int maxJdate) throws SQLException {
        boolean hasAgency = name.getAgency() != null && !name.getAgency().isEmpty();
        String agencyClause = hasAgency ? "agency = ? and " : "";
        boolean hasNet = name.getNet() != null && !name.getNet().isEmpty();
        String netClause = hasNet ? "network_code = ? and " : "";
        boolean hasNetDate = name.getNetJdate() != null;
        String netDateClause = hasNetDate ? "net_start_date = ? and " : "";
        boolean hasLocid = name.getLocationCode() != null && !name.getLocationCode().isEmpty();
        String locidClause = hasLocid ? "location_code = ? and " : "";
        String sql = String.format("select /*= parallel */\n"
                + " min(begin_time) min_time,\n"
                + " max(end_time) max_time,\n"
                + " median(samprate) samprate\n"
                + "  from %s\n"
                + " where %s \n"
                + "    %s \n"
                + "    %s \n"
                + "    station_code = ? and \n"
                + "    chan = ? and \n"
                + "    %s \n"
                + "    begin_time between jdate2epoch(?) - 7200 and\n"
                + "       jdate2epoch(?)\n"
                + "   and end_time between jdate2epoch(?) and\n"
                + "       jdate2epoch(?) + 7200",
                TableNames.getContinuousWaveformViewName(),
                agencyClause,
                netClause,
                netDateClause,
                locidClause);
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int jdx = 1;
                if (hasAgency) {
                    stmt.setString(jdx++, name.getAgency());
                }
                if (hasNet) {
                    stmt.setString(jdx++, name.getNet());
                }
                if (hasNetDate) {
                    stmt.setInt(jdx++, name.getNetJdate());
                }
                stmt.setString(jdx++, name.getSta());
                stmt.setString(jdx++, name.getChan());
                if (hasLocid) {
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

        boolean hasAgency = key.getAgency() != null && !key.getAgency().isEmpty();
        String agencyClause = hasAgency ? "agency = ? and " : "";
        boolean hasNetwork = key.getNet() != null && !key.getNet().isEmpty();
        String networkClause = hasNetwork ? "network_code = ? and " : "";
        boolean hasNetStartDate = key.getNetJdate() != null;
        String netStartDateClause = hasNetStartDate ? "net_start_date = ? and " : "";

        boolean useChan = key.getChan() != null && !key.getChan().isEmpty();
        String chanClause = useChan ? "chan = ? and " : "";
        boolean hasLocid = key.getLocationCode() != null && !key.getLocationCode().isEmpty();
        String locidClause = hasLocid ? "location_code = ? and " : "";
        String sql = String.format("select  network_code,chan,location_code,waveform_id, begin_time, end_time, samprate\n"
                + "  from %s\n"
                + " where %s \n"
                + "    %s "
                + "    %s \n"
                + "    station_code = ? and \n"
                + "    %s "
                + "    %s "
                + "    end_time >= ? "
                + "   and begin_time <= ?\n"
                + " order by begin_time",
                TableNames.getContinuousWaveformViewName(),
                agencyClause,
                networkClause,
                netStartDateClause,
                chanClause,
                locidClause);
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                int jdx = 1;
                if (hasAgency) {
                    stmt.setString(jdx++, key.getAgency());
                }
                if (hasNetwork) {
                    stmt.setString(jdx++, key.getNet());
                }
                if (hasNetStartDate) {
                    stmt.setInt(jdx++, key.getNetJdate());
                }
                stmt.setString(jdx++, key.getSta());
                if (useChan) {
                    stmt.setString(jdx++, key.getChan());
                }
                if (hasLocid) {
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
