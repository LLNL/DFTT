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
package llnl.gnem.dftt.core.gui.waveform.factory;

import llnl.gnem.dftt.core.gui.waveform.StationNavigationModel;
import llnl.gnem.dftt.core.gui.waveform.factory.actions.ComponentDataSaver;
import llnl.gnem.dftt.core.gui.waveform.CompSetPlotToolbar;
import llnl.gnem.dftt.core.gui.waveform.ThreeComponentModel;
import llnl.gnem.dftt.core.gui.waveform.filterBank.FilterBankToolbar;
import llnl.gnem.dftt.core.gui.waveform.phaseVisibility.PreferredPhaseManager;
import llnl.gnem.dftt.core.gui.waveform.phaseVisibility.UsablePhaseManager;
import llnl.gnem.dftt.core.gui.waveform.recsec.MultiStationPlot;
import llnl.gnem.dftt.core.gui.waveform.recsec.MultiStationWaveformDataModel;
import llnl.gnem.dftt.core.gui.waveform.recsec.RsToolbar;
import llnl.gnem.dftt.core.gui.waveform.phaseVisibility.AvailablePhaseManager;

/**
 *
 * @author dodge1
 */
public interface WaveformViewerComponentFactory {
    MainForm getMainForm();

    AvailablePhaseManager getAvailablePhaseManager();

    PreferredPhaseManager getPreferredPhaseManager();

    UsablePhaseManager getUsablePhaseManager();

    StationNavigationModel getStationNavigationModel();

    ComponentDataSaver getComponentDataSaver();

    CompSetPlotToolbar getCompSetPlotToolbar();

    MultiStationWaveformDataModel getMSWaveformDataModel();

    MultiStationPlot getMultiStationWaveformView();

    RsToolbar getRsToolbar();

    FilterBankToolbar getFilterbankToolbar();

    ThreeComponentModel getThreeComponentModel();
}
