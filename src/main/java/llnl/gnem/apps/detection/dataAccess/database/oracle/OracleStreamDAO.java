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
package llnl.gnem.apps.detection.dataAccess.database.oracle;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.FrameworkPreprocessorParams;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayDetectorSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.bulletin.BulletinSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTASpecification;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.database.DbStreamDAO;
import llnl.gnem.apps.detection.dataAccess.database.SequenceNames;
import llnl.gnem.apps.detection.dataAccess.database.TableNames;

import llnl.gnem.apps.detection.util.initialization.BootDetectorParams;
import llnl.gnem.apps.detection.util.initialization.StreamInfo;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.Passband;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;
import oracle.sql.BLOB;

/**
 *
 * @author dodge1
 */
public class OracleStreamDAO extends DbStreamDAO {

    private OracleStreamDAO() {
    }

    public static OracleStreamDAO getInstance() {
        return OracleStreamDAOHolder.INSTANCE;
    }

    private static class OracleStreamDAOHolder {

        private static final OracleStreamDAO INSTANCE = new OracleStreamDAO();
    }

    @Override
    public void writeStreamParamsIntoConfiguration(int streamid, String stream, double sampleRate) throws DataAccessException {
        try {
            writeStreamParamsIntoConfigurationP(streamid, stream, sampleRate);
        } catch (SQLException | IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public int getStreamidForDetector(int detectorid) throws DataAccessException {
        try {
            return getStreamidForDetectorP(detectorid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public int getStreamid(int configid, String stream) throws DataAccessException {
        try {
            return getStreamidP(configid, stream);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void createSingleStream(int configid, String stream) throws DataAccessException {
        try {
            createSingleStreamP(configid, stream);
        } catch (SQLException | IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public String getStreamName(int streamid) throws DataAccessException {
        try {
            return getStreamNameP(streamid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public StoredFilter getStreamFilter(int detectorid) throws DataAccessException {
        try {
            return getStreamFilterP(detectorid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<StreamKey> getStreamKeys(String configName) throws DataAccessException {
        try {
            return getStreamKeysP(configName);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<StreamKey> getStreamKeys(int configid) throws DataAccessException {
        try {
            return getStreamKeysP(configid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public ArrayList<StreamKey> getStreamKeysForStream(int streamid) throws DataAccessException {
        try {
            return getStreamKeysForStreamP(streamid);
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Epoch getAllTriggersEpoch(int configid) throws DataAccessException {
        try {
            return getAllTriggersEpochP(configid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private Epoch getAllTriggersEpochP(int configid) throws SQLException {
        String sql = String.format("select min(c.time), max(c.time)\n"
                + "  from %s a, %s b, %s c\n"
                + " where a.configid = ?\n"
                + "   and b.configid = a.configid\n"
                + "   and b.runid = c.runid",
                TableNames.getConfigurationTable(),
                TableNames.getFrameworkRunTable(),
                TableNames.getTriggerRecordTable());
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, configid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        double start = rs.getDouble(1);
                        double end = rs.getDouble(2);
                        return new Epoch(start, end);
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return null;
    }

    private ArrayList<StreamKey> getStreamKeysForStreamP(int streamid) throws SQLException, DataAccessException {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            int numColumns = getStreamChannelColumnCount(conn);
            switch (numColumns) {
                case 3:
                    return getStreamKeysForStreamP3Columns(streamid, conn);
                case 7:
                    return getStreamKeysForStreamP7Columns(streamid, conn);
                default:
                    throw new DataAccessException("Could not find a STREAM_CHANNEL table with usable column count!");
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Collection<StreamKey> getStreamKeysP(int configid) throws SQLException, DataAccessException {
        Connection conn = null;
        String sql = String.format("select config_name from %s where configid = ?",
                TableNames.getConfigurationTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, configid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String configName = rs.getString(1);
                        return getKeysHavingConnection(conn, configName);
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return new ArrayList<>();
    }

    private Collection<StreamKey> getStreamKeysP(String configName) throws SQLException, DataAccessException {
        Connection conn = null;

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            return getKeysHavingConnection(conn, configName);
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Collection<StreamKey> getKeysHavingConnection(Connection conn, String configName) throws SQLException, DataAccessException {
        int numColumns = getStreamChannelColumnCount(conn);
        switch (numColumns) {
            case 3:
                return getStreamChannels3Columns(configName, conn);
            case 7:
                return getStreamChannels7Columns(configName, conn);
            default:
                throw new DataAccessException("Could not find a STREAM_CHANNEL table with usable column count!");
        }
    }

    private ArrayList<StreamKey> getStreamKeysForStreamP7Columns(int streamid, Connection conn) throws SQLException {
        ArrayList<StreamKey> result = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(String.format("select distinct c.agency,c.network_code,c.net_start_date,c.station_code, c.chan, c.location_code\n"
                + "   from  %s c\n"
                + "   where streamid = ? order by c.station_code, c.chan, location_code\n",
                TableNames.getStreamChannelTable()))) {
            stmt.setInt(1, streamid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int jdx = 1;
                    String agency = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    String networkCode = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    Integer netStartDate = OracleDBUtil.getIntegerFromCursor(rs, jdx++);
                    String stationCode = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    String chan = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    String locationCode = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    result.add(new StreamKey(agency, networkCode, netStartDate, stationCode, chan, locationCode));
                }
            }
        }
        return result;
    }

    private ArrayList<StreamKey> getStreamKeysForStreamP3Columns(int streamid, Connection conn) throws SQLException {
        ArrayList<StreamKey> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(String.format("select distinct c.sta, c.chan\n"
                + "   from  %s c\n"
                + "   where c.streamid = ? order by c.sta, c.chan\n",
                TableNames.getStreamChannelTable()))) {
            stmt.setInt(1, streamid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int jdx = 1;

                    String stationCode = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    String chan = OracleDBUtil.getStringFromCursor(rs, jdx++);

                    result.add(new StreamKey(stationCode, chan));
                }
            }
        }

        return result;
    }

    private Collection<StreamKey> getStreamChannels7Columns(String configName, Connection conn) throws SQLException {
        Collection<StreamKey> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(String.format("select distinct c.agency,c.network_code,c.net_start_date,c.station_code, c.chan, c.location_code\n"
                + "   from %s a,%s b, %s c\n"
                + "   where config_name = ?\n"
                + "   and a.configid = b.configid\n"
                + "   and b.streamid = c.streamid order by c.station_code, c.chan, location_code\n",
                TableNames.getConfigurationTable(),
                TableNames.getStreamTable(),
                TableNames.getStreamChannelTable()))) {
            stmt.setString(1, configName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int jdx = 1;
                    String agency = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    String networkCode = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    Integer netStartDate = OracleDBUtil.getIntegerFromCursor(rs, jdx++);
                    String stationCode = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    String chan = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    String locationCode = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    result.add(new StreamKey(agency, networkCode, netStartDate, stationCode, chan, locationCode));
                }
            }
        }

        return result;
    }

    private Collection<StreamKey> getStreamChannels3Columns(String configName, Connection conn) throws SQLException {
        Collection<StreamKey> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(String.format("select distinct c.sta, c.chan\n"
                + "   from %s a,%s b, %s c\n"
                + "   where config_name = ?\n"
                + "   and a.configid = b.configid\n"
                + "   and b.streamid = c.streamid order by c.sta, c.chan\n",
                TableNames.getConfigurationTable(),
                TableNames.getStreamTable(),
                TableNames.getStreamChannelTable()))) {
            stmt.setString(1, configName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int jdx = 1;

                    String stationCode = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    String chan = OracleDBUtil.getStringFromCursor(rs, jdx++);

                    result.add(new StreamKey(stationCode, chan));
                }
            }
        }

        return result;
    }

    private int getStreamChannelColumnCount(Connection conn) throws SQLException {
        String sql = String.format("select count(*) from user_tab_columns where table_name = 'STREAM_CHANNEL'");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private void writeStreamParamsIntoConfigurationP(int streamid, String stream, double sampleRate) throws SQLException, IOException {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            PreprocessorParams params = new FrameworkPreprocessorParams(stream, sampleRate);
            writeStreamParamBlob(params, streamid, conn);
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }

    }

    private int getStreamidForDetectorP(int detectorid) throws SQLException {
        Connection conn = null;
        String sql = String.format("select streamid from %s where detectorid = ?",
                TableNames.getDetectorTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    } else {
                        throw new IllegalArgumentException("No stream for supplied detectorid: " + detectorid);
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private int getStreamidP(int configid, String stream) throws SQLException {
        Connection conn = null;
        String sql = String.format("select streamid from %s where configid = ? and stream_name = ?",
                TableNames.getStreamTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, configid);
                stmt.setString(2, stream);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return -1;
    }

    private void createSingleStreamP(int configid, String stream) throws SQLException, IOException, DataAccessException {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            int streamid = writeStreamRow(conn, configid, stream);
            writeStreamFkRow(conn, streamid, configid, stream);
            StreamInfo info = StreamsConfig.getInstance().getInfo(stream);
            Collection<StreamKey> channels = info.getChannels();
            writeStreamChannels(conn, streamid, channels);

            BootDetectorParams bdp = info.getBootDetectorParams();
            for (STALTASpecification specs : bdp.getStaLtaParams(stream)) {
                DetectionDAOFactory.getInstance().getStaLtaDetectorDAO().saveStaLtaDetector(specs, streamid);
            }

            for (ArrayDetectorSpecification ap : bdp.getArrayParams(stream)) {
                DetectionDAOFactory.getInstance().getArrayDetectorDAO().saveArrayDetector(ap, streamid);
            }

            for (BulletinSpecification specs : bdp.getBulletinParams(stream)) {
                DetectionDAOFactory.getInstance().getBulletinDetectorDAO().saveBulletinDetector(specs, streamid);
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private int writeStreamRow(Connection conn, int configid, String stream) throws SQLException, IOException {
        File configFileDir = StreamsConfig.getInstance().getInfo(stream).getConfigFileDir();
        String configFile = StreamsConfig.getInstance().getInfo(stream).getConfigFileName();
        int filterOrder = StreamsConfig.getInstance().getPreprocessorFilterOrder(stream);
        double lowPass = StreamsConfig.getInstance().getPassBandLowFrequency(stream);
        double highPass = StreamsConfig.getInstance().getPassBandHighFrequency(stream);
        String sql = String.format("insert into %s values (%s.nextval,?,?,?,?, empty_blob(),?,?,?)",
                TableNames.getStreamTable(),
                SequenceNames.getStreamidSequenceName());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idx = 1;
            stmt.setInt(idx++, configid);
            stmt.setString(idx++, stream);
            stmt.setString(idx++, configFileDir.getAbsolutePath());
            stmt.setString(idx++, configFile);
            stmt.setDouble(idx++, lowPass);
            stmt.setDouble(idx++, highPass);
            stmt.setInt(idx++, filterOrder);
            stmt.execute();
            int streamid = (int) OracleDBUtil.getIdCurrVal(conn, SequenceNames.getStreamidSequenceName());
            conn.commit();
            return streamid;
        }
    }

    private void writeStreamFkRow(Connection conn, int streamid, int configid, String stream) throws SQLException {
        FKScreenParams params = StreamsConfig.getInstance().getFKScreenParams(stream);
        String sql = String.format("insert into %s values(?,?,?,?,?,?,?,?)",
                TableNames.getStreamFKParamTableName());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idx = 1;
            stmt.setInt(idx++, streamid);
            stmt.setInt(idx++, configid);
            stmt.setString(idx++, stream);
            stmt.setDouble(idx++, params.getMaxSlowness());
            stmt.setDouble(idx++, params.getMinFKFreq());
            stmt.setDouble(idx++, params.getMaxFKFreq());
            stmt.setDouble(idx++, params.getMinFKQual());
            stmt.setDouble(idx++, params.getfKWindowLength());
            stmt.execute();
            conn.commit();
        }
    }

    private String getStreamNameP(int streamid) throws SQLException {
        Connection conn = null;
        String sql = String.format("select stream_name from %s where streamid = ?",
                TableNames.getStreamTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, streamid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        return rs.getString(1);
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return null;
    }

    private void writeStreamParamBlob(PreprocessorParams params, int streamid, Connection conn) throws SQLException, IOException {
        String sql = String.format("select PREPROCESSOR_PARAMS from %s where streamid = ? for update",
                TableNames.getStreamTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, streamid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BLOB blob = (BLOB) rs.getBlob(1);
                    blob.truncate(0);
                    try (OutputStream os = blob.getBinaryOutputStream()) {
                        try (ObjectOutputStream oop = new ObjectOutputStream(os)) {
                            oop.writeObject(params);
                            oop.flush();
                            conn.commit();
                        }
                    }
                } else {
                    throw new IllegalStateException("Failed to write PreprocessorParams into database!");
                }
            }

        }

    }

    private void writeStreamChannels(Connection conn, int streamid, Collection<StreamKey> channels) throws SQLException, DataAccessException {

        int numColumns = getStreamChannelColumnCount(conn);
        switch (numColumns) {
            case 3:
                write3ColumnStreamChannelRows(conn, streamid, channels);
                return;
            case 7:
                write7ColumnStreamChannelRows(conn, streamid, channels);
                return;
            default:
                throw new DataAccessException("Could not find a STREAM_CHANNEL table with usable column count!");
        }
    }

    private void write3ColumnStreamChannelRows(Connection conn, int streamid, Collection<StreamKey> channels) throws SQLException {
        String sql = String.format("insert into %s values (?,?,?)",
                TableNames.getStreamChannelTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, streamid);
            for (StreamKey sc : channels) {
                stmt.setString(2, sc.getSta());
                stmt.setString(3, sc.getChan());
                stmt.execute();
            }
            conn.commit();

        }
    }

    private void write7ColumnStreamChannelRows(Connection conn, int streamid, Collection<StreamKey> channels) throws SQLException {
        String sql = String.format("insert into %s values (?,?,?,?,?,?,?)",
                TableNames.getStreamChannelTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, streamid);
            for (StreamKey sc : channels) {
                int jdx = 2;
                OracleDBUtil.setStringValue(sc.getAgency(), stmt, jdx++);
                OracleDBUtil.setStringValue(sc.getNet(), stmt, jdx++);
                OracleDBUtil.setIntegerValue(sc.getNetJdate(), stmt, jdx++);
                stmt.setString(jdx++, sc.getSta());
                stmt.setString(jdx++, sc.getChan());
                OracleDBUtil.setStringValue(sc.getLocationCode(), stmt, jdx++);
                stmt.execute();
            }
            conn.commit();

        }
    }

    private StoredFilter getStreamFilterP(int detectorid) throws SQLException {
        String sql = String.format("select low_corner, high_corner, filter_order from %s a,%s b where detectorid = ? and a.streamid = b.streamid",
                TableNames.getDetectorTable(),
                TableNames.getStreamTable());
        Connection conn = null;

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        double lc = rs.getDouble(1);
                        double hc = rs.getDouble(2);
                        int order = rs.getInt(3);
                        return new StoredFilter(-1, Passband.BAND_PASS,
                                true, order, lc, hc, "-", "iir", "-", false);
                    }
                    return new StoredFilter();
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

}
