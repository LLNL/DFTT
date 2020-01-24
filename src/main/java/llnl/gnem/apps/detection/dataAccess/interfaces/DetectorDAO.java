/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.interfaces;

import java.util.List;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public interface DetectorDAO {

    List<StreamKey> getDetectorChannels(long detectorid) throws DataAccessException;

    List<StreamKey> getDetectorChannelsFromConfig(long configid) throws DataAccessException;
}
