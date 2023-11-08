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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;

import llnl.gnem.apps.detection.dataAccess.database.SequenceNames;
import llnl.gnem.apps.detection.dataAccess.database.TableNames;
import llnl.gnem.apps.detection.dataAccess.interfaces.ConfigurationDAO;

import llnl.gnem.apps.detection.util.Configuration;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.dataAccess.SeismogramSourceInfo;
import llnl.gnem.dftt.core.dataAccess.SeismogramSourceInfo.SourceType;
import llnl.gnem.dftt.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class OracleConfigurationDAO implements ConfigurationDAO {

    private OracleConfigurationDAO() {
    }

    public static OracleConfigurationDAO getInstance() {
        return OracleConfigurationDAOHolder.INSTANCE;
    }

    private int getColumnCount(String tableName, Connection conn) throws SQLException {
        String sql = String.format("select count(*) from user_tab_columns where table_name = ?");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private static class OracleConfigurationDAOHolder {

        private static final OracleConfigurationDAO INSTANCE = new OracleConfigurationDAO();
    }

    @Override
    public Collection<Configuration> getAllConfigurations() throws DataAccessException {
        try {
            return getAllConfigurationsP();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void createOrReplaceConfigurationUsingInputFiles(String configName, String commandLineArgs, SeismogramSourceInfo sourceInfo) throws DataAccessException {
        try {
            createOrReplaceConfigurationUsingInputFilesP(configName, commandLineArgs, sourceInfo);
        } catch (SQLException | IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void removeConfiguration(int configid) throws DataAccessException {
        try {
            removeConfigurationP(configid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public int getConfigid(String configName) throws DataAccessException {
        try {
            return getConfigidP(configName);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public String getConfigNameForRun(int runid) throws DataAccessException {
        try {
            return getConfigNameForRunP(runid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public boolean configExists(String configName) throws DataAccessException {
        try {
            return configExistsP(configName);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<Integer> getStreamids(String configName) throws DataAccessException {
        try {
            return getStreamidsP(configName);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<StreamKey> getStreamKeys(int streamid) throws DataAccessException {
        try {
            return getStreamKeysP(streamid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public SeismogramSourceInfo getConfigurationSeismogramSourceInfo(int configid) throws DataAccessException {
        try {
            return getConfigurationSeismogramSourceInfoP(configid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private SeismogramSourceInfo getConfigurationSeismogramSourceInfoP(int configid) throws SQLException {
        Connection conn = null;
        String sql = String.format("select source_type,source_identifier from %s  where configid = ?",
                TableNames.getConfigurationTable());

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, configid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String tmp = rs.getString(1);

                        SourceType sourceType = tmp == null ? SourceType.CssDatabase : SourceType.valueOf(tmp);
                        tmp = OracleDBUtil.getStringFromCursor(rs, 2);
                        return new SeismogramSourceInfo(sourceType, tmp);
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return null;
    }

    private Collection<StreamKey> getStreamKeysP(int streamid) throws SQLException {
        Collection<StreamKey> result = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            //Up until late 2021 STREAM_CHANNEL had only the columns (STREAMID,STA,CHAN)
            //Now the table has(STREAMID,AGENCY,NETWORK_CODE,NET_START_DATE,STATION_CODE,CHAN,LOCATION_CODE)
            // To support backwards compatibility, detect which version exists in current schema and
            // adjust SQL accordingly.
            int columnCount = getColumnCount(TableNames.getStreamChannelTable().toUpperCase(), conn);
            if (columnCount == 3) {
                String sql = String.format("select distinct sta, chan\n"
                        + "  from %s where streamid = ? order by sta, chan",
                        TableNames.getStreamChannelTable());
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, streamid);
                    try (ResultSet rs = stmt.executeQuery()) {

                        while (rs.next()) {
                            String sta = rs.getString(1);
                            String chan = rs.getString(2);
                            result.add(new StreamKey(sta, chan));
                        }
                    }
                }
            } else if (columnCount == 7) {
                String sql = String.format("select agency,network_code,net_start_date,station_code, chan, location_code\n"
                        + "  from %s where streamid = ? order by agency,network_code,net_start_date,station_code, chan, location_code",
                        TableNames.getStreamChannelTable());
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, streamid);
                    try (ResultSet rs = stmt.executeQuery()) {

                        while (rs.next()) {
                            int jdx = 1;
                            String agency = OracleDBUtil.getStringFromCursor(rs, jdx++);
                            String networkCode = OracleDBUtil.getStringFromCursor(rs, jdx++);
                            Integer netStartDate = OracleDBUtil.getIntegerFromCursor(rs, jdx++);
                            String stationCode = rs.getString(jdx++);
                            String chan = rs.getString(jdx++);
                            String locationCode = OracleDBUtil.getStringFromCursor(rs, jdx++);
                            result.add(new StreamKey(agency, networkCode, netStartDate, stationCode, chan, locationCode));
                        }
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections();
        }
        return result;
    }

    private Collection<Integer> getStreamidsP(String configName) throws SQLException {
        Connection conn = null;
        String sql = String.format("select streamid from %s a, %s b  where config_name = ? and a.configid = b.configid",
                TableNames.getConfigurationTable(),
                TableNames.getStreamTable());
        Collection<Integer> result = new ArrayList<>();
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, configName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getInt(1));
                    }
                    return result;
                }
            }
        } finally {

            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    @Override
    public void createConfiguration(String configName) throws DataAccessException {
        try {
            createConfigurationP(configName);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void createConfigurationP(String configName) throws SQLException {
        Connection conn = null;

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(String.format("insert into %s select %s.nextval, ? from dual",
                    TableNames.getConfigurationTable(),
                    SequenceNames.getConfigidSequenceName()))) {
                stmt.setString(1, configName);
                stmt.execute();
                conn.commit();
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    private Collection<Configuration> getAllConfigurationsP() throws SQLException {
        Collection<Configuration> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement("select configid, config_name,config_dir,config_file_name from configuration ORDER BY CONFIG_NAME");
            rs = stmt.executeQuery();
            while (rs.next()) {
                int configid = rs.getInt(1);
                String name = rs.getString(2);
                String dir = rs.getString(3);
                String fileName = rs.getString(4);
                result.add(new Configuration(configid, name, dir, fileName));
            }
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

    private void createOrReplaceConfigurationUsingInputFilesP(String configName, String commandLineArgs, SeismogramSourceInfo sourceInfo) throws SQLException, IOException, DataAccessException {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            removeExistingConfiguration(conn, configName);
            int configid = addConfigurationRow(conn, configName, sourceInfo);
            RunInfo.getInstance().initialize(null, commandLineArgs);
            DetectionDAOFactory.getInstance().getFrameworkRunDAO().updateFrameworkRun(configid);

            Collection<String> streams = StreamsConfig.getInstance().getStreamNames();
            for (String stream : streams) {
                DetectionDAOFactory.getInstance().getStreamDAO().createSingleStream(configid, stream);
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

    private void removeConfigurationP(int configid) throws SQLException {
        Connection conn = null;
        String sql = String.format("delete from %s where configid = ?",
                TableNames.getConfigurationTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, configid);
                stmt.execute();
                conn.commit();
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void removeExistingConfiguration(Connection conn, String configName) throws SQLException {
        String sql = String.format("delete from %s where config_name = ?",
                TableNames.getConfigurationTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, configName);
            stmt.execute();
        }
    }

    private int addConfigurationRow(Connection conn, String configName, SeismogramSourceInfo sourceInfo) throws SQLException {
        String sql = String.format("insert into %s values (%s.nextval, ?, ?, ?, ?, ?)",
                TableNames.getConfigurationTable(),
                SequenceNames.getConfigidSequenceName());

        String configDir = ProcessingPrescription.getInstance().getConfigFileDirectory().getAbsolutePath();
        String configFile = ProcessingPrescription.getInstance().getConfigFileName();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, configName);
            stmt.setString(2, configDir);
            stmt.setString(3, configFile);
            if (sourceInfo == null) {
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.VARCHAR);
            } else {
                OracleDBUtil.setStringValue(sourceInfo.getSourceType().toString(), stmt, 4);
                OracleDBUtil.setStringValue(sourceInfo.getSourceIdentifier(), stmt, 5);
            }
            stmt.execute();
            conn.commit();
            return (int) OracleDBUtil.getIdCurrVal(conn, SequenceNames.getConfigidSequenceName());
        }

    }

    private int getConfigidP(String configName) throws SQLException {
        Connection conn = null;
        String sql = String.format("select configid from %s where config_name = ?",
                TableNames.getConfigurationTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, configName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    } else {
                        return -1;
                    }
                }
            }
        } finally {

            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private String getConfigNameForRunP(int runid) throws SQLException {
        Connection conn = null;
        String sql = String.format("select b.config_name from %s a, %s b where runid = ? and a.configid = b.configid",
                TableNames.getFrameworkRunTable(),
                TableNames.getConfigurationTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(1);
                    } else {
                        throw new IllegalStateException("Could not retrieve run information for runid " + runid);
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private boolean configExistsP(String configName) throws SQLException {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(String.format("select configid from configuration where config_name = ?", TableNames.getConfigurationTable()))) {
                stmt.setString(1, configName);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }
}
