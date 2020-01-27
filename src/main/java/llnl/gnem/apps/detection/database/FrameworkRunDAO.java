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
package llnl.gnem.apps.detection.database;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import llnl.gnem.apps.detection.util.FrameworkRun;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.TimeT;
import oracle.sql.BLOB;

/**
 *
 * @author dodge1
 */
public class FrameworkRunDAO {

    private FrameworkRunDAO() {
    }

    public static FrameworkRunDAO getInstance() {
        return FrameworkRunDAOHolder.INSTANCE;
    }

    public boolean isConsistentRunid(Integer runidToResume, String configName) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select b.rowid from configuration a, framework_run b where config_name = ? "
                    + "and a.configid = b.configid "
                    + "and runid = ?");
            stmt.setString(1, configName);
            stmt.setInt(2, runidToResume);
            rs = stmt.executeQuery();
            while (rs.next()) {
                return true;
            }
            return false;
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

    public Integer getJdateOfLastTrigger(Integer runidToResume) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select max(time) from trigger_record where runid = ?");
            stmt.setInt(1, runidToResume);
            rs = stmt.executeQuery();
            while (rs.next()) {
                double time = rs.getDouble(1);
                return new TimeT(time).getJdate();
            }
            return null;
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

    private static class FrameworkRunDAOHolder {

        private static final FrameworkRunDAO INSTANCE = new FrameworkRunDAO();
    }

    public void deleteFrameworkRun(int runid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("delete from framework_run where runid = ?");
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

    public Collection<FrameworkRun> getConfigRunCollection(int configid) throws SQLException {
        Collection<FrameworkRun> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select runid, run_date, wfdisc_used, config_file_text, command_line_text, end_date, fixed_raw_sample_rate from framework_run where configid = ? order by runid");
            stmt.setInt(1, configid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int jdx = 1;
                int runid = rs.getInt(jdx++);
                Date runDate = rs.getDate(jdx++);
                String wfdisc = rs.getString(jdx++);
                byte[] bytes = rs.getBytes(jdx++);
                String cmdLine = rs.getString(jdx++);
                Date endDate = rs.getDate(jdx++);
                if (rs.wasNull()) {
                    endDate = null;
                }
                double fixedRate = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    fixedRate = -999;
                }
                result.add(new FrameworkRun(runid, runDate, wfdisc, configid, bytes, cmdLine, endDate, fixedRate));
            }
            return result;
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

    public FrameworkRun getFrameworkRun(int runid) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select configid, run_date, wfdisc_used, config_file_text, command_line_text, end_date, fixed_raw_sample_rate from framework_run where runid = ?");
            stmt.setInt(1, runid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int jdx = 1;
                int configid = rs.getInt(jdx++);
                Date runDate = rs.getDate(jdx++);
                String wfdisc = rs.getString(jdx++);
                byte[] bytes = rs.getBytes(jdx++);
                String cmdLine = rs.getString(jdx++);
                Date endDate = rs.getDate(jdx++);
                if (rs.wasNull()) {
                    endDate = null;
                }
                double fixedRate = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    fixedRate = -999;
                }
                return new FrameworkRun(runid, runDate, wfdisc, configid, bytes, cmdLine, endDate, fixedRate);
            }
            throw new IllegalStateException("No such run " + runid + "!");
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

    public void logEndTime(int runid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("update framework_run set end_date = sysdate where runid = ?");

            stmt.setInt(1, runid);
            stmt.execute();
            conn.commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    public int createFrameworkRunEntry(String wfdiscTable, String configName, String commandLineArgs, double fixedRawSampleRate) throws SQLException, IOException {
        byte[] fileContents = ProcessingPrescription.getInstance().getConfigFileBytes();
        int configid = ConfigurationDAO.getInstance().getConfigid(configName);
        if (configid > 0) {
            Connection conn = null;
            try {
                conn = ConnectionManager.getInstance().checkOut();
                int runid = createFrameworkRunEntry(configid, wfdiscTable, commandLineArgs, fixedRawSampleRate, conn);
                writeConfigFileBlob(fileContents, runid, conn);
                conn.commit();
                return runid;
            } finally {
                ConnectionManager.getInstance().checkIn(conn);
            }

        } else {
            throw new IllegalStateException("Configuration: " + configName + " does not exist!");
        }
    }

    private int createFrameworkRunEntry(int configid, String wfdiscTable, String commandLineArgs, double fixedRawSampleRate, Connection conn) throws SQLException, IOException {
        PreparedStatement stmt = null;

        try {

            String sql = "insert into framework_run (runid,run_date,wfdisc_used,"
                    + "configid,config_file_text,command_line_text, fixed_raw_sample_rate) "
                    + "values(runid.nextval,sysdate,?,?,empty_blob(),?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, wfdiscTable);
            stmt.setInt(2, configid);
            stmt.setString(3, commandLineArgs);
            stmt.setDouble(4, fixedRawSampleRate);
            stmt.execute();

            int runid = getRunidCurrVal(conn);

            return runid;
        } finally {
            if (stmt != null) {
                stmt.close();
            }

        }
    }

    public String getWfdiscTableForRun(int runid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select wfdisc_used from framework_run where runid = ?");
            stmt.setInt(1, runid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
            {
                throw new IllegalStateException("Could not retrieve run information for runid " + runid);
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

    public void updateFrameworkRun(int configid) throws SQLException {
        int runid = RunInfo.getInstance().getRunid();
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("update framework_run set configid = ? where runid = ?");
            stmt.setInt(1, configid);
            stmt.setInt(2, runid);
            stmt.execute();
            conn.commit();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    private void writeConfigFileBlob(byte[] fileContents, int runid, Connection conn) throws SQLException, IOException {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        OutputStream os = null;
        ObjectOutputStream oop = null;
        try {

            stmt = conn.prepareStatement("select config_file_text from framework_run where runid = ?");
            stmt.setInt(1, runid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                BLOB blob = (BLOB) rs.getBlob(1);
                os = blob.getBinaryOutputStream();
                oop = new ObjectOutputStream(os);
                oop.writeObject(fileContents);
                oop.flush();

            } else {
                throw new IllegalStateException("Failed to write config file text into database!");
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

    private static int getRunidCurrVal(Connection conn) throws SQLException {
        return (int) OracleDBUtil.getIdCurrVal(conn, "runid");
    }

}
