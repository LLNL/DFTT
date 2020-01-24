/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.EventSummary;
import llnl.gnem.core.dataAccess.selectionCriteria.EventSelectionCriteria;
import llnl.gnem.core.seismicData.EventInfo;

/**
 *
 * @author dodge1
 */
public interface EventDataDAO {

    Collection<EventSummary> getEvents(EventSelectionCriteria criteria) throws DataAccessException;

    Collection<EventSummary> getEvents(Collection<Long> evids) throws DataAccessException;

    EventInfo getEventInfo(long eventId) throws DataAccessException;
}
