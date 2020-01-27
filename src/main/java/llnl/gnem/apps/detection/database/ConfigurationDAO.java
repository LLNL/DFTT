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

import llnl.gnem.apps.detection.util.Configuration;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.core.database.ConnectionManager;

/**
 *
 * @author dodge1
 */
public class ConfigurationDAO {
    
    private ConfigurationDAO() {
    }
    
    public static ConfigurationDAO getInstance() {
        return ConfigurationDAOHolder.INSTANCE;
    }
    
    private static class ConfigurationDAOHolder {
        
        private static final ConfigurationDAO INSTANCE = new ConfigurationDAO();
    }
    
    public Collection<Configuration> getAllConfigurations() throws SQLException {
        Collection<Configuration> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select configid, config_name,config_dir,config_file_name from configuration");
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
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }
    
    public void createOrReplaceConfigurationUsingInputFiles(String configName, String wfdiscTable, String commandLineArgs, double fixSampleRateValue) throws SQLException, IOException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            removeExistingConfiguration(conn, configName);
            int configid = addConfigurationRow(conn, configName);
            RunInfo.getInstance().initialize(null,wfdiscTable, commandLineArgs, fixSampleRateValue);
            FrameworkRunDAO.getInstance().updateFrameworkRun(configid);
            ChannelSubstitutionDAO.getInstance().writeChannelSubstitutions(ProcessingPrescription.getInstance().getSubstitutionMap(), configid);
            
            Collection<String> streams = StreamsConfig.getInstance().getStreamNames();
            for (String stream : streams) {
                StreamDAO.getInstance().createSingleStream(conn, configid, stream);
            }
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }
    
    public void removeConfiguration(Connection conn, int configid) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("delete from configuration where configid = ?")) {
            stmt.setInt(1, configid);
            stmt.execute();
            conn.commit();
        }
    }
    
    private void removeExistingConfiguration(Connection conn, String configName) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("delete from configuration where config_name = ?")) {
            stmt.setString(1, configName);
            stmt.execute();
            conn.commit();
        }
    }
    
    private int addConfigurationRow(Connection conn, String configName) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String configDir = ProcessingPrescription.getInstance().getConfigFileDirectory().getAbsolutePath();
            String configFile = ProcessingPrescription.getInstance().getConfigFileName();
            stmt = conn.prepareStatement("insert into configuration values (configid.nextval, ?, ?, ?)");
            stmt.setString(1, configName);
            stmt.setString(2, configDir);
            stmt.setString(3, configFile);
            stmt.execute();
            conn.commit();
            return (int)OracleDBUtil.getIdCurrVal(conn, "configid");
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    public int getConfigid(String configName) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select configid from configuration where config_name = ?");
            stmt.setString(1, configName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
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
     
    public String getConfigNameForRun(int runid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select b.config_name from framework_run a, configuration b where runid = ? and a.configid = b.configid");
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
    
    public boolean configExists(String configName) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select configid from configuration where config_name = ?"));
            stmt.setString(1, configName);
            rs = stmt.executeQuery();
            return rs.next();
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
}
