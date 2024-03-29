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
package llnl.gnem.dftt.core.dataAccess.database.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import llnl.gnem.dftt.core.dataAccess.DAOFactory;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.dataAccess.interfaces.FilterDAO;
import llnl.gnem.dftt.core.util.Passband;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class OracleFilterDAO implements FilterDAO {

    private static final String USER_FILTER_TABLE = "FILTER_IN_USE";
    private static final String STORED_FILTER_TABLE = "STORED_FILTER";
    private static final String FILTER_ID_SEQUENCE = "FILTERID";
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
        this.storedFilterTable = STORED_FILTER_TABLE;
        sequenceName = FILTER_ID_SEQUENCE;
    }

    @Override
    public Collection<StoredFilter> getAllFilters() throws DataAccessException {
        try {
            return getAllFiltersP();
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    @Override
    public StoredFilter getSingleFilter(int filterid) throws DataAccessException {
        try {
            return getSingleFilterP(filterid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    @Override
    public Collection<StoredFilter> getUserFilters() throws DataAccessException {
        try {
            return getUserFiltersP();
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private Collection<StoredFilter> getAllFiltersP() throws SQLException {
        String sql = String.format(
                "select filter_id, type,causal,filter_order,lowpass," + "highpass,description,impulse_response,auth,is_default from %s  order by lowpass, highpass",
                    storedFilterTable);
        return getTheFilters(sql);
    }

    @Override
    public void createDefaultUserFilters() throws DataAccessException {
        try {
            createDefaultUserFiltersP();
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    @Override
    public void removeUserFilter(int filterid) throws DataAccessException {
        try {
            removeUserFilterP(filterid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private Collection<StoredFilter> getTheFilters(String sql) throws SQLException {
        Collection<StoredFilter> result = new ArrayList<>();
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
                        StoredFilter filter = new StoredFilter(filterid, passband, causal.equals("y"), order, lowpass, highpass, descrip, impulseResponse, auth, isDefault);
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
        String sql = String.format("insert into %s values ( %s.nextval,?,?,?,?,?,?,?,lower(user), 'n' )", storedFilterTable, sequenceName);
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
            addToUserFilterTable(filterid, conn);
            conn.commit();

            return getSingleFilter(filterid, conn);
        }
    }

    private boolean filterIsRedundant(StoredFilter filter, Connection conn) throws SQLException {

        String sql = String.format(
                "select filter_id  from %s where type = ? and " + "causal = ? and filter_order = ? and lowpass = ? and " + "highpass = ? and impulse_response = ?",
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

        String sql = String.format("select type,causal,filter_order,lowpass," + "highpass,description,impulse_response,auth,is_default from %s where filter_id = ?", storedFilterTable);
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
                    return new StoredFilter(filterid, passband, causal.equals("y"), order, lowpass, highpass, descrip, impulseResponse, auth, defaultStr.equals("y"));

                }
                return null;
            }

        }
    }

    private StoredFilter getSingleFilterP(int filterid) throws SQLException {
        Connection conn = null;
        try {

            conn = DAOFactory.getInstance().getConnections().checkOut();
            return getSingleFilter(filterid, conn);
        } finally {
            if (conn != null) {
                DAOFactory.getInstance().getConnections().checkIn(conn);
            }
        }
    }

    private Collection<StoredFilter> getUserFiltersP() throws SQLException {
        String sql = String.format(
                "select b.filter_id, type,causal,filter_order,lowpass,"
                        + "highpass,description,impulse_response,auth,is_default from %s a,%s b "
                        + "where a.filter_id = b.filter_id  order by lowpass, highpass",
                    storedFilterTable,
                    USER_FILTER_TABLE);
        return getTheFilters(sql);
    }

    private void createDefaultUserFiltersP() throws SQLException {
        Connection conn = null;
        try {

            conn = DAOFactory.getInstance().getConnections().checkOut();
            deleteExistingUserFilters(conn);
            String sql = String.format("insert into %s select filter_id from %s where is_default = 'y'", USER_FILTER_TABLE, storedFilterTable);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.execute();
                conn.commit();
            }
        } finally {
            if (conn != null) {
                DAOFactory.getInstance().getConnections().checkIn(conn);
            }
        }
    }

    private void deleteExistingUserFilters(Connection conn) throws SQLException {
        String sql = String.format("delete from %s ", USER_FILTER_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    private void removeUserFilterP(int filterid) throws SQLException {
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            String sql = String.format("delete from %s where  filter_id = ?", USER_FILTER_TABLE);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, filterid);
                stmt.execute();
                conn.commit();
            }
        } finally {
            if (conn != null) {
                DAOFactory.getInstance().getConnections().checkIn(conn);
            }
        }
    }

    private void addToUserFilterTable(int filterid, Connection conn) throws SQLException {
        String sql = String.format("insert into %s values (?)", USER_FILTER_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, filterid);
            stmt.execute();
        }
    }

}
