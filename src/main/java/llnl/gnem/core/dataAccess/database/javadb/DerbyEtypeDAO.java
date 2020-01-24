/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.javadb;

import java.util.Collection;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.interfaces.EtypeDAO;
import llnl.gnem.core.metadata.EtypeInfo;

/**
 *
 * @author dodge1
 */
public class DerbyEtypeDAO implements EtypeDAO{

    @Override
    public Collection<EtypeInfo> getEtypeInfo() throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
