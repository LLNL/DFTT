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
package llnl.gnem.dftt.core.database.dao;

import llnl.gnem.dftt.core.dataAccess.database.oracle.OracleDBUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.dftt.core.database.ConnectionManager;
import llnl.gnem.dftt.core.database.column.CssVersion;
import llnl.gnem.dftt.core.database.row.ColumnSet;
import llnl.gnem.dftt.core.database.row.SiteRow;
import llnl.gnem.dftt.core.metadata.site.core.CssSite;
import llnl.gnem.dftt.core.util.FileInputArrayLoader;

/**
 *
 * @author dodge1
 */
public class CssSiteDAO {

    private static final int FOREVER = 2286324;
    private static String siteTableName = "detector.site";
    public static void setSiteTableName(String name){
        siteTableName = name;
    }

    private CssSiteDAO() {
    }

    public static CssSiteDAO getInstance() {
        return CssSiteDAOHolder.INSTANCE;
    }

    private static class CssSiteDAOHolder {

        private static final CssSiteDAO INSTANCE = new CssSiteDAO();
    }

    public Collection<CssSite> getArrayElements(String refsta, int jdate, String filename, CssVersion version) throws IOException, ParseException
    {
        Collection<CssSite> result = new ArrayList<>();
        Collection<CssSite> all = readSiteFile(filename, version);
        for( CssSite site : all){
            if(site.getRefsta().equals(refsta)&& site.isEffective(jdate)){
                result.add(site);
            }
        }
        return result;
    }

    public Collection<CssSite> readSiteFile(String filename, CssVersion version) throws IOException, ParseException {
        Collection<CssSite> result = new ArrayList<>();
        ColumnSet.setVersion(version);

        String[] lines = FileInputArrayLoader.fillStrings(filename);
        for (String line : lines) {
            SiteRow sr = new SiteRow();
            sr.parseString(line);
            int offdate = sr.getOffdate();
            if (offdate <= 0) {
                offdate = FOREVER;
            }
            CssSite site = new CssSite(sr.getSta(), sr.getOndate(), offdate, sr.getLat(), sr.getLon(),
                    sr.getElev(), sr.getStaname(), sr.getStatype(), sr.getRefsta(), sr.getDnorth(), sr.getDeast());
            result.add(site);
        }
        return result;
    }

    /**
     * Check if station is in the table
     * @param sta
     * @param tableName
     * @param conn
     * @return
     * @throws SQLException
     */
    public boolean siteExists(String sta, String tableName, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("select ondate from %s where sta = ?", tableName);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, sta);
            rs = stmt.executeQuery();
            return rs.next();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    public Collection<String> getStationsByRefsta(String refsta) throws SQLException {
        Collection<String> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select distinct sta from %s "
                    + "where refsta = ? ",siteTableName));
            stmt.setString(1, refsta);
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
        }
        finally{
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            ConnectionManager.getInstance().checkIn(conn);
        }
        
        
        return result;
    }
    
    public Collection<CssSite> getArrayElements(String refsta, int jdate, String siteTable, Connection conn) throws SQLException
    {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Collection<CssSite> result = new ArrayList<>();
        try {
            stmt = conn.prepareStatement(String.format("select lat, lon, elev, staname, "
                    + "ondate, offdate,statype, sta, dnorth,deast from %s "
                    + "where refsta = ? and ? between ondate and offdate", siteTable));
            stmt.setString(1, refsta);

            stmt.setInt(2, jdate);
            rs = stmt.executeQuery();
            while (rs.next()) {
                double lat = rs.getDouble(1);
                double lon = rs.getDouble(2);
                double elev = rs.getDouble(3);
                String staname = rs.getString(4);
                int ondate = rs.getInt(5);
                int offdate = rs.getInt(6);
                String statype = rs.getString(7);
                String sta = rs.getString(8);
                double dnorth = rs.getDouble(9);
                double deast = rs.getDouble(10);
                result.add (new CssSite(sta, ondate, offdate, lat, lon, elev, staname, statype, refsta, dnorth, deast));
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

    
    public CssSite getSiteRow(String sta, int jdate, String siteTable, Connection conn) throws SQLException {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(String.format("select lat, lon, elev, staname, "
                    + "ondate, offdate,statype, refsta, dnorth,deast from %s "
                    + "where sta = ? and ? between ondate and offdate", siteTable));
            stmt.setString(1, sta);

            stmt.setInt(2, jdate);
            rs = stmt.executeQuery();
            if (rs.next()) {
                double lat = rs.getDouble(1);
                double lon = rs.getDouble(2);
                double elev = rs.getDouble(3);
                String staname = rs.getString(4);
                int ondate = rs.getInt(5);
                int offdate = rs.getInt(6);
                String statype = rs.getString(7);
                String refsta = rs.getString(8);
                double dnorth = rs.getDouble(9);
                double deast = rs.getDouble(10);
                return new CssSite(sta, ondate, offdate, lat, lon, elev, staname, statype, refsta, dnorth, deast);
            }
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * 
     * @param sta
     * @param siteMasterTable
     * @param conn
     * @return
     * @throws SQLException 
     */
    public Collection<CssSite> getAllSiteMasterRows(String sta, String siteMasterTable, Connection conn) throws SQLException {
        Collection<CssSite> result = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(String.format("select lat, lon, elev, staname, "
                    + "ondate, offdate,statype, refsta, dnorth,deast, siteid from %s "
                    + "where sta = ? ", siteMasterTable));
            stmt.setString(1, sta);

            rs = stmt.executeQuery();
            while (rs.next()) {
                double lat = rs.getDouble(1);
                double lon = rs.getDouble(2);
                double elev = rs.getDouble(3);
                String staname = rs.getString(4);
                int ondate = rs.getInt(5);
                int offdate = rs.getInt(6);
                String statype = rs.getString(7);
                String refsta = rs.getString(8);
                double dnorth = rs.getDouble(9);
                double deast = rs.getDouble(10);
                int siteid = rs.getInt(11);
                result.add(new CssSite(sta, ondate, offdate, lat, lon, elev,
                        staname, statype, refsta, dnorth, deast, siteid));
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

    public int writeSiteMasterRow(CssSite site,
            String siteTableName,
            String siteidSequenceName,
            Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(String.format("insert into %s values "
                + "(?,?,?,?,?,?,?,?,?,?,?,sysdate, %s.nextval)",
                siteTableName, siteidSequenceName))) {
            stmt.setString(1, site.getSta());
            stmt.setInt(2, site.getOndate());
            stmt.setInt(3, site.getOffdate());
            stmt.setDouble(4, site.getLat());
            stmt.setDouble(5, site.getLon());
            stmt.setDouble(6, site.getElevation());
            stmt.setString(7, site.getStaname());
            stmt.setString(8, site.getStatype());
            stmt.setString(9, site.getRefsta());
            stmt.setDouble(10, site.getDnorth());
            stmt.setDouble(11, site.getDeast());
            stmt.execute();
            int siteid = (int)OracleDBUtil.getIdCurrVal(conn, siteidSequenceName);
            conn.commit();
            return siteid;
        }
    }
}
