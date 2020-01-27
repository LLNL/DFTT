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
package llnl.gnem.apps.detection.source;

import llnl.gnem.apps.detection.core.framework.StreamProcessor;

import llnl.gnem.apps.detection.gaps.GapManager;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.dataObjects.ChannelSubstitution;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.database.dao.OracleWaveformDAO;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.merge.IntWaveform;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.core.waveform.merge.MergeException;
import llnl.gnem.core.waveform.merge.NamedIntWaveform;

import llnl.gnem.core.waveform.merge.WaveformMerger;

/**
 * Created by dodge1 Date: Jul 14, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public abstract class SourceData {

    public static final int QUEUE_DEPTH = 2;
    public static final String REPROCESS_THREAD_NAME = "ReprocessAllBlocksThread";
    public static final int SLEEP_MILLIS = 10000;
    private String wfdiscTable;
    private double minTime;
    private double maxTime;
    private final double BLOCK_SIZE = 7200;
    private final Collection<PairT<String, String>> channels;
    private final Collection<String> stations;
    private double commonRate;
    private final String configName;
    private double currentStartTime;
    private final ArrayBlockingQueue<Collection<WaveformSegment>> retrievedSegments;
    private boolean hasMoreData;
    private boolean stopRetrieving;
    private final String threadName;
    private double currentEndOfData = -Double.MAX_VALUE;
    private final ArrayList<NamedIntWaveform> cache;
    private final boolean scaleByCalib;
    private HashMap<String, ChannelSubstitution> channelSubstitutions;
    private double fixedRawRate = -999;

    public SourceData(String streamGroup, boolean scaleByCalib) throws SQLException {
        this.configName = streamGroup;
        channels = new ArrayList<>();
        stations = new HashSet<>();
        wfdiscTable = "wfdisc_stage";
        retrievedSegments = new ArrayBlockingQueue<>(QUEUE_DEPTH);
        hasMoreData = true;
        stopRetrieving = false;
        threadName = "RetrieveAllBlocksThread";
        cache = new ArrayList<>();
        this.scaleByCalib = scaleByCalib;
        channelSubstitutions = new HashMap<>();
    }

    protected void setWfdiscTable(String srcWfdiscTable) {
        wfdiscTable = srcWfdiscTable;
    }

    public Epoch getTimeRange() {
        return new Epoch(minTime, maxTime);
    }

    public Collection<String> getStations() {
        return new ArrayList<>(stations);
    }

    public Collection<StreamKey> getStaChan() {
        Collection<StreamKey> result = new ArrayList<>();
        channels.stream().forEach((pair) -> {
            result.add(new StreamKey(pair.getFirst(), pair.getSecond()));
        });
        return result;
    }

    public void retrieveAllBlocks(boolean exitOnFileEnd) throws SQLException, InterruptedException {

        Thread.currentThread().setName(threadName);
        TimeT blockStartTime = new TimeT(currentStartTime);
        if (blockStartTime.getJdate() > ProcessingPrescription.getInstance().getMaxJdateToProcess()) {
            retrievedSegments.put(new ArrayList<>());// poison pill to be used by streamServer to shut down
            hasMoreData = false;
            return;
        }
        while (currentStartTime + BLOCK_SIZE < maxTime || !exitOnFileEnd) {
            if (!getDataBlockForCurrentTime()) {
                break;
            }
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
        }
        ApplicationLogger.getInstance().log(Level.FINEST, "No more blocks to retrieve in (retrieveAllBlocks)");
        retrievedSegments.offer(new ArrayList<>(), 10l, TimeUnit.SECONDS);// poison pill to be used by streamServer to shut down
        ApplicationLogger.getInstance().log(Level.FINEST, "Inserted termination block into queue in (retrieveAllBlocks)");
        hasMoreData = false;
    }

    public boolean isHasMoreData() {
        return hasMoreData || !retrievedSegments.isEmpty();
    }

    private boolean getDataBlockForCurrentTime() throws SQLException, InterruptedException {

        if (stopRetrieving) {
            return false;
        }
        double start = currentStartTime;
        double end = start + BLOCK_SIZE;
        double secondsRetrieved = BLOCK_SIZE;

        double endOfData = getEndOfData(start);
        if (currentEndOfData < endOfData) {
            String msg = String.format("New file end time = %s", new TimeT(endOfData));
            ApplicationLogger.getInstance().log(Level.FINEST, msg);
            currentEndOfData = endOfData;
        }
        if (endOfData < end) {
            if (endOfData > start) {
                end = endOfData;
                secondsRetrieved = end - start;
            } else {
                Thread.sleep(SLEEP_MILLIS);
                return true;
            }

        }
        Collection<WaveformSegment> trimmed = createTrimmedWaveformCollection(end, start, scaleByCalib);
        double lastSampleTime = getLastSampleTime(trimmed);
        ApplicationLogger.getInstance().log(Level.FINEST, "Inserting block into queue in (getDataBlockForCurrentTime)");
        try {
            retrievedSegments.put(trimmed);
            ApplicationLogger.getInstance().log(Level.FINEST, "Inserting block into queue in (getDataBlockForCurrentTime)");
            currentStartTime += (secondsRetrieved + 1 / commonRate);
            double predictedStartTime = lastSampleTime + 1 / commonRate;
            currentStartTime = predictedStartTime;
            return true;
        } catch (InterruptedException ex) {
            return false;
        }
    }

    private Collection<WaveformSegment> createTrimmedWaveformCollection(double end, double start, boolean scaleByCalib) throws SQLException, IllegalStateException {
        Collection<WaveformSegment> result = new ArrayList<>();
        Collection<NamedIntWaveform> namedWaveforms = getNamedWaveformCollection(end, start);
        for (NamedIntWaveform waveform : namedWaveforms) {
            if (scaleByCalib) {
                waveform.scaleByCalib();
            }
             WaveformSegment aSeg = new WaveformSegment(waveform, commonRate);
            result.add(aSeg);
        }
        return result;

    }

    private double getEndOfData(double start) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        double earliestEnd = Double.MAX_VALUE;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select max(endtime) from %s where sta = ? and chan = ? and endtime >= ?", wfdiscTable));
            for (PairT<String, String> val : channels) {
                stmt.setString(1, val.getFirst());
                stmt.setString(2, val.getSecond());
                stmt.setDouble(3, start - 100000);
                Double thisEnd = getEndQueryResult(stmt);
                if (thisEnd != null && thisEnd < earliestEnd) {
                    earliestEnd = thisEnd;
                }
            }
            return earliestEnd;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    private Double getEndQueryResult(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                double tmp = rs.getDouble(1);
                if (rs.wasNull()) {
                    return null;
                } else {
                    return tmp;
                }
            }
            return null;
        }
    }

    private Collection<NamedIntWaveform> getNamedWaveformCollection(double end, double start) throws SQLException {

        PreparedStatement stmt = null;
        Connection aConn = null;

        try {
            aConn = ConnectionManager.getInstance().checkOut();

            stmt = aConn.prepareStatement(String.format("select distinct time, endtime,nsamp,samprate,dir,dfile, foff, datatype, sta, chan, wfid, calib, calper, locid from %s  "
                    + "where sta = ? and chan = ? and time <= ? and ? <= endtime order by locid, time", wfdiscTable));

            Collection<NamedIntWaveform> namedWaveforms = new ArrayList<>();
            for (PairT<String, String> staChan : channels) {

                stmt.setString(1, staChan.getFirst());
                stmt.setString(2, staChan.getSecond());
                stmt.setDouble(3, end);
                stmt.setDouble(4, start);

                try {
                    NamedIntWaveform waveform = getRows(start, end, stmt, staChan.getFirst(), staChan.getSecond());
                    if (waveform == null) {
                        waveform = maybeGetSubstitute(staChan, start, end, stmt);
                        if (waveform == null) {
                            waveform = createEmptySegment(start, end, staChan.getFirst(), staChan.getSecond(), commonRate);
                        }
                    }
                    waveform = GapManager.getInstance().maybeFillGaps(waveform);

                    namedWaveforms.add(waveform);
                } catch (MergeException | SQLException e) {
                    ApplicationLogger.getInstance().log(Level.SEVERE, e.getMessage(), e);
                }

            }
            return namedWaveforms;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (aConn != null) {
                ConnectionManager.getInstance().checkIn(aConn);
            }
        }
    }

    private NamedIntWaveform maybeGetSubstitute(PairT<String, String> staChan, double start, double end, PreparedStatement stmt) throws MergeException, SQLException {
        if (channelSubstitutions != null) {
            ChannelSubstitution subs = channelSubstitutions.get(staChan.getSecond());
            if (subs != null) {
                Collection<String> alternates = subs.getSubstitutions();
                for (String aChan : alternates) {
                    stmt.setString(2, aChan);
                    NamedIntWaveform altWave = getRows(start, end, stmt, staChan.getFirst(), aChan);
                    if (altWave != null) {
                        return new NamedIntWaveform(altWave.getWfid(),
                                staChan.getFirst(),
                                staChan.getSecond(),
                                altWave.getStart(),
                                altWave.getRate(),
                                altWave.getData());
                    }
                }
            }
        }
        return null;
    }

    public Collection<WaveformSegment> retrieveDataBlock(TimeT startTime, double duration, boolean scaleByCalib) throws Exception {

        ApplicationLogger.getInstance().log(Level.FINE, String.format("Retrieving new primary data block starting at %s and extending for %9.3f s", startTime, duration));
        double start = startTime.getEpochTime();
        double end = start + duration;
        Collection<WaveformSegment> trimmed = createTrimmedWaveformCollection(end, start, scaleByCalib);
        return trimmed;
    }

    private NamedIntWaveform getRows(double requestedStart, double requestedEnd, PreparedStatement stmt, String requestedSta, String requestedChan) throws MergeException, SQLException {

        NamedIntWaveform result = retrieveFromCache(requestedStart, requestedEnd, requestedSta, requestedChan);
        if (result != null) {
            return result;
        }
        Map<String, Collection<NamedIntWaveform>> locidWaveformMap = new TreeMap<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                try {
                    PairT<String, NamedIntWaveform> res = createNamedWaveform(requestedStart, requestedEnd, rs);
                    if (res != null) {
                        Collection<NamedIntWaveform> nw = locidWaveformMap.get(res.getFirst());
                        if (nw == null) {
                            nw = new ArrayList<>();
                            locidWaveformMap.put(res.getFirst(), nw);
                        }
                        nw.add(res.getSecond());
                    }
                } catch (SQLException | IOException e) {
                    ApplicationLogger.getInstance().log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
        if (locidWaveformMap.isEmpty()) {
            return null;//createEmptySegment(requestedStart, requestedEnd, requestedSta, requestedChan, expectedRate);
        }

        ArrayList<NamedIntWaveform> best = getBestSpanningSet(requestedStart, requestedEnd, locidWaveformMap);

        if (best.isEmpty()) {
            return null;//createEmptySegment(requestedStart, requestedEnd, requestedSta, requestedChan, expectedRate);
        }
        result = best.get(0);
        boolean ignoreMergeError = true;
        boolean ignoreMismatchedSamples = true;
        for (int j = 1; j < best.size(); ++j) {
            NamedIntWaveform waveform = best.get(j);
            if (!waveform.isEmpty()) {
                IntWaveform wf = WaveformMerger.mergeWaveforms(waveform, result, ignoreMergeError, ignoreMismatchedSamples);
                result = new NamedIntWaveform(wf.getWfid(), waveform.getSta(), waveform.getChan(), wf.getStart(), wf.getRate(), wf.getData());
            }
        }

        if (result != null) {
            if (result.getStart() - requestedStart > 1 / result.getRate()) {
                IntWaveform wf = result.getNewStartCopy(requestedStart);
                result = new NamedIntWaveform(wf, result.getSta(), result.getChan());
            }
            if (Math.abs(result.getEnd() - requestedEnd) > 1 / result.getRate()) {
                IntWaveform wf = result.getNewEndCopy(requestedEnd);
                result = new NamedIntWaveform(wf, result.getSta(), result.getChan());
            }
            addToCache(result);
        } else {
            return result;
        }
        if (result.getNpts() < 10) {
            ApplicationLogger.getInstance().log(Level.WARNING, "After merge, length = " + result.getNpts());
            result = null;
        }

        return result;

    }

    private void createConfiguration(String configName) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("insert into configuration  select configid.nextval, ? from dual");
            stmt.setString(1, configName);
            stmt.execute();
            conn.commit();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            ConnectionManager.getInstance().checkIn(conn);
        }

    }

    private Collection<Integer> getStreamids(String configName) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Collection<Integer> result = new ArrayList<>();
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select streamid from configuration a, stream b  where config_name = ? and a.configid = b.configid");
            stmt.setString(1, configName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt(1));
            }
            return result;
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

    private void listChannels(int streamid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            System.out.println(String.format("Station-channel combinations in stream %d are:", streamid));
            stmt = conn.prepareStatement("select distinct sta, chan\n"
                    + "  from stream_channel where streamid = ? order by sta, chan");
            stmt.setInt(1, streamid);
            rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                String sta = rs.getString(1);
                String chan = rs.getString(2);
                System.out.println(String.format("\t%s\t%s", sta, chan));
                ++count;
            }
            System.out.println("======================================================\n");
            if (count < 1) {
                throw new IllegalStateException(String.format("Stream %d has no channels!", streamid));
            }
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

    public Collection<WaveformSegment> getDataBlock() throws InterruptedException {
        return retrievedSegments.take();

    }

    private PairT<String, NamedIntWaveform> createNamedWaveform(double requestedStart, double requestedEnd, ResultSet rs) throws SQLException, IOException {
        int jdx = 1;
        double time = rs.getDouble(jdx++);
        double endtime = rs.getDouble(jdx++);
        int nsamp = rs.getInt(jdx++);
        double samprate = rs.getDouble(jdx++);
        if (commonRate == 0) {
            commonRate = samprate;
        }
        String dir = rs.getString(jdx++);
        String dfile = rs.getString(jdx++);
        int foff = rs.getInt(jdx++);
        String datatype = rs.getString(jdx++);
        String sta = rs.getString(jdx++);
        String chan = rs.getString(jdx++);
        int sourceWfid = rs.getInt(jdx++);
        double calib = rs.getDouble(jdx++);
        double calper = rs.getDouble(jdx++);
        String locid = rs.getString(jdx++);
        ApplicationLogger.getInstance().log(Level.FINER, String.format("\t\t\tRetrieving %d samples of waveform data for wfid %d...", nsamp, sourceWfid));
        int[] data = new int[nsamp];
        try {
            data = OracleWaveformDAO.getInstance().getSeismogramDataAsIntArray(dir, dfile, foff, nsamp, datatype);
            if (samprate != commonRate) {
                data = interpolateToCommonRate(data, samprate, commonRate);
            }
        } catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.FINER, String.format("Failed reading file %s/%s. Missing data will be replaced with zeros.", dir, dfile), e);

        }

        int idx = 0;
        double start = time;
        if (requestedStart > time) { // The segment starts earlier than we need...
            start = requestedStart;
            idx = (int) Math.round((start - time) * commonRate);
        }

        int endIdx = data.length - 1;
        if (requestedEnd < endtime) {  // The segment extends past what we need.
            endIdx = (int) Math.round((requestedEnd - time) * commonRate);
        }

        int length = endIdx - idx + 1;
        if (length < 2) {
            return null;
        }
        int[] resultArray = new int[length];
        if (data.length >= resultArray.length && idx >= 0 && idx + length <= data.length) {
            System.arraycopy(data, idx, resultArray, 0, length);
        }
        ApplicationLogger.getInstance().log(Level.FINEST, String.format("\t\t\t\tRetrieved %d-point segment starting "
                + "%8.3f seconds from request window start for %s - %s",
                length, start - requestedStart, sta, chan));
        int wfid = 1;
        IntWaveform waveform = new IntWaveform(wfid, start, commonRate, resultArray);
        return new PairT<>(locid, new NamedIntWaveform(waveform, sta, chan, calib, calper, "-", "-", "-", new ArrayList<>()));
    }

    /**
     * Called by constructor in FrameworkRunner
     *
     * @throws SQLException
     */
    public void summarize(Integer startingJdate) throws SQLException {
        if (!configExists()) {
            createConfiguration(configName);
        }
        summarizeStreams();
        populateTimeRange(startingJdate);
        getChannels();
    }

    private boolean configExists() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select *  from configuration  where config_name = ?");
            stmt.setString(1, configName);
            rs = stmt.executeQuery();
            return (rs.next());
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

    private void summarizeStreams() throws SQLException {

        Collection<Integer> streamids = getStreamids(configName);
        if (streamids.isEmpty()) {
            throw new IllegalStateException(String.format("Configuration %s (specified in parameter file) has no streams!", configName));
        } else {
            for (int streamid : streamids) {
                listChannels(streamid);
            }
        }

    }

    private void getChannels() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        stations.clear();
        channels.clear();
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select  d.sta, c.chan\n"
                    + "   from configuration a,stream b, stream_channel c, %s d\n"
                    + "   where config_name = ?\n"
                    + "   and a.configid = b.configid\n"
                    + "   and b.streamid = c.streamid\n"
                    + "   and d.sta = c.sta\n"
                    + "   and c.chan = d.chan \n"
                    + "union\n"
                    + "select  d.sta, c.chan\n"
                    + "   from configuration a,stream b, stream_channel c, %s d, channel_substitution e\n"
                    + "   where config_name = ?\n"
                    + "   and a.configid = b.configid\n"
                    + "   and b.streamid = c.streamid\n"
                    + "   and d.sta = c.sta\n"
                    + "   and (c.chan = d.chan or (c.chan = e.chan and e.CHAN_SUB = d.chan and e.CONFIGID = a.CONFIGID)) \n"
                    + "order by sta, chan", wfdiscTable, wfdiscTable));
            stmt.setString(1, configName);
            stmt.setString(2, configName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String sta = rs.getString(1);
                stations.add(sta);
                String chan = rs.getString(2);
                channels.add(new PairT<>(sta, chan));
            }
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

    /**
     * Get the time range extending from the latest start to the earliest end of
     * the data.
     *
     * @throws SQLException For various database errors.
     */
    private void populateTimeRange(Integer startingJdate) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int minJdate = startingJdate == null ? ProcessingPrescription.getInstance().getMinJdateToProcess() : startingJdate;
        int maxJdate = ProcessingPrescription.getInstance().getMaxJdateToProcess();
        try {
            String query = String.format("select min(time) minTime, max(endtime) maxTime, median(samprate) medrate\n"
                    + " from(\n"
                    + "select time,endtime,samprate \n"
                    + "   from configuration a, stream b, stream_channel c, %s d\n"
                    + "   where config_name = ?\n"
                    + "   and a.configid = b.configid\n"
                    + "   and b.streamid = c.streamid\n"
                    + "   and d.sta = c.sta\n"
                    + "   and c.chan = d.chan  and jdate between ? and ?\n"
                    + "union\n"
                    + "select time,endtime,samprate \n"
                    + "   from configuration a, stream b, stream_channel c, %s d, channel_substitution e\n"
                    + "   where config_name = ?\n"
                    + "   and a.configid = b.configid\n"
                    + "   and b.streamid = c.streamid\n"
                    + "   and d.sta = c.sta\n"
                    + "   and ((c.chan = e.chan(+) and e.CHAN_SUB = d.chan and e.CONFIGID = a.CONFIGID)) and jdate between ? and ?)", wfdiscTable, wfdiscTable);
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(query);
            int jdx = 1;
            stmt.setString(jdx++, configName);
            stmt.setInt(jdx++, minJdate);
            stmt.setInt(jdx++, maxJdate);
            stmt.setString(jdx++, configName);
            stmt.setInt(jdx++, minJdate);
            stmt.setInt(jdx++, maxJdate);
            rs = stmt.executeQuery();
            while (rs.next()) {
                minTime = rs.getDouble(1);
                maxTime = rs.getDouble(2);
                if (minJdate == maxJdate) {
                    maxTime = minTime + TimeT.SECPERDAY;
                }
                if (maxTime <= minTime || rs.wasNull()) {
                    String msg = String.format("Data in WFDISC do not span requested range of %d to %d", minJdate, maxJdate);
                    ApplicationLogger.getInstance().log(Level.INFO, msg);
                    populateTimeRangeUnrestricted();
                    return;
                }
                currentStartTime = minTime;

                commonRate = fixedRawRate > 0 ? fixedRawRate : Math.round(rs.getDouble(3));

                double passbandUpperCorner = StreamsConfig.getInstance().getMaxPassbandUpperCorner();
                if (passbandUpperCorner >= commonRate / 2) {
                    throw new IllegalStateException("Wideband passband upper corner is >= Nyquist frequency of data!");
                }
            }
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

    public abstract void close() throws SQLException, IOException;

    public String getWfdiscTable() {
        return wfdiscTable;
    }

    public void printSummary() throws SQLException {
        TimeT start = new TimeT(minTime);
        TimeT end = new TimeT(maxTime);
        ApplicationLogger.getInstance().log(Level.FINE, String.format("Data extend from %s to %s", start, end));

        StringBuilder sb = new StringBuilder("\n");
        for (PairT<String, String> pair : channels) {
            sb.append(String.format("%s  %s\n", pair.getFirst(), pair.getSecond()));
        }
        ApplicationLogger.getInstance().log(Level.FINER, String.format("Available sta-chan are: %s", sb.toString()));
    }

    public double getCommonSampleRate() {
        return commonRate;
    }

    public boolean supports(StreamProcessor processor) {
        Collection<StreamKey> requiredChannels = processor.getChannels();
        Collection<StreamKey> myChannels = getStaChan();
        if (!requiredChannels.stream().noneMatch((sc) -> (!myChannels.contains(sc)))) {
            return false;
        }
        return true;
    }

    public synchronized void stopRetrieving() {
        stopRetrieving = true;
    }

    private NamedIntWaveform createEmptySegment(double requestedStart, double requestedEnd, String requestedSta, String requestedChan, double expectedRate) {

        double duration = requestedEnd - requestedStart;
        long npts = Math.round(duration * expectedRate) + 1;
        int[] data = new int[(int) npts];
        Arrays.fill(data, 0);
        return new NamedIntWaveform(1,
                requestedSta,
                requestedChan,
                requestedStart,
                expectedRate,
                data);
    }

    public void setStaChanArrays() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        stations.clear();
        channels.clear();
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select distinct c.sta, c.chan\n"
                    + "   from configuration a,stream b, stream_channel c\n"
                    + "   where config_name = ?\n"
                    + "   and a.configid = b.configid\n"
                    + "   and b.streamid = c.streamid order by c.sta, c.chan\n"));
            stmt.setString(1, configName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String sta = rs.getString(1);
                stations.add(sta);
                String chan = rs.getString(2);
                channels.add(new PairT<>(sta, chan));
            }
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

    private void populateTimeRangeUnrestricted() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            ApplicationLogger.getInstance().log(Level.INFO, "Retrieving time span of data (unrestricted)...");
            String query = String.format("select min(time) minTime, max(endtime) maxTime, median(samprate) medrate\n"
                    + "   from configuration a, stream b, stream_channel c, %s d\n"
                    + "   where config_name = ?\n"
                    + "   and a.configid = b.configid\n"
                    + "   and b.streamid = c.streamid\n"
                    + "   and d.sta = c.sta\n"
                    + "   and d.chan = c.chan", wfdiscTable);
            stmt = conn.prepareStatement(query);
            stmt.setString(1, configName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                minTime = rs.getDouble(1);
                maxTime = rs.getDouble(2);
                if (maxTime <= minTime || rs.wasNull()) {
                    String msg = String.format("WFDISC contains no data for any channels in configuration! stopping execution.");
                    ApplicationLogger.getInstance().log(Level.INFO, msg);
                    System.exit(1);
                }
                currentStartTime = minTime;
                commonRate = Math.round(rs.getDouble(3));
                double passbandUpperCorner = StreamsConfig.getInstance().getMaxPassbandUpperCorner();
                if (passbandUpperCorner >= commonRate / 2) {
                    throw new IllegalStateException("Wideband passband upper corner is >= Nyquist frequency of data!");
                }
            }
            ApplicationLogger.getInstance().log(Level.INFO, "Done retrieving time span of data.");
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

    private NamedIntWaveform retrieveFromCache(double requestedStart, double requestedEnd, String requestedSta, String requestedChan) {
        for (NamedIntWaveform wf : cache) {
            if (wf.getSta().equals(requestedSta) && wf.getChan().equals(requestedChan)) {
                if (requestedStart >= wf.getStart() && requestedEnd <= wf.getEnd()) {
                    return wf.getSubset(new Epoch(requestedStart, requestedEnd));
                }
            }
        }

        return null;
    }

    private void addToCache(NamedIntWaveform namedWaveform) {
        for (NamedIntWaveform wf : cache) {
            if (wf.getSta().equals(namedWaveform.getSta()) && wf.getChan().equals(namedWaveform.getChan())) {
                cache.remove(wf);
                break;
            }
        }
        cache.add(namedWaveform);
    }

    private double getLastSampleTime(Collection<WaveformSegment> trimmed) {
        double result = -Double.MAX_VALUE;
        for (WaveformSegment segment : trimmed) {
            double endTime = segment.getEndtimeAsDouble();
            if (endTime > result) {
                result = endTime;
            }
        }
        return result;
    }

    private int[] interpolateToCommonRate(int[] data, double samprate, double newRate) {
        float[] y = new float[data.length];
        float xStart = 0;
        float dx = (float) (1.0 / samprate);
        for (int j = 0; j < data.length; ++j) {
            y[j] = data[j];
        }
        double expectedLastSampleTime = (data.length - 1) * dx;
        double newSampleInterval = 1.0 / newRate;
        int requiredSamples = (int) (expectedLastSampleTime / newSampleInterval + 1);
        float[] xinterp = new float[requiredSamples];
        for (int j = 0; j < requiredSamples; ++j) {
            xinterp[j] = (float) (j * newSampleInterval);
        }
        float[] tmp = SeriesMath.interpolate(xStart, dx, y, xinterp);
        int[] result = new int[tmp.length];
        for (int j = 0; j < tmp.length; ++j) {
            result[j] = Math.round(tmp[j]);
        }
        return result;
    }

    public void setCommonSampleRate(Collection<Double> times) throws SQLException {
        Connection aConn = null;
        if (this.fixedRawRate > 0) {
            commonRate = fixedRawRate;
            return;
        }
        try {
            ApplicationLogger.getInstance().log(Level.INFO, "Determining median sample rate...");
            aConn = ConnectionManager.getInstance().checkOut();
            String query = String.format("select  /*+ parallel(24) */ median(samprate)\n"
                    + "   from configuration a, stream b, stream_channel c, %s d\n"
                    + "   where config_name = ?\n"
                    + "   and a.configid = b.configid\n"
                    + "   and b.streamid = c.streamid\n"
                    + "   and d.sta = c.sta\n"
                    + "   and d.chan = c.chan", wfdiscTable);
            try (PreparedStatement stmt = aConn.prepareStatement(query)) {
                stmt.setString(1, configName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        commonRate = rs.getDouble(1);
                    } else {
                        throw new IllegalStateException("Unable to set common sample rate for configuration!");
                    }
                }

            }

            ApplicationLogger.getInstance().log(Level.INFO, "Done setting common sample rate.");
        } finally {
            ConnectionManager.getInstance().checkIn(aConn);
        }
    }

    private ArrayList<NamedIntWaveform> getBestSpanningSet(double requestedStart, double requestedEnd, Map<String, Collection<NamedIntWaveform>> locidWaveformMap) {
        String bestLocid = null;
        Epoch bestEpoch = null;

        for (String locid : locidWaveformMap.keySet()) {
            Epoch span = computeEpoch(locidWaveformMap.get(locid));
            if (span.ContainsTime(new TimeT(requestedStart)) && span.ContainsTime(new TimeT(requestedEnd))) {
                return new ArrayList<>(locidWaveformMap.get(locid));
            }
            if (bestLocid == null || bestEpoch == null) {
                bestLocid = locid;
                bestEpoch = span;
            } else if (span.duration() > bestEpoch.duration()) {
                bestLocid = locid;
                bestEpoch = span;
            }
        }
        if (bestLocid != null) {
            return new ArrayList<>(locidWaveformMap.get(bestLocid));
        } else {
            return new ArrayList<>();
        }
    }

    private Epoch computeEpoch(Collection<NamedIntWaveform> waveforms) {
        double minT = Double.MAX_VALUE;
        double maxT = -minT;
        for (NamedIntWaveform w : waveforms) {
            if (w.getStart() < minT) {
                minT = w.getStart();
            }
            if (w.getEnd() > maxT) {
                maxT = w.getEnd();
            }
        }
        return new Epoch(minT, maxT);
    }

    public void setChannelSubstitutions(Map<String, ChannelSubstitution> channelSubstitutions) {
        this.channelSubstitutions = new HashMap<>(channelSubstitutions);
    }

    public void setFixedRawRate(double fixedRawRate) {
        this.fixedRawRate = fixedRawRate;
    }
}
