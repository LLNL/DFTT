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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.ogc.kml.KMLAbstractContainer;
import gov.nasa.worldwind.ogc.kml.KMLAbstractFeature;
import gov.nasa.worldwind.ogc.kml.KMLAbstractGeometry;
import gov.nasa.worldwind.ogc.kml.KMLConstants;
import gov.nasa.worldwind.ogc.kml.KMLPlacemark;
import gov.nasa.worldwind.ogc.kml.KMLPolygon;
import gov.nasa.worldwind.ogc.kml.KMLRoot;
import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;
import llnl.gnem.dftt.core.polygon.Vertex;

/**
 * This class is a collection of coastline polygons used in rendering a world
 * map. The class expects to have access to two serialized Vectors of JPolyShape
 * objects with resource names "coastlines" and "lakes". The default constructor
 * of this class will attempt to load those two resources. After construction,
 * the class's render method can be called on a JBasicPlot object to render the
 * coastlines and land masses.
 */
public class GeographicFeatures {
    private final List<JPolyShape> lakes;
    private final List<JPolyShape> countries;

    /**
     * Load two serialized Vectors of JPolyShape objects named "coastlines" and
     * "lakes".
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public GeographicFeatures() {
        Color landColor = new Color(.8F, 1.0F, .8F);
        Color lakeColor = new Color(.8f, .8f, 1.0f);
        countries = getShapes("polygons/countries.kml", landColor);
        lakes = getShapes("polygons/lakes.kml", lakeColor);
    }

    private List<JPolyShape> getShapes(String resourceName, Color shapeColor) {
        List<JPolyShape> polygons = readKML(resourceName);
        for (JPolyShape shape : polygons) {
            shape.setFillColor(shapeColor);
        }
        return polygons;
    }

    private List<JPolyShape> readKML(String resourceName) {
        List<JPolyShape> shapes = new ArrayList<>();
        try {
            ClassLoader cl = getClass().getClassLoader();
            URL url = cl.getResource(resourceName);
            InputStream in = url.openStream();
            KMLRoot root = new KMLRoot(in, KMLConstants.KML_MIME_TYPE);

            root.parse();
            addPolygons(root.getFeature(), shapes);
        } catch (XMLStreamException | IOException ex) {
            Logger.getLogger(GeographicFeatures.class.getName()).log(Level.SEVERE, null, ex);
        }

        return shapes;
    }

    public void addPolygons(KMLAbstractFeature feature, List<JPolyShape> shapes) {
        if (feature instanceof KMLAbstractContainer) {
            KMLAbstractContainer container = (KMLAbstractContainer) feature;
            for (KMLAbstractFeature f : container.getFeatures()) {
                addPolygons(f, shapes);
            }
        } else if (feature instanceof KMLPlacemark) {
            KMLAbstractGeometry geom = ((KMLPlacemark) feature).getGeometry();
            if (geom instanceof KMLPolygon) {
                KMLPolygon polygon = (KMLPolygon) geom;
                List<? extends Position> coords = polygon.getOuterBoundary().getCoordinates().list;

                Vertex[] vx = new Vertex[coords.size()];
                for (int i = 0; i < coords.size(); i++) {
                    Position coord = coords.get(i);
                    vx[i] = new Vertex(coord.getLatitude().getDegrees(), coord.getLongitude().getDegrees());
                }

                JPolyShape shape = new JPolyShape(vx);
                shapes.add(shape);
            } else {
                // Handle point, line, etc placemarks
            }
        }
    }

    /**
     * Set the supplied JBasicPlot object as the owner of all the JPolyShape
     * objects stored in this class. This should be done before rendering them
     * to the plot.
     *
     * @param owner The JBasicPlot that will become the owner of the JPolyShape
     *              objects.
     */
    public void setOwner(JBasicPlot owner) {

        for (JPolyShape s : lakes) {
            s.setOwner(owner);
        }

        for (JPolyShape s : countries) {
            s.setOwner(owner);
        }

    }

    /**
     * render all the contained JPOlyShape objects to the supplied graphics
     * context used by the supplied JBasicPlot object.
     *
     * @param g    The graphics context on which to render the objects
     * @param plot The plot using the graphics context.
     */
    public void Render(Graphics g, JBasicPlot plot) {

        for (JPolyShape s : countries) {
            s.render(g, plot);
        }

        for (JPolyShape l : lakes) {
            l.render(g, plot);
        }

    }
}
