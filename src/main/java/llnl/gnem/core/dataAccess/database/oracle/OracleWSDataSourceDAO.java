/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.WSDataSource;
import llnl.gnem.core.dataAccess.dataObjects.WSServiceType;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.interfaces.WSDataSourceDAO;
import llnl.gnem.core.database.ConnectionManager;

/**
 *
 * @author dodge1
 */
public class OracleWSDataSourceDAO implements WSDataSourceDAO{

    @Override
    public Map<String, WSDataSource> getDataSources(WSServiceType serviceType) throws DataAccessException {
        try {
            return getDataSourcesP(serviceType);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    @Override
    public Map<WSServiceType, WSDataSource> getDataSources(int sourceId) throws DataAccessException {
        try {
            return getDataSourcesP(sourceId);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private Map<String, WSDataSource> getDataSourcesP(WSServiceType serviceType) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            return gds(serviceType, conn);
        } finally {
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    private Map<WSServiceType, WSDataSource> getDataSourcesP(int sourceId) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            return gds(sourceId, conn);
        } finally {
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    private Map<WSServiceType, WSDataSource> gds(int sourceId, Connection conn) throws SQLException {
        Map<WSServiceType, WSDataSource> result = new HashMap<>();
        String sql = String.format("select a.service_type, source_code, service_url "
                + "from %s a, %s b where a.source_id = ? "
                + "and a.source_id = b.source_id",
                TableNames.DATA_SERVICES_TABLE,
                TableNames.SOURCE_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sourceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String serviceType = rs.getString(1);
                    WSServiceType wst = WSServiceType.valueOf(serviceType);
                    String sourceCode = rs.getString(2);
                    String url = rs.getString(3);
                    result.put(wst, new WSDataSource(sourceCode, sourceId, url, wst));
                }
                return result;
            }
        }
    }

    private Map<String, WSDataSource> gds(WSServiceType serviceType, Connection conn) throws SQLException {
        Map<String, WSDataSource> result = new HashMap<>();
        String sql = String.format("select a.source_id, source_code, service_url, service_type "
                + "from %s a, %s b where service_type = ? "
                + "and a.source_id = b.source_id",
                TableNames.DATA_SERVICES_TABLE,
                TableNames.SOURCE_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, serviceType.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int sourceId = rs.getInt(1);
                    String sourceCode = rs.getString(2);
                    String url = rs.getString(3);
                    result.put(sourceCode, new WSDataSource(sourceCode, sourceId, url, serviceType));
                }
                return result;
            }
        }
    }

}
