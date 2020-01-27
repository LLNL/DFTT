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
