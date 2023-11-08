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
package llnl.gnem.apps.detection.dataAccess.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.Projection;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.apps.detection.util.SubspaceThreshold;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.dftt.core.util.StreamKey;
import oracle.sql.BLOB;

/**
 *
 * @author dodge1
 */
public class DetectorUtil {

    private static final double DUPLICATE_TEMPLATE_THRESHOLD = 0.95;
    private static final int SUBSPACE_OFFSET_RANGE = 10;

    public static void writeDetectorChannels(Connection conn, int detectorid, Collection<StreamKey> channels) throws SQLException {
        int numCols = getDetectorChannelColumnCount(conn);
        if (numCols == 4) {
            writeDetectorChannels4ColTable(conn, detectorid, channels);
        } else if (numCols == 8) {
            writeDetectorChannels8ColTable(conn, detectorid, channels);
        }
    }

    public static void writeDetectorChannels4ColTable(Connection conn, int detectorid, Collection<StreamKey> channels) throws SQLException {
        String sql = String.format("insert into %s values (?,?,?,?)", TableNames.getDetectorChannelTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int position = 0;
            for (StreamKey sck : channels) {
                int jdx = 1;
                stmt.setInt(jdx++, detectorid);
                stmt.setString(jdx++, sck.getSta());
                stmt.setString(jdx++, sck.getChan());
                stmt.setInt(jdx++, position++);
                stmt.execute();
            }
        }
    }

    private static void writeDetectorChannels8ColTable(Connection conn, int detectorid, Collection<StreamKey> channels) throws SQLException {
        String sql = String.format("insert into %s values (?,?,?,?,?,?,?,?)", TableNames.getDetectorChannelTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int position = 0;
            for (StreamKey sck : channels) {
                int jdx = 1;
                stmt.setInt(jdx++, detectorid);
                OracleDBUtil.setStringValue(sck.getAgency(), stmt, jdx++);
                OracleDBUtil.setStringValue(sck.getNet(), stmt, jdx++);
                OracleDBUtil.setIntegerValue(sck.getNetJdate(), stmt, jdx++);
                stmt.setString(jdx++, sck.getSta());
                stmt.setString(jdx++, sck.getChan());
                OracleDBUtil.setStringValue(sck.getLocationCode(), stmt, jdx++);
                stmt.setInt(jdx++, position++);
                stmt.execute();
            }
        }
    }

    private static int getDetectorChannelColumnCount(Connection conn) throws SQLException {
        String sql = String.format("select count(*) from user_tab_columns where table_name = 'DETECTOR_CHANNEL'");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //           stmt.setString(1, TableNames.getDetectorChannelTable().toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new IllegalStateException("Failed to determine column-count for DETECTOR_CHANNEL table!");
    }

    public static ArrayList<StreamKey> getDetectorChannels(Connection conn, int detectorid) throws SQLException {
        int numCols = getDetectorChannelColumnCount(conn);
        switch (numCols) {
        case 4:
            return getDetectorChannels4ColTable(conn, detectorid);
        case 8:
            return getDetectorChannels8ColTable(conn, detectorid);
        default:
            throw new IllegalStateException("Could not determine number of columns in DETECTOR_CHANNEL table!");
        }
    }

    public static ArrayList<StreamKey> getDetectorChannels4ColTable(Connection conn, int detectorid) throws SQLException {
        ArrayList<StreamKey> result = new ArrayList<>();
        String sql = String.format("select sta, chan from %s where detectorid = ? order by position", TableNames.getDetectorChannelTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detectorid);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String sta = rs.getString(1);
                    String chan = rs.getString(2);
                    result.add(new StreamKey(sta, chan));
                }
                return result;
            }
        }
    }

    public static ArrayList<StreamKey> getDetectorChannels8ColTable(Connection conn, int detectorid) throws SQLException {
        ArrayList<StreamKey> result = new ArrayList<>();
        String sql = String.format("select agency,network_code,net_start_date, station_code, chan, location_code from %s where detectorid = ? order by position", TableNames.getDetectorChannelTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detectorid);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    int jdx = 1;
                    String agency = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    String net = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    Integer netDate = OracleDBUtil.getIntegerFromCursor(rs, jdx++);
                    String sta = rs.getString(jdx++);
                    String chan = rs.getString(jdx++);
                    String locid = OracleDBUtil.getStringFromCursor(rs, jdx++);
                    result.add(new StreamKey(agency, net, netDate, sta, chan, locid));
                }
                return result;
            }
        }
    }

    public static ArrayList<float[][]> extractFromTemplates(PreprocessorParams parameters, Collection<StreamSegment> eventSegments, double targetSamplingRate, Collection<StreamKey> streamChannels) {
        ArrayList<float[][]> eventData = new ArrayList<>();

        int nevents = eventSegments.size();
        if (nevents > 0) {
            StreamSegment tmp = eventSegments.iterator().next();
            int npts = tmp.size();
            double delta = tmp.getSampleInterval();
            int nch = tmp.getNumChannels();

            if (Math.abs(1.0 / delta - targetSamplingRate) > 0.0001) {
                throw new IllegalStateException("Framework target sampling rate and data sampling rate are mismatched");
            }

            for (StreamSegment segment : eventSegments) {
                // Now put data in order of the stream...
                int ich = 0;
                float[][] data = new float[nch][npts];
                for (StreamKey sc : streamChannels) {
                    float[] channelData = segment.getWaveformSegment(sc).getData();
                    if (channelData == null) {
                        throw new IllegalStateException(String.format("Stream channel %s not found in template data!", sc));
                    }
                    System.arraycopy(channelData, 0, data[ich++], 0, channelData.length);
                }
                if (ich != nch) {
                    throw new IllegalStateException(String.format("Number of channels in template data does not match stream channel count!"));
                }
                eventData.add(data);

            }
        }
        return eventData;
    }

    public static boolean isDuplicateTemplate(ArrayList<float[][]> newTemplateRepresentation, ArrayList<StreamKey> chanIDs, Collection<Detector> ssDetectors) {
        for (Detector detector : ssDetectors) {
            if (detector instanceof SubspaceDetector) {
                SubspaceDetector ssd = (SubspaceDetector) detector;
                SubspaceTemplate existingTemplate = ssd.getTemplate();
                if (newTemplateRepresentation.get(0)[0].length == existingTemplate.getTemplateLength()) {
                    Projection projection = new Projection(existingTemplate, newTemplateRepresentation, chanIDs, SUBSPACE_OFFSET_RANGE);
                    double v = projection.getProjectionValue();
                    if (v >= DUPLICATE_TEMPLATE_THRESHOLD) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    public static SubspaceSpecification createSubspaceSpecification(ConcreteStreamProcessor streamProcessor, double templateLeadSeconds, double duration) {
        Collection<StreamKey> streamChannels = streamProcessor.getChannels();
        String streamName = streamProcessor.getStreamName();
        double threshold = SubspaceThreshold.getInstance().getNewDetectorThreshold(streamName);
        double blackoutSeconds = StreamsConfig.getInstance().getSubspaceBlackoutPeriod(streamName);
        double energyCapture = StreamsConfig.getInstance().getSubspaceEnergyCaptureThreshold(streamName);
        SubspaceSpecification spec = new SubspaceSpecification((float) threshold, (float) blackoutSeconds, templateLeadSeconds, duration, (float) energyCapture, streamChannels);
        return spec;
    }

    /**
     * Returns a database BLOB as a java object under the assumption that the
     * BLOB is in fact a Java object.
     *
     * @param rs
     *            The resultSet must be from a query that only retrieves a BLOB
     *            column
     * @return a Java Object
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object getBlobObject(ResultSet rs) throws SQLException, IOException, ClassNotFoundException {

        try (InputStream is = rs.getBlob(1).getBinaryStream()) {
            try (ObjectInputStream oip = new ObjectInputStream(is)) {
                return oip.readObject();
            }
        }
    }

    public static void writeIntoBlob(PreparedStatement stmt, Object object) throws SQLException, IOException {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                BLOB blob = (BLOB) rs.getBlob(1);
                try (OutputStream os = blob.getBinaryOutputStream()) {
                    try (ObjectOutputStream oop = new ObjectOutputStream(os)) {
                        oop.writeObject(object);
                        oop.flush();
                    }
                }
            } else {
                throw new IllegalStateException("Failed to write object into blob!");
            }
        }
    }

    public static DetectorType getDetectorType(int detectorid) throws SQLException {
        Connection conn = null;
        String sql = String.format("select detectortype from %s where detectorid = ?", TableNames.getDetectorTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return DetectorType.valueOf(rs.getString(1));
                    }
                    throw new IllegalStateException("Invalid detectorid!");
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    public static boolean isTableExists(String name) throws DataAccessException {
        try {
            return isTableExistsP(name);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private static boolean isTableExistsP(String name) throws SQLException {
        Connection conn = null;

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            if (name.indexOf('.') > 0) {
                StringTokenizer st = new StringTokenizer(name, ".");
                String schema = st.nextToken();
                String table = st.nextToken();
                try (PreparedStatement stmt = conn.prepareStatement("select * from all_objects where upper(object_name) = ? and upper(owner) = ?")) {
                    stmt.setString(1, table.toUpperCase());
                    stmt.setString(2, schema.toUpperCase());
                    try (ResultSet rs = stmt.executeQuery()) {
                        return rs.next();
                    }
                }
            } else {
                try (PreparedStatement stmt = conn.prepareStatement("select * from user_objects where upper(object_name) = ?")) {
                    stmt.setString(1, name.toUpperCase());
                    try (ResultSet rs = stmt.executeQuery()) {
                        return rs.next();
                    }
                }
            }
        } finally {

            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    public static int writeDetectorRow(Connection conn, int streamid, DetectorType type, double threshold, double blackoutSeconds, String source) throws SQLException {
        String sql = String.format("insert into %s select %s.nextval, ?, ?, ?, ?, ?, ?, 'n', sysdate from dual", TableNames.getDetectorTable(), SequenceNames.getDetectoridSequenceName());
        int runid = RunInfo.getInstance().getRunid();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, streamid);
            stmt.setString(2, type.toString());
            stmt.setDouble(3, threshold);
            stmt.setDouble(4, blackoutSeconds);
            stmt.setInt(5, runid);
            stmt.setString(6, source);
            stmt.execute();
            return (int) OracleDBUtil.getIdCurrVal(conn, SequenceNames.getDetectoridSequenceName());
        }
    }

}
