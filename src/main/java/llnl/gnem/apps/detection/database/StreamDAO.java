/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.database;

import llnl.gnem.apps.detection.FrameworkPreprocessorParams;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayDetectorSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTASpecification;
import llnl.gnem.apps.detection.util.initialization.BootDetectorParams;
import llnl.gnem.apps.detection.util.initialization.StreamInfo;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import llnl.gnem.apps.detection.core.framework.detectors.bulletin.BulletinSpecification;
import llnl.gnem.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.Passband;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.waveform.filter.StoredFilter;
import oracle.sql.BLOB;

/**
 *
 * @author dodge1
 */
public class StreamDAO {

    private StreamDAO() {
    }

    public static StreamDAO getInstance() {
        return StreamDAOHolder.INSTANCE;
    }

    private void writeStreamFkRow(Connection conn, int streamid, int configid, String stream) throws SQLException {
        FKScreenParams params = StreamsConfig.getInstance().getFKScreenParams(stream);
        try (PreparedStatement stmt = conn.prepareStatement("insert into stream_fk_param values(?,?,?,?,?,?,?,?)")) {
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

    private static class StreamDAOHolder {

        private static final StreamDAO INSTANCE = new StreamDAO();
    }

    public void writeStreamParamsIntoConfiguration(int streamid, String stream, double sampleRate) throws SQLException, IOException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            PreprocessorParams params = new FrameworkPreprocessorParams(stream, sampleRate);
            writeStreamParamBlob(params, streamid, conn);
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }

    }

    public int getStreamidForDetector(int detectorid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select streamid from detector where detectorid = ?");
            stmt.setInt(1, detectorid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new IllegalArgumentException("No stream for supplied detectorid: " + detectorid);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    public void createSingleStream(Connection conn, int configid, String stream) throws SQLException, IOException {
        int streamid = writeStreamRow(conn, configid, stream);
        writeStreamFkRow(conn, streamid, configid, stream);
        StreamInfo info = StreamsConfig.getInstance().getInfo(stream);
        Collection<StreamKey> channels = info.getChannels();
        writeStreamChannels(conn, streamid, channels);

        BootDetectorParams bdp = info.getBootDetectorParams();
        for (STALTASpecification specs : bdp.getStaLtaParams(stream)) {
            StaLtaDetectorDAO.getInstance().saveStaLtaDetector(specs, streamid);
        }

        for (ArrayDetectorSpecification ap : bdp.getArrayParams(stream)) {
            ArrayDetectorDAO.getInstance().saveArrayDetector(ap, streamid);
        }

        for (BulletinSpecification specs : bdp.getBulletinParams(stream)) {
            BulletinDetectorDAO.getInstance().saveBulletinDetector(specs, streamid);
        }
    }

    private int writeStreamRow(Connection conn, int configid, String stream) throws SQLException, IOException {
        File configFileDir = StreamsConfig.getInstance().getInfo(stream).getConfigFileDir();
        String configFile = StreamsConfig.getInstance().getInfo(stream).getConfigFileName();
        int filterOrder = StreamsConfig.getInstance().getPreprocessorFilterOrder(stream);
        double lowPass = StreamsConfig.getInstance().getPassBandLowFrequency(stream);
        double highPass = StreamsConfig.getInstance().getPassBandHighFrequency(stream);
        try (PreparedStatement stmt = conn.prepareStatement("insert into stream values (streamid.nextval,?,?,?,?, empty_blob(),?,?,?)")) {
            int idx = 1;
            stmt.setInt(idx++, configid);
            stmt.setString(idx++, stream);
            stmt.setString(idx++, configFileDir.getAbsolutePath());
            stmt.setString(idx++, configFile);
            stmt.setDouble(idx++, lowPass);
            stmt.setDouble(idx++, highPass);
            stmt.setInt(idx++, filterOrder);
            stmt.execute();
            int streamid = (int) OracleDBUtil.getIdCurrVal(conn, "streamid");
            conn.commit();
            return streamid;
        }
    }

    public void writeStreamParamBlob(PreprocessorParams params, int streamid, Connection conn) throws SQLException, IOException {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        OutputStream os = null;
        ObjectOutputStream oop = null;
        try {
            stmt = conn.prepareStatement("select PREPROCESSOR_PARAMS from stream where streamid = ? for update");
            stmt.setInt(1, streamid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                BLOB blob = (BLOB) rs.getBlob(1);
                blob.truncate(0);
                os = blob.getBinaryOutputStream();
                oop = new ObjectOutputStream(os);
                oop.writeObject(params);
                oop.flush();
                conn.commit();
            } else {
                throw new IllegalStateException("Failed to write PreprocessorParams into database!");
            }
        } finally {
            if (oop != null) {
                oop.close();
            }
            if (os != null) {
                os.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }

    }

    private void writeStreamChannels(Connection conn, int streamid, Collection<StreamKey> channels) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("insert into stream_channel values (?,?,?)")) {
            stmt.setInt(1, streamid);
            for (StreamKey sc : channels) {
                stmt.setString(2, sc.getSta());
                stmt.setString(3, sc.getChan());
                stmt.execute();
            }
            conn.commit();

        }
    }

    public StoredFilter getStreamFilter(int detectorid) throws SQLException {
        String sql = "select low_corner, high_corner, filter_order from detector a,stream b where detectorid = ? and a.streamid = b.streamid";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, detectorid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                double lc = rs.getDouble(1);
                double hc = rs.getDouble(2);
                int order = rs.getInt(3);
                return new StoredFilter(-1, Passband.BAND_PASS,
                        true, order, lc, hc, "-", "iir", "-", false);
            }
            return new StoredFilter();
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

    public void validateStreamCompatibility(Connection conn, int streamid, Collection<StreamKey> channels) throws SQLException {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Set<StreamKey> streamChannels = new HashSet<>();
        try {
            String sql = String.format("select sta, chan from stream_channel where streamid = %d", streamid);
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();
            while (rs.next()) {
                String sta = rs.getString(1);
                String chan = rs.getString(2);
                streamChannels.add(new StreamKey(sta, chan));
            }
            for (StreamKey sck : channels) {
                if (!streamChannels.contains(sck)) {
                    throw new IllegalStateException("Detector channel: " + sck + " not found in stream!");
                }
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
