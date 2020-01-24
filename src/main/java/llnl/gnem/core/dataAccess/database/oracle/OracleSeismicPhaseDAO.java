/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.PhaseType;
import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.interfaces.SeismicPhaseDAO;
import llnl.gnem.core.traveltime.Ak135.TraveltimeCalculatorProducer;

/**
 *
 * @author dodge1
 */
public class OracleSeismicPhaseDAO implements SeismicPhaseDAO{

    @Override
    public Collection<SeismicPhase> getAK135Phases() throws DataAccessException {
        try {
            return getAK135PhasesP();
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    
    private Collection<SeismicPhase> getAK135PhasesP() throws Exception
    {
        Collection<SeismicPhase> result = new ArrayList<>();
    
        String sql = String.format("select phasetype, phase,description from %s", 
                TableNames.PHASE_DESC_TABLE);
        TraveltimeCalculatorProducer tcp = TraveltimeCalculatorProducer.getInstance();
        Collection<String> allowablePhases = tcp.getAllowablePhases();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int jdx = 1;
                PhaseType type = PhaseType.valueOf(rs.getString(jdx++));
                String name = rs.getString(jdx++);
                String description = rs.getString(jdx++);
                if( allowablePhases.contains(name)){
                    result.add( new SeismicPhase(type,name,description));
                }
            }
            return result;
        }
        finally{
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
