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
package llnl.gnem.dftt.core.gui.map.simple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SpringLayout;

import llnl.gnem.dftt.core.gui.map.EditableMap;
import llnl.gnem.dftt.core.gui.map.EditableMapView;
import llnl.gnem.dftt.core.gui.map.LayerNames;
import llnl.gnem.dftt.core.gui.map.MapModel;
import llnl.gnem.dftt.core.gui.map.PrintableMap;
import llnl.gnem.dftt.core.gui.map.ViewPort;
import llnl.gnem.dftt.core.gui.map.actions.ClosePopupAction;
import llnl.gnem.dftt.core.gui.map.internal.IconManager;
import llnl.gnem.dftt.core.gui.map.internal.LayerPreferences;
import llnl.gnem.dftt.core.gui.map.internal.Measurable;
import llnl.gnem.dftt.core.gui.map.internal.ViewportPositionMonitor;
import llnl.gnem.dftt.core.gui.map.location.LocationInfo;
import llnl.gnem.dftt.core.gui.map.location.LocationModel;
import llnl.gnem.dftt.core.gui.plotting.MouseMode;
import llnl.gnem.dftt.core.gui.plotting.MouseOverPlotObject;
import llnl.gnem.dftt.core.gui.plotting.PlotObjectClicked;
import llnl.gnem.dftt.core.gui.plotting.PlotObjectObservable;
import llnl.gnem.dftt.core.gui.plotting.jgeographicplot.JAzimuthalPlot;
import llnl.gnem.dftt.core.gui.plotting.jgeographicplot.JGeographicPlot;
import llnl.gnem.dftt.core.gui.plotting.plotobject.JPolygon;
import llnl.gnem.dftt.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.dftt.core.gui.plotting.plotobject.Symbol;
import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.dftt.core.gui.util.SpringUtilities;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.polygon.BasePolygon;
import llnl.gnem.dftt.core.polygon.Vertex;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
public abstract class SimpleMap extends JPanel implements PrintableMap, EditableMap {
    private static final long serialVersionUID = 1L;
    private ArrayList<EditableMapView> editableViews = new ArrayList<>();
    protected IconManager iconManager;
    protected final JGeographicPlot plot;
    private final JPopupMenu popupMenu;
    private final PlotObserver plotObserver;
    private MapModel mapModel;
    private final MouseMode defaultMouseMode;
    private JPanel layerPanel;
    private TreeMap<String, SimpleLayer> layers = new TreeMap<>();
    private static final float DEFAULT_POLY_STROKE_WIDTH = 3.0f;
    private ArrayList<PlotObject> selectedObjectList = new ArrayList<>();
    private final LayerPreferences prefs;
    
    public SimpleMap(IconManager iconManager) throws IOException, ClassNotFoundException {
        this(iconManager, true);
    }

    public SimpleMap(IconManager iconManager, boolean closeAction) throws IOException, ClassNotFoundException {
        super(new BorderLayout());

        this.iconManager = iconManager;

        popupMenu = new JPopupMenu();
        
        if (closeAction) {
            popupMenu.add(new ClosePopupAction(this, popupMenu));
        }

        layerPanel = new JPanel(new SpringLayout());
        layerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Layers"));

        plot = new JGeographicPlot();
        this.add(plot, BorderLayout.CENTER);
        plotObserver = new PlotObserver();
        plot.addPlotObjectObserver(plotObserver);
        defaultMouseMode = plot.getMouseMode();

        plot.addKeyListener(new MapKeyListener());

        // Setup tracking / updating for view port changes
        mapModel = new MapModel();

        // Load / Initialize our preserved preferences
        prefs = LayerPreferences.getInstance();
    }

    public void setIconManager(IconManager manager) {
        this.iconManager = manager;
    }

    protected void addLocationModel(LocationModel model, PlotManager manager) {
        model.addView(manager);
        addLocationManager(manager);
    }
    
    protected void addLocationManager(PlotManager manager) {
        mapModel.addView(manager);
        plotObserver.addListener(manager);
    }

    public Collection<SimpleLayer> getLayers() {
        return layers.values();
    }

    public SimpleLayer getLayerByName(String name) {
        return layers.get(name);
    }

    public JPanel getLayerPanel() {
        return layerPanel;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    protected void centerView(double lat, double lon, boolean zoom) {
        JAzimuthalPlot plot1 = (JAzimuthalPlot) plot.getPlot();
        plot1.setOriginLat(lat);
        plot1.setOriginLon(lon);
        if (zoom) {
            double rad = getPlotRadius();
            if (rad > 1.0) {
                setRadius(1.0);
            }
        }
        viewChanged();
    }

    void centerView(LocationInfo info, boolean zoom) {
        centerView(info.getLat(), info.getLon(), zoom);
    }

    public static class PlotItem<T extends Measurable> implements Measurable {
        private final T info;
        private Symbol symbol;

        public PlotItem(T info, Symbol symbol) {
            this.info = info;
            this.symbol = symbol;
            this.symbol.setTextVisible(false);
            this.symbol.setText(info.toString());
        }

        @Override
        public double distanceFrom(Measurable other) {
            Measurable otherInfo = ((PlotItem) other).info;
            return info.distanceFrom(otherInfo);
        }

        @Override
        public boolean isInside(ViewPort viewport) {
            return info.isInside(viewport);
        }

        @Override
        public boolean intersects(ViewPort viewport) {
            return info.intersects(viewport);
        }

        public T getInfo() {
            return info;
        }

        public Symbol getSymbol() {
            return symbol;
        }
        
        public void setSymbol(Symbol symbol) {
            this.symbol = symbol;
        }
    }

    protected void showTooltip(Symbol symbol) {
        if (symbol.getText() != null && !symbol.getText().trim().isEmpty() && !symbol.getTextVisible()) {
            plot.setToolTipText(symbol.getText());
        }
    }

    protected class PlotObserver implements Observer {
        private List<PlotObserverListener> listeners = new ArrayList<>();

        private void addListener(PlotObserverListener listener) {
            listeners.add(listener);
        }

        @Override
        public void update(Observable observable, Object obj) {
            Graphics g = plot.getGraphics();

            if (observable instanceof PlotObjectObservable) {
                if (obj instanceof MouseOverPlotObject) {
                    MouseOverPlotObject pos = (MouseOverPlotObject) obj;
                    PlotObject po1 = pos.getPlotObject();

                    if (po1 != null && (po1 instanceof Symbol)) {
                        Symbol symbol = (Symbol) po1;
                        showTooltip(symbol);
                    }
                }
                if (obj instanceof PlotObjectClicked) {
                    PlotObjectClicked poc = (PlotObjectClicked) obj;
                    MouseEvent me = poc.me;
                    PlotObject po = poc.po;
                    if (po != null) {
                        if (po instanceof Symbol) {
                            Symbol symbol = (Symbol) po;
                            for (PlotObserverListener listener : listeners) {
                                listener.handleSymbolSelection(symbol, me);
                            }
                        } else if (po instanceof JPolygon) {
                            JPolygon polygon = (JPolygon) po;
                            if (me.getButton() == MouseEvent.BUTTON1) {
                                if (!selectedObjectList.contains(polygon)) {
                                    polygon.setFillColor(getPolygonFillColor(true));
                                    selectedObjectList.add(polygon);
                                }
                            } else if (me.getButton() == MouseEvent.BUTTON3) {
                                polygon.setFillColor(getPolygonFillColor(false));
                                selectedObjectList.remove(polygon);
                            }
                        }
                    }
                }
            }

            if (obj instanceof ArrayList) {
                @SuppressWarnings("unchecked")
                ArrayList<Coordinate> points = (ArrayList<Coordinate>) obj;
                addPolygon(points);
            }

            if (obj instanceof MouseWheelEvent) { // was else if
                MouseWheelEvent event = (MouseWheelEvent) obj;
                int notches = event.getWheelRotation();

                if (notches < 0) {
                    double radius = getPlotRadius() / 1.1;
                    if (radius > 0) {
                        setRadius(radius);
                        plot.repaint();
                    }
                } else {
                    double radius = getPlotRadius() * 1.1;
                    if (radius < 180) {
                        setRadius(radius);
                        plot.repaint();
                    }
                }
                viewChanged();  // we are zooming in/out
            }
        }
    }

    private void viewChanged() {
        final ViewPort viewPort = getViewPort();
        if (ViewportPositionMonitor.getInstance().notifyViewportChange(viewPort)) {
            mapModel.viewChanged();
        }
    }

    /**
     * Gets the viewport radius in degrees.
     *
     * @return The viewport radius in degrees.
     */
    public double getPlotRadius() {
        JAzimuthalPlot p = (JAzimuthalPlot) plot.getPlot();
        return p.getPlotRadius();
    }

    /**
     * Sets the radius of the map viewport in degrees.
     *
     * @param radius The new viewport radius in degrees.
     */
    public void setRadius(double radius) {
        JAzimuthalPlot p = (JAzimuthalPlot) plot.getPlot();
        p.setPlotRadius(radius);
    }
    final double maxElevation = ViewportPositionMonitor.getInstance().getApproxElevation(ViewportPositionMonitor.MAX_LEVELS - 1) * 500;
    final double elevationPerDegree = maxElevation / 180.0;  // convert to km
    final double degreePerElevation = 360.0 / maxElevation;

    public ViewPort getViewPort() {
        JAzimuthalPlot plot1 = (JAzimuthalPlot) plot.getPlot();
        final double lat = plot1.getOriginLat();
        final double lon = plot1.getOriginLon();
        final double elevation = plot1.getPlotRadius() * elevationPerDegree;  // base the elevation guess on view radius
        final int level = (ViewportPositionMonitor.getInstance().getElevationLevel(elevation / 1000.0)) + 1;
        final double radius = degreePerElevation * ViewportPositionMonitor.getInstance().getApproxElevation(level) * 1000;
        ApplicationLogger.getInstance().log(Level.FINE, "Approx elevation(" + elevation + ") radius(" + radius + ") level(" + level + ") actualRadius(" + plot1.getPlotRadius() + ")");
        return new ViewPort(lat, lon, radius, elevation);
    }

    @Override
    public BufferedImage getPlotImage() {
        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.paintAll(img.getGraphics());
        return img;
    }

    @Override
    public void setPolygonEditable(boolean editable) {
        if (editable) {
            plot.setMouseMode(MouseMode.CREATE_POLYGON);
        } else {
            plot.setMouseMode(defaultMouseMode);
        }
    }

    @Override
    public boolean getPolygonEditable() {
        return plot.getMouseMode() == MouseMode.CREATE_POLYGON;
    }

    @Override
    public void clearPolygons() {
        polygonLayer.clear();
        plot.repaint();
    }

    @Override
    public void addPolygon(BasePolygon polygon) {
        ArrayList<Coordinate> coords = new ArrayList<>();
        for (Vertex vert : polygon.getVertices()) {
            Coordinate coord = new Coordinate(vert.getLat(), vert.getLon());
            coords.add(coord);
        }
        addPolygon(coords);
    }

    @Override
    public List<BasePolygon> getPolygons() {
        ArrayList<BasePolygon> polys = new ArrayList<>();
        for (PlotObject po : polygonLayer.items) {
            if (po instanceof JPolygon) {
                JPolygon surfacePoly = (JPolygon) po;
                BasePolygon poly = new BasePolygon(surfacePoly.getVertices());
                polys.add(poly);
            }
        }
        return polys;
    }

    @Override
    public void addEditableMapView(EditableMapView view) {
        editableViews.add(view);
    }

    private Color getPolygonEdgeColor() {
        return Color.BLACK;
    }

    private Color getPolygonFillColor(boolean selected) {
        float[] compArray = new float[4];

        if (selected) {
            Color.WHITE.getColorComponents(compArray);
        } else {
            Color.YELLOW.getColorComponents(compArray);
        }
        return new Color(compArray[0], compArray[1], compArray[2], 0.5f);
    }
    private SimpleLayer polygonLayer;

    private void addPolygon(ArrayList<Coordinate> points) {
        Point2D[] vertices = new Point2D[points.size() + 1];

        int j = 0;
        for (Coordinate coord : points) {
            vertices[j++] = new Point2D.Double(coord.getWorldC1(), coord.getWorldC2());
        }
        Coordinate coord = points.get(0);
        vertices[j] = new Point2D.Double(coord.getWorldC1(), coord.getWorldC2());

        JPolygon polygon = new JPolygon(vertices);
        polygon.setSelectable(true);
        polygon.setFillColor(getPolygonFillColor(false));
        polygon.setEdgeColor(getPolygonEdgeColor());
        polygon.setInteriorFilled(true);
        polygon.setStrokeWidth(DEFAULT_POLY_STROKE_WIDTH);

        if (polygonLayer == null) {
            polygonLayer = new SimpleLayer(LayerNames.POLYGON_LAYER_NAME, 0, true);
        }
        polygonLayer.add(polygon);

        plot.repaint();
    }

    public SimpleLayer createLayer(String name) {
        return new SimpleLayer(name, true);
    }

    @ThreadSafe
    public final class SimpleLayer {
        private final ArrayList<PlotObject> items = new ArrayList<>();
        private boolean enabled = true;
        private final JCheckBox box;
        public final String name;
        public final Integer level;

        public SimpleLayer(String name, boolean showBox) {
            this.name = name;
            level = null;
            box = createCheckBox();
            layers.put(name, this);
            if (showBox) {
                showBox();
            }
        }

        public SimpleLayer(String name, int level, boolean showBox) {
            this.name = name;
            this.level = level;
            box = createCheckBox();
            layers.put(name, this);
            if (showBox) {
                showBox();
            }
        }

        public void showBox() {
            if (!layerPanel.isAncestorOf(box)) {
                layerPanel.add(box);
                final int rows = layerPanel.getComponentCount();
                SpringUtilities.makeCompactGrid(layerPanel,
                        rows, 1, //rows, cols
                        5, 5, //initX, initY
                        5, 5);   //xPad, yPad
                layerPanel.revalidate();
            }
        }

        public void hideBox() {
            layerPanel.remove(box);
            final int rows = layerPanel.getComponentCount();
            SpringUtilities.makeCompactGrid(layerPanel,
                    rows, 1, //rows, cols
                    5, 5, //initX, initY
                    5, 5);   //xPad, yPad
            layerPanel.revalidate();
        }

        private JCheckBox createCheckBox() {
            JCheckBox checkBox = new JCheckBox(name);
            if (prefs.containsLayer(name)) {
                enabled = prefs.getLayerEnabled(name);
            }
            checkBox.setSelected(enabled);
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (box.isSelected()) {
                        prefs.setLayerEnabled(name, true);
                        enable();
                    } else {
                        prefs.setLayerEnabled(name, false);
                        disable();
                    }
                }
            });
            return checkBox;
        }

        public void setBoxValue(boolean value) {
            if (box.isSelected() != value) {
                box.doClick();
            }
        }

        public void clear() {
            synchronized (items) {
                if (enabled) {
                    for (PlotObject obj : items) {
                        plot.getPlot().DeletePlotObject(obj);
                    }
                }
                items.clear();
            }
        }

        public boolean contains(PlotObject object) {
            synchronized (items) {
                return items.contains(object);
            }
        }

        public void add(PlotObject object) {
            synchronized (items) {
                if (enabled) {
                    if (level == null) {
                        plot.getPlot().AddPlotObject(object);
                    } else {
                        plot.getPlot().AddPlotObject(object, level);
                    }
                }
                items.add(object);
            }
        }

        public void remove(PlotObject object) {
            synchronized (items) {
                items.remove(object);
                if (enabled) {
                    plot.getPlot().DeletePlotObject(object);
                }
            }
        }

        public void enable() {
            synchronized (items) {
                if (enabled) {
                    return;
                }
                if (level == null) {
                    for (PlotObject object : items) {
                        plot.getPlot().AddPlotObject(object);
                    }
                } else {
                    for (PlotObject object : items) {
                        plot.getPlot().AddPlotObject(object, level);
                    }
                }
                plot.repaint();
                enabled = true;
            }
        }

        public void disable() {
            synchronized (items) {
                if (!enabled) {
                    return;
                }
                for (PlotObject object : items) {
                    plot.getPlot().DeletePlotObject(object);
                }
                plot.repaint();
                enabled = false;
            }
        }
    }

    private class MapKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent keyEvent) {
            int keyCode = keyEvent.getKeyCode();

            switch (keyCode) {
                case KeyEvent.VK_DELETE:
                    for (PlotObject selectedObject : selectedObjectList) {
                        if (selectedObject instanceof JPolygon) {
                            polygonLayer.remove(selectedObject);
                        }
                    }
                    plot.repaint();
                    selectedObjectList.clear();
                    break;
            }
        }
    }

    interface PlotObserverListener {
        public void handleSymbolSelection(Symbol symbol, MouseEvent me);
    }
}
