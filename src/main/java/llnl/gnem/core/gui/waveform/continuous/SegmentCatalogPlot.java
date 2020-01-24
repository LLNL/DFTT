/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.continuous;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import llnl.gnem.core.gui.plotting.MouseMode;
import llnl.gnem.core.gui.plotting.PlotObjectClicked;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JPlotKeyMessage;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickMovedState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.WindowDurationChangedState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.ZoomInStateChange;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.ZoomOutStateChange;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.waveform.continuous.segments.ChannelSegmentCatalog;
import llnl.gnem.core.waveform.continuous.segments.SegmentCatalogModel;
import llnl.gnem.core.waveform.continuous.segments.SegmentCatalogView;

/**
 *
 * @author dodge
 */
public class SegmentCatalogPlot extends JMultiAxisPlot implements SegmentCatalogView, Observer {

    protected final JSubplot subplot;
    private final SegmentCatalogModel model;
    private final Map<SegmentCatalogLine, StreamKey> lineKeyMap;
    private VPickLine selectWindowPickLine;

    public SegmentCatalogPlot(SegmentCatalogModel model) {
        super(JMultiAxisPlot.XAxisType.EpochTime);
        subplot = this.addSubplot();
        this.model = model;
        subplot.getYaxis().setTicksVisible(false);
        lineKeyMap = new HashMap<>();
        addPlotObjectObserver(this);
    }

    @Override
    public void catalogsWereLoaded() {
        subplot.Clear();
        lineKeyMap.clear();
        List<StreamKey> keys = model.getCatalogList();
        int numLines = keys.size();
        if (numLines > 0) {
            double ymin = 0;
            double ymax = 1;
            double xmin = Double.MAX_VALUE;
            double xmax = -xmin;
            double spacing = 1.0 / (numLines + 1);
            int count = 0;
            for (StreamKey key : keys) {
                ChannelSegmentCatalog catalog = model.getChannelSegmentCatalog(key);
                double yValue = ++count * spacing;
                SegmentCatalogLine line = new SegmentCatalogLine(catalog, (float) yValue);
                subplot.AddPlotObject(line);
                lineKeyMap.put(line, key);
                double tmp = catalog.getStart();
                if (tmp < xmin) {
                    xmin = tmp;
                }
                tmp = catalog.getEnd();
                if (tmp > xmax) {
                    xmax = tmp;
                }
            }
            subplot.SetAxisLimits(xmin, xmax, ymin, ymax);
            double windowTime = (xmin + xmax) / 2;
            model.setSelectionWindowTime(windowTime);
            selectWindowPickLine = new VPickLine(windowTime, 1.0, "");
            double duration = model.getSelectionWindowDuration();
            selectWindowPickLine.getWindow().setDuration(duration);
            selectWindowPickLine.getWindow().setVisible(true);
            selectWindowPickLine.getWindow().setCanDragX(true);
            selectWindowPickLine.getWindowHandle().setCanDragX(true);
            selectWindowPickLine.getWindow().setRightHandleFractionalWidth(1.0);
            selectWindowPickLine.getWindowHandle().setWidth(3);
            subplot.AddPlotObject(selectWindowPickLine);
        }
        repaint();
    }

    @Override
    public void catalogWasLoaded(StreamKey key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void catalogsCleared() {
        subplot.Clear();
        lineKeyMap.clear();
        repaint();
    }

    @Override
    public void update(Observable o, Object obj) {
        if (obj instanceof PlotObjectClicked) {
            PlotObjectClicked poc = (PlotObjectClicked) obj;
            if (poc.po instanceof Line) {
            }

        } else if (obj instanceof WindowDurationChangedState) {
            WindowDurationChangedState wdcs = (WindowDurationChangedState) obj;
            if (wdcs.getWindowHandle().getAssociatedPick() == selectWindowPickLine) {
                double delta = wdcs.getDeltaD();
                double newDuration = model.validateDurationChange(delta);
                model.setWindowDuration(newDuration);
            }
        } else if (obj instanceof MouseMode) {
            // System.out.println( "owner.setMouseModeMessage((MouseMode) obj);");
        } else if (obj instanceof JPlotKeyMessage) {
            JPlotKeyMessage msg = (JPlotKeyMessage) obj;
            KeyEvent e = msg.getKeyEvent();
            //       ControlKeyMapper controlKeyMapper = msg.getControlKeyMapper();
            int keyCode = e.getKeyCode();

            PlotObject po = msg.getPlotObject();

        } else if (obj instanceof ZoomInStateChange) {
//            ZoomInStateChange zisc = (ZoomInStateChange) obj;
//            this.getSubplotManager().zoomToBox(zisc.getZoomBounds());
        } else if (obj instanceof ZoomOutStateChange) {
//            this.getSubplotManager().UnzoomAll();
        } else if (obj instanceof PickMovedState) {
            PickMovedState pms = (PickMovedState) obj;
            double deltaT = pms.getDeltaT();
            model.setSelectionWindowTime(model.getSelectionWindowTime() + deltaT);
        }
    }

    @Override
    public void windowDurationWasChanged() {
        double duration = model.getSelectionWindowDuration();
        if (selectWindowPickLine != null) {
            selectWindowPickLine.getWindow().setDuration(duration);
        }
        repaint();
    }

    @Override
    public void selectionWindowWasMoved() {
        double newTime = model.getSelectionWindowTime();
        if (selectWindowPickLine != null) {
            selectWindowPickLine.setXval(newTime);
        }
        repaint();
    }

}
