/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public interface FilterDAO {

    Collection<StoredFilter> getAllFilters() throws DataAccessException;

    StoredFilter maybeAddFilter(StoredFilter filterToAdd) throws DataAccessException;

    void setStoredFilterTable(String storedFilterTable);

    void setSequenceName(String filterdSequenceName);
}
