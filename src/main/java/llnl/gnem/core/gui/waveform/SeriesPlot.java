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
package llnl.gnem.core.gui.waveform;

import java.awt.Color;
import llnl.gnem.core.gui.plotting.plotobject.AbstractLine;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author addair1
 */
public class SeriesPlot extends SeismogramPlot<TimeSeries> {

    public SeriesPlot(String xLabel, TimeSeries... seismograms) {
        super(xLabel, seismograms);
    }

    @Override
    protected AbstractLine getLine(TimeSeries seismogram, Color lineColor) {
        AbstractLine line = (AbstractLine) getSubplot(seismogram).Plot(0.0, seismogram.getDelta(), 1, seismogram.getData());
        line.setColor(getCorrectedColor(lineColor));
        return line;
    }

    @Override
    public AbstractLine addLine(TimeSeries seismogram, Color lineColor) {
        Line line = new Line(0.0, seismogram.getDelta(), seismogram.getData(), 1);
        line.setColor(lineColor);
        getSubplot(seismogram).AddPlotObject(line);
        return line;
    }

    @Override
    protected void addSeismogram(TimeSeries seismogram) {
        seismogram.addListener(this);
        super.addSeismogram(seismogram);
    }

    public void addSeismogramPublic(TimeSeries seismogram) {
        addSeismogram(seismogram);
    }

    @Override
    public void clear() {
        super.clear();
        for (TimeSeries seismogram : getSeismograms()) {
            seismogram.removeListener(this);
        }
    }
}
