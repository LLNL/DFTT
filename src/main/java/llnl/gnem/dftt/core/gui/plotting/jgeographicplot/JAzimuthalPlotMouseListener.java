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
package llnl.gnem.dftt.core.gui.plotting.jgeographicplot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Observer;

import javax.swing.event.MouseInputAdapter;

import llnl.gnem.dftt.core.gui.plotting.DrawingRegion;
import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;
import llnl.gnem.dftt.core.gui.plotting.MouseMode;
import llnl.gnem.dftt.core.gui.plotting.PlotObjectObservable;
import llnl.gnem.dftt.core.gui.plotting.keymapper.ControlKeyMapper;
import llnl.gnem.dftt.core.gui.plotting.keymapper.DefaultControlKeyMapper;
import llnl.gnem.dftt.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.dftt.core.gui.plotting.transforms.CoordinateTransform;
import llnl.gnem.dftt.core.util.Geometry.EModel;
import llnl.gnem.dftt.core.polygon.BasePolygon;
import llnl.gnem.dftt.core.polygon.Vertex;


@SuppressWarnings({"AssignmentToNull"})
public class JAzimuthalPlotMouseListener extends MouseInputAdapter implements KeyListener {

    private final JGeographicPlot plot;
    private final CoordinateTransform ct;
    private Coordinate coord;
    private PlotObject currentObject;
    private boolean zooming;
    private double zoomRadius;
    private Coordinate zoomCenter;
    private final static int NUM_ZOOM_POINTS = 100;
    private Vertex zoomCenterVertex;
    private BasePolygon previousZoomPoly;
    private final PlotObjectObservable objectObservable;
    private MouseEvent currentEvent;
    private boolean modeChangeOccurred;
    private boolean keyIsDown;
    private final ControlKeyMapper controlKeyMapper;
    private boolean handPanning;
    private Vertex originalPlotOrigin;
    private int lastPanX;
    private int lastPanY;
    private Graphics myGraphics;
    private final ArrayList<Coordinate> polyPoints;
    private static final float POLY_STROKE_WIDTH = 3.0F;


    JAzimuthalPlotMouseListener(JGeographicPlot plot) {
        this.plot = plot;
        ct = plot.getCoordinateTransform();
        zooming = false;
        objectObservable = new PlotObjectObservable();
        controlKeyMapper = new DefaultControlKeyMapper();
        myGraphics = plot.getGraphics();
        polyPoints = new ArrayList<Coordinate>();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        objectObservable.MouseWheelAction(e);
    }


    void addPlotObjectObserver(Observer o) {
        objectObservable.addObserver(o);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        currentEvent = me;
        plot.requestFocus();
        int X = me.getX();
        int Y = me.getY();
        JBasicPlot sp = plot.getPlot();
        if (sp == null)
            return;
        PlotObject po = sp.getHotObject(X, Y);
        if (me.getButton() == MouseEvent.BUTTON3 && po != null) {
            objectObservable.MouseButtonAction(me, po, null);
        }

    }

    @Override
    public void mousePressed(MouseEvent me) {
        currentEvent = me;
        plot.requestFocus();

        // Check to make sure the pointer is inside the plotting region
        int X = me.getX();
        int Y = me.getY();
        if (!plot.getPlotRegion().getRect().contains(X, Y))
            return;

        // Check to see if any plot objects were selected
        JBasicPlot sp = plot.getPlot();
        if (sp == null)
            return;

        PlotObject po = sp.getHotObject(X, Y);
        if (me.getClickCount() == 1 && po == null)
            objectObservable.sendPlotClickedMessage(me, new Coordinate(X, Y), null);

        if (me.getButton() == MouseEvent.BUTTON3 &&
                po == null &&
                plot.getMouseMode() != MouseMode.SELECT_REGION) {
            sp.ZoomOut();
            objectObservable.PlotZoomStateChanged();
            plot.repaint();
            return;
        }

        if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 2 && po != null) {
            objectObservable.DoubleClickObject(po);
            return;
        }
        if (me.getButton() == MouseEvent.BUTTON1 &&
                me.getClickCount() == 1 &&
                po != null && plot.getMouseMode() != MouseMode.SELECT_REGION &&
                plot.getMouseMode() != MouseMode.PAN) {
            objectObservable.MouseButtonAction(me, po, null);
            return;
        }

        if (me.getButton() == MouseEvent.BUTTON1 &&
                po == null &&
                (plot.getMouseMode() == MouseMode.SELECT_ZOOM ||
                        plot.getMouseMode() == MouseMode.ZOOM_ONLY ||
                        plot.getMouseMode() == MouseMode.SELECT_REGION)) {
            myGraphics = plot.getGraphics();
            myGraphics.setXORMode(Color.white);
            zooming = true;
            previousZoomPoly = null;
            zoomCenter = new Coordinate(X, Y);
            ct.PlotToWorld(zoomCenter);
            zoomCenterVertex = new Vertex(zoomCenter.getWorldC1(), zoomCenter.getWorldC2());
            zoomRadius = 0;
            objectObservable.MouseButtonAction(me, po, null);
            return;
        }

        if (me.getButton() == MouseEvent.BUTTON1 &&
                po == null &&
                (plot.getMouseMode() == MouseMode.CREATE_POLYGON)) {
            myGraphics = plot.getGraphics();
            myGraphics.setXORMode(Color.white);
            polyPoints.clear();
            Coordinate newCoord = new Coordinate(X, Y);
            ct.PlotToWorld(newCoord);
            polyPoints.add(newCoord);
            return;
        }


        if (plot.getMouseMode() == MouseMode.PAN) {
            lastPanX = X;
            lastPanY = Y;
            JAzimuthalPlot jplot = (JAzimuthalPlot) plot.getPlot();
            originalPlotOrigin = jplot.getPlotOrigin();
            handPanning = true;
        }


    }

    @Override
    public void mouseReleased(MouseEvent me) {
        handPanning = false;
        currentEvent = me;
        if (zooming) {
            zooming = false;

            JBasicPlot sp = plot.getPlot();
            if (sp == null)
                return;

            //noinspection MagicNumber
            if (zoomRadius > 0.0001 && zoomRadius <= 180) {
                sp.ZoomIn(new AzimuthalZoomState(zoomCenter, zoomRadius));
                plot.repaint();
                objectObservable.PlotZoomStateChanged();
            } else
                plot.repaint();


        } else if (plot.getMouseMode() == MouseMode.CREATE_POLYGON) {
            Graphics2D g2d = (Graphics2D) myGraphics;
            DrawingRegion dr = plot.getPlotRegion();
            g2d.clip(dr.getRect());
            g2d.setStroke(new BasicStroke(POLY_STROKE_WIDTH));
            drawCurrentPolygon(g2d);
            objectObservable.finishedDrawingPolygon(polyPoints);
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        currentEvent = me;
        int x = me.getX();
        int y = me.getY();
        if (zooming) {
            coord = new Coordinate(x, y);
            ct.PlotToWorld(coord);
            zoomRadius = ct.getWorldDistance(zoomCenter, coord);
            BasePolygon poly = BasePolygon.makeCirclePolygon_Deg(zoomCenterVertex, zoomRadius, NUM_ZOOM_POINTS);
            Graphics2D g2d = (Graphics2D) myGraphics;
            DrawingRegion dr = plot.getPlotRegion();
            g2d.clip(dr.getRect());
            DrawCircle(previousZoomPoly, g2d);
            DrawCircle(poly, g2d);
            previousZoomPoly = poly;
        } else if (handPanning) {
            doPanOperation(x, y);
        } else if (plot.getMouseMode() == MouseMode.CREATE_POLYGON) {
            Graphics2D g2d = (Graphics2D) myGraphics;
            DrawingRegion dr = plot.getPlotRegion();
            g2d.clip(dr.getRect());
            g2d.setStroke(new BasicStroke(POLY_STROKE_WIDTH));
            drawCurrentPolygon(g2d);
            coord = new Coordinate(x, y);
            ct.PlotToWorld(coord);
            polyPoints.add(coord);
            drawCurrentPolygon(g2d);

        }

    }

    private void drawCurrentPolygon(Graphics2D g2d) {
        int npts = polyPoints.size() + 1;

        int[] xPoints = new int[npts];
        int[] yPoints = new int[npts];
        for (int j = 0; j < npts - 1; ++j) {
            Coordinate acoord = polyPoints.get(j);
            xPoints[j] = (int) acoord.getX();
            yPoints[j] = (int) acoord.getY();
        }
        Coordinate acoord = polyPoints.get(0);
        xPoints[npts - 1] = (int) acoord.getX();
        yPoints[npts - 1] = (int) acoord.getY();
        g2d.drawPolyline(xPoints, yPoints, npts);
    }

    private void doPanOperation(int x, int y) {
        if (originalPlotOrigin != null) {
            CoordinateTransform coordinateTransform = plot.getCoordinateTransform();
            Coordinate currentCoord = new Coordinate(x, y);
            coordinateTransform.PlotToWorld(currentCoord);
            Coordinate lastCoord = new Coordinate(lastPanX, lastPanY);
            coordinateTransform.PlotToWorld(lastCoord);
            double shiftAzimuth = EModel.getGreatCircleAzimuth(currentCoord.getWorldC1(),
                    currentCoord.getWorldC2(),
                    lastCoord.getWorldC1(),
                    lastCoord.getWorldC2());
            double shiftDelta = EModel.getGreatCircleDelta(currentCoord.getWorldC1(),
                    currentCoord.getWorldC2(),
                    lastCoord.getWorldC1(),
                    lastCoord.getWorldC2());
            Vertex newOrigin = EModel.reckon(originalPlotOrigin.getLat(), originalPlotOrigin.getLon(), shiftDelta, shiftAzimuth);

            JAzimuthalPlot jplot = (JAzimuthalPlot) plot.getPlot();
            jplot.setPlotOrigin(newOrigin);
            plot.repaint();
            lastPanX = x;
            lastPanY = y;
            originalPlotOrigin = newOrigin;
        }
    }


    @Override
    public void mouseMoved(MouseEvent me) {
        currentEvent = me;
        if ((plot == null) || (plot.getPlotRegion() == null) || (plot.getPlotRegion().getRect() == null))
            return;
        int x = me.getX();
        int y = me.getY();
        if (!plot.getPlotRegion().getRect().contains(x, y)) {
            objectObservable.MouseMove(null);
            return;
        }

        Coordinate c = new Coordinate(x, y);
        ct.PlotToWorld(c);
        objectObservable.MouseMove(c);

        PlotObject po = plot.getPlot().getHotObject(x, y);
        if (po == null) {
            plot.setToolTipText(null);
            if (currentObject != null) {
                currentObject.setSelected(false, plot.getGraphics());
            }
            currentObject = null;
        } else if (po != currentObject) {
            if (currentObject != null) {
                currentObject.setSelected(false, plot.getGraphics());
            }
            currentObject = po;
            po.setSelected(true, plot.getGraphics());
            objectObservable.MouseOverAction(po);
        }
    }


    void DrawCircle(BasePolygon poly, Graphics2D g2d) {
        if (poly == null)
            return;
        Vertex[] vertices = poly.getVertices();
        int Npts = vertices.length;

        int[] xPoints = new int[Npts];
        int[] yPoints = new int[Npts];
        for (int j = 0; j < Npts; ++j) {
            coord.setWorldC1(vertices[j].getLat());
            coord.setWorldC2(vertices[j].getLon());
            ct.WorldToPlot(coord);
            xPoints[j] = (int) coord.getX();
            yPoints[j] = (int) coord.getY();
        }
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawPolyline(xPoints, yPoints, Npts);
    }


    @Override
	public void keyPressed(KeyEvent e) {

        // Keymatic action causes this event to be fired repeatedly as long as key is down.
        // Only process for first event.
        if (!keyIsDown) {
            keyIsDown = true;
            int keyCode = e.getKeyCode();
            MouseMode mouseMode = controlKeyMapper.getMouseMode(e);
            if (mouseMode != null) {
                modeChangeOccurred = true;
                plot.setMouseMode(mouseMode);
            }
        }

    }

    @Override
	public void keyReleased(KeyEvent e) {
        if (modeChangeOccurred) {
            plot.restorePreviousMouseMode();
            modeChangeOccurred = false;
        }
        keyIsDown = false;
    }

    @Override
	public void keyTyped(KeyEvent e) {
    }


    public MouseEvent getCurrentEvent() {
        return currentEvent;
    }
}