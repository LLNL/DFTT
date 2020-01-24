package llnl.gnem.core.gui.waveform.filterBank;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;
import llnl.gnem.core.gui.filter.FilterModel;
import llnl.gnem.core.seismicData.AbstractEventInfo;
import llnl.gnem.core.gui.map.events.EventModel;
import llnl.gnem.core.gui.plotting.PlotObjectClicked;
import llnl.gnem.core.gui.plotting.VertAlignment;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickTextPosition;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.XPinnedText;
import llnl.gnem.core.gui.waveform.WaveformPlot;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.gui.waveform.phaseVisibility.UsablePhaseManager;
import llnl.gnem.core.traveltime.Ak135.TraveltimeCalculatorProducer;
import llnl.gnem.core.traveltime.Point3D;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public final class FilterBankPlot extends WaveformPlot {
    private final FilterBankViewer parent;
    private final FilterBankModel dataModel;
    private final Map<Line, StoredFilter> lineToFilter;
    private JSubplot plot;

    public FilterBankPlot(FilterBankViewer parent) {
        this.parent = parent;
        configurePlotFromPrefs();

        dataModel = parent.getDataModel();

        lineToFilter = new HashMap<>();

        plot = addSubplot();
        getXaxis().setLabelText("Time Relative to Origin (seconds)");
        setPlotProperties(plot);
        plot.getPlotRegion().setDrawBox(true);
        addPlotObjectObserver(this);
    }

    @Override
    public void clear() {
        super.clear();
        plot = addSubplot();
        setPlotProperties(plot);
        repaint();
    }

    @Override
    protected void handlePlotObjectClicked(Object obj) {
        PlotObjectClicked poc = (PlotObjectClicked) obj;
        if (poc.po instanceof Line) {
            Line line = (Line) poc.po;
            StoredFilter filter = lineToFilter.get(line);
            if (filter != null) {
                filter(filter, line);
            }
        }
    }

    public void filter(StoredFilter filter, Line line) {
        dataModel.applyFilter(filter);
        for (Line currentLine : lineToFilter.keySet()) {
            currentLine.setColor(prefs.getTraceColor());
        }
        line.setColor(prefs.getSelectedTraceColor());
        FilterModel.getInstance().changeSelectedFilter(filter);
    }

    public void setData(Collection<SeisFilterData> data, BaseSingleComponent aComp) {
        plot.Clear();

        EventModel<? extends AbstractEventInfo> eventModel = parent.getDataModel().getEventModel();
        AbstractEventInfo event = eventModel.getCurrent();
        double range = 1.0;
        double center = range / 2;

        for (SeisFilterData sfd : data) {
            CssSeismogram seis = sfd.getSeismogram();

            double delta = seis.getDelta();
            double max = seis.getMax();
            double min = seis.getMin();
            double thisRange = max - min;
            double scale = range / thisRange;
            seis.MultiplyScalar(scale);
            seis.AddScalar(center);
            float[] values = seis.getData();
            double start = seis.getTimeAsDouble() - event.getTime().getEpochTime();

            Line line = new Line(start, delta, values);
            plot.AddPlotObject(line);
            lineToFilter.put(line, sfd.getFilter());

            String label = String.format("%s", sfd.getFilter().toString());
            XPinnedText text = new XPinnedText(5, center + 0.05, label);
            text.setVerticalAlignment(VertAlignment.BOTTOM);
            plot.AddPlotObject(text);

            center += range;
        }
        addTheoreticalPicks(aComp, event);
        plot.SetAxisLimits();
        plot.getYaxis().setMin(0);
        plot.getYaxis().setMax(data.size());
        
        this.repaint();
    }

    private void addTheoreticalPicks(BaseSingleComponent aComp, AbstractEventInfo eventInfo) {
        UsablePhaseManager apm = WaveformViewerFactoryHolder.getInstance().getUsablePhaseManager();

        for (SeismicPhase phase : apm.getUsablePhases()) {
            try {
                llnl.gnem.core.traveltime.SinglePhaseTraveltimeCalculator calculator = TraveltimeCalculatorProducer.getInstance().getSinglePhaseTraveltimeCalculator(phase.getName());
                Point3D staPos = aComp.getPoint3D();
                Point3D eventPos = eventInfo.getPoint3D();
                double ttime = calculator.getTT(eventPos, staPos);
                if (ttime > 0) {
                    int pickLineWidth = 1;
                    boolean draggable = false;
                    Color pickColor = Color.gray;
                    VPickLine vpl = new VPickLine(ttime, 0.9, phase.getName(),
                            pickColor, pickLineWidth, draggable, 10,
                            PickTextPosition.TOP);


                    vpl.setVisible(true);
                    vpl.setErrorBarShowHandles(false);
                    vpl.setShowErrorBars(false);
                    plot.AddPlotObject(vpl);
                }
            } catch (IOException | ClassNotFoundException ex) {
                ApplicationLogger.getInstance().log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException e) {
                ApplicationLogger.getInstance().log(Level.FINE, "Failed plotting theoretical time!", e);
            }
        }
    }

    @Override
    public void magnify() {
        scaleTraces(2.0);
    }

    private void scaleTraces(double factor) {
        Line[] lines = plot.getLines();
        for (Line line : lines) {
            float[] data = line.getYdata();
            double mean = SeriesMath.getMean(data);
            SeriesMath.RemoveMean(data);
            SeriesMath.MultiplyScalar(data, factor);
            data = SeriesMath.Add(data, mean);
            line.replaceYarray(data);
        }
        repaint();
    }

    @Override
    public void reduce() {
        scaleTraces(0.5);
    }

    @Override
    public void unzoomAll() {
        super.unzoomAll();
    }

    @Override
    public void configurePlotFromPrefs() {
        super.configurePlotFromPrefs();
        getPlotRegion().setDrawBox(false);
    }
    
    @Override
    protected void setPlotProperties(JSubplot plot)
    {
        super.setPlotProperties(plot);
        plot.getPlotRegion().setDrawBox(true);
        plot.getYaxis().setTicksVisible(false);
    }
}
