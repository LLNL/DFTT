package llnl.gnem.core.dataAccess.database.oracle;

import llnl.gnem.core.polygon.SequenceNames;
import llnl.gnem.core.polygon.PolygonSetType;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.polygon.PolygonSet;
import llnl.gnem.core.polygon.Polygon;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.interfaces.PolygonDAO;
import llnl.gnem.core.polygon.Vertex;

public class OraclePolygonDAO implements PolygonDAO {
    
    @Override
    public int insertPolygon(final int polygonSetId, Polygon polygon) throws DataAccessException {
        try {
            return insertPolygonP(polygonSetId, polygon);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }
    
    private int insertPolygonP(final int polygonSetId, Polygon polygon) throws SQLException {
        
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            return insertPolygon(polygonSetId, polygon, conn);
        } finally {
            if (conn != null) {
                DAOFactory.getInstance().getConnections().checkIn(conn);
            }
        }
    }
    
    private int insertPolygon(final int polygonSetId, Polygon polygon, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        String sql = String.format("insert into %s values(?, ?, ?, ?, ?, ?, ?, lower(user), SYSDATE)",
                TableNames.POLYGON_TABLE);
        try {
            
            stmt = conn.prepareStatement(sql);
            
            final int polyId = (int)OracleDBUtil.getNextId(conn, SequenceNames.POLYID);

            // Insert the polygon table entry
            int ndx = 1;
            stmt.setInt(ndx++, polygonSetId);
            stmt.setInt(ndx++, polyId);
            stmt.setString(ndx++, polygon.getName());
            stmt.setDouble(ndx++, polygon.getMinLat());
            stmt.setDouble(ndx++, polygon.getMaxLat());
            stmt.setDouble(ndx++, polygon.getMinLon());
            stmt.setDouble(ndx++, polygon.getMaxLon());
            stmt.execute();
            
            insertPolygonVertices(polygon, polyId, conn);
            conn.commit();
            return polyId;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            
        }
    }
    
    @Override
    public void updatePolygonSetBounds(PolygonSet set) throws DataAccessException {
        try {
            updatePolygonSetBoundsP(set);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }
    
    private void updatePolygonSetBoundsP(PolygonSet set) throws SQLException {
        
        set.refreshSetBounds();
        
        String sql = String.format("update %s set minlat=?, maxlat=?, minlon=?, maxlon=? where polysetid=%d",
                TableNames.POLYGON_SET_TABLE,
                set.getId());
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            int idx = 1;
            stmt.setDouble(idx++, set.getMinLat());
            stmt.setDouble(idx++, set.getMaxLat());
            stmt.setDouble(idx++, set.getMinLon());
            stmt.setDouble(idx++, set.getMaxLon());
            stmt.execute();
            conn.commit();
        } finally {
            
            if (stmt != null) {
                stmt.close();
            }
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }
    
    private boolean isSetExists(PolygonSetType type, String name, Connection conn)
            throws SQLException {
        
        String sql = String.format(
                "select polysetid from %s where set_name=? and set_type=?",
                TableNames.POLYGON_SET_TABLE);
        
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            
            stmt = conn.prepareStatement(sql);
            int ndx = 1;
            stmt.setString(ndx++, name.trim());
            stmt.setString(ndx++, type.toString());
            
            result = stmt.executeQuery();
            return result.next();
        } finally {
            if (result != null) {
                result.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    @Override
    public Integer polygonExistsInPolygonSet(int polygonSetId, Polygon poly)
            throws DataAccessException {
        try {
            return polygonExistsInPolygonSetP(polygonSetId, poly);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }
    
    private Integer polygonExistsInPolygonSetP(int polygonSetId, Polygon poly)
            throws SQLException {
        final double degreeOffset = 0.005;  // within 5 thousands of a degree

        String sql = String.format(
                "select polyid from %s where polysetid=? and minlat < ? and maxlat > ? and minlon < ? and maxlon > ?",
                TableNames.POLYGON_TABLE);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            int ndx = 1;
            stmt.setInt(ndx++, polygonSetId);
            stmt.setDouble(ndx++, poly.getMinLat() + degreeOffset);
            stmt.setDouble(ndx++, poly.getMaxLat() - degreeOffset);
            stmt.setDouble(ndx++, poly.getMinLon() + degreeOffset);
            stmt.setDouble(ndx++, poly.getMaxLon() - degreeOffset);
            
            result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        } finally {
            if (result != null) {
                
                result.close();
                if (stmt != null) {
                    stmt.close();
                }
                DAOFactory.getInstance().getConnections().checkIn(conn);
            }
        }
        return null;
    }
    
    @Override
    public PolygonSet retrievePolyset(int polysetid) throws DataAccessException {
        try {
            return retrievePolysetP(polysetid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }
    
    private PolygonSet retrievePolysetP(int polysetid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            Collection<Polygon> polygons = getPolygonsBySetid(polysetid, conn);
            String sql = String.format("select set_type,set_name from %s where polysetid = ?",
                    TableNames.POLYGON_SET_TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, polysetid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int idx = 1;
                String setType = rs.getString(idx++);
                String setName = rs.getString(idx++);
                return new PolygonSet(PolygonSetType.valueOf(setType), setName, polysetid, polygons);
            } else {
                return null;
            }
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
    
    @Override
    public PolygonSet insertPolygonSet(PolygonSetType type, String name, Collection<Polygon> polygons)
            throws DataAccessException {
        try {
            return insertPolygonSetP(type, name, polygons);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }
    
    private PolygonSet insertPolygonSetP(PolygonSetType type, String name, Collection<Polygon> polygons)
            throws SQLException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            
            if (isSetExists(type, name, conn)) {
                throw new IllegalStateException("Polygon Set " + name + " exists!");
            }
            
            int polySetId = (int)OracleDBUtil.getNextId(conn, SequenceNames.POLYSETID);
            
            PolygonSet polySet = new PolygonSet(type, name, polySetId, polygons);
            String sql = String.format(
                    "insert into %s values(?, ?, ?, ?, ?, ?, ?, lower(user), SYSDATE)",
                    TableNames.POLYGON_SET_TABLE);
            stmt = conn.prepareStatement(sql);
            int ndx = 1;
            stmt.setInt(ndx++, polySet.getId());
            stmt.setString(ndx++, polySet.getType().toString());
            stmt.setString(ndx++, polySet.getName());
            stmt.setDouble(ndx++, polySet.getMinLat());
            stmt.setDouble(ndx++, polySet.getMaxLat());
            stmt.setDouble(ndx++, polySet.getMinLon());
            stmt.setDouble(ndx++, polySet.getMaxLon());
            
            stmt.execute();

            // Insert the polygons [ might have more original! ]
            for (Polygon polygon : polygons) {
                int polyid = insertPolygon(polySetId, polygon, conn);
                polygon.setPolyid(polyid);
            }
            conn.commit();
            return polySet;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }
    
    @Override
    public void deletePolygon(int polyid)
            throws DataAccessException {
        try {
            deletePolygonP(polyid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private void deletePolygonP(int polyid)
            throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        String sql = String.format("delete from %s where polyid=%d", TableNames.POLYGON_TABLE, polyid);
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.createStatement();
            stmt.execute(sql);
            conn.commit();
        } finally {
            
            if (stmt != null) {
                stmt.close();
            }
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }
    
    private void insertPolygonVertices(Polygon polygon, int polyId, Connection conn) throws SQLException {
        
        String sql = String.format("insert into %s values(?, ?, ?, ?)",
                TableNames.POLYGON_DATA_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int vertId = 1;
            for (Vertex vert : polygon.getVertices()) {
                int ndx = 1;
                stmt.setInt(ndx++, polyId);
                stmt.setInt(ndx++, vertId++);
                stmt.setDouble(ndx++, vert.getLat());
                stmt.setDouble(ndx++, vert.getLon());
                stmt.execute();
            }
        }
    }
    
    @Override
    public void deletePolygonSet(PolygonSet set) throws DataAccessException {
        try {
            deletePolygonSetP(set);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }
    
    private void deletePolygonSetP(PolygonSet set) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = String.format(
                "delete from %s where polysetid = ?",
                TableNames.POLYGON_SET_TABLE);
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, set.getId());
            stmt.execute();
            conn.commit();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }
    
    private Collection<Polygon> getPolygonsBySetid(int polysetid, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Collection<Polygon> result = new ArrayList<>();
        try {
            String sql = String.format("select polyid, poly_name,minlat,maxlat,minlon, maxlon from %s where polysetid = ?", TableNames.POLYGON_TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, polysetid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int idx = 1;
                int polyid = rs.getInt(idx++);
                String polyName = rs.getString(idx++);
                double minLat = rs.getDouble(idx++);
                double maxLat = rs.getDouble(idx++);
                double minLon = rs.getDouble(idx++);
                double maxLon = rs.getDouble(idx++);
                Vertex[] verts = getPolygonVertices(polyid, conn);
                Polygon poly = new Polygon(polyid, polyName, verts, minLat, maxLat, minLon, maxLon);
                result.add(poly);
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
    
    private Vertex[] getPolygonVertices(int polyid, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Vertex> tmp = new ArrayList<>();
        try {
            String sql = String.format("select lat,lon from %s where polyid = ?", TableNames.POLYGON_DATA_TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, polyid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                double lat = rs.getDouble(1);
                double lon = rs.getDouble(2);
                Vertex v = new Vertex(lat, lon);
                tmp.add(v);
            }
            return tmp.toArray(new Vertex[1]);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
        
    }
    
    @Override
    public Collection<PolygonSet> getAllPolygonSets() throws DataAccessException {
        try {
            return getAllPolygonSetsP();
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }
    
    private Collection<PolygonSet> getAllPolygonSetsP() throws SQLException {
        Collection<PolygonSet> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = String.format("select polysetid from %s", TableNames.POLYGON_SET_TABLE);
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int polysetid = rs.getInt(1);
                result.add(retrievePolysetP(polysetid));
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
