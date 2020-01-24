package llnl.gnem.core.gui.plotting.jgeographicplot;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.ogc.kml.*;
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
import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.polygon.Vertex;

/**
 * This class is a collection of coastline polygons used in rendering a world
 * map. The class expects to have access to two serialized Vectors of JPolyShape
 * objects with resource names "coastlines" and "lakes". The default constructor
 * of this class will attempt to load those two resources. After construction,
 * the class's render method can be called on a JBasicPlot object to render the
 * coastlines and land masses.
 */
public class GeographicFeatures {
    private List<JPolyShape> lakes;
    private List<JPolyShape> countries;

    /**
     * Load two serialized Vectors of JPolyShape objects named "coastlines" and
     * "lakes".
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public GeographicFeatures() throws IOException, ClassNotFoundException {
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
        List<JPolyShape> shapes = new ArrayList<JPolyShape>();
        try {
            ClassLoader cl = getClass().getClassLoader();
            URL url = cl.getResource(resourceName);
            InputStream in = url.openStream();
            KMLRoot root = new KMLRoot(in, KMLConstants.KML_MIME_TYPE);
            
            root.parse();
            addPolygons(root.getFeature(), shapes);
        } catch (XMLStreamException ex) {
            Logger.getLogger(GeographicFeatures.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
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
     * objects.
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
     * @param g The graphics context on which to render the objects
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
