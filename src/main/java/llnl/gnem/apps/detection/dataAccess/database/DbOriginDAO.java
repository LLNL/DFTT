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
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.database.Connections;
import llnl.gnem.dftt.core.util.Epoch;

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
