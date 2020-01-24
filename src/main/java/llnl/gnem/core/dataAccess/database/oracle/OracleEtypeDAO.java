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
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.interfaces.EtypeDAO;
import llnl.gnem.core.metadata.EtypeInfo;

/**
 *
 * @author dodge1
 */
public class OracleEtypeDAO implements EtypeDAO {

    @Override
    public Collection<EtypeInfo> getEtypeInfo() throws DataAccessException {
        try {
            return getEtypeInfoP();
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    public Collection<EtypeInfo> getEtypeInfoP() throws SQLException {
        Collection<EtypeInfo> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;

        ResultSet rs = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            String sql = String.format("select etype, descrip from %s", TableNames.ETYPE_DESC_TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setFetchSize(100);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String etype = rs.getString(1);
                String descrip = rs.getString(2);
                results.add(new EtypeInfo(etype, descrip));

            }
            return results;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                DAOFactory.getInstance().getConnections().checkIn(conn);
            }
        }
    }

}
