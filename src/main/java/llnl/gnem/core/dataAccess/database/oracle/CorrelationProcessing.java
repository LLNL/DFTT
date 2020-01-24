/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.CorrelationTraceData;
import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.dataObjects.ApplicationStationInfo;
import llnl.gnem.core.dataAccess.dataObjects.ProgressMonitor;
import llnl.gnem.core.dataAccess.dataObjects.StreamEpochInfo;
import llnl.gnem.core.dataAccess.dataObjects.StreamInfo;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.database.oracle.OracleSeismogramDAO.MonitorWrapper;
import llnl.gnem.core.seismicData.EventInfo;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Geometry.EModel;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.components.ComponentIdentifier;
import llnl.gnem.core.waveform.components.RotationStatus;
import llnl.gnem.core.waveform.responseProcessing.TransferStatus;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataUnits;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
class CorrelationProcessing {

    static Collection<CorrelationComponent> getNearbyEventsP(long eventId,
            int streamId,
            String phase,
            double separationKm,
            ProgressMonitor monitor) throws Exception {
        Collection<CorrSeisDataHolder> holders = getNearbyEventsQ(eventId, streamId, phase, separationKm, monitor);
        monitor.setProgressStateIndeterminate(false);
        monitor.setRange(0, holders.size());
        MonitorWrapper wrapper = new MonitorWrapper(monitor);
        long start = System.currentTimeMillis();
        List<CorrelationComponent> ar = holders.parallelStream().map(s -> s.produceComponent(wrapper)).filter(Objects::nonNull).collect(Collectors.toList());

        long end = System.currentTimeMillis();
        monitor.setText(String.format("Processed into components in %d ms", end - start));
        return new ArrayList<>(ar);
    }

    static Collection<CorrSeisDataHolder> getNearbyEventsQ(long eventId, int streamId, String phase, double separationKm,
            ProgressMonitor monitor) throws Exception {
        Collection<CorrSeisDataHolder> result = new ArrayList<>();

        double radius = separationKm * EModel.getDegreesPerKilometer();

        String sql = String.format("with pos as\n"
                + " (select lat tlat, lon tlon from %s where event_id = ?)\n"
                + "select *\n"
                + "  from (select a.event_id,\n"
                + "               a.olat,\n"
                + "               a.olon,\n"
                + "               a.depth odepth,\n"
                + "               a.otime,\n"
                + "               a.waveform_id,\n"
                + "               h.source_code,\n"
                + "               f.network_id,\n"
                + "               a.network_code,\n"
                + "               a.net_start_date,\n"
                + "               a.station_code,\n"
                + "               a.chan,\n"
                + "               a.location_code,\n"
                + "               a.array_id,\n"
                + "               a.array_element_id,\n"
                + "               b.begin_time,\n"
                + "               b.nsamp,\n"
                + "               b.samprate wfdisc_rate,\n"
                + "               b.data_type,\n"
                + "               c.calib,\n"
                + "               c.calper,\n"
                + "               b.foff,\n"
                + "               b.dir,\n"
                + "               b.dfile,\n"
                + "               f.station_id,\n"
                + "               d.band,\n"
                + "               d.instrument_code,\n"
                + "               d.orientation_code,\n"
                + "               d.description,\n"
                + "               e.begin_time stream_begin,\n"
                + "               e.end_time stream_end,\n"
                + "               e.azimuth,\n"
                + "               e.dip,\n"
                + "               e.samprate stream_rate,\n"
                + "               e.depth,\n"
                + "               f.description sta_descrip,\n"
                + "               g.lat stla,\n"
                + "               g.lon stlo,\n"
                + "               g.elev stelev,\n"
                + "               g.begin_time sta_begin,\n"
                + "               g.end_time sta_end,\n"
                + "               g.station_epoch_id,\n"
                + "               e.stream_epoch_id,\n"
                + "               dist(aa.tlat, aa.tlon, olat, olon) km_sep,\n"
                + "               rank() over(partition by a.event_id order by d.band, e.stream_epoch_id desc, a.waveform_id) arank\n"
                + "          from pos aa,\n"
                + "               %s  a,\n"
                + "               %s  b,\n"
                + "               %s  c,\n"
                + "               %s  d,\n"
                + "               %s  e,\n"
                + "               %s  f,\n"
                + "               %s  g,\n"
                + "               %s  h\n"
                + "         where olat between aa.tlat - %f and aa.tlat + %f\n"
                + "           and olon between aa.tlon - %f and aa.tlon + %f\n"
                + "           and dist(aa.tlat, aa.tlon, olat, olon) <= %f\n"
                + "           and a.stream_id = ?\n"
                + "           and a.waveform_id = b.waveform_id\n"
                + "           and a.waveform_id = c.waveform_id(+)\n"
                + "           and a.stream_id = d.stream_id\n"
                + "           and a.stream_id = e.stream_id\n"
                + "           and b.begin_time between e.begin_time and e.end_time\n"
                + "           and a.used_station_id = f.station_id\n"
                + "           and a.used_epoch_id = g.station_epoch_id\n"
                + "           and f.source_id = h.source_id)\n"
                + " where arank = 1\n"
                + " order by km_sep",
                TableNames.QUICK_ORIGIN_LOOKUP_TABLE,
                TableNames.SEARCH_LINK_TABLE,
                TableNames.WAVEFORM_SEGMENT_TABLE,
                TableNames.WAVEFORM_CSS_CAL_FACTOR_TABLE,
                TableNames.STREAM_TABLE,
                TableNames.STREAM_EPOCH_TABLE,
                TableNames.STATION_TABLE,
                TableNames.STATION_EPOCH_TABLE,
                TableNames.SOURCE_TABLE,
                radius, radius, radius, radius, separationKm);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        monitor.setProgressStateIndeterminate(true);
        try {
            int count = 0;
            final long startTime = System.currentTimeMillis();
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, eventId);
            stmt.setInt(2, streamId);
            monitor.setText("starting query");
            rs = stmt.executeQuery();
            monitor.setText("Processing result set");
            while (rs.next()) {
                if (Thread.interrupted()) {
                    return null;
                }
                int jdx = 1;
                long eventId2 = rs.getLong(jdx++);
                double olat = rs.getDouble(jdx++);
                double olon = rs.getDouble(jdx++);
                double odepth = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    odepth = 0.0;
                }
                double otime = rs.getDouble(jdx++);
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
                NominalArrival arrival = DAOFactory.getInstance().getArrivalDAO().getNominalArrival(eventId2,
                        asi.getStationId(), (int) streamId, phase);
                if(arrival != null)
                result.add(new CorrSeisDataHolder(dir, dfile,
                        foff, nsamp, dataType, waveformId, streamKey, wfdiscRate,
                        beginTime, calib, calper, info, asi, arrival, eventId2, olat, olon, odepth, otime));
                else{
                    ApplicationLogger.getInstance().log(Level.INFO, 
                            String.format("No %s arrival found or producible for event %d",phase,eventId2));
                }
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

    private static class CorrSeisDataHolder {

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
        private final NominalArrival arrival;
        private final long eventId;
        private final double olat;
        private final double olon;
        private final double depth;
        private final double time;

        public CorrSeisDataHolder(String dir, String dfile, int foff, int nsamp,
                String dataType, long waveformId, StreamKey streamKey,
                double wfdiscRate, double beginTime, Double calib,
                Double calper, StreamEpochInfo info, ApplicationStationInfo asi,
                NominalArrival arrival,
                long eventId, double olat, double olon, double depth, double time) {
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
            this.arrival = arrival;
            this.eventId = eventId;
            this.olat = olat;
            this.olon = olon;
            this.depth = depth;
            this.time = time;
        }

        public CorrelationComponent produceComponent(OracleSeismogramDAO.MonitorWrapper wrapper) {
            try {
                float[] floatData = OracleSeismogramDAO.getSeismogramDataAsFloatArray(dir,
                        dfile, foff, nsamp, dataType);
                CssSeismogram seis = new CssSeismogram(waveformId,
                        streamKey, floatData, wfdiscRate,
                        new TimeT(beginTime), calib, calper);

                StreamKey key = seis.getStreamKey();
                ComponentIdentifier identifier = new ComponentIdentifier(info.getStreamInfo().getBand(),
                        info.getStreamInfo().getInstrumentCode(),
                        info.getStreamInfo().getOrientation(),
                        key.getLocationCode());
                WaveformDataType waveDataType = WaveformDataType.counts;
                WaveformDataUnits dataUnits = WaveformDataUnits.unknown;
                CorrelationTraceData ctd = new CorrelationTraceData(seis, waveDataType, dataUnits, arrival);
                EventInfo event = new EventInfo(eventId, olat, olon, depth, time, "-");
                CorrelationComponent component = new CorrelationComponent(asi,
                        identifier,
                        ctd,
                        TransferStatus.UNTRANSFERRED,
                        RotationStatus.UNROTATED,
                        event, info);
                if (wrapper != null) {
                    wrapper.increment();
                }
                return component;
            } catch (Exception ex) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Failed retrieving seismogram!", ex);
                return null;
            }
        }

    }

}
