/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.gui.map.origins.OriginInfo;
import llnl.gnem.core.seismicData.Netmag;
import llnl.gnem.core.seismicData.Origerr;

/**
 *
 * @author dodge1
 */
public interface OriginDAO {

    Origerr getOrigerr(int originId) throws DataAccessException;

    Collection<OriginInfo> getOriginsForEvent(long eventID) throws DataAccessException;

    Collection<Netmag> getNetmagInfo(int originID) throws DataAccessException;
}
