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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.database.DbFrameworkRunDAO;
import llnl.gnem.apps.detection.dataAccess.database.SequenceNames;
import llnl.gnem.apps.detection.dataAccess.database.TableNames;
import llnl.gnem.apps.detection.util.FrameworkRun;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.dftt.core.util.TimeT;
import oracle.sql.BLOB;

/**
 *
 * @author dodge1
 */
public class OracleFrameworkRunDAO extends DbFrameworkRunDAO {

    private OracleFrameworkRunDAO() {
    }

    public static OracleFrameworkRunDAO getInstance() {
        return OracleFrameworkRunDAOHolder.INSTANCE;
    }

    private static class OracleFrameworkRunDAOHolder {

        private static final OracleFrameworkRunDAO INSTANCE = new OracleFrameworkRunDAO();
    }

    @Override
    public void updateFrameworkRun(int configid) throws DataAccessException {
        try {
            updateFrameworkRunP(configid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Integer getJdateOfLastTrigger(Integer runidToResume) throws DataAccessException {
        try {
            return getJdateOfLastTriggerP(runidToResume);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public boolean isConsistentRunid(Integer runidToResume, String configName) throws DataAccessException {
        try {
            return isConsistentRunidP(runidToResume, configName);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public int createFrameworkRunEntry(String configName, String commandLineArgs) throws DataAccessException {
        try {
            return createFrameworkRunEntryP(configName, commandLineArgs);
        } catch (SQLException | IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void logEndTime(int runid) throws DataAccessException {
        try {
            logEndTimeP(runid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public FrameworkRun getFrameworkRun(int runid) throws DataAccessException {
        try {
            return getFrameworkRunP(runid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void deleteFrameworkRun(int runid) throws DataAccessException {
        try {
            deleteFrameworkRunP(runid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<FrameworkRun> getConfigRunCollection(int configid) throws DataAccessException {
        try {
            return getConfigRunCollectionP(configid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private boolean isConsistentRunidP(Integer runidToResume, String configName) throws SQLException {
        Connection conn = null;
        String sql = String.format("select b.rowid from %s a, %s b where config_name = ? "
                + "and a.configid = b.configid "
                + "and runid = ?",
                TableNames.getConfigurationTable(),
                TableNames.getFrameworkRunTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, configName);
                stmt.setInt(2, runidToResume);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        return true;
                    }
                    return false;
                }
            }
        } finally {

            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

    private Integer getJdateOfLastTriggerP(Integer runidToResume) throws SQLException {
        Connection conn = null;
        String sql = String.format("select max(time) from %s where runid = ?",
                TableNames.getTriggerRecordTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runidToResume);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        double time = rs.getDouble(1);
                        return new TimeT(time).getJdate();
                    }
                    return null;
                }
            }
        } finally {

            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

    private void deleteFrameworkRunP(int runid) throws SQLException {
        Connection conn = null;
        String sql = String.format("delete from %s where runid = ?",
                TableNames.getFrameworkRunTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                stmt.execute();
                conn.commit();

            }
        } finally {

            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Collection<FrameworkRun> getConfigRunCollectionP(int configid) throws SQLException {
        Collection<FrameworkRun> result = new ArrayList<>();
        Connection conn = null;
        String sql = String.format("select runid, run_date, wfdisc_used, config_file_text, command_line_text, end_date, fixed_raw_sample_rate from %s where configid = ? order by runid",
                TableNames.getFrameworkRunTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, configid);
                try (ResultSet rs = stmt.executeQuery()) {
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
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private FrameworkRun getFrameworkRunP(int runid) throws SQLException {

        Connection conn = null;
        String sql = String.format("select configid, run_date, wfdisc_used, config_file_text, command_line_text, end_date, fixed_raw_sample_rate from %s where runid = ?",
                TableNames.getFrameworkRunTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                try (ResultSet rs = stmt.executeQuery()) {
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
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

    private void logEndTimeP(int runid) throws SQLException {
        Connection conn = null;

        String sql = String.format("update %s set end_date = sysdate where runid = ?",
                TableNames.getFrameworkRunTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                stmt.execute();
                conn.commit();
            }
        } finally {

            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private int createFrameworkRunEntryP(String configName, String commandLineArgs) throws SQLException, IOException, DataAccessException {
        byte[] fileContents = ProcessingPrescription.getInstance().getConfigFileBytes();
        int configid = DetectionDAOFactory.getInstance().getConfigurationDAO().getConfigid(configName);
        if (configid > 0) {
            Connection conn = null;
            try {
                conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
                int runid = createFrameworkRunEntry(configid, commandLineArgs, conn);
                writeConfigFileBlob(fileContents, runid, conn);
                conn.commit();
                return runid;
            } finally {
                DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
            }

        } else {
            throw new IllegalStateException("Configuration: " + configName + " does not exist!");
        }
    }

    private int createFrameworkRunEntry(int configid, String commandLineArgs, Connection conn) throws SQLException, IOException {

        String sql = String.format("insert into %s (runid,run_date,wfdisc_used,"
                + "configid,config_file_text,command_line_text, fixed_raw_sample_rate) "
                + "values(runid.nextval,sysdate,?,?,empty_blob(),?, ?)",
                TableNames.getFrameworkRunTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "-");
            stmt.setInt(2, configid);
            stmt.setString(3, commandLineArgs);
            stmt.setDouble(4, -999.0);
            stmt.execute();

            int runid = getRunidCurrVal(conn);

            return runid;
        }
    }

    private void updateFrameworkRunP(int configid) throws SQLException {
        int runid = RunInfo.getInstance().getRunid();
        String sql = String.format("update %s set configid = ? where runid = ?",
                TableNames.getFrameworkRunTable());
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, configid);
                stmt.setInt(2, runid);
                stmt.execute();
                conn.commit();
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void writeConfigFileBlob(byte[] fileContents, int runid, Connection conn) throws SQLException, IOException {
        String sql = String.format("select config_file_text from %s where runid = ?",
                TableNames.getFrameworkRunTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, runid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BLOB blob = (BLOB) rs.getBlob(1);
                    try (OutputStream os = blob.getBinaryOutputStream()) {
                        try (ObjectOutputStream oop = new ObjectOutputStream(os)) {
                            oop.writeObject(fileContents);
                            oop.flush();
                        }
                    }
                } else {
                    throw new IllegalStateException("Failed to write config file text into database!");
                }
            }
        }

    }

    private static int getRunidCurrVal(Connection conn) throws SQLException {
        return (int) OracleDBUtil.getIdCurrVal(conn, SequenceNames.getRunidSequenceName());
    }
}
