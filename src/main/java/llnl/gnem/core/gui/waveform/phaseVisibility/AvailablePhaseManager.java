/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.phaseVisibility;

import java.util.Collection;
import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;

/**
 *
 * @author dodge1
 */
public interface AvailablePhaseManager {

    Collection<SeismicPhase> getAvailablePhases();

    boolean isAvailablePhase(SeismicPhase phase);

    boolean isAvailablePhase(String phase);

    void replacePhases(Collection<SeismicPhase> phases);
}
