/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.ProgressMonitor;
import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.waveform.components.ComponentSet;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public interface SeismogramDAO {

    CssSeismogram getSeismogram(long waveformId) throws DataAccessException;

    Collection<BaseSingleComponent> getComponentData(long eventId, double delta, ProgressMonitor monitor) throws DataAccessException;

    Collection<ComponentSet> getComponentSets(long eventId, StationInfo station) throws DataAccessException;

    CorrelationComponent getCorrelationComponent(long waveformId, long eventId, String phase) throws DataAccessException;

    Collection<CorrelationComponent> getNearbyEvents(long eventId, int streamId, String phase, double separationKm,
            ProgressMonitor monitor) throws DataAccessException;

    int getStreamIdForWaveform(long waveformId) throws DataAccessException;
}
