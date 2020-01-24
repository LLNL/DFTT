package llnl.gnem.apps.detection.dataAccess.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.OriginInfo;
import llnl.gnem.apps.detection.dataAccess.interfaces.OriginDAO;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.database.Connections;
import llnl.gnem.core.util.Epoch;

public abstract class DbOriginDAO implements OriginDAO {

    @Override
    public OriginInfo getOriginInfo(int evid) throws DataAccessException
    {
        try{
            return getOriginInfoP(evid);
        }
        catch(SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }
    
    
    private OriginInfo getOriginInfoP(int evid) throws SQLException
    {
       
        String sql = String.format("select lat,lon,depth,time,mag,auth from %s where evid = ?", TableNames.getOriginTable());
        Connection conn = null;
        try{
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, evid);
                try(ResultSet rs = stmt.executeQuery()){
                    while(rs.next()){
                        int jdx = 1;
                        double lat = rs.getDouble(jdx++);
                        double lon = rs.getDouble(jdx++);
                        double depth = rs.getDouble(jdx++);
                        double time = rs.getDouble(jdx++);
                        double mag = rs.getDouble(jdx++);
                        String auth = rs.getString(jdx++);
                        return new OriginInfo(evid,lat,lon,depth,time,mag,auth);
                    }
                }
                return null;
            }
        }
        finally{
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }
    
    @Override
    public Collection<OriginInfo> getOriginsInTimeWindow(Epoch epoch) throws DataAccessException
    {
        try{
            return getOriginsInTimeWindowP(epoch);
        }
        catch(SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    private Collection<OriginInfo> getOriginsInTimeWindowP(Epoch epoch) throws SQLException {
      
        Collection<OriginInfo> result = new ArrayList<>();
        String sql = String.format("select evid, lat,lon,depth,time,mag,auth from %s where time between ? and ?", TableNames.getOriginTable());
        Connection conn = null;
        try{
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setDouble(1, epoch.getStart());
                stmt.setDouble(2, epoch.getEnd());
                try(ResultSet rs = stmt.executeQuery()){
                    while(rs.next()){
                        int jdx = 1;
                        int evid = rs.getInt(jdx++);
                        double lat = rs.getDouble(jdx++);
                        double lon = rs.getDouble(jdx++);
                        double depth = rs.getDouble(jdx++);
                        double time = rs.getDouble(jdx++);
                        double mag = rs.getDouble(jdx++);
                        String auth = rs.getString(jdx++);
                        result.add(new OriginInfo(evid,lat,lon,depth,time,mag,auth));
                    }
                }
                return result;
            }
        }
        finally{
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }
}
