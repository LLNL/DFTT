/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.javadb;

import java.util.Collection;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;
import llnl.gnem.core.dataAccess.interfaces.SeismicPhaseDAO;

/**
 *
 * @author dodge1
 */
public class DerbySeismicPhaseDAO implements SeismicPhaseDAO {

    @Override
    public Collection<SeismicPhase> getAK135Phases() throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
