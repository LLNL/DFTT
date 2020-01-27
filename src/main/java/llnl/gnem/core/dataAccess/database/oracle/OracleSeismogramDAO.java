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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.CorrelationTraceData;
import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.ApplicationStationInfo;
import llnl.gnem.core.dataAccess.dataObjects.ComponentKey;
import llnl.gnem.core.dataAccess.dataObjects.DefaultProgressMonitor;
import llnl.gnem.core.dataAccess.dataObjects.ProgressMonitor;
import llnl.gnem.core.dataAccess.dataObjects.StreamEpochInfo;
import llnl.gnem.core.dataAccess.dataObjects.StreamInfo;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.interfaces.SeismogramDAO;
import llnl.gnem.core.dataAccess.interfaces.StationDAO;
import llnl.gnem.core.dataAccess.interfaces.StreamDAO;
import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.seismicData.EventInfo;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.StationKey;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.waveform.components.ComponentIdentifier;
import llnl.gnem.core.waveform.components.ComponentSet;
import llnl.gnem.core.waveform.components.ComponentSetException;
import llnl.gnem.core.waveform.components.RotationStatus;
import llnl.gnem.core.waveform.io.BinaryData;
import llnl.gnem.core.waveform.io.BinaryDataReader;
import llnl.gnem.core.waveform.responseProcessing.TransferStatus;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataUnits;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class OracleSeismogramDAO implements SeismogramDAO {

    @Override
    public CssSeismogram getSeismogram(long waveformId) throws DataAccessException {
        try {
            return getSeismogramP(waveformId);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private CssSeismogram getSeismogramP(long waveformId) throws Exception {
        String sql = String.format("select station_source,network_code,"
                + "net_start_date,station_code, chan, "
                + "location_code, begin_time, nsamp, "
                + "samprate,data_type, calib,calper, "
                + "foff, dir, dfile from %s where waveform_id = ?", TableNames.WAVEFORM_VIEW);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, waveformId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (Thread.interrupted()) {
                    return null;
                }
                int jdx = 1;
                String stationSource = rs.getString(jdx++);
                String networkCode = rs.getString(jdx++);
                int netStartDate = rs.getInt(jdx++);
                String sta = rs.getString(jdx++);
                StationKey staKey = new StationKey(stationSource, networkCode, netStartDate, sta);
                String chan = rs.getString(jdx++);
                String locationCode = rs.getString(jdx++);
                StreamKey streamKey = new StreamKey(staKey, chan, locationCode);
                double time = rs.getDouble(jdx++);
                int nsamp = rs.getInt(jdx++);
                double samprate = rs.getDouble(jdx++);
                String datatype = rs.getString(jdx++);
                Double calib = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    calib = null;
                }
                Double calper = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    calper = null;
                }
                int foff = rs.getInt(jdx++);
                String dir = rs.getString(jdx++);
                String dfile = rs.getString(jdx++);
                float[] floatData = getSeismogramDataAsFloatArray(dir, dfile, foff, nsamp, datatype);
                return new CssSeismogram(waveformId, streamKey, floatData, samprate, new TimeT(time), calib, calper);

            }
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            DAOFactory.getInstance().getConnections().checkIn(conn);

        }

    }

    static float[] getSeismogramDataAsFloatArray(String dir, String dfile, int foff, int nsamp, String datatype) throws Exception {
        String fname = dir + '/' + dfile;

        BinaryDataReader bdr = BinaryDataReader.getReader(datatype);
        if (bdr != null) {
            BinaryData bd = bdr.readData(fname, foff, nsamp);
            return bd.getFloatData();
        } else {
            throw new IllegalStateException("No BinaryDataReader was instantiated. Could not read data.");
        }

    }

    @Override
    public Collection<BaseSingleComponent> getComponentData(long eventId, double delta, ProgressMonitor monitor) throws DataAccessException {
        try {
            ProgressMonitor myMonitor = monitor != null ? monitor : new DefaultProgressMonitor();
            return getComponentDataP(eventId, delta, myMonitor);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    @Override
    public CorrelationComponent getCorrelationComponent(long waveformId, long eventId, String phase) throws DataAccessException {
        try {
            return getCorrelationComponentP(waveformId, eventId, phase);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<CorrelationComponent> getNearbyEvents(long eventId,
            int streamId,
            String phase,
            double separationKm,
            ProgressMonitor monitor) throws DataAccessException {
        try {
            ProgressMonitor myMonitor = monitor != null ? monitor : new DefaultProgressMonitor();
            return CorrelationProcessing.getNearbyEventsP(eventId, streamId, phase, separationKm, myMonitor);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private CorrelationComponent getCorrelationComponentP(long waveformId, long eventId, String phase) throws Exception {
        StreamDAO streamDAO = new OracleStreamDAO();
        StationDAO stationDAO = new OracleStationDAO();
        CssSeismogram seis = getSeismogramP(waveformId);
        int streamId = getStreamIdForWaveform(waveformId);
        StreamEpochInfo info = streamDAO.getBestStreamEpoch(streamId, seis.getTimeAsDouble());
        ApplicationStationInfo asi = stationDAO.getStationInfoForWaveform(waveformId);
        NominalArrival arrival = DAOFactory.getInstance().getArrivalDAO().getNominalArrival(eventId,
                asi.getStationId(), (int) streamId, phase);
        StreamKey key = seis.getStreamKey();
        ComponentIdentifier identifier = new ComponentIdentifier(info.getStreamInfo().getBand(),
                info.getStreamInfo().getInstrumentCode(),
                info.getStreamInfo().getOrientation(),
                key.getLocationCode());
        WaveformDataType dataType = WaveformDataType.counts;
        WaveformDataUnits dataUnits = WaveformDataUnits.unknown;
        CorrelationTraceData ctd = new CorrelationTraceData(seis, dataType, dataUnits, arrival);
        EventInfo event = new OracleEventDataDAO().getEventInfo(eventId);
        CorrelationComponent component = new CorrelationComponent(asi,
                identifier,
                ctd,
                TransferStatus.UNTRANSFERRED,
                RotationStatus.UNROTATED,
                event, info);
        return component;
    }

    private Collection<SeisDataHolder> getComponentDataPP(long eventId, double delta, ProgressMonitor monitor) throws SQLException {
        Collection<SeisDataHolder> result = new ArrayList<>();
        String sql = String.format("select * from (\n"
                + "select /*+ ordered use_nl(b,c,d,e,f,g) */\n"
                + " a.waveform_id,\n"
                + " h.source_code,\n"
                + " f.network_id,\n"
                + " a.network_code,\n"
                + " a.net_start_date,\n"
                + " a.station_code,\n"
                + " a.chan,\n"
                + " a.location_code,\n"
                + " a.array_id,\n"
                + " a.array_element_id,\n"
                + " b.begin_time,\n"
                + " b.nsamp,\n"
                + " b.samprate         wfdisc_rate,\n"
                + " b.data_type,\n"
                + " c.calib,\n"
                + " c.calper,\n"
                + " b.foff,\n"
                + " b.dir,\n"
                + " b.dfile,\n"
                + " a.stream_id,\n"
                + " f.station_id,\n"
                + " d.band,\n"
                + " d.instrument_code,\n"
                + " d.orientation_code,\n"
                + " d.description,\n"
                + " e.begin_time       stream_begin,\n"
                + " e.end_time         stream_end,\n"
                + " e.azimuth,\n"
                + " e.dip,\n"
                + " e.samprate         stream_rate,\n"
                + " e.depth,\n"
                + " f.description      sta_descrip,\n"
                + " g.lat              stla,\n"
                + " g.lon              stlo,\n"
                + " g.elev             stelev,\n"
                + " g.begin_time       sta_begin,\n"
                + " g.end_time         sta_end,\n"
                + " g.station_epoch_id,\n"
                + " e.stream_epoch_id, a.degdist,\n"
                + " rank() over(partition by f.station_id order by d.band,e.stream_epoch_id desc,a.waveform_id) arank\n"
                + "  from %s a,\n"
                + "       %s b,\n"
                + "       %s c,\n"
                + "       %s d,\n"
                + "       %s e,\n"
                + "       %s f,\n"
                + "       %s g,\n"
                + "       %s h\n"
                + " where a.event_id = ?\n"
                + "   and a.waveform_id = b.waveform_id\n"
                + "   and a.waveform_id = c.waveform_id(+)\n"
                + "   and a.stream_id = d.stream_id\n"
                + "   and a.stream_id = e.stream_id\n"
                + "   and b.begin_time between e.begin_time and e.end_time\n"
                + "   and a.used_station_id = f.station_id\n"
                + "   and a.used_epoch_id = g.station_epoch_id\n"
                + "   and f.source_id = h.source_id\n"
                + "   and d.band in ('B', 'H', 'S')\n"
                + "   and d.orientation_code = 'Z' and degdist <= ?)\n"
                + "   where arank = 1 order by degdist",
                TableNames.SEARCH_LINK_TABLE,
                TableNames.WAVEFORM_SEGMENT_TABLE,
                TableNames.WAVEFORM_CSS_CAL_FACTOR_TABLE,
                TableNames.STREAM_TABLE,
                TableNames.STREAM_EPOCH_TABLE,
                TableNames.STATION_TABLE,
                TableNames.STATION_EPOCH_TABLE,
                TableNames.SOURCE_TABLE);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        monitor.setProgressStateIndeterminate(true);
        try {
            int count = 0;
            final long startTime = System.currentTimeMillis();
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            monitor.setText("starting query");
            stmt.setLong(1, eventId);
            stmt.setDouble(2, delta);
            rs = stmt.executeQuery();
            monitor.setText("Processing result set");
            while (rs.next()) {
                if (Thread.interrupted()) {
                    return null;
                }
                int jdx = 1;
                long waveformId = rs.getLong(jdx++);
                String stationSource = rs.getString(jdx++);
                int networkId = rs.getInt(jdx++);
                String networkCode = rs.getString(jdx++);
                int netDate = rs.getInt(jdx++);
                String stationCode = rs.getString(jdx++);
                String chan = rs.getString(jdx++);
                String locationCode = rs.getString(jdx++);
                Long arrayId = rs.getLong(jdx++);
                if (rs.wasNull()) {
                    arrayId = null;
                }
                Long arrayElementId = rs.getLong(jdx++);
                if (rs.wasNull()) {
                    arrayElementId = null;
                }
                double beginTime = rs.getDouble(jdx++);
                int nsamp = rs.getInt(jdx++);
                double wfdiscRate = rs.getDouble(jdx++);
                String dataType = rs.getString(jdx++);
                Double calib = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    calib = null;
                }
                Double calper = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    calper = null;
                }
                int foff = rs.getInt(jdx++);
                String dir = rs.getString(jdx++);
                String dfile = rs.getString(jdx++);
                int streamId = rs.getInt(jdx++);
                int stationId = rs.getInt(jdx++);
                String band = rs.getString(jdx++);
                if (rs.wasNull()) {
                    band = null;
                }
                String instrumentCode = rs.getString(jdx++);
                if (rs.wasNull()) {
                    instrumentCode = null;
                }
                String orientationCode = rs.getString(jdx++);
                if (rs.wasNull()) {
                    orientationCode = null;
                }
                String description = rs.getString(jdx++);
                if (rs.wasNull()) {
                    description = null;
                }
                double streamBegin = rs.getDouble(jdx++);
                double streamEnd = rs.getDouble(jdx++);
                Double azimuth = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    azimuth = null;
                }
                Double dip = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    dip = null;
                }
                Double streamRate = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    streamRate = null;
                }
                Double depth = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    depth = null;
                }
                String staDescrip = rs.getString(jdx++);
                double stla = rs.getDouble(jdx++);
                double stlo = rs.getDouble(jdx++);
                Double stelev = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    stelev = null;
                }
                double staBegin = rs.getDouble(jdx++);
                double staEnd = rs.getDouble(jdx++);
                int stationEpochId = rs.getInt(jdx++);
                int streamEpochId = rs.getInt(jdx++);

                StreamKey streamKey = new StreamKey(stationSource, networkCode, netDate, stationCode, chan, locationCode);
                StreamInfo streamInfo = new StreamInfo(streamId,
                        stationId,
                        streamKey,
                        band,
                        instrumentCode,
                        orientationCode,
                        description);
                StreamEpochInfo info = new StreamEpochInfo(streamEpochId,
                        streamInfo,
                        streamBegin,
                        streamEnd,
                        depth,
                        azimuth,
                        dip,
                        streamRate);
                ApplicationStationInfo asi = new ApplicationStationInfo(stationSource,
                        networkCode,
                        netDate,
                        stationCode,
                        staDescrip,
                        stla,
                        stlo,
                        stelev,
                        staBegin,
                        staEnd,
                        networkId,
                        stationId,
                        stationEpochId,
                        arrayId,
                        arrayElementId);
                SeisDataHolder holder = new SeisDataHolder(dir, dfile, foff, nsamp, dataType, waveformId, streamKey, wfdiscRate, beginTime, calib, calper, info, asi);
                result.add(holder);
                ++count;
            }
            final long endTime = System.currentTimeMillis();
            monitor.setText(String.format("Retrieved %d records for evid %d in %d ms", count, eventId, endTime - startTime));
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            DAOFactory.getInstance().getConnections().checkIn(conn);

        }

    }

    private Collection<BaseSingleComponent> getComponentDataP(long eventId, double delta, ProgressMonitor monitor) throws SQLException {
        Collection<SeisDataHolder> holders = getComponentDataPP(eventId, delta, monitor);
        monitor.setProgressStateIndeterminate(false);
        int singleStationCount = getSingleStationCount(holders);
        monitor.setRange(0, singleStationCount);
        MonitorWrapper wrapper = new MonitorWrapper(monitor);
        long start = System.currentTimeMillis();
        List<BaseSingleComponent> ss = holders.parallelStream().map(s -> s.produceSSComponent(TraceType.SINGLE_STATION, wrapper)).filter(Objects::nonNull).collect(Collectors.toList());
        Collection<SeisDataHolder> arList = getSingleArrayElements(holders);

        wrapper.resetCounter();
        monitor.setRange(0, arList.size());
        List<BaseSingleComponent> ar = arList.parallelStream().map(s -> s.produceSSComponent(TraceType.ARRAY, wrapper)).filter(Objects::nonNull).collect(Collectors.toList());

        long end = System.currentTimeMillis();
        monitor.setText(String.format("Processed into components in %d ms", end - start));
        ss.addAll(ar);
        return new ArrayList<>(ss);
    }

    private Collection<SeisDataHolder> getSingleArrayElements(Collection<SeisDataHolder> holders) {
        Set<Long> ids = holders.stream().map(s -> s.getArrayId()).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, List<SeisDataHolder>> parMap = holders.stream().filter(SeisDataHolder::isArray).collect(Collectors.groupingBy(SeisDataHolder::getArrayId));
        Collection<SeisDataHolder> arList = new ArrayList<>();
        ids.stream().map((id) -> parMap.get(id)).forEachOrdered((tmp) -> {
            arList.add(tmp.get(0));
        });
        return arList;
    }

    private int getSingleStationCount(Collection<SeisDataHolder> holders) {
        int count = 0;
        count = holders.stream().filter((holder) -> (holder.isSingleStation())).map((_item) -> 1).reduce(count, Integer::sum);
        return count;
    }

    static class MonitorWrapper {

        private final ProgressMonitor monitor;
        private final AtomicInteger counter;

        public MonitorWrapper(ProgressMonitor monitor) {
            this.monitor = monitor;
            counter = new AtomicInteger(0);
        }

        public void increment() {
            int val = counter.incrementAndGet();
            monitor.setValue(val);
        }

        private void resetCounter() {
            counter.set(0);
        }
    }

    private static enum TraceType {
        ARRAY, SINGLE_STATION
    };

    private static class SeisDataHolder {

        private final String dir;
        private final String dfile;
        private final int foff;
        private final int nsamp;
        private final String dataType;
        private final long waveformId;
        private final StreamKey streamKey;
        private final double wfdiscRate;
        private final double beginTime;
        private final Double calib;
        private final Double calper;
        private final StreamEpochInfo info;
        private final ApplicationStationInfo asi;

        public SeisDataHolder(String dir, String dfile, int foff, int nsamp,
                String dataType, long waveformId, StreamKey streamKey,
                double wfdiscRate, double beginTime, Double calib,
                Double calper, StreamEpochInfo info, ApplicationStationInfo asi) {
            this.dir = dir;
            this.dfile = dfile;
            this.foff = foff;
            this.nsamp = nsamp;
            this.dataType = dataType;
            this.waveformId = waveformId;
            this.streamKey = streamKey;
            this.wfdiscRate = wfdiscRate;
            this.beginTime = beginTime;
            this.calib = calib;
            this.calper = calper;
            this.info = info;
            this.asi = asi;
        }

        public BaseSingleComponent produceSSComponent(TraceType traceType, MonitorWrapper wrapper) {
            if (asi.getArrayId() != null && traceType == TraceType.SINGLE_STATION) {
                return null;
            } else if (asi.getArrayId() == null && traceType != TraceType.SINGLE_STATION) {
                return null;
            }
            try {
                float[] floatData = getSeismogramDataAsFloatArray(dir,
                        dfile, foff, nsamp, dataType);
                CssSeismogram seis = new CssSeismogram(waveformId,
                        streamKey, floatData, wfdiscRate,
                        new TimeT(beginTime), calib, calper);
                wrapper.increment();
                return new BaseSingleComponent(seis, info, asi);
            } catch (Exception ex) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Failed retrieving seismogram!", ex);
                return null;
            }
        }

        private boolean isArray() {
            return asi.getArrayId() != null;
        }

        private boolean isSingleStation() {
            return asi.getArrayId() == null;
        }

        private Long getArrayId() {
            return asi.getArrayId();
        }

    }

    @Override
    public Collection<ComponentSet> getComponentSets(long eventId, StationInfo station) throws DataAccessException {
        try {
            return getComponentSetsP(eventId, station);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private Collection<ComponentSet> getComponentSetsP(long eventId, StationInfo station) throws SQLException {
        Collection<ComponentSet> result = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            Collection<BaseSingleComponent> bsc = getSingleComponents(eventId, station, conn);
            Map<ComponentKey, Collection<BaseSingleComponent>> keyCompMap = new HashMap<>();
            for (BaseSingleComponent sc : bsc) {
                ComponentKey key = sc.getComponentKey();
                Collection<BaseSingleComponent> components = keyCompMap.get(key);
                if (components == null) {
                    components = new ArrayList<>();
                    keyCompMap.put(key, components);
                }
                components.add(sc);
            }

            for (ComponentKey key : keyCompMap.keySet()) {
                try {
                    ComponentSet set = new ComponentSet(keyCompMap.get(key));
                    result.add(set);
                } catch (ComponentSetException ex) {
                    ApplicationLogger.getInstance().log(Level.FINE,
                            String.format("Failed forming component set for (%s) because of empty intersection.", key.toString()));
                    for (BaseSingleComponent bsc2 : keyCompMap.get(key)) {
                        result.add(new ComponentSet(bsc2));
                    }
                }
            }
            return result;
        } finally {
            DAOFactory.getInstance().getConnections().checkIn(conn);

        }

    }

    private Collection<BaseSingleComponent> getSingleComponents(long eventId, StationInfo station, Connection conn) throws SQLException {
        Collection<BaseSingleComponent> result = new ArrayList<>();
        StreamDAO streamDAO = new OracleStreamDAO();
        StationDAO stationDAO = new OracleStationDAO();
        String sql = String.format("select waveform_id, a.stream_id from \n"
                + "%s a, \n"
                + "%s b where event_id = ? \n"
                + "and a.stream_id = b.stream_id and b.station_id = ?\n"
                + "order by  band",
                TableNames.WAVEFORM_SEGMENT_TABLE,
                TableNames.STREAM_TABLE);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, eventId);
            stmt.setLong(2, station.getStationId());
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (Thread.interrupted()) {
                    return null;
                }
                try {
                    int jdx = 1;
                    long waveformId = rs.getLong(jdx++);

                    int streamId = rs.getInt(jdx++);

                    CssSeismogram seis = this.getSeismogramP(waveformId);
                    StreamEpochInfo info = streamDAO.getBestStreamEpoch(streamId, seis.getTimeAsDouble());
                    ApplicationStationInfo asi = stationDAO.getStationInfoForWaveform(waveformId);
                    result.add(new BaseSingleComponent(seis, info, asi));
                } catch (Exception ex) {
                    ApplicationLogger.getInstance().log(Level.SEVERE, "Failed retrieving component seismogram!", ex);
                }

            }
            return result;
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
    public int getStreamIdForWaveform(long waveformId) throws DataAccessException {
        try {
            return getStreamIdForWaveformP(waveformId);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private int getStreamIdForWaveformP(long waveformId) throws SQLException {
        String sql = String.format("select stream_id from %s where waveform_id = ?",
                TableNames.WAVEFORM_SEGMENT_TABLE);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, waveformId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
            throw new IllegalStateException("No such waveform (" + waveformId + ")!");
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            DAOFactory.getInstance().getConnections().checkIn(conn);

        }

    }

}
