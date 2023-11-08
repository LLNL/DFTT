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
package llnl.gnem.apps.detection.dataAccess.database.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;

import llnl.gnem.apps.detection.dataAccess.database.TableNames;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayElementInfo;
import llnl.gnem.apps.detection.dataAccess.interfaces.ArrayConfigurationDAO;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;

/**
 *
 * @author dodge1
 */
public class OracleArrayConfigurationDAO implements ArrayConfigurationDAO{
    
    private OracleArrayConfigurationDAO() {
    }
    
    public static OracleArrayConfigurationDAO getInstance() {
        return OracleArrayConfigurationDAOHolder.INSTANCE;
    }

    @Override
    public Collection<ArrayElementInfo> getAllArrayElements() throws DataAccessException {
        try {
            return getAllElementsP();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    
    private static class OracleArrayConfigurationDAOHolder {

        private static final OracleArrayConfigurationDAO INSTANCE = new OracleArrayConfigurationDAO();
    }
    
    
    
    private Collection<ArrayElementInfo> getAllElementsP() throws SQLException {
        Collection<ArrayElementInfo> result= new ArrayList<>();
        Connection conn = null;
        String sql = String.format("SELECT AGENCY ,NETWORK_CODE,ARRAY_NAME ,STATION_CODE,STANAME ,BEGIN_TIME,ONDATE,END_TIME,OFFDATE ,LAT ,LON,ELEV,STATYPE ,DNORTH,DEAST from %s",
                TableNames.getArrayInfoTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
           
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try(ResultSet rs = stmt.executeQuery()){
                    while(rs.next()){
                        int jdx = 1;
                        String agency = rs.getString(jdx++);
                        String net = rs.getString(jdx++);
                        String arrayName = rs.getString(jdx++);
                        String stationCode = rs.getString(jdx++);
                        String description = rs.getString(jdx++);
                        double beginTime = rs.getDouble(jdx++);
                        int ondate = rs.getInt(jdx++);
                        double endTime = rs.getDouble(jdx++);
                        int offdate = rs.getInt(jdx++);
                        double lat = rs.getDouble(jdx++);
                        double lon = rs.getDouble(jdx++);
                        double elev = rs.getDouble(jdx++);
                        
                        String statype = rs.getString(jdx++);
                        double dnorth = rs.getDouble(jdx++);
                        double deast = rs.getDouble(jdx++);
                        result.add(new ArrayElementInfo( agency,  net,  arrayName,  
                                stationCode,  description,  beginTime,  ondate,  
                                endTime,  offdate,  lat,  lon,  elev,  statype,  
                                dnorth,  deast));
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return result;
    }
}
