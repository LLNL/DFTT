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
