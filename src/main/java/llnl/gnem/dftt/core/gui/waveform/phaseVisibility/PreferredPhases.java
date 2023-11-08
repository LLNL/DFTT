/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.dftt.core.gui.waveform.phaseVisibility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import llnl.gnem.dftt.core.dataAccess.dataObjects.PhaseType;
import llnl.gnem.dftt.core.dataAccess.dataObjects.SeismicPhase;

/**
 *
 * @author dodge1
 */
public class PreferredPhases implements Serializable {

    private final Collection<SeismicPhase> phases;
    static final long serialVersionUID = 5663822896377567970L;

    public PreferredPhases() {
        phases = new ArrayList<>();
        phases.add(new SeismicPhase(PhaseType.MANTLE, "P", "-"));
    }

    public void clear() {
        phases.clear();
    }

    public void addPhase(SeismicPhase phase) {
        phases.remove(phase);
        phases.add(phase);
    }

    public Collection<SeismicPhase> getPhases() {
        return new ArrayList<>(phases);
    }

    void removePhase(SeismicPhase phase) {
        phases.remove(phase);
    }

    void removePhase(String phase) {
        Iterator<SeismicPhase> it = phases.iterator();
        while (it.hasNext()) {
            if (it.next().getName().equals(phase)) {
                it.remove();
                return;
            }
        }
    }

    boolean contains(SeismicPhase phase) {
        return phases.contains(phase);
    }

    boolean contains(String phase) {
        for (SeismicPhase aPhase : phases) {
            if (aPhase.getName().equals(phase)) {
                return true;
            }
        }
        return false;
    }

    void addPhase(String phase) {
        for (SeismicPhase aPhase : BaseAvailablePhaseManager.getInstance().getAvailablePhases()) {
            if (aPhase.getName().equals(phase)) {
                phases.add(aPhase);
                return;
            }
        }
    }
}
