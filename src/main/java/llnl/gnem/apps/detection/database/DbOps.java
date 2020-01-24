package llnl.gnem.apps.detection.database;

import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.util.RunInfo;
import java.io.*;
import java.sql.*;
import java.util.*;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.core.dataAccess.database.oracle.OracleDBUtil;

import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.components.ComponentIdentifier;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.core.waveform.io.s4IO;
import oracle.sql.BLOB;

/**
 * Created by dodge1 Date: Sep 29, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public final class DbOps {

    private static class DbOpsHolder {

        private static final DbOps instance = new DbOps();
    }

    public static DbOps getInstance() {
        return DbOpsHolder.instance;
    }

    public Collection<Integer> getBootDetectorIds(Connection conn, int streamid)
            throws SQLException {
        Collection<Integer> result = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("select a.detectorid\n"
                    + "  from detector a, stalta_detector_params b\n"
                    + " where streamid = ? and is_retired = 'n' \n"
                    + "   and a.detectorid = b.detectorid\n"
                    + "   union\n"
                    + "select a.detectorid\n"
                    + "  from detector a, array_detector_params b\n"
                    + " where streamid = ? and is_retired = 'n' \n"
                    + "   and a.detectorid = b.detectorid "
                    + "   union\n"
                    + "select a.detectorid\n"
                    + "  from detector a, BULLETIN_DETECTOR_SPEC b\n"
                    + " where streamid = ? and is_retired = 'n' \n"
                    + "   and a.detectorid = b.detectorid");
            stmt.setInt(1, streamid);
            stmt.setInt(2, streamid);
            stmt.setInt(3, streamid);
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
        }

    }

    public DetectorType getDetectorType(Connection conn, int detectorid)
            throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("select detectortype from detector where detectorid = ?");
            stmt.setInt(1, detectorid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return DetectorType.valueOf(rs.getString(1));
            }
            throw new IllegalStateException("Invalid detectorid!");
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public void writeNewWfdiscRow(TimeT start,
            String sta,
            String chan,
            int jdate,
            int[] data,
            double samprate,
            int nsamp,
            double endtime,
            Connection conn,
            String targetTable,
            String targetDir) throws SQLException, IOException {
        double calib = 1;
        double calper = -1;
        String datatype = "s4";
        String clip = "-";
        String segtype = "-";
        String instype = "-";
        PreparedStatement wfdiscInsStmt = null;
        try {
            int wfid = getNextAvailableWfid(conn);
            String dfile = sta + '_' + chan + '_' + wfid + ".w";
            wfdiscInsStmt = conn.prepareStatement(String.format("insert into %s "
                    + "(sta,chan,time,wfid,chanid,jdate,endtime,nsamp,samprate,calib,calper,instype,"
                    + "segtype,datatype,clip,dir,dfile,foff,lddate)"
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,sysdate)", targetTable));
            wfdiscInsStmt.setString(1, sta);
            wfdiscInsStmt.setString(2, chan);
            wfdiscInsStmt.setDouble(3, start.getEpochTime());
            wfdiscInsStmt.setInt(4, wfid);
            wfdiscInsStmt.setInt(5, -1);
            wfdiscInsStmt.setInt(6, jdate);
            wfdiscInsStmt.setDouble(7, endtime);
            wfdiscInsStmt.setInt(8, nsamp);
            wfdiscInsStmt.setDouble(9, samprate);
            wfdiscInsStmt.setDouble(10, calib);
            wfdiscInsStmt.setDouble(11, calper);
            wfdiscInsStmt.setString(12, instype);
            wfdiscInsStmt.setString(13, segtype);
            wfdiscInsStmt.setString(14, datatype);
            wfdiscInsStmt.setString(15, clip);
            wfdiscInsStmt.setString(16, targetDir);
            wfdiscInsStmt.setString(17, dfile);
            wfdiscInsStmt.execute();
            String filename = targetDir + File.separator + dfile;
            s4IO.writeIntData(filename, data);
            conn.commit();
        } finally {
            if (wfdiscInsStmt != null) {
                wfdiscInsStmt.close();
            }
        }
    }

    private static int getNextAvailableWfid(Connection conn) throws SQLException {
        PreparedStatement getWfidStmt = null;
        ResultSet rs = null;
        try {
            getWfidStmt = conn.prepareStatement("select wfid.nextval from dual");
            rs = getWfidStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

            return -1;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (getWfidStmt != null) {
                getWfidStmt.close();
            }
        }
    }

    public boolean isTableExists(String name) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            if (name.indexOf('.') > 0) {
                StringTokenizer st = new StringTokenizer(name, ".");
                String schema = st.nextToken();
                String table = st.nextToken();
                stmt = conn.prepareStatement("select * from all_objects where upper(object_name) = ? and upper(owner) = ?");
                stmt.setString(1, table.toUpperCase());
                stmt.setString(2, schema.toUpperCase());
            } else {
                stmt = conn.prepareStatement("select * from user_objects where upper(object_name) = ?");
                stmt.setString(1, name.toUpperCase());
            }
            rs = stmt.executeQuery();
            return rs.next();
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

    public int writeDetectorRow(Connection conn, int streamid, DetectorType type, double threshold, double blackoutSeconds, String source) throws SQLException {
        PreparedStatement stmt = null;
        int runid = RunInfo.getInstance().getRunid();
        try {
            stmt = conn.prepareStatement("insert into detector select detectorid.nextval, ?, ?, ?, ?, ?, ?, 'n', sysdate from dual");
            stmt.setInt(1, streamid);
            stmt.setString(2, type.toString());
            stmt.setDouble(3, threshold);
            stmt.setDouble(4, blackoutSeconds);
            stmt.setInt(5, runid);
            stmt.setString(6, source);
            stmt.execute();
            return (int)OracleDBUtil.getIdCurrVal(conn, "detectorid");
        } finally {
            if (stmt != null) {
                stmt.close();
            }

        }
    }

    public void addProcessedSequenceRow(int evid, String auth, String commandLine) throws SQLException {
        int runid = RunInfo.getInstance().getRunid();
        PreparedStatement stmt = null;
        Connection conn = null;

        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("insert into processed_sequence values (?,?,?,sysdate,null,?)");
            stmt.setInt(1, runid);
            stmt.setInt(2, evid);
            stmt.setString(3, commandLine);
            stmt.setString(4, auth);
            stmt.execute();
            conn.commit();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    public void setProcessEndDate() throws SQLException {
        int runid = RunInfo.getInstance().getRunid();
        PreparedStatement stmt = null;
        Connection conn = null;

        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("update processed_sequence set end_date = sysdate where runid = ?");
            stmt.setInt(1, runid);

            stmt.execute();
            conn.commit();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }

    }

    public double getMeanDuration(int runid, int detectorid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select signal_duration from trigger_record where runid = ? and detectorid = ?");
            stmt.setInt(1, runid);
            stmt.setInt(2, detectorid);
            rs = stmt.executeQuery();
            double mean = 0;
            int count = 0;
            while (rs.next()) {
                double value = rs.getDouble(1);
                if (rs.wasNull()) {
                    return 50.0;
                } else {
                    mean += value;
                    ++count;
                }
            }
            if (count == 0) {
                return 50.0;
            } else {
                return mean / count;
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

    public ComponentIdentifier getComponentIdentifier(String chan) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select band, instrument, orientation, locid from %s where chan = ?",
                    TableNames.getInstance().getChanDescTableName()));
            stmt.setString(1, chan);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String band = rs.getString(1);
                String inscode = rs.getString(2);
                String orientation = rs.getString(3);
                String locid = rs.getString(4);
                return new ComponentIdentifier(band, inscode, orientation, locid);
            } else {
                return null;
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

    public ArrayList<float[][]> extractFromTemplates(PreprocessorParams parameters,
            Collection<StreamSegment> eventSegments,
            double targetSamplingRate,
            Collection<StreamKey> streamChannels) {
        ArrayList< float[][]> eventData = new ArrayList<>();

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

    public static void writeIntoBlob(int detectorid, PreparedStatement getBlobStmt, Object object) throws SQLException, IOException {
        getBlobStmt.setInt(1, detectorid);

        ResultSet rs = null;
        OutputStream os = null;
        ObjectOutputStream oop = null;
        try {
            rs = getBlobStmt.executeQuery();
            if (rs.next()) {
                BLOB blob = (BLOB) rs.getBlob(1);
                os = blob.getBinaryOutputStream();
                oop = new ObjectOutputStream(os);
                oop.writeObject(object);
                oop.flush();
            } else {
                throw new IllegalStateException("Failed to write object into blob!");
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
        }
    }

    /**
     * Returns a database BLOB as a java object under the assumption that the
     * BLOB is in fact a Java object.
     *
     * @param rs The resultSet must be from a query that only retrieves a BLOB
     * column
     * @return a Java Object
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object getBlobObject(ResultSet rs)
            throws SQLException, IOException, ClassNotFoundException {
        InputStream is = null;
        ObjectInputStream oip = null;
        try {

            is = rs.getBlob(1).getBinaryStream();
            oip = new ObjectInputStream(is);
            return oip.readObject();
        } finally {
            if (oip != null) {
                oip.close();
            }
            if (is != null) {
                is.close();
            }

        }

    }

}
