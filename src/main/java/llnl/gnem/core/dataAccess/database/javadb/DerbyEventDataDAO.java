/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.javadb;

import java.util.Collection;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.EventSummary;
import llnl.gnem.core.dataAccess.selectionCriteria.EventSelectionCriteria;
import llnl.gnem.core.dataAccess.interfaces.EventDataDAO;
import llnl.gnem.core.seismicData.EventInfo;

/**
 *
 * @author dodge1
 */
public class DerbyEventDataDAO implements EventDataDAO{

    @Override
    public Collection<EventSummary> getEvents(EventSelectionCriteria criteria) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<EventSummary> getEvents(Collection<Long> evids) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EventInfo getEventInfo(long eventId) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
