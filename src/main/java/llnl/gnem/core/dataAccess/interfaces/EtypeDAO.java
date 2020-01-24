/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.metadata.EtypeInfo;

/**
 *
 * @author dodge1
 */
public interface EtypeDAO {
    Collection<EtypeInfo> getEtypeInfo() throws DataAccessException;
}
