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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;
import llnl.gnem.dftt.core.gui.plotting.ZoomState;
import llnl.gnem.dftt.core.gui.plotting.plotobject.DataText;
import llnl.gnem.dftt.core.gui.plotting.transforms.AzimuthalEqualAreaTransform;
import llnl.gnem.dftt.core.polygon.Vertex;

public class JAzimuthalPlot extends JBasicPlot {
    private double originLat;
    private double originLon;
    private double degreeRadius;
    private GridLines gridlines;
    private GeographicFeatures geoFeatures;
    private JSelectionCrosshair crosshair;
    private static final int TEN_DEGREE_THRESHOLD = 10;
    private static final int NINETY_DEGREE_THRESHOLD = 90;
    private boolean renderGridLines = false;
    private static final int DEFAULT_ORIGIN_LAT = 30;
    private static final int DEFAULT_ORIGIN_LON = -119;
    private static final int DEFAULT_RADIUS = 180;

    public JAzimuthalPlot(JGeographicPlot owner) {
        super(owner);
        this.owner = owner;
        originLat = DEFAULT_ORIGIN_LAT;
        originLon = DEFAULT_ORIGIN_LON;
        degreeRadius = DEFAULT_RADIUS;
        PlotRegion = owner.getPlotRegion();
        gridlines = new GridLines();
        geoFeatures = new GeographicFeatures();
        crosshair = new JSelectionCrosshair();
        crosshair.setVisible(false);

    }

    public void setSelectionCrosshair(double lat, double lon) {
        crosshair.setPosition(this, lat, lon);
    }

    public void setSelectionCrosshairVisible(boolean visible) {
        crosshair.setVisible(visible);
    }

    public Vertex getSelectionCrosshairPosition() {
        return crosshair.getPosition();
    }

    public boolean isCrosshairVisible() {
        return crosshair.isVisible();
    }

    @Override
    public void ZoomIn(ZoomState state) {
        AzimuthalZoomState azs = (AzimuthalZoomState) state;
        AzimuthalZoomState current = new AzimuthalZoomState(originLat, originLon, degreeRadius);
        zoomStack.push(current);
        originLat = azs.getCenterLat();
        originLon = azs.getCenterLon();
        degreeRadius = azs.getDegreeRadius();
    }

    @Override
    public void ZoomOut() {
        if (!zoomStack.empty()) {
            AzimuthalZoomState azs = zoomStack.pop();
            originLat = azs.getCenterLat();
            originLon = azs.getCenterLon();
            degreeRadius = azs.getDegreeRadius();
        }
    }

    @Override
    public void UnzoomAll() {
        while (!zoomStack.empty()) {
            AzimuthalZoomState azs = zoomStack.pop();
            if (zoomStack.empty()) { // This is the last one so use it
                originLat = azs.getCenterLat();
                originLon = azs.getCenterLon();
                degreeRadius = azs.getDegreeRadius();
                return;
            }
        }
    }

    public void reset() {
        UnzoomAll();
        originLat = DEFAULT_ORIGIN_LAT;
        originLon = DEFAULT_ORIGIN_LON;
        degreeRadius = DEFAULT_RADIUS;

    }

    public void setOriginLat(double lat) {
        originLat = lat;
    }

    public void setOriginLon(double lon) {
        originLon = lon;
    }

    public void setPlotRadius(double radius) {
        degreeRadius = radius;
    }

    public double getPlotRadius() {
        return degreeRadius;
    }

    public double getOriginLon() {
        return originLon;
    }

    public double getOriginLat() {
        return originLat;
    }

    public Vertex getPlotOrigin() {
        return new Vertex(originLat, originLon);
    }

    public void setPlotOrigin(Vertex origin) {
        originLat = origin.getLat();
        originLon = origin.getLon();
    }

    synchronized void Render(Graphics g) {
        if (!getVisible()) {
            return;
        }

        Rectangle rect = PlotRegion.getRect();
        int LeftMargin = (int) rect.getX();
        int BoxWidth = (int) rect.getWidth();
        int top = (int) rect.getY();
        int height = (int) rect.getHeight();

        // Don't render interior if it cannot show up.
        if (height < 2 || BoxWidth < 2) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.clip(owner.getPlotBorder().getRect());

        // Initialize the transform for this render
        AzimuthalEqualAreaTransform ct = (AzimuthalEqualAreaTransform) coordTransform;
        ct.initialize(originLat, originLon, degreeRadius, LeftMargin, BoxWidth,
                top, height);

        g2d.clip(PlotRegion.getRect());

        // Create a blue background for the globe
        Ellipse2D.Double globe = new Ellipse2D.Double(ct.getGlobeLeft(), ct.getGlobeTop(),
                ct.getGlobeDiameter(), ct.getGlobeDiameter());
        g2d.setColor(new Color(0.5f, 0.5f, 1.0f));
        g2d.fill(globe);

        // render geographic features...
        if (geoFeatures != null) {
            geoFeatures.Render(g, this);
        }

        if (isRenderGridLines() && gridlines != null && degreeRadius > TEN_DEGREE_THRESHOLD) {
            gridlines.setRenderLimits(originLat, originLon, degreeRadius);
            gridlines.Render(g, this);
        }

        // render any contained objects
        renderVisiblePlotObjects(g);

        if (degreeRadius <= TEN_DEGREE_THRESHOLD) {
            renderTenDegreeDetails(g);
        } else if (degreeRadius <= NINETY_DEGREE_THRESHOLD) {
            double minLon = -150;
            double maxLon = DEFAULT_RADIUS;
            double lonIncrement = DEFAULT_ORIGIN_LAT;
            renderLonLabels(g, minLon, maxLon, lonIncrement);

            double minLat = -60;
            double maxLat = 60;
            double latIncrement = 30.0;
            renderLatLabels(g, minLat, maxLat, latIncrement);

        }

        if (degreeRadius < TEN_DEGREE_THRESHOLD / 2) {
            JScaleBar scalebar = new JScaleBar(degreeRadius, originLat - 0.7 * degreeRadius, originLon);
            scalebar.render(g, this);
        }

        crosshair.render(g, this);
    }

    private void renderTenDegreeDetails(Graphics g) {
        double minLon = (int) (originLon - TEN_DEGREE_THRESHOLD * 2) - 1;
        double maxLon = (int) (originLon + TEN_DEGREE_THRESHOLD * 2) + 1;
        double minLat = (int) (originLat - TEN_DEGREE_THRESHOLD * 2) - 1;
        double maxLat = (int) (originLat + TEN_DEGREE_THRESHOLD * 2) + 1;

        if (isRenderGridLines()) {
            GridLines gl = new GridLines(1, 100);
            gl.setRenderLimits(originLat, originLon, degreeRadius);
            gl.Render(g, this);
        }

        double lonIncrement = 1;
        renderLonLabels(g, minLon, maxLon, lonIncrement);

        double latIncrement = 1.0;
        renderLatLabels(g, minLat, maxLat, latIncrement);
    }

    private void renderLonLabels(Graphics g, double minLon, double maxLon, double lonIncrement) {
        double lon = minLon;
        double lat = originLat - .9 * degreeRadius;
        while (lon <= maxLon) {
            double displayLon = lon <= DEFAULT_RADIUS ? lon : lon - 360;
            DataText dt = new DataText(lat, lon, String.format("%4.0f", displayLon));
            lon += lonIncrement;
            dt.render(g, this);
        }
    }

    private void renderLatLabels(Graphics g, double minLat, double maxLat, double latIncrement) {
        double lat = minLat;
        double lon = originLon - .98 * degreeRadius;
        while (lat <= maxLat) {
            DataText dt = new DataText(lat, lon, String.format("%4.0f", lat));
            lat += latIncrement;
            dt.render(g, this);
        }
    }

    public void setRenderGridLines(boolean renderGridLines) {
        this.renderGridLines = renderGridLines;
    }

    public boolean isRenderGridLines() {
        return renderGridLines;
    }
}
