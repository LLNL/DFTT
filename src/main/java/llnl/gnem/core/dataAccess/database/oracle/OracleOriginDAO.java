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
import llnl.gnem.core.dataAccess.interfaces.OriginDAO;
import llnl.gnem.core.gui.map.origins.OriginInfo;
import llnl.gnem.core.seismicData.Netmag;
import llnl.gnem.core.seismicData.Origerr;

/**
 *
 * @author dodge1
 */
public class OracleOriginDAO implements OriginDAO {

    @Override
    public Origerr getOrigerr(int originId) throws DataAccessException {
        try {
            return getOrigerrP(originId);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }
    
    @Override
    public Collection<OriginInfo> getOriginsForEvent(long eventID) throws DataAccessException
    {
        try {
            return getOriginsForEventP(eventID);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }
    
    @Override
    public Collection<Netmag> getNetmagInfo(int originID) throws DataAccessException
    {
        try {
            return getNetmagInfoP(originID);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private Origerr getOrigerrP(int originId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        String sql = String.format("select SXX, SYY, "
                    + " SZZ,"
                    + " STT,"
                    + " SXY,"
                    + " SXZ,"
                    + " SYZ, "
                    + " STX ,"
                    + " STY,"
                    + " STZ,"
                    + " SDOBS,"
                    + " SMAJAX,"
                    + " SMINAX,"
                    + " STRIKE,"
                    + " SDEPTH,"
                    + " STIME,"
                    + " CONF from %s where origin_id = ?", TableNames.ORIGERR_TABLE);
        ResultSet rs = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, originId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (Thread.interrupted()) {
                    return null;
                }
                int jdx = 1;
                double sxx = rs.getDouble(jdx++);
                double syy = rs.getDouble(jdx++);
                double szz = rs.getDouble(jdx++);
                double stt = rs.getDouble(jdx++);
                double sxy = rs.getDouble(jdx++);
                double sxz = rs.getDouble(jdx++);
                double syz = rs.getDouble(jdx++);
                double stx = rs.getDouble(jdx++);
                double sty = rs.getDouble(jdx++);
                double stz = rs.getDouble(jdx++);
                double sdobs = rs.getDouble(jdx++);
                double smajax = rs.getDouble(jdx++);
                double sminax = rs.getDouble(jdx++);
                double strike = rs.getDouble(jdx++);
                double sdepth = rs.getDouble(jdx++);
                double stime = rs.getDouble(jdx++);
                double conf = rs.getDouble(jdx++);
                return new Origerr(originId, sxx, syy, szz, stt, sxy, sxz, syz,
                        stx, sty, stz, sdobs, smajax, sminax, strike, sdepth, stime, conf);
            }

            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Collection<Netmag> getNetmagInfoP(int originID) throws SQLException {
        Connection conn = null;
        Collection<Netmag> result = new ArrayList<>();
        PreparedStatement stmt = null;

        String sql = String.format("select magnitude_id,event_id, magtype,nsta,magnitude,uncertainty,"
                + "auth from %s where origin_id = ?", TableNames.NETMAG_TABLE);
        ResultSet rs = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, originID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (Thread.interrupted()) {
                    return null;
                }
                int jdx = 1;
                int magid = rs.getInt(jdx++);
                int eventID = rs.getInt(jdx++);
                String magtype = rs.getString(jdx++);
                Integer nsta = rs.getInt(jdx++);
                if (rs.wasNull()) {
                    nsta = null;
                }
                double magnitude = rs.getDouble(jdx++);
                Double uncertainty = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    uncertainty = null;
                }
                String auth = rs.getString(jdx++);
                result.add(new Netmag(magid, originID, eventID, magtype, nsta, magnitude, uncertainty, auth));
            }

            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Collection<OriginInfo> getOriginsForEventP(long eventID) throws SQLException {

        Collection<OriginInfo> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;

        String sql = String.format("select origin_id,lat,lon,depth,time,nass,ndef,"
                + "ndp,etype,auth, prime from %s where event_id = ?", TableNames.ORIGIN_TABLE);
        ResultSet rs = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, eventID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (Thread.interrupted()) {
                    return null;
                }
                int jdx = 1;
                int orid = rs.getInt(jdx++);
                double lat = rs.getDouble(jdx++);
                double lon = rs.getDouble(jdx++);
                Double depth = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    depth = null;
                }
                double time = rs.getDouble(jdx++);
                Integer nass = rs.getInt(jdx++);
                if (rs.wasNull()) {
                    nass = null;
                }
                Integer ndef = rs.getInt(jdx++);
                if (rs.wasNull()) {
                    ndef = null;
                }
                Integer ndp = rs.getInt(jdx++);
                if (rs.wasNull()) {
                    ndp = null;
                }
                String etype = rs.getString(jdx++);
                if (rs.wasNull()) {
                    etype = null;
                }

                String auth = rs.getString(jdx++);
                String primeStr = rs.getString(jdx++);
                Origerr origerr = getOrigerrP(orid);
                Collection<Netmag> netmags = getNetmagInfoP(orid);
                OriginInfo info = new OriginInfo(eventID, orid, lat, lon, depth, 
                        time, nass, ndef, ndp, etype, auth, origerr, netmags, primeStr.equalsIgnoreCase("y"));
                result.add(info);
            }

            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

}
