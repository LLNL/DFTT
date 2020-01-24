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
import llnl.gnem.core.dataAccess.interfaces.FilterDAO;
import llnl.gnem.core.util.Passband;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class OracleFilterDAO implements FilterDAO {

    private String storedFilterTable;
    private String sequenceName;

    private static OracleFilterDAO instance = null;

    public static OracleFilterDAO getInstance() {
        if (instance == null) {
            instance = new OracleFilterDAO();
        }
        return instance;
    }

    private OracleFilterDAO() {
        this.storedFilterTable = TableNames.STORED_FILTER_TABLE;
        sequenceName = SequenceNames.FILTER_ID_SEQUENCE;
    }

    @Override
    public Collection<StoredFilter> getAllFilters() throws DataAccessException {
        try {
            return getAllFiltersP();
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private Collection<StoredFilter> getAllFiltersP() throws SQLException {
        Collection<StoredFilter> result = new ArrayList<>();
        String sql = String.format("select filter_id, type,causal,filter_order,lowpass,"
                + "highpass,description,impulse_response,auth,is_default from %s  order by lowpass, highpass",
                storedFilterTable);
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int filterid = rs.getInt(jdx++);
                        String type = rs.getString(jdx++);
                        String causal = rs.getString(jdx++);
                        int order = rs.getInt(jdx++);
                        double lowpass = rs.getDouble(jdx++);
                        double highpass = rs.getDouble(jdx++);
                        String descrip = rs.getString(jdx++);
                        String impulseResponse = rs.getString(jdx++);
                        String auth = rs.getString(jdx++);
                        String defString = rs.getString(jdx++);
                        boolean isDefault = defString.equals("y");
                        Passband passband = Passband.getPassbandFromString(type);
                        StoredFilter filter = new StoredFilter(filterid,
                                passband, causal.equals("y"),
                                order, lowpass, highpass,
                                descrip, impulseResponse, auth, isDefault);
                        result.add(filter);
                    }
                    return result;
                }
            }

        } finally {

            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    @Override
    public StoredFilter maybeAddFilter(StoredFilter filterToAdd) throws DataAccessException {
        try {
            return maybeAddFilterP(filterToAdd);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    public StoredFilter maybeAddFilterP(StoredFilter filterToAdd) throws SQLException {
        Connection conn = null;
        try {

            conn = DAOFactory.getInstance().getConnections().checkOut();
            if (filterIsRedundant(filterToAdd, conn)) {// exists in stored_filter 
                return null;
            } else {
                return addThisFilter(filterToAdd, conn);
            }
        } finally {
            if (conn != null) {
                DAOFactory.getInstance().getConnections().checkIn(conn);
            }
        }
    }

    private StoredFilter addThisFilter(StoredFilter filter, Connection conn) throws SQLException {
        String sql = String.format("insert into %s values ( %s.nextval,?,?,?,?,?,?,?,lower(user), 'n' )",
                storedFilterTable, sequenceName);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, filter.getPassband().toString());
            stmt.setString(2, filter.isCausal() ? "y" : "n");
            stmt.setInt(3, filter.getOrder());
            stmt.setDouble(4, filter.getLowpass());
            stmt.setDouble(5, filter.getHighpass());
            stmt.setString(6, filter.toString());
            stmt.setString(7, filter.getImpulseResponse());
            stmt.execute();

            int filterid = (int) OracleDBUtil.getIdCurrVal(conn, sequenceName);
            conn.commit();

            return getSingleFilter(filterid, conn);
        } 
    }

    private boolean filterIsRedundant(StoredFilter filter, Connection conn) throws SQLException {

        String sql = String.format("select filter_id  from %s where type = ? and "
                + "causal = ? and filter_order = ? and lowpass = ? and "
                + "highpass = ? and impulse_response = ?",
                storedFilterTable);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int jdx = 1;
            stmt.setString(jdx++, filter.getPassband().toString());
            stmt.setString(jdx++, filter.isCausal() ? "y" : "n");
            stmt.setInt(jdx++, filter.getOrder());
            stmt.setDouble(jdx++, filter.getLowpass());
            stmt.setDouble(jdx++, filter.getHighpass());
            stmt.setString(jdx++, filter.getImpulseResponse());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private StoredFilter getSingleFilter(int filterid, Connection conn) throws SQLException {

        String sql = String.format("select type,causal,filter_order,lowpass,"
                + "highpass,description,impulse_response,auth,is_default from %s where filter_id = ?",
                storedFilterTable);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, filterid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int jdx = 1;
                    String type = rs.getString(jdx++);
                    String causal = rs.getString(jdx++);
                    int order = rs.getInt(jdx++);
                    double lowpass = rs.getDouble(jdx++);
                    double highpass = rs.getDouble(jdx++);
                    String descrip = rs.getString(jdx++);
                    String impulseResponse = rs.getString(jdx++);
                    String auth = rs.getString(jdx++);
                    String defaultStr = rs.getString(jdx++);
                    Passband passband = Passband.getPassbandFromString(type);
                    return new StoredFilter(filterid, passband, causal.equals("y"),
                            order, lowpass, highpass, descrip, impulseResponse, auth, defaultStr.equals("y"));

                }
                return null;
            }

        }
    }

    @Override
    public void setStoredFilterTable(String storedFilterTable) {
        this.storedFilterTable = storedFilterTable;
    }

    @Override
    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }
}
