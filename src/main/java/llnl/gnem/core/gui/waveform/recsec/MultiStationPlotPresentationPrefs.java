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
package llnl.gnem.core.gui.waveform.recsec;

import java.awt.Color;
import llnl.gnem.core.dataAccess.dataObjects.ApplicationStationInfo;

import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.gui.waveform.plotPrefs.PlotPresentationPrefs;

public class MultiStationPlotPresentationPrefs extends PlotPresentationPrefs {

    private static final long serialVersionUID = -7669758834987828569L;

    /**
     * Return the preferred trace color for the given station. Default
     * implementation is to return simply the preferred trace color
     *
     * @param info
     * @return
     */
    public Color getTraceColor(StationInfo info) {
        if (info instanceof ApplicationStationInfo) {
            ApplicationStationInfo asi = (ApplicationStationInfo) info;
            return asi.getArrayId() != null ? Color.black : Color.blue;
        } else {
            return super.getTraceColor();
        }
    }
}
