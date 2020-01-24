/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.javadb;

import java.util.Collection;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.interfaces.OriginDAO;
import llnl.gnem.core.gui.map.origins.OriginInfo;
import llnl.gnem.core.seismicData.Netmag;
import llnl.gnem.core.seismicData.Origerr;

/**
 *
 * @author dodge1
 */
public class DerbyOriginDAO implements OriginDAO {

    @Override
    public Origerr getOrigerr(int originId) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<OriginInfo> getOriginsForEvent(long eventID) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<Netmag> getNetmagInfo(int originID) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
